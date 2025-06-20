package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

import java.security.GeneralSecurityException;

import static logic.drive.GoogleDriveFolderCreator.createOrGetFolder;
import static logic.drive.GoogleDriveUploader.uploadFile;

import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import gui.CriterionInput;

public class GUI_RegisterSelfAssessmentController {

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
    private static final Logger LOGGER = Logger.getLogger(GUI_RegisterSelfAssessmentController.class.getName());

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
            showError("Error de base de datos al cargar proyectos.");
            LOGGER.log(Level.SEVERE, "Error de SQL al cargar proyectos", e);
        }catch (Exception e) {
            showError("Error al cargar proyectos.");
            LOGGER.log(Level.SEVERE, "Error al cargar proyectos", e);
        }
    }

    private void configureCriterionInputs() {
        for (CriterionInput input : criterionInputs) {
            input.gradeField.setTextFormatter(new TextFormatter<>(change -> {
                String newText = change.getControlNewText();
                if (newText.matches("\\d{1,2}")) { // Permite hasta dos dígitos
                    try {
                        int value = Integer.parseInt(newText);
                        if (value >= 1 && value <= 10) { // Valida el rango
                            return change;
                        }
                    } catch (NumberFormatException e) {
                        LOGGER.log(Level.WARNING, "Formato de calificación inválido", e);
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
            configureCriterionInputs(); // Llamada al método después de cargar los criterios
        } catch (SQLException e) {
            showError("Error de base de datos al cargar criterios.");
            LOGGER.log(Level.SEVERE, "Error de SQL al cargar criterios", e);
        } catch (Exception e) {
            showError("Error al cargar criterios.");
            LOGGER.log(Level.SEVERE, "Error al cargar criterios", e);
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
                LOGGER.log(Level.WARNING, "Calificación inválida para el criterio: " + input.idCriteria);
                return 0f;
            }catch (Exception e) {
                LOGGER.log(Level.WARNING, "Calificación inválida para el criterio: " + input.idCriteria);
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
            showError("Error de base de datos al obtener el ID de evidencia.");
            LOGGER.log(Level.SEVERE, "Error de SQL al obtener el ID de evidencia", e);
            return -1;
        } catch (Exception e) {
            showError("Error al obtener el ID de evidencia.");
            LOGGER.log(Level.SEVERE, "Error al obtener el ID de evidencia", e);
            return -1;
        }
    }

    private String uploadEvidenceToDrive(File file) {
        try {
            String idPeriod = getIdPeriod();
            String parentId = createDriveFolders(idPeriod);
            return uploadFile(file.getAbsolutePath(), parentId);
        } catch (IOException e) {
            showError("Error de acceso al archivo al subir a Google Drive.");
            LOGGER.log(Level.SEVERE, "IOException al subir archivo a Drive", e);
            return null;
        } catch (GeneralSecurityException e) {
            showError("Error al conectar con Google Drive.");
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
            parentId = createOrGetFolder("Autoevaluacion", parentId);
            return parentId;
        } catch (IOException e) {
            showError("Error de acceso a las carpetas de Google Drive.");
            LOGGER.log(Level.SEVERE, "IOException al subir archivo a Drive", e);
            return null;
        } catch (GeneralSecurityException e) {
            showError("Error al conectar con Google Drive.");
            LOGGER.log(Level.SEVERE, "GeneralSecurityException al crear carpeta en Drive", e);
            return null;
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

    private boolean insertEvidenceToDatabase(int id, String fileName, String driveUrl) {
        try {
            EvidenceDAO evidenceDAO = new EvidenceDAO();
            EvidenceDTO evidence = new EvidenceDTO(id, fileName, new Date(), driveUrl);
            evidenceDAO.insertEvidence(evidence);
            return true;
        } catch (SQLException e) {
            showError("Error de base de datos al guardar la evidencia.");
            LOGGER.log(Level.SEVERE, "Error de SQL al guardar evidencia", e);
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
            LOGGER.log(Level.WARNING, "Formato numérico inválido", e);
        } catch (SQLException e) {
            showError("Error de base de datos al registrar la autoevaluación.");
            LOGGER.log(Level.SEVERE, "Error de SQL al registrar autoevaluación", e);
        } catch (Exception e) {
            showError("Ocurrió un error inesperado al registrar la autoevaluación.");
            LOGGER.log(Level.SEVERE, "Error inesperado al registrar autoevaluación", e);
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
            showError("Error de base de datos al guardar los criterios.");
            LOGGER.log(Level.SEVERE, "Error de SQL al guardar criterios", e);
        } catch (Exception e) {
            showError("Ocurrió un error inesperado al guardar los criterios.");
            LOGGER.log(Level.SEVERE, "Error inesperado al guardar criterios", e);
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