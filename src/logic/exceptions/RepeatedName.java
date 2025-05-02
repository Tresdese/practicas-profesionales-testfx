package logic.exceptions;

public class RepeatedName extends RuntimeException {
    public RepeatedName() {
        super("El nombre ya existe.");
    }

    public RepeatedName(String message) { super(message); }
}
