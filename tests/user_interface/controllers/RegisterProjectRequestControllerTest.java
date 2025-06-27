package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_RegisterProjectRequestController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.DAO.*;
import logic.DTO.*;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.*;
import java.sql.Timestamp;
import java.util.List;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RegisterProjectRequestControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private LinkedOrganizationDAO linkedOrganizationDAO;
    private DepartmentDAO departmentDAO;
    private RepresentativeDAO representativeDAO;
    private ProjectDAO projectDAO;
    private StudentDAO studentDAO;
    private PeriodDAO periodDAO;
    private GroupDAO groupDAO;

    private int organizationId;
    private int departmentId;
    private String representativeId;
    private int projectId;
    private int academicId;
    private String studentTuiton = "S12345678";
    private static final int TEST_PERIOD_ID = 1001;
    private static final int TEST_NRC = 11111;

    private FXMLLoader loader;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterProjectRequest.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Registrar solicitud de proyecto");
        stage.setScene(scene);
        stage.show();
    }

    @BeforeAll
    void setUpAll() throws Exception {
        connectToDatabase();
        setServices();
        clearTablesAndResetAutoIncrement();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createBaseData();
    }

    private void connectToDatabase() throws SQLException, IOException {
        if (connection == null || connection.isClosed()) {
            connectionDB = new ConnectionDataBase();
            connection = connectionDB.connectDB();
        }
    }

    private void setServices() {
        linkedOrganizationDAO = new LinkedOrganizationDAO();
        departmentDAO = new DepartmentDAO();
        representativeDAO = new RepresentativeDAO();
        projectDAO = new ProjectDAO();
        studentDAO = new StudentDAO();
        periodDAO = new PeriodDAO();
        groupDAO = new GroupDAO();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SET FOREIGN_KEY_CHECKS=0");
        statement.execute("DELETE FROM solicitud_proyecto");
        statement.execute("DELETE FROM estudiante");
        statement.execute("DELETE FROM grupo");
        statement.execute("DELETE FROM periodo");
        statement.execute("DELETE FROM proyecto");
        statement.execute("DELETE FROM usuario");
        statement.execute("DELETE FROM departamento");
        statement.execute("DELETE FROM organizacion_vinculada");
        statement.execute("DELETE FROM representante");
        statement.execute("ALTER TABLE solicitud_proyecto AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE estudiante AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE grupo AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE periodo AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE departamento AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE representante AUTO_INCREMENT = 1");
        statement.execute("SET FOREIGN_KEY_CHECKS=1");
        statement.close();
    }

    private void createBaseData() throws SQLException, IOException {
        createTestPeriod();
        createTestGroup();
        organizationId = createTestOrganization();
        departmentId = createTestDepartment(organizationId);
        academicId = createTestAcademic();
        representativeId = createTestRepresentative(organizationId, departmentId);
        projectId = createTestProject(organizationId, departmentId, academicId);
        createTestStudent();
    }

    private void createTestPeriod() throws SQLException, IOException {
        PeriodDTO period = new PeriodDTO(String.valueOf(TEST_PERIOD_ID), "Periodo Test",
                new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        periodDAO.insertPeriod(period);
    }

    private void createTestGroup() throws SQLException, IOException {
        GroupDTO group = new GroupDTO(String.valueOf(TEST_NRC), "Grupo Test", null, String.valueOf(TEST_PERIOD_ID));
        groupDAO.insertGroup(group);
    }

    private int createTestOrganization() throws SQLException, IOException {
        LinkedOrganizationDTO organization = new LinkedOrganizationDTO(null, "Org Test", "Dirección Test", 1);
        return Integer.parseInt(linkedOrganizationDAO.insertLinkedOrganizationAndGetId(organization));
    }

    private int createTestDepartment(int orgId) throws SQLException {
        String sqlDept = "INSERT INTO departamento (nombre, descripcion, idOrganizacion) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlDept, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "Dept test");
            statement.setString(2, "Description test");
            statement.setInt(3, orgId);
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el id del departamento insertado");
    }

    private int createTestAcademic() throws SQLException, IOException {
        UserDAO userDAO = new UserDAO();
        UserDTO user = new UserDTO(null, 1, "12345", "Nombre", "Apellido", "usuarioTest", "passTest", Role.ACADEMICO);
        String sql = "INSERT INTO usuario (numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getStaffNumber());
            stmt.setString(2, user.getNames());
            stmt.setString(3, user.getSurnames());
            stmt.setString(4, user.getUserName());
            stmt.setString(5, user.getPassword());
            stmt.setString(6, user.getRole().toString());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el id del usuario insertado");
    }

    private String createTestRepresentative(int orgId, int deptId) throws SQLException, IOException {
        RepresentativeDTO representative = new RepresentativeDTO(null, "RepName", "RepSurname", "rep@example.com", String.valueOf(orgId), String.valueOf(deptId), 1);
        representativeDAO.insertRepresentative(representative);
        List<RepresentativeDTO> reps = representativeDAO.getAllRepresentatives();
        return reps.get(0).getIdRepresentative();
    }

    private int createTestProject(int orgId, int deptId, int userId) throws SQLException, IOException {
        ProjectDTO project = new ProjectDTO(
                null,
                "Proyecto de Prueba",
                "Descripción del proyecto",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                String.valueOf(userId),
                orgId,
                deptId
        );
        projectDAO.insertProject(project);
        List<ProjectDTO> projects = projectDAO.getAllProjects();
        return Integer.parseInt(projects.get(0).getIdProject());
    }

    private void createTestStudent() throws SQLException, IOException {
        StudentDTO student = new StudentDTO(studentTuiton, 1, "Juan", "Perez", "1234567890", "juan.perez@example.com", "juanperez", "password", String.valueOf(TEST_NRC), "50", 0.0);
        studentDAO.insertStudent(student);
    }

    private void forceLoadData() {
        GUI_RegisterProjectRequestController controller = loader.getController();
        interact(() -> {
            controller.loadOrganizations();
            controller.loadRepresentatives();
            controller.loadProjects();
        });
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
    public void testSuccessProjectRequestRegister() {
        interact(this::forceLoadData);

        interact(() -> {
            GUI_RegisterProjectRequestController controller = loader.getController();
            controller.setStudent(new StudentDTO(
                    "S12345678", 1, "Juan", "Perez", "1234567890", "juan.perez@example.com", "juanperez", "password", "11111", "50", 0.0
            ));
        });

        clickOn("#descriptionArea").write("Descripción de prueba");
        clickOn("#generalObjectiveArea").write("Objetivo general");
        clickOn("#immediateObjectivesArea").write("Objetivos inmediatos");
        clickOn("#mediateObjectivesArea").write("Objetivos mediatos");
        clickOn("#methodologyArea").write("Metodología");
        clickOn("#resourcesArea").write("Recursos");
        clickOn("#activitiesArea").write("Actividades");
        clickOn("#responsibilitiesArea").write("Responsabilidades");
        clickOn("#directUsersField").write("10");
        clickOn("#indirectUsersField").write("20");
        clickOn("#scheduleTimeField").write("08:00-12:00");
        clickOn("#mondayCheck");
        clickOn("#tuesdayCheck");

        interact(() -> {
            ComboBox<?> orgBox = lookup("#organizationComboBox").query();
            if (!orgBox.getItems().isEmpty()) orgBox.getSelectionModel().selectFirst();

            ComboBox<?> repBox = lookup("#representativeComboBox").query();
            if (!repBox.getItems().isEmpty()) repBox.getSelectionModel().selectFirst();

            ComboBox<?> projBox = lookup("#projectComboBox").query();
            if (!projBox.getItems().isEmpty()) projBox.getSelectionModel().selectFirst();
        });

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#registerButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("Solicitud registrada correctamente.");
    }

    @Test
    public void testFailureProjectRequestRegisterWithoutRequiredFields() {
        interact(this::forceLoadData);

        clickOn("#registerButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("Completa todos los campos obligatorios.");
    }

    @Test
    public void testLoadOrganizationsPopulatesComboBox() {
        forceLoadData();

        ComboBox<?> organizationBox = lookup("#organizationComboBox").query();
        interact(() -> {
            if (!organizationBox.getItems().isEmpty()) {
                organizationBox.getSelectionModel().selectFirst();
            }
        });

        assertThat(organizationBox.getItems()).isNotEmpty();
        assertThat(organizationBox.getItems().size()).isGreaterThan(0);
        assertThat(organizationBox.getValue()).isNotNull();
    }

    @Test
    public void testLoadOrganizationsWithNoData() throws SQLException {
        clearTablesAndResetAutoIncrement();

        ComboBox<?> organizationBox = lookup("#organizationComboBox").query();
        assertThat(organizationBox.getItems()).isEmpty();
        assertThat(organizationBox.getValue()).isNull();
    }

    @Test
    public void testLoadRepresentativesPopulatesComboBox() {
        forceLoadData();

        ComboBox<?> organizationBox = lookup("#organizationComboBox").query();
        interact(() -> {
            if (!organizationBox.getItems().isEmpty()) {
                organizationBox.getSelectionModel().selectFirst();
            }
        });

        WaitForAsyncUtils.waitForFxEvents();

        ComboBox<?> representativeBox = lookup("#representativeComboBox").query();
        interact(() -> {
            if (!representativeBox.getItems().isEmpty()) {
                representativeBox.getSelectionModel().selectFirst();
            }
        });

        assertThat(representativeBox.getItems()).isNotEmpty();
        assertThat(representativeBox.getItems().size()).isGreaterThan(0);
        assertThat(representativeBox.getValue()).isNotNull();
    }

    @Test
    public void testLoadRepresentativesWithNoSelectedOrgnanization() throws SQLException {
        ComboBox<?> representativeBox = lookup("#representativeComboBox").query();
        assertThat(representativeBox.getItems()).isEmpty();
        assertThat(representativeBox.getValue()).isNull();
    }

    @Test
    public void testLoadProjectsPopulatesComboBox() {
        forceLoadData();

        ComboBox<?> organizationBox = lookup("#organizationComboBox").query();
        interact(() -> {
            if (!organizationBox.getItems().isEmpty()) {
                organizationBox.getSelectionModel().selectFirst();
            }
        });

        WaitForAsyncUtils.waitForFxEvents();

        ComboBox<?> projectBox = lookup("#projectComboBox").query();
        interact(() -> {
            if (!projectBox.getItems().isEmpty()) {
                projectBox.getSelectionModel().selectFirst();
            }
        });

        assertThat(projectBox.getItems()).isNotEmpty();
        assertThat(projectBox.getItems().size()).isGreaterThan(0);
        assertThat(projectBox.getValue()).isNotNull();
    }

    @Test
    public void testLoadProjectsWithNoSelectedOrganization() throws SQLException {
        ComboBox<?> projectBox = lookup("#projectComboBox").query();
        assertThat(projectBox.getItems()).isEmpty();
        assertThat(projectBox.getValue()).isNull();
    }
}