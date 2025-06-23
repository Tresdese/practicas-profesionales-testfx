package gui;

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
import logic.services.LinkedOrganizationService;
import logic.services.ServiceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GUI_CheckListLinkedOrganizationController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_CheckListLinkedOrganizationController.class);

    @FXML
    private TableView<LinkedOrganizationDTO> tableView;

    @FXML
    private TableColumn<LinkedOrganizationDTO, String> organizationNameColumn;

    @FXML
    private TableColumn<LinkedOrganizationDTO, String> organizationAddressColumn;

    @FXML
    private TableColumn<LinkedOrganizationDTO, Void> managementColumn;

    @FXML
    private ChoiceBox<String> filterChoiceBox;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button registerOrganizationButton, deleteOrganizationButton, deleteDepartmentButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Label organizationCountsLabel;

    private LinkedOrganizationDTO selectedLinkedOrganization;
    private LinkedOrganizationService linkedOrganizationService;

    public void setOrganizationService(LinkedOrganizationService linkedOrganizationService) {
        this.linkedOrganizationService = linkedOrganizationService;
    }

    public void initialize() {
        try {
            ServiceConfig serviceConfig = new ServiceConfig();
            linkedOrganizationService = serviceConfig.getLinkedOrganizationService();
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Error de interrupcion de conexión con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de interrupcion de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S02")) {
                statusLabel.setText("Tabla de organizaciones no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla de organizaciones no encontrada: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S22")) {
                statusLabel.setText("Columna no encontrada en la tabla de organizaciones.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada en la tabla de organizaciones: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error de base de datos al inicializar el servicio de organizaciones.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de base de datos al inicializar el servicio de organizaciones: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al inicializar el servicio de organizaciones.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al inicializar el servicio de organizaciones: {}", e.getMessage(), e);
        }

        filterChoiceBox.getItems().addAll("Todos", "Activos", "Inactivos");
        filterChoiceBox.setValue("Todos");
        filterChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> loadOrganizationData());

        setColumns();
        addManagementButtonToTable();

        loadOrganizationData();
        updateOrganizationCounts();

        setButtons();

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedLinkedOrganization = newValue;
            deleteOrganizationButton.setDisable(selectedLinkedOrganization == null);
            deleteDepartmentButton.setDisable(selectedLinkedOrganization == null);
            tableView.refresh();
        });
    }

    private void setColumns() {
        organizationNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        organizationAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
    }

    private void setButtons() {
        searchButton.setOnAction(event -> searchOrganization());
        registerOrganizationButton.setOnAction(event -> openRegisterOrganizationWindow());
        deleteOrganizationButton.setOnAction(event -> handleDeleteOrganization());
        deleteOrganizationButton.setDisable(true);
        deleteDepartmentButton.setOnAction(event -> handleDeleteDepartment());
    }

    private void openRegisterOrganizationWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterLinkedOrganization.fxml"));
            Parent root = loader.load();

            GUI_RegisterLinkedOrganizationController registerController = loader.getController();
            registerController.setParentController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.error("No se pudo leer el archivo de configuracion de la base de datos: {}", e.getMessage(), e);
            statusLabel.setText("No se pudo leer el archivo de configuracion de la base de datos.");
            statusLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al abrir la ventana de registro de organización: {}", e.getMessage(), e);
            statusLabel.setText("Ocurrió un error inesperado. Intente más tarde.");
            statusLabel.setTextFill(Color.RED);
        }
    }

    public void loadOrganizationData() {
        ObservableList<LinkedOrganizationDTO> organizationList = FXCollections.observableArrayList();

        try {
            List<LinkedOrganizationDTO> organizations = linkedOrganizationService.getAllLinkedOrganizations();
            String filter = filterChoiceBox != null ? filterChoiceBox.getValue() : "Todos";
            for (LinkedOrganizationDTO organization : organizations) {
                if (filterOrganizationByChoice(organization, filter)) {
                    organizationList.add(organization);
                }
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Error de interrupción de conexión con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de interrupción de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S02")) {
                statusLabel.setText("Tabla de organizaciones no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla de organizaciones no encontrada: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S22")) {
                statusLabel.setText("Columna no encontrada en la tabla de organizaciones.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada en la tabla de organizaciones: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al cargar las organizaciones.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al cargar las organizaciones: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("No se pudo leer el archivo de configuracion de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("No se pudo leer el archivo de configuracion de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Ocurrió un error inesperado al cargar las organizaciones.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al cargar las organizaciones: {}", e.getMessage(), e);
        }

        tableView.setItems(organizationList);
        updateOrganizationCounts();
    }

    private boolean filterOrganizationByChoice(LinkedOrganizationDTO organization, String filter) {
        return switch (filter) {
            case "Todos" -> true;
            case "Activos" -> organization.getStatus() == 1;
            case "Inactivos" -> organization.getStatus() == 0;
            case null, default -> false;
        };
    }

    private void searchOrganization() {
        String searchQuery = searchField.getText().trim();
        if (searchQuery.isEmpty()) {
            loadOrganizationData();
            return;
        }

        ObservableList<LinkedOrganizationDTO> filteredList = FXCollections.observableArrayList();

        try {
            LinkedOrganizationDTO organization = linkedOrganizationService.searchLinkedOrganizationById(searchQuery);
            if (organization != null) {
                filteredList.add(organization);
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Error de interrupción de conexión con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de interrupción de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S02")) {
                statusLabel.setText("Tabla de organizaciones no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla de organizaciones no encontrada: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S22")) {
                statusLabel.setText("Columna no encontrada en la tabla de organizaciones.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada en la tabla de organizaciones: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al buscar la organización.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al buscar la organización: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            statusLabel.setText("Ocurrió un error inesperado al buscar la organización.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al buscar la organización: {}", e.getMessage(), e);
        }

        tableView.setItems(filteredList);
        updateOrganizationCounts();
    }

    private void updateOrganizationCounts() {
        try {
            List<LinkedOrganizationDTO> organizations = linkedOrganizationService.getAllLinkedOrganizations();
            int total = 0;
            int active = 0;
            int inactive = 0;
            for (LinkedOrganizationDTO organization : organizations) {
                if (organization.getIdOrganization() != null && !organization.getIdOrganization().isEmpty()) {
                    total++;
                    if (organization.getStatus() == 1) {
                        active++;
                    } else {
                        inactive++;
                    }
                }
            }
            organizationCountsLabel.setText("Totales: " + total + " | Activos: " + active + " | Inactivos: " + inactive);
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S02")) {
                statusLabel.setText("Tabla de organizaciones no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla de organizaciones no encontrada: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S22")) {
                statusLabel.setText("Columna no encontrada en la tabla de organizaciones.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada en la tabla de organizaciones: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al inicializar el servicio de estudiantes.");
                LOGGER.error("Error al inicializar el servicio de estudiantes: {}", e.getMessage(), e);
                statusLabel.setTextFill(Color.RED);
            }
        } catch (IOException e) {
            organizationCountsLabel.setText("No se pudo leer el archivo de configuracion de la base de datos.");
            LOGGER.error("No se pudo leer el archivo de configuracion de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            organizationCountsLabel.setText("Ocurrió un error inesperado al contar estudiantes.");
            LOGGER.error("Error inesperado al contar estudiantes: {}", e.getMessage(), e);
        }
    }

    private void addManagementButtonToTable() {
        Callback<TableColumn<LinkedOrganizationDTO, Void>, TableCell<LinkedOrganizationDTO, Void>> cellFactory = param -> new TableCell<>() {
            private final Button manageButton = new Button("Gestionar Organización");

            {
                manageButton.setOnAction(event -> {
                    LinkedOrganizationDTO organization = getTableView().getItems().get(getIndex());
                    openManageOrganizationWindow(organization);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || selectedLinkedOrganization == null || getTableView().getItems().get(getIndex()) != selectedLinkedOrganization) {
                    setGraphic(null);
                } else {
                    setGraphic(manageButton);
                }
            }
        };

        managementColumn.setCellFactory(cellFactory);
    }

    private void openManageOrganizationWindow(LinkedOrganizationDTO organization) {
        try {
            GUI_ManageLinkedOrganization.setLinkedOrganization(organization);
            GUI_ManageLinkedOrganization manageOrganizationApp = new GUI_ManageLinkedOrganization();
            Stage stage = new Stage();
            manageOrganizationApp.start(stage);
        } catch (IllegalStateException e) {
            LOGGER.error("Error al abrir la ventana de gestión de organización: {}", e.getMessage(), e);
            statusLabel.setText("Error al abrir la ventana de gestión de organización.");
            statusLabel.setTextFill(Color.RED);
        } catch (RuntimeException e) {
            LOGGER.error("Error de tiempo de ejecucion al abrir la ventana de gestión de organización: {}", e.getMessage(), e);
            statusLabel.setText("Error de tiempo de ejecución al abrir la ventana de gestión de organización.");
            statusLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al abrir la ventana de gestión de organización: {}", e.getMessage(), e);
            statusLabel.setText("Ocurrió un error inesperado al abrir la ventana de gestión de organización.");
            statusLabel.setTextFill(Color.RED);
        }
    }

    private void handleDeleteOrganization() {
        if (selectedLinkedOrganization == null) {
            statusLabel.setText("Debe seleccionar una organización para eliminar");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_ConfirmDialog.fxml"));
            Parent root = loader.load();
            GUI_ConfirmDialogController confirmController = loader.getController();
            confirmController.setInformationMessage("Al borrar una organizacion, se eliminarán los departamentos y representantes asociados a ella.");
            confirmController.setConfirmMessage("¿Está seguro de que desea eliminar la organizacion " + selectedLinkedOrganization.getName() + "?");
            Stage confirmStage = new Stage();
            confirmStage.setTitle("Confirmar Eliminación");
            confirmStage.setScene(new Scene(root));
            confirmStage.showAndWait();
            if (confirmController.isConfirmed()) {
                linkedOrganizationService.updateLinkedOrganizationStatus(selectedLinkedOrganization.getIdOrganization(), 0);
                statusLabel.setText("Organizacion eliminada correctamente.");
                loadOrganizationData();
                updateOrganizationCounts();
            } else {
                statusLabel.setText("Eliminación cancelada.");
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S02")) {
                statusLabel.setText("Tabla de organizaciones no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla de organizaciones no encontrada: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S22")) {
                statusLabel.setText("Columna no encontrada en la tabla de organizaciones.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada en la tabla de organizaciones: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al inicializar el servicio de organización.");
                LOGGER.error("Error al inicializar el servicio de organización: {}", e.getMessage(), e);
                statusLabel.setTextFill(Color.RED);
            }
        } catch (IOException e) {
            statusLabel.setText("No se pudo leer el archivo de configuracion de la base de datos.");
            LOGGER.error("No se pudo leer el archivo de configuracion de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Ocurrió un error inesperado al eliminar la organización.");
            LOGGER.error("Error inesperado al eliminar la organización: {}", e.getMessage(), e);
        }
    }

    private void handleDeleteDepartment() {
        if (selectedLinkedOrganization == null) {
            statusLabel.setText("Debe seleccionar una organización para eliminar un departamento.");
            statusLabel.setTextFill(Color.RED);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_DeleteDepartment.fxml"));
            Parent root = loader.load();
            GUI_DeleteDepartmentController deleteController = loader.getController();
            deleteController.setOrganizationId(selectedLinkedOrganization.getIdOrganization());
            Stage stage = new Stage();
            stage.setTitle("Eliminar Departamento");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            statusLabel.setText("No se pudo leer el archivo de configuracion de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("No se pudo leer el archivo de configuracion de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Ocurrió un error inesperado al abrir la ventana de eliminación de departamento.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al abrir la ventana de eliminación de departamento: {}", e.getMessage(), e);
        }
    }
}