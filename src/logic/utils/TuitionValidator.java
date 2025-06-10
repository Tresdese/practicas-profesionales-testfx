package logic.utils;

import logic.exceptions.InvalidData;

public class TuitionValidator {

    public static void validate(String tuiton) {
        if (!tuiton.matches("^S\\d{8}$")) {
            throw new InvalidData("La matrícula debe comenzar con 'S' y estar seguida de exactamente 8 números.");
        }
    }
}