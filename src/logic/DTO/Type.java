package logic.DTO;

public enum Type {
    Final("Final"),
    Partial("Parcial");

    private final String dataBaseValue;

    Type(String dataBaseValue) {
        this.dataBaseValue = dataBaseValue;
    }

    public String getDataBaseValue() {
        return dataBaseValue;
    }

    public static Type getValueFromDataBase(String dbValue) {
        for (Type type : values()) {
            if (type.dataBaseValue.equalsIgnoreCase(dbValue)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo desconocido: " + dbValue);
    }
}
