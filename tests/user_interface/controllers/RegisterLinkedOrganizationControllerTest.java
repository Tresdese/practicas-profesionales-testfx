package user_interface.controllers;

import data_access.ConnectionDataBase;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.DAO.LinkedOrganizationDAO;
import logic.DTO.LinkedOrganizationDTO;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RegisterLinkedOrganizationControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private LinkedOrganizationDAO linkedOrganizationDAO;
    private FXMLLoader loader;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterLinkedOrganization.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setTitle("Registrar organización vinculada");
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
        linkedOrganizationDAO = new LinkedOrganizationDAO();
    }

    @BeforeAll
    void setUpAll() throws SQLException, IOException {
        connectToDatabase();
        setServices();
        clearTable();
    }

    @BeforeEach
    void setUp() throws SQLException {
        clearTable();
    }

    private void clearTable() throws SQLException {
        connection.createStatement().execute("DELETE FROM organizacion_vinculada");
        connection.createStatement().execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
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
        clearTable();
    }

    @Test
    public void testSuccessLinkedOrganizationRegister() {
        clickOn("#nameField").write("Organización Test");
        clickOn("#addressField").write("Dirección Test");

        clickOn("#registerButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("¡Organización registrada exitosamente!");
    }

    @Test
    public void testFailureRegisterWithoutName() {
        clickOn("#addressField").write("Dirección Test");

        clickOn("#registerButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("Todos los campos deben estar llenos.");
    }

    @Test
    public void testFailureRegisterWithoutAddress() {
        clickOn("#nameField").write("Organización Test");

        clickOn("#registerButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("Todos los campos deben estar llenos.");
    }

    @Test
    public void testFailureRegisterDuplicateName() throws SQLException, IOException {

        LinkedOrganizationDTO org = new LinkedOrganizationDTO(null, "Org Duplicada", "Dirección Duplicada", 1);
        linkedOrganizationDAO.insertLinkedOrganizationAndGetId(org);

        clickOn("#nameField").write("Org Duplicada");
        clickOn("#addressField").write("Dirección Duplicada");

        clickOn("#registerButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("Nombre de organización ya registrado.");
    }
}