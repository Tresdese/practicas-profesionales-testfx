package logic.exceptions;

public class RepeatedPhone extends RuntimeException {

    public RepeatedPhone() {
        super("El número de teléfono ya está registrado.");
    }

    public RepeatedPhone(String message) {
        super(message);
    }
}
