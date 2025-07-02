package logic.DTO;

public enum ProjectStatus {
    pending("pendiente"),
    approved("aprobada"),
    refused("rechazada");

    private final String dataBaseValue;

    ProjectStatus(String dataBaseValue) {
        this.dataBaseValue = dataBaseValue;
    }

    public String getDataBaseValue() {
        return dataBaseValue;
    }

    public static ProjectStatus getValueFromDataBase(String dataBaseValue) {
        for (ProjectStatus status : values()) {
            if (status.dataBaseValue.equalsIgnoreCase(dataBaseValue)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Estado de proyecto desconocido: " + dataBaseValue);
    }
}