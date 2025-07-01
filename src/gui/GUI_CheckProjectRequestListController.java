package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.paint.Color;

import logic.DAO.LinkedOrganizationDAO;
import logic.DAO.ProjectDAO;
import logic.DAO.ProjectRequestDAO;
import logic.DAO.RepresentativeDAO;
import logic.DTO.ProjectRequestDTO;
import logic.DTO.Role;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GUI_CheckProjectRequestListController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_CheckProjectRequestListController.class);

    @FXML
    private TableView<ProjectRequestDTO> tableView;

    @FXML
    private TableColumn<ProjectRequestDTO, String> tuitonColumn;

    @FXML
    private TableColumn<ProjectRequestDTO, String> projectNameColumn;

    @FXML
    private TableColumn<ProjectRequestDTO, String> descriptionColumn;

    @FXML
    private TableColumn<ProjectRequestDTO, String> organizationIdColumn;

    @FXML
    private TableColumn<ProjectRequestDTO, String> representativeIdColumn;

    @FXML
    private TableColumn<ProjectRequestDTO, String> statusColumn;

    @FXML
    private TableColumn<ProjectRequestDTO, Void> approveColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private Button searchButton, clearButton, registerRequestButton, manageRequestButton, refreshListButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Label requestCountsLabel;

    private ProjectRequestDTO selectedRequest;
    private ProjectRequestDAO projectRequestDAO;
    private ProjectDAO projectDAO;
    private LinkedOrganizationDAO organizationDAO;
    private RepresentativeDAO representativeDAO;
    private Role userRole;

    public void initialize() {
        try {
            this.projectRequestDAO = new ProjectRequestDAO();
            this.projectDAO = new ProjectDAO();
            this.organizationDAO = new LinkedOrganizationDAO();
            this.representativeDAO = new RepresentativeDAO();
        } catch (RuntimeException e) {
            LOGGER.error("Error al inicializar DAOs: {}", e.getMessage(), e);
            statusLabel.setText("Error interno. Intente más tarde.");
            statusLabel.setTextFill(Color.RED);
            return;
        } catch (Exception e) {
            LOGGER.error("Error inesperado al inicializar DAOs: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al conectar con la base de datos.");
            statusLabel.setTextFill(Color.RED);
            return;
        }

        setAllCellValueFactories();

        addApproveButtonToTable();

        ObservableList<String> statusOptions = FXCollections.observableArrayList(
                "Todos", "pendiente", "aprobada", "rechazada"
        );
        filterComboBox.setItems(statusOptions);
        filterComboBox.setValue("Todos");

        filterComboBox.setOnAction(event -> loadRequestData());

        loadRequestData();
        setButtons();

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedRequest = newValue;
            manageRequestButton.setDisable(selectedRequest == null);
            tableView.refresh();
        });
    }

    private void setButtons() {
        searchButton.setOnAction(event -> searchRequest());
        clearButton.setOnAction(event -> {
            searchField.clear();
            filterComboBox.setValue("Todos");
            loadRequestData();
        });
        registerRequestButton.setOnAction(event -> openRegisterRequestWindow());
        refreshListButton.setOnAction(event -> loadRequestData());
        manageRequestButton.setDisable(true);
        manageRequestButton.setOnAction(event -> {
            if (selectedRequest != null) {
                openManageProjectRequestWindow(selectedRequest);
            } else {
                statusLabel.setText("Seleccione una solicitud para gestionar.");
                statusLabel.setTextFill(Color.RED);
            }
        });
    }

    public void setUserRole(Role role) {
        this.userRole = role;
        applyRolRestrictions();
    }

    private void applyRolRestrictions() {
        if (userRole == Role.COORDINATOR) {
            registerRequestButton.setVisible(true);
        } else if (userRole == Role.ACADEMIC) {
            registerRequestButton.setVisible(true);
        } else {
            registerRequestButton.setVisible(false);
        }
    }

    private void openRegisterRequestWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterProjectRequest.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        }  catch (IOException e) {
            LOGGER.error("Error al leer fxml para cargar la ventana de registro de solicitud: {}", e.getMessage(), e);
            statusLabel.setText("Error al leer fxml para cargar la ventana de registro");
            statusLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al abrir la ventana de registro de solicitud: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al abrir la ventana de registro");
            statusLabel.setTextFill(Color.RED);
        }
    }

    public void loadRequestData() {
        ObservableList<ProjectRequestDTO> requestList = FXCollections.observableArrayList();

        try {
            List<ProjectRequestDTO> requests;
            String selectedStatus = filterComboBox.getValue();

            if ("Todos".equalsIgnoreCase(selectedStatus)) {
                requests = projectRequestDAO.getAllProjectRequests();
            } else {
                requests = projectRequestDAO.getProjectRequestsByStatus(selectedStatus);
            }

            requestList.addAll(requests);
            statusLabel.setText("");
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error de base de datos al cargar solicitudes.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de base de datos al cargar solicitudes: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error al cargar solicitudes.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al cargar solicitudes: {}", e.getMessage(), e);
        }

        tableView.setItems(requestList);
        updateRequestCounts(requestList);
    }

    private void setAllCellValueFactories() {
        projectNameColumn.setCellValueFactory(cellData -> {
            SimpleStringProperty projectNameProperty = new SimpleStringProperty();
            String projectName = cellData.getValue().getProjectName();
            return new SimpleStringProperty(
                    (projectName != null && !projectName.isEmpty()) ? projectName : "N/A"
            );
        });

        tuitonColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTuition()));

        descriptionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDescription()));

        organizationIdColumn.setCellValueFactory(cellData -> {
            SimpleStringProperty projectNameProperty = new SimpleStringProperty();
            try {
                String orgId = cellData.getValue().getOrganizationId();
                if (orgId != null && !orgId.isEmpty()) {
                    String organizationName = organizationDAO.getOrganizationNameById(orgId);
                    return new SimpleStringProperty(organizationName != null && !organizationName.isEmpty() ?
                            organizationName : "No encontrado");
                } else {
                    return new SimpleStringProperty("N/A");
                }
            } catch (SQLException e) {
                String sqlState = e.getSQLState();
                if (sqlState != null && sqlState.equals("08001")) {
                    LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                    statusLabel.setText("Error de conexión con la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    projectNameProperty = new SimpleStringProperty("Error de conexión con la base de datos al obtener nombre de organización.");
                } else if (sqlState != null && sqlState.equals("08S01")) {
                    LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                    statusLabel.setText("Conexión interrumpida con la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    projectNameProperty = new SimpleStringProperty("Error de conexión interrumpida al obtener nombre de organización.");
                } else if (sqlState != null && sqlState.equals("42000")) {
                    LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
                    statusLabel.setText("Base de datos desconocida.");
                    statusLabel.setTextFill(Color.RED);
                    projectNameProperty = new SimpleStringProperty("Error de base de datos desconocida al obtener nombre de organización.");
                } else if (sqlState != null && sqlState.equals("28000")) {
                    LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                    statusLabel.setText("Acceso denegado a la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    projectNameProperty = new SimpleStringProperty("Error de acceso denegado al obtener nombre de organización.");
                } else {
                    LOGGER.error("Error de base de datos al obtener nombre de organización: {}", e.getMessage(), e);
                    statusLabel.setText("Error de base de datos al obtener nombre de organización.");
                    statusLabel.setTextFill(Color.RED);
                    projectNameProperty = new SimpleStringProperty("Error de base de datos al obtener nombre de organización.");
                }
            } catch (Exception e) {
                LOGGER.error("Error inesperado al obtener nombre de organización: {}", e.getMessage(), e);
                statusLabel.setText("Error inesperado al obtener nombre de organización.");
                statusLabel.setTextFill(Color.RED);
                projectNameProperty = new SimpleStringProperty("Error inesperado al obtener nombre de organización.");
            }
            return projectNameProperty;
        });

        representativeIdColumn.setCellValueFactory(cellData -> {
            SimpleStringProperty projectNameProperty = new SimpleStringProperty();
            try {
                String representativeId = cellData.getValue().getRepresentativeId();
                if (representativeId != null && !representativeId.isEmpty()) {
                    String repName = representativeDAO.getRepresentativeNameById(representativeId);
                    return new SimpleStringProperty(repName != null && !repName.isEmpty() ?
                            repName : "No encontrado");
                } else {
                    return new SimpleStringProperty("N/A");
                }
            } catch (SQLException e) {
                String sqlState = e.getSQLState();
                if (sqlState != null && sqlState.equals("08001")) {
                    LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                    statusLabel.setText("Error de conexión con la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    projectNameProperty = new SimpleStringProperty("Error de conexión con la base de datos al obtener nombre de representante.");
                } else if (sqlState != null && sqlState.equals("08S01")) {
                    LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                    statusLabel.setText("Conexión interrumpida con la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    projectNameProperty = new SimpleStringProperty("Error de conexión interrumpida al obtener nombre de representante.");
                } else if (sqlState != null && sqlState.equals("42000")) {
                    LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
                    statusLabel.setText("Base de datos desconocida.");
                    statusLabel.setTextFill(Color.RED);
                    projectNameProperty = new SimpleStringProperty("Error de base de datos desconocida al obtener nombre de representante.");
                } else if (sqlState != null && sqlState.equals("28000")) {
                    LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                    statusLabel.setText("Acceso denegado a la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    projectNameProperty = new SimpleStringProperty("Error de acceso denegado al obtener nombre de representante.");
                } else {
                    LOGGER.error("Error de base de datos al obtener nombre de representante: {}", e.getMessage(), e);
                    statusLabel.setText("Error de base de datos al obtener nombre de representante.");
                    statusLabel.setTextFill(Color.RED);
                    projectNameProperty = new SimpleStringProperty("Error de base de datos al obtener nombre de representante.");
                }
            } catch (Exception e) {
                LOGGER.error("Error inesperado al obtener nombre de representante: {}", e.getMessage(), e);
                statusLabel.setText("Error inesperado al obtener nombre de representante.");
                statusLabel.setTextFill(Color.RED);
                projectNameProperty = new SimpleStringProperty("Error inesperado al obtener nombre de representante.");
            }

            return projectNameProperty;
        });

        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus().name()));
    }

    private void searchRequest() {
        String searchQuery = searchField.getText().trim();
        ObservableList<ProjectRequestDTO> filteredList = FXCollections.observableArrayList();

        try {
            List<ProjectRequestDTO> requests = projectRequestDAO.getAllProjectRequests();
            String selectedStatus = filterComboBox.getValue();

            for (ProjectRequestDTO request : requests) {
                boolean matches = searchQuery.isEmpty() ||
                        request.getTuition().contains(searchQuery) ||
                        request.getProjectName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                        request.getDescription().toLowerCase().contains(searchQuery.toLowerCase());

                boolean statusMatches = "Todos".equalsIgnoreCase(selectedStatus) ||
                        request.getStatus().name().equalsIgnoreCase(selectedStatus);

                if (matches && statusMatches) {
                    filteredList.add(request);
                } else {
                    statusLabel.setText("No se encontraron solicitudes que coincidan con la búsqueda.");
                    statusLabel.setTextFill(Color.RED);
                }
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("40S02")) {
                statusLabel.setText("Tabla no encontrada en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla no encontrada en la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("40S22")) {
                statusLabel.setText("Columna no encontrada en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada en la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error de base de datos al buscar solicitudes.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de base de datos al buscar solicitudes: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al buscar solicitudes.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al buscar solicitudes: {}", e.getMessage(), e);
        }

        tableView.setItems(filteredList);
        updateRequestCounts(filteredList);
    }

    private void addApproveButtonToTable() {
        Callback<TableColumn<ProjectRequestDTO, Void>, TableCell<ProjectRequestDTO, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btnManage = new Button("Gestionar Solicitud");

            {
                btnManage.setOnAction(event -> {
                    ProjectRequestDTO request = getTableView().getItems().get(getIndex());
                    openManageProjectRequestWindow(request);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    ProjectRequestDTO request = getTableView().getItems().get(getIndex());
                    if (request.getStatus().name().equalsIgnoreCase("Pendiente")) {
                        setGraphic(btnManage);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        };

        approveColumn.setCellFactory(cellFactory);
    }

    private void openManageProjectRequestWindow(ProjectRequestDTO request) {
        try {
            gui.GUI_ManageProjectRequest.setProjectRequest(request);
            Stage stage = new Stage();
            new gui.GUI_ManageProjectRequest().start(stage);
        } catch (RuntimeException e) {
            LOGGER.error("Error en ventana de aprobación: {}", e.getMessage(), e);
            statusLabel.setText("Error en ventana de aprobación");
            statusLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            LOGGER.error("Error al abrir ventana de aprobación: {}", e.getMessage(), e);
            statusLabel.setText("Error al abrir ventana de aprobación");
            statusLabel.setTextFill(Color.RED);
        }
    }

    private void updateRequestCounts(ObservableList<ProjectRequestDTO> list) {
        int total = list.size();
        requestCountsLabel.setText("Totales: " + total);
    }
}