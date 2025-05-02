package logic.utils;

public class GradeValidator {
    public static void validate(String grade) {
        if (!grade.matches("\\d+(\\.\\d+)?")) {
            throw new IllegalArgumentException("La calificación debe ser un número válido.");
        }

        double value = Double.parseDouble(grade);
        if (value < 1 || value > 10) {
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 10.");
        }
    }
}