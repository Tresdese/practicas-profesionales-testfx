package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import logic.DAO.EvidenceDAO;
import logic.DAO.ScheduleOfActivitiesDAO;
import logic.DTO.EvidenceDTO;
import logic.DTO.ScheduleOfActivitiesDTO;
import logic.DTO.StudentDTO;

import static logic.drive.GoogleDriveFolderCreator.createOrGetFolder;
import static logic.drive.GoogleDriveUploader.uploadFile;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GUI_RegisterActivityScheduleController {

    @FXML
    private TextField evidenceNameField;

    @FXML
    private DatePicker deliveryDatePicker;

    @FXML
    private TextField evidenceFileTextField;

    @FXML
    private Button selectFileButton;

    @FXML
    private Button registerEvidenceButton;

    @FXML
    private TextField fieldMilestone;

    @FXML
    private DatePicker fieldEstimatedDate;

    @FXML
    private TextField fieldTuition;

    @FXML
    private ChoiceBox<EvidenceDTO> choiceEvidence;

    @FXML
    private Button registerScheduleButton;

    @FXML
    private Label statusLabel;

    private StudentDTO student;
    private File selectedEvidenceFile;
    private ObservableList<EvidenceDTO> evidences = FXCollections.observableArrayList();

    private static final Logger LOGGER = Logger.getLogger(GUI_RegisterActivityScheduleController.class.getName());

    @FXML
    private void initialize() {
        evidenceFileTextField.setEditable(false);
        setScheduleSectionEnabled(false);
        loadEvidences();
        choiceEvidence.setConverter(new javafx.util.StringConverter<EvidenceDTO>() {
            @Override
            public String toString(EvidenceDTO evidence) {
                return evidence != null ? evidence.getEvidenceName() : "";
            }
            @Override
            public EvidenceDTO fromString(String string) {
                return null;
            }
        });

        choiceEvidence.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean enabled = newVal != null;
            setScheduleSectionEnabled(enabled);
        });
    }

    public void setStudent(StudentDTO student) {
        this.student = student;
        if (student != null) {
            fieldTuition.setText(student.getTuition());
        }
    }

    @FXML
    private void handleSelectEvidenceFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo de evidencia");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos permitidos", "*.pdf", "*.jpg", "*.jpeg", "*.png", "*.docx")
        );
        File file = fileChooser.showOpenDialog(evidenceFileTextField.getScene().getWindow());
        if (file != null) {
            String fileName = file.getName().toLowerCase();
            if (!(fileName.endsWith(".pdf") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                    fileName.endsWith(".png") || fileName.endsWith(".docx"))) {
                showAlert("Solo se permiten archivos PDF, imágenes (JPG, PNG) o documentos DOCX.");
                return;
            }
            selectedEvidenceFile = file;
            evidenceFileTextField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void handleRegisterEvidence() {
        String name = evidenceNameField.getText();
        LocalDate localDate = deliveryDatePicker.getValue();
        String route = evidenceFileTextField.getText();

        if (name.isEmpty() || localDate == null || route.isEmpty()) {
            showAlert("Completa todos los campos de evidencia.");
            return;
        }

        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        try {
            EvidenceDAO evidenceDAO = new EvidenceDAO();
            int nextId = evidenceDAO.getNextEvidenceId();

            String driveUrl = uploadEvidenceToDrive(selectedEvidenceFile);
            if (driveUrl == null) return;

            EvidenceDTO evidence = new EvidenceDTO(nextId, name, date, driveUrl);
            boolean inserted = evidenceDAO.insertEvidence(evidence);
            if (inserted) {
                showAlert("Evidencia registrada correctamente.");
                clearEvidenceForm();
                loadEvidences();
                setScheduleSectionEnabled(true);
                for (EvidenceDTO ev : evidences) {
                    if (ev.getIdEvidence() == nextId) {
                        choiceEvidence.setValue(ev);
                        break;
                    }
                }
            } else {
                showAlert("No se pudo registrar la evidencia.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al registrar la evidencia", e);
            showAlert("Error de base de datos al registrar la evidencia.");
        }
    }

    private String uploadEvidenceToDrive(File file) {
        try {
            String idPeriod = getIdPeriod();
            String parentId = createDriveFolders(idPeriod);
            return uploadFile(file.getAbsolutePath(), parentId);
        } catch (IOException | GeneralSecurityException e) {
            showAlert("Error al subir archivo a Google Drive.");
            LOGGER.log(Level.SEVERE, "Error al subir archivo a Drive", e);
            return null;
        }
    }

    private String createDriveFolders(String idPeriod) {
        try {
            String parentId = null;
            parentId = createOrGetFolder(idPeriod, parentId);
            parentId = createOrGetFolder(student.getNRC(), parentId);
            parentId = createOrGetFolder(student.getTuition(), parentId);
            parentId = createOrGetFolder("Cronograma", parentId);
            return parentId;
        } catch (IOException | GeneralSecurityException e) {
            showAlert("Error al crear carpetas en Google Drive.");
            LOGGER.log(Level.SEVERE, "Error al crear carpetas en Drive", e);
            return null;
        }
    }

    private void loadEvidences() {
        try {
            EvidenceDAO evidenceDAO = new EvidenceDAO();
            List<EvidenceDTO> list = evidenceDAO.getAllEvidences();
            evidences.setAll(list);
            choiceEvidence.setItems(evidences);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar evidencias", e);
        }
    }

    @FXML
    private void handleRegisterActivitySchedule() {
        String milestone = fieldMilestone.getText();
        LocalDate localDate = fieldEstimatedDate.getValue();
        String tuition = fieldTuition.getText();
        EvidenceDTO selectedEvidence = choiceEvidence.getValue();

        if (milestone.isEmpty() || localDate == null || tuition.isEmpty() || selectedEvidence == null) {
            statusLabel.setText("Completa todos los campos del cronograma.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        Timestamp estimatedDate = Timestamp.valueOf(localDate.atStartOfDay());
        String idEvidence = String.valueOf(selectedEvidence.getIdEvidence());

        try {
            ScheduleOfActivitiesDAO scheduleDAO = new ScheduleOfActivitiesDAO();
            ScheduleOfActivitiesDTO schedule = new ScheduleOfActivitiesDTO(
                    null, milestone, estimatedDate, tuition, idEvidence
            );
            boolean inserted = scheduleDAO.insertScheduleOfActivities(schedule);
            if (inserted) {
                statusLabel.setText("¡Cronograma registrado exitosamente!");
                statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
                clearScheduleForm();
            } else {
                statusLabel.setText("No se pudo registrar el cronograma.");
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al registrar el cronograma", e);
            statusLabel.setText("Error de base de datos al registrar el cronograma.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    private String getIdPeriod() {
        try {
            logic.DAO.GroupDAO groupDAO = new logic.DAO.GroupDAO();
            logic.DTO.GroupDTO group = groupDAO.searchGroupById(student.getNRC());
            return (group != null && group.getIdPeriod() != null) ? group.getIdPeriod() : "PeriodoDesconocido";
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error en la base de datos", e);
            return "PeriodoDesconocido";
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se pudo obtener el periodo del grupo", e);
            return "PeriodoDesconocido";
        }
    }

    private void setScheduleSectionEnabled(boolean enabled) {
        fieldMilestone.setDisable(!enabled);
        fieldEstimatedDate.setDisable(!enabled);
        fieldTuition.setDisable(!enabled);
        registerScheduleButton.setDisable(!enabled);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }

    private void clearEvidenceForm() {
        evidenceNameField.clear();
        deliveryDatePicker.setValue(null);
        evidenceFileTextField.clear();
        selectedEvidenceFile = null;
    }

    private void clearScheduleForm() {
        fieldMilestone.clear();
        fieldEstimatedDate.setValue(null);
        fieldTuition.clear();
        choiceEvidence.setValue(null);
    }
}