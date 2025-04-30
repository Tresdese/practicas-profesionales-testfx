package logic.exceptions;

public class InvalidCredential extends RuntimeException {

    public InvalidCredential() {
        super("Usuario o contraseña incorrecto.");
    }

    public InvalidCredential(String message) {
        super(message);
    }
}
