package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_CheckListOfStudentsController;
import gui.GUI_LinkActivityToScheduleController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.DAO.ActivityDAO;
import logic.DAO.ScheduleOfActivitiesDAO;
import logic.DTO.ActivityDTO;
import logic.DTO.ScheduleOfActivitiesDTO;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.*;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LinkActivityToScheduleControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private FXMLLoader loader;
    private int activityId;
    private String scheduleId;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_LinkActivityToSchedule.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Vincular Actividad a Cronograma");
        stage.setScene(scene);
        stage.show();
    }

    void connectToDatabase() throws SQLException, IOException {
        if (connection == null || connection.isClosed()) {
            connectionDB = new ConnectionDataBase();
            connection = connectionDB.connectDataBase();
        }
    }

    private void forceLoadData() {
        GUI_LinkActivityToScheduleController controller = loader.getController();
        interact(() -> controller.initialize(null, null));
    }

    @BeforeAll
    void setUpAll() throws Exception {
        connectToDatabase();
        clearTablesAndResetAutoIncrement();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createBaseData();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("DELETE FROM cronograma_actividad");
        statement.execute("ALTER TABLE cronograma_actividad AUTO_INCREMENT = 1");
        statement.execute("DELETE FROM cronograma_de_actividades");
        statement.execute("ALTER TABLE cronograma_de_actividades AUTO_INCREMENT = 1");
        statement.execute("DELETE FROM actividad");
        statement.execute("ALTER TABLE actividad AUTO_INCREMENT = 1");
        statement.execute("DELETE FROM evidencia");
        statement.execute("ALTER TABLE evidencia AUTO_INCREMENT = 1");
        statement.execute("DELETE FROM estudiante");
        statement.execute("DELETE FROM grupo");
        statement.execute("DELETE FROM usuario");
        statement.execute("DELETE FROM periodo");
        statement.execute("ALTER TABLE periodo AUTO_INCREMENT = 1");
        statement.close();
    }

    private void createBaseData() throws SQLException {

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, 1);
            ps.setString(2, "2025-1");
            ps.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            ps.executeUpdate();
        }

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, 1);
            ps.setString(2, "Grupo 1");
            ps.setNull(3, Types.INTEGER);
            ps.setInt(4, 1);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio, calificacionFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, "A0001");
            ps.setInt(2, 1);
            ps.setString(3, "John");
            ps.setString(4, "Doe");
            ps.setString(5, "1234567890");
            ps.setString(6, "john@mail.com");
            ps.setString(7, "johnuser");
            ps.setString(8, "1234567890123456789012345678901234567890123456789012345678901234");
            ps.setInt(9, 1);
            ps.setInt(10, 100);
            ps.setDouble(11, 95.5);
            ps.executeUpdate();
        }

        int evidenceId = 1;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO evidencia (idEvidencia, nombreEvidencia, fechaEntrega, ruta) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, evidenceId);
            ps.setString(2, "Evidence 1");
            ps.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            ps.setString(4, "/path/evidence1.pdf");
            ps.executeUpdate();
        }

        scheduleId = "1";
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO cronograma_de_actividades (idCronograma, hito, fechaEstimada, matricula, idEvidencia) VALUES (?, ?, ?, ?, ?)")) {
            ps.setInt(1, Integer.parseInt(scheduleId));
            ps.setString(2, "Milestone 1");
            ps.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            ps.setString(4, "A0001");
            ps.setInt(5, evidenceId);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO actividad (idActividad, nombreActividad) VALUES (?, ?)")) {
            activityId = 1;
            ps.setInt(1, activityId);
            ps.setString(2, "Activity 1");
            ps.executeUpdate();
        }
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
    public void testSuccessPopulateActivityChoiceBox() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<ActivityDTO> activityBox = lookup("#activityChoiceBox").query();

        interact(() -> {
            if (activityBox.getValue() == null && !activityBox.getItems().isEmpty()) {
                activityBox.getSelectionModel().select(0);
            }
        });

        assertThat(activityBox.getItems()).isNotEmpty();
        assertThat(activityBox.getValue()).isNotNull();
    }

    @Test
    public void testSuccessPopulateScheduleChoiceBox() {
        forceLoadData();

        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<ScheduleOfActivitiesDTO> scheduleBox = lookup("#scheduleChoiceBox").query();

        interact(() -> {
            if (scheduleBox.getValue() == null && !scheduleBox.getItems().isEmpty()) {
                scheduleBox.getSelectionModel().select(0);
            }
        });

        assertThat(scheduleBox.getItems()).isNotEmpty();
        assertThat(scheduleBox.getValue()).isNotNull();
    }

    @Test
    public void testPopulateActivityChoiceBoxesWithNoData() throws SQLException {
        clearTablesAndResetAutoIncrement();

        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<ActivityDTO> activityBox = lookup("#activityChoiceBox").query();

        assertThat(activityBox.getItems()).isEmpty();
    }

    @Test
    public void testPopulateScheduleChoiceBoxWithNoData() throws SQLException {
        clearTablesAndResetAutoIncrement();

        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<ScheduleOfActivitiesDTO> scheduleBox = lookup("#scheduleChoiceBox").query();
        assertThat(scheduleBox.getItems()).isEmpty();
    }

    @Test
    public void testLinkActivityToScheduleSuccess() {
        forceLoadData();

        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<ScheduleOfActivitiesDTO> scheduleBox = lookup("#scheduleChoiceBox").query();
        ChoiceBox<ActivityDTO> activityBox = lookup("#activityChoiceBox").query();
        Button linkButton = lookup(".button").queryAll().stream()
                .filter(node -> node instanceof Button && ((Button) node).getText().contains("Vincular"))
                .map(node -> (Button) node)
                .findFirst()
                .orElseThrow();

        interact(() -> {
            scheduleBox.getSelectionModel().select(0);
            activityBox.getSelectionModel().select(0);
        });

        clickOn(linkButton);
        WaitForAsyncUtils.waitForFxEvents();

        Label statusLabel = lookup("#statusLabel").query();
        assertThat(statusLabel.getText()).contains("exitosamente");
    }

    @Test
    public void testLinkActivityToScheduleWithoutSelectionShowsError() {
        WaitForAsyncUtils.waitForFxEvents();

        Button linkButton = lookup(".button").queryAll().stream()
                .filter(node -> node instanceof Button && ((Button) node).getText().contains("Vincular"))
                .map(node -> (Button) node)
                .findFirst()
                .orElseThrow();

        clickOn(linkButton);
        WaitForAsyncUtils.waitForFxEvents();

        Label statusLabel = lookup("#statusLabel").query();
        assertThat(statusLabel.getText()).contains("Debe seleccionar un cronograma y una actividad");
    }
}