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
import logic.DTO.ProjectDTO;
import logic.DTO.ProjectRequestDTO;
import logic.DTO.ProjectStatus;
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
    private TableColumn<ProjectRequestDTO, String> columnTuiton;

    @FXML
    private TableColumn<ProjectRequestDTO, String> columnProjectName;

    @FXML
    private TableColumn<ProjectRequestDTO, String> columnDescription;

    @FXML
    private TableColumn<ProjectRequestDTO, String> columnOrganizationId;

    @FXML
    private TableColumn<ProjectRequestDTO, String> columnRepresentativeId;

    @FXML
    private TableColumn<ProjectRequestDTO, String> columnStatus;

    @FXML
    private TableColumn<ProjectRequestDTO, Void> columnDetails;

    @FXML
    private TableColumn<ProjectRequestDTO, Void> columnApprove;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private Button searchButton;

    @FXML
    private Button clearButton;

    @FXML
    private Button buttonRegisterRequest;

    @FXML
    private Button buttonRefreshList;

    @FXML
    private Label statusLabel;

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

        addDetailsButtonToTable();
        addApproveButtonToTable();
        addManageButtonToTable();

        ObservableList<String> statusOptions = FXCollections.observableArrayList(
                "Todos", "pendiente", "aprobada", "rechazada"
        );
        filterComboBox.setItems(statusOptions);
        filterComboBox.setValue("Todos");

        loadRequestData();

        searchButton.setOnAction(event -> searchRequest());
        clearButton.setOnAction(event -> {
            searchField.clear();
            filterComboBox.setValue("Todos");
            loadRequestData();
        });
        buttonRegisterRequest.setOnAction(event -> openRegisterRequestWindow());
        buttonRefreshList.setOnAction(event -> loadRequestData());

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
            buttonRegisterRequest.setVisible(true);
        } else if (userRole == Role.ACADEMICO) {
            buttonRegisterRequest.setVisible(true);
        } else {
            buttonRegisterRequest.setVisible(false);
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
            List<ProjectRequestDTO> requests = projectRequestDAO.getAllProjectRequests();

            String selectedStatus = filterComboBox.getValue();
            if (!"Todos".equals(selectedStatus)) {
                requests.removeIf(request -> !request.getStatus().name().equals(selectedStatus));
            }

            requestList.addAll(requests);
            statusLabel.setText("");
        } catch (SQLException e) {
            statusLabel.setText("Error al cargar los datos de las solicitudes.");
            logger.error("Error al cargar los datos de las solicitudes: {}", e.getMessage(), e);
        }

        tableView.setItems(requestList);
    }

    private void setAllCellValueFactories() {
        columnProjectName.setCellValueFactory(cellData -> {
            try {
                String projectId = cellData.getValue().getProjectId();
                if (projectId != null && !projectId.isEmpty()) {
                    String projectName = projectDAO.getProyectNameById(projectId);
                    return new SimpleStringProperty(projectId != null && !projectId.isEmpty() ? projectName : "No encontrado");
                }
                return new SimpleStringProperty("N/A");
            } catch (Exception e) {
                logger.error("Error al obtener nombre del proyecto: {}", e.getMessage(), e);
                return new SimpleStringProperty("Error");
            }
        });

        columnTuiton.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTuiton()));

        columnDescription.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDescription()));

        columnOrganizationId.setCellValueFactory(cellData -> {
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

        columnRepresentativeId.setCellValueFactory(cellData -> {
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

        columnStatus.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus().name()));
    }

    private void searchRequest() {
        String searchQuery = searchField.getText().trim();
        if (searchQuery.isEmpty()) {
            loadRequestData();
            return;
        }

        ObservableList<ProjectRequestDTO> filteredList = FXCollections.observableArrayList();

        try {
            List<ProjectRequestDTO> allRequests = projectRequestDAO.getAllProjectRequests();

            for (ProjectRequestDTO request : allRequests) {
                if (request.getTuiton().toLowerCase().contains(searchQuery.toLowerCase())) {
                    filteredList.add(request);
                    continue;
                }

                try {
                    String projectName = projectDAO.getProyectNameById(request.getProjectId());
                    if (projectName.toLowerCase().contains(searchQuery.toLowerCase())) {
                        filteredList.add(request);
                    }
                } catch (SQLException e) {
                    logger.error("Error al obtener nombre del proyecto: {}", e.getMessage(), e);
                }
            }

            String selectedStatus = filterComboBox.getValue();
            if (!"Todos".equals(selectedStatus)) {
                filteredList.removeIf(request -> !request.getStatus().name().equals(selectedStatus));
            }

            if (filteredList.isEmpty()) {
                statusLabel.setText("No se encontraron solicitudes para la búsqueda: " + searchQuery);
            } else {
                statusLabel.setText("");
            }
        } catch (SQLException e) {
            statusLabel.setText("Error al buscar solicitudes.");
            logger.error("Error al buscar solicitudes: {}", e.getMessage(), e);
        }

        tableView.setItems(filteredList);
    }

    private void addDetailsButtonToTable() {
        Callback<TableColumn<ProjectRequestDTO, Void>, TableCell<ProjectRequestDTO, Void>> cellFactory = param -> new TableCell<>() {
            private final Button detailsButton = new Button("Ver detalles");

            {
                detailsButton.setOnAction(event -> {
                    ProjectRequestDTO request = getTableView().getItems().get(getIndex());
                    openDetailsWindow(request);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(detailsButton);
                }
            }
        };

        columnDetails.setCellFactory(cellFactory);
    }

    private void addApproveButtonToTable() {
        Callback<TableColumn<ProjectRequestDTO, Void>, TableCell<ProjectRequestDTO, Void>> cellFactory = param -> new TableCell<>() {
            private final Button approveButton = new Button("Aprobar/Rechazar");

            {
                approveButton.setOnAction(event -> {
                    ProjectRequestDTO request = getTableView().getItems().get(getIndex());
                    openApprovalWindow(request);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    if (getTableView().getItems().get(getIndex()).getStatus() == ProjectStatus.pendiente) {
                        setGraphic(approveButton);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        };

        columnApprove.setCellFactory(cellFactory);
    }

    private void addManageButtonToTable() {
        TableColumn<ProjectRequestDTO, Void> columnManage = new TableColumn<>("Gestionar");

        Callback<TableColumn<ProjectRequestDTO, Void>, TableCell<ProjectRequestDTO, Void>> cellFactory = param -> new TableCell<>() {
            private final Button manageButton = new Button("Gestionar");

            {
                manageButton.setOnAction(event -> {
                    ProjectRequestDTO request = getTableView().getItems().get(getIndex());
                    openManageRequestWindow(request);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(manageButton);
                }
            }
        };

        columnManage.setCellFactory(cellFactory);
        tableView.getColumns().add(columnManage);
    }

    private void openManageRequestWindow(ProjectRequestDTO request) {
        try {
            GUI_ManageProjectRequest.setProjectRequest(request);

            Stage stage = new Stage();
            GUI_ManageProjectRequest manageWindow = new GUI_ManageProjectRequest();
            manageWindow.start(stage);
        } catch (RuntimeException e) {
            logger.error("Error de ejecución al abrir la ventana de gestión: {}", e.getMessage(), e);
            statusLabel.setText("Error de ejecución al abrir la ventana de gestión");
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de gestión: {}", e.getMessage(), e);
            statusLabel.setText("Error al abrir la ventana de gestión");
        }
    }

    private void openDetailsWindow(ProjectRequestDTO request) {
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Detalles de Solicitud");
            alert.setHeaderText("Solicitud #" + request.getRequestId());

            String content = "Matrícula: " + request.getTuiton() + "\n" +
                    "Nombre del proyecto: " + request.getProjectId() + "\n" +
                    "Descripción: " + request.getDescription() + "\n" +
                    "Estado: " + (request.getStatus() != null ? request.getStatus().name() : "");

            alert.setContentText(content);
            alert.showAndWait();
        } catch (RuntimeException e) {
            logger.error("Error al mostrar detalles: {}", e.getMessage(), e);
            statusLabel.setText("Error al mostrar detalles");
        } catch (Exception e) {
            logger.error("Error al mostrar detalles: {}", e.getMessage(), e);
            statusLabel.setText("Error al mostrar detalles");
        }
    }

    private void openApprovalWindow(ProjectRequestDTO request) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Aprobar/Rechazar Solicitud");
            alert.setHeaderText("Solicitud #" + request.getRequestId() + ": " + request.getProjectId());
            alert.setContentText("¿Desea aprobar o rechazar esta solicitud?");

            ButtonType buttonTypeApprove = new ButtonType("Aprobar");
            ButtonType buttonTypeReject = new ButtonType("Rechazar");
            ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeApprove, buttonTypeReject, buttonTypeCancel);

            alert.showAndWait().ifPresent(buttonType -> {
                try {
                    if (buttonType == buttonTypeApprove) {
                        request.setStatus(ProjectStatus.aprobada);
                        updateRequestStatus(request);
                    } else if (buttonType == buttonTypeReject) {
                        request.setStatus(ProjectStatus.rechazada);
                        updateRequestStatus(request);
                    }
                } catch (RuntimeException e) {
                    logger.error("Error al actualizar estado: {}", e.getMessage(), e);
                    statusLabel.setText("Error al actualizar estado");
                } catch (Exception e) {
                    logger.error("Error al actualizar estado: {}", e.getMessage(), e);
                    statusLabel.setText("Error al actualizar estado");
                }
            });
        } catch (RuntimeException e) {
            logger.error("Error al mostrar ventana de aprobación: {}", e.getMessage(), e);
            statusLabel.setText("Error al mostrar ventana de aprobación");
        } catch (Exception e) {
            logger.error("Error al mostrar ventana de aprobación: {}", e.getMessage(), e);
            statusLabel.setText("Error al mostrar ventana de aprobación");
        }
    }

    private void updateRequestStatus(ProjectRequestDTO request) {
        try {
            boolean result = projectRequestDAO.updateProjectRequest(request);
            if (result) {
                statusLabel.setText("Estado de la solicitud actualizado correctamente");
                loadRequestData();
            } else {
                statusLabel.setText("No se pudo actualizar el estado de la solicitud");
            }
        } catch (SQLException e) {
            logger.error("Error al actualizar estado en la base de datos: {}", e.getMessage(), e);
            statusLabel.setText("Error al actualizar en la base de datos");
        }
    }
}