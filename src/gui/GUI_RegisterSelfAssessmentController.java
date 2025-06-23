package gui;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import logic.DAO.ProjectDAO;
import logic.DAO.SelfAssessmentDAO;
import logic.DAO.CriterionSelfAssessmentDAO;
import logic.DAO.EvidenceDAO;
import logic.DAO.SelfAssessmentCriteriaDAO;
import logic.DTO.ProjectDTO;
import logic.DTO.SelfAssessmentDTO;
import logic.DTO.CriterionSelfAssessmentDTO;
import logic.DTO.EvidenceDTO;
import logic.DTO.StudentDTO;
import logic.DTO.SelfAssessmentCriteriaDTO;

import java.io.FileNotFoundException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;

import static logic.drive.GoogleDriveFolderCreator.createOrGetFolder;
import static logic.drive.GoogleDriveUploader.uploadFile;

import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gui.CriterionInput;

public class GUI_RegisterSelfAssessmentController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_RegisterSelfAssessmentController.class);

    @FXML
    private ComboBox<ProjectDTO> projectComboBox;
    @FXML
    private TextField evidenceFileTextField;
    @FXML
    private VBox criteriaVBox;
    @FXML
    private TextArea generalCommentsTextArea;
    @FXML
    private Button registerButton;
    @FXML
    private Label statusLabel, commentsCharCountLabel;

    private File selectedEvidenceFile;
    private StudentDTO student;
    private ProjectDTO assignedProject;
    private List<CriterionInput> criterionInputs = new ArrayList<>();
    private static final int MAX_COMMENTS_LENGTH = 500;
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024;

    @FXML
    public void initialize() {
        loadProjects();
        configureTextFormatters();
        configureCharCount();
        loadCriteria();
    }

    private void configureTextFormatters() {
        generalCommentsTextArea.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= MAX_COMMENTS_LENGTH ? change : null
        ));
    }

    private void configureCharCount() {
        generalCommentsTextArea.textProperty().addListener((observable, oldText, newText) ->
                commentsCharCountLabel.setText(newText.length() + "/500")
        );
    }


    public void setStudentAndProject(StudentDTO student, ProjectDTO project) {
        this.student = student;
        this.assignedProject = project;
        if (project != null) {
            projectComboBox.getItems().clear();
            projectComboBox.getItems().add(project);
            projectComboBox.getSelectionModel().select(project);
            projectComboBox.setDisable(true);
        } else {
            projectComboBox.setDisable(false);
            loadProjects();
        }
    }

    private void loadProjects() {
        try {
            ProjectDAO projectDAO = new ProjectDAO();
            List<ProjectDTO> projects = projectDAO.getAllProjects();
            projectComboBox.getItems().setAll(projects);
        } catch (SQLException e){
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if ("08S01".equals(sqlState)) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos no encontradaL: {}", e.getMessage(), e);
            } else if ("42S02".equals(sqlState)) {
                statusLabel.setText("Tabla o vista no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla o vista no encontrada: {}", e.getMessage(), e);
            } else if ("42S22".equals(sqlState)) {
                statusLabel.setText("Columna no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada: {}", e.getMessage(), e);
            } else if ("HY000".equals(sqlState)) {
                statusLabel.setText("Error general de la base de datos al cargar proyectos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error general de la base de datos al cargar proyectos: {}", e.getMessage(), e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al cargar proyectos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al cargar proyectos: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            showError("Error al leer la configuración de la base de datos.");
            LOGGER.error("Error al leer la configuración de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            showError("Error inesperado al cargar proyectos.");
            LOGGER.error("Error inesperado al cargar proyectos: {}", e.getMessage(), e);
        }
    }

    private void configureCriterionInputs() {
        for (CriterionInput input : criterionInputs) {
            input.gradeField.setTextFormatter(new TextFormatter<>(change -> {
                String newText = change.getControlNewText();
                if (newText.matches("\\d{1,2}")) {
                    try {
                        int value = Integer.parseInt(newText);
                        if (value >= 1 && value <= 10) {
                            return change;
                        }
                    } catch (NumberFormatException e) {
                        LOGGER.error("Error al parsear la calificación: {}", e.getMessage(), e);
                    }
                }
                change.setText("");
                return change;
            }));
        }
    }

    private void loadCriteria() {
        try {
            SelfAssessmentCriteriaDAO criteriaDAO = new SelfAssessmentCriteriaDAO();
            List<SelfAssessmentCriteriaDTO> criteriaList = criteriaDAO.getAllSelfAssessmentCriteria();
            criteriaVBox.getChildren().clear();
            criterionInputs.clear();
            for (SelfAssessmentCriteriaDTO crit : criteriaList) {
                CriterionInput input = new CriterionInput(crit.getIdCriteria(), crit.getNameCriteria());
                criterionInputs.add(input);
                criteriaVBox.getChildren().add(input.toHBox());
            }
            configureCriterionInputs();
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if ("08S01".equals(sqlState)) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos no encontrada: {}", e.getMessage(), e);
            } else if ("42S02".equals(sqlState)) {
                statusLabel.setText("Tabla o vista no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla o vista no encontrada: {}", e.getMessage(), e);
            } else if ("42S22".equals(sqlState)) {
                statusLabel.setText("Columna no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada: {}", e.getMessage(), e);
            } else if ("HY000".equals(sqlState)) {
                statusLabel.setText("Error general de la base de datos al cargar criterios.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error general de la base de datos al cargar criterios: {}", e.getMessage(), e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if ("23000".equals(sqlState)) {
                statusLabel.setText("Violación de restricción de integridad.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al cargar criterios.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al cargar criterios: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            showError("Error al leer la configuración de la base de datos.");
            LOGGER.error("Error al leer la configuración de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            showError("Error al cargar criterios.");
            LOGGER.error("Error al cargar criterios: {}", e.getMessage(), e);
        }
    }


    @FXML
    private void handleSelectEvidenceFile(javafx.event.ActionEvent event) {
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
                showError("Solo se permiten archivos PDF, imágenes (JPG, PNG) o documentos DOCX.");
                return;
            }
            if (file.length() > MAX_FILE_SIZE) {
                showError("El archivo no puede ser mayor a 20 MB.");
                return;
            }
            selectedEvidenceFile = file;
            evidenceFileTextField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void handleRegisterSelfAssessment(javafx.event.ActionEvent event) {
        statusLabel.setText("");
        if (!validateInputs()) return;

        int evidenceId = saveEvidenceFile(selectedEvidenceFile);
        if (evidenceId == -1) return;

        float averageGrade = calculateAverageGrade();
        String generalComments = generalCommentsTextArea.getText();

        int selfAssessmentId = saveSelfAssessment(evidenceId, averageGrade, generalComments);
        if (selfAssessmentId == -1) return;

        boolean criteriaSaved = saveCriteria(selfAssessmentId);
        if (!criteriaSaved) return;

        statusLabel.setText("Autoevaluación registrada correctamente.");
        clearForm();
    }

    private boolean validateInputs() {
        ProjectDTO selectedProject = assignedProject != null ? assignedProject : projectComboBox.getValue();
        if (selectedProject == null) {
            showError("Selecciona un proyecto.");
            return false;
        }
        if (selectedEvidenceFile == null) {
            showError("Selecciona un archivo de evidencia.");
            return false;
        }
        for (CriterionInput input : criterionInputs) {
            if (input.getGrade() == null || input.getGrade().isEmpty()) {
                showError("Completa todas las calificaciones.");
                return false;
            }
            try {
                Float.parseFloat(input.getGrade());
            } catch (NumberFormatException e) {
                showError("Las calificaciones deben ser números válidos.");
                LOGGER.error("Calificación inválida para el criterio: {}", input.idCriteria, e);
                return false;
            } catch (Exception e) {
                showError("Error inesperado al validar las calificaciones.");
                LOGGER.error("Error inesperado al validar las calificaciones: {}", e.getMessage(), e);
                return false;
            }
        }
        return true;
    }

    private float calculateAverageGrade() {
        float sum = 0f;
        int count = 0;
        for (CriterionInput input : criterionInputs) {
            try {
                float grade = Float.parseFloat(input.getGrade());
                sum += grade;
                count++;
            } catch (NumberFormatException e){
                showError("Calificación inválida para el criterio: " + input.idCriteria);
                LOGGER.error("Calificación inválida para el criterio: " + input.idCriteria, e);
                return 0f;
            }catch (Exception e) {
                showError("Error inesperado al calcular la calificación promedio.");
                LOGGER.error("Error inesperado al calcular la calificación promedio", e);
                return 0f;
            }
        }
        return count > 0 ? (float) Math.round((sum / count) * 100) / 100 : 0f;
    }

    private int saveEvidenceFile(File file) {
        int nextId = getNextEvidenceId();
        if (nextId == -1) return -1;

        String driveUrl = uploadEvidenceToDrive(file);
        if (driveUrl == null) return -1;

        if (!insertEvidenceToDatabase(nextId, file.getName(), driveUrl)) return -1;

        return nextId;
    }

    private int getNextEvidenceId() {
        try {
            EvidenceDAO evidenceDAO = new EvidenceDAO();
            return evidenceDAO.getNextEvidenceId();
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                return -1;
            } else if ("08S01".equals(sqlState)) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                return -1;
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos no encontrada: {}", e.getMessage(), e);
                return -1;
            } else if ("42S02".equals(sqlState)) {
                statusLabel.setText("Tabla o vista no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla o vista no encontrada: {}", e.getMessage(), e);
                return -1;
            } else if ("42S22".equals(sqlState)) {
                statusLabel.setText("Columna no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada: {}", e.getMessage(), e);
                return -1;
            } else if ("HY000".equals(sqlState)) {
                statusLabel.setText("Error general de la base de datos al obtener el ID de evidencia.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error general de la base de datos al obtener el ID de evidencia: {}", e.getMessage(), e);
                return -1;
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                return -1;
            } else if ("23000".equals(sqlState)) {
                statusLabel.setText("Violación de restricción de integridad.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
                return -1;
            } else {
                showError("Error al obtener el ID de evidencia.");
                LOGGER.error("Error al obtener el ID de evidencia: {}", e.getMessage(), e);
                return -1;
            }
        } catch (IOException e) {
            showError("Error al leer la configuración de la base de datos.");
            LOGGER.error("Error al leer la configuración de la base de datos: {}", e.getMessage(), e);
            return -1;
        }
        catch (Exception e) {
            showError("Error al obtener el ID de evidencia.");
            LOGGER.error("Error al obtener el ID de evidencia", e);
            return -1;
        }
    }

    private String uploadEvidenceToDrive(File file) {
        try {
            String idPeriod = getIdPeriod();
            String parentId = createDriveFolders(idPeriod);
            return uploadFile(file.getAbsolutePath(), parentId);
        } catch (UnknownHostException e) {
            showError("Error de conexión a Internet. Verifica tu conexión.");
            LOGGER.error("UnknownHostException al subir archivo a Drive", e);
            return "";
        } catch (SocketTimeoutException e) {
            showError("Tiempo de espera agotado al intentar subir el archivo a Google Drive.");
            LOGGER.error("SocketTimeoutException al subir archivo a Drive", e);
            return "";
        } catch (FileNotFoundException e) {
            showError("Archivo de credenciales no encontrado. Verifica la configuración.");
            LOGGER.error("FileNotFoundException al subir archivo a Drive", e);
            return "";
        } catch (GoogleJsonResponseException e) {
            showError("Error de Google Drive al subir el archivo.");
            LOGGER.error("GoogleJsonResponseException al subir archivo a Drive", e);
            return "";
        } catch (IOException e) {
            showError("Error de acceso al archivo al subir a Google Drive.");
            LOGGER.error("IOException al subir archivo a Drive", e);
            return "";
        } catch (GeneralSecurityException e) {
            showError("Error de seguridad al conectar con Google Drive.");
            LOGGER.error("GeneralSecurityException al subir archivo a Drive", e);
            return "";
        } catch (Exception e) {
            showError("Error inesperado al subir el archivo a Google Drive.");
            LOGGER.error("Error inesperado al subir archivo a Drive", e);
            return "";
        }
    }

    private String createDriveFolders(String idPeriod) {
        try {
            String parentId = null;
            parentId = createOrGetFolder(idPeriod, parentId);
            parentId = createOrGetFolder(student.getNRC(), parentId);
            parentId = createOrGetFolder(student.getTuition(), parentId);
            parentId = createOrGetFolder("Autoevaluacion", parentId);
            return parentId;
        } catch (UnknownHostException e) {
            showError("Error de conexión a Internet. Verifica tu conexión.");
            LOGGER.error("UnknownHostException al crear carpetas en Drive", e);
            return "";
        } catch (SocketTimeoutException e) {
            showError("Tiempo de espera agotado al intentar conectar con Google Drive.");
            LOGGER.error("SocketTimeoutException al crear carpetas en Drive", e);
            return "";
        } catch (FileNotFoundException e) {
            showError("Archivo de credenciales no encontrado. Verifica la configuración.");
            LOGGER.error("FileNotFoundException al crear carpetas en Drive", e);
            return "";
        } catch (GoogleJsonResponseException e) {
            showError("Error de Google Drive al interactuar con las carpetas.");
            LOGGER.error("GoogleJsonResponseException al crear carpetas en Drive", e);
            return "";
        } catch (IOException e) {
            showError("Error de acceso a las carpetas de Google Drive.");
            LOGGER.error("IOException al crear carpetas en Drive", e);
            return "";
        } catch (GeneralSecurityException e) {
            showError("Error de seguridad al conectar con Google Drive.");
            LOGGER.error("GeneralSecurityException al crear carpetas en Drive", e);
            return "";
        } catch (Exception e) {
            showError("Error inesperado al crear carpetas en Google Drive.");
            LOGGER.error("Error inesperado al crear carpetas en Drive", e);
            return "";
        }
    }

    private String getIdPeriod() {
        try {
            logic.DAO.GroupDAO groupDAO = new logic.DAO.GroupDAO();
            logic.DTO.GroupDTO group = groupDAO.searchGroupById(student.getNRC());
            return (group != null && group.getIdPeriod() != null) ? group.getIdPeriod() : "PeriodoDesconocido";
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                return "PeriodoDesconocido";
            } else if ("42S02".equals(sqlState)) {
                statusLabel.setText("Tabla o vista no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla o vista no encontrada: {}", e.getMessage(), e);
                return "PeriodoDesconocido";
            } else if ("42S22".equals(sqlState)) {
                statusLabel.setText("Columna no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada: {}", e.getMessage(), e);
                return "PeriodoDesconocido";
            } else if ("HY000".equals(sqlState)) {
                statusLabel.setText("Error general de la base de datos al obtener el periodo del grupo.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error general de la base de datos al obtener el periodo del grupo: {}", e.getMessage(), e);
                return "PeriodoDesconocido";
            } else if ("08S01".equals(sqlState)) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                return "PeriodoDesconocido";
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                return "PeriodoDesconocido";
            } else {
                showError("Error al obtener el periodo del grupo.");
                LOGGER.error("Error al obtener el periodo del grupo: {}", e.getMessage(), e);
                return "PeriodoDesconocido";
            }
        } catch (IOException e) {
            showError("Error al leer la configuración de la base de datos.");
            LOGGER.error("Error al leer la configuración de la base de datos: {}", e.getMessage(), e);
            return "PeriodoDesconocido";
        } catch (Exception e) {
            showError("Error inesperado al obtener el periodo del grupo.");
            LOGGER.error("Error inesperado al obtener el periodo del grupo: {}", e.getMessage(), e);
            return "PeriodoDesconocido";
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
            if ("08001".equals(sqlState)) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                return false;
            } else if ("08S01".equals(sqlState)) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                return false;
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos no encontrada: {}", e.getMessage(), e);
                return false;
            } else if ("42S02".equals(sqlState)) {
                statusLabel.setText("Tabla o vista no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla o vista no encontrada: {}", e.getMessage(), e);
                return false;
            } else if ("42S22".equals(sqlState)) {
                statusLabel.setText("Columna no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada: {}", e.getMessage(), e);
                return false;
            } else if ("HY000".equals(sqlState)) {
                statusLabel.setText("Error general de la base de datos al insertar evidencia.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error general de la base de datos al insertar evidencia: {}", e.getMessage(), e);
                return false;
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                return false;
            } else if ("23000".equals(sqlState)) {
                statusLabel.setText("Violación de restricción de integridad.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
                return false;
            } else {
                showError("Error al insertar evidencia en la base de datos.");
                LOGGER.error("Error al insertar evidencia en la base de datos: {}", e.getMessage(), e);
                return false;
            }
        } catch (IOException e) {
            showError("Error al leer la configuración de la base de datos.");
            LOGGER.error("Error al leer la configuración de la base de datos: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            showError("Error al insertar evidencia en la base de datos.");
            LOGGER.error("Error inesperado al insertar evidencia en la base de datos: {}", e.getMessage(), e);
            return false;
        }
    }

    private int saveSelfAssessment(int evidenceId, float averageGrade, String generalComments) {
        try {
            ProjectDTO selectedProject = assignedProject != null ? assignedProject : projectComboBox.getValue();
            SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                    0,
                    "",
                    averageGrade,
                    student != null ? student.getTuition() : "",
                    Integer.parseInt(selectedProject.getIdProject()),
                    evidenceId,
                    new Date(),
                    SelfAssessmentDTO.EstadoAutoevaluacion.COMPLETADA,
                    generalComments
            );
            SelfAssessmentDAO selfAssessmentDAO = new SelfAssessmentDAO();
            boolean inserted = selfAssessmentDAO.insertSelfAssessment(selfAssessment);
            if (!inserted) {
                showError("No se pudo registrar la autoevaluación.");
                return -1;
            }
            return selfAssessmentDAO.getLastSelfAssessmentId();
        } catch (NumberFormatException e) {
            showError("Error en el formato de los datos numéricos.");
            LOGGER.error("Error en el formato de los datos numéricos al registrar autoevaluación: {}", e.getMessage(), e);
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if ("08S01".equals(sqlState)) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos no encontrada: {}", e.getMessage(), e);
            } else if ("42S02".equals(sqlState)) {
                statusLabel.setText("Tabla o vista no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla o vista no encontrada: {}", e.getMessage(), e);
            } else if ("42S22".equals(sqlState)) {
                statusLabel.setText("Columna no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada: {}", e.getMessage(), e);
            } else if ("HY000".equals(sqlState)) {
                statusLabel.setText("Error general de la base de datos al registrar la autoevaluación.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error general de la base de datos al registrar autoevaluación: {}", e.getMessage(), e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if ("23000".equals(sqlState)) {
                statusLabel.setText("Violación de restricción de integridad.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            } else {
                showError("Error al registrar la autoevaluación.");
                LOGGER.error("Error al registrar autoevaluación: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            showError("Error al leer la configuración de la base de datos.");
            LOGGER.error("Error al leer la configuración de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            showError("Ocurrió un error inesperado al registrar la autoevaluación.");
            LOGGER.error("Error inesperado al registrar autoevaluación: {}", e.getMessage(), e);
        }
        return -1;
    }

    private boolean saveCriteria(int selfAssessmentId) {
        try {
            CriterionSelfAssessmentDAO criterionDAO = new CriterionSelfAssessmentDAO();
            for (CriterionInput input : criterionInputs) {
                CriterionSelfAssessmentDTO critDTO = new CriterionSelfAssessmentDTO(
                        selfAssessmentId,
                        Integer.parseInt(input.idCriteria),
                        Float.parseFloat(input.getGrade()),
                        input.getComments()
                );
                criterionDAO.insertCriterionSelfAssessment(critDTO);
            }
            return true;
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if ("08S01".equals(sqlState)) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos no encontrada: {}", e.getMessage(), e);
            } else if ("42S02".equals(sqlState)) {
                statusLabel.setText("Tabla o vista no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla o vista no encontrada: {}", e.getMessage(), e);
            } else if ("42S22".equals(sqlState)) {
                statusLabel.setText("Columna no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna no encontrada: {}", e.getMessage(), e);
            } else if ("HY000".equals(sqlState)) {
                statusLabel.setText("Error general de la base de datos al guardar criterios.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error general de la base de datos al guardar criterios: {}", e.getMessage(), e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if ("23000".equals(sqlState)) {
                statusLabel.setText("Violación de restricción de integridad.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al cargar criterios.");
                statusLabel.setTextFill(Color.RED);

            }
        } catch (NumberFormatException e){
            showError("Formato numérico inválido en las calificaciones.");
            LOGGER.error("Formato numérico inválido en las calificaciones: {}", e.getMessage(), e);
        } catch (IOException e) {
            showError("Error al leer la configuración de la base de datos.");
            LOGGER.error("Error al leer la configuración de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            showError("Ocurrió un error inesperado al guardar los criterios.");
            LOGGER.error("Error inesperado al guardar criterios: {}", e.getMessage(), e);
        }
        return false;
     }


    private void clearForm() {
        evidenceFileTextField.clear();
        selectedEvidenceFile = null;
        for (CriterionInput input : criterionInputs) {
            input.gradeField.clear();
            input.commentsField.clear();
        }
        generalCommentsTextArea.clear();
    }

    private void showError(String message) {
        statusLabel.setText(message);
    }
}