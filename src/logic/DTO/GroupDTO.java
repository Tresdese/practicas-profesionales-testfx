package logic.DTO;

public class GroupDTO {
    private String NRC;
    private String name;
    private String idUser;
    private String idPeriod;

    public GroupDTO() {
        this.NRC = "";
        this.name = "";
        this.idUser = "";
        this.idPeriod = "";
    }

    public GroupDTO(String NRC, String name, String idUser, String idPeriod) {
        this.NRC = NRC;
        this.name = name;
        this.idUser = idUser;
        this.idPeriod = idPeriod;
    }

    public String getNRC() {
        return NRC;
    }

    public void setNRC(String NRC) {
        this.NRC = NRC;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdPeriod() {
        return idPeriod;
    }

    public void setIdPeriod(String idPeriod) {
        this.idPeriod = idPeriod;
    }

    @Override
    public String toString() {
        return "GroupDTO{" +
                "NRC='" + NRC + '\'' +
                ", name='" + name + '\'' +
                ", idUser='" + idUser + '\'' +
                ", idPeriod='" + idPeriod + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        GroupDTO groupDTO = (GroupDTO) obj;

        if (!NRC.equals(groupDTO.NRC)) return false;
        if (!name.equals(groupDTO.name)) return false;
        if (!idUser.equals(groupDTO.idUser)) return false;
        return idPeriod.equals(groupDTO.idPeriod);
    }
}
