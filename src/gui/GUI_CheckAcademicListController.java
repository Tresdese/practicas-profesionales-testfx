package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.paint.Color;
import logic.DTO.UserDTO;
import logic.DTO.Role;
import logic.services.ServiceFactory;
import logic.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

public class GUI_CheckAcademicListController {

    private static final Logger logger = LogManager.getLogger(GUI_CheckAcademicListController.class);

    @FXML
    private TableView<UserDTO> tableView;

    @FXML
    private TableColumn<UserDTO, String> staffNumberColumn;

    @FXML
    private TableColumn<UserDTO, String> namesColumn;

    @FXML
    private TableColumn<UserDTO, String> surnamesColumn;

    @FXML
    private TableColumn<UserDTO, String> userNamesColumn;

    @FXML
    private TableColumn<UserDTO, Role> roleColumn;

    @FXML
    private TableColumn<UserDTO, Void> managementColumn;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button registerAcademicButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Label academicCountsLabel;

    private UserDTO selectedAcademic;
    private UserService userService;

    public void initialize() {
        try {
            this.userService = ServiceFactory.getUserService();
        } catch (RuntimeException e) {
            logger.error("Error de tiempo de ejecucion al inicializar UserService: {}", e.getMessage(), e);
            statusLabel.setText("Error interno de tiempo de ejecucion. Intente más tarde.");
            statusLabel.setTextFill(Color.RED);
            return;
        } catch (Exception e) {
            logger.error("Error inesperado al inicializar UserService: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al al inicializar UserService.");
            statusLabel.setTextFill(Color.RED);
            return;
        }

        staffNumberColumn.setCellValueFactory(new PropertyValueFactory<>("staffNumber"));
        namesColumn.setCellValueFactory(new PropertyValueFactory<>("names"));
        surnamesColumn.setCellValueFactory(new PropertyValueFactory<>("surnames"));
        userNamesColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        addManagementButtonToTable();

        loadAcademicData();

        searchButton.setOnAction(event -> searchAcademic());
        registerAcademicButton.setOnAction(event -> openRegisterAcademicWindow());

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedAcademic = newValue;
            tableView.refresh();
        });
    }

    private void openRegisterAcademicWindow() {
        try {
            GUI_RegisterAcademic registerAcademicApp = new GUI_RegisterAcademic();
            registerAcademicApp.start(new Stage());
        } catch (IllegalStateException e) {
            statusLabel.setText("Error de estado al abrir la ventana de registrar Académico.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error de estado al abrir la ventana de registro: {}", e.getMessage(), e);
        } catch (RuntimeException e) {
            statusLabel.setText("Error de tiempo de ejecucion al abrir la ventana de regisrar Academico.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error de tiempo de ejecucion al abrir la ventana de registro: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al abrir la ventana de registro.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error inesperado al abrir la ventana de registro: {}", e.getMessage(), e);
        }
    }

    public void loadAcademicData() {
        ObservableList<UserDTO> userList = FXCollections.observableArrayList();
        try {
            List<UserDTO> users = userService.getAllUsers();
            userList.addAll(users);
            statusLabel.setText("");
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al cargar los académicos de la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error al cargar los académicos de la base de datos: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al cargar los académicos.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error inesperado al cargar los académicos: {}", e.getMessage(), e);
        }
        tableView.setItems(userList);
        updateAcademicCounts();
    }

    public void searchAcademic() {
        String searchQuery = searchField.getText().trim();
        if (searchQuery.isEmpty()) {
            loadAcademicData();
            return;
        }

        ObservableList<UserDTO> filteredList = FXCollections.observableArrayList();

        try {
            UserDTO user = userService.searchUserById(searchQuery);
            if (user != null) {
                filteredList.add(user);
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al buscar académico de la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error al buscar académico de la base de datos: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al buscar académico.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error inesperado al buscar académico: {}", e.getMessage(), e);
        }

        tableView.setItems(filteredList);
        updateAcademicCounts(filteredList);
    }

    private void addManagementButtonToTable() {
        Callback<TableColumn<UserDTO, Void>, TableCell<UserDTO, Void>> cellFactory = param -> new TableCell<>() {
            private final Button manageButton = new Button("Gestionar Académico");

            {
                manageButton.setOnAction(event -> {
                    UserDTO academic = getTableView().getItems().get(getIndex());
                    openManageAcademicWindow(academic);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || selectedAcademic == null || getTableView().getItems().get(getIndex()) != selectedAcademic) {
                    setGraphic(null);
                } else {
                    setGraphic(manageButton);
                }
            }
        };

        managementColumn.setCellFactory(cellFactory);
    }

    private void openManageAcademicWindow(UserDTO academic) {
        try {
            GUI_ManageAcademic.setAcademic(academic);
            GUI_ManageAcademic manageAcademicApp = new GUI_ManageAcademic();
            Stage stage = new Stage();
            manageAcademicApp.start(stage);
        } catch (IllegalStateException e) {
            logger.error("Error de estado al abrir la ventana de gestión de académico: {}", e.getMessage(), e);
            statusLabel.setText("Error de estado al abrir la ventana de gestión de académico.");
            statusLabel.setTextFill(Color.RED);
        } catch (RuntimeException e) {
            logger.error("Error de tiempo de ejecucion al abrir la ventana de gestión de académico: {}", e.getMessage(), e);
            statusLabel.setText("Error de tiempo de ejecucion al abrir la ventana de gestión de académico.");
            statusLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            logger.error("Error inesperado al abrir la ventana de gestión de académico: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al abrir la ventana de gestión de académico.");
            statusLabel.setTextFill(Color.RED);
        }
    }

    private void updateAcademicCounts() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            int total = users.size();
            academicCountsLabel.setText("Totales: " + total);
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                academicCountsLabel.setText("Error de conexión con la base de datos.");
                academicCountsLabel.setTextFill(Color.RED);
                logger.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                academicCountsLabel.setText("Conexión interrumpida con la base de datos.");
                academicCountsLabel.setTextFill(Color.RED);
                logger.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                academicCountsLabel.setText("Base de datos desconocida.");
                academicCountsLabel.setTextFill(Color.RED);
                logger.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                academicCountsLabel.setText("Acceso denegado a la base de datos.");
                academicCountsLabel.setTextFill(Color.RED);
                logger.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                academicCountsLabel.setText("Error al contar los académicos de la base de datos.");
                academicCountsLabel.setTextFill(Color.RED);
                logger.error("Error al contar los académicos de la base de datos: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            academicCountsLabel.setText("Error inesperado al contar los académicos.");
            academicCountsLabel.setTextFill(Color.RED);
            logger.error("Error inesperado al contar los académicos: {}", e.getMessage(), e);
        }
    }

    private void updateAcademicCounts(ObservableList<UserDTO> list) {
        int total = list.size();
        academicCountsLabel.setText("Totales: " + total);
    }
}