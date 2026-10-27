package com.hosea.creditsimulator.factory;

import com.hosea.creditsimulator.model.Car;
import com.hosea.creditsimulator.model.Motorcycle;
import com.hosea.creditsimulator.model.Vehicle;
import com.hosea.creditsimulator.model.enums.VehicleCondition;
import com.hosea.creditsimulator.model.enums.VehicleType;

import java.util.Objects;

public class VehicleFactory {

    public Vehicle create(VehicleType type, VehicleCondition condition, int year) {
        Objects.requireNonNull(type, "type");
        return switch (type) {
            case CAR -> new Car(condition, year);
            case MOTORCYCLE -> new Motorcycle(condition, year);
        };
    }
}
