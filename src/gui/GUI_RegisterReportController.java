package gui;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import logic.DAO.ActivityDAO;
import logic.DAO.ActivityReportDAO;
import logic.DAO.ReportDAO;
import logic.DTO.ActivityDTO;
import logic.DTO.ActivityReportDTO;
import logic.DTO.ReportDTO;
import logic.DTO.StudentDTO;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;

import logic.DAO.EvidenceDAO;
import logic.DTO.EvidenceDTO;

import static logic.drive.GoogleDriveFolderCreator.createOrGetFolder;
import static logic.drive.GoogleDriveUploader.uploadFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.SQLException;

public class GUI_RegisterReportController {

    @FXML
    private TextField progressPercentageField;

    @FXML
    private TextArea observationsArea;

    @FXML
    private Button handleRegister, registerActivityButton;

    @FXML
    private ComboBox<ActivityDTO> activityComboBox;

    @FXML
    private TextField careerField;

    @FXML
    private TextField nrcField;

    @FXML
    private TextField professorField;

    @FXML
    private TextField schoolPeriodField;

    @FXML
    private TextField studentField;

    @FXML
    private TextField organizationField;

    @FXML
    private TextField projectField;

    @FXML
    private TextField totalHoursField;

    @FXML
    private TextField reportDateField;

    @FXML
    private TextArea generalObjectiveArea;

    @FXML
    private TextArea methodologyArea;

    @FXML
    private TextArea obtainedResultArea;

    @FXML
    private TableView<ActivityReportDTO> activitiesTable;

    @FXML
    private TableColumn<ActivityReportDTO, String> activityColumn;

    @FXML
    private TableColumn<ActivityReportDTO, Integer> progressColumn;

    @FXML
    private TableColumn<ActivityReportDTO, String> observationColumn;

    @FXML
    private TextArea generalObservationsArea;

    @FXML
    private TextField evidenceFileTextField;

    @FXML
    private Label observationsCharCountLabel, generalObjectiveCharCountLabel, methodologyCharCountLabel,
            obtainedResultCharCountLabel, generalObservationsCharCountLabel;

    private static final int MAX_OBSERVATIONS = 200;

    private static final int MAX_GENERAL_OBJECTIVES = 500;

    private static final int MAX_METHODOLOGIES = 100;

    private static final int MAX_OBTAINED_RESULT = 200;

    private static final int MAX_GENERAL_OBSERVATIONS = 200;

    private static final int MAX_DIGITS = 3;

    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024;

    private File selectedEvidenceFile;

    private StudentDTO student;
    private String projectId;
    private ObservableList<ActivityReportDTO> activityReports = FXCollections.observableArrayList();

    private static final Logger LOGGER = Logger.getLogger(GUI_RegisterReportController.class.getName());

    @FXML
    private void initialize() {
        configureTextFormatters();
        configureCharCountLabels();
        configureActivitiesTable();
        loadActivitiesComboBox();
    }

    private void configureTextFormatters() {
        progressPercentageField.setTextFormatter(createNumericTextFormatter(MAX_DIGITS));
        totalHoursField.setTextFormatter(createNumericTextFormatter(MAX_DIGITS));
        progressPercentageField.setPromptText("0-100");

        configureTextAreaFormatter(observationsArea, MAX_OBSERVATIONS);
        configureTextAreaFormatter(generalObjectiveArea, MAX_GENERAL_OBJECTIVES);
        configureTextAreaFormatter(methodologyArea, MAX_METHODOLOGIES);
        configureTextAreaFormatter(obtainedResultArea, MAX_OBTAINED_RESULT);
        configureTextAreaFormatter(generalObservationsArea, MAX_GENERAL_OBSERVATIONS);
    }

    private TextFormatter<String> createNumericTextFormatter(int maxDigits) {
        return new TextFormatter<>(change -> {
            String filtered = change.getControlNewText().replaceAll("[^\\d]", "");
            if (filtered.length() > maxDigits) {
                filtered = filtered.substring(0, maxDigits);
            }
            change.setText(filtered);
            change.setRange(0, change.getControlText().length());
            return change;
        });
    }

    private void configureTextAreaFormatter(TextArea textArea, int maxLength) {
        textArea.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= maxLength ? change : null
        ));
    }

    private void configureCharCountLabels() {
        configureCharCount(observationsArea, observationsCharCountLabel, MAX_OBSERVATIONS);
        configureCharCount(generalObjectiveArea, generalObjectiveCharCountLabel, MAX_GENERAL_OBJECTIVES);
        configureCharCount(methodologyArea, methodologyCharCountLabel, MAX_METHODOLOGIES);
        configureCharCount(obtainedResultArea, obtainedResultCharCountLabel, MAX_OBTAINED_RESULT);
        configureCharCount(generalObservationsArea, generalObservationsCharCountLabel, MAX_GENERAL_OBSERVATIONS);
    }

    private void configureCharCount(TextArea textArea, Label charCountLabel, int maxLength) {
        charCountLabel.setText("0/" + maxLength);
        textArea.textProperty().addListener((obs, oldText, newText) ->
                charCountLabel.setText(newText.length() + "/" + maxLength)
        );
    }

    private void configureActivitiesTable() {
        activityColumn.setCellValueFactory(cellData -> {
            String idActivity = cellData.getValue().getIdActivity();
            String name = idActivity;
            try {
                ActivityDAO activityDAO = new ActivityDAO();
                List<ActivityDTO> activities = activityDAO.getAllActivities();
                for (ActivityDTO act : activities) {
                    if (act.getActivityId().equals(idActivity)) {
                        name = act.getActivityName();
                        break;
                    }
                }
            } catch (SQLException e) {
                if (e.getMessage().contains("The driver has not received any packets from the server")) {
                    LOGGER.log(Level.SEVERE, "Error de conexión con la base de datos", e);
                    showAlert("Error de conexión con la base de datos al cargar actividades.");
                } else if (e.getMessage().contains("Unknown database")) {
                    LOGGER.log(Level.SEVERE, "La base de datos no está disponible", e);
                    showAlert("La base de datos no está disponible al cargar actividades.");
                } else {
                    LOGGER.log(Level.SEVERE, "Error de SQL al cargar actividades", e);
                    showAlert("Error de base de datos al cargar actividades.");
                }
                showAlert("Error de base de datos al cargar actividades.");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error inesperado al cargar actividades", e);
            }
            return new SimpleStringProperty(name);
        });
        progressColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getProgressPercentage()).asObject());
        observationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getObservations()));
        activitiesTable.setItems(activityReports);

        activitiesTable.setEditable(true);
        progressColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        progressColumn.setOnEditCommit(event -> {
            int value = event.getNewValue() != null ? event.getNewValue() : 0;
            if (value < 0) value = 0;
            if (value > 100) value = 100;
            event.getRowValue().setProgressPercentage(value);
            activitiesTable.refresh();
        });
        observationColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        observationColumn.setOnEditCommit(event -> {
            event.getRowValue().setObservations(event.getNewValue());
        });
    }

    private void loadActivitiesComboBox() {
        try {
            ActivityDAO activityDAO = new ActivityDAO();
            activityComboBox.setItems(FXCollections.observableArrayList(activityDAO.getAllActivities()));
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                showAlert("Error de conexión con la base de datos.");
                LOGGER.log(Level.SEVERE, "Error de SQL al conectarse al servidor", e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                showAlert("Conexión interrumpida con la base de datos.");
                LOGGER.log(Level.SEVERE, "Conexión interrumpida con la base de datos", e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                showAlert("Acceso denegado a la base de datos.");
                LOGGER.log(Level.SEVERE, "Acceso denegado a la base de datos", e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                showAlert("La base de datos no está disponible.");
                LOGGER.log(Level.SEVERE, "La base de datos no está disponible", e);
            } else if (sqlState != null && sqlState.equals("42S02")) {
                showAlert("Tabla de actividades encontrada.");
                LOGGER.log(Level.SEVERE, "Tabla no encontrada", e);
            } else {
                showAlert("Error de base de datos al cargar actividades.");
                LOGGER.log(Level.SEVERE, "Error de SQL al cargar actividades", e);
            }
        } catch (Exception e) {
            showAlert("Error inesperado al cargar actividades.");
            LOGGER.log(Level.SEVERE, "Error inesperado al cargar actividades", e);
        }
    }

    public void setReportContext(String professorName, String nrc, String period, String studentName, String organization, String projectName, String projectId, String tuiton) {
        this.projectId = projectId;
        careerField.setText("Licenciatura en Ingenieria de software");
        nrcField.setText(nrc);
        professorField.setText(professorName);
        schoolPeriodField.setText(period);
        studentField.setText(studentName);
        organizationField.setText(organization);
        projectField.setText(projectName);
        reportDateField.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
    }

    public void setStudent(StudentDTO student) {
        this.student = student;
    }

    @FXML
    public void handleRegisterActivity() {
        ActivityDTO selectedActivity = activityComboBox.getValue();
        String progressString = progressPercentageField.getText();
        String observations = observationsArea.getText();

        if (selectedActivity == null || progressString.isEmpty()) {
            showAlert("Selecciona una actividad y proporciona el porcentaje de avance.");
            return;
        }
        int progress;
        try {
            progress = Integer.parseInt(progressString);
            if (progress < 0 || progress > 100) {
                showAlert("El porcentaje de avance debe ser un número entre 0 y 100.");
                LOGGER.log(Level.WARNING, "Porcentaje de avance inválido: " + progressString);
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("El porcentaje de avance debe ser un número entre 0 y 100.");
            LOGGER.log(Level.WARNING, "Porcentaje de avance inválido: " + progressString, e);
            return;
        }

        for (ActivityReportDTO ar : activityReports) {
            if (ar.getIdActivity().equals(selectedActivity.getActivityId())) {
                showAlert("Ya agregaste esta actividad.");
                return;
            }
        }

        activityReports.add(new ActivityReportDTO("", selectedActivity.getActivityId(), progress, observations));
        activitiesTable.refresh();

        progressPercentageField.clear();
        observationsArea.clear();
        activityComboBox.getSelectionModel().clearSelection();
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
                showAlert("El archivo no debe superar los 20 MB.");
                return;
            }
            selectedEvidenceFile = file;
            evidenceFileTextField.setText(file.getAbsolutePath());
        }
    }

    private String uploadEvidenceToDrive(File file) {
        try {
            String idPeriod = getIdPeriod();
            String parentId = createDriveFolders(idPeriod);
            return uploadFile(file.getAbsolutePath(), parentId);
        } catch (UnknownHostException e) {
            showAlert("Error de conexión a Internet al subir a Google Drive. Verifica tu conexión y vuelve a intentarlo.");
            LOGGER.log(Level.SEVERE, "UnknownHostException al subir archivo a Drive", e);
            return "";
        } catch (SocketTimeoutException e) {
            showAlert("Tiempo de espera agotado al intentar subir el archivo a Google Drive. Verifica tu conexión a Internet.");
            LOGGER.log(Level.SEVERE, "SocketTimeoutException al subir archivo a Drive", e);
            return "";
        } catch (FileNotFoundException e) {
            showAlert("Archivo no encontrado al intentar subir a Google Drive.");
            LOGGER.log(Level.SEVERE, "FileNotFoundException al subir archivo a Drive", e);
            return "";
        } catch (GoogleJsonResponseException e) {
            showAlert("Error de Google Drive al subir el archivo.");
            LOGGER.log(Level.SEVERE, "GoogleJsonResponseException al subir archivo a Drive", e);
            return "";
        } catch (IOException e) {
            showAlert("Error de acceso al archivo al subir a Google Drive.");
            LOGGER.log(Level.SEVERE, "IOException al subir archivo a Drive", e);
            return "";
        } catch (GeneralSecurityException e) {
            showAlert("Error de seguridad al conectar con Google Drive.");
            LOGGER.log(Level.SEVERE, "GeneralSecurityException al subir archivo a Drive", e);
            return "";
        } catch (Exception e) {
            showAlert("Error inesperado al subir el archivo a Google Drive.");
            LOGGER.log(Level.SEVERE, "Error inesperado al subir archivo a Drive", e);
            return "";
        }
    }

    private String createDriveFolders(String idPeriod) {
        try {
            String parentId = null;
            parentId = createOrGetFolder(idPeriod, parentId);
            parentId = createOrGetFolder(student.getNRC(), parentId);
            parentId = createOrGetFolder(student.getTuition(), parentId);
            parentId = createOrGetFolder("Reporte", parentId);
            return parentId;
        } catch (UnknownHostException e) {
            showAlert("Error de conexión a Internet al crear folders para drive. Verifica tu conexión y vuelve a intentarlo.");
            LOGGER.log(Level.SEVERE, "UnknownHostException al crear carpeta en Drive", e);
            return "";
        } catch (SocketTimeoutException e) {
            showAlert("Tiempo de espera agotado al intentar crear carpetas en Google Drive. Verifica tu conexión a Internet.");
            LOGGER.log(Level.SEVERE, "SocketTimeoutException al crear carpeta en Drive", e);
            return "";
        } catch (GoogleJsonResponseException e) {
            showAlert("Error de Google Drive al interactuar con las carpetas.");
            LOGGER.log(Level.SEVERE, "GoogleJsonResponseException al crear carpeta en Drive", e);
            return "";
        } catch (IOException e) {
            showAlert("Error de acceso a las carpetas de Google Drive.");
            LOGGER.log(Level.SEVERE, "IOException al subir archivo a Drive", e);
            return "";
        } catch (GeneralSecurityException e) {
            showAlert("Error de seguridad al conectar con Google Drive.");
            LOGGER.log(Level.SEVERE, "GeneralSecurityException al crear carpeta en Drive", e);
            return "";
        } catch (Exception e) {
            showAlert("Error inesperado al crear carpetas en Google Drive.");
            LOGGER.log(Level.SEVERE, "Error inesperado al crear carpeta en Drive", e);
            return "";
        }
    }

    private int getNextEvidenceId() {
        try {
            EvidenceDAO evidenceDAO = new EvidenceDAO();
            return evidenceDAO.getNextEvidenceId();
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                showAlert("Error de conexión con la base de datos.");
                LOGGER.log(Level.SEVERE, "Error de SQL al conectarse al servidor", e);
                return -1;
            } else if (sqlState != null && sqlState.equals("08S01")) {
                showAlert("Conexión interrumpida con la base de datos.");
                LOGGER.log(Level.SEVERE, "Conexión interrumpida con la base de datos", e);
                return -1;
            } else if (sqlState != null && sqlState.equals("28000")) {
                showAlert("Acceso denegado a la base de datos.");
                LOGGER.log(Level.SEVERE, "Acceso denegado a la base de datos", e);
                return -1;
            } else if (sqlState != null && sqlState.equals("42000")) {
                showAlert("La base de datos no está disponible.");
                LOGGER.log(Level.SEVERE, "La base de datos no está disponible", e);
                return -1;
            } else if (sqlState != null && sqlState.equals("42S22")) {
                showAlert("Columna de ID de evidencia no encontrada.");
                LOGGER.log(Level.SEVERE, "Columna no encontrada", e);
                return -1;
            } else if (sqlState != null && sqlState.equals("HY000")) {
                showAlert("Error general de base de datos al obtener el ID de evidencia.");
                LOGGER.log(Level.SEVERE, "Error general de SQL al obtener el ID de evidencia", e);
                return -1;
            } else if (sqlState != null && sqlState.equals("42S02")) {
                showAlert("Tabla de evidencias no encontrada.");
                LOGGER.log(Level.SEVERE, "Tabla no encontrada", e);
                return -1;
            } else {
                showAlert("Error de base de datos al obtener el ID de evidencia.");
                LOGGER.log(Level.SEVERE, "Error de SQL al obtener el ID de evidencia", e);
                return -1;
            }
        } catch (IOException e) {
            showAlert("Error al leer la configuración de la base de datos.");
            LOGGER.log(Level.SEVERE, "Error al leer la configuración de la base de datos", e);
            return -1;
        } catch (Exception e) {
            showAlert("Error inesperado al obtener el ID de evidencia.");
            LOGGER.log(Level.SEVERE, "Error al obtener el ID de evidencia", e);
            return -1;
        }
    }

    private boolean insertEvidenceToDatabase(int id, String fileName, String driveUrl) {
        try {
            EvidenceDAO evidenceDAO = new EvidenceDAO();
            EvidenceDTO evidence = new EvidenceDTO(id, fileName, new Date(), driveUrl);
            evidenceDAO.insertEvidence(evidence);
            return true;
        } catch (SQLException e) {
           String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                showAlert("Error de conexión con la base de datos.");
                LOGGER.log(Level.SEVERE, "Error de SQL al conectarse al servidor", e);
                return false;
            } else if (sqlState != null && sqlState.equals("08S01")) {
                showAlert("Conexión interrumpida con la base de datos.");
                LOGGER.log(Level.SEVERE, "Conexión interrumpida con la base de datos", e);
                return false;
            } else if (sqlState != null && sqlState.equals("28000")) {
                showAlert("Acceso denegado a la base de datos.");
                LOGGER.log(Level.SEVERE, "Acceso denegado a la base de datos", e);
                return false;
            } else if (sqlState != null && sqlState.equals("42000")) {
                showAlert("La base de datos no está disponible.");
                LOGGER.log(Level.SEVERE, "La base de datos no está disponible", e);
                return false;
            } else if (sqlState != null && sqlState.equals("42S02")) {
                showAlert("Tabla de evidencias no encontrada.");
                LOGGER.log(Level.SEVERE, "Tabla no encontrada", e);
                return false;
            } else if (sqlState != null && sqlState.equals("42S22")) {
                showAlert("Columna de ID de evidencia no encontrada.");
                LOGGER.log(Level.SEVERE, "Columna no encontrada", e);
                return false;
            } else if (sqlState != null && sqlState.equals("22001")) {
                showAlert("El nombre del archivo es demasiado largo.");
                LOGGER.log(Level.WARNING, "Nombre de archivo demasiado largo", e);
                return false;
            } else if (sqlState != null && sqlState.equals("HY000")) {
                showAlert("Error general de base de datos al insertar evidencia.");
                LOGGER.log(Level.SEVERE, "Error general de SQL al insertar evidencia", e);
                return false;
            } else if (sqlState != null && sqlState.equals("23000")) {
                showAlert("Ya existe una evidencia con el mismo ID.");
                LOGGER.log(Level.WARNING, "Evidencia con ID ya existe", e);
                return false;
            } else {
                showAlert("Error de base de datos al insertar evidencia.");
                LOGGER.log(Level.SEVERE, "Error de SQL al insertar evidencia", e);
                return false;
            }
        } catch (IOException e) {
            showAlert("Error al leer la configuración de la base de datos.");
            LOGGER.log(Level.SEVERE, "Error al leer la configuración de la base de datos", e);
            return false;
        } catch (Exception e) {
            showAlert("Error inesperado al insertar evidencia.");
            LOGGER.log(Level.SEVERE, "Error inesperado al insertar evidencia", e);
            return false;
        }
    }

    // Java
    @FXML
    public void handleRegisterReport() {
        if (!validateReportFields()) return;
        if (!validateActivities()) return;
        if (!validateEvidenceFile()) return;
        int totalHours = Integer.parseInt(totalHoursField.getText());
        if (!validateReportedHours(totalHours)) return;
        int evidenceId = getNextEvidenceId();
        if (evidenceId == -1) return;
        String driveUrl = uploadEvidenceToDrive(selectedEvidenceFile);
        if (driveUrl == null || driveUrl.isEmpty()) return;
        if (!insertEvidenceToDatabase(evidenceId, selectedEvidenceFile.getName(), driveUrl)) return;
        if (!insertReportAndActivities(evidenceId, totalHours)) return;
        showAlert("Informe y actividades registrados correctamente.");
        clearForm();
    }

    private boolean validateReportFields() {
        if (totalHoursField.getText().isEmpty() ||
                generalObjectiveArea.getText().isEmpty() ||
                methodologyArea.getText().isEmpty() ||
                obtainedResultArea.getText().isEmpty()) {
            showAlert("Completa todos los campos obligatorios del informe.");
            return false;
        }
        return true;
    }

    private boolean validateActivities() {
        if (activityReports.isEmpty()) {
            showAlert("Agrega al menos una actividad al informe.");
            return false;
        }
        return true;
    }

    private boolean validateEvidenceFile() {
        if (selectedEvidenceFile == null) {
            showAlert("Selecciona un archivo de evidencia.");
            return false;
        }
        return true;
    }

    private boolean validateReportedHours(int totalHours) {
        ReportDAO reportDAO = new ReportDAO();
        try {
            int reportedHours = reportDAO.getTotalReportedHoursByStudent(student.getTuition());
            if (reportedHours + totalHours > 420) {
                showAlert("El alumno ya ha cumplido las 420 horas requeridas o las superaría con este informe.");
                return false;
            }
        } catch (SQLException e) {
            handleSQLException(e, "verificar horas reportadas");
            return false;
        } catch (IOException e) {
            showAlert("Error al leer la configuracion de la base de datos.");
            LOGGER.log(Level.SEVERE, "Error al leer la configuracion de la base de datos ", e);
            return false;
        } catch (Exception e) {
            showAlert("Error inesperado al verificar horas reportadas.");
            LOGGER.log(Level.SEVERE, "Error inesperado al verificar horas reportadas", e);
            return false;
        }
        return true;
    }

    private boolean insertReportAndActivities(int evidenceId, int totalHours) {
        try {
            ReportDTO report = new ReportDTO(
                    "0",
                    new Date(),
                    totalHours,
                    generalObjectiveArea.getText(),
                    methodologyArea.getText(),
                    obtainedResultArea.getText(),
                    Integer.parseInt(projectId),
                    student.getTuition(),
                    generalObservationsArea.getText(),
                    String.valueOf(evidenceId)
            );
            ReportDAO reportDAO = new ReportDAO();
            boolean inserted = reportDAO.insertReport(report);
            if (!inserted) {
                showAlert("No se pudo registrar el informe.");
                LOGGER.log(Level.WARNING, "No se pudo insertar el informe en la base de datos.");
                return false;
            }
            String numReporte = report.getNumberReport();
            ActivityReportDAO activityReportDAO = new ActivityReportDAO();
            for (ActivityReportDTO ar : activityReports) {
                ar.setNumberReport(numReporte);
                try {
                    activityReportDAO.insertActivityReport(ar);
                } catch (SQLException e) {
                    handleSQLException(e, "registrar actividad");
                } catch (Exception e) {
                    showAlert("Error inesperado al registrar una actividad.");
                    LOGGER.log(Level.SEVERE, "Error inesperado al registrar actividad", e);
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Las horas totales deben ser un número.");
            LOGGER.log(Level.WARNING, "Formato inválido en horas totales", e);
            return false;
        } catch (SQLException e) {
            handleSQLException(e, "registrar el reporte");
            return false;
        } catch (IOException e) {
            showAlert("Error al leer el archivo de configuración de la base de datos.");
            LOGGER.log(Level.WARNING, "Error al leer el archivo de configuración de la base de datos", e);
            return false;
        } catch (Exception e) {
            showAlert("Error inesperado al registrar el informe.");
            LOGGER.log(Level.SEVERE, "Error inesperado al registrar informe", e);
            return false;
        }
        return true;
    }

    private void handleSQLException(SQLException e, String context) {
        String sqlState = e.getSQLState();
        if (sqlState != null && sqlState.equals("08001")) {
            showAlert("Error de conexión con la base de datos.");
            LOGGER.log(Level.SEVERE, "Error de SQL al " + context, e);
        } else if (sqlState != null && sqlState.equals("08S01")) {
            showAlert("Conexión interrumpida con la base de datos.");
            LOGGER.log(Level.SEVERE, "Conexión interrumpida con la base de datos al " + context, e);
        } else if (sqlState != null && sqlState.equals("28000")) {
            showAlert("Acceso denegado a la base de datos.");
            LOGGER.log(Level.SEVERE, "Acceso denegado a la base de datos al " + context, e);
        } else if (sqlState != null && sqlState.equals("42000")) {
            showAlert("La base de datos no está disponible.");
            LOGGER.log(Level.SEVERE, "La base de datos no está disponible al " + context, e);
        } else if (sqlState != null && sqlState.equals("42S02")) {
            showAlert("Tabla no encontrada.");
            LOGGER.log(Level.SEVERE, "Tabla no encontrada al " + context, e);
        } else if (sqlState != null && sqlState.equals("42S22")) {
            showAlert("Columna no encontrada.");
            LOGGER.log(Level.SEVERE, "Columna no encontrada al " + context, e);
        } else if (sqlState != null && sqlState.equals("HY000")) {
            showAlert("Error general de base de datos.");
            LOGGER.log(Level.SEVERE, "Error general de SQL al " + context, e);
        } else {
            showAlert("Error de base de datos al " + context + ".");
            LOGGER.log(Level.SEVERE, "Error de SQL al " + context, e);
        }
    }

    @FXML
    public void handleRegisterNewActivity() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_ManageActivity.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gestión de Actividades");
            stage.setScene(new Scene(root));
            stage.setOnHiding(event -> {
                reloadActivitiesComboBox();
            });
            stage.show();
        } catch  (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al leer el fxml para cargar la ventana de actividades: {}", e);
            showAlert("Error al leer el fxml para abrir la ventana de actividades.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al abrir la ventana de actividades: {}", e);
            showAlert("Error inesperado al abrir la ventana de actividades.");
        }
    }

    private void reloadActivitiesComboBox() {
        try {
            ActivityDAO activityDAO = new ActivityDAO();
            activityComboBox.setItems(FXCollections.observableArrayList(activityDAO.getAllActivities()));
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                showAlert("Error de conexión con la base de datos al recargar las actividades.");
                LOGGER.log(Level.SEVERE, "Error de SQL al conectarse al servidor", e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                showAlert("Conexión interrumpida con la base de datos al recargar las actividades.");
                LOGGER.log(Level.SEVERE, "Conexión interrumpida con la base de datos", e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                showAlert("Acceso denegado a la base de datos al recargar las actividades.");
                LOGGER.log(Level.SEVERE, "Acceso denegado a la base de datos", e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                showAlert("La base de datos no está disponible al recargar las actividades.");
                LOGGER.log(Level.SEVERE, "La base de datos no está disponible", e);
            } else if (sqlState != null && sqlState.equals("42S02")) {
                showAlert("Tabla de actividades no encontrada al recargar las actividades.");
                LOGGER.log(Level.SEVERE, "Tabla no encontrada", e);
            } else if (sqlState != null && sqlState.equals("42S22")) {
                showAlert("Columna de actividad no encontrada al recargar las actividades.");
                LOGGER.log(Level.SEVERE, "Columna no encontrada", e);
            } else if (sqlState != null && sqlState.equals("HY000")) {
                showAlert("Error general de base de datos al recargar las actividades.");
                LOGGER.log(Level.SEVERE, "Error general de SQL al recargar las actividades", e);
            } else {
                showAlert("Error de base de datos al recargar las actividades.");
                LOGGER.log(Level.SEVERE, "Error de SQL al recargar las actividades", e);
            }
        } catch (IOException e) {
            showAlert("Error al leer el archivo de configuración de la base de datos al recargar las actividades.");
            LOGGER.log(Level.WARNING, "Error al leer el archivo de configuración de la base de datos", e);
        } catch (Exception e) {
            showAlert("Error inesperado al recargar actividades.");
            LOGGER.log(Level.SEVERE, "Error inesperado al recargar actividades", e);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }

    private void clearForm() {
        totalHoursField.clear();
        generalObjectiveArea.clear();
        methodologyArea.clear();
        obtainedResultArea.clear();
        generalObservationsArea.clear();
        activityReports.clear();
    }

    private String getIdPeriod() {
        try {
            logic.DAO.GroupDAO groupDAO = new logic.DAO.GroupDAO();
            logic.DTO.GroupDTO group = groupDAO.searchGroupById(student.getNRC());
            return (group != null && group.getIdPeriod() != null) ? group.getIdPeriod() : "PeriodoDesconocido";
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                showAlert("Error de conexión con la base de datos al obtener el periodo del grupo.");
                LOGGER.log(Level.SEVERE, "Error de conexión con la base de datos al obtener el periodo del grupo", e);
                return "PeriodoDesconocido";
            } else if (sqlState != null && sqlState.equals("08S01")) {
                showAlert("Conexión interrumpida con la base de datos al obtener el periodo del grupo.");
                LOGGER.log(Level.SEVERE, "Conexión interrumpida con la base de datos al obtener el periodo del grupo", e);
                return "PeriodoDesconocido";
            } else if (sqlState != null && sqlState.equals("28000")) {
                showAlert("Acceso denegado a la base de datos al obtener el periodo del grupo.");
                LOGGER.log(Level.SEVERE, "Acceso denegado a la base de datos al obtener el periodo del grupo", e);
                return "PeriodoDesconocido";
            } else if (sqlState != null && sqlState.equals("42000")) {
                showAlert("La base de datos no está disponible al obtener el periodo del grupo.");
                LOGGER.log(Level.SEVERE, "La base de datos no está disponible al obtener el periodo del grupo", e);
                return "PeriodoDesconocido";
            } else if (sqlState != null && sqlState.equals("42S02")) {
                showAlert("Tabla de grupos no encontrada al obtener el periodo del grupo.");
                LOGGER.log(Level.SEVERE, "Tabla de grupos no encontrada al obtener el periodo del grupo", e);
                return "PeriodoDesconocido";
            } else if (sqlState != null && sqlState.equals("42S22")) {
                showAlert("Columna de periodo no encontrada al obtener el periodo del grupo.");
                LOGGER.log(Level.SEVERE, "Columna de periodo no encontrada al obtener el periodo del grupo", e);
                return "PeriodoDesconocido";
            } else if (sqlState != null && sqlState.equals("HY000")) {
                showAlert("Error general de base de datos al obtener el periodo del grupo.");
                LOGGER.log(Level.SEVERE, "Error general de SQL al obtener el periodo del grupo", e);
                return "PeriodoDesconocido";
            } else {
                showAlert("Error de base de datos al obtener el periodo del grupo.");
                LOGGER.log(Level.SEVERE, "Error de SQL al obtener el periodo del grupo", e);
                return "PeriodoDesconocido";
            }
        } catch (IOException e) {
            showAlert("Error al leer el archivo de configuracion de base de datos.");
            LOGGER.log(Level.WARNING, "Error al leer el archivo de configuracion de base de datos", e);
            return "PeriodoDesconocido";
        } catch (Exception e) {
            showAlert("Error inesperado al obtener el periodo del grupo.");
            LOGGER.log(Level.WARNING, "No se pudo obtener el periodo del grupo", e);
            return "PeriodoDesconocido";
        }
    }
}