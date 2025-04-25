package logic.exceptions;

public class RepeatedId extends RuntimeException {
  public RepeatedId() {
    super("La ID ya se encuentra registrada");
  }

    public RepeatedId(String message) { super(message); }
}
