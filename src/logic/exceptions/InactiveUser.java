package logic.exceptions;

public class InactiveUser extends RuntimeException {

    public InactiveUser() {
        super("Usuario dado de baja o inactivo.");
    }

    public InactiveUser(String message) {
        super(message);
    }
}