package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_RegisterProjectController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import logic.DAO.LinkedOrganizationDAO;
import logic.DAO.ProjectDAO;
import logic.DAO.UserDAO;
import logic.DTO.*;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;


import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RegisterProjectControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private UserDAO userDAO;
    private LinkedOrganizationDAO linkedOrganizationDAO;
    private ProjectDAO projectDAO;

    private int testUserId;
    private int testOrganizationId;
    private int testDepartmentId;
    private boolean isDatabaseActive = false;

    private FXMLLoader loader;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterProject.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setTitle("Registrar proyecto");
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
        linkedOrganizationDAO = new LinkedOrganizationDAO();
        projectDAO = new ProjectDAO();
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
        createBaseUserAndOrganization();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SET FOREIGN_KEY_CHECKS=0");
        statement.execute("DELETE FROM proyecto");
        statement.execute("DELETE FROM usuario");
        statement.execute("DELETE FROM departamento");
        statement.execute("DELETE FROM organizacion_vinculada");
        statement.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        statement.execute("SET FOREIGN_KEY_CHECKS=1");
        statement.close();
    }

    private void createBaseUserAndOrganization() throws SQLException, IOException {
        LinkedOrganizationDTO organization = new LinkedOrganizationDTO(null, "Org Test", "Dirección Test", 1);
        testOrganizationId = Integer.parseInt(linkedOrganizationDAO.insertLinkedOrganizationAndGetId(organization));

        testDepartmentId = createTestDepartment();

        UserDTO user = new UserDTO(null, 1, "12345", "Nombre", "Apellido", "usuarioTest", "passTest", Role.ACADEMIC);
        testUserId = insertUserAndGetId(user);
    }

    private int insertUserAndGetId(UserDTO user) throws SQLException, IOException {
        String sql = "INSERT INTO usuario (numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getStaffNumber());
            statement.setString(2, user.getNames());
            statement.setString(3, user.getSurnames());
            statement.setString(4, user.getUserName());
            statement.setString(5, user.getPassword());
            statement.setString(6, user.getRole().toString());
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el id del usuario insertado");
    }

    private int createTestDepartment() throws SQLException {
        String sql = "INSERT INTO departamento (nombre, descripcion, idOrganizacion) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "Dept test");
            statement.setString(2, "Description test");
            statement.setInt(3, testOrganizationId);
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el id del departamento insertado");
    }

    private void forceLoadData() {
        GUI_RegisterProjectController controller = loader.getController();
        interact(() -> {
            controller.loadAcademics();
            controller.loadOrganizations();
            controller.loadDepartments();
        });
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
    public void testSuccessProjectRegister() throws SQLException, IOException {
        forceLoadData();

        clickOn("#nameField").write("Proyecto Test");
        clickOn("#descriptionField").write("Descripción de prueba");

        interact(() -> {
            ChoiceBox<UserDTO> academicBox = lookup("#academicChoiceBox").query();
            ChoiceBox<LinkedOrganizationDTO> organizationBox = lookup("#organizationChoiceBox").query();
            ChoiceBox<DepartmentDTO> departmentBox = lookup("#departmentChoiceBox").query();

            if (!academicBox.getItems().isEmpty()) {
                academicBox.getSelectionModel().selectFirst();
            }
            if (!organizationBox.getItems().isEmpty()) {
                organizationBox.getSelectionModel().selectFirst();
            }
            if (!departmentBox.getItems().isEmpty()) {
                departmentBox.getSelectionModel().selectFirst();
            }

            ((DatePicker) lookup("#startDatePicker").query()).setValue(LocalDate.of(2024, 1, 1));
            ((DatePicker) lookup("#endDatePicker").query()).setValue(LocalDate.of(2024, 12, 31));
        });

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#registerProjectButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(lookup("#statusLabel").queryLabeled()).hasText("Proyecto registrado correctamente.");
    }

    @Test
    public void testFailureProjectRegisterWithoutName() {
        forceLoadData();

        clickOn("#descriptionField").write("Descripción de prueba");

        interact(() -> {
            ChoiceBox<UserDTO> academicBox = lookup("#academicChoiceBox").query();
            academicBox.getSelectionModel().selectFirst();

            ChoiceBox<LinkedOrganizationDTO> organizationBox = lookup("#organizationChoiceBox").query();
            organizationBox.getSelectionModel().selectFirst();

            ChoiceBox<DepartmentDTO> departmentBox = lookup("#departmentChoiceBox").query();
            departmentBox.getSelectionModel().selectFirst();

            ((DatePicker) lookup("#startDatePicker").query()).setValue(LocalDate.of(2024, 1, 1));
            ((DatePicker) lookup("#endDatePicker").query()).setValue(LocalDate.of(2024, 12, 31));
        });

        clickOn("#registerProjectButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(lookup("#statusLabel").queryLabeled()).hasText("Todos los campos son obligatorios.");
    }

    @Test
    public void testFailureProjectRegisterWithoutDescription() {
        forceLoadData();

        clickOn("#nameField").write("Proyecto Test");

        interact(() -> {
            ChoiceBox<UserDTO> academicBox = lookup("#academicChoiceBox").query();
            academicBox.getSelectionModel().selectFirst();

            ChoiceBox<LinkedOrganizationDTO> organizationBox = lookup("#organizationChoiceBox").query();
            organizationBox.getSelectionModel().selectFirst();

            ChoiceBox<DepartmentDTO> departmentBox = lookup("#departmentChoiceBox").query();
            departmentBox.getSelectionModel().selectFirst();

            ((DatePicker) lookup("#startDatePicker").query()).setValue(LocalDate.of(2024, 1, 1));
            ((DatePicker) lookup("#endDatePicker").query()).setValue(LocalDate.of(2024, 12, 31));
        });

        clickOn("#registerProjectButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(lookup("#statusLabel").queryLabeled()).hasText("Todos los campos son obligatorios.");
    }

    @Test
    public void testLoadAcademicsPopulatesChoiceBox() {
        forceLoadData();

        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<UserDTO> academicBox = lookup("#academicChoiceBox").query();
        assertThat(academicBox.getItems()).isNotEmpty();

        for (UserDTO user : academicBox.getItems()) {
            assertThat(user.getRole()).isEqualTo(Role.ACADEMIC);
        }
        assertThat(academicBox.getItems().size()).isGreaterThan(0);
    }

    @Test
    public void testLoadAcademicsChoiceBoxEmptyWhenNoAcademics() throws Exception {
        clearTablesAndResetAutoIncrement();

        GUI_RegisterProjectController controller = loader.getController();
        interact(controller::loadAcademics);

        WaitForAsyncUtils.waitForFxEvents();
        ChoiceBox<UserDTO> academicBox = lookup("#academicChoiceBox").query();
        assertThat(academicBox.getItems()).isEmpty();
    }

    @Test
    public void testLoadOrganizationsPopulatesChoiceBox() {
        forceLoadData();

        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<LinkedOrganizationDTO> organizationBox = lookup("#organizationChoiceBox").query();
        assertThat(organizationBox.getItems()).isNotEmpty();

        assertThat(organizationBox.getItems().size()).isGreaterThan(0);
    }

    @Test
    public void testLoadOrganizationsChoiceEmptyWhenNoOrganizations() throws Exception {
        clearTablesAndResetAutoIncrement();
        GUI_RegisterProjectController controller = loader.getController();
        interact(controller::loadOrganizations);

        WaitForAsyncUtils.waitForFxEvents();
        ChoiceBox<LinkedOrganizationDTO> organizationBox = lookup("#organizationChoiceBox").query();
        assertThat(organizationBox.getItems()).isEmpty();
    }

    @Test
    public void testLoadDepartmentsPopulatesChoiceBox() {
        forceLoadData();

        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<DepartmentDTO> departmentBox = lookup("#departmentChoiceBox").query();
        assertThat(departmentBox.getItems()).isNotEmpty();

        assertThat(departmentBox.getItems().size()).isGreaterThan(0);
    }

    @Test
    public void testLoadDepartmentsChoiceEmptyWhenNoDepartments() throws Exception {
        clearTablesAndResetAutoIncrement();

        GUI_RegisterProjectController controller = loader.getController();
        interact(controller::loadDepartments);

        WaitForAsyncUtils.waitForFxEvents();
        ChoiceBox<DepartmentDTO> departmentBox = lookup("#departmentChoiceBox").query();
        assertThat(departmentBox.getItems()).isEmpty();
    }
}