package logic.DTO;

public class UserDTO {
    private String idUser;
    private int state;
    private String staffNumber;
    private String names;
    private String surnames;
    private String userName;
    private String password;
    private Role role;

    public UserDTO (String s) {
        this.idUser = "";
        this.state = 1;
        this.staffNumber = "";
        this.names = "";
        this.surnames = "";
        this.userName = "";
        this.password = "";
        this.role = null;
    }

    public UserDTO(String idUser, int state, String staffNumber, String names, String surnames, String userName, String password, Role role) {
        this.idUser = idUser;
        this.state = state;
        this.staffNumber = staffNumber;
        this.names = names;
        this.surnames = surnames;
        this.userName = userName;
        this.password = password;
        this.role = role;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public int getStatus() {
        return state;
    }

    public void setStatus(int state) {
        this.state = state;
    }

    public String getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(String staffNumber) {
        this.staffNumber = staffNumber;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getSurnames() {
        return surnames;
    }

    public void setSurnames(String surname) {
        this.surnames = surname;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    //TODO Métodos para realizar las acciones basadas en el rol
    public void performRoleAction1() {
        if (role != null) {
            role.performAction1();
        } else {
            System.out.println("Rol no asignado");
        }
    }

    public void performRoleAction2() {
        if (role != null) {
            role.performAction2();
        } else {
            System.out.println("Rol no asignado");
        }
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "idUser='" + idUser + '\'' +
                ", state=" + state +
                ", numberOffStaff='" + staffNumber + '\'' +
                ", names='" + names + '\'' +
                ", surname='" + surnames + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        UserDTO userDTO = (UserDTO) obj;

        if (state != userDTO.state) return false;
        if (!idUser.equals(userDTO.idUser)) return false;
        if (!staffNumber.equals(userDTO.staffNumber)) return false;
        if (!names.equals(userDTO.names)) return false;
        if (!surnames.equals(userDTO.surnames)) return false;
        if (!userName.equals(userDTO.userName)) return false;
        if (!password.equals(userDTO.password)) return false;
        return role != null ? role.equals(userDTO.role) : userDTO.role == null;
    }
}