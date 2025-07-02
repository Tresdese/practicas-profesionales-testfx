package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_DetailsStudentController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.DTO.Role;
import logic.DTO.StudentDTO;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.*;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DetailsStudentControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private String testStudentTuition;
    private FXMLLoader loader;

    private int testUserId;
    private String testGroupNRC;
    private String testPeriodId;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_DetailsStudent.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Detalles del Estudiante");
        stage.setScene(scene);
        stage.show();
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
        clearTablesAndResetAutoIncrement();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createTestAcademic();
        createTestPeriod();
        createTestGroup();
        createTestStudent();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
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

    private void createTestAcademic() throws SQLException {
        String sql = "INSERT INTO usuario (numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "12345");
            statement.setString(2, "Juan");
            statement.setString(3, "Pérez");
            statement.setString(4, "juanperez");
            statement.setString(5, "passTest");
            statement.setString(6, Role.ACADEMIC.getDataBaseValue());
            statement.setInt(7, 1);
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    testUserId = rs.getInt(1);
                }
            }
        }
    }

    private void createTestPeriod() throws SQLException {
        String sql = "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (?, ?, ?, ?)";
        testPeriodId = "0";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, testPeriodId);
            statement.setString(2, "Periodo de Prueba");
            statement.setTimestamp(3, java.sql.Timestamp.valueOf("2024-01-01 00:00:00"));
            statement.setTimestamp(4, java.sql.Timestamp.valueOf("2024-06-30 23:59:59"));
            statement.executeUpdate();
        }
    }

    private void createTestGroup() throws SQLException {
        String sql = "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)";
        testGroupNRC = "11111";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, testGroupNRC);
            statement.setString(2, "Grupo de Prueba");
            statement.setInt(3, testUserId);
            statement.setString(4, testPeriodId);
            statement.executeUpdate();
        }
    }

    private void createTestStudent() throws SQLException {
        String sql = "INSERT INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio, calificacionFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        testStudentTuition = "S20240001";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, testStudentTuition);
            statement.setInt(2, 1);
            statement.setString(3, "Ana");
            statement.setString(4, "García");
            statement.setString(5, "5551234567");
            statement.setString(6, "ana.garcia@prueba.com");
            statement.setString(7, "anagarcia");
            statement.setString(8, "passTest");
            statement.setString(9, testGroupNRC);
            statement.setString(10, "80");
            statement.setDouble(11, 9.5);
            statement.executeUpdate();
        }
    }

    private void forceLoadData() {
        GUI_DetailsStudentController controller = loader.getController();
        StudentDTO student = new StudentDTO();
        student.setTuition(testStudentTuition);
        student.setNames("Ana");
        student.setSurnames("García");
        student.setEmail("ana.garcia@prueba.com");
        student.setNRC(testGroupNRC);
        interact(() -> controller.setStudent(student));
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
        clearTablesAndResetAutoIncrement();
    }

    @Test
    public void testStudentDataIsDisplayed() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        Label tuitionLabel = lookup("#tuitionLabel").query();
        Label namesLabel = lookup("#namesLabel").query();
        Label surnamesLabel = lookup("#surnamesLabel").query();
        Label emailLabel = lookup("#emailLabel").query();
        Label nrcLabel = lookup("#NRCLabel").query();

        assertThat(tuitionLabel.getText()).isEqualTo(testStudentTuition);
        assertThat(namesLabel.getText()).isEqualTo("Ana");
        assertThat(surnamesLabel.getText()).isEqualTo("García");
        assertThat(emailLabel.getText()).isEqualTo("ana.garcia@prueba.com");
        assertThat(nrcLabel.getText()).isEqualTo(testGroupNRC);
    }

    @Test
    public void testProjectLabelsShowNoAssignedWhenNoProject() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        Label projectNameLabel = lookup("#projectNameLabel").query();
        Label projectDescriptionLabel = lookup("#projectDescriptionLabel").query();
        Label projectOrganizationLabel = lookup("#projectOrganizationLabel").query();

        assertThat(projectNameLabel.getText()).isEqualTo("N/A");
        assertThat(projectDescriptionLabel.getText()).isEqualTo("N/A");
        assertThat(projectOrganizationLabel.getText()).isEqualTo("N/A");
    }

    @Test
    public void testCheckSelfAssessmentButtonExists() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        Button selfAssessmentButton = lookup("#checkSelfAssessmentButton").query();
        assertThat(selfAssessmentButton).isNotNull();
    }

    @Test
    public void testOpenPresentationGradeWindow() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        Button presentationGradeButton = lookup("#checkPresentationGradeButton").query();
        assertThat(presentationGradeButton).isNotNull();

        clickOn(presentationGradeButton);
        WaitForAsyncUtils.waitForFxEvents();

        Stage presentationStage = (Stage) presentationGradeButton.getScene().getWindow();
        assertThat(presentationStage.isShowing()).isTrue();
    }

    @Test
    public void testOpenPresentationGradeWindowWithNoGrade() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        Button presentationGradeButton = lookup("#checkPresentationGradeButton").query();
        assertThat(presentationGradeButton).isNotNull();

        clickOn(presentationGradeButton);
        WaitForAsyncUtils.waitForFxEvents();

        Stage presentationStage = (Stage) presentationGradeButton.getScene().getWindow();
        assertThat(presentationStage.isShowing()).isTrue();

        Label noGradeLabel = lookup("#statusLabel").query();
        assertThat(noGradeLabel.getText()).isEqualTo("No tienes evaluaciones de presentación registradas.");
    }

    @Test
    public void testOpenCheckReportsWindow() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        Button checkReportsButton = lookup("#checkReportsButton").query();
        assertThat(checkReportsButton).isNotNull();

        clickOn(checkReportsButton);
        WaitForAsyncUtils.waitForFxEvents();

        Stage reportsStage = (Stage) checkReportsButton.getScene().getWindow();
        assertThat(reportsStage.isShowing()).isTrue();
    }

    @Test
    public void testOpenCheckSelfAssessmentWindow() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        Button selfAssessmentButton = lookup("#checkSelfAssessmentButton").query();
        assertThat(selfAssessmentButton).isNotNull();

        clickOn(selfAssessmentButton);
        WaitForAsyncUtils.waitForFxEvents();

        Stage selfAssessmentStage = (Stage) selfAssessmentButton.getScene().getWindow();
        assertThat(selfAssessmentStage.isShowing()).isTrue();
    }

    @Test
    public void testOpenCheckScheduleWindow() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        Button checkScheduleButton = lookup("#checkScheduleOfActivities").query();
        assertThat(checkScheduleButton).isNotNull();

        clickOn(checkScheduleButton);
        WaitForAsyncUtils.waitForFxEvents();

        Stage scheduleStage = (Stage) checkScheduleButton.getScene().getWindow();
        assertThat(scheduleStage.isShowing()).isTrue();
    }
}