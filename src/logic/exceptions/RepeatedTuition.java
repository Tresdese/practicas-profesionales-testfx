package logic.exceptions;

public class RepeatedTuition extends RuntimeException {
    public RepeatedTuition() {
        super("La matrícula ya se encuentra en uso.");
    }

    public RepeatedTuition(String message) {
        super(message);
    }
}