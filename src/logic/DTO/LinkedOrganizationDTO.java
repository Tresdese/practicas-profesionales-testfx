package logic.DTO;

public class LinkedOrganizationDTO {
    private String idOrganization;
    private String name;
    private String address;
    private int status;

    public LinkedOrganizationDTO() {
        this.idOrganization = "";
        this.name = "";
        this.address = "";
        this.status = 1;
    }

    public LinkedOrganizationDTO(String idOrganization, String name, String address) {
        this.idOrganization = idOrganization;
        this.name = name;
        this.address = address;
        this.status = 1;
    }

    public String getIdOrganization() {
        return idOrganization;
    }

    public void setIdOrganization(String idOrganization) {
        this.idOrganization = idOrganization;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String adddress) {
        this.address = adddress;
    }

    public int getStatus() { return status; }

    public void setStatus(int status) { this.status = status; }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        LinkedOrganizationDTO that = (LinkedOrganizationDTO) obj;

        if (!idOrganization.equals(that.idOrganization)) return false;
        if (!name.equals(that.name)) return false;
        return address.equals(that.address);
    }
}
