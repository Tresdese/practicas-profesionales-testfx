package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_RegisterDepartmentController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.DAO.DepartmentDAO;
import logic.DAO.LinkedOrganizationDAO;
import logic.DTO.LinkedOrganizationDTO;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.*;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RegisterDepartmentControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private DepartmentDAO departmentDAO;
    private LinkedOrganizationDAO linkedOrganizationDAO;
    private int testOrganizationId;

    private FXMLLoader loader;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterDepartment.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setTitle("Registrar departamento");
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
        departmentDAO = new DepartmentDAO();
        linkedOrganizationDAO = new LinkedOrganizationDAO();
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
        createBaseOrganization();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SET FOREIGN_KEY_CHECKS=0");
        statement.execute("DELETE FROM departamento");
        statement.execute("DELETE FROM organizacion_vinculada");
        statement.execute("ALTER TABLE departamento AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        statement.execute("SET FOREIGN_KEY_CHECKS=1");
        statement.close();
    }

    private void createBaseOrganization() throws SQLException, IOException {
        LinkedOrganizationDTO org = new LinkedOrganizationDTO(null, "Org Test", "Dirección Test", 1);
        testOrganizationId = Integer.parseInt(linkedOrganizationDAO.insertLinkedOrganizationAndGetId(org));
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
    public void testSuccessDepartmentRegister() {
        GUI_RegisterDepartmentController controller = loader.getController();
        interact(() -> {
            controller.setOrganizationId(testOrganizationId);
        });

        clickOn("#nameField").write("Departamento de prueba");
        clickOn("#descriptionArea").write("Descripción de prueba");

        clickOn("#registerButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#messageLabel").queryLabeled()).hasText("Departamento registrado exitosamente.");
    }

    @Test
    public void testFailureDepartmentRegisterWithoutName() {

        clickOn("#descriptionArea").write("Descripción de prueba");

        clickOn("#registerButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#messageLabel").queryLabeled()).hasText("Todos los campos son obligatorios y deben ser válidos.");
    }

    @Test
    public void testFailureDepartmentRegisterWithoutDescription() {

        clickOn("#nameField").write("Departamento de prueba");

        clickOn("#registerButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#messageLabel").queryLabeled()).hasText("Todos los campos son obligatorios y deben ser válidos.");
    }

    @Test
    public void testFailureDepartmentRegisterWithoutOrganizationId() {
        GUI_RegisterDepartmentController controller = loader.getController();
        interact(() -> {
            controller.setOrganizationId(0);
        });

        clickOn("#nameField").write("Departamento de prueba");
        clickOn("#descriptionArea").write("Descripción de prueba");

        clickOn("#registerButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#messageLabel").queryLabeled()).hasText("Todos los campos son obligatorios y deben ser válidos.");
    }
}