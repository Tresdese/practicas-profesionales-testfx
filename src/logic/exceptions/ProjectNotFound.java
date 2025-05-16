package logic.exceptions;

public class ProjectNotFound extends Exception {
    public ProjectNotFound(String message) {
        super(message);
    }
    public ProjectNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}