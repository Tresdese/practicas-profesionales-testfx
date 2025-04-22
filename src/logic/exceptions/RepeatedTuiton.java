package logic.exceptions;

public class RepeatedTuiton extends RuntimeException {
    public RepeatedTuiton() {
        super("La matrícula ya se encuentra en uso.");
    }

    public RepeatedTuiton(String message) {
        super(message);
    }
}