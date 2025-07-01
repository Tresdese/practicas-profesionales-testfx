package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_RegisterAcademicController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import logic.DAO.UserDAO;
import logic.DTO.Role;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RegisterAcademicControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private UserDAO userDAO;
    private FXMLLoader loader;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterAcademic.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Registrar académico");
        stage.setScene(scene);
        stage.show();
    }

    void connectToDatabase() throws SQLException, IOException {
        if (connection == null || connection.isClosed()) {
            connectionDB = new ConnectionDataBase();
            connection = connectionDB.connectDataBase();
        }
    }

    void setServices() {
        userDAO = new UserDAO();
    }

    @BeforeAll
    void setUpAll() throws SQLException, IOException {
        connectToDatabase();
        setServices();
        clearUsersTable();
    }

    @BeforeEach
    void setUp() throws SQLException {
        clearUsersTable();
    }

    private void clearUsersTable() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM usuario")) {
            statement.executeUpdate();
        }
    }

    public void clearRoles() {
        ChoiceBox<Role> roleBox = lookup("#roleBox").query();
        roleBox.getItems().clear();
    }

    private void forceLoadData() {
        GUI_RegisterAcademicController controller = loader.getController();
        interact(controller::setRoles);
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
    void tearDown() throws SQLException {
        clearUsersTable();
    }

    @Test
    public void testSuccessAcademicRegister() {
        forceLoadData();

        clickOn("#numberOfStaffField").write("12345");
        clickOn("#namesField").write("Nombre");
        clickOn("#surnamesField").write("Apellido");
        clickOn("#userField").write("usuarioTest");
        clickOn("#passwordField").write("passTest");
        clickOn("#confirmPasswordField").write("passTest");

        interact(() -> {
            ChoiceBox<Role> roleBox = lookup("#roleBox").query();
            if (!roleBox.getItems().isEmpty()) {
                roleBox.getSelectionModel().select(Role.ACADEMIC);
            }
        });

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#registerUserButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(lookup("#statusLabel").queryLabeled()).hasText("¡Académico registrado exitosamente!");
    }

    @Test
    public void testFailureAcademicRegisterWithoutFillFields() {
        clickOn("#registerUserButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(lookup("#statusLabel").queryLabeled()).hasText("Todos los campos deben estar llenos.");
    }

    @Test
    public void testMismatchPassword() {
        forceLoadData();

        clickOn("#numberOfStaffField").write("12345");
        clickOn("#namesField").write("Nombre");
        clickOn("#surnamesField").write("Apellido");
        clickOn("#userField").write("usuarioTest");
        clickOn("#passwordField").write("passTest1");
        clickOn("#confirmPasswordField").write("passTest2");

        interact(() -> {
            ChoiceBox<Role> roleBox = lookup("#roleBox").query();
            roleBox.getSelectionModel().select(Role.ACADEMIC);
        });

        clickOn("#registerUserButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(lookup("#statusLabel").queryLabeled()).hasText("Las contraseñas no coinciden.");
    }

    @Test
    public void testLoadRolesPopulatesChoiceBox() {
        forceLoadData();

        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<Role> roleBox = lookup("#roleBox").query();
        assertThat(roleBox.getItems()).isNotEmpty();
        assertThat(roleBox.getItems()).contains(Role.ACADEMIC);
        assertThat(roleBox.getItems()).contains(Role.COORDINATOR);
        assertThat(roleBox.getItems()).contains(Role.EVALUATOR_ACADEMIC);
    }

    @Test
    public void testRoleBoxIsEmptyBeforeLoadingRoles() {
        clearRoles();
        ChoiceBox<Role> roleBox = lookup("#roleBox").query();
        assertThat(roleBox.getItems()).isEmpty();
    }
}