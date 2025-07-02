package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_CheckSelfAssessmentController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.DAO.SelfAssessmentDAO;
import logic.DTO.Role;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.*;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CheckSelfAssessmentControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private SelfAssessmentDAO selfAssessmentDAO;
    private FXMLLoader loader;
    private String testPeriodId = "1";
    private String testUserId = "1";
    private String testGroupNRC = "1001";
    private String testStudentTuition = "S12345";
    private int testEvidenceId = 1;
    private int testProjectId = 1;
    private int testOrganizationId = 1;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckSelfAssessment.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Ver autoevaluación");
        stage.setScene(scene);
        stage.show();
    }

    @BeforeAll
    void setUpAll() throws Exception {
        connectionDB = new ConnectionDataBase();
        connection = connectionDB.connectDataBase();
        clearTablesAndResetAutoIncrement();
        createBaseObjects();
        selfAssessmentDAO = new SelfAssessmentDAO();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createBaseObjects();
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

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("DELETE FROM autoevaluacion");
        statement.execute("DELETE FROM evidencia");
        statement.execute("DELETE FROM proyecto");
        statement.execute("DELETE FROM organizacion_vinculada");
        statement.execute("DELETE FROM estudiante");
        statement.execute("DELETE FROM grupo");
        statement.execute("DELETE FROM usuario");
        statement.execute("DELETE FROM periodo");
        statement.execute("ALTER TABLE autoevaluacion AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE evidencia AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        statement.close();
    }

    private void createBaseObjects() throws SQLException {
        String sqlPeriod = "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlPeriod)) {
            statement.setString(1, testPeriodId);
            statement.setString(2, "2024-1");
            statement.setDate(3, java.sql.Date.valueOf("2024-01-01"));
            statement.setDate(4, java.sql.Date.valueOf("2024-06-30"));
            statement.executeUpdate();
        }

        String sqlUser = "INSERT INTO usuario (idUsuario, numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlUser)) {
            statement.setString(1, testUserId);
            statement.setString(2, "10001");
            statement.setString(3, "Juan");
            statement.setString(4, "Pérez");
            statement.setString(5, "juanp");
            statement.setString(6, "1234567890123456789012345678901234567890123456789012345678901234");
            statement.setString(7, Role.ACADEMIC.getDataBaseValue());
            statement.executeUpdate();
        }

        String sqlGroup = "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlGroup)) {
            statement.setString(1, testGroupNRC);
            statement.setString(2, "Grupo 1");
            statement.setString(3, testUserId);
            statement.setString(4, testPeriodId);
            statement.executeUpdate();
        }

        String sqlStudent = "INSERT INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio, calificacionFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlStudent)) {
            statement.setString(1, testStudentTuition);
            statement.setInt(2, 1);
            statement.setString(3, "Pedro");
            statement.setString(4, "López");
            statement.setString(5, "5555555555");
            statement.setString(6, "pedro@test.com");
            statement.setString(7, "pedrolopez");
            statement.setString(8, "1234567890123456789012345678901234567890123456789012345678901234");
            statement.setString(9, testGroupNRC);
            statement.setString(10, "100");
            statement.setDouble(11, 0.0);
            statement.executeUpdate();
        }

        String sqlOrg = "INSERT INTO organizacion_vinculada (nombre, direccion) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlOrg, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "Org Test");
            statement.setString(2, "Calle Falsa 123");
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    testOrganizationId = rs.getInt(1);
                }
            }
        }

        String sqlProject = "INSERT INTO proyecto (idProyecto, nombre, descripcion, fechaAproximada, fechaInicio, idUsuario, idOrganizacion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlProject)) {
            statement.setInt(1, testProjectId);
            statement.setString(2, "Proyecto Test");
            statement.setString(3, "Descripción de prueba");
            statement.setDate(4, java.sql.Date.valueOf("2024-05-01"));
            statement.setDate(5, java.sql.Date.valueOf("2024-04-01"));
            statement.setString(6, testUserId);
            statement.setInt(7, testOrganizationId);
            statement.executeUpdate();
        }

        String sqlEvidence = "INSERT INTO evidencia (nombreEvidencia, fechaEntrega, ruta) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlEvidence, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "Evidencia 1");
            statement.setDate(2, java.sql.Date.valueOf("2024-05-01"));
            statement.setString(3, "/ruta/evidencia1.pdf");
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    testEvidenceId = rs.getInt(1);
                }
            }
        }

        String sqlSelfAssessmentQuery = "INSERT INTO autoevaluacion (comentarios, calificacion, matricula, idProyecto, idEvidencia, fecha_registro, estado, comentarios_generales) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlSelfAssessmentQuery)) {
            statement.setString(1, "Buen trabajo");
            statement.setDouble(2, 9.5);
            statement.setString(3, testStudentTuition);
            statement.setInt(4, testProjectId);
            statement.setInt(5, testEvidenceId);
            statement.setDate(6, new java.sql.Date(System.currentTimeMillis()));
            statement.setString(7, "completada");
            statement.setString(8, "Comentarios generales");
            statement.executeUpdate();
        }
    }

    private void forceLoadData(String tuition) {
        GUI_CheckSelfAssessmentController controller = loader.getController();
        interact(() -> controller.setStudentTuition(tuition));
    }

    @Test
    public void testLoadSelfAssessmentPopulatesLabels() {
        forceLoadData(testStudentTuition);
        WaitForAsyncUtils.waitForFxEvents();

        Label commentsLabel = lookup("#commentsLabel").query();
        Label gradeLabel = lookup("#gradeLabel").query();
        Label statusLabel = lookup("#statusLabel").query();

        assertThat(commentsLabel.getText()).isEqualTo("Buen trabajo");
        assertThat(gradeLabel.getText()).isEqualTo("9.5");
        assertThat(statusLabel.getText().toLowerCase()).contains("completada");
    }

    @Test
    public void testLoadSelfAssessmentWithNoSelfAssessment() throws Exception {
        clearTablesAndResetAutoIncrement();

        forceLoadData(testStudentTuition);
        WaitForAsyncUtils.waitForFxEvents();

        Label noSelfAssessmentLabel = lookup("#noSelfAssessmentLabel").query();
        assertThat(noSelfAssessmentLabel.isVisible()).isTrue();
    }

    @Test
    public void testLoadSelfAssessmentWithInvalidTuition() {
        forceLoadData("NO_EXISTE");
        WaitForAsyncUtils.waitForFxEvents();

        Label noSelfAssessmentLabel = lookup("#noSelfAssessmentLabel").query();
        assertThat(noSelfAssessmentLabel.isVisible()).isTrue();
    }

    @Test
    public void testFailureOpenEvidences() {

    }
}