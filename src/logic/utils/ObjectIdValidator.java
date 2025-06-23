package logic.utils;

import logic.exceptions.InvalidData;

import java.util.regex.Pattern;

public class ObjectIdValidator {
    private static final Pattern ID_PATTERN = Pattern.compile("^\\d+$");

    public static void validate(String idObject) throws InvalidData {
        if (idObject == null || !ID_PATTERN.matcher(idObject).matches()) {
            throw new InvalidData("Número de identificacion inválido. Debe ser un número, permitiendo ceros a la izquierda.");
        }
    }
}
