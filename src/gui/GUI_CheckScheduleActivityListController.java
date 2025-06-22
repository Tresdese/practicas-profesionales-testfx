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
import javafx.scene.paint.Color;
import logic.DTO.EvidenceDTO;
import logic.DTO.ScheduleOfActivitiesDTO;
import logic.DAO.EvidenceDAO;
import logic.DAO.ScheduleOfActivitiesDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class GUI_CheckScheduleActivityListController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_CheckScheduleActivityListController.class);

    @FXML
    private Button registerScheduleButton;

    @FXML
    private TableColumn<ScheduleOfActivitiesDTO, String> milestoneColumn;

    @FXML
    private TableColumn<ScheduleOfActivitiesDTO, String> estimatedDateColumn;

    @FXML
    private TableColumn<ScheduleOfActivitiesDTO, String> tuitionColumn;

    @FXML
    private TableColumn<ScheduleOfActivitiesDTO, String> evidenceColumn;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private Label statusLabel;

    @FXML
    private Label scheduleCountsLabel;

    @FXML
    private TableView<ScheduleOfActivitiesDTO> tableView;

    private ScheduleOfActivitiesDTO selectedSchedule;
    private final ScheduleOfActivitiesDAO scheduleDAO = new ScheduleOfActivitiesDAO();
    private final EvidenceDAO evidenceDAO = new EvidenceDAO();

    @FXML
    public void initialize() {
        milestoneColumn.setCellValueFactory(new PropertyValueFactory<>("milestone"));
        estimatedDateColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getEstimatedDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return new SimpleStringProperty(sdf.format(cellData.getValue().getEstimatedDate()));
            }
            return new SimpleStringProperty("");
        });
        tuitionColumn.setCellValueFactory(new PropertyValueFactory<>("tuition"));
        evidenceColumn.setCellValueFactory(cellData -> {
            String idEvidence = cellData.getValue().getIdEvidence();
            String evidenceName = getEvidenceNameById(idEvidence);
            return new SimpleStringProperty(evidenceName);
        });

        loadScheduleData();

        searchButton.setOnAction(event -> searchSchedule());
        registerScheduleButton.setOnAction(event -> openRegisterScheduleWindow());

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedSchedule = newValue;
            tableView.refresh();
        });
    }

    private String getEvidenceNameById(String idEvidence) {
        try {
            if (idEvidence == null || idEvidence.isEmpty()) {
                return "No asignada";
            }
            EvidenceDTO evidence = evidenceDAO.searchEvidenceById(Integer.parseInt(idEvidence));
            return (evidence != null) ? evidence.getEvidenceName() : "Evidencia no encontrada";
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Error de conexión con la base de datos");
                statusLabel.setTextFill(Color.RED);
                return "Error de conexión con la base de datos";
            } else if (sqlState != null && sqlState.equals("08S01")) {
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Conexión interrumpida con la base de datos");
                statusLabel.setTextFill(Color.RED);
                return "Conexión interrumpida con la base de datos";
            } else if (sqlState != null && sqlState.equals("42000")) {
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
                statusLabel.setText("Base de datos desconocida");
                statusLabel.setTextFill(Color.RED);
                return "Base de datos desconocida";
            } else if (sqlState != null && sqlState.equals("28000")) {
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Acceso denegado a la base de datos");
                statusLabel.setTextFill(Color.RED);
                return "Acceso denegado a la base de datos";
            } else {
                LOGGER.error("Error al buscar evidencia por ID: {}", e.getMessage(), e);
                statusLabel.setText("Error al buscar evidencia por ID en la base de datos");
                statusLabel.setTextFill(Color.RED);
                return "Error al buscar evidencia por ID en la base de datos";
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Error al convertir ID de evidencia a número: {}", e.getMessage(), e);
            statusLabel.setText("ID de evidencia inválido");
            statusLabel.setTextFill(Color.RED);
            return "ID de evidencia inválido";
        } catch (Exception e) {
            LOGGER.error("Error inesperado al obtener nombre de evidencia: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al obtener nombre de evidencia");
            statusLabel.setTextFill(Color.RED);
            return "Error inesperado al obtener nombre de evidencia";
        }
    }

    private void openRegisterScheduleWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterActivitySchedule.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.error("Error al abrir la ventana de registro de cronograma: {}", e.getMessage(), e);
            statusLabel.setText("Error al abrir la ventana de registro de cronograma.");
            statusLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al abrir la ventana de registro de cronograma: {}", e.getMessage(), e);
            statusLabel.setText("Ocurrió un error inesperado al abrir la ventana de registro de cronograma.");
            statusLabel.setTextFill(Color.RED);
        }
    }

    public void loadScheduleData() {
        ObservableList<ScheduleOfActivitiesDTO> scheduleList = FXCollections.observableArrayList();
        try {
            List<ScheduleOfActivitiesDTO> schedules = scheduleDAO.getAllSchedulesOfActivities();
            scheduleList.addAll(schedules);
            statusLabel.setText("");
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error de base de datos al cargar los cronogramas.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de base de datos al cargar los cronogramas: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error de entrada/salida al cargar los datos del cronograma.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error de entrada/salida al cargar los datos del cronograma: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al cargar los cronogramas.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al cargar los cronogramas: {}", e.getMessage(), e);
        }
        tableView.setItems(scheduleList);
        updateScheduleCounts(scheduleList);
    }

    private void searchSchedule() {
        String searchQuery = searchField.getText().trim();
        if (searchQuery.isEmpty()) {
            loadScheduleData();
            return;
        }
        ObservableList<ScheduleOfActivitiesDTO> filteredList = FXCollections.observableArrayList();
        try {
            ScheduleOfActivitiesDTO schedule = scheduleDAO.searchScheduleOfActivitiesById(searchQuery);
            if (schedule != null && !"N/A".equals(schedule.getIdSchedule())) {
                filteredList.add(schedule);
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al buscar el cronograma.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al buscar el cronograma: {}", e.getMessage(), e);
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("ID de cronograma inválido.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("ID de cronograma inválido: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al buscar el cronograma.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al buscar el cronograma: {}", e.getMessage(), e);
        }
        tableView.setItems(filteredList);
        updateScheduleCounts(filteredList);
    }

    private void updateScheduleCounts(ObservableList<ScheduleOfActivitiesDTO> list) {
        int total = list.size();
        scheduleCountsLabel.setText("Totales: " + total);
    }
}