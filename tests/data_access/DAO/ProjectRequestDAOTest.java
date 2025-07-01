package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.*;
import logic.DTO.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectRequestDAOTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private LinkedOrganizationDAO linkedOrganizationDAO;
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
    private int projectId;
    private String studentTuiton;
    private String representativeId;
    private String projectName;
    private int departmentId;

    private static final int TEST_PERIOD_ID = 1001;
    private static final int TEST_NRC = 11111;

    @BeforeAll
    void setUpAll() throws Exception {
        connectionDB = new ConnectionDataBase();
        connection = connectionDB.connectDataBase();
        linkedOrganizationDAO = new LinkedOrganizationDAO();
        userDAO = new UserDAO();
        departmentDAO = new DepartmentDAO();
        projectDAO = new ProjectDAO();
        studentDAO = new StudentDAO();
        projectRequestDAO = new ProjectRequestDAO();
        representativeDAO = new RepresentativeDAO();
        periodDAO = new PeriodDAO();
        groupDAO = new GroupDAO();

        clearTablesAndResetAutoIncrement();
        createDataBase();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createDataBase();
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
        statement.execute("TRUNCATE TABLE solicitud_proyecto");
        statement.execute("TRUNCATE TABLE estudiante");
        statement.execute("TRUNCATE TABLE grupo");
        statement.execute("TRUNCATE TABLE periodo");
        statement.execute("TRUNCATE TABLE proyecto");
        statement.execute("TRUNCATE TABLE usuario");
        statement.execute("TRUNCATE TABLE departamento");
        statement.execute("TRUNCATE TABLE organizacion_vinculada");
        statement.execute("TRUNCATE TABLE representante");
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

    private void createDataBase() throws SQLException, IOException {
        PeriodDTO period = new PeriodDTO(String.valueOf(TEST_PERIOD_ID), "Periodo Test",
                new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        periodDAO.insertPeriod(period);

        GroupDTO group = new GroupDTO(String.valueOf(TEST_NRC), "Grupo Test", null, String.valueOf(TEST_PERIOD_ID));
        groupDAO.insertGroup(group);

        LinkedOrganizationDTO organization = new LinkedOrganizationDTO(null, "Org Test", "Dirección Test", 1);
        organizationId = Integer.parseInt(linkedOrganizationDAO.insertLinkedOrganizationAndGetId(organization));

        UserDTO user = new UserDTO(null,1,  "12345", "Nombre", "Apellido", "usuarioTest", "passTest", Role.ACADEMIC);
        userId = insertUserAndGetId(user);

        DepartmentDTO department = new DepartmentDTO(0, "Dept Test", "Descripción test", organizationId, 1);
        departmentDAO.insertDepartment(department);
        List<DepartmentDTO> departments = departmentDAO.getAllDepartmentsByOrganizationId(organizationId);
        departmentId = departments.get(0).getDepartmentId();

        RepresentativeDTO representative = new RepresentativeDTO(null, "RepName", "RepSurname", "rep@example.com", String.valueOf(organizationId), String.valueOf(departmentId), 1);
        representativeDAO.insertRepresentative(representative);
        List<RepresentativeDTO> representatives = representativeDAO.getAllRepresentatives();
        representativeId = representatives.get(0).getIdRepresentative();

        projectName = "Proyecto de Prueba";
        ProjectDTO project = new ProjectDTO(
                null,
                projectName,
                "Descripción del proyecto",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                String.valueOf(userId),
                organizationId,
                departmentId
        );
        projectDAO.insertProject(project);
        List<ProjectDTO> projects = projectDAO.getAllProjects();
        projectId = Integer.parseInt(projects.get(0).getIdProject());

        studentTuiton = "S12345678";
        StudentDTO student = new StudentDTO(studentTuiton, 1, "Juan", "Perez", "1234567890", "juan.perez@example.com", "juanperez", "password", String.valueOf(TEST_NRC), "50", 0.0);
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
            statement.setString(6, user.getRole().getDataBaseValue());
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el id del usuario insertado");
    }

    @Test
    void insertProjectRequestSuccessfully() throws SQLException, IOException {
        ProjectRequestDTO request = new ProjectRequestDTO(
                0,
                studentTuiton,
                String.valueOf(organizationId),
                String.valueOf(representativeId),
                projectName,
                "Descripción de la solicitud de proyecto",
                "Objetivo general de la solicitud",
                "Objetivos inmediatos de la solicitud",
                "Objetivos mediatos de la solicitud",
                "Metodología propuesta",
                "Recursos necesarios",
                "Actividades a realizar",
                "Responsabilidades del estudiante",
                420,
                "Lunes a Viernes",
                10,
                20,
                ProjectStatus.pending,
                null
        );

        boolean inserted = projectRequestDAO.insertProjectRequest(request);
        assertTrue(inserted, "La solicitud de proyecto debe insertarse correctamente");

        List<ProjectRequestDTO> requests = projectRequestDAO.getAllProjectRequests();
        assertFalse(requests.isEmpty(), "Debe haber al menos una solicitud de proyecto");

        ProjectRequestDTO insertedRequest = requests.get(0);
        assertEquals(studentTuiton, insertedRequest.getTuition());
        assertEquals(projectName, insertedRequest.getProjectName());
        assertEquals("Descripción de la solicitud de proyecto", insertedRequest.getDescription());
        assertEquals("Objetivo general de la solicitud", insertedRequest.getGeneralObjective());
        assertEquals(ProjectStatus.pending, insertedRequest.getStatus());
    }

    @Test
    void updateProjectRequestSuccessfully() throws SQLException, IOException {
        ProjectRequestDTO request = new ProjectRequestDTO(
                0, studentTuiton, String.valueOf(organizationId), String.valueOf(representativeId),
                projectName, "desc", "objGen", "objInm", "objMed", "met", "rec", "act", "resp",
                100, "Lunes", 5, 10, ProjectStatus.pending, null
        );
        projectRequestDAO.insertProjectRequest(request);
        List<ProjectRequestDTO> requests = projectRequestDAO.getAllProjectRequests();
        ProjectRequestDTO inserted = requests.get(0);

        inserted.setDescription("Nueva descripción");
        inserted.setGeneralObjective("Nuevo objetivo");
        inserted.setStatus(ProjectStatus.approved);
        boolean updated = projectRequestDAO.updateProjectRequest(inserted);
        assertTrue(updated);

        ProjectRequestDTO updatedRequest = projectRequestDAO.searchProjectRequestById(inserted.getRequestId());
        assertEquals("Nueva descripción", updatedRequest.getDescription());
        assertEquals("Nuevo objetivo", updatedRequest.getGeneralObjective());
        assertEquals(ProjectStatus.approved, updatedRequest.getStatus());
    }

    @Test
    void deleteProjectRequestSuccessfully() throws SQLException, IOException {
        ProjectRequestDTO request = new ProjectRequestDTO(
                0,
                studentTuiton,
                String.valueOf(organizationId),
                String.valueOf(representativeId),
                "Nombre de prueba",
                "Descripción de la solicitud de proyecto",
                "Objetivo general de la solicitud",
                "Objetivos inmediatos de la solicitud",
                "Objetivos mediatos de la solicitud",
                "Metodología propuesta",
                "Recursos necesarios",
                "Actividades a realizar",
                "Responsabilidades del estudiante",
                420,
                "Lunes a Viernes",
                10,
                20,
                ProjectStatus.pending,
                null
        );
        projectRequestDAO.insertProjectRequest(request);

        List<ProjectRequestDTO> requests = projectRequestDAO.getAllProjectRequests();
        int requestId = requests.get(0).getRequestId();

        boolean deleted = projectRequestDAO.deleteProjectRequest(requestId);
        assertTrue(deleted);

        ProjectRequestDTO deletedRequest = null;
        try {
            deletedRequest = projectRequestDAO.searchProjectRequestById(requestId);
        } catch (Exception e) {
            assertTrue(true);
            return;
        }

        assertEquals(-1, deletedRequest.getRequestId());
    }

    @Test
    void searchProjectRequestByIdSuccessfully() throws SQLException, IOException {
        ProjectRequestDTO request = new ProjectRequestDTO(
                0, studentTuiton, String.valueOf(organizationId), String.valueOf(representativeId),
                projectName, "desc", "objGen", "objInm", "objMed", "met", "rec", "act", "resp",
                100, "Lunes", 5, 10, ProjectStatus.pending, null
        );
        projectRequestDAO.insertProjectRequest(request);
        List<ProjectRequestDTO> requests = projectRequestDAO.getAllProjectRequests();
        int id = requests.get(0).getRequestId();

        ProjectRequestDTO found = projectRequestDAO.searchProjectRequestById(id);
        assertNotNull(found);
        assertEquals(id, found.getRequestId());
        assertEquals(studentTuiton, found.getTuition());
    }

    @Test
    void getAllProjectRequestsSuccessfully() throws SQLException, IOException {
        ProjectRequestDTO req1 = new ProjectRequestDTO(
                0, studentTuiton, String.valueOf(organizationId), String.valueOf(representativeId),
                projectName, "desc1", "objGen1", "objInm1", "objMed1", "met1", "rec1", "act1", "resp1",
                100, "Lunes", 5, 10, ProjectStatus.pending, null
        );
        ProjectRequestDTO req2 = new ProjectRequestDTO(
                0, studentTuiton, String.valueOf(organizationId), String.valueOf(representativeId),
                projectName + " 2", "desc2", "objGen2", "objInm2", "objMed2", "met2", "rec2", "act2", "resp2",
                200, "Martes", 15, 20, ProjectStatus.pending, null
        );
        projectRequestDAO.insertProjectRequest(req1);
        projectRequestDAO.insertProjectRequest(req2);

        List<ProjectRequestDTO> requests = projectRequestDAO.getAllProjectRequests();
        assertEquals(2, requests.size());
    }

    @Test
    void updateProjectRequestStatusSuccessfully() throws SQLException, IOException {
        ProjectRequestDTO request = new ProjectRequestDTO(
                0, studentTuiton, String.valueOf(organizationId), String.valueOf(representativeId),
                projectName, "desc", "objGen", "objInm", "objMed", "met", "rec", "act", "resp",
                100, "Lunes", 5, 10, ProjectStatus.pending, null
        );
        projectRequestDAO.insertProjectRequest(request);
        List<ProjectRequestDTO> requests = projectRequestDAO.getAllProjectRequests();
        int id = requests.get(0).getRequestId();

        boolean updated = projectRequestDAO.updateProjectRequestStatus(id, ProjectStatus.approved);
        assertTrue(updated);

        ProjectRequestDTO updatedRequest = projectRequestDAO.searchProjectRequestById(id);
        assertNotNull(updatedRequest);
        // Solo valida si el id es válido (no -1)
        if (updatedRequest.getRequestId() != -1) {
            assertEquals(id, updatedRequest.getRequestId());
            assertEquals(ProjectStatus.approved, updatedRequest.getStatus());
        } else {
            fail("No se encontró la solicitud de proyecto actualizada");
        }
    }

    @Test
    void getProjectRequestsByTuitonSuccessfully() throws SQLException, IOException {
        ProjectRequestDTO req1 = new ProjectRequestDTO(
                0, studentTuiton, String.valueOf(organizationId), String.valueOf(representativeId),
                projectName, "desc1", "objGen1", "objInm1", "objMed1", "met1", "rec1", "act1", "resp1",
                100, "Lunes", 5, 10, ProjectStatus.pending, null
        );
        ProjectRequestDTO req2 = new ProjectRequestDTO(
                0, studentTuiton, String.valueOf(organizationId), String.valueOf(representativeId),
                projectName + " 2", "desc2", "objGen2", "objInm2", "objMed2", "met2", "rec2", "act2", "resp2",
                200, "Martes", 15, 20, ProjectStatus.pending, null
        );
        projectRequestDAO.insertProjectRequest(req1);
        projectRequestDAO.insertProjectRequest(req2);

        List<ProjectRequestDTO> requests = projectRequestDAO.getProjectRequestsByTuiton(studentTuiton);
        assertEquals(2, requests.size());
        for (ProjectRequestDTO req : requests) {
            assertEquals(studentTuiton, req.getTuition());
        }
    }

    @Test
    void insertProjectRequestWithInvalidData() {
        ProjectRequestDTO request = new ProjectRequestDTO(
                0, "", "", "", "", "", "", "", "", "", "", "", "", 0, "", 0, 0, ProjectStatus.pending, null
        );
        assertThrows(NumberFormatException.class, () -> projectRequestDAO.insertProjectRequest(request));
    }

    @Test
    void updateNonExistentProjectRequest() throws SQLException, IOException {
        ProjectRequestDTO request = new ProjectRequestDTO(
                9999, studentTuiton, String.valueOf(organizationId), String.valueOf(representativeId),
                projectName, "desc", "objGen", "objInm", "objMed", "met", "rec", "act", "resp",
                100, "Lunes", 5, 10, ProjectStatus.pending, null
        );
        boolean updated = projectRequestDAO.updateProjectRequest(request);
        assertFalse(updated);
    }

    @Test
    void deleteNonExistentProjectRequest() throws SQLException, IOException {
        boolean deleted = projectRequestDAO.deleteProjectRequest(9999);
        assertFalse(deleted);
    }

    @Test
    void getProjectRequestsByStatusSuccessfully() throws SQLException, IOException {
        ProjectRequestDTO req1 = new ProjectRequestDTO(
                0, studentTuiton, String.valueOf(organizationId), String.valueOf(representativeId),
                projectName, "desc1", "objGen1", "objInm1", "objMed1", "met1", "rec1", "act1", "resp1",
                100, "Lunes", 5, 10, ProjectStatus.pending, null
        );
        ProjectRequestDTO req2 = new ProjectRequestDTO(
                0, studentTuiton, String.valueOf(organizationId), String.valueOf(representativeId),
                projectName + " 2", "desc2", "objGen2", "objInm2", "objMed2", "met2", "rec2", "act2", "resp2",
                200, "Martes", 15, 20, ProjectStatus.approved, null
        );
        projectRequestDAO.insertProjectRequest(req1);
        projectRequestDAO.insertProjectRequest(req2);

        List<ProjectRequestDTO> pending = projectRequestDAO.getProjectRequestsByStatus(ProjectStatus.pending.getDataBaseValue());
        assertEquals(1, pending.size());
        assertEquals(ProjectStatus.pending, pending.get(0).getStatus());

        List<ProjectRequestDTO> approved = projectRequestDAO.getProjectRequestsByStatus(ProjectStatus.approved.getDataBaseValue());
        assertEquals(1, approved.size());
        assertEquals(ProjectStatus.approved, approved.get(0).getStatus());
    }

    @Test
    void insertDuplicateProjectRequest() throws SQLException, IOException {
        ProjectRequestDTO request = new ProjectRequestDTO(
                0, studentTuiton, String.valueOf(organizationId), String.valueOf(representativeId),
                projectName, "desc", "objGen", "objInm", "objMed", "met", "rec", "act", "resp",
                100, "Lunes", 5, 10, ProjectStatus.pending, null
        );
        assertTrue(projectRequestDAO.insertProjectRequest(request));
        try {
            projectRequestDAO.insertProjectRequest(request);
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("Duplicate") || e.getErrorCode() == 1062);
        }
    }
}