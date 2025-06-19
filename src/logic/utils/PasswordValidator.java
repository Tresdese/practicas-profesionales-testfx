package logic.utils;

import logic.exceptions.InvalidData;

public class PasswordValidator {
    public static void validate(String password) {
        if (password == null || password.length() < 12) {
            throw new InvalidData("La contraseña debe tener al menos 12 caracteres.");
        }
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{12,}$")) {
            throw new InvalidData("La contraseña debe contener al menos una mayúscula, una minúscula, un número y un símbolo.");
        }
    }
}
