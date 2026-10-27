package com.hosea.creditsimulator.factory;

import com.hosea.creditsimulator.model.Car;
import com.hosea.creditsimulator.model.Motorcycle;
import com.hosea.creditsimulator.model.Vehicle;
import com.hosea.creditsimulator.model.VehicleCondition;
import com.hosea.creditsimulator.model.VehicleType;

import java.util.Objects;

/**
 * Creates the concrete {@link Vehicle} for a vehicle type, so callers never depend on subclasses.
 */
public class VehicleFactory {

    public Vehicle create(VehicleType type, VehicleCondition condition, int year) {
        Objects.requireNonNull(type, "type");
        return switch (type) {
            case CAR -> new Car(condition, year);
            case MOTORCYCLE -> new Motorcycle(condition, year);
        };
    }
}
