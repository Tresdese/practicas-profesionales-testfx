package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.DTO.PeriodDTO;
import logic.DAO.PeriodDAO;
import logic.DTO.Role;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
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
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, verifica tu conexión.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos. Verifica tus credenciales.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if ("42S02".equals(sqlState)) {
                statusLabel.setText("Tabla no encontrada en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Tabla no encontrada en la base de datos: {}", e.getMessage(), e);
            } else if ("42S22".equals(sqlState)) {
                statusLabel.setText("Columna no encontrada en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Columna no encontrada en la base de datos: {}", e.getMessage(), e);
            } else if ("HY000".equals(sqlState)) {
                statusLabel.setText("Error general de la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error general de la base de datos: {}", e.getMessage(), e);
            } else if ("08S01".equals(sqlState)) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("La base de datos no está disponible.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al cargar los periodos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error al cargar los periodos: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al leer la configuración de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error al leer la configuración de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al cargar los periodos.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error inesperado al cargar los periodos: {}", e.getMessage(), e);
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
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, verifica tu conexión.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos. Verifica tus credenciales.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if ("42S02".equals(sqlState)) {
                statusLabel.setText("Tabla no encontrada en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Tabla no encontrada en la base de datos: {}", e.getMessage(), e);
            } else if ("42S22".equals(sqlState)) {
                statusLabel.setText("Columna no encontrada en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Columna no encontrada en la base de datos: {}", e.getMessage(), e);
            } else if ("HY000".equals(sqlState)) {
                statusLabel.setText("Error general de la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error general de la base de datos: {}", e.getMessage(), e);
            } else if ("08S01".equals(sqlState)) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("La base de datos no está disponible.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al buscar el periodo.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error al buscar el periodo: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al leer la configuración de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error al leer la configuración de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al buscar el periodo.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error inesperado al buscar el periodo: {}", e.getMessage(), e);
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