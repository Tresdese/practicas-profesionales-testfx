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
import logic.DTO.LinkedOrganizationDTO;
import logic.services.LinkedOrganizationService;
import logic.services.ServiceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GUI_CheckListLinkedOrganizationController {

    private static final Logger logger = LogManager.getLogger(GUI_CheckListLinkedOrganizationController.class);

    @FXML
    private TableView<LinkedOrganizationDTO> tableView;

    @FXML
    private TableColumn<LinkedOrganizationDTO, String> organizationNameColumn;

    @FXML
    private TableColumn<LinkedOrganizationDTO, String> organizationAddressColumn;

    @FXML
    private TableColumn<LinkedOrganizationDTO, Void> managementColumn;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button registerOrganizationButton;

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
            logger.error("Error al inicializar el servicio de organización: {}", e.getMessage(), e);
        }

        organizationNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        organizationAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        addManagementButtonToTable();

        loadOrganizationData();

        searchButton.setOnAction(event -> searchOrganization());
        registerOrganizationButton.setOnAction(event -> openRegisterOrganizationWindow());

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedLinkedOrganization = newValue;
            tableView.refresh();
        });
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
            logger.error("Error al abrir la ventana de registro de organización: {}", e.getMessage(), e);
        }
    }

    public void loadOrganizationData() {
        ObservableList<LinkedOrganizationDTO> organizationList = FXCollections.observableArrayList();

        try {
            List<LinkedOrganizationDTO> organizations = linkedOrganizationService.getAllLinkedOrganizations();
            organizationList.addAll(organizations);
            statusLabel.setText("");
        } catch (SQLException e) {
            statusLabel.setText("Error al cargar los datos de las organizaciones.");
            logger.error("Error al cargar los datos de las organizaciones: {}", e.getMessage(), e);
        }

        tableView.setItems(organizationList);
        updateOrganizationCounts(organizationList);
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
            statusLabel.setText("Error al buscar la organización.");
            logger.error("Error al buscar la organización: {}", e.getMessage(), e);
        }

        tableView.setItems(filteredList);
        updateOrganizationCounts(filteredList);
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
        } catch (RuntimeException e) {
            logger.error("Error al abrir la ventana de gestión de organización: {}", e.getMessage(), e);
        }
    }

    private void updateOrganizationCounts(ObservableList<LinkedOrganizationDTO> list) {
        int total = list.size();
        organizationCountsLabel.setText("Totales: " + total);
    }
}