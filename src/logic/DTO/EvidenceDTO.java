package logic.DTO;

import java.util.Date;

public class EvidenceDTO {
    private int idEvidence;
    private String evidenceName;
    private Date deliveryDate;
    private String route;
    private byte[] contenido;

    public EvidenceDTO() {
        this.idEvidence = 0;
        this.evidenceName = "";
        this.deliveryDate = null;
        this.route = "";
        this.contenido = null;
    }

    public EvidenceDTO(int idEvidence, String evidenceName, Date deliveryDate, String route, byte[] contenido) {
        this.idEvidence = idEvidence;
        this.evidenceName = evidenceName;
        this.deliveryDate = deliveryDate;
        this.route = route;
        this.contenido = contenido;
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

    public byte[] getContenido() {
        return contenido;
    }

    public void setContenido(byte[] contenido) {
        this.contenido = contenido;
    }
}