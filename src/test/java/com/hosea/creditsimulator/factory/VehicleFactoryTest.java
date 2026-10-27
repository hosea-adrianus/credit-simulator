package com.hosea.creditsimulator.factory;

import com.hosea.creditsimulator.model.Car;
import com.hosea.creditsimulator.model.Motorcycle;
import com.hosea.creditsimulator.model.Vehicle;
import com.hosea.creditsimulator.model.enums.VehicleCondition;
import com.hosea.creditsimulator.model.enums.VehicleType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VehicleFactoryTest {

    private final VehicleFactory factory = new VehicleFactory();

    @Test
    void createsCarWithEightPercentBaseRate() {
        Vehicle vehicle = factory.create(VehicleType.CAR, VehicleCondition.NEW, 2025);

        assertInstanceOf(Car.class, vehicle);
        assertEquals(VehicleType.CAR, vehicle.type());
        assertEquals(0, new BigDecimal("8").compareTo(vehicle.baseInterestRate()));
        assertEquals(2025, vehicle.year());
    }

    @Test
    void createsMotorcycleWithNinePercentBaseRate() {
        Vehicle vehicle = factory.create(VehicleType.MOTORCYCLE, VehicleCondition.USED, 2019);

        assertInstanceOf(Motorcycle.class, vehicle);
        assertEquals(0, new BigDecimal("9").compareTo(vehicle.baseInterestRate()));
    }

    @Test
    void downPaymentRatioFollowsCondition() {
        assertEquals(new BigDecimal("0.35"),
                factory.create(VehicleType.CAR, VehicleCondition.NEW, 2025).minDownPaymentRatio());
        assertEquals(new BigDecimal("0.25"),
                factory.create(VehicleType.MOTORCYCLE, VehicleCondition.USED, 2015).minDownPaymentRatio());
    }

    @Test
    void rejectsMissingType() {
        assertThrows(NullPointerException.class, () -> factory.create(null, VehicleCondition.NEW, 2025));
    }
}
