package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_ManageStudentController;
import gui.GUI_RecordFinalGradeController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.DTO.ProjectDTO;
import logic.DTO.StudentDTO;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ManageStudentControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private FXMLLoader loader;
    private StudentDTO testStudent;
    private ProjectDTO testProject;
    private int idUsuario;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_ManageStudent.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Gestión de Estudiante");
        stage.setScene(scene);
        stage.show();
    }

    private void forceLoadData() {
        interact(this::setStudentDataInController);
    }

    void connectToDatabase() throws SQLException, IOException {
        if (connection == null || connection.isClosed()) {
            connectionDB = new ConnectionDataBase();
            connection = connectionDB.connectDataBase();
        }
    }

    @BeforeAll
    void setUpAll() throws Exception {
        connectToDatabase();
        clearTables();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTables();
        createTestData();
        interact(this::setStudentDataInController);
    }

    private void clearTables() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SET FOREIGN_KEY_CHECKS=0");
        statement.execute("DELETE FROM estudiante");
        statement.execute("DELETE FROM grupo");
        statement.execute("DELETE FROM usuario");
        statement.execute("DELETE FROM periodo");
        statement.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE grupo AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE periodo AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE estudiante AUTO_INCREMENT = 1");
        statement.execute("SET FOREIGN_KEY_CHECKS=1");
        statement.close();
    }

    private void createTestData() throws SQLException {
        String insertUser = "INSERT INTO usuario (numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "12345");
            statement.setString(2, "Juan");
            statement.setString(3, "Pérez");
            statement.setString(4, "juanperez");
            statement.setString(5, "passTest");
            statement.setString(6, "ACADEMICO");
            statement.setInt(7, 1);
            statement.executeUpdate();
            try (var rs = statement.getGeneratedKeys()) {
                rs.next();
                idUsuario = rs.getInt(1);
            }
        }

        String insertPeriod = "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertPeriod)) {
            statement.setString(1, "2024");
            statement.setString(2, "Periodo de Prueba");
            statement.setTimestamp(3, java.sql.Timestamp.valueOf("2024-01-01 00:00:00"));
            statement.setTimestamp(4, java.sql.Timestamp.valueOf("2024-06-30 23:59:59"));
            statement.executeUpdate();
        }

        String insertGroup = "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertGroup)) {
            statement.setString(1, "11111");
            statement.setString(2, "Grupo 1");
            statement.setInt(3, idUsuario);
            statement.setString(4, "2024");
            statement.executeUpdate();
        }
        try (PreparedStatement statement = connection.prepareStatement(insertGroup)) {
            statement.setString(1, "22222");
            statement.setString(2, "Grupo 2");
            statement.setInt(3, idUsuario);
            statement.setString(4, "2024");
            statement.executeUpdate();
        }

        String insertStudent = "INSERT INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio, calificacionFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertStudent)) {
            statement.setString(1, "S20240001");
            statement.setInt(2, 1);
            statement.setString(3, "Ana");
            statement.setString(4, "García");
            statement.setString(5, "5551234567");
            statement.setString(6, "ana.garcia@prueba.com");
            statement.setString(7, "anagarcia");
            statement.setString(8, "passTest");
            statement.setString(9, "11111");
            statement.setString(10, "80");
            statement.setDouble(11, 9.5);
            statement.executeUpdate();
        }

        testStudent = new StudentDTO();
        testStudent.setTuition("S20240001");
        testStudent.setNames("Ana");
        testStudent.setSurnames("García");
        testStudent.setNRC("11111");
        testStudent.setCreditAdvance("80");
        testProject = new ProjectDTO();
    }

    private void setStudentDataInController() {
        GUI_ManageStudentController controller = loader.getController();
        controller.setStudentData(testStudent, testProject);
    }

    @AfterAll
    void tearDownAll() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        if (connectionDB != null) {
            connectionDB.close();
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        clearTables();
    }

    @Test
    public void testEditAndSaveStudent() {
        WaitForAsyncUtils.waitForFxEvents();

        TextField namesField = lookup("#namesField").query();
        TextField surnamesField = lookup("#surnamesField").query();
        ChoiceBox<String> nrcChoiceBox = lookup("#nrcChoiceBox").query();
        TextField creditAdvanceField = lookup("#creditAdvanceField").query();
        Button saveButton = lookup(".button")
                .queryAll()
                .stream()
                .filter(node -> node instanceof Button && ((Button) node).getText().contains("Guardar"))
                .map(node -> (Button) node)
                .findFirst()
                .orElseThrow();

        assertThat(namesField.getText()).isEqualTo("Ana");
        assertThat(surnamesField.getText()).isEqualTo("García");
        assertThat(nrcChoiceBox.getValue()).isEqualTo("11111");
        assertThat(creditAdvanceField.getText()).isEqualTo("80");

        interact(() -> {
            namesField.setText("María");
            surnamesField.setText("López");
            nrcChoiceBox.setValue("22222");
            creditAdvanceField.setText("90");
        });

        clickOn(saveButton);
        WaitForAsyncUtils.waitForFxEvents();

        Label statusLabel = lookup("#statusLabel").query();
        assertThat(statusLabel.getText()).isEqualTo("¡Estudiante actualizado exitosamente!");
        assertThat(statusLabel.getTextFill()).isEqualTo(Color.GREEN);
    }

    @Test
    public void testEditAndSaveStudentWithEmptyFields() {
        WaitForAsyncUtils.waitForFxEvents();

        TextField namesField = lookup("#namesField").query();
        TextField surnamesField = lookup("#surnamesField").query();
        ChoiceBox<String> nrcChoiceBox = lookup("#nrcChoiceBox").query();
        TextField creditAdvanceField = lookup("#creditAdvanceField").query();
        Button saveButton = lookup(".button")
                .queryAll()
                .stream()
                .filter(node -> node instanceof Button && ((Button) node).getText().contains("Guardar"))
                .map(node -> (Button) node)
                .findFirst()
                .orElseThrow();

        interact(() -> {
            namesField.setText("");
            surnamesField.setText("");
            nrcChoiceBox.setValue(null);
            creditAdvanceField.setText("");
        });

        clickOn(saveButton);
        WaitForAsyncUtils.waitForFxEvents();

        Label statusLabel = lookup("#statusLabel").query();
        assertThat(statusLabel.getText()).isEqualTo("Todos los campos deben estar llenos.");
        assertThat(statusLabel.getTextFill()).isEqualTo(Color.RED);
    }

    @Test
    public void testAssignFinalGrade() {
        WaitForAsyncUtils.waitForFxEvents();

        Button assignFinalGradeButton = lookup("#assignFinalGradeButton").query();
        clickOn(assignFinalGradeButton);
        WaitForAsyncUtils.waitForFxEvents();

        TextField gradeField = lookup("#finalGradeField").query();
        Button saveButton = lookup("#saveGradeButton").query();
        Label statusLabel = lookup("#statusLabel").query();

        assertThat(gradeField).isNotNull();
        assertThat(saveButton).isNotNull();
        assertThat(statusLabel).isNotNull();
    }

    @Test
    public void testAssignFinalGradeWithEmptyFields() {
        forceLoadData();

        WaitForAsyncUtils.waitForFxEvents();

        Button assignFinalGradeButton = lookup("#assignFinalGradeButton").query();
        clickOn(assignFinalGradeButton);
        WaitForAsyncUtils.waitForFxEvents();

        Stage modalStage = (Stage) lookup("#finalGradeField").query().getScene().getWindow();
        targetWindow(modalStage);

        TextField gradeField = lookup("#finalGradeField").query();
        Button saveButton = lookup("#saveGradeButton").query();

        interact(() -> gradeField.setText(""));
        clickOn(saveButton);
        WaitForAsyncUtils.waitForFxEvents();

        Label statusLabel = lookup("#statusLabel").query();
        assertThat(statusLabel.getText()).isEqualTo("Todos los campos deben estar llenos.");
        assertThat(statusLabel.getTextFill()).isEqualTo(Color.RED);
    }

    @Test
    public void testCloseFinalGradeWindow() {
        WaitForAsyncUtils.waitForFxEvents();

        Button assignFinalGradeButton = lookup("#assignFinalGradeButton").query();
        clickOn(assignFinalGradeButton);
        WaitForAsyncUtils.waitForFxEvents();

        Stage modalStage = (Stage) lookup("#finalGradeField").query().getScene().getWindow();
        targetWindow(modalStage);

        interact(modalStage::close);
        WaitForAsyncUtils.waitForFxEvents();

        targetWindow(window("Gestión de Estudiante"));

        Label statusLabel = lookup("#statusLabel").query();
        assertThat(statusLabel.getText()).isEqualTo("No se asignó la calificación final.");
    }

}