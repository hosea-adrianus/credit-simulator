#!/usr/bin/env bash
#
# End-to-end check of the packaged application through the ./credit_simulator launcher.
# Used by CI after the unit tests; can also be run locally: ./scripts/smoke_test.sh

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
APP="$ROOT_DIR/credit_simulator"
failures=0

pass() { echo "  PASS  $1"; }
fail() { echo "  FAIL  $1"; failures=$((failures + 1)); }

expect_output() {
    local description="$1" expected="$2" output="$3"
    if grep -qF -- "$expected" <<<"$output"; then
        pass "$description"
    else
        fail "$description (expected: $expected)"
    fi
}

echo "Smoke test: file input"
output="$("$APP" "$ROOT_DIR/file_inputs.txt")"
expect_output "show lists save_sheet" "save_sheet <nama>" "$output"
expect_output "year 1 installment" "tahun 1 : Rp. 1,950,000.00/bln , Suku Bunga : 8%" "$output"
expect_output "year 2 installment" "tahun 2 : Rp. 2,107,950.00/bln , Suku Bunga : 8,1%" "$output"
expect_output "year 3 installment" "tahun 3 : Rp. 2,289,233.70/bln , Suku Bunga : 8,6%" "$output"
expect_output "low down payment rejected" "DP kendaraan baru minimal 35%" "$output"
expect_output "motorcycle installment" "tahun 1 : Rp. 1,703,125.00/bln , Suku Bunga : 9%" "$output"
expect_output "switch sheet" "Sheet aktif: avanza" "$output"

echo "Smoke test: bin/ launcher"
output="$("$ROOT_DIR/bin/credit_simulator" "$ROOT_DIR/file_inputs.txt")"
expect_output "bin/credit_simulator runs" "Terima kasih" "$output"

echo "Smoke test: error handling"
if "$APP" "$ROOT_DIR/does-not-exist.txt" >/dev/null 2>&1; then
    fail "missing input file should exit with an error"
else
    pass "missing input file exits with an error"
fi
if "$APP" a b >/dev/null 2>&1; then
    fail "too many arguments should exit with an error"
else
    pass "too many arguments exits with an error"
fi

if [[ $failures -gt 0 ]]; then
    echo "$failures smoke test(s) failed"
    exit 1
fi
echo "All smoke tests passed"
