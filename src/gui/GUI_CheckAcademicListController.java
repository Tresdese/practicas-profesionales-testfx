package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
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
            logger.error("Error al inicializar StudentService: {}", e.getMessage(), e);
            statusLabel.setText("Error interno. Intente más tarde.");
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
        } catch (Exception e) {
            statusLabel.setText("Error al conectar a la base de datos.");
            logger.error("Error al cargar la interfaz GUI_RegisterAcademic.fxml: {}", e.getMessage(), e);
        }
    }

    public void loadAcademicData() {
        ObservableList<UserDTO> userList = FXCollections.observableArrayList();
        try {
            List<UserDTO> users = userService.getAllUsers();
            userList.addAll(users);
            statusLabel.setText("");
        } catch (SQLException e) {
            statusLabel.setText("Error al conectar a la base de datos.");
            logger.error("Error al cargar los datos de los académicos: {}", e.getMessage(), e);
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
            statusLabel.setText("Error al buscar académicos.");
            logger.error("Error al buscar académicos: {}", e.getMessage(), e);
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
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de gestión de académico: {}", e.getMessage(), e);
        }
    }

    private void updateAcademicCounts() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            int total = users.size();
            int activos = 0;
            int inactivos = 0;
            academicCountsLabel.setText("Totales: " + total);
        } catch (SQLException e) {
            academicCountsLabel.setText("Error al contar académicos");
            logger.error("Error al contar académicos: {}", e.getMessage(), e);
        }
    }

    private void updateAcademicCounts(ObservableList<UserDTO> list) {
        int total = list.size();
        int activos = 0;
        int inactivos = 0;
        academicCountsLabel.setText("Totales: " + total);
    }
}