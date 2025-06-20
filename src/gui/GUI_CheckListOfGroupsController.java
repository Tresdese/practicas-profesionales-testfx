package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import logic.DTO.GroupDTO;
import logic.DAO.GroupDAO;
import logic.DTO.Role;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

public class GUI_CheckListOfGroupsController {

    private static final Logger logger = LogManager.getLogger(GUI_CheckListOfGroupsController.class);

    @FXML
    private TableView<GroupDTO> tableViewGroups;

    @FXML
    private TableColumn<GroupDTO, String> columnNRC;

    @FXML
    private TableColumn<GroupDTO, String> columnName;

    @FXML
    private TableColumn<GroupDTO, String> columnIdUser;

    @FXML
    private TableColumn<GroupDTO, String> columnIdPeriod;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button buttonRegisterGroup;

    @FXML
    private Label statusLabel;

    @FXML
    private Label labelGroupCounts;

    private GroupDTO selectedGroup;
    private GroupDAO groupDAO;
    private Role userRole;

    public void initialize() {
        this.groupDAO = new GroupDAO();

        columnNRC.setCellValueFactory(new PropertyValueFactory<>("NRC"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnIdUser.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        columnIdPeriod.setCellValueFactory(new PropertyValueFactory<>("idPeriod"));

        loadGroupData();

        searchButton.setOnAction(event -> searchGroup());
        buttonRegisterGroup.setOnAction(event -> openRegisterGroupWindow());

        tableViewGroups.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedGroup = newVal;
            tableViewGroups.refresh();
        });
    }

    private void openRegisterGroupWindow() {
        try {
            GUI_RegisterGroup registerGroupApp = new GUI_RegisterGroup();
            Stage stage = new Stage();
            registerGroupApp.start(stage);
        } catch (RuntimeException e) {
            statusLabel.setText("Error inesperado en tiempo de ejecución.");
            logger.error("RuntimeException al abrir la ventana de registro de grupo: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Ocurrió un error al abrir la ventana de registro.");
            logger.error("Excepción al abrir la ventana de registro de grupo: {}", e.getMessage(), e);
        }
    }

    public void loadGroupData() {
        ObservableList<GroupDTO> groupList = FXCollections.observableArrayList();
        try {
            List<GroupDTO> groups = groupDAO.getAllGroups();
            groupList.addAll(groups);
            statusLabel.setText("");
        } catch (SQLException e) {
            statusLabel.setText("Error al conectar a la base de datos.");
            logger.error("Error al cargar los grupos: {}", e.getMessage(), e);
        }
        tableViewGroups.setItems(groupList);
        updateGroupCounts(groupList);
    }

    public void searchGroup() {
        String searchQuery = searchField.getText().trim();
        if (searchQuery.isEmpty()) {
            loadGroupData();
            return;
        }

        ObservableList<GroupDTO> filteredList = FXCollections.observableArrayList();

        try {
            GroupDTO group = groupDAO.searchGroupById(searchQuery);
            if (group != null && !"N/A".equals(group.getNRC())) {
                filteredList.add(group);
            }
        } catch (SQLException e) {
            statusLabel.setText("Error al buscar grupos.");
            logger.error("Error al buscar grupos: {}", e.getMessage(), e);
        }

        tableViewGroups.setItems(filteredList);
        updateGroupCounts(filteredList);
    }

    private void updateGroupCounts(ObservableList<GroupDTO> list) {
        int total = list.size();
        labelGroupCounts.setText("Totales: " + total);
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
        if (userRole == Role.ACADEMICO_EVALUADOR) {
            setButtonVisibility(buttonRegisterGroup, false);
        } else if (userRole == Role.ACADEMICO) {
            setButtonVisibility(buttonRegisterGroup, false);
        } else if (userRole == Role.COORDINADOR) {
            setButtonVisibility(buttonRegisterGroup, true);
        }
    }
}