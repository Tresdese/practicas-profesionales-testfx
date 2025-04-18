package logic.DTO;

import java.util.Date;

public class EvidenceDTO {
    private int idEvidence;
    private String evidenceName;
    private Date deliveryDate;
    private String route;

    public EvidenceDTO() {
        this.idEvidence = 0;
        this.evidenceName = "";
        this.deliveryDate = null;
        this.route = "";
    }

    public EvidenceDTO(int idEvidence, String evidenceName, Date deliveryDate, String route) {
        this.idEvidence = idEvidence;
        this.evidenceName = evidenceName;
        this.deliveryDate = deliveryDate;
        this.route = route;
    }

    public int getIdEvidence() {
        return idEvidence;
    }

    public void setIdEvidence(int idEvidence) {
        this.idEvidence = idEvidence;
    }

    public String getEvidenceName() {
        return evidenceName;
    }

    public void setEvidenceName(String evidenceName) {
        this.evidenceName = evidenceName;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    @Override
    public String toString() {
        return "EvidenceDTO{" +
                "idEvidence=" + idEvidence +
                ", evidenceName='" + evidenceName + '\'' +
                ", deliveryDate=" + deliveryDate +
                ", route='" + route + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        EvidenceDTO that = (EvidenceDTO) obj;

        if (idEvidence != that.idEvidence) return false;
        if (!evidenceName.equals(that.evidenceName)) return false;
        if (!deliveryDate.equals(that.deliveryDate)) return false;
        return route.equals(that.route);
    }
}