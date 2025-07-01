package logic.DTO;

public enum ProjectStatus {
    pending("Pendiente"),
    approved("Aprobado"),
    refused("Rechazado");

    private final String dataBaseValue;

    ProjectStatus(String dataBaseValue) {
        this.dataBaseValue = dataBaseValue;
    }

    public String getDataBaseValue() {
        return dataBaseValue;
    }

    public static ProjectStatus getValueFromDataBase(String dbValue) {
        for (ProjectStatus status : values()) {
            if (status.dataBaseValue.equalsIgnoreCase(dbValue)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Estado de proyecto desconocido: " + dbValue);
    }
}