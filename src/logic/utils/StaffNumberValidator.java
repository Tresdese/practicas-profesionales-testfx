package logic.utils;

import logic.exceptions.InvalidData;

import java.util.regex.Pattern;

public class StaffNumberValidator {
    private static final Pattern STAFF_NUMBER_PATTERN = Pattern.compile("^0{0,4}[1-9]\\d{0,4}$");

    public static void validate(String staffNumber) throws InvalidData {
        if (staffNumber == null || !STAFF_NUMBER_PATTERN.matcher(staffNumber).matches()) {
            throw new InvalidData("Número de personal inválido. Debe ser un número entre 1 y 99999, permitiendo ceros a la izquierda.");
        }
    }
}
