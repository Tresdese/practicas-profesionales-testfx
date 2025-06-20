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

    private static final Logger logger = LogManager.getLogger(GUI_CheckScheduleActivityListController.class);

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
            logger.error("Error al obtener nombre de evidencia: {}", e.getMessage(), e);
            return "Error";
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
            logger.error("Error al abrir la ventana de registro de cronograma: {}", e.getMessage(), e);
        }
    }

    public void loadScheduleData() {
        ObservableList<ScheduleOfActivitiesDTO> scheduleList = FXCollections.observableArrayList();
        try {
            List<ScheduleOfActivitiesDTO> schedules = scheduleDAO.getAllSchedulesOfActivities();
            scheduleList.addAll(schedules);
            statusLabel.setText("");
        } catch (SQLException e) {
            statusLabel.setText("Error al cargar los cronogramas.");
            logger.error("Error al cargar los cronogramas: {}", e.getMessage(), e);
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
            statusLabel.setText("Error al buscar el cronograma.");
            logger.error("Error al buscar el cronograma: {}", e.getMessage(), e);
        }
        tableView.setItems(filteredList);
        updateScheduleCounts(filteredList);
    }

    private void updateScheduleCounts(ObservableList<ScheduleOfActivitiesDTO> list) {
        int total = list.size();
        scheduleCountsLabel.setText("Totales: " + total);
    }
}