package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_CheckProjectListController;
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
import java.util.List;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CheckProjectListControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private FXMLLoader loader;

    private LinkedOrganizationDAO organizationDAO;
    private DepartmentDAO departmentDAO;
    private ProjectDAO projectDAO;
    private StudentDAO studentDAO;
    private RepresentativeDAO representativeDAO;
    private PeriodDAO periodDAO;
    private GroupDAO groupDAO;

    private int organizationId;
    private int userId;
    private String studentTuiton;
    private String projectName;
    private int departmentId;

    private static final int TEST_PERIOD_ID = 1001;
    private static final int TEST_NRC = 11111;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckProjectList.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Lista de solicitudes de proyecto");
        stage.setScene(scene);
        stage.show();
    }

    @BeforeAll
    void setUpAll() throws Exception {
        connectionDB = new ConnectionDataBase();
        connection = connectionDB.connectDataBase();
        organizationDAO = new LinkedOrganizationDAO();
        departmentDAO = new DepartmentDAO();
        projectDAO = new ProjectDAO();
        studentDAO = new StudentDAO();
        representativeDAO = new RepresentativeDAO();
        periodDAO = new PeriodDAO();
        groupDAO = new GroupDAO();

        clearTablesAndResetAutoIncrement();
        createBaseData();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createBaseData();
    }

    @AfterEach
    void tearDown() throws Exception {
        clearTablesAndResetAutoIncrement();
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
        statement.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE representante AUTO_INCREMENT = 1");
        statement.execute("SET FOREIGN_KEY_CHECKS=1");
        statement.close();
    }

    private void createBaseData() throws SQLException, IOException {
        createPeriod();
        createGroup();
        createOrganization();
        createUser();
        createDepartment();
        createRepresentative();
        createProject();
        createStudent();
    }

    private void createPeriod() throws SQLException, IOException {
        PeriodDTO period = new PeriodDTO(
                String.valueOf(TEST_PERIOD_ID), "Periodo Test",
                new java.sql.Timestamp(System.currentTimeMillis()),
                new java.sql.Timestamp(System.currentTimeMillis() + 1000000)
        );
        periodDAO.insertPeriod(period);
    }

    private void createGroup() throws SQLException, IOException {
        GroupDTO group = new GroupDTO(
                String.valueOf(TEST_NRC), "Grupo Test", null, String.valueOf(TEST_PERIOD_ID)
        );
        groupDAO.insertGroup(group);
    }

    private void createOrganization() throws SQLException, IOException {
        LinkedOrganizationDTO organization = new LinkedOrganizationDTO(
                null, "Org Test", "Dirección Test", 1
        );
        organizationId = Integer.parseInt(organizationDAO.insertLinkedOrganizationAndGetId(organization));
    }

    private void createUser() throws SQLException {
        UserDTO user = new UserDTO(
                null, 1, "12345", "Nombre", "Apellido", "usuarioTest", "passTest", Role.ACADEMIC
        );
        userId = insertUserAndGetId(user);
    }

    private void createDepartment() throws SQLException, IOException {
        DepartmentDTO department = new DepartmentDTO(
                0, "Dept Test", "Descripción test", organizationId, 1
        );
        departmentDAO.insertDepartment(department);
        List<DepartmentDTO> departments = departmentDAO.getAllDepartmentsByOrganizationId(organizationId);
        departmentId = departments.get(0).getDepartmentId();
    }

    private void createRepresentative() throws SQLException, IOException {
        RepresentativeDTO representative = new RepresentativeDTO(
                null, "RepName", "RepSurname", "rep@example.com",
                String.valueOf(organizationId), String.valueOf(departmentId), 1
        );
        representativeDAO.insertRepresentative(representative);
        List<RepresentativeDTO> representatives = representativeDAO.getAllRepresentatives();
    }

    private void createProject() throws SQLException, IOException {
        projectName = "Proyecto de Prueba";
        ProjectDTO project = new ProjectDTO(
                null, projectName, "Descripción Test",
                new java.sql.Timestamp(System.currentTimeMillis()),
                new java.sql.Timestamp(System.currentTimeMillis() + 1000000),
                String.valueOf(userId), organizationId, departmentId
        );
        projectDAO.insertProject(project);
    }

    private void createStudent() throws SQLException, IOException {
        studentTuiton = "S12345678";
        StudentDTO student = new StudentDTO(
                studentTuiton, 1, "Juan", "Perez", "1234567890",
                "juan.perez@example.com", "juanperez", "password",
                String.valueOf(TEST_NRC), "50", 0.0
        );
        studentDAO.insertStudent(student);
    }

    private int insertUserAndGetId(UserDTO user) throws SQLException {
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

    private void forceLoadData() {
        GUI_CheckProjectListController controller = loader.getController();
        interact(controller::initialize);
    }

    @Test
    public void testLoadProjectPopulatesTable() throws SQLException, IOException {

        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<ProjectDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isNotEmpty();
        ProjectDTO project = tableView.getItems().get(0);
        assertThat(project.getName()).isEqualTo(projectName);
    }

    @Test
    public void testLoadProjectRequestsTableEmptyWhenNoRequests() throws SQLException {
        clearTablesAndResetAutoIncrement();

        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<ProjectDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isEmpty();

        Label statusLabel = lookup("#statusLabel").query();
        assertThat(statusLabel.getText()).contains("No se encontraron proyectos");
    }

    @Test
    public void testVisibleManagerButtonWhenProjectSelected() throws Exception {

        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<ProjectDTO> tableView = lookup("#tableView").query();
        interact(() -> tableView.getSelectionModel().select(0));
        WaitForAsyncUtils.waitForFxEvents();

        TableCell<?, ?> cell = (TableCell<?, ?>) lookup(".table-cell").nth(tableView.getSelectionModel().getSelectedIndex() * tableView.getColumns().size() + 6).query();
        Button manageButton = (Button) cell.getGraphic();

        assertThat(manageButton).isNotNull();
        assertThat(manageButton.isVisible()).isTrue();
    }

    @Test
    public void testSearchIdProject() throws SQLException, IOException {

        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TextField searchField = lookup("#searchField").query();
        interact(() -> searchField.setText(projectName));
        WaitForAsyncUtils.waitForFxEvents();

        TableView<ProjectDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).hasSize(1);
        assertThat(tableView.getItems().get(0).getName()).isEqualTo(projectName);
    }

    @Test
    public void testSearchWithEmptyTable() throws SQLException {
        clearTablesAndResetAutoIncrement();

        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#searchButton");

        Label statusLabel = lookup("#statusLabel").query();
        assertThat(statusLabel.getText()).contains("No se encontraron proyectos con ese ID o nombre.");
    }



}