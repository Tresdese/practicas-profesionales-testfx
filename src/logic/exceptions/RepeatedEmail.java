package logic.exceptions;

public class RepeatedEmail extends RuntimeException {

    public RepeatedEmail() {
        super("El correo electrónico ya está registrado.");
    }

    public RepeatedEmail(String message) {
        super(message);
    }
}
