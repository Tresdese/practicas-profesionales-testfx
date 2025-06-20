package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.*;
import logic.DTO.*;
import org.junit.jupiter.api.*;

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
        connection = connectionDB.connectDB();
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
        stmt.execute("TRUNCATE TABLE solicitud_proyecto");
        stmt.execute("TRUNCATE TABLE estudiante");
        stmt.execute("TRUNCATE TABLE grupo");
        stmt.execute("TRUNCATE TABLE periodo");
        stmt.execute("TRUNCATE TABLE proyecto");
        stmt.execute("TRUNCATE TABLE usuario");
        stmt.execute("TRUNCATE TABLE departamento");
        stmt.execute("TRUNCATE TABLE organizacion_vinculada");
        stmt.execute("TRUNCATE TABLE representante");
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

    private void createBaseData() throws SQLException {
        PeriodDTO period = new PeriodDTO(String.valueOf(TEST_PERIOD_ID), "Periodo Test",
                new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        periodDAO.insertPeriod(period);

        GroupDTO group = new GroupDTO(String.valueOf(TEST_NRC), "Grupo Test", null, String.valueOf(TEST_PERIOD_ID));
        groupDAO.insertGroup(group);

        LinkedOrganizationDTO organization = new LinkedOrganizationDTO(null, "Org Test", "Dirección Test");
        organizationId = Integer.parseInt(linkedOrganizationDAO.insertLinkedOrganizationAndGetId(organization));

        UserDTO user = new UserDTO(null, "12345", "Nombre", "Apellido", "usuarioTest", "passTest", Role.ACADEMICO);
        userId = insertUserAndGetId(user);

        DepartmentDTO department = new DepartmentDTO(0, "Dept Test", "Descripción test", organizationId);
        departmentDAO.insertDepartment(department);
        List<DepartmentDTO> departments = departmentDAO.getAllDepartmentsByOrganizationId(organizationId);
        departmentId = departments.get(0).getDepartmentId();

        RepresentativeDTO representative = new RepresentativeDTO(null, "RepName", "RepSurname", "rep@example.com", String.valueOf(organizationId), String.valueOf(departmentId));
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

    @Test
    void insertProjectRequestSuccessfully() throws Exception {
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
                "pendiente",
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
        assertEquals(ProjectStatus.pendiente, insertedRequest.getStatus());
    }

    @Test
    void updateProjectRequestSuccessfully() throws Exception {
        ProjectRequestDTO request = new ProjectRequestDTO(
                0, studentTuiton, String.valueOf(organizationId), String.valueOf(representativeId),
                projectName, "desc", "objGen", "objInm", "objMed", "met", "rec", "act", "resp",
                100, "Lunes", 5, 10, "pendiente", null
        );
        projectRequestDAO.insertProjectRequest(request);
        List<ProjectRequestDTO> requests = projectRequestDAO.getAllProjectRequests();
        ProjectRequestDTO inserted = requests.get(0);

        inserted.setDescription("Nueva descripción");
        inserted.setGeneralObjective("Nuevo objetivo");
        inserted.setStatus(ProjectStatus.aprobada);
        boolean updated = projectRequestDAO.updateProjectRequest(inserted);
        assertTrue(updated);

        ProjectRequestDTO updatedRequest = projectRequestDAO.searchProjectRequestById(inserted.getRequestId());
        assertEquals("Nueva descripción", updatedRequest.getDescription());
        assertEquals("Nuevo objetivo", updatedRequest.getGeneralObjective());
        assertEquals(ProjectStatus.aprobada, updatedRequest.getStatus());
    }

    @Test
    void deleteProjectRequestSuccessfully() throws Exception {
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
                "pendiente",
                null
        );
        projectRequestDAO.insertProjectRequest(request);

        List<ProjectRequestDTO> requests = projectRequestDAO.getAllProjectRequests();
        int requestId = requests.get(0).getRequestId();

        boolean deleted = projectRequestDAO.deleteProjectRequest(requestId);
        assertTrue(deleted);

        ProjectRequestDTO deletedRequest = projectRequestDAO.searchProjectRequestById(requestId);

        assertEquals(-1, deletedRequest.getRequestId());
        assertNull(deletedRequest.getStatus());
    }

    @Test
    void searchProjectRequestByIdSuccessfully() throws Exception {
        ProjectRequestDTO request = new ProjectRequestDTO(
                0, studentTuiton, String.valueOf(organizationId), String.valueOf(representativeId),
                projectName, "desc", "objGen", "objInm", "objMed", "met", "rec", "act", "resp",
                100, "Lunes", 5, 10, "pendiente", null
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
    void getAllProjectRequestsSuccessfully() throws Exception {
        ProjectRequestDTO req1 = new ProjectRequestDTO(
                0, studentTuiton, String.valueOf(organizationId), String.valueOf(representativeId),
                projectName, "desc1", "objGen1", "objInm1", "objMed1", "met1", "rec1", "act1", "resp1",
                100, "Lunes", 5, 10, "pendiente", null
        );
        ProjectRequestDTO req2 = new ProjectRequestDTO(
                0, studentTuiton, String.valueOf(organizationId), String.valueOf(representativeId),
                projectName + " 2", "desc2", "objGen2", "objInm2", "objMed2", "met2", "rec2", "act2", "resp2",
                200, "Martes", 15, 20, "pendiente", null
        );
        projectRequestDAO.insertProjectRequest(req1);
        projectRequestDAO.insertProjectRequest(req2);

        List<ProjectRequestDTO> requests = projectRequestDAO.getAllProjectRequests();
        assertEquals(2, requests.size());
    }

    @Test
    void updateProjectRequestStatusSuccessfully() throws Exception {
        ProjectRequestDTO request = new ProjectRequestDTO(
                0, studentTuiton, String.valueOf(organizationId), String.valueOf(representativeId),
                projectName, "desc", "objGen", "objInm", "objMed", "met", "rec", "act", "resp",
                100, "Lunes", 5, 10, "pendiente", null
        );
        projectRequestDAO.insertProjectRequest(request);
        List<ProjectRequestDTO> requests = projectRequestDAO.getAllProjectRequests();
        int id = requests.get(0).getRequestId();

        boolean updated = projectRequestDAO.updateProjectRequestStatus(id, "aprobada");
        assertTrue(updated);

        ProjectRequestDTO updatedRequest = projectRequestDAO.searchProjectRequestById(id);
        assertNotNull(updatedRequest);
        assertEquals(id, updatedRequest.getRequestId());
        assertEquals(ProjectStatus.aprobada, updatedRequest.getStatus());
    }

    @Test
    void getProjectRequestsByTuitonSuccessfully() throws Exception {
        ProjectRequestDTO req1 = new ProjectRequestDTO(
                0, studentTuiton, String.valueOf(organizationId), String.valueOf(representativeId),
                projectName, "desc1", "objGen1", "objInm1", "objMed1", "met1", "rec1", "act1", "resp1",
                100, "Lunes", 5, 10, "pendiente", null
        );
        ProjectRequestDTO req2 = new ProjectRequestDTO(
                0, studentTuiton, String.valueOf(organizationId), String.valueOf(representativeId),
                projectName + " 2", "desc2", "objGen2", "objInm2", "objMed2", "met2", "rec2", "act2", "resp2",
                200, "Martes", 15, 20, "pendiente", null
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
                0, "", "", "", "", "", "", "", "", "", "", "", "", 0, "", 0, 0, "pendiente", null
        );
        assertThrows(NumberFormatException.class, () -> projectRequestDAO.insertProjectRequest(request));
    }

    @Test
    void updateNonExistentProjectRequest() throws Exception {
        ProjectRequestDTO request = new ProjectRequestDTO(
                9999, studentTuiton, String.valueOf(organizationId), String.valueOf(representativeId),
                projectName, "desc", "objGen", "objInm", "objMed", "met", "rec", "act", "resp",
                100, "Lunes", 5, 10, "pendiente", null
        );
        boolean updated = projectRequestDAO.updateProjectRequest(request);
        assertFalse(updated);
    }

    @Test
    void deleteNonExistentProjectRequest() throws Exception {
        boolean deleted = projectRequestDAO.deleteProjectRequest(9999);
        assertFalse(deleted);
    }

    @Test
    void getProjectRequestsByStatusSuccessfully() throws Exception {
        ProjectRequestDTO req1 = new ProjectRequestDTO(
                0, studentTuiton, String.valueOf(organizationId), String.valueOf(representativeId),
                projectName, "desc1", "objGen1", "objInm1", "objMed1", "met1", "rec1", "act1", "resp1",
                100, "Lunes", 5, 10, "pendiente", null
        );
        ProjectRequestDTO req2 = new ProjectRequestDTO(
                0, studentTuiton, String.valueOf(organizationId), String.valueOf(representativeId),
                projectName + " 2", "desc2", "objGen2", "objInm2", "objMed2", "met2", "rec2", "act2", "resp2",
                200, "Martes", 15, 20, "aprobada", null
        );
        projectRequestDAO.insertProjectRequest(req1);
        projectRequestDAO.insertProjectRequest(req2);

        List<ProjectRequestDTO> pending = projectRequestDAO.getProjectRequestsByStatus("pendiente");
        assertEquals(1, pending.size());
        assertEquals(ProjectStatus.pendiente, pending.get(0).getStatus());

        List<ProjectRequestDTO> approved = projectRequestDAO.getProjectRequestsByStatus("aprobada");
        assertEquals(1, approved.size());
        assertEquals(ProjectStatus.aprobada, approved.get(0).getStatus());
    }

    @Test
    void insertDuplicateProjectRequest() throws Exception {
        ProjectRequestDTO request = new ProjectRequestDTO(
                0, studentTuiton, String.valueOf(organizationId), String.valueOf(representativeId),
                projectName, "desc", "objGen", "objInm", "objMed", "met", "rec", "act", "resp",
                100, "Lunes", 5, 10, "pendiente", null
        );
        assertTrue(projectRequestDAO.insertProjectRequest(request));
        try {
            projectRequestDAO.insertProjectRequest(request);
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("Duplicate") || e.getErrorCode() == 1062);
        }
    }
}