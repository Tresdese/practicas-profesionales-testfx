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

import logic.DAO.LinkedOrganizationDAO;
import logic.DAO.ProjectDAO;
import logic.DAO.ProjectRequestDAO;
import logic.DAO.RepresentativeDAO;
import logic.DTO.ProjectRequestDTO;
import logic.DTO.Role;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

public class GUI_CheckProjectRequestListController {

    private static final Logger logger = LogManager.getLogger(GUI_CheckProjectRequestListController.class);

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
    private Button searchButton;

    @FXML
    private Button clearButton;

    @FXML
    private Button registerRequestButton;

    @FXML
    private Button refreshListButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Label labelRequestCounts;

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
            logger.error("Error al inicializar DAOs: {}", e.getMessage(), e);
            statusLabel.setText("Error interno. Intente más tarde.");
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

        searchButton.setOnAction(event -> searchRequest());
        clearButton.setOnAction(event -> {
            searchField.clear();
            filterComboBox.setValue("Todos");
            loadRequestData();
        });
        registerRequestButton.setOnAction(event -> openRegisterRequestWindow());
        refreshListButton.setOnAction(event -> loadRequestData());

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedRequest = newValue;
            tableView.refresh();
        });
    }

    public void setUserRole(Role role) {
        this.userRole = role;
        applyRolRestrictions();
    }

    private void applyRolRestrictions() {
        if (userRole == Role.COORDINADOR) {
            registerRequestButton.setVisible(false);
        } else if (userRole == Role.ACADEMICO) {
            registerRequestButton.setVisible(false);
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
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de registro de solicitud: {}", e.getMessage(), e);
            statusLabel.setText("Error al abrir la ventana de registro");
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
            statusLabel.setText("Error al cargar los datos de las solicitudes.");
            logger.error("Error al cargar los datos de las solicitudes: {}", e.getMessage(), e);
        }

        tableView.setItems(requestList);
        updateRequestCounts(requestList);
    }

    private void setAllCellValueFactories() {
        projectNameColumn.setCellValueFactory(cellData -> {
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
            try {
                String orgId = cellData.getValue().getOrganizationId();
                if (orgId != null && !orgId.isEmpty()) {
                    String orgName = organizationDAO.getOrganizationNameById(orgId);
                    return new SimpleStringProperty(orgName != null && !orgName.isEmpty() ?
                            orgName : "No encontrado");
                }
                return new SimpleStringProperty("N/A");
            } catch (SQLException e) {
                logger.error("Error al obtener nombre de la organización: {}", e.getMessage(), e);
                return new SimpleStringProperty("Error");
            }
        });

        representativeIdColumn.setCellValueFactory(cellData -> {
            try {
                String repId = cellData.getValue().getRepresentativeId();
                if (repId != null && !repId.isEmpty()) {
                    String repName = representativeDAO.getRepresentativeNameById(repId);
                    return new SimpleStringProperty(repName != null && !repName.isEmpty() ?
                            repName : "No encontrado");
                }
                return new SimpleStringProperty("N/A");
            } catch (SQLException e) {
                logger.error("Error al obtener nombre del representante: {}", e.getMessage(), e);
                return new SimpleStringProperty("Error");
            }
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
                }
            }
        } catch (SQLException e) {
            statusLabel.setText("Error al buscar solicitudes.");
            logger.error("Error al buscar solicitudes: {}", e.getMessage(), e);
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
                    if (request.getStatus().name().equalsIgnoreCase("pendiente")) {
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
            logger.error("Error en ventana de aprobación: {}", e.getMessage(), e);
            statusLabel.setText("Error en ventana de aprobación");
        } catch (Exception e) {
            logger.error("Error al abrir ventana de aprobación: {}", e.getMessage(), e);
            statusLabel.setText("Error al abrir ventana de aprobación");
        }
    }

    private void updateRequestCounts(ObservableList<ProjectRequestDTO> list) {
        int total = list.size();
        labelRequestCounts.setText("Totales: " + total);
    }
}