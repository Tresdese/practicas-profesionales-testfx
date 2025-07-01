package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_CheckProjectRequestListController;
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
public class CheckProjectRequestListControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private FXMLLoader loader;

    private LinkedOrganizationDAO organizationDAO;
    private UserDAO userDAO;
    private DepartmentDAO departmentDAO;
    private ProjectDAO projectDAO;
    private StudentDAO studentDAO;
    private ProjectRequestDAO projectRequestDAO;
    private RepresentativeDAO representativeDAO;
    private PeriodDAO periodDAO;
    private GroupDAO groupDAO;

    private int organizationId;
    private int userId;
    private String representativeId;
    private String studentTuition;
    private String projectName;
    private int departmentId;

    private static final int TEST_PERIOD_ID = 1001;
    private static final int TEST_NRC = 11111;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckProjectRequestList.fxml"));
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
        userDAO = new UserDAO();
        departmentDAO = new DepartmentDAO();
        projectDAO = new ProjectDAO();
        studentDAO = new StudentDAO();
        projectRequestDAO = new ProjectRequestDAO();
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
        Statement stmt = connection.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS=0");
        stmt.execute("DELETE FROM solicitud_proyecto");
        stmt.execute("DELETE FROM estudiante");
        stmt.execute("DELETE FROM grupo");
        stmt.execute("DELETE FROM periodo");
        stmt.execute("DELETE FROM proyecto");
        stmt.execute("DELETE FROM usuario");
        stmt.execute("DELETE FROM departamento");
        stmt.execute("DELETE FROM organizacion_vinculada");
        stmt.execute("DELETE FROM representante");
        stmt.execute("ALTER TABLE solicitud_proyecto AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE estudiante AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE grupo AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE periodo AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE representante AUTO_INCREMENT = 1");
        stmt.execute("SET FOREIGN_KEY_CHECKS=1");
        stmt.close();
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
        createProjectRequest();
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
                null, "Organizacion prueba", "Dirección prueba", 1
        );
        organizationId = Integer.parseInt(organizationDAO.insertLinkedOrganizationAndGetId(organization));
    }

    private void createUser() throws SQLException, IOException {
        UserDTO user = new UserDTO(
                null, 1, "12345", "Nombre", "Apellido", "usuario", "contraseña", Role.ACADEMIC
        );
        userId = insertUserAndGetId(user);
    }

    private void createDepartment() throws SQLException, IOException {
        DepartmentDTO department = new DepartmentDTO(
                0, "Dpto. prueba", "Descripción prueba", organizationId, 1
        );
        departmentDAO.insertDepartment(department);
        List<DepartmentDTO> departments = departmentDAO.getAllDepartmentsByOrganizationId(organizationId);
        departmentId = departments.get(0).getDepartmentId();
    }

    private void createRepresentative() throws SQLException, IOException {
        RepresentativeDTO representative = new RepresentativeDTO(
                null, "Nombre representante", "Apellido representante", "rep@ejemplo.com",
                String.valueOf(organizationId), String.valueOf(departmentId), 1
        );
        representativeDAO.insertRepresentative(representative);
        List<RepresentativeDTO> representatives = representativeDAO.getAllRepresentatives();
        representativeId = representatives.get(0).getIdRepresentative();
    }

    private void createProject() throws SQLException, IOException {
        projectName = "Proyecto de Prueba";
        ProjectDTO project = new ProjectDTO(
                null, projectName, "Descripción prueba",
                new java.sql.Timestamp(System.currentTimeMillis()),
                new java.sql.Timestamp(System.currentTimeMillis() + 1000000),
                String.valueOf(userId), organizationId, departmentId
        );
        projectDAO.insertProject(project);
    }

    private void createStudent() throws SQLException, IOException {
        studentTuition = "S12345678";
        StudentDTO student = new StudentDTO(
                studentTuition, 1, "Juan", "Perez", "1234567890",
                "juan.perez@ejemplo.com", "juanperez", "Contraseña123!",
                String.valueOf(TEST_NRC), "50", 0.0
        );
        studentDAO.insertStudent(student);
    }

    private void createProjectRequest() throws SQLException, IOException {
        ProjectRequestDTO request = new ProjectRequestDTO(
                0, studentTuition, String.valueOf(organizationId), representativeId,
                projectName, "Descripción de la solicitud de proyecto", "Objetivo general de la solicitud",
                "Objetivos inmediatos", "Objetivos mediatos", "Metodología", "Recursos", "Actividades", "Responsabilidades",
                100, "Lunes", 5, 10, ProjectStatus.pending, null
        );
        projectRequestDAO.insertProjectRequest(request);
    }

    private int insertUserAndGetId(UserDTO user) throws SQLException, IOException {
        String sql = "INSERT INTO usuario (numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getStaffNumber());
            stmt.setString(2, user.getNames());
            stmt.setString(3, user.getSurnames());
            stmt.setString(4, user.getUserName());
            stmt.setString(5, user.getPassword());
            stmt.setString(6, user.getRole().getDataBaseValue());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el id del usuario insertado");
    }

    private void forceLoadData() {
        GUI_CheckProjectRequestListController controller = loader.getController();
        interact(controller::initialize);
    }

    @Test
    public void testLoadProjectRequestsPopulatesTable() throws SQLException, IOException {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<ProjectRequestDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isNotEmpty();
        ProjectRequestDTO projectRequest = tableView.getItems().get(0);
        assertThat(projectRequest.getProjectName()).isNotEmpty();
    }

    @Test
    public void testLoadProjectRequestsTableEmptyWhenNoRequests() throws SQLException {
        clearTablesAndResetAutoIncrement();

        forceLoadData();

        WaitForAsyncUtils.waitForFxEvents();

        TableView<ProjectRequestDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isEmpty();

        Label statusLabel = lookup("#statusLabel").query();
        assertThat(statusLabel.getText()).isIn("", "No se encontraron solicitudes");
    }

    @Test
    public void testRefreshListButton() {
        forceLoadData();

        Button refreshButton = lookup("#refreshListButton").query();
        clickOn(refreshButton);
        WaitForAsyncUtils.waitForFxEvents();

        TableView<ProjectRequestDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isNotEmpty();
    }

    @Test
    public void testSearchProjectRequestByProjectName() {
        forceLoadData();

        TextField searchField = lookup("#searchField").query();
        clickOn(searchField).write("Proyecto de Prueba");
        Button searchButton = lookup("#searchButton").query();
        clickOn(searchButton);
        WaitForAsyncUtils.waitForFxEvents();

        TableView<ProjectRequestDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).hasSize(1);
        assertThat(tableView.getItems().get(0).getProjectName()).isEqualTo("Proyecto de Prueba");
    }

    @Test
    public void testSearchProjectRequestByStudentTuition() {
        forceLoadData();

        TextField searchField = lookup("#searchField").query();
        clickOn(searchField).write(studentTuition);
        Button searchButton = lookup("#searchButton").query();
        clickOn(searchButton);
        WaitForAsyncUtils.waitForFxEvents();

        TableView<ProjectRequestDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).hasSize(1);
        assertThat(tableView.getItems().get(0).getTuition()).isEqualTo(studentTuition);
    }

    @Test
    public void testSearchProjectRequestByProjectDescription() {
        forceLoadData();

        TextField searchField = lookup("#searchField").query();
        clickOn(searchField).write("Descripción de la solicitud de proyecto");
        Button searchButton = lookup("#searchButton").query();
        clickOn(searchButton);
        WaitForAsyncUtils.waitForFxEvents();

        TableView<ProjectRequestDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).hasSize(1);
        assertThat(tableView.getItems().get(0).getDescription()).isEqualTo("Descripción de la solicitud de proyecto");
    }

    @Test
    public void testSearchProjectRequestNoResults() {
        forceLoadData();

        TextField searchField = lookup("#searchField").query();
        clickOn(searchField).write("Proyecto Inexistente");
        Button searchButton = lookup("#searchButton").query();
        clickOn(searchButton);
        WaitForAsyncUtils.waitForFxEvents();

        TableView<ProjectRequestDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isEmpty();

        Label statusLabel = lookup("#statusLabel").query();
        assertThat(statusLabel.getText()).isEqualTo("No se encontraron solicitudes que coincidan con la búsqueda.");
    }

    @Test
    public void testClearSearch() {
        forceLoadData();

        TextField searchField = lookup("#searchField").query();
        clickOn(searchField).write("Proyecto de Prueba");
        Button searchButton = lookup("#searchButton").query();
        clickOn(searchButton);
        WaitForAsyncUtils.waitForFxEvents();

        TableView<ProjectRequestDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).hasSize(1);

        Button clearSearchButton = lookup("#clearButton").query();
        clickOn(clearSearchButton);
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(searchField.getText()).isEmpty();
        assertThat(tableView.getItems()).isNotEmpty();
    }

//    @Test
//    public void testAproveProjectRequest() throws SQLException, IOException {
//        forceLoadData();
//
//        TableView<ProjectRequestDTO> tableView = lookup("#tableView").query();
//        assertThat(tableView.getItems()).isNotEmpty();
//
//        ProjectRequestDTO request = tableView.getItems().get(0);
//        Button approveButton = lookup("#approveButton").query();
//        clickOn(approveButton);
//        WaitForAsyncUtils.waitForFxEvents();
//
//        assertThat(tableView.getItems()).hasSize(1);
//        assertThat(request.getStatus()).isEqualTo(ProjectStatus.approved);
//    }

}