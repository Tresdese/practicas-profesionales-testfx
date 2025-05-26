package gui;

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

import logic.DTO.ProjectRequestDTO;
import logic.DAO.ProjectRequestDAO;

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
    private TableColumn<ProjectRequestDTO, Integer> columnOrganizationId;

    @FXML
    private TableColumn<ProjectRequestDTO, Integer> columnRepresentativeId;

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

    public void initialize() {
        try {
            this.projectRequestDAO = new ProjectRequestDAO();
        } catch (RuntimeException e) {
            logger.error("Error al inicializar ProjectRequestDAO: {}", e.getMessage(), e);
            statusLabel.setText("Error interno. Intente más tarde.");
            return;
        }

        columnTuiton.setCellValueFactory(new PropertyValueFactory<>("tuiton"));
        columnProjectName.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        columnOrganizationId.setCellValueFactory(new PropertyValueFactory<>("organizationId"));
        columnRepresentativeId.setCellValueFactory(new PropertyValueFactory<>("representativeId"));
        columnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        addDetailsButtonToTable();
        addApproveButtonToTable();
        addManageButtonToTable();

        // Configurar el ComboBox para filtrar por estado
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
                requests.removeIf(request -> !request.getStatus().equals(selectedStatus));
            }

            requestList.addAll(requests);
            statusLabel.setText("");
        } catch (SQLException e) {
            statusLabel.setText("Error al cargar los datos de las solicitudes.");
            logger.error("Error al cargar los datos de las solicitudes: {}", e.getMessage(), e);
        }

        tableView.setItems(requestList);
    }

    private void searchRequest() {
        String searchQuery = searchField.getText().trim();
        if (searchQuery.isEmpty()) {
            loadRequestData();
            return;
        }

        ObservableList<ProjectRequestDTO> filteredList = FXCollections.observableArrayList();

        try {
            List<ProjectRequestDTO> requests = projectRequestDAO.searchProjectRequestByTuiton(searchQuery);

            String selectedStatus = filterComboBox.getValue();
            if (!"Todos".equals(selectedStatus)) {
                requests.removeIf(request -> !request.getStatus().equals(selectedStatus));
            }

            filteredList.addAll(requests);

            if (filteredList.isEmpty()) {
                statusLabel.setText("No se encontraron solicitudes para la matrícula: " + searchQuery);
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
                    // Solo mostrar el botón si la solicitud está pendiente
                    if ("pendiente".equals(getTableView().getItems().get(getIndex()).getStatus())) {
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
            // Configurar la ventana de gestión
            GUI_ManageProjectRequest.setProjectRequest(request);

            // Crear y mostrar la ventana
            Stage stage = new Stage();
            GUI_ManageProjectRequest manageWindow = new GUI_ManageProjectRequest();
            manageWindow.start(stage);
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
                    "Nombre del proyecto: " + request.getProjectName() + "\n" +
                    "Descripción: " + request.getDescription() + "\n" +
                    "Estado: " + request.getStatus();

            alert.setContentText(content);
            alert.showAndWait();
        } catch (Exception e) {
            logger.error("Error al mostrar detalles: {}", e.getMessage(), e);
            statusLabel.setText("Error al mostrar detalles");
        }
    }

    private void openApprovalWindow(ProjectRequestDTO request) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Aprobar/Rechazar Solicitud");
            alert.setHeaderText("Solicitud #" + request.getRequestId() + ": " + request.getProjectName());
            alert.setContentText("¿Desea aprobar o rechazar esta solicitud?");

            ButtonType buttonTypeApprove = new ButtonType("Aprobar");
            ButtonType buttonTypeReject = new ButtonType("Rechazar");
            ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeApprove, buttonTypeReject, buttonTypeCancel);

            alert.showAndWait().ifPresent(buttonType -> {
                try {
                    if (buttonType == buttonTypeApprove) {
                        request.setStatus("aprobada");
                        updateRequestStatus(request);
                    } else if (buttonType == buttonTypeReject) {
                        request.setStatus("rechazada");
                        updateRequestStatus(request);
                    }
                } catch (Exception e) {
                    logger.error("Error al actualizar estado: {}", e.getMessage(), e);
                    statusLabel.setText("Error al actualizar estado");
                }
            });
        } catch (Exception e) {
            logger.error("Error al mostrar ventana de aprobación: {}", e.getMessage(), e);
            statusLabel.setText("Error al mostrar ventana de aprobación");
        }
    }

    private void updateRequestStatus(ProjectRequestDTO request) {
        try {
            boolean result = projectRequestDAO.updateProjectRequestStatus(request.getRequestId(), request.getStatus());
            if (result) {
                statusLabel.setText("Estado de la solicitud actualizado correctamente");
                loadRequestData(); // Recargar la tabla
            } else {
                statusLabel.setText("No se pudo actualizar el estado de la solicitud");
            }
        } catch (SQLException e) {
            logger.error("Error al actualizar estado en la base de datos: {}", e.getMessage(), e);
            statusLabel.setText("Error al actualizar en la base de datos");
        }
    }
}