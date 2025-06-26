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
import javafx.scene.paint.Color;
import javafx.util.Callback;
import logic.DAO.RepresentativeDAO;
import logic.DTO.DepartmentDTO;
import logic.DTO.LinkedOrganizationDTO;
import logic.DTO.RepresentativeDTO;
import logic.DAO.DepartmentDAO;
import logic.services.ServiceConfig;
import logic.services.RepresentativeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GUI_CheckRepresentativeListController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_CheckRepresentativeListController.class);

    @FXML
    private Button registerRepresentativeButton;

    @FXML
    private TableColumn<RepresentativeDTO, ?> representativeEmailColumn;

    @FXML
    private TableColumn<RepresentativeDTO, ?> representativeNameColumn;

    @FXML
    private TableColumn<RepresentativeDTO, String> representativeDepartmentColumn;

    @FXML
    private TableColumn<RepresentativeDTO, String> representativeOrganizationColumn;

    @FXML
    private TableColumn<RepresentativeDTO, ?> representativeSurnameColumn;

    @FXML
    private TableColumn<RepresentativeDTO, Void> managementColumn;

    @FXML
    private Button searchButton, deleteRepresentativeButton;

    @FXML
    private ChoiceBox<String> filterChoiceBox;

    @FXML
    private TextField searchField;

    @FXML
    private Label statusLabel;

    @FXML
    private Label representativeCountsLabel;

    @FXML
    private TableView<RepresentativeDTO> tableView;

    private RepresentativeDTO selectedRepresentative;
    private RepresentativeService representativeService;
    private DepartmentDAO departmentDAO = new DepartmentDAO();

    public void initialize() {
        try {
            ServiceConfig serviceConfig = new ServiceConfig();
            representativeService = serviceConfig.getRepresentativeService();
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S02")) {
                statusLabel.setText("Tabla no encontrada en la base de datos. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla no encontrada en la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S22")) {
                statusLabel.setText("Columna no encontrada en la base de datos. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada en la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("HY000")) {
                statusLabel.setText("Error general de la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error general de la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error de base de datos al inicializar el servicio de representantes.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de base de datos al inicializar el servicio de representantes: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("No se pudo leer el archivo de configuracion de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("No se pudo leer el archivo de configuracion de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al inicializar el servicio de representantes.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al inicializar el servicio de representantes: {}", e.getMessage(), e);
        }

        filterChoiceBox.getItems().addAll("Todos", "Activos", "Inactivos");
        filterChoiceBox.setValue("Todos");
        filterChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> loadOrganizationData());

        setColumns();
        addManagementButtonToTable();
        loadOrganizationData();
        updateRepresentativeCounts();

        setButtons();

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedRepresentative = (RepresentativeDTO) newValue;
            deleteRepresentativeButton.setDisable(selectedRepresentative == null);
            tableView.refresh();
        });
    }

    private void setColumns() {
        representativeEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        representativeNameColumn.setCellValueFactory(new PropertyValueFactory<>("names"));
        representativeSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("surnames"));

        representativeDepartmentColumn.setCellValueFactory(cellData -> {
            String departmentId = cellData.getValue().getIdDepartment();
            String departmentName = getDepartmentNameById(departmentId);
            return new SimpleStringProperty(departmentName);
        });

        representativeOrganizationColumn.setCellValueFactory(cellData -> {
            String departmentId = cellData.getValue().getIdDepartment();
            String organizationName = getOrganizationNameByDepartmentId(departmentId);
            return new SimpleStringProperty(organizationName);
        });
    }

    private void setButtons() {
        searchButton.setOnAction(event -> searchRepresentative());
        registerRepresentativeButton.setOnAction(event -> openRegisterRepresentativeWindow());
        deleteRepresentativeButton.setOnAction(event -> handleDeleteRepresentative());
    }

    private String getDepartmentNameById(String departmentId) {
        try {
            if (departmentId == null || departmentId.isEmpty()) {
                return "No asignado";
            }
            DepartmentDTO department = departmentDAO.searchDepartmentById(Integer.parseInt(departmentId));
            return (department != null) ? department.getName() : "Departamento no encontrado";
        } catch (SQLException e){
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return "Error de conexión";
            } else if ("08S01".equals(sqlState)) {
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return "Conexión interrumpida";
            } else if ("42S02".equals(sqlState)) {
                LOGGER.error("Tabla no encontrada en la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Tabla no encontrada en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return "Tabla no encontrada";
            } else if ("42S22".equals(sqlState)) {
                LOGGER.error("Columna no encontrada en la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Columna no encontrada en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return "Columna no encontrada";
            } else if ("HY000".equals(sqlState)) {
                LOGGER.error("Error general de la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Error general de la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return "Error general de la base de datos";
            } else if ("42000".equals(sqlState)) {
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                return "Base de datos desconocida";
            } else if ("28000".equals(sqlState)) {
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return "Acceso denegado";
            } else {
                LOGGER.error("Error de base de datos al obtener el departamento: {}", e.getMessage(), e);
                statusLabel.setText("Error al obtener el departamento.");
                statusLabel.setTextFill(Color.RED);
                return "Error al obtener departamento";
            }
        } catch (IOException e) {
            LOGGER.error("Error al leer el archivo de configuración de la base de datos: {}", e.getMessage(), e);
            statusLabel.setText("Error al leer el archivo de configuración de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            return "Error al leer archivo de configuración";
        } catch (Exception e) {
            LOGGER.error("Error inesperado al obtener nombre de departamento: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al obtener el departamento.");
            statusLabel.setTextFill(Color.RED);
            return "Error inesperado";
        }
    }

    private String getOrganizationNameByDepartmentId(String departmentId) {
        try {
            if (departmentId == null || departmentId.isEmpty()) {
                return "No asignado";
            }
            DepartmentDTO department = departmentDAO.searchDepartmentById(Integer.parseInt(departmentId));
            if (department == null) {
                return "Departamento no encontrado";
            }
            ServiceConfig serviceConfig = new ServiceConfig();
            LinkedOrganizationDTO organization = serviceConfig.getLinkedOrganizationService()
                    .searchLinkedOrganizationById(String.valueOf(department.getOrganizationId()));
            return (organization != null) ? organization.getName() : "Organización no encontrada";
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return "Error de conexión";
            } else if ("08S01".equals(sqlState)) {
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return "Conexión interrumpida";
            } else if ("42S02".equals(sqlState)) {
                LOGGER.error("Tabla no encontrada en la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Tabla no encontrada en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return "Tabla no encontrada";
            } else if ("42S22".equals(sqlState)) {
                LOGGER.error("Columna no encontrada en la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Columna no encontrada en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return "Columna no encontrada";
            } else if ("HY000".equals(sqlState)) {
                LOGGER.error("Error general de la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Error general de la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return "Error general de la base de datos";
            } else if ("42000".equals(sqlState)) {
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                return "Base de datos desconocida";
            } else if ("28000".equals(sqlState)) {
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                return "Acceso denegado";
            } else {
                LOGGER.error("Error de base de datos al obtener organización por departamento: {}", e.getMessage(), e);
                statusLabel.setText("Error de base de datos al obtener organización por departamento.");
                statusLabel.setTextFill(Color.RED);
                return "Error de base de datos al obtener organización por departamento";
            }
        } catch (IOException e) {
            LOGGER.error("Error al leer el archivo de configuración de la base de datos: {}", e.getMessage(), e);
            statusLabel.setText("Error al leer el archivo de configuración de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            return "Error al leer archivo de configuración";
        } catch (Exception e) {
            LOGGER.error("Error inesperado al obtener nombre de organización por departamento: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al obtener la organización por departamento.");
            statusLabel.setTextFill(Color.RED);
            return "Error";
        }
    }

    private void openRegisterRepresentativeWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterRepresentative.fxml"));
            Parent root = loader.load();

            GUI_RegisterRepresentativeController registerController = loader.getController();
            registerController.setParentController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            statusLabel.setText("Error al abrir la ventana de registro de representantes.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al abrir la ventana de registro de representantes: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al abrir la ventana de registro de representantes.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al abrir la ventana de registro de representantes: {}", e.getMessage(), e);
        }
    }

    public void loadOrganizationData() {
        ObservableList<RepresentativeDTO> representativeList = FXCollections.observableArrayList();

        try {
            List<RepresentativeDTO> representatives = representativeService.getAllRepresentatives();
            String filter = filterChoiceBox != null ? filterChoiceBox.getValue() : "Todos";
            for (RepresentativeDTO representative : representatives) {
                if (filterRepresentativeByChoice(representative, filter)) {
                    representativeList.add(representative);
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
            } else if (sqlState != null && sqlState.equals("42S02")) {
                statusLabel.setText("Tabla no encontrada en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla no encontrada en la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S22")) {
                statusLabel.setText("Columna no encontrada en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada en la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("HY000")) {
                statusLabel.setText("Error general de la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error general de la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error de base de datos al cargar los representantes.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de base de datos al cargar los representantes: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al cargar los representantes desde el archivo.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al cargar los representantes desde el archivo: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al cargar los representantes.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al cargar los representantes: {}", e.getMessage(), e);
        }

        tableView.setItems(representativeList);
        updateRepresentativeCounts();
    }

    private boolean filterRepresentativeByChoice(RepresentativeDTO representative, String filter) {
        return switch (filter) {
            case "Todos" -> true;
            case "Activos" -> representative.getStatus() == 1;
            case "Inactivos" -> representative.getStatus() == 0;
            default -> false;
        };
    }

    private void searchRepresentative() {
        String searchQuery = searchField.getText().trim();
        if (searchQuery.isEmpty()) {
            loadOrganizationData();
            return;
        }

        ObservableList<RepresentativeDTO> filteredList = FXCollections.observableArrayList();

        try {
            RepresentativeDTO representative = representativeService.searchRepresentativeById(searchQuery);
            if (representative != null) {
                filteredList.add(representative);
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
            } else if (sqlState != null && sqlState.equals("42S02")) {
                statusLabel.setText("Tabla no encontrada en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla no encontrada en la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S22")) {
                statusLabel.setText("Columna no encontrada en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada en la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("HY000")) {
                statusLabel.setText("Error general de la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error general de la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al buscar representante.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al buscar representante: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al cargar el archivo de configuración de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al cargar el archivo de configuración de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al buscar representante.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al buscar representante: {}", e.getMessage(), e);
        }

        tableView.setItems(filteredList);
        updateRepresentativeCounts();
    }

    private void addManagementButtonToTable() {
        Callback<TableColumn<RepresentativeDTO, Void>, TableCell<RepresentativeDTO, Void>> cellFactory = param -> new TableCell<>() {
            private final Button manageButton = new Button("Gestionar Representante");

            {
                manageButton.setOnAction(event -> {
                    if (getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                        return;
                    }
                    RepresentativeDTO representative = getTableView().getItems().get(getIndex());
                    openManageRepresentativeWindow(representative);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || selectedRepresentative == null ||
                        getIndex() < 0 || getIndex() >= getTableView().getItems().size() ||
                        !getTableView().getItems().get(getIndex()).equals(selectedRepresentative)) {
                    setGraphic(null);
                } else {
                    setGraphic(manageButton);
                }
            }
        };

        managementColumn.setCellFactory(cellFactory);
    }

    private void openManageRepresentativeWindow(RepresentativeDTO representative) {
        try {
            GUI_ManageRepresentative.setRepresentative(representative);
            GUI_ManageRepresentative manageRepresentativeApp = new GUI_ManageRepresentative();
            Stage stage = new Stage();
            manageRepresentativeApp.start(stage);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al abrir la ventana de gestión de representante.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al abrir la ventana de gestión de representante: {}", e.getMessage(), e);
        }
    }

    private void updateRepresentativeCounts() {
        try {
            List<RepresentativeDTO> representatives = representativeService.getAllRepresentatives();
            int total = 0;
            int active = 0;
            int inactive = 0;
            for (RepresentativeDTO representative : representatives) {
                total++;
                if (representative.getStatus() == 1) {
                    active++;
                } else {
                    inactive++;
                }
            }
            representativeCountsLabel.setText("Totales: " + total + " | Activos: " + active + " | Inactivos: " + inactive);
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S02")) {
                statusLabel.setText("Tabla no encontrada en la base de datos. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla no encontrada en la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S22")) {
                statusLabel.setText("Columna no encontrada en la base de datos. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada en la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("HY000")) {
                statusLabel.setText("Error general de la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error general de la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
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
            representativeCountsLabel.setText("No se pudo leer el archivo de configuracion de la base de datos.");
            representativeCountsLabel.setTextFill(Color.RED);
            LOGGER.error("No se pudo leer el archivo de configuracion de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            representativeCountsLabel.setText("Error inesperado al contar estudiantes");
            LOGGER.error("Error inesperado al contar estudiantes: {}", e.getMessage(), e);
        }
    }

    private void handleDeleteRepresentative() {
        if (selectedRepresentative == null) {
            statusLabel.setText("Por favor, seleccione un representante para eliminar.");
            statusLabel.setTextFill(Color.RED);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_ConfirmDialog.fxml"));
            Parent root = loader.load();
            GUI_ConfirmDialogController confirmController = loader.getController();
            confirmController.setInformationMessage("Al borrar un representante, no podrá ser asignado a ningun proyecto ni organizacion.");
            confirmController.setConfirmMessage("¿Está seguro de que desea eliminar al representante " + selectedRepresentative.getNames() + "?");
            Stage confirmStage = new Stage();
            confirmStage.setTitle("Confirmar eliminación");
            confirmStage.setScene(new Scene(root));
            confirmStage.showAndWait();
            if (confirmController.isConfirmed()) {
                representativeService.updateRepresentativeStatus(selectedRepresentative.getIdOrganization(), 0);
                statusLabel.setText("Representante eliminado correctamente.");
                loadOrganizationData();
                updateRepresentativeCounts();
            } else {
                statusLabel.setText("Eliminación cancelada.");
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S02")) {
                statusLabel.setText("Tabla no encontrada en la base de datos. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla no encontrada en la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S22")) {
                statusLabel.setText("Columna no encontrada en la base de datos. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada en la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("HY000")) {
                statusLabel.setText("Error general de la base de datos. Por favor, intente más tarde.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error general de la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida. Por favor, verifique la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al eliminar el representante.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al eliminar el representante: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("No se pudo leer el archivo de configuracion de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("No se pudo leer el archivo de configuracion de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al eliminar el representante.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al eliminar el representante: {}", e.getMessage(), e);
        }
    }
}