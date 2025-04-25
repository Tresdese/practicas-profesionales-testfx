package logic.exceptions;

public class RepeatedUserName extends RuntimeException {
    public RepeatedUserName() {
        super("El nombre de usuario ya existe.");
    }

    public RepeatedUserName(String message) { super(message); }
}
