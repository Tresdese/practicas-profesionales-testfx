package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import logic.DAO.LinkedOrganizationDAO;
import logic.DAO.RepresentativeDAO;
import logic.DAO.UserDAO;
import logic.DTO.LinkedOrganizationDTO;
import logic.DTO.RepresentativeDTO;

import logic.DTO.Role;
import logic.DTO.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class GUI_CheckListLinkedOrganizationController {
    private final static Logger logger = LogManager.getLogger(GUI_CheckListLinkedOrganizationController.class);

    @FXML
    private TableView<LinkedOrganizationDTO> tableView;

    @FXML
    private TableColumn<LinkedOrganizationDTO, String> columnOrganizationName;

    @FXML
    private TableColumn<LinkedOrganizationDTO, String> columnOrganizationAddress;

    @FXML
    private TableColumn<RepresentativeDTO, String> columnRepresentativeName;

    @FXML
    private TableColumn<RepresentativeDTO, String> columnRepresentativeSurname;

    @FXML
    private TableColumn<RepresentativeDTO, Void> columnRepresentativeEmail;

    @FXML
    private TableColumn<LinkedOrganizationDTO, Void> columnDetails;

    @FXML
    private TableColumn<LinkedOrganizationDTO, Void> columnManagement;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Label statusLabel;

    private LinkedOrganizationDTO selectedLinkedOrganization;

    private GUI_CheckListLinkedOrganization parentController;

    public void initialize() {

        columnOrganizationName.setCellValueFactory(new PropertyValueFactory<>("organizationName"));
        columnOrganizationAddress.setCellValueFactory(new PropertyValueFactory<>("organizationAddress"));
        columnRepresentativeName.setCellValueFactory(new PropertyValueFactory<>("representativeName"));
        columnRepresentativeSurname.setCellValueFactory(new PropertyValueFactory<>("representativeSurname"));
        columnRepresentativeEmail.setCellValueFactory(new PropertyValueFactory<>("representativeEmail"));

        addDetailsButtonToTable();
        addManagementButtonToTable();

        searchButton.setOnAction(event -> searchOrganization());

        buttonRegisterOrganization.setOnAction(event -> openRegisterOrganizationWindow());

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedOrganization = newValue;
            tableView.refresh();
        });
    }

    private void openRegisterOrganizationWindow() {
        try {
            GUI_RegisterLinkedOrganization registerLinkedOrganizationApp = new GUI_RegisterLinkedOrganization();
            registerLinkedOrganizationApp.start(new Stage());
        } catch (Exception e) {
            statusLabel.setText("Error al conectar a la base de datos.");
            logger.error("Error al cargar la interfaz GUI_RegisterLinkedOrganization.fxml: {}", e.getMessage(), e);
        }
    }

    public void loadOrganizationData() {
        ObservableList<LinkedOrganizationDTO> organizationList = FXCollections.observableArrayList();

        try (Connection connection = new data_access.ConecctionDataBase().connectDB()) {
            LinkedOrganizationDAO organizationDAO = new LinkedOrganizationDAO(connection);
            List<LinkedOrganizationDTO> organizations = organizationDAO.getAllLinkedOrganizations();
            organizationList.addAll(organizations);
            statusLabel.setText("");
        } catch (SQLException e) {
            statusLabel.setText("Error al conectar a la base de datos.");
            logger.error("Error al cargar los datos de las organizaciones: {}", e.getMessage(), e);
        }

        tableView.setItems(organizationList);
    }

    public void searchOrganization() {
        String searchQuery = searchField.getText().trim();
        if (searchQuery.isEmpty()) {
            loadOrganizationData();
            return;
        }

        ObservableList<UserDTO> filteredList = FXCollections.observableArrayList();

        try (Connection connection = new data_access.ConecctionDataBase().connectDB()) {
            UserDAO userDAO = new UserDAO(connection);
            UserDTO user = userDAO.searchUserById(searchQuery);
            if (user != null) {
                filteredList.add(user);
            }
        } catch (SQLException e) {
            statusLabel.setText("Error al buscar académicos.");
            logger.error("Error al buscar académicos: {}", e.getMessage(), e);
        }

        tableView.setItems(filteredList);
    }

}
