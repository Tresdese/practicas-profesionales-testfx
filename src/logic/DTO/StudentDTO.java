package logic.DTO;

public class StudentDTO {
    private String tuition;
    private int state;
    private String names;
    private String surnames;
    private String phone;
    private String email;
    private String user;
    private String password;
    private String NRC;
    private String creditAdvance;
    private double finalGrade;

    public StudentDTO() {
        this.tuition = "";
        this.state = 1;
        this.names = "";
        this.surnames = "";
        this.phone = "";
        this.email = "";
        this.user = "";
        this.password = "";
        this.NRC = "";
        this.creditAdvance = "";
        this.finalGrade = 0.0;
    }

    public StudentDTO(String tuition, int state, String names, String surnames, String phone, String email, String user, String password, String NRC, String creditAdvance, double finalGrade) {
        this.tuition = tuition;
        this.state = state;
        this.names = names;
        this.surnames = surnames;
        this.phone = phone;
        this.email = email;
        this.user = user;
        this.password = password;
        this.NRC = NRC;
        this.creditAdvance = creditAdvance;
        this.finalGrade = finalGrade;
    }

    public String getTuition() {
        return tuition;
    }

    public void setTuition(String tuition) {
        this.tuition = tuition;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
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

    public void setSurnames(String surnames) {
        this.surnames = surnames;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNRC() {
        return NRC;
    }

    public void setNRC(String NRC) {
        this.NRC = NRC;
    }

    public String getCreditAdvance() {
        return creditAdvance;
    }

    public void setCreditAdvance(String creditAdvance) {
        this.creditAdvance = creditAdvance;
    }

    @Override
    public String toString() {
        return "StudentDTO{" +
                "tuition='" + tuition + '\'' +
                ", state=" + state +
                ", names='" + names + '\'' +
                ", surnames='" + surnames + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", NRC='" + NRC + '\'' +
                ", creditAdvance='" + creditAdvance + '\'' +
                ", finalGrade=" + finalGrade +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof StudentDTO)) return false;
        StudentDTO that = (StudentDTO) obj;
        return state == that.state &&
                tuition.equals(that.tuition) &&
                names.equals(that.names) &&
                surnames.equals(that.surnames) &&
                phone.equals(that.phone) &&
                email.equals(that.email) &&
                user.equals(that.user) &&
                password.equals(that.password) &&
                NRC.equals(that.NRC) &&
                creditAdvance.equals(that.creditAdvance);
    }

    public double getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(double finalGrade) {
        this.finalGrade = finalGrade;
    }
}