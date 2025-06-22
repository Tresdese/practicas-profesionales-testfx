package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import logic.DAO.ProjectDAO;
import logic.DAO.ProjectPresentationDAO;
import logic.DTO.ProjectDTO;
import logic.DTO.ProjectPresentationDTO;
import logic.DTO.Tipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
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

    private static final Logger LOGGER = LogManager.getLogger(GUI_RegisterPresentationController.class);

    @FXML
    public void initialize() {
        try {
            List<ProjectDTO> projects = projectDAO.getAllProjects();
            idProjectComboBox.getItems().setAll(projects);
        } catch (SQLException e) {
            if (e.getMessage().contains("The driver has not received any packets from the server")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                LOGGER.error("Error de conexion con la base de datos: " , e);
            } else if (e.getMessage().contains("Unknown database")) {
                statusLabel.setText("Base de datos desconocida.");
                LOGGER.error("Base de datos desconocida: ", e);
            } else {
                statusLabel.setText("Error al cargar proyectos: ");
                LOGGER.error("Error al cargar proyectos: ", e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error al cargar proyectos: ");
            LOGGER.error("Error al cargar proyectos: ", e);
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
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                statusLabel.setText("Error de conexión con la base de datos.");
                LOGGER.error("Error de conexión con la base de datos: ", e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos desconocida.");
                LOGGER.error("Base de datos desconocida: ", e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                LOGGER.error("Acceso denegado a la base de datos: ", e);
            } else if ("23000".equals(sqlState)) {
                statusLabel.setText("Violación de restricción de integridad.");
                LOGGER.error("Violación de restricción de integridad: ", e);
            } else {
                statusLabel.setText("Error al conectar a la base de datos: ");
                LOGGER.error("Error al conectar a la base de datos: ", e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al registrar: ");
            LOGGER.error("Error inesperado al registrar la presentación: ", e);
        }
    }

    public void setParentController(GUI_CheckListOfPresentationsController parentController) {
        this.parentController = parentController;
    }
}