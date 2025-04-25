package logic.exceptions;

public class PasswordDoesNotMatch extends RuntimeException {

    public PasswordDoesNotMatch() {
        super("Las contraseña no coinciden.");
    }

    public PasswordDoesNotMatch(String message) {
        super(message);
    }
}
