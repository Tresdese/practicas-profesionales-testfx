package logic.DTO;

public enum Role {
    ACADEMIC("Academico"),
    EVALUATOR_ACADEMIC("Academico_Evaluador"),
    COORDINATOR("Coordinador"),
    GUEST("Invitado");

    private final String dataBaseValue;

    Role(String dataBaseValue) {
        this.dataBaseValue = dataBaseValue;
    }

    public String getDataBaseValue() {
        return dataBaseValue;
    }

    public static Role getValueFromDataBase(String dbValue) {
        for (Role role : values()) {
            if (role.dataBaseValue.equalsIgnoreCase(dbValue)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Rol desconocido: " + dbValue);
    }
}