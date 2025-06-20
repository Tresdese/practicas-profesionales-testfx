package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
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
    private TableView<PeriodDTO> periodsTableView;

    @FXML
    private TableColumn<PeriodDTO, String> idPeriodColumn;

    @FXML
    private TableColumn<PeriodDTO, String> nameColumn;

    @FXML
    private TableColumn<PeriodDTO, java.sql.Timestamp> startDateColumn;

    @FXML
    private TableColumn<PeriodDTO, java.sql.Timestamp> endDateColumn;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button registerPeriodButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Label periodCountsLabel;

    private PeriodDTO selectedPeriod;
    private PeriodDAO periodDAO;
    private Role userRole;

    public void initialize() {
        this.periodDAO = new PeriodDAO();

        idPeriodColumn.setCellValueFactory(new PropertyValueFactory<>("idPeriod"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        loadPeriodData();

        searchButton.setOnAction(event -> searchPeriod());
        registerPeriodButton.setOnAction(event -> openRegisterPeriodWindow());

        periodsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedPeriod = newVal;
            periodsTableView.refresh();
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
        periodsTableView.setItems(periodList);
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

        periodsTableView.setItems(filteredList);
        updatePeriodCounts(filteredList);
    }

    private void updatePeriodCounts(ObservableList<PeriodDTO> list) {
        int total = list.size();
        periodCountsLabel.setText("Totales: " + total);
    }

    public void setUserRole(Role userRole) {
        this.userRole = userRole;
    }

    public void setButtonVisibility(Button btn, boolean visible) {
        if (btn != null) {
            btn.setVisible(visible);
            btn.setManaged(visible);
        }
    }

}