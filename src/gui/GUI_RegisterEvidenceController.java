//package gui;
//
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.stage.FileChooser;
//import javafx.stage.Stage;
//import logic.DAO.EvidenceDAO;
//import logic.DTO.EvidenceDTO;
//
//import java.io.File;
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//import java.sql.SQLException;
//import java.time.LocalDate;
//import java.time.ZoneId;
//import java.util.Date;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import static logic.drive.GoogleDriveFolderCreator.createOrGetFolder;
//import static logic.drive.GoogleDriveUploader.uploadFile;
//
//public class GUI_RegisterEvidenceController {
//
//    @FXML
//    private TextField evidenceNameField;
//
//    @FXML
//    private DatePicker deliveryDatePicker;
//
//    @FXML
//    private TextField evidenceFileTextField;
//
//    @FXML
//    private Button selectFileButton;
//
//    @FXML
//    private Button registerButton;
//
//    private File selectedEvidenceFile;
//
//    private static final Logger LOGGER = Logger.getLogger(GUI_RegisterEvidenceController.class.getName());
//
//    @FXML
//    private void initialize() {
//        evidenceFileTextField.setEditable(false);
//    }
//
//    @FXML
//    private void handleSelectEvidenceFile() {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Seleccionar archivo de evidencia");
//        fileChooser.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter("Archivos permitidos", "*.pdf", "*.jpg", "*.jpeg", "*.png", "*.docx")
//        );
//        File file = fileChooser.showOpenDialog(evidenceFileTextField.getScene().getWindow());
//        if (file != null) {
//            String fileName = file.getName().toLowerCase();
//            if (!(fileName.endsWith(".pdf") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
//                    fileName.endsWith(".png") || fileName.endsWith(".docx"))) {
//                showAlert("Solo se permiten archivos PDF, imágenes (JPG, PNG) o documentos DOCX.");
//                return;
//            }
//            selectedEvidenceFile = file;
//            evidenceFileTextField.setText(file.getAbsolutePath());
//        }
//    }
//
//    private String uploadEvidenceToDrive(File file) {
//        try {
//            String parentId = createDriveFolders(idPeriod);
//            return uploadFile(file.getAbsolutePath(), parentId);
//        } catch (IOException | GeneralSecurityException e) {
//            showAlert("Error al subir archivo a Google Drive.");
//            LOGGER.log(Level.SEVERE, "Error al subir archivo a Drive", e);
//            return null;
//        }
//    }
//
//    private String createDriveFolders(String idPeriod) {
//        try {
//            String parentId = null;
//            parentId = createOrGetFolder(idPeriod, parentId);
//            parentId = createOrGetFolder(student.getNRC(), parentId);
//            parentId = createOrGetFolder(student.getTuition(), parentId);
//            parentId = createOrGetFolder("Reporte", parentId);
//            return parentId;
//        } catch (IOException | GeneralSecurityException e) {
//            showAlert("Error al crear carpetas en Google Drive.");
//            LOGGER.log(Level.SEVERE, "Error al crear carpetas en Drive", e);
//            return null;
//        }
//    }
//
//    @FXML
//    private void handleRegisterEvidence() {
//        String name = evidenceNameField.getText();
//        LocalDate localDate = deliveryDatePicker.getValue();
//        String route = evidenceFileTextField.getText();
//
//        if (name.isEmpty() || localDate == null || route.isEmpty()) {
//            showAlert("Completa todos los campos.");
//            return;
//        }
//
//        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//        try {
//            EvidenceDAO evidenceDAO = new EvidenceDAO();
//            int nextId = evidenceDAO.getNextEvidenceId();
//            EvidenceDTO evidence = new EvidenceDTO(nextId, name, date, route);
//            boolean inserted = evidenceDAO.insertEvidence(evidence);
//            if (inserted) {
//                showAlert("Evidencia registrada correctamente.");
//                clearForm();
//            } else {
//                showAlert("No se pudo registrar la evidencia.");
//            }
//        } catch (SQLException e) {
//            LOGGER.log(Level.SEVERE, "Error al registrar la evidencia", e);
//            showAlert("Error de base de datos al registrar la evidencia.");
//        }
//    }
//
//    private void showAlert(String message) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
//        alert.showAndWait();
//    }
//
//    private void clearForm() {
//        evidenceNameField.clear();
//        deliveryDatePicker.setValue(null);
//        evidenceFileTextField.clear();
//        selectedEvidenceFile = null;
//    }
//}