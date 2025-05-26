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
import data_access.ConecctionDataBase;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GUI_RegisterSelfAssessmentController {

    @FXML private ComboBox<ProjectDTO> projectComboBox;
    @FXML private TextField evidenceFileTextField;
    @FXML private VBox criteriaVBox;
    @FXML private TextArea generalCommentsTextArea;
    @FXML private Button registerButton;
    @FXML private Label statusLabel;

    private File selectedEvidenceFile;
    private StudentDTO student;
    private ProjectDTO assignedProject;
    private List<CriterionInput> criterionInputs = new ArrayList<>();
    private static final Logger LOGGER = Logger.getLogger(GUI_RegisterSelfAssessmentController.class.getName());

    @FXML
    public void initialize() {
        loadProjects();
        loadCriteria();
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
        } catch (Exception e) {
            showError("Error al cargar proyectos.");
            LOGGER.log(Level.SEVERE, "Error al cargar proyectos", e);
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
        } catch (Exception e) {
            showError("Error al cargar criterios.");
            LOGGER.log(Level.SEVERE, "Error al cargar criterios", e);
        }
    }

    @FXML
    private void handleSelectEvidenceFile(javafx.event.ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo de evidencia");
        File file = fileChooser.showOpenDialog(evidenceFileTextField.getScene().getWindow());
        if (file != null) {
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
            } catch (Exception ignored) {}
        }
        return count > 0 ? (float) Math.round((sum / count) * 100) / 100 : 0f;
    }

    private int saveEvidenceFile(File file) {
        try {
            String destDir = "evidencias/";
            Files.createDirectories(Path.of(destDir));
            EvidenceDAO evidenceDAO = new EvidenceDAO();
            int nextId = evidenceDAO.getNextEvidenceId();
            String destPath = destDir + "evidencia_" + nextId + "_" + file.getName();
            Files.copy(file.toPath(), Path.of(destPath), StandardCopyOption.REPLACE_EXISTING);

            EvidenceDTO evidence = new EvidenceDTO(
                    nextId,
                    file.getName(),
                    new Date(),
                    destPath
            );
            evidenceDAO.insertEvidence(evidence);
            return nextId;
        } catch (IOException e) {
            showError("Error al guardar el archivo de evidencia.");
            LOGGER.log(Level.SEVERE, "Error de IO al guardar evidencia", e);
        } catch (SQLException e) {
            showError("Error de base de datos al guardar la evidencia.");
            LOGGER.log(Level.SEVERE, "Error de SQL al guardar evidencia", e);
        } catch (Exception e) {
            showError("Ocurrió un error inesperado al guardar la evidencia.");
            LOGGER.log(Level.SEVERE, "Error inesperado al guardar evidencia", e);
        }
        return -1;
    }

    private int saveSelfAssessment(int evidenceId, float averageGrade, String generalComments) {
        try {
            ProjectDTO selectedProject = assignedProject != null ? assignedProject : projectComboBox.getValue();
            SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                    0,
                    "",
                    averageGrade,
                    student != null ? student.getTuiton() : "",
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
        try (ConecctionDataBase db = new ConecctionDataBase();
             Connection conn = db.connectDB()) {
            CriterionSelfAssessmentDAO criterionDAO = new CriterionSelfAssessmentDAO(conn);
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

    // Clase interna para los controles de cada criterio
    private static class CriterionInput {
        String idCriteria;
        Label nameLabel;
        TextField gradeField;
        TextField commentsField;

        CriterionInput(String idCriteria, String name) {
            this.idCriteria = idCriteria;
            this.nameLabel = new Label(name);
            this.gradeField = new TextField();
            this.gradeField.setPromptText("Calificación");
            this.commentsField = new TextField();
            this.commentsField.setPromptText("Comentarios");
        }

        HBox toHBox() {
            HBox hbox = new HBox(12, nameLabel, gradeField, commentsField);
            hbox.setStyle("-fx-padding: 4 0 4 0;");
            return hbox;
        }

        String getGrade() { return gradeField.getText(); }
        String getComments() { return commentsField.getText(); }
    }
}