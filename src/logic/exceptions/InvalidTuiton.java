package logic.exceptions;

public class InvalidTuiton extends RuntimeException {

    public InvalidTuiton () {
        super("La matricula no es valida.");
    }

    public InvalidTuiton(String message) {
        super(message);
    }
}
