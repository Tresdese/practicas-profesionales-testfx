package logic.DTO;

public class LinkedOrganizationDTO {
    private String iddOrganization;
    private String name;
    private String adddress;

    public LinkedOrganizationDTO() {
        this.iddOrganization = "";
        this.name = "";
        this.adddress = "";
    }

    public LinkedOrganizationDTO(String iddOrganization, String name, String adddress) {
        this.iddOrganization = iddOrganization;
        this.name = name;
        this.adddress = adddress;
    }

    public String getIddOrganization() {
        return iddOrganization;
    }

    public void setIddOrganization(String iddOrganization) {
        this.iddOrganization = iddOrganization;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdddress() {
        return adddress;
    }

    public void setAdddress(String adddress) {
        this.adddress = adddress;
    }

    @Override
    public String toString() {
        return "LinkedOrganizationDTO{" +
                "iddOrganization='" + iddOrganization + '\'' +
                ", name='" + name + '\'' +
                ", adddress='" + adddress + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        LinkedOrganizationDTO that = (LinkedOrganizationDTO) obj;

        if (!iddOrganization.equals(that.iddOrganization)) return false;
        if (!name.equals(that.name)) return false;
        return adddress.equals(that.adddress);
    }
}
