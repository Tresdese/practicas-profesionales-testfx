package logic.utils;

import logic.exceptions.InvalidData;

public class StaffNumberValidator {

    public static void validate(String id) {
        if (!id.matches("^\\d{5}$")) {
            throw new InvalidData("El número de personal debe tener exactamente 8 números.");
        }
    }

}
