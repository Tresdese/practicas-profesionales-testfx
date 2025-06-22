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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

import logic.DTO.LinkedOrganizationDTO;
import logic.DTO.ProjectDTO;
import logic.DTO.Role;
import logic.DTO.UserDTO;
import logic.DTO.DepartmentDTO;

import logic.services.ProjectService;
import logic.services.ServiceConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class GUI_CheckProjectListController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_CheckProjectListController.class);

    @FXML
    private Button registerProjectButton;

    @FXML
    private TableColumn<ProjectDTO, String> projectNameColumn;

    @FXML
    private TableColumn<ProjectDTO, String> projectStartDateColumn;

    @FXML
    private TableColumn<ProjectDTO, String> projectEndDateColumn;

    @FXML
    private TableColumn<ProjectDTO, String> projectOrganizationColumn;

    @FXML
    private TableColumn<ProjectDTO, String> projectAcademicColumn;

    @FXML
    private TableColumn<ProjectDTO, String> projectDepartmentColumn;

    @FXML
    private TableColumn<ProjectDTO, Void> managementColumn;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private Label statusLabel;

    @FXML
    private TableView<ProjectDTO> tableView;

    @FXML
    private Label projectCountsLabel;

    private ProjectDTO selectedProject;
    private ProjectService projectService;
    private ServiceConfig serviceConfig;
    private logic.DAO.DepartmentDAO departmentDAO = new logic.DAO.DepartmentDAO();
    private Role userRole;

    @FXML
    public void initialize() {
        try {
            serviceConfig = new ServiceConfig();
            projectService = serviceConfig.getProjectService();
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error de base de datos al inicializar el servicio de proyectos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de base de datos al inicializar el servicio de proyectos: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al inicializar el servicio de proyectos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al inicializar el servicio de proyectos: {}", e.getMessage(), e);
        }

        setAllCellValueFactories();

        addManagementButtonToTable();

        loadProjectData();

        initializeButtons();

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedProject = newValue;
            tableView.refresh();
        });
    }

    public void setRole(Role role) {
        this.userRole = role;
        applyRolRestrictions();
    }

    private void applyRolRestrictions() {
        if (userRole == Role.COORDINADOR) {
            registerProjectButton.setVisible(true);
        } else if (userRole == Role.ACADEMICO) {
            registerProjectButton.setVisible(false);
        } else {
            registerProjectButton.setVisible(false);
        }
    }

    private void initializeButtons() {
        registerProjectButton.setOnAction(event -> openRegisterProjectWindow());
        searchButton.setOnAction(event -> searchProject());
    }

    private void setAllCellValueFactories() {
        projectNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        projectStartDateColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStartDate() != null) {
                return new SimpleStringProperty(dateFormat.format(cellData.getValue().getStartDate()));
            } else {
                return new SimpleStringProperty("Sin fecha");
            }
        });

        projectEndDateColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getApproximateDate() != null) {
                return new SimpleStringProperty(dateFormat.format(cellData.getValue().getApproximateDate()));
            } else {
                return new SimpleStringProperty("Sin fecha");
            }
        });

        projectOrganizationColumn.setCellValueFactory(cellData -> {
            String organizationId = String.valueOf(cellData.getValue().getIdOrganization());
            String organizationName = getOrganizationNameById(organizationId);
            return new SimpleStringProperty(organizationName);
        });

        projectAcademicColumn.setCellValueFactory(cellData -> {
            String academicId = cellData.getValue().getIdUser();
            String academicName = getAcademicNameById(academicId);
            return new SimpleStringProperty(academicName);
        });

        projectDepartmentColumn.setCellValueFactory(cellData -> {
            int departmentId = cellData.getValue().getIdDepartment();
            String departmentName = getDepartmentNameById(departmentId);
            return new SimpleStringProperty(departmentName);
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
        } catch (IllegalArgumentException e) {
            LOGGER.error("ID de organización inválido: {}", e.getMessage(), e);
            statusLabel.setText("ID de organización inválido");
            statusLabel.setTextFill(Color.RED);
            return "ID inválido";
        } catch (Exception e) {
            LOGGER.error("Error inesperado al obtener organización: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al obtener organización");
            statusLabel.setTextFill(Color.RED);
            return "Error inesperado de sistema";
        }
    }

    private String getAcademicNameById(String academicId) {
        try {
            if (academicId == null || academicId.isEmpty()) {
                return "No asignado";
            }

            UserDTO academic = serviceConfig.getUserService().searchUserById(academicId);
            return (academic != null) ? academic.getNames() + " " + academic.getSurnames() : "Académico no encontrado";
        }catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Error de conexión a la base de datos");
                statusLabel.setTextFill(Color.RED);
                return "Error de conexión a la base de datos";
            } else if (sqlState != null && sqlState.equals("08S01")) {
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Conexión interrumpida a la base de datos");
                statusLabel.setTextFill(Color.RED);
                return "Conexión interrumpida a la base de datos";
            } else if (sqlState != null && sqlState.equals("42000")) {
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
                statusLabel.setText("Base de datos desconocida");
                statusLabel.setTextFill(Color.RED);
                return "Base de datos desconocida";
            } else if (sqlState != null && sqlState.equals("28000")) {
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Acceso denegado a la base de datos");
                statusLabel.setTextFill(Color.RED);
                return "Acceso denegado a la base de datos";
            } else {
                LOGGER.error("Error de base de datos al obtener académico: {}", e.getMessage(), e);
                statusLabel.setText("Error de base de datos al obtener académico");
                statusLabel.setTextFill(Color.RED);
                return "Error al obtener académico";
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error("ID de academico inválido: {}", e.getMessage(), e);
            statusLabel.setText("ID de académico inválido");
            statusLabel.setTextFill(Color.RED);
            return "ID inválido";
        } catch (Exception e) {
            LOGGER.error("Error inesperado al obtener academico: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al obtener académico");
            statusLabel.setTextFill(Color.RED);
            return "Error inesperado de sistema";
        }
    }

    private String getDepartmentNameById(int departmentId) {
        try {
            if (departmentId == 0) {
                return "No asignado";
            }
            DepartmentDTO department = departmentDAO.searchDepartmentById(departmentId);
            return (department != null) ? department.getName() : "Departamento no encontrado";
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Error de conexión a la base de datos");
                statusLabel.setTextFill(Color.RED);
                return "Error de conexión a la base de datos";
            } else if (sqlState != null && sqlState.equals("08S01")) {
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Conexión interrumpida a la base de datos");
                statusLabel.setTextFill(Color.RED);
                return "Conexión interrumpida a la base de datos";
            } else if (sqlState != null && sqlState.equals("42000")) {
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
                statusLabel.setText("Base de datos desconocida");
                statusLabel.setTextFill(Color.RED);
                return "Base de datos desconocida";
            } else if (sqlState != null && sqlState.equals("28000")) {
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Acceso denegado a la base de datos");
                statusLabel.setTextFill(Color.RED);
                return "Acceso denegado a la base de datos";
            } else {
                LOGGER.error("Error de base de datos al obtener departamento: {}", e.getMessage(), e);
                statusLabel.setText("Error de base de datos al obtener departamento");
                statusLabel.setTextFill(Color.RED);
                return "Error al obtener departamento";
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error("ID de departamento inválido: {}", e.getMessage(), e);
            statusLabel.setText("ID de departamento inválido");
            statusLabel.setTextFill(Color.RED);
            return "ID inválido";
        } catch (Exception e) {
            LOGGER.error("Error inesperado al obtener departamento: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al obtener departamento");
            statusLabel.setTextFill(Color.RED);
            return "Error inesperado de sistema";
        }
    }

    private void openRegisterProjectWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterProject.fxml"));
            Parent root = loader.load();

            GUI_RegisterProjectController registerController = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Registrar Proyecto");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.error("Error al abrir la ventana de registro de proyectos: {}", e.getMessage(), e);
            statusLabel.setText("Error al cargar la interfaz de registro de proyectos.");
            statusLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al abrir la ventana de registro de proyectos: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al abrir la ventana de registro de proyectos.");
            statusLabel.setTextFill(Color.RED);
        }
    }

    public void loadProjectData() {
        ObservableList<ProjectDTO> projectList = FXCollections.observableArrayList();

        if (projectService == null) {
            statusLabel.setText("No se pudo inicializar el servicio de proyectos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("El servicio de proyectos no está disponible (projectService es null).");
            tableView.setItems(projectList);
            updateProjectCounts(projectList);
            return;
        }

        try {
            List<ProjectDTO> projects = projectService.getAllProjects();
            projectList.addAll(projects);
            statusLabel.setText("");
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error de base de datos al cargar los proyectos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de base de datos al cargar los proyectos: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al cargar los proyectos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al cargar los proyectos: {}", e.getMessage(), e);
        }

        tableView.setItems(projectList);
        updateProjectCounts(projectList);
    }

    private void searchProject() {
        String searchQuery = searchField.getText().trim();
        if (searchQuery.isEmpty()) {
            loadProjectData();
            return;
        }

        ObservableList<ProjectDTO> filteredList = FXCollections.observableArrayList();

        if (projectService == null) {
            statusLabel.setText("No se pudo inicializar el servicio de proyectos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("El servicio de proyectos no está disponible (projectService es null).");
            tableView.setItems(filteredList);
            updateProjectCounts(filteredList);
            return;
        }

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
                statusLabel.setTextFill(Color.RED);
            } else {
                statusLabel.setText("");
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al buscar proyectos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al buscar proyectos: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al buscar proyectos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al buscar proyectos: {}", e.getMessage(), e);
        }

        tableView.setItems(filteredList);
        updateProjectCounts(filteredList);
    }

    private void updateProjectCounts(ObservableList<ProjectDTO> list) {
        int total = list.size();
        projectCountsLabel.setText("Totales: " + total);
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

        managementColumn.setCellFactory(cellFactory);
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
        } catch (IOException e) {
            statusLabel.setText("Error al cargar la interfaz de gestión de proyecto.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al cargar el archivo FXML: {}", e.getMessage(), e);
        } catch (IllegalStateException e) {
            statusLabel.setText("Error de configuración en la ventana de gestión.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error de estado al configurar controlador: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error al abrir la ventana de gestión de proyecto.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al gestionar proyecto: {}", e.getMessage(), e);
        }
    }
}