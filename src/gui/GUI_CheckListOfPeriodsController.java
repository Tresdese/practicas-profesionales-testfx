package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import logic.DTO.PeriodDTO;
import logic.DAO.PeriodDAO;
import logic.DTO.Role;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

public class GUI_CheckListOfPeriodsController {

    private static final Logger logger = LogManager.getLogger(GUI_CheckListOfPeriodsController.class);

    @FXML
    private TableView<PeriodDTO> tableViewPeriods;

    @FXML
    private TableColumn<PeriodDTO, String> columnIdPeriod;

    @FXML
    private TableColumn<PeriodDTO, String> columnName;

    @FXML
    private TableColumn<PeriodDTO, java.sql.Timestamp> columnStartDate;

    @FXML
    private TableColumn<PeriodDTO, java.sql.Timestamp> columnEndDate;

    @FXML
    private TableColumn<PeriodDTO, Void> columnDetails;

    @FXML
    private TableColumn<PeriodDTO, Void> columnManagement;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button buttonRegisterPeriod;

    @FXML
    private Label statusLabel;

    @FXML
    private Label labelPeriodCounts;

    private PeriodDTO selectedPeriod;
    private PeriodDAO periodDAO;
    private Role userRole;

    public void initialize() {
        this.periodDAO = new PeriodDAO();

        columnIdPeriod.setCellValueFactory(new PropertyValueFactory<>("idPeriod"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        columnEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        addDetailsButtonToTable();

        loadPeriodData();

        searchButton.setOnAction(event -> searchPeriod());
        buttonRegisterPeriod.setOnAction(event -> openRegisterPeriodWindow());

        tableViewPeriods.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedPeriod = newVal;
            tableViewPeriods.refresh();
        });
    }

    private void openRegisterPeriodWindow() {
        try {
            GUI_RegisterPeriod registerPeriodApp = new GUI_RegisterPeriod();
            Stage stage = new Stage();
            registerPeriodApp.start(stage);
        } catch (RuntimeException e) {
            statusLabel.setText("Error inesperado en tiempo de ejecución.");
            logger.error("RuntimeException al abrir la ventana de registro de periodo: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Ocurrió un error al abrir la ventana de registro.");
            logger.error("Excepción al abrir la ventana de registro de periodo: {}", e.getMessage(), e);
        }
    }

    public void loadPeriodData() {
        ObservableList<PeriodDTO> periodList = FXCollections.observableArrayList();
        try {
            List<PeriodDTO> periods = periodDAO.getAllPeriods();
            periodList.addAll(periods);
            statusLabel.setText("");
        } catch (SQLException e) {
            statusLabel.setText("Error al conectar a la base de datos.");
            logger.error("Error al cargar los periodos: {}", e.getMessage(), e);
        }
        tableViewPeriods.setItems(periodList);
        updatePeriodCounts(periodList);
    }

    public void searchPeriod() {
        String searchQuery = searchField.getText().trim();
        if (searchQuery.isEmpty()) {
            loadPeriodData();
            return;
        }

        ObservableList<PeriodDTO> filteredList = FXCollections.observableArrayList();

        try {
            PeriodDTO period = periodDAO.searchPeriodById(searchQuery);
            if (period != null && !"N/A".equals(period.getIdPeriod())) {
                filteredList.add(period);
            }
        } catch (SQLException e) {
            statusLabel.setText("Error al buscar periodos.");
            logger.error("Error al buscar periodos: {}", e.getMessage(), e);
        }

        tableViewPeriods.setItems(filteredList);
        updatePeriodCounts(filteredList);
    }

    private void updatePeriodCounts(ObservableList<PeriodDTO> list) {
        int total = list.size();
        labelPeriodCounts.setText("Totales: " + total);
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
            setButtonVisibility(buttonRegisterPeriod, false);
            columnManagement.setVisible(false);
        } else if (userRole == Role.ACADEMICO) {
            setButtonVisibility(buttonRegisterPeriod, false);
            columnManagement.setVisible(true);
        } else if (userRole == Role.COORDINADOR) {
            setButtonVisibility(buttonRegisterPeriod, true);
            columnManagement.setVisible(false);
        }
    }

    private void addDetailsButtonToTable() {
        Callback<TableColumn<PeriodDTO, Void>, TableCell<PeriodDTO, Void>> cellFactory = param -> new TableCell<>() {
            private final Button detailsButton = new Button("Ver detalles");

            {
                detailsButton.setOnAction(event -> {
                    PeriodDTO period = getTableView().getItems().get(getIndex());
                    System.out.println("Detalles de: " + period.getIdPeriod());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || selectedPeriod == null || getTableView().getItems().get(getIndex()) != selectedPeriod) {
                    setGraphic(null);
                } else {
                    setGraphic(detailsButton);
                }
            }
        };

        columnDetails.setCellFactory(cellFactory);
    }

}