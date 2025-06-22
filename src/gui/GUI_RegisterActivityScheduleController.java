package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.scene.paint.Color;
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
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GUI_RegisterActivityScheduleController {

    @FXML
    private TextField evidenceFileTextField;
    @FXML
    private Button selectFileButton;
    @FXML
    private TextField milestoneField;
    @FXML
    private DatePicker estimatedDateField;
    @FXML
    private TextField fieldTuition;
    @FXML
    private Button registerScheduleButton;
    @FXML
    private Label statusLabel;

    private StudentDTO student;
    private File selectedEvidenceFile;

    private static final Logger LOGGER = Logger.getLogger(GUI_RegisterActivityScheduleController.class.getName());
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024;

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
            if (file.length() > MAX_FILE_SIZE) {
                showAlert("El archivo seleccionado es demasiado grande. El tamaño máximo permitido es 20 MB.");
                return;
            }
            selectedEvidenceFile = file;
            evidenceFileTextField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void handleRegisterActivitySchedule() {
        statusLabel.setText("");
        if (!validateInputs()) return;

        int evidenceId = saveEvidenceFile(selectedEvidenceFile);
        if (evidenceId == -1) return;

        boolean scheduleSaved = saveActivitySchedule(evidenceId);
        if (!scheduleSaved) return;

        statusLabel.setText("¡Cronograma y evidencia registrados exitosamente!");
        statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
        clearForm();
    }

    private boolean validateInputs() {
        String filePath = evidenceFileTextField.getText();
        String milestone = milestoneField.getText();
        LocalDate estimatedDate = estimatedDateField.getValue();
        String tuition = fieldTuition.getText();

        if (filePath.isEmpty() || milestone.isEmpty() || estimatedDate == null || tuition.isEmpty() || selectedEvidenceFile == null) {
            statusLabel.setText("Completa todos los campos y selecciona un archivo.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            return false;
        }
        return true;
    }

    private int saveEvidenceFile(File file) {
        try {
            EvidenceDAO evidenceDAO = new EvidenceDAO();
            int nextId = evidenceDAO.getNextEvidenceId();

            String driveUrl = uploadEvidenceToDrive(file);
            if (driveUrl == null) return -1;

            EvidenceDTO evidence = new EvidenceDTO(nextId, file.getName(), new Date(), driveUrl);
            boolean inserted = evidenceDAO.insertEvidence(evidence);
            if (!inserted) {
                statusLabel.setText("No se pudo registrar la evidencia.");
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
                return -1;
            }
            return nextId;
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                showAlert("Error de conexión con la base de datos. Por favor, intente más tarde.");
                LOGGER.log(Level.SEVERE, "Error de conexión con la base de datos", e);
                return -1;
            } else if (sqlState != null && sqlState.equals("08S01")) {
                showAlert("Conexión interrumpida con la base de datos.");
                LOGGER.log(Level.SEVERE, "Conexión interrumpida con la base de datos", e);
                return -1;
            } else if (sqlState != null && sqlState.equals("42000")) {
                showAlert("Base de datos desconocida.");
                LOGGER.log(Level.SEVERE, "Base de datos desconocida", e);
                return -1;
            } else if (sqlState != null && sqlState.equals("28000")) {
                showAlert("Acceso denegado a la base de datos.");
                LOGGER.log(Level.SEVERE, "Acceso denegado a la base de datos", e);
                return -1;
            } else if (sqlState != null && sqlState.equals("23000")) {
                showAlert("Violación de restricción de integridad.");
                LOGGER.log(Level.SEVERE, "Violación de restricción de integridad", e);
                return -1;
            } else {
                showAlert("Error al registrar la evidencia.");
                LOGGER.log(Level.SEVERE, "Error al registrar la evidencia", e);
                return -1;
            }
        } catch (Exception e) {
            showAlert("Ocurrió un error inesperado al registrar la evidencia.");
            LOGGER.log(Level.SEVERE, "Error inesperado al registrar la evidencia", e);
            return -1;
        }
    }

    private String uploadEvidenceToDrive(File file) {
        try {
            String idPeriod = getIdPeriod();
            String parentId = createDriveFolders(idPeriod);
            return uploadFile(file.getAbsolutePath(), parentId);
        } catch (IOException e) {
            showAlert("Error de acceso al archivo al subir a Google Drive.");
            LOGGER.log(Level.SEVERE, "IOException al subir archivo a Drive", e);
            return null;
        } catch (GeneralSecurityException e) {
            showAlert("Error al conectar con Google Drive.");
            LOGGER.log(Level.SEVERE, "GeneralSecurityException al subir archivo a Drive", e);
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
        } catch (IOException e) {
            showAlert("Error de acceso a las carpetas de Google Drive.");
            LOGGER.log(Level.SEVERE, "IOException al subir archivo a Drive", e);
            return null;
        } catch (GeneralSecurityException e) {
            showAlert("Error al conectar con Google Drive.");
            LOGGER.log(Level.SEVERE, "GeneralSecurityException al crear carpeta en Drive", e);
            return null;
        }
    }

    private boolean saveActivitySchedule(int evidenceId) {
        String milestone = milestoneField.getText();
        LocalDate localDate = estimatedDateField.getValue();
        String tuition = fieldTuition.getText();
        Timestamp estimatedDate = Timestamp.valueOf(localDate.atStartOfDay());

        try {
            ScheduleOfActivitiesDAO scheduleDAO = new ScheduleOfActivitiesDAO();
            ScheduleOfActivitiesDTO schedule = new ScheduleOfActivitiesDTO(
                    null, milestone, estimatedDate, tuition, String.valueOf(evidenceId)
            );
            return scheduleDAO.insertScheduleOfActivities(schedule);
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                showAlert("Error de conexión con la base de datos. Por favor, intente más tarde.");
                LOGGER.log(Level.SEVERE, "Error de conexión con la base de datos", e);
                return false;
            } else if (sqlState != null && sqlState.equals("08S01")) {
                showAlert("Conexión interrumpida con la base de datos.");
                LOGGER.log(Level.SEVERE, "Conexión interrumpida con la base de datos", e);
                return false;
            } else if (sqlState != null && sqlState.equals("42000")) {
                showAlert("Base de datos desconocida.");
                LOGGER.log(Level.SEVERE, "Base de datos desconocida", e);
                return false;
            } else if (sqlState != null && sqlState.equals("28000")) {
                showAlert("Acceso denegado a la base de datos.");
                LOGGER.log(Level.SEVERE, "Acceso denegado a la base de datos", e);
                return false;
            } else if (sqlState != null && sqlState.equals("23000")) {
                showAlert("Violación de restricción de integridad.");
                LOGGER.log(Level.SEVERE, "Violación de restricción de integridad", e);
                return false;
            }
             else {
                showAlert("Error al registrar el cronograma de actividades.");
                LOGGER.log(Level.SEVERE, "Error al registrar el cronograma de actividades", e);
                return false;
            }
        } catch (Exception e) {
            showAlert("Ocurrió un error inesperado al registrar el cronograma de actividades.");
            LOGGER.log(Level.SEVERE, "Error inesperado al registrar el cronograma de actividades", e);
            return false;
        }
    }

    private String getIdPeriod() {
        try {
            logic.DAO.GroupDAO groupDAO = new logic.DAO.GroupDAO();
            logic.DTO.GroupDTO group = groupDAO.searchGroupById(student.getNRC());
            return (group != null && group.getIdPeriod() != null) ? group.getIdPeriod() : "PeriodoDesconocido";
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                LOGGER.log(Level.WARNING, "Error de conexión con la base de datos", e);
                statusLabel.setText("Error de conexión con la base de datos");
                statusLabel.setTextFill(Color.RED);
                return "PeriodoDesconocido";
            } else if (sqlState != null && sqlState.equals("08S01")) {
                LOGGER.log(Level.WARNING, "Conexión interrumpida con la base de datos", e);
                statusLabel.setText("Conexión interrumpida con la base de datos");
                statusLabel.setTextFill(Color.RED);
                return "PeriodoDesconocido";
            } else if (sqlState != null && sqlState.equals("42000")) {
                LOGGER.log(Level.WARNING, "Base de datos desconocida", e);
                statusLabel.setText("Base de datos desconocida");
                statusLabel.setTextFill(Color.RED);
                return "PeriodoDesconocido";
            } else if (sqlState != null && sqlState.equals("28000")) {
                LOGGER.log(Level.WARNING, "Acceso denegado a la base de datos", e);
                statusLabel.setText("Acceso denegado a la base de datos");
                statusLabel.setTextFill(Color.RED);
                return "PeriodoDesconocido";
            } else {
                LOGGER.log(Level.WARNING, "Error al obtener el periodo del grupo", e);
                statusLabel.setText("Error al obtener el periodo del grupo");
                statusLabel.setTextFill(Color.RED);
                return "PeriodoDesconocido";
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se pudo obtener el periodo del grupo", e);
            statusLabel.setText("Error al obtener el periodo del grupo");
            statusLabel.setTextFill(Color.RED);
            return "PeriodoDesconocido";
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }

    private void clearForm() {
        evidenceFileTextField.clear();
        selectedEvidenceFile = null;
        milestoneField.clear();
        estimatedDateField.setValue(null);
        fieldTuition.clear();
    }
}