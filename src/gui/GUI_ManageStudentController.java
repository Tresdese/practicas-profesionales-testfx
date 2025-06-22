package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import logic.DAO.GroupDAO;
import logic.DTO.GroupDTO;
import logic.DTO.ProjectDTO;
import logic.DTO.StudentDTO;
import logic.DTO.StudentProjectDTO;
import logic.services.ServiceFactory;
import logic.services.StudentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GUI_ManageStudentController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_ManageStudentController.class);

    private static final int MAX_NAMES = 50;
    private static final int MAX_SURNAMES = 50;

    @FXML
    private TextField namesField, surnamesField, creditAdvanceField;

    @FXML
    private ChoiceBox<String> nrcChoiceBox;

    @FXML
    private Label statusLabel, namesCharCountLabel, surnamesCharCountLabel;

    private StudentDTO student;
    private ProjectDTO currentProject;
    private StudentProjectDTO studentProject;
    private StudentService studentService;

    @FXML
    private void initialize() {
        configureTextFormatters();
        configureCharCountLabels();
        loadNRCs();
        initializeStudentService();
    }

    private void configureTextFormatters() {
        namesField.setTextFormatter(createTextFormatter(MAX_NAMES));
        surnamesField.setTextFormatter(createTextFormatter(MAX_SURNAMES));
    }

    private TextFormatter<String> createTextFormatter(int maxLength) {
        return new TextFormatter<>(change ->
                change.getControlNewText().length() <= maxLength ? change : null
        );
    }

    private void configureCharCountLabels() {
        configureCharCount(namesField, namesCharCountLabel, MAX_NAMES);
        configureCharCount(surnamesField, surnamesCharCountLabel, MAX_SURNAMES);
    }

    private void configureCharCount(TextField textField, Label charCountLabel, int maxLength) {
        charCountLabel.setText("0/" + maxLength);
        textField.textProperty().addListener((observable, oldText, newText) ->
                charCountLabel.setText(newText.length() + "/" + maxLength)
        );
    }

    private void initializeStudentService() {
        try {
            this.studentService = ServiceFactory.getStudentService();
        } catch (Exception e) {
            LOGGER.error("Error al obtener StudentService desde ServiceFactory: {}", e.getMessage(), e);
            statusLabel.setText("Error al conectar con la base de datos.");
            statusLabel.setTextFill(Color.RED);
        }
    }

    private void loadNRCs() {
        try {
            GroupDAO groupDAO = new GroupDAO();
            List<GroupDTO> groups = groupDAO.getAllGroups();
            for (GroupDTO group : groups) {
                nrcChoiceBox.getItems().add(group.getNRC());
            }
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
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al cargar los NRCs.");
                LOGGER.error("Error al cargar los NRCs: {}", e.getMessage(), e);
                statusLabel.setTextFill(Color.RED);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado al cargar los NRCs: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al cargar los NRCs.");
            statusLabel.setTextFill(Color.RED);
        }
    }

    public void setStudentData(StudentDTO student, ProjectDTO currentProject) {
        if (student == null) {
            LOGGER.error("El objeto StudentDTO es nulo.");
            return;
        }

        this.student = student;
        this.currentProject = currentProject;

        namesField.setText(student.getNames() != null ? student.getNames() : "");
        surnamesField.setText(student.getSurnames() != null ? student.getSurnames() : "");
        nrcChoiceBox.setValue(student.getNRC() != null ? student.getNRC() : "");
        creditAdvanceField.setText(student.getCreditAdvance() != null ? student.getCreditAdvance() : "");
    }

    @FXML
    private void handleSaveChanges() {
        if (studentService == null) {
            LOGGER.error("StudentService no ha sido inicializado.");
            statusLabel.setText("Error interno. Intente más tarde.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        try {
            if (!areFieldsFilled()) {
                throw new IllegalArgumentException("Todos los campos deben estar llenos.");
            }

            String names = namesField.getText();
            String surnames = surnamesField.getText();
            String nrc = nrcChoiceBox.getValue();
            String creditAdvance = creditAdvanceField.getText();

            student.setNames(names);
            student.setSurnames(surnames);
            student.setNRC(nrc);
            student.setCreditAdvance(creditAdvance);

            studentService.updateStudent(student);

            statusLabel.setText("¡Estudiante actualizado exitosamente!");
            statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
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
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if ("23000".equals(sqlState)) {
                statusLabel.setText("Error de integridad .");
                statusLabel.setTextFill(Color.RED);
                LOGGER.warn("Error de integridad: {}", e.getMessage());
            } else {
                statusLabel.setText("Error al actualizar el estudiante.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al actualizar el estudiante: {}", e);
            }
        } catch (Exception e) {
            statusLabel.setText(e.getMessage());
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error: {}", e);
        }
    }

    private boolean areFieldsFilled() {
        return !namesField.getText().isEmpty() &&
                !surnamesField.getText().isEmpty() &&
                nrcChoiceBox.getValue() != null &&
                !creditAdvanceField.getText().isEmpty();
    }

    @FXML
    private void handleAssignFinalGrade() {
        if (student == null) {
            statusLabel.setText("No hay un estudiante seleccionado.");
            statusLabel.setTextFill(Color.RED);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RecordFinalGrade.fxml"));
            Parent root = loader.load();

            GUI_RecordFinalGradeController controller = loader.getController();
            controller.setStudent(student);

            Stage stage = new Stage();
            stage.setTitle("Asignar Calificación Final");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.error("Error al cargar la interfaz GUI_RecordFinalGrade.fxml: {}", e);
            statusLabel.setText("Error al abrir la ventana de calificación.");
            statusLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al abrir la ventana de calificación: {}", e);
            statusLabel.setText("Ocurrió un error inesperado. Intente más tarde.");
            statusLabel.setTextFill(Color.RED);
        }
    }
}