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
import logic.DAO.LinkedOrganizationDAO;
import logic.DTO.LinkedOrganizationDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class GUI_CheckListLinkedOrganizationController {

    private static final Logger logger = LogManager.getLogger(GUI_CheckListLinkedOrganizationController.class);

    @FXML
    private TableView<LinkedOrganizationDTO> tableView;

    @FXML
    private TableColumn<LinkedOrganizationDTO, String> columnOrganizationName;

    @FXML
    private TableColumn<LinkedOrganizationDTO, String> columnOrganizationAddress;

    @FXML
    private TableColumn<LinkedOrganizationDTO, Void> columnDetails;

    @FXML
    private TableColumn<LinkedOrganizationDTO, Void> columnManagement;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button buttonRegisterOrganization;

    @FXML
    private Label statusLabel;

    private LinkedOrganizationDTO selectedLinkedOrganization;
    private LinkedOrganizationDAO linkedOrganizationDAO;

    public void initialize() {
        try {
            Connection connection = new data_access.ConecctionDataBase().connectDB();
            this.linkedOrganizationDAO = new LinkedOrganizationDAO(connection);

            columnOrganizationName.setCellValueFactory(new PropertyValueFactory<>("name"));
            columnOrganizationAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

            addDetailsButtonToTable();
            addManagementButtonToTable();

            searchButton.setOnAction(event -> searchOrganization());
            buttonRegisterOrganization.setOnAction(event -> openRegisterOrganizationWindow());

            tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                selectedLinkedOrganization = newValue;
                tableView.refresh();
            });

            loadOrganizationData();

        } catch (Exception e) {
            logger.error("Error al inicializar el controlador: {}", e.getMessage(), e);
            statusLabel.setText("Error al inicializar. Por favor, intente más tarde.");
        }
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
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de registro de organización: {}", e.getMessage(), e);
        }
    }

    public void loadOrganizationData() {
        ObservableList<LinkedOrganizationDTO> organizationList = FXCollections.observableArrayList();

        try {
            List<LinkedOrganizationDTO> organizations = linkedOrganizationDAO.getAllLinkedOrganizations();
            organizationList.addAll(organizations);
            statusLabel.setText("");
        } catch (SQLException e) {
            statusLabel.setText("Error al cargar los datos de las organizaciones.");
            logger.error("Error al cargar los datos de las organizaciones: {}", e.getMessage(), e);
        }

        tableView.setItems(organizationList);
    }

    private void searchOrganization() {
        String searchQuery = searchField.getText().trim();
        if (searchQuery.isEmpty()) {
            loadOrganizationData();
            return;
        }

        ObservableList<LinkedOrganizationDTO> filteredList = FXCollections.observableArrayList();

        try {
            LinkedOrganizationDTO organization = linkedOrganizationDAO.searchLinkedOrganizationById(searchQuery);
            if (organization != null) {
                filteredList.add(organization);
            }
        } catch (SQLException e) {
            statusLabel.setText("Error al buscar la organización.");
            logger.error("Error al buscar la organización: {}", e.getMessage(), e);
        }

        tableView.setItems(filteredList);
    }

    private void addDetailsButtonToTable() {
        Callback<TableColumn<LinkedOrganizationDTO, Void>, TableCell<LinkedOrganizationDTO, Void>> cellFactory = param -> new TableCell<>() {
            private final Button detailsButton = new Button("Ver detalles");

            {
                detailsButton.setOnAction(event -> {
                    LinkedOrganizationDTO organization = getTableView().getItems().get(getIndex());
                    System.out.println("Detalles de: " + organization.getName());
                    // TODO Lógica para mostrar detalles de la organización
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || selectedLinkedOrganization == null || getTableView().getItems().get(getIndex()) != selectedLinkedOrganization) {
                    setGraphic(null);
                } else {
                    setGraphic(detailsButton);
                }
            }
        };

        columnDetails.setCellFactory(cellFactory);
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

        columnManagement.setCellFactory(cellFactory);
    }

    private void openManageOrganizationWindow(LinkedOrganizationDTO organization) {
        try {
            GUI_ManageLinkedOrganization.setLinkedOrganization(organization);
            GUI_ManageLinkedOrganization manageOrganizationApp = new GUI_ManageLinkedOrganization();
            Stage stage = new Stage();
            manageOrganizationApp.start(stage);
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de gestión de organización: {}", e.getMessage(), e);
        }
    }
}