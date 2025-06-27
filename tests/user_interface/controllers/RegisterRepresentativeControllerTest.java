package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_RegisterRepresentativeController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import logic.DTO.DepartmentDTO;
import logic.DTO.LinkedOrganizationDTO;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.*;
import java.io.IOException;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RegisterRepresentativeControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private int testOrganizationId;
    private int testDepartmentId;
    private FXMLLoader loader;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterRepresentative.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Registrar representante");
        stage.setScene(scene);
        stage.show();
    }

    @BeforeAll
    void setUpAll() throws Exception {
        connectToDatabase();
        clearTablesAndResetAutoIncrement();
        createBaseOrganizationAndDepartment();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createBaseOrganizationAndDepartment();
    }

    void connectToDatabase() throws SQLException, IOException {
        if (connection == null || connection.isClosed()) {
            connectionDB = new ConnectionDataBase();
            connection = connectionDB.connectDB();
        }
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SET FOREIGN_KEY_CHECKS=0");
        statement.execute("DELETE FROM representante");
        statement.execute("DELETE FROM organizacion_vinculada");
        statement.execute("DELETE FROM departamento");
        statement.execute("ALTER TABLE representante AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE departamento AUTO_INCREMENT = 1");
        statement.execute("SET FOREIGN_KEY_CHECKS=1");
        statement.close();
    }

    private void createBaseOrganizationAndDepartment() throws SQLException {
        String orgSql = "INSERT INTO organizacion_vinculada (nombre, direccion) VALUES ('Org Test', 'Direccion Test')";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(orgSql, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                testOrganizationId = rs.getInt(1);
            }
        }

        String deptSql = "INSERT INTO departamento (nombre, descripcion, idOrganizacion) VALUES ('Dept test', 'Desc', ?)";
        try (PreparedStatement statement = connection.prepareStatement(deptSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, testOrganizationId);
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                testDepartmentId = rs.getInt(1);
            }
        }
    }

    private void forceLoadData() {
        GUI_RegisterRepresentativeController controller = loader.getController();
        interact(() -> {
            controller.initialize();
        });
    }

    @AfterAll
    void tearDownAll() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        clearTablesAndResetAutoIncrement();
    }

    @Test
    public void testSuccessRegisterRepresentative() {
        forceLoadData();

        interact(() -> {
            ChoiceBox<LinkedOrganizationDTO> organizationBox = lookup("#organizationChoiceBox").query();
            if (!organizationBox.getItems().isEmpty()) {
                organizationBox.getSelectionModel().selectFirst();
            }
        });

        WaitForAsyncUtils.waitForFxEvents();

        interact(() -> {
            ChoiceBox<DepartmentDTO> departmentBox = lookup("#departmentBox").query();
            if (!departmentBox.getItems().isEmpty()) {
                departmentBox.getSelectionModel().selectFirst();
            }
        });

        clickOn("#nameField").write("Juan");
        clickOn("#surnameField").write("Pérez");
        clickOn("#emailField").write("juan.perez@example.com");

        clickOn("#registerUserButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("¡Representante registrado exitosamente!");
    }

    @Test
    public void testRegisterRepresentativeWithoutFillFields() {
        clickOn("#registerUserButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("Todos los campos deben estar llenos.");
    }

    @Test
    public void testLoadOrganizationsOnStart() {
        forceLoadData();

        ChoiceBox<LinkedOrganizationDTO> organizationBox = lookup("#organizationChoiceBox").query();
        assertThat(organizationBox.getItems()).isNotEmpty();
        assertThat(organizationBox.getItems().size()).isGreaterThan(0);
    }

    @Test
    public void testLoadDepartmentsOnOrganizationChange() {
        forceLoadData();

        interact(() -> {
            ChoiceBox<LinkedOrganizationDTO> organizationBox = lookup("#organizationChoiceBox").query();
            if (!organizationBox.getItems().isEmpty()) {
                organizationBox.getSelectionModel().selectFirst();
            }
        });

        WaitForAsyncUtils.waitForFxEvents();

        interact(() -> {
            ChoiceBox<DepartmentDTO> departmentBox = lookup("#departmentBox").query();
            assertThat(departmentBox.getItems()).isNotEmpty();
            assertThat(departmentBox.getItems().get(0).getDepartmentId()).isEqualTo(testDepartmentId);
        });
    }
}