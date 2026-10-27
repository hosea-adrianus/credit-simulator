package com.hosea.creditsimulator.model;

import com.hosea.creditsimulator.exception.ValidationException;
import com.hosea.creditsimulator.model.enums.VehicleCondition;
import com.hosea.creditsimulator.model.enums.VehicleType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VehicleLabelTest {

    @ParameterizedTest
    @ValueSource(strings = {"Mobil", "mobil", "MOBIL", "  mObIl  "})
    void parsesCarIgnoringCase(String input) {
        assertEquals(VehicleType.CAR, VehicleType.fromLabel(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Motor", "motor", "MOTOR"})
    void parsesMotorcycleIgnoringCase(String input) {
        assertEquals(VehicleType.MOTORCYCLE, VehicleType.fromLabel(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"truk", "car", "123"})
    void rejectsUnknownVehicleType(String input) {
        assertThrows(ValidationException.class, () -> VehicleType.fromLabel(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Baru", "baru", "BARU"})
    void parsesNewIgnoringCase(String input) {
        assertEquals(VehicleCondition.NEW, VehicleCondition.fromLabel(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Bekas", "bekas", "BEKAS"})
    void parsesUsedIgnoringCase(String input) {
        assertEquals(VehicleCondition.USED, VehicleCondition.fromLabel(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"lama", "new"})
    void rejectsUnknownCondition(String input) {
        assertThrows(ValidationException.class, () -> VehicleCondition.fromLabel(input));
    }
}
