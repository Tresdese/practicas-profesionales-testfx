package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import logic.DAO.ProjectDAO;
import logic.DAO.ProjectPresentationDAO;
import logic.DTO.ProjectDTO;
import logic.DTO.ProjectPresentationDTO;
import logic.DTO.Tipe;

import java.sql.Timestamp;
import java.util.List;

public class GUI_RegisterPresentationController {

    @FXML
    private ComboBox<ProjectDTO> idProjectComboBox;
    @FXML
    private DatePicker dateField;
    @FXML
    private TextField timeField;
    @FXML
    private ComboBox<Tipe> typeComboBox;
    @FXML
    private Button registerButton;
    @FXML
    private Label statusLabel;

    private GUI_CheckListOfPresentationsController parentController;
    private final ProjectPresentationDAO dao = new ProjectPresentationDAO();
    private final ProjectDAO projectDAO = new ProjectDAO();

    @FXML
    public void initialize() {
        try {
            List<ProjectDTO> projects = projectDAO.getAllProjects();
            idProjectComboBox.getItems().setAll(projects);
        } catch (Exception e) {
            statusLabel.setText("Error al cargar proyectos: " + e.getMessage());
        }
        typeComboBox.getItems().setAll(Tipe.values());
        registerButton.setOnAction(event -> handleRegister());
    }

    private void handleRegister() {
        try {
            ProjectDTO selectedProject = idProjectComboBox.getValue();
            if (selectedProject == null || dateField.getValue() == null || typeComboBox.getValue() == null || timeField.getText().trim().isEmpty()) {
                statusLabel.setText("Todos los campos son obligatorios.");
                return;
            }
            String idProject = selectedProject.getIdProject();
            String dateString = dateField.getValue().toString() + " " + timeField.getText().trim() + ":00";
            Timestamp date = Timestamp.valueOf(dateString);
            Tipe type = typeComboBox.getValue();

            ProjectPresentationDTO dto = new ProjectPresentationDTO(idProject, date, type);
            boolean success = dao.insertProjectPresentation(dto);

            if (success) {
                statusLabel.setText("Presentación registrada correctamente.");
                idProjectComboBox.setValue(null);
                dateField.setValue(null);
                timeField.clear();
                typeComboBox.setValue(null);
                if (parentController != null) {
                    parentController.reloadPresentations();
                }
            } else {
                statusLabel.setText("No se pudo registrar la presentación.");
            }
        } catch (IllegalArgumentException e) {
            statusLabel.setText("Formato de fecha/hora incorrecto. Usa HH:mm.");
        } catch (Exception e) {
            statusLabel.setText("Error al registrar: " + e.getMessage());
        }
    }

    public void setParentController(GUI_CheckListOfPresentationsController parentController) {
        this.parentController = parentController;
    }
}