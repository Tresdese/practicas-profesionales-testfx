package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_CheckScheduleOfActivitiesController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.DAO.ScheduleOfActivitiesDAO;
import logic.DTO.ScheduleOfActivitiesDTO;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.*;
import java.io.IOException;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CheckScheduleOfActivitiesControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private ScheduleOfActivitiesDAO scheduleDAO;
    private FXMLLoader loader;
    private String testScheduleId = "1";
    private String testTuition = "A12345678";
    private String testEvidenceId = "1";

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckScheduleOfActivities.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Ver cronograma de actividades");
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
        scheduleDAO = new ScheduleOfActivitiesDAO();
        createDataBaseRecords();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createDataBaseRecords();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM cronograma_de_actividades");
            stmt.execute("ALTER TABLE cronograma_de_actividades AUTO_INCREMENT = 1");
            stmt.execute("DELETE FROM estudiante");
            stmt.execute("DELETE FROM grupo");
            stmt.execute("DELETE FROM periodo");
            stmt.execute("DELETE FROM evidencia");
            stmt.execute("ALTER TABLE evidencia AUTO_INCREMENT = 1");
            stmt.execute("DELETE FROM usuario");
            stmt.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        }
    }

    private void createDataBaseRecords() throws SQLException {
        String userSql = "INSERT INTO usuario (numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (?, ?, ?, ?, ?, ?)";
        int userId = 0;
        try (PreparedStatement ps = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, 1001);
            ps.setString(2, "Academico");
            ps.setString(3, "Prueba");
            ps.setString(4, "academico1");
            ps.setString(5, "password123");
            ps.setString(6, "Academico");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    userId = rs.getInt(1);
                }
            }
        }

        String periodSql = "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (?, ?, ?, ?)";
        String periodId = "1";
        try (PreparedStatement ps = connection.prepareStatement(periodSql)) {
            ps.setString(1, periodId);
            ps.setString(2, "Periodo Base");
            ps.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            ps.executeUpdate();
        }

        String groupSql = "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)";
        String nrc = "101";
        try (PreparedStatement ps = connection.prepareStatement(groupSql)) {
            ps.setString(1, nrc);
            ps.setString(2, "Grupo Base");
            ps.setInt(3, userId);
            ps.setString(4, periodId);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio, calificacionFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, testTuition);
            ps.setInt(2, 1);
            ps.setString(3, "Juan");
            ps.setString(4, "Pérez");
            ps.setString(5, "1234567890");
            ps.setString(6, "juan.perez@example.com");
            ps.setString(7, "juanperez");
            ps.setString(8, "password123");
            ps.setString(9, nrc);
            ps.setInt(10, 50);
            ps.setDouble(11, 85.5);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO evidencia (nombreEvidencia, fechaEntrega, ruta) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Evidencia Base");
            ps.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            ps.setString(3, "/ruta/evidencia/base");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    testEvidenceId = String.valueOf(rs.getInt(1));
                }
            }
        }

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO cronograma_de_actividades (idCronograma, hito, fechaEstimada, matricula, idEvidencia) VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, testScheduleId);
            ps.setString(2, "Hito 1");
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, testTuition);
            ps.setString(5, testEvidenceId);
            ps.executeUpdate();
        }
    }

    private void forceLoadData() {
        GUI_CheckScheduleOfActivitiesController controller = loader.getController();
        interact(() -> controller.setStudentTuition(testTuition));
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
    public void testLoadSchedulesForStudentPopulatesListView() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        ListView<String> listView = lookup("#scheduleListView").query();
        assertThat(listView.getItems()).isNotEmpty();
        assertThat(listView.getItems().get(0)).contains("Hito: Hito 1");
    }

    @Test
    public void testLoadSchedulesForStudentWithNoSchedules() throws Exception {
        clearTablesAndResetAutoIncrement();
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        Label statusLabel = lookup("#statusLabel").query();
        assertThat(statusLabel.getText()).contains("No hay cronograma registrado");
    }

    @Test
    public void testShowScheduleDetailsUpdatesLabels() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        Label milestoneLabel = lookup("#milestoneLabel").query();
        assertThat(milestoneLabel.getText()).isEqualTo("Hito 1");
    }
}