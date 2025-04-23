package logic.exceptions;

public class EmptyFields extends RuntimeException {

    public EmptyFields() {
        super("Se detectaron campos vacios.");
    }

    public EmptyFields(String message) {
        super(message);
    }
}
