package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_CheckListOfStudentsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.DTO.Role;
import logic.DTO.StudentDTO;
import logic.DTO.UserDTO;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CheckListOfStudentsControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private String testStudentTuition;
    private FXMLLoader loader;

    private int testUserId;
    private String testGroupNRC;
    private String testPeriodId;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckListOfStudents.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Lista de Estudiantes");
        stage.setScene(scene);
        stage.show();
    }

    void connectToDatabase() throws SQLException, IOException {
        if (connection == null || connection.isClosed()) {
            connectionDB = new ConnectionDataBase();
            connection = connectionDB.connectDB();
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
            statement.setString(6, "ACADEMICO");
            statement.setInt(7, 1); // Activo
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
            statement.setInt(2, 1); // Activo
            statement.setString(3, "Ana");
            statement.setString(4, "García");
            statement.setString(5, "5551234567");
            statement.setString(6, "ana.garcia@prueba.com");
            statement.setString(7, "anagarcia");
            statement.setString(8, "passTest");
            statement.setString(9, testGroupNRC); // NRC FK
            statement.setString(10, "80");
            statement.setDouble(11, 9.5);
            statement.executeUpdate();
        }
    }

    private void forceLoadData() {
        GUI_CheckListOfStudentsController controller = loader.getController();
        interact(controller::initialize);
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
    public void testLoadStudentsPopulatesTable() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<StudentDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isNotEmpty();
        StudentDTO student = tableView.getItems().get(0);
        assertThat(student.getTuition()).isEqualTo(testStudentTuition);
        assertThat(student.getNames()).isEqualTo("Ana");
        assertThat(student.getSurnames()).isEqualTo("García");
    }

    @Test
    public void testFailPopulateStudentTable() throws Exception {
        clearTablesAndResetAutoIncrement();
        GUI_CheckListOfStudentsController controller = loader.getController();
        interact(controller::initialize);
        WaitForAsyncUtils.waitForFxEvents();

        TableView<StudentDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isEmpty();
    }

    @Test
    public void testFilterActiveStudents() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<String> filterChoiceBox = lookup("#filterChoiceBox").query();
        interact(() -> filterChoiceBox.setValue("Activos"));
        WaitForAsyncUtils.waitForFxEvents();

        TableView<StudentDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isNotEmpty();
        for (StudentDTO student : tableView.getItems()) {
            assertThat(student.getState()).isEqualTo(1);
        }
    }

    @Test
    public void testFilterInactiveStudents() throws Exception {
        String sql = "UPDATE estudiante SET estado = 0 WHERE matricula = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, testStudentTuition);
            statement.executeUpdate();
        }

        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<String> filterChoiceBox = lookup("#filterChoiceBox").query();
        interact(() -> filterChoiceBox.setValue("Inactivos"));
        WaitForAsyncUtils.waitForFxEvents();

        TableView<StudentDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isNotEmpty();
        for (StudentDTO student : tableView.getItems()) {
            assertThat(student.getState()).isEqualTo(0);
        }
    }

    @Test
    public void testSearchStudentByTuition() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TextField searchField = lookup("#searchField").query();
        Button searchButton = lookup("#searchButton").query();

        interact(() -> searchField.setText(testStudentTuition));
        clickOn(searchButton);
        WaitForAsyncUtils.waitForFxEvents();

        TableView<StudentDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isNotEmpty();
        assertThat(tableView.getItems().get(0).getTuition()).isEqualTo(testStudentTuition);
    }

    @Test
    public void testSearchStudentByTuitionNotFound() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TextField searchField = lookup("#searchField").query();
        Button searchButton = lookup("#searchButton").query();

        interact(() -> searchField.setText("S00000000"));
        clickOn(searchButton);
        WaitForAsyncUtils.waitForFxEvents();

        TableView<StudentDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isEmpty();
    }

    @Test
    public void testLoadStudentsTableEmptyWhenNoStudents() throws Exception {
        clearTablesAndResetAutoIncrement();
        GUI_CheckListOfStudentsController controller = loader.getController();
        interact(controller::initialize);
        WaitForAsyncUtils.waitForFxEvents();

        TableView<StudentDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isEmpty();
    }

    @Test
    public void testRegisterStudentButtonExists() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        Button registerButton = lookup("#registerStudentButton").query();
        assertThat(registerButton).isNotNull();
    }

    @Test
    public void testDeleteButtonDisabledWhenNoStudentSelected() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        Button deleteButton = lookup("#deleteStudentButton").query();
        assertThat(deleteButton.isDisable()).isTrue();
    }

    @Test
    public void testDeleteButtonEnabledWhenStudentSelected() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<StudentDTO> tableView = lookup("#tableView").query();
        interact(() -> tableView.getSelectionModel().select(0));

        Button deleteButton = lookup("#deleteStudentButton").query();
        assertThat(deleteButton.isDisable()).isFalse();
    }

    @Test
    public void testDeleteStudent() throws Exception {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<StudentDTO> tableView = lookup("#tableView").query();
        interact(() -> tableView.getSelectionModel().select(0));

        Button deleteButton = lookup("#deleteStudentButton").query();
        clickOn(deleteButton);
        WaitForAsyncUtils.waitForFxEvents();

        Label confirmLabel = lookup("#confirmMessageLabel").query();
        Stage confirmStage = (Stage) confirmLabel.getScene().getWindow();
        targetWindow(confirmStage);

        Button confirmButton = lookup("#okButton").queryButton();
        clickOn(confirmButton);
        WaitForAsyncUtils.waitForFxEvents();

        targetWindow(window("Lista de Estudiantes"));

        ChoiceBox<String> filterChoiceBox = lookup("#filterChoiceBox").query();
        interact(() -> filterChoiceBox.setValue("Activos"));
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(tableView.getItems()).isEmpty();

        String sql = "SELECT estado FROM estudiante WHERE matricula = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, testStudentTuition);
            try (ResultSet rs = statement.executeQuery()) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getInt("estado")).isEqualTo(0);
            }
        }
    }
}