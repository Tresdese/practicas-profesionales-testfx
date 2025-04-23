package logic.utils;

import logic.exceptions.InvalidData;

public class PhoneValidator {

    public static void validate(String phone) {
        if (!phone.matches("\\d{10}")) {
            throw new InvalidData("El número de teléfono debe tener exactamente 10 dígitos.");
        }
    }
}