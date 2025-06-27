package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_RegisterGroupController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import logic.DAO.GroupDAO;
import logic.DAO.PeriodDAO;
import logic.DAO.UserDAO;
import logic.DTO.*;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.*;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RegisterGroupControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private UserDAO userDAO;
    private PeriodDAO periodDAO;
    private GroupDAO groupDAO;

    private int testUserId;
    private int testPeriodId;

    private FXMLLoader loader;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterGroup.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setTitle("Registrar grupo");
        stage.setScene(scene);
        stage.show();
    }

    void connectToDatabase() throws SQLException, IOException {
        if (connection == null || connection.isClosed()) {
            connectionDB = new ConnectionDataBase();
            connection = connectionDB.connectDB();
        }
    }

    void setServices() {
        userDAO = new UserDAO();
        periodDAO = new PeriodDAO();
        groupDAO = new GroupDAO();
    }

    @BeforeAll
    void setUpAll() throws SQLException, IOException {
        connectToDatabase();
        setServices();
        clearTablesAndResetAutoIncrement();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createBaseUserAndPeriod();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SET FOREIGN_KEY_CHECKS=0");
        statement.execute("DELETE FROM grupo");
        statement.execute("DELETE FROM usuario");
        statement.execute("DELETE FROM periodo");
        statement.execute("ALTER TABLE grupo AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE periodo AUTO_INCREMENT = 1");
        statement.execute("SET FOREIGN_KEY_CHECKS=1");
        statement.close();
    }

    private void createBaseUserAndPeriod() throws SQLException, IOException {
        String periodSql = "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(periodSql)) {
            statement.setInt(1, 1);
            statement.setString(2, "Periodo de prueba");
            statement.setDate(3, java.sql.Date.valueOf("2024-01-01"));
            statement.setDate(4, java.sql.Date.valueOf("2024-12-31"));
            statement.executeUpdate();
        }

        String userSql = "INSERT INTO usuario (numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "12345");
            statement.setString(2, "Nombre");
            statement.setString(3, "Apellido");
            statement.setString(4, "usuarioTest");
            statement.setString(5, "passTest");
            statement.setString(6, Role.ACADEMICO.toString());
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    testUserId = rs.getInt(1);
                }
            }
        }
    }

    private void forceLoadData() {
        GUI_RegisterGroupController controller = loader.getController();
        interact(controller::initialize);
    }

    @AfterAll
    void tearDownAll() throws SQLException, IOException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        if (connectionDB != null) {
            connectionDB.close();
        }
    }

    @AfterEach
    void tearDown() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
    }

    @Test
    public void testSuccessGroupRegister() {
        forceLoadData();

        clickOn("#nrcField").write("13211");
        clickOn("#nameField").write("Grupo de prueba");

        interact(() -> {
            ChoiceBox<UserDTO> academicBox = lookup("#academicChoiceBox").query();
            ChoiceBox<PeriodDTO> periodBox = lookup("#periodChoiceBox").query();

            if (!academicBox.getItems().isEmpty()) {
                academicBox.getSelectionModel().selectFirst();
            }
            if (!periodBox.getItems().isEmpty()) {
                periodBox.getSelectionModel().selectFirst();
            }
        });

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#registerButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(lookup("#statusLabel").queryLabeled()).hasText("¡Grupo registrado exitosamente!");
    }

    @Test
    public void testFailureGroupRegisterWithoutNRC() {
        forceLoadData();

        clickOn("#nameField").write("Grupo de prueba");

        interact(() -> {
            ChoiceBox<UserDTO> academicBox = lookup("#academicChoiceBox").query();
            academicBox.getSelectionModel().selectFirst();

            ChoiceBox<PeriodDTO> periodBox = lookup("#periodChoiceBox").query();
            periodBox.getSelectionModel().selectFirst();
        });

        clickOn("#registerButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(lookup("#statusLabel").queryLabeled()).hasText("Todos los campos deben estar llenos.");
    }

    @Test
    public void testFailureGroupRegisterWithoutName() {
        forceLoadData();

        clickOn("#nrcField").write("12411");

        interact(() -> {
            ChoiceBox<UserDTO> academicBox = lookup("#academicChoiceBox").query();
            academicBox.getSelectionModel().selectFirst();

            ChoiceBox<PeriodDTO> periodBox = lookup("#periodChoiceBox").query();
            periodBox.getSelectionModel().selectFirst();
        });

        clickOn("#registerButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(lookup("#statusLabel").queryLabeled()).hasText("Todos los campos deben estar llenos.");
    }

    @Test
    public void testLoadAcademicsPopulatesChoiceBox() {
        forceLoadData();

        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<UserDTO> academicBox = lookup("#academicChoiceBox").query();
        assertThat(academicBox.getItems()).isNotEmpty();

        for (UserDTO user : academicBox.getItems()) {
            assertThat(user.getRole()).isEqualTo(Role.ACADEMICO);
        }
        assertThat(academicBox.getItems().size()).isGreaterThan(0);
    }

    @Test
    public void testLoadAcademicsChoiceBoxEmptyWhenNoAcademics() throws Exception {
        clearTablesAndResetAutoIncrement();

        GUI_RegisterGroupController controller = loader.getController();
        interact(controller::initialize);

        WaitForAsyncUtils.waitForFxEvents();
        ChoiceBox<UserDTO> academicBox = lookup("#academicChoiceBox").query();
        assertThat(academicBox.getItems()).isEmpty();
    }

    @Test
    public void testLoadPeriodsPopulatesChoiceBox() {
        forceLoadData();

        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<PeriodDTO> periodBox = lookup("#periodChoiceBox").query();
        assertThat(periodBox.getItems()).isNotEmpty();
        assertThat(periodBox.getItems().size()).isGreaterThan(0);
    }

    @Test
    public void testLoadPeriodsChoiceBoxEmptyWhenNoPeriods() throws Exception {
        clearTablesAndResetAutoIncrement();

        GUI_RegisterGroupController controller = loader.getController();
        interact(controller::initialize);

        WaitForAsyncUtils.waitForFxEvents();
        ChoiceBox<PeriodDTO> periodBox = lookup("#periodChoiceBox").query();
        assertThat(periodBox.getItems()).isEmpty();
    }
}