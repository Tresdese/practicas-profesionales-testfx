package logic.exceptions;

public class InvalidData extends RuntimeException {
    public InvalidData() {
        super("El dato ingresado es inválido.");
    }

    public InvalidData(String message) {
        super(message);
    }
}