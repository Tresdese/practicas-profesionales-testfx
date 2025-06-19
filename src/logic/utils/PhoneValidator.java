package logic.utils;

import logic.exceptions.InvalidData;

public class PhoneValidator {

    public static void validate(String phone) {
        if (!phone.matches("^(\\+\\d{1,3}\\s?)?(\\d{2,4}[\\s-]?){2,3}((?i)ext\\.?\\s?\\d{1,6})?")) {
            throw new InvalidData("El número de teléfono no tiene un formato válido. Ejemplo: +52 55 1234 5678 ext. 123");
        }
    }
}