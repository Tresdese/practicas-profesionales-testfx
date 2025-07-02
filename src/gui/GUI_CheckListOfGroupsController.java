package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import logic.DTO.GroupDTO;
import logic.DAO.GroupDAO;
import logic.DTO.Role;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

public class GUI_CheckListOfGroupsController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_CheckListOfGroupsController.class);

    @FXML
    private TableView<GroupDTO> groupsTableView;

    @FXML
    private TableColumn<GroupDTO, String> NRCColumn;

    @FXML
    private TableColumn<GroupDTO, String> nameColumn;

    @FXML
    private TableColumn<GroupDTO, String> idUserColumn;

    @FXML
    private TableColumn<GroupDTO, String> idPeriodColumn;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button registerGroupButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Label groupCountsLabel;

    private GroupDTO selectedGroup;
    private GroupDAO groupDAO;
    private Role userRole;

    public void initialize() {
        this.groupDAO = new GroupDAO();

        NRCColumn.setCellValueFactory(new PropertyValueFactory<>("NRC"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        idUserColumn.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        idPeriodColumn.setCellValueFactory(new PropertyValueFactory<>("idPeriod"));

        loadGroupData();

        searchButton.setOnAction(event -> searchGroup());
        registerGroupButton.setOnAction(event -> openRegisterGroupWindow());

        groupsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedGroup = newVal;
            groupsTableView.refresh();
        });
    }

    private void openRegisterGroupWindow() {
        try {
            GUI_RegisterGroup registerGroupApp = new GUI_RegisterGroup();
            Stage stage = new Stage();
            registerGroupApp.start(stage);
        } catch (RuntimeException e) {
            statusLabel.setText("Error inesperado en tiempo de ejecución.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("RuntimeException al abrir la ventana de registro de grupo: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Ocurrió un error al abrir la ventana de registro.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Excepción al abrir la ventana de registro de grupo: {}", e.getMessage(), e);
        }
    }

    public void loadGroupData() {
        ObservableList<GroupDTO> groupList = FXCollections.observableArrayList();
        try {
            List<GroupDTO> groups = groupDAO.getAllGroups();
            groupList.addAll(groups);
            statusLabel.setText("");
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if ("08S01".equals(sqlState)) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if ("40S02".equals(sqlState)) {
                statusLabel.setText("Tabla de grupos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla de grupos no encontrada: {}", e.getMessage(), e);
            } else if ("40S22".equals(sqlState)) {
                statusLabel.setText("Columna no encontrada en la tabla de grupos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada en la tabla de grupos: {}", e.getMessage(), e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al cargar los grupos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al cargar los grupos: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al cargar los grupos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al cargar los grupos: {}", e.getMessage(), e);
        }
        groupsTableView.setItems(groupList);
        updateGroupCounts(groupList);
    }

    public void searchGroup() {
        ObservableList<GroupDTO> filteredList = FXCollections.observableArrayList();
        String searchQuery = searchField.getText().trim();

        if (searchQuery.isEmpty()) {
            loadGroupData();
        } else {
            try {
                GroupDTO group = groupDAO.searchGroupById(searchQuery);
                if (group != null && !"N/A".equals(group.getNRC())) {
                    filteredList.add(group);
                }
            } catch (SQLException e) {
                String sqlState = e.getSQLState();
                if ("08001".equals(sqlState)) {
                    statusLabel.setText("Error de conexión con la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                } else if ("08S01".equals(sqlState)) {
                    statusLabel.setText("Conexión interrumpida con la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                } else if ("42000".equals(sqlState)) {
                    statusLabel.setText("Base de datos desconocida.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
                } else if ("28000".equals(sqlState)) {
                    statusLabel.setText("Acceso denegado a la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                } else {
                    statusLabel.setText("Error al buscar el grupo.");
                    statusLabel.setTextFill(Color.RED);
                    LOGGER.error("Error al buscar el grupo: {}", e.getMessage(), e);
                }
            } catch (Exception e) {
                statusLabel.setText("Error inesperado al buscar el grupo.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error inesperado al buscar el grupo: {}", e.getMessage(), e);
            }
            groupsTableView.setItems(filteredList);
            updateGroupCounts(filteredList);
        }
    }

    private void updateGroupCounts(ObservableList<GroupDTO> list) {
        int total = list.size();
        groupCountsLabel.setText("Totales: " + total);
    }

    public void setUserRole(Role userRole) {
        this.userRole = userRole;
        applyRoleRestrictions();
    }

    public void setButtonVisibility(Button btn, boolean visible) {
        if (btn != null) {
            btn.setVisible(visible);
            btn.setManaged(visible);
        }
    }

    public void applyRoleRestrictions() {
        if (userRole == Role.EVALUATOR_ACADEMIC) {
            setButtonVisibility(registerGroupButton, false);
        } else if (userRole == Role.ACADEMIC) {
            setButtonVisibility(registerGroupButton, false);
        } else if (userRole == Role.COORDINATOR) {
            setButtonVisibility(registerGroupButton, true);
        }
    }
}