package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_RegisterStudentController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import logic.DAO.GroupDAO;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.*;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RegisterStudentControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private GroupDAO groupDAO;
    private int testNRC;
    private FXMLLoader loader;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterStudent.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Registrar estudiante");
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
        groupDAO = new GroupDAO();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createTestGroup();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SET FOREIGN_KEY_CHECKS=0");
        statement.execute("DELETE FROM estudiante");
        statement.execute("DELETE FROM grupo");
        statement.execute("DELETE FROM periodo");
        statement.execute("ALTER TABLE estudiante AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE grupo AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE periodo AUTO_INCREMENT = 1");
        statement.execute("SET FOREIGN_KEY_CHECKS=1");
        statement.close();
    }

    private void createTestGroup() throws SQLException {
        String periodoSql = "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(periodoSql)) {
            statement.setInt(1, 1001);
            statement.setString(2, "Periodo Test");
            statement.setDate(3, Date.valueOf("2024-01-01"));
            statement.setDate(4, Date.valueOf("2024-12-31"));
            statement.executeUpdate();
        }
        String grupoSql = "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(grupoSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, 11111);
            statement.setString(2, "Grupo Test");
            statement.setNull(3, Types.INTEGER);
            statement.setInt(4, 1001);
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                testNRC = 11111;
            }
        }
    }

    private void forceLoadData() {
        GUI_RegisterStudentController controller = loader.getController();
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
    public void testSuccessStudentRegister() {
        forceLoadData();

        clickOn("#tuitionField").write("S12345678");
        clickOn("#namesField").write("Juan");
        clickOn("#surnamesField").write("Pérez");
        clickOn("#phoneField").write("1234567890");
        clickOn("#emailField").write("juan.perez@example.com");
        clickOn("#userField").write("juanperez");
        clickOn("#passwordField").write("Password1234@");
        clickOn("#confirmPasswordField").write("Password1234@");
        clickOn("#creditAdvanceField").write("50");

        interact(() -> {
            ChoiceBox<String> nrcChoiceBox = lookup("#nrcChoiceBox").query();
            if (!nrcChoiceBox.getItems().isEmpty()) {
                nrcChoiceBox.getSelectionModel().selectFirst();
            }
        });

        clickOn("#registerStudentButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("¡Estudiante registrado exitosamente!");
    }

    @Test
    public void testPasswordsMismatch() {
        forceLoadData();

        clickOn("#tuitionField").write("S12345678");
        clickOn("#namesField").write("Juan");
        clickOn("#surnamesField").write("Pérez");
        clickOn("#phoneField").write("1234567890");
        clickOn("#emailField").write("juan.perez@example.com");
        clickOn("#userField").write("juanperez");
        clickOn("#passwordField").write("Password1234@");
        clickOn("#confirmPasswordField").write("Password1234!");
        clickOn("#creditAdvanceField").write("50");

        interact(() -> {
            ChoiceBox<String> nrcChoiceBox = lookup("#nrcChoiceBox").query();
            if (!nrcChoiceBox.getItems().isEmpty()) {
                nrcChoiceBox.getSelectionModel().selectFirst();
            }
        });

        clickOn("#registerStudentButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("Las contraseñas no coinciden.");
    }

    @Test
    public void testPasswordLesserThanTwelveCharacters() {
        forceLoadData();

        clickOn("#tuitionField").write("S12345678");
        clickOn("#namesField").write("Juan");
        clickOn("#surnamesField").write("Pérez");
        clickOn("#phoneField").write("1234567890");
        clickOn("#emailField").write("juan.perez@example.com");
        clickOn("#userField").write("juanperez");
        clickOn("#passwordField").write("password123");
        clickOn("#confirmPasswordField").write("password123");
        clickOn("#creditAdvanceField").write("50");

        interact(() -> {
            ChoiceBox<String> nrcChoiceBox = lookup("#nrcChoiceBox").query();
            if (!nrcChoiceBox.getItems().isEmpty()) {
                nrcChoiceBox.getSelectionModel().selectFirst();
            }
        });

        clickOn("#registerStudentButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("La contraseña debe tener al menos 12 caracteres.");
    }

    @Test
    public void testWrongPasswordFormat() {
        forceLoadData();

        clickOn("#tuitionField").write("S12345678");
        clickOn("#namesField").write("Juan");
        clickOn("#surnamesField").write("Pérez");
        clickOn("#phoneField").write("1234567890");
        clickOn("#emailField").write("juan.perez@example.com");
        clickOn("#userField").write("juanperez");
        clickOn("#passwordField").write("password1234");
        clickOn("#confirmPasswordField").write("password1234");
        clickOn("#creditAdvanceField").write("50");

        interact(() -> {
            ChoiceBox<String> nrcChoiceBox = lookup("#nrcChoiceBox").query();
            if (!nrcChoiceBox.getItems().isEmpty()) {
                nrcChoiceBox.getSelectionModel().selectFirst();
            }
        });

        clickOn("#registerStudentButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("La contraseña debe contener al menos una mayúscula, una minúscula, un número y un símbolo.");
    }

    @Test
    public void testRegisterStudentWithoutFillFields() {
        forceLoadData();

        clickOn("#registerStudentButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("Todos los campos deben estar llenos.");
    }

    @Test
    public void testLoadNRCsPopulatesChoiceBox() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<String> nrcChoiceBox = lookup("#nrcChoiceBox").query();
        assertThat(nrcChoiceBox.getItems()).isNotEmpty();
        assertThat(nrcChoiceBox.getItems().get(0)).isEqualTo("11111");
    }

    @Test
    public void testLoadNRCsChoiceBoxEmptyWhenNoGroups() throws Exception {
        clearTablesAndResetAutoIncrement();

        GUI_RegisterStudentController controller = loader.getController();
        interact(controller::initialize);

        WaitForAsyncUtils.waitForFxEvents();
        ChoiceBox<String> nrcChoiceBox = lookup("#nrcChoiceBox").query();
        assertThat(nrcChoiceBox.getItems()).isEmpty();
    }
}