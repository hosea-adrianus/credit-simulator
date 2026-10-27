# Credit Simulator

A console application that calculates monthly installments for a vehicle loan (car or motorcycle).

- **Language:** Java 21 (builds and runs on Java 21+)
- **Build tool:** Maven (via the bundled Maven Wrapper, `./mvnw`)
- **Runtime dependency:** Jackson (JSON mapping for the web service). HTTP uses the JDK's built-in `java.net.http.HttpClient`.
- **Test dependency:** JUnit 5

## Contents

1. [Quick start](#quick-start)
2. [Using the application](#using-the-application)
3. [Business rules](#business-rules)
4. [Running the tests](#running-the-tests)
5. [Docker](#docker)
6. [CI/CD](#cicd)
7. [Design](#design)
8. [Assumptions](#assumptions)

## Quick start

Requirements: **Java 21 or newer** on Linux or macOS. Maven is optional, because `./mvnw` downloads it on first use.

```bash
# 1. Get the code
git clone <repository-url> credit_simulator
cd credit_simulator

# 2. Run interactively. The first run builds target/credit-simulator.jar automatically.
./credit_simulator

# 3. Or run with a file input
./credit_simulator file_inputs.txt
```

`bin/credit_simulator` works exactly the same as `./credit_simulator`:

```bash
bin/credit_simulator
bin/credit_simulator file_inputs.txt
```

The launcher rebuilds the jar whenever the sources are newer than the jar. To build it manually:

```bash
./mvnw package          # compile, run unit tests, create target/credit-simulator.jar
java -jar target/credit-simulator.jar [file_inputs.txt]
```

## Using the application

After it starts, the application waits for a command at the `>` prompt. Type `show` to list all of them:

| Command | Description |
|---|---|
| `calculate` | Asks for the loan data step by step, then shows the monthly installment for each year |
| `load` | Fetches an existing calculation from the JSON web service and calculates it automatically |
| `save_sheet <name>` | Saves the simulation currently on screen as a sheet called `<name>` |
| `switch_sheet <name>` | Switches to sheet `<name>` and shows its result again |
| `list_sheets` | Lists saved sheets. `*` marks the active sheet |
| `show` | Lists all available commands |
| `exit` | Quits the application |

Commands are case-insensitive. Unknown commands print a hint to use `show`.

### Calculate: step by step

```text
> calculate
Jenis Kendaraan (Motor|Mobil) : Mobil
Kondisi Kendaraan (Baru|Bekas) : Baru
Tahun Kendaraan (4 digit) : 2025
Jumlah Pinjaman Total (maks 1000000000) : 100000000
Tenor Pinjaman (1-6 thn) : 3
Jumlah DP : 20000000
[ERROR] DP kendaraan baru minimal 35% dari jumlah pinjaman (Rp. 35,000,000.00)
Jumlah DP : 35000000
---------------- Hasil Simulasi ----------------
Kendaraan       : Mobil Baru (2025)
Jumlah Pinjaman : Rp. 100,000,000.00
Jumlah DP       : Rp. 35,000,000.00
Pokok Pinjaman  : Rp. 65,000,000.00
Tenor           : 3 tahun

tahun 1 : Rp. 1,950,000.00/bln , Suku Bunga : 8%
tahun 2 : Rp. 2,107,950.00/bln , Suku Bunga : 8,1%
tahun 3 : Rp. 2,289,233.70/bln , Suku Bunga : 8,6%
------------------------------------------------
```

If a value is invalid, the application shows the reason and asks for **the same field** again.

### File input

A file input contains exactly what you would type in interactive mode, **one line per answer**. Lines are echoed so the output reads like an interactive session. The program stops at `exit` or at the end of the file. See [`file_inputs.txt`](file_inputs.txt):

```text
show
calculate
Mobil
Baru
2025
100000000
3
20000000      <- rejected (DP below 35%), so the next line is used for the DP
35000000
save_sheet avanza
...
exit
```

(The `<- ...` note above is only an explanation and is not part of the file.)

A missing file prints `Error: File input tidak ditemukan: ...` and exits with code 1.

### Load: JSON web service

`load` sends an HTTP `GET` to a mock web service:

```
https://hosea.free.beeceptor.com/2cf4efab-33d9-4e4e-acd6-b8ca30566bb1
```

To use another endpoint, set the environment variable **`CREDIT_SIMULATOR_LOAD_URL`**. The endpoint must return this JSON body (numbers may also be sent as strings, and unknown fields are ignored):

```json
{
  "vehicleType": "Mobil",
  "vehicleCondition": "Baru",
  "vehicleYear": 2025,
  "totalLoanAmount": 1000000000,
  "loanTenure": 6,
  "downPayment": 500000000
}
```

The data is checked with the same rules as manual input, then calculated and shown immediately. HTTP errors, timeouts (10 s), invalid JSON and invalid data are reported as messages, and the application keeps running.

Try it locally with the sample payload in [`mock/loan.json`](mock/loan.json):

```bash
# terminal 1: serve the mock
python3 -m http.server 8080 --directory mock

# terminal 2
CREDIT_SIMULATOR_LOAD_URL=http://localhost:8080/loan.json ./credit_simulator
> load
```

### Sheets

A **sheet** is a named slot that holds one simulation, like a tab in a spreadsheet. It lets you keep several simulations and move between them without typing the data again:

```text
> calculate              (car simulation)
> save_sheet avanza
> calculate              (motorcycle simulation)
> save_sheet nmax
> list_sheets
Daftar sheet:
  avanza
* nmax
> switch_sheet avanza    (the car result is shown again)
```

Saving to an existing name replaces that sheet. Sheet names may contain letters, digits, `_` and `-`. Sheets live in memory for the duration of the run.

## Business rules

| Rule | Implementation |
|---|---|
| Vehicle type `Motor`/`Mobil`, condition `Baru`/`Bekas`, case-insensitive | `VehicleType`, `VehicleCondition` |
| Year: 4 digits. New vehicle: year ≥ current year − 1. No year in the future | `LoanValidator#validateYear` |
| Total loan: numeric, > 0 and ≤ 1,000,000,000 | `LoanValidator#validateLoanAmount` |
| Tenure: 1–6 years | `LoanValidator#validateTenure` |
| Down payment ≥ 35% of the loan (new) / ≥ 25% (used), and less than the loan | `LoanValidator#validateDownPayment` |
| Base rate: Mobil 8%, Motor 9% | `Car`, `Motorcycle` |
| Rate +0.1% each year, +0.5% every 2 years | `InterestRateCalculator` |

Amounts are entered as plain digits, without thousand separators (`100000000`, not `100.000.000`).

### How the installment is calculated

```
principal          = total loan - down payment
for each year y = 1..tenure:
    rate(y)        = base rate (y = 1); + 0.1% when y is even; + 0.5% when y is odd
    balance        = remaining * (1 + rate(y))
    monthly(y)     = balance / months left in the tenure      (rounded half-up to 2 decimals)
    remaining      = balance - monthly(y) * 12
```

Example (car, loan 100,000,000, DP 35,000,000, 3 years). Principal = 65,000,000:

| Year | Rate | Calculation | Monthly |
|---|---|---|---|
| 1 | 8% | 65,000,000 × 1.08 / 36 | 1,950,000.00 |
| 2 | 8.1% | 46,800,000 × 1.081 / 24 | 2,107,950.00 |
| 3 | 8.6% | 25,295,400 × 1.086 / 12 | 2,289,233.70 |

All money calculations use `BigDecimal`.

## Running the tests

```bash
./mvnw test                  # unit + integration tests (JUnit 5)
./mvnw verify                # tests + build the jar
```

Run a single test class or method:

```bash
./mvnw test -Dtest=InstallmentCalculatorTest
./mvnw test -Dtest=LoanValidatorTest#newVehicleRejectsOlderThanPreviousYear
```

Test reports are written to `target/surefire-reports/`.

| Test | Covers |
|---|---|
| `VehicleFactoryTest`, `VehicleLabelTest` | Factory output, case-insensitive parsing |
| `InterestRateCalculatorTest` | Yearly rate progression for car and motorcycle |
| `InstallmentCalculatorTest` | Installment amounts, rounding, 1–6 year tenures |
| `LoanValidatorTest` | Every input rule and its boundaries (with a fixed `Clock`) |
| `LoanSimulationServiceTest` | Assembling and simulating a complete input |
| `HttpLoanDataClientTest` | Real HTTP calls to a local JDK `HttpServer`: success, 404, invalid JSON, timeout, unreachable |
| `AppConfigTest` | Web service URL configuration |
| `InMemorySheetRepositoryTest` | Sheet storage |
| `CurrencyFormatterTest` | `Rp. 1,950,000.00` and `8,1%` formatting |
| `CreditSimulatorApplicationTest` | The fully wired app driven by scripted input: calculate, re-prompting, load, show, sheets, unknown commands |

## Docker

```bash
docker build -t credit-simulator .

# interactive
docker run -it --rm credit-simulator

# with file input
docker run -i --rm -v "$PWD/file_inputs.txt:/data/file_inputs.txt:ro" credit-simulator /data/file_inputs.txt

# with the load web service
docker run -it --rm -e CREDIT_SIMULATOR_LOAD_URL=https://example.com/loan.json credit-simulator
```

The published image is `<dockerhub-user>/credit-simulator` (tags: `latest`, `sha-<commit>`, and `<version>` for `v*` git tags).

## CI/CD

[`.github/workflows/ci.yml`](.github/workflows/ci.yml) runs on GitHub Actions:

1. **Build & test** (every push and pull request): `./mvnw -B verify` (unit tests and packaging). Test reports are uploaded as a build artifact.
2. **Build & push Docker image** (pushes to `main`, `v*` tags, or manual run): builds the multi-stage `Dockerfile`, pushes to Docker Hub, then runs the pushed image against `file_inputs.txt` to verify it.

Required repository secrets (Settings → Secrets and variables → Actions):

| Secret | Value |
|---|---|
| `DOCKERHUB_USERNAME` | Docker Hub username |
| `DOCKERHUB_TOKEN` | Docker Hub access token (Account settings → Personal access tokens) |

Release a version with a tag:

```bash
git tag v1.0.0
git push origin v1.0.0
```

## Design

The code follows **MVC**, with a **Factory** for vehicles and the **Command** pattern for the menu.

```
com.hosea.creditsimulator
├── CreditSimulatorApplication   entry point and dependency wiring
├── AppConfig                    environment configuration
├── model/        Vehicle (abstract) ← Car, Motorcycle; VehicleType, VehicleCondition;
│                 LoanInput, LoanApplication, YearlyInstallment, LoanSimulation
├── factory/      VehicleFactory: creates Car/Motorcycle from a VehicleType
├── service/      LoanValidator, LoanApplicationAssembler, InterestRateCalculator,
│                 InstallmentCalculator, LoanSimulationService,
│                 LoanDataClient (interface) ← HttpLoanDataClient
├── repository/   SheetRepository (interface) ← InMemorySheetRepository
├── controller/   CreditSimulatorController (command loop), InputPrompter, SimulationSession
│   └── command/  Command (interface), CommandRegistry, Calculate/Load/SaveSheet/
│                 SwitchSheet/ListSheets/Show/ExitCommand
├── view/         ConsoleView, ConsoleInput, CurrencyFormatter
└── exception/    ValidationException, LoanDataException
```

- **Model:** `Vehicle` subclasses own their base interest rate. The down payment ratio comes from `VehicleCondition`. Adding a new vehicle type means adding a subclass and one line in `VehicleFactory`.
- **View:** all output goes through `ConsoleView`, so the logic never prints directly.
- **Controller:** `CreditSimulatorController` only reads a command and dispatches it. Each menu item is a `Command` registered in `CommandRegistry`, and `show` lists the registry, so a new command shows up automatically.
- **Dependencies are injected** through constructors and depend on interfaces (`LoanDataClient`, `SheetRepository`). `LoanValidator` takes a `Clock`, so the year rule is testable.
- **Error handling:** invalid input raises `ValidationException` and the user is asked again. Web service problems raise `LoanDataException`. Neither stops the application.

## Assumptions

The problem statement leaves some details open. These are the choices made:

1. **Installment formula:** the statement shows only illustrative numbers. The formula above recalculates each year on the remaining balance with that year's rate, so installments rise as the rate rises.
2. **Rate progression:** "+0.1% every year" and "+0.5% every 2 years" are read together with the sample (8% → 8.1% → 8.6%): even years add 0.1%, odd years add 0.5%.
3. **"Total loan" is the vehicle price.** The down payment is subtracted from it to get the financed principal. The DP percentages apply to the total loan.
4. **Year rule:** a new vehicle may be from the current or previous year. No vehicle may be from a future year.
5. **Web service URL:** the URL is missing from the problem statement, and the endpoint in the sample repository (`mocky.io/v2/5d11a58d310000b23508cd62`) no longer exists. The application therefore defaults to a Beeceptor mock that serves [`mock/loan.json`](mock/loan.json), and the URL can be changed through `CREDIT_SIMULATOR_LOAD_URL`. The free Beeceptor plan has a daily request limit. If the mock is unavailable, serve `mock/loan.json` locally as shown above.
6. **Sheets** are interpreted as named, in-memory slots holding one simulation each (see [Sheets](#sheets)). They are stored behind the `SheetRepository` interface, so a file-based store could be added without changing the commands.
7. **File input** replays keyboard input line by line, so both modes share one code path.
