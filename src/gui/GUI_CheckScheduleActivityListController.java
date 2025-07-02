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
        loadScheduleData();
        setColumns();
        setButtons();

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedSchedule = newValue;
            tableView.refresh();
        });
    }

    private void setColumns() {
        milestoneColumn.setCellValueFactory(new PropertyValueFactory<>("milestone"));

        estimatedDateColumn.setCellValueFactory(cellData -> {
            String formattedDate;
            if (cellData.getValue().getEstimatedDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                formattedDate = sdf.format(cellData.getValue().getEstimatedDate());
            } else {
                formattedDate = "";
            }
            return new SimpleStringProperty(formattedDate);
        });

        tuitionColumn.setCellValueFactory(new PropertyValueFactory<>("tuition"));

        evidenceColumn.setCellValueFactory(cellData -> {
            String idEvidence = cellData.getValue().getIdEvidence();
            String evidenceName = getEvidenceNameById(idEvidence);
            return new SimpleStringProperty(evidenceName);
        });
    }

    private void setButtons() {
        searchButton.setOnAction(event -> searchSchedule());
        registerScheduleButton.setOnAction(event -> openRegisterScheduleWindow());
    }

    private String getEvidenceNameById(String idEvidence) {
        String result;
        try {
            if (idEvidence == null || idEvidence.isEmpty()) {
                result = "No asignada";
            } else {
                EvidenceDTO evidence = evidenceDAO.searchEvidenceById(Integer.parseInt(idEvidence));
                result = (evidence != null) ? evidence.getEvidenceName() : "Evidencia no encontrada";
            }
        } catch (SQLException e) {
            result = handleSqlException(e, "evidencia");
        } catch (NumberFormatException e) {
            LOGGER.error("Error al convertir ID de evidencia a número: {}", e.getMessage(), e);
            statusLabel.setText("ID de evidencia inválido");
            statusLabel.setTextFill(Color.RED);
            result = "ID de evidencia inválido";
        } catch (IOException e) {
            LOGGER.error("Error al leer el archivo de configuracion de la base de datos: {}", e.getMessage(), e);
            statusLabel.setText("Error al leer el archivo de configuracion de la base de datos");
            statusLabel.setTextFill(Color.RED);
            result = "Error al leer el archivo de configuracion de la base de datos";
        } catch (Exception e) {
            LOGGER.error("Error inesperado al obtener nombre de evidencia: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al obtener nombre de evidencia");
            statusLabel.setTextFill(Color.RED);
            result = "Error inesperado al obtener nombre de evidencia";
        }
        return result;
    }

    private String handleSqlException(SQLException e, String context) {
        String sqlState = e.getSQLState();
        String errorMessage;

        if ("08001".equals(sqlState)) {
            errorMessage = "Error de conexión con la base de datos";
        } else if ("08S01".equals(sqlState)) {
            errorMessage = "Conexión interrumpida con la base de datos";
        } else if ("42S02".equals(sqlState)) {
            errorMessage = "Tabla no encontrada en la base de datos";
        } else if ("42S22".equals(sqlState)) {
            errorMessage = "Columna no encontrada en la base de datos";
        } else if ("HY000".equals(sqlState)) {
            errorMessage = "Error general de la base de datos";
        } else if ("42000".equals(sqlState)) {
            errorMessage = "Base de datos desconocida";
        } else if ("28000".equals(sqlState)) {
            errorMessage = "Acceso denegado a la base de datos";
        } else {
            errorMessage = "Error al buscar " + context + " por ID en la base de datos";
        }

        LOGGER.error("{}: {}", errorMessage, e.getMessage(), e);
        statusLabel.setText(errorMessage);
        statusLabel.setTextFill(Color.RED);
        return errorMessage;
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
            handleSqlException(e, "cronogramas");
        } catch (IOException e) {
            statusLabel.setText("Error al leer el archivo de configuracion de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al leer el archivo de configuracion de la base de datos: {}", e.getMessage(), e);
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
        ObservableList<ScheduleOfActivitiesDTO> filteredList = FXCollections.observableArrayList();

        if (searchQuery.isEmpty()) {
            loadScheduleData();
        } else {
            try {
                ScheduleOfActivitiesDTO schedule = scheduleDAO.searchScheduleOfActivitiesById(searchQuery);
                if (schedule != null && !"N/A".equals(schedule.getIdSchedule())) {
                    filteredList.add(schedule);
                } else {
                    statusLabel.setText("No se encontró el cronograma con el ID especificado.");
                    statusLabel.setTextFill(Color.RED);
                }
            } catch (SQLException e) {
                handleSqlException(e, "cronograma");
            } catch (NumberFormatException e) {
                statusLabel.setText("ID de cronograma inválido.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("ID de cronograma inválido: {}", e.getMessage(), e);
            } catch (IOException e) {
                statusLabel.setText("Error al leer el archivo de configuración de la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al leer el archivo de configuración de la base de datos: {}", e.getMessage(), e);
            } catch (Exception e) {
                statusLabel.setText("Error inesperado al buscar el cronograma.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error inesperado al buscar el cronograma: {}", e.getMessage(), e);
            }
            tableView.setItems(filteredList);
            updateScheduleCounts(filteredList);
        }
    }

    private void updateScheduleCounts(ObservableList<ScheduleOfActivitiesDTO> list) {
        int total = list.size();
        scheduleCountsLabel.setText("Totales: " + total);
    }
}