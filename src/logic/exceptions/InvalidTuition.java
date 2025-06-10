package logic.exceptions;

public class InvalidTuition extends RuntimeException {

    public InvalidTuition() {
        super("La matricula no es valida.");
    }

    public InvalidTuition(String message) {
        super(message);
    }
}
