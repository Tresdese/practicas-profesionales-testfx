package logic.DTO;

public enum ProjectStatus {
    PENDING("pendiente"),
    APPROVED("aprobado"),
    REJECTED("rechazado");

    private final String value;

    ProjectStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
