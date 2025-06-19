package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import logic.DAO.DepartmentDAO;
import logic.DTO.DepartmentDTO;

public class GUI_RegisterDepartmentController {

    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Button registerButton;
    @FXML
    private Label messageLabel;

    private int organizationId = -1;

    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
    }

    @FXML
    private void handleRegister() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (name.isEmpty() || description.isEmpty()) {
            messageLabel.setText("Todos los campos son obligatorios.");
            return;
        }
        if (organizationId <= 0) {
            messageLabel.setText("ID de organización no válido.");
            return;
        }

        DepartmentDTO department = new DepartmentDTO(0, name, description, organizationId);
        DepartmentDAO dao = new DepartmentDAO();
        try {
            if (dao.insertDepartment(department)) {
                messageLabel.setText("Departamento registrado exitosamente.");
                nameField.clear();
                descriptionArea.clear();
            } else {
                messageLabel.setText("No se pudo registrar el departamento.");
            }
        } catch (Exception e) {
            messageLabel.setText("Error al registrar: " + e.getMessage());
        }
    }
}