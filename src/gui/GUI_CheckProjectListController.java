package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import logic.DTO.LinkedOrganizationDTO;
import logic.DTO.ProjectDTO;
import logic.DTO.UserDTO;
import logic.services.ProjectService;
import logic.services.ServiceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class GUI_CheckProjectListController {

    private static final Logger logger = LogManager.getLogger(GUI_CheckProjectListController.class);

    @FXML
    private Button buttonRegisterProject;

    @FXML
    private TableColumn<ProjectDTO, String> projectName;

    @FXML
    private TableColumn<ProjectDTO, String> projectStartDate;

    @FXML
    private TableColumn<ProjectDTO, String> projectEndDate;

    @FXML
    private TableColumn<ProjectDTO, String> projectOrganization;

    @FXML
    private TableColumn<ProjectDTO, String> projectAcademic;

    @FXML
    private TableColumn<ProjectDTO, Void> columnDetails;

    @FXML
    private TableColumn<ProjectDTO, Void> columnManagement;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private Label statusLabel;

    @FXML
    private TableView<ProjectDTO> tableView;

    private ProjectDTO selectedProject;
    private ProjectService projectService;
    private ServiceConfig serviceConfig;

    @FXML
    public void initialize() {
        try {
            serviceConfig = new ServiceConfig();
            projectService = serviceConfig.getProjectService();
        } catch (SQLException e) {
            logger.error("Error al inicializar el servicio de proyectos: {}", e.getMessage(), e);
        }

        projectName.setCellValueFactory(new PropertyValueFactory<>("name"));

        SimpleDateFormat dateFormat = new SimpleDateFormat("DD/MM/YYYY");

        projectStartDate.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStartDate() != null) {
                return new SimpleStringProperty(dateFormat.format(cellData.getValue().getStartDate()));
            } else {
                return new SimpleStringProperty("Sin fecha");
            }
        });

        projectEndDate.setCellValueFactory(cellData -> {
            if (cellData.getValue().getApproximateDate() != null) {
                return new SimpleStringProperty(dateFormat.format(cellData.getValue().getApproximateDate()));
            } else {
                return new SimpleStringProperty("Sin fecha");
            }
        });

        projectOrganization.setCellValueFactory(cellData -> {
            String orgId = String.valueOf(cellData.getValue().getIdOrganization());
            String orgName = getOrganizationNameById(orgId);
            return new SimpleStringProperty(orgName);
        });

        projectAcademic.setCellValueFactory(cellData -> {
            String academicId = cellData.getValue().getIdUser();
            String academicName = getAcademicNameById(academicId);
            return new SimpleStringProperty(academicName);
        });

        addDetailsButtonToTable();
        addManagementButtonToTable();

        loadProjectData();

        searchButton.setOnAction(event -> searchProject());
        buttonRegisterProject.setOnAction(event -> openRegisterProjectWindow());

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedProject = newValue;
            tableView.refresh();
        });
    }

    private String getOrganizationNameById(String organizationId) {
        try {
            if (organizationId == null || organizationId.isEmpty() || organizationId.equals("0")) {
                return "No asignado";
            }

            LinkedOrganizationDTO organization = serviceConfig.getLinkedOrganizationService()
                    .searchLinkedOrganizationById(organizationId);

            return (organization != null) ? organization.getName() : "Organización no encontrada";
        } catch (Exception e) {
            logger.error("Error al obtener nombre de organización: {}", e.getMessage(), e);
            return "Error";
        }
    }

    private String getAcademicNameById(String academicId) {
        try {
            if (academicId == null || academicId.isEmpty()) {
                return "No asignado";
            }

            UserDTO academic = serviceConfig.getUserService().searchUserById(academicId);
            return (academic != null) ? academic.getNames() + " " + academic.getSurnames() : "Académico no encontrado";
        } catch (Exception e) {
            logger.error("Error al obtener nombre del académico: {}", e.getMessage(), e);
            return "Error";
        }
    }

    private void openRegisterProjectWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterProject.fxml"));
            Parent root = loader.load();

            GUI_RegisterProjectController registerController = loader.getController();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de registro de proyectos: {}", e.getMessage(), e);
        }
    }

    public void loadProjectData() {
        ObservableList<ProjectDTO> projectList = FXCollections.observableArrayList();

        try {
            List<ProjectDTO> projects = projectService.getAllProjects();
            projectList.addAll(projects);
            statusLabel.setText("");
        } catch (SQLException e) {
            statusLabel.setText("Error al cargar los datos de los proyectos.");
            logger.error("Error al cargar los datos de los proyectos: {}", e.getMessage(), e);
        }

        tableView.setItems(projectList);
    }

    private void searchProject() {
        String searchQuery = searchField.getText().trim();
        if (searchQuery.isEmpty()) {
            loadProjectData();
            return;
        }

        ObservableList<ProjectDTO> filteredList = FXCollections.observableArrayList();

        try {
            ProjectDTO projectById = projectService.searchProjectById(searchQuery);
            if (projectById != null && !"-1".equals(projectById.getIdProject())) {
                filteredList.add(projectById);
            } else {
                ProjectDTO projectByName = projectService.searchProjectByName(searchQuery);
                if (projectByName != null && !"-1".equals(projectByName.getIdProject())) {
                    filteredList.add(projectByName);
                }
            }

            if (filteredList.isEmpty()) {
                statusLabel.setText("No se encontraron proyectos con ese ID o nombre.");
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            } else {
                statusLabel.setText("");
            }
        } catch (SQLException e) {
            statusLabel.setText("Error al buscar el proyecto.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            logger.error("Error al buscar el proyecto: {}", e.getMessage(), e);
        }

        tableView.setItems(filteredList);
    }

    private void addDetailsButtonToTable() {
        Callback<TableColumn<ProjectDTO, Void>, TableCell<ProjectDTO, Void>> cellFactory = param -> new TableCell<>() {
            private final Button detailsButton = new Button("Ver detalles");

            {
                detailsButton.setOnAction(event -> {
                    if (getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                        return;
                    }
                    ProjectDTO project = getTableView().getItems().get(getIndex());
                    System.out.println("Detalles de: " + project.getName());
                    // TODO Lógica para mostrar detalles del proyecto
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || selectedProject == null || getIndex() < 0 || getIndex() >= getTableView().getItems().size() || !getTableView().getItems().get(getIndex()).equals(selectedProject)) {
                    setGraphic(null);
                } else {
                    setGraphic(detailsButton);
                }
            }
        };

        columnDetails.setCellFactory(cellFactory);
    }

    private void addManagementButtonToTable() {
        Callback<TableColumn<ProjectDTO, Void>, TableCell<ProjectDTO, Void>> cellFactory = param -> new TableCell<>() {
            private final Button manageButton = new Button("Gestionar");

            {
                manageButton.setOnAction(event -> {
                    if (getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                        return;
                    }
                    ProjectDTO project = getTableView().getItems().get(getIndex());
                    openManageProjectWindow(project);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || selectedProject == null ||
                        getIndex() < 0 || getIndex() >= getTableView().getItems().size() ||
                        !getTableView().getItems().get(getIndex()).equals(selectedProject)) {
                    setGraphic(null);
                } else {
                    setGraphic(manageButton);
                }
            }
        };

        columnManagement.setCellFactory(cellFactory);
    }

    private void openManageProjectWindow(ProjectDTO project) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_ManageProject.fxml"));
            Parent root = loader.load();

            GUI_ManageProjectController controller = loader.getController();
            controller.setProjectData(project);
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Gestionar Proyecto");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            statusLabel.setText("Error al abrir la ventana de gestión de proyecto.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            logger.error("Error al abrir la ventana de gestión de proyecto: {}", e.getMessage(), e);
        }
    }
}