package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import logic.DAO.ProjectRequestDAO;
import logic.DTO.ProjectRequestDTO;

public class GUI_ManageRequestController {

    @FXML
    private Label labelTuition;
    @FXML
    private Label labelProjectName;
    @FXML
    private Label labelDescription;
    @FXML
    private Label labelOrganization;
    @FXML
    private Label labelRepresentative;
    @FXML
    private Label statusLabel;
    @FXML
    private Button buttonApprove;
    @FXML
    private Button buttonReject;

    private ProjectRequestDTO request;
    private final ProjectRequestDAO requestDAO = new ProjectRequestDAO();

    public void setRequest(ProjectRequestDTO request, String organizationName, String representativeName) {
        this.request = request;
        labelTuition.setText(request.getTuition());
        labelProjectName.setText(request.getProjectName());
        labelDescription.setText(request.getDescription());
        labelOrganization.setText(organizationName);
        labelRepresentative.setText(representativeName);
    }

    @FXML
    private void initialize() {
        buttonApprove.setOnAction(e -> approveRequest());
        buttonReject.setOnAction(e -> rejectRequest());
    }

    private void approveRequest() {
        updateStatus("aprobada");
    }

    private void rejectRequest() {
        updateStatus("rechazada");
    }

    private void updateStatus(String status) {
        try {
            boolean updated = requestDAO.updateProjectRequestStatus(request.getRequestId(), status);
            if (updated) {
                statusLabel.setText("Estado actualizado a " + status);
                buttonApprove.setDisable(true);
                buttonReject.setDisable(true);
            } else {
                statusLabel.setText("No se pudo actualizar el estado.");
            }
        } catch (Exception ex) {
            statusLabel.setText("Error al actualizar: " + ex.getMessage());
        }
    }
}