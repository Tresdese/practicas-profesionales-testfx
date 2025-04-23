package logic.utils;

import logic.exceptions.InvalidData;

public class EmailValidator {

    public static void validate(String email) {
        if (!email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new InvalidData("El correo electrónico no es válido.");
        }
    }
}