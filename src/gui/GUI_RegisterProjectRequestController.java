//package gui;
//
//import data_access.ConecctionDataBase;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.util.StringConverter;
//import logic.DAO.ProjectRequestDAO;
//import logic.DTO.LinkedOrganizationDTO;
//import logic.DTO.ProjectDTO;
//import logic.DTO.ProjectRequestDTO;
//import logic.DTO.RepresentativeDTO;
//import logic.exceptions.*;
//import logic.services.LinkedOrganizationService;
//import logic.services.ProjectService;
//import logic.services.RepresentativeService;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.List;
//
//public class GUI_RegisterProjectRequestController {
//
//    private static final Logger logger = LogManager.getLogger(GUI_RegisterProjectRequestController.class);
//
//    @FXML
//    private Label statusLabel;
//
//    @FXML
//    private TextField fieldTuiton, fieldDuration, fieldScheduleDays, fieldDirectUsers, fieldIndirectUsers;
//
//    @FXML
//    private TextArea fieldDescription, fieldGeneralObjective;
//
//    @FXML
//    private ComboBox<ProjectDTO> comboProject;
//    @FXML
//    private ComboBox<LinkedOrganizationDTO> comboOrganization;
//    @FXML
//    private ComboBox<RepresentativeDTO> comboRepresentative;
//    @FXML
//    private ComboBox<String> comboStatus;
//
//    private ProjectService projectService;
//    private LinkedOrganizationService organizationService;
//    private RepresentativeService representativeService;
//    private ProjectRequestDAO projectRequestDAO;
//
//    @FXML
//    public void initialize() {
//        try (ConecctionDataBase connectionDB = new ConecctionDataBase();
//             Connection connection = connectionDB.connectDB()) {
//
//            projectService = new ProjectService(connection);
//            organizationService = new LinkedOrganizationService(connection);
//            representativeService = new RepresentativeService(connection);
//
//            setupComboBoxes();
//            setupComboStatus();
//            setupProjectListener();
//
//        } catch (SQLException e) {
//            logger.error("Error al inicializar servicios: {}", e.getMessage(), e);
//            statusLabel.setText("Error al cargar datos. Intente más tarde.");
//            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
//        }
//    }
//
//    private void setupComboBoxes() {
//        try {
//            // Configurar combo organizaciones
//            List<LinkedOrganizationDTO> organizations = organizationService.getAllLinkedOrganizations();
//            comboOrganization.setItems(FXCollections.observableArrayList(organizations));
//            comboOrganization.setConverter(new StringConverter<LinkedOrganizationDTO>() {
//                @Override
//                public String toString(LinkedOrganizationDTO organization) {
//                    return organization != null ? organization.getName() : "";
//                }
//
//                @Override
//                public LinkedOrganizationDTO fromString(String string) {
//                    return null; // No necesario para este caso
//                }
//            });
//
//            // Configurar combo proyectos
//            List<ProjectDTO> projects = projectService.getAllProjects();
//            comboProject.setItems(FXCollections.observableArrayList(projects));
//            comboProject.setConverter(new StringConverter<ProjectDTO>() {
//                @Override
//                public String toString(ProjectDTO project) {
//                    return project != null ? project.getName() : "";
//                }
//
//                @Override
//                public ProjectDTO fromString(String string) {
//                    return null;
//                }
//            });
//
//            // Configurar combo representantes
//            List<RepresentativeDTO> representatives = representativeService.getAllRepresentatives();
//            comboRepresentative.setItems(FXCollections.observableArrayList(representatives));
//            comboRepresentative.setConverter(new StringConverter<RepresentativeDTO>() {
//                @Override
//                public String toString(RepresentativeDTO representative) {
//                    return representative != null ? representative.getNames() + " " + representative.getSurnames() : "";
//                }
//
//                @Override
//                public RepresentativeDTO fromString(String string) {
//                    return null;
//                }
//            });
//
//        } catch (SQLException e) {
//            logger.error("Error al cargar datos en los combos: {}", e.getMessage(), e);
//        }
//    }
//
//    private void setupComboStatus() {
//        ObservableList<String> statusOptions = FXCollections.observableArrayList(
//                "Pendiente", "Aceptado", "Rechazado", "Cancelado"
//        );
//        comboStatus.setItems(statusOptions);
//        comboStatus.setValue("Pendiente");
//    }
//
//    private void setupProjectListener() {
//        comboProject.setOnAction(event -> {
//            ProjectDTO selectedProject = comboProject.getValue();
//            if (selectedProject != null) {
//                fieldDescription.setText(selectedProject.getDescription());
//            } else {
//                fieldDescription.clear();
//            }
//        });
//    }
//
//    @FXML
//    private void handleRegisterProject() {
//        try {
//            if (!areFieldsFilled()) {
//                statusLabel.setText("Todos los campos deben estar llenos.");
//                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
//                return;
//            }
//
//            ProjectDTO project = comboProject.getValue();
//            LinkedOrganizationDTO organization = comboOrganization.getValue();
//            RepresentativeDTO representative = comboRepresentative.getValue();
//
//            if (project == null || organization == null || representative == null) {
//                statusLabel.setText("Debes seleccionar una organización, un proyecto y un representante.");
//                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
//                return;
//            }
//
//            ProjectRequestDTO request = new ProjectRequestDTO(
//                    project.getIdProject(),
//                    fieldTuiton.getText(),
//                    organization.getIddOrganization(), // Usar getId() en lugar de getIdLinkedOrganization()
//                    representative.getIdRepresentative(),
//                    project.getDescription(),
//                    fieldGeneralObjective.getText(),
//                    Integer.parseInt(fieldDuration.getText()),
//                    fieldScheduleDays.getText(),
//                    Integer.parseInt(fieldDirectUsers.getText()),
//                    Integer.parseInt(fieldIndirectUsers.getText()),
//                    comboStatus.getValue()
//            );
//
//            try (ConecctionDataBase connectionDB = new ConecctionDataBase();
//                 Connection connection = connectionDB.connectDB()) {
//
//                projectRequestDAO = new ProjectRequestDAO();
//                boolean success = projectRequestDAO.insertProjectRequest(request);
//
//                if (success) {
//                    statusLabel.setText("¡Solicitud de proyecto registrada exitosamente!");
//                    statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
//                    clearFields();
//                } else {
//                    statusLabel.setText("No se pudo registrar la solicitud de proyecto.");
//                    statusLabel.setTextFill(javafx.scene.paint.Color.RED);
//                }
//            }
//
//        } catch (NumberFormatException e) {
//            logger.warn("Error de formato en campos numéricos: {}", e.getMessage(), e);
//            statusLabel.setText("Los campos de duración, usuarios directos e indirectos deben ser números.");
//            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
//        } catch (SQLException e) {
//            logger.error("Error de base de datos: {}", e.getMessage(), e);
//            statusLabel.setText("Error al registrar la solicitud: " + e.getMessage());
//            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
//        } catch (Exception e) {
//            logger.error("Error inesperado: {}", e.getMessage(), e);
//            statusLabel.setText("Ocurrió un error inesperado. Intente más tarde.");
//            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
//        }
//    }
//
//    @FXML
//    private void handleCancel() {
//        clearFields();
//        statusLabel.setText("");
//    }
//
//    private void clearFields() {
//        fieldTuiton.clear();
//        fieldDuration.clear();
//        fieldScheduleDays.clear();
//        fieldDirectUsers.clear();
//        fieldIndirectUsers.clear();
//        fieldGeneralObjective.clear();
//        fieldDescription.clear();
//        comboOrganization.getSelectionModel().clearSelection();
//        comboProject.getSelectionModel().clearSelection();
//        comboRepresentative.getSelectionModel().clearSelection();
//        comboStatus.setValue("Pendiente");
//    }
//
//    private boolean areFieldsFilled() {
//        return !fieldTuiton.getText().isEmpty() &&
//                comboOrganization.getValue() != null &&
//                comboProject.getValue() != null &&
//                comboRepresentative.getValue() != null &&
//                !fieldGeneralObjective.getText().isEmpty() &&
//                !fieldDuration.getText().isEmpty() &&
//                !fieldScheduleDays.getText().isEmpty() &&
//                !fieldDirectUsers.getText().isEmpty() &&
//                !fieldIndirectUsers.getText().isEmpty() &&
//                comboStatus.getValue() != null;
//    }
//}