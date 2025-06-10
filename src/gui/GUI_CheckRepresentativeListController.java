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
import javafx.util.Callback;
import logic.DTO.LinkedOrganizationDTO;
import logic.DTO.RepresentativeDTO;
import logic.services.RepresentativeService;
import logic.services.ServiceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

public class GUI_CheckRepresentativeListController {

    private static final Logger logger = LogManager.getLogger(GUI_CheckRepresentativeListController.class);

    @FXML
    private Button buttonRegisterRepresentative;

    @FXML
    private TableColumn<RepresentativeDTO, ?> representativeEmail;

    @FXML
    private TableColumn<RepresentativeDTO, ?> representativeName;

    @FXML
    private TableColumn<RepresentativeDTO, String> representativeOrganization;

    @FXML
    private TableColumn<RepresentativeDTO, ?> representativeSurname;

    @FXML
    private TableColumn<RepresentativeDTO, Void> columnDetails;

    @FXML
    private TableColumn<RepresentativeDTO, Void> columnManagement;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private Label statusLabel;

    @FXML
    private Label labelRepresentativeCounts;

    @FXML
    private TableView<RepresentativeDTO> tableView;

    private RepresentativeDTO selectedRepresentative;
    private RepresentativeService representativeService;

    public void initialize() {
        try {
            ServiceConfig serviceConfig = new ServiceConfig();
            representativeService = serviceConfig.getRepresentativeService();
        } catch (SQLException e) {
            logger.error("Error al inicializar el servicio del representante: {}", e.getMessage(), e);
        }

        representativeName.setCellValueFactory(new PropertyValueFactory<>("names"));
        representativeSurname.setCellValueFactory(new PropertyValueFactory<>("surnames"));
        representativeEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        representativeOrganization.setCellValueFactory(cellData -> {
            String organizationId = cellData.getValue().getIdOrganization();
            String organizationName = getOrganizationNameById(organizationId);
            return new SimpleStringProperty(organizationName);
        });

        addDetailsButtonToTable();
        addManagementButtonToTable();

        loadOrganizationData();

        searchButton.setOnAction(event -> searchRepresentative());
        buttonRegisterRepresentative.setOnAction(event -> openRegisterRepresentativeWindow());

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedRepresentative = (RepresentativeDTO) newValue;
            tableView.refresh();
        });
    }

    private String getOrganizationNameById(String organizationId) {
        try {
            if (organizationId == null || organizationId.isEmpty()) {
                return "No asignado";
            }

            ServiceConfig serviceConfig = new ServiceConfig();
            LinkedOrganizationDTO organization = serviceConfig.getLinkedOrganizationService().searchLinkedOrganizationById(organizationId);

            return (organization != null) ? organization.getName() : "Organización no encontrada";
        } catch (Exception e) {
            logger.error("Error al obtener nombre de organización: {}", e.getMessage(), e);
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
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de registro de representantes: {}", e.getMessage(), e);
        }
    }

    public void loadOrganizationData() {
        ObservableList<RepresentativeDTO> representativeList = FXCollections.observableArrayList();

        try {
            List<RepresentativeDTO> representatives = representativeService.getAllRepresentatives();
            representativeList.addAll(representatives);
            statusLabel.setText("");
        } catch (SQLException e) {
            statusLabel.setText("Error al cargar los datos de las organizaciones.");
            logger.error("Error al cargar los datos de las organizaciones: {}", e.getMessage(), e);
        }

        tableView.setItems(representativeList);
        updateRepresentativeCounts(representativeList); // <-- AGREGADO
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
            statusLabel.setText("Error al buscar la organización.");
            logger.error("Error al buscar la organización: {}", e.getMessage(), e);
        }

        tableView.setItems(filteredList);
        updateRepresentativeCounts(filteredList); // <-- AGREGADO
    }

    private void addDetailsButtonToTable() {
        Callback<TableColumn<RepresentativeDTO, Void>, TableCell<RepresentativeDTO, Void>> cellFactory = param -> new TableCell<>() {
            private final Button detailsButton = new Button("Ver detalles");

            {
                detailsButton.setOnAction(event -> {
                    if (getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                        return;
                    }
                    RepresentativeDTO representative = getTableView().getItems().get(getIndex());
                    System.out.println("Detalles de: " + representative.getNames() + " " + representative.getSurnames());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || selectedRepresentative == null || getIndex() < 0 || getIndex() >= getTableView().getItems().size() || !getTableView().getItems().get(getIndex()).equals(selectedRepresentative)) {
                    setGraphic(null);
                } else {
                    setGraphic(detailsButton);
                }
            }
        };

        columnDetails.setCellFactory(cellFactory);
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

        columnManagement.setCellFactory(cellFactory);
    }

    private void openManageRepresentativeWindow(RepresentativeDTO representative) {
        try {
            GUI_ManageRepresentative.setRepresentative(representative);
            GUI_ManageRepresentative manageRepresentativeApp = new GUI_ManageRepresentative();
            Stage stage = new Stage();
            manageRepresentativeApp.start(stage);
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de gestión de representante: {}", e.getMessage(), e);
        }
    }

    private void updateRepresentativeCounts(ObservableList<RepresentativeDTO> list) {
        int total = list.size();
        labelRepresentativeCounts.setText("Totales: " + total);
    }
}