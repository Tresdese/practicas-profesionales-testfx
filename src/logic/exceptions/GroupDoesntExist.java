package logic.exceptions;

public class GroupDoesntExist extends RuntimeException {

    public GroupDoesntExist() {
        super("El grupo no existe.");
    }

    public GroupDoesntExist(String message) {
        super(message);
    }
}
