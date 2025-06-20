package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.*;
import logic.DTO.*;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectDAOTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private UserDAO userDAO;
    private LinkedOrganizationDAO linkedOrganizationDAO;
    private ProjectDAO projectDAO;

    private int testUserId;
    private int testOrganizationId;
    private int testDepartmentId;

    @BeforeAll
    void setUpAll() throws Exception {
        connectionDB = new ConnectionDataBase();
        connection = connectionDB.connectDB();
        userDAO = new UserDAO();
        linkedOrganizationDAO = new LinkedOrganizationDAO();
        projectDAO = new ProjectDAO();

        clearTablesAndResetAutoIncrement();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createBaseUserAndOrganization();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS=0");
        stmt.execute("TRUNCATE TABLE proyecto");
        stmt.execute("TRUNCATE TABLE usuario");
        stmt.execute("TRUNCATE TABLE departamento");
        stmt.execute("TRUNCATE TABLE organizacion_vinculada");
        stmt.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        stmt.execute("SET FOREIGN_KEY_CHECKS=1");
        stmt.close();
    }

    private void createBaseUserAndOrganization() throws SQLException {
        LinkedOrganizationDTO organization = new LinkedOrganizationDTO(null, "Org Test", "Dirección Test");
        testOrganizationId = Integer.parseInt(linkedOrganizationDAO.insertLinkedOrganizationAndGetId(organization));

        testDepartmentId = createTestDepartment();

        UserDTO user = new UserDTO(null, "12345", "Nombre", "Apellido", "usuarioTest", "passTest", Role.ACADEMICO);
        testUserId = insertUserAndGetId(user);
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

    private int createTestDepartment() throws SQLException {
        String sql = "INSERT INTO departamento (nombre, descripcion, idOrganizacion) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "Dept test");
            stmt.setString(2, "Description test");
            stmt.setInt(3, testOrganizationId);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el id del departamento insertado");
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
    void insertProjectSuccessfully() throws Exception {
        ProjectDTO project = new ProjectDTO(
                null,
                "Proyecto Test",
                "Descripción Test",
                new java.sql.Timestamp(System.currentTimeMillis()),
                new java.sql.Timestamp(System.currentTimeMillis()),
                String.valueOf(testUserId),
                testOrganizationId,
                testDepartmentId
        );
        boolean inserted = projectDAO.insertProject(project);
        assertTrue(inserted, "El proyecto debe insertarse correctamente");
    }

    @Test
    void getAllProjectsSuccessfully() throws Exception {
        insertProjectSuccessfully();
        List<ProjectDTO> projects = projectDAO.getAllProjects();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
    }

    @Test
    void searchProjectByIdSuccessfully() throws Exception {
        ProjectDTO project = new ProjectDTO(
                null,
                "Proyecto Test",
                "Descripción Test",
                new java.sql.Timestamp(System.currentTimeMillis()),
                new java.sql.Timestamp(System.currentTimeMillis()),
                String.valueOf(testUserId),
                testOrganizationId,
                testDepartmentId
        );
        projectDAO.insertProject(project);

        List<ProjectDTO> projects = projectDAO.getAllProjects();
        assertFalse(projects.isEmpty());
        ProjectDTO inserted = projects.get(0);

        ProjectDTO found = projectDAO.searchProjectById(inserted.getIdProject());
        assertNotNull(found);
        assertEquals(inserted.getName(), found.getName());
    }

    @Test
    void updateProjectSuccessfully() throws Exception {
        ProjectDTO project = new ProjectDTO(
                null,
                "Proyecto Actualizar",
                "Descripción Original",
                new java.sql.Timestamp(System.currentTimeMillis()),
                new java.sql.Timestamp(System.currentTimeMillis()),
                String.valueOf(testUserId),
                testOrganizationId,
                testDepartmentId
        );
        projectDAO.insertProject(project);

        List<ProjectDTO> projects = projectDAO.getAllProjects();
        ProjectDTO toUpdate = projects.get(0);

        toUpdate.setName("Proyecto Actualizado");
        toUpdate.setDescription("Descripción Modificada");
        boolean updated = projectDAO.updateProject(toUpdate);
        assertTrue(updated);

        ProjectDTO updatedProject = projectDAO.searchProjectById(toUpdate.getIdProject());
        assertEquals("Proyecto Actualizado", updatedProject.getName());
        assertEquals("Descripción Modificada", updatedProject.getDescription());
    }

    @Test
    void deleteProjectSuccessfully() throws Exception {
        ProjectDTO project = new ProjectDTO(
                null,
                "Proyecto Eliminar",
                "Descripción Eliminar",
                new java.sql.Timestamp(System.currentTimeMillis()),
                new java.sql.Timestamp(System.currentTimeMillis()),
                String.valueOf(testUserId),
                testOrganizationId,
                testDepartmentId
        );
        projectDAO.insertProject(project);

        List<ProjectDTO> projects = projectDAO.getAllProjects();
        ProjectDTO toDelete = projects.get(0);

        boolean deleted = projectDAO.deleteProject(toDelete.getIdProject());
        assertTrue(deleted);

        ProjectDTO deletedProject = projectDAO.searchProjectById(toDelete.getIdProject());
        assertEquals("-1", deletedProject.getIdProject());
    }

    @Test
    void insertProjectWithNullData() {
        ProjectDTO project = new ProjectDTO(null, null, null, null, null, null, 0, 0);
        assertThrows(SQLException.class, () -> projectDAO.insertProject(project),
                "No debe permitir insertar proyecto con datos nulos");
    }

    @Test
    void updateNonExistentProject() throws Exception {
        ProjectDTO project = new ProjectDTO("9999", "No existe", "Desc", new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()), String.valueOf(testUserId), testOrganizationId,
                testDepartmentId);
        boolean updated = projectDAO.updateProject(project);
        assertFalse(updated, "No debe actualizar un proyecto inexistente");
    }

    @Test
    void deleteNonExistentProject() throws Exception {
        boolean deleted = projectDAO.deleteProject("9999");
        assertFalse(deleted, "No debe eliminar un proyecto inexistente");
    }

    @Test
    void insertDuplicateProjectName() throws Exception {
        ProjectDTO project1 = new ProjectDTO(null, "Proyecto Duplicado", "Desc1",
                new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()),
                String.valueOf(testUserId), testOrganizationId, testDepartmentId);
        ProjectDTO project2 = new ProjectDTO(null, "Proyecto Duplicado", "Desc2",
                new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()),
                String.valueOf(testUserId), testOrganizationId, testDepartmentId);

        boolean inserted1 = projectDAO.insertProject(project1);
        boolean inserted2 = projectDAO.insertProject(project2);

        assertTrue(inserted1, "Debe permitir insertar el primer proyecto");
        assertTrue(inserted2, "Debe permitir insertar duplicados si la base de datos lo permite");
    }

    @Test
    void getAllProjectsWhenTableIsEmpty() throws Exception {
        clearTablesAndResetAutoIncrement();
        List<ProjectDTO> projects = projectDAO.getAllProjects();
        assertNotNull(projects);
        assertTrue(projects.isEmpty(), "La lista debe estar vacía si no hay proyectos");
    }

    @Test
    void insertAndRetrieveMultipleProjects() throws Exception {
        for (int i = 0; i < 3; i++) {
            ProjectDTO project = new ProjectDTO(null, "Proyecto" + i, "Desc" + i,
                    new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()),
                    String.valueOf(testUserId), testOrganizationId, testDepartmentId);
            projectDAO.insertProject(project);
        }
        List<ProjectDTO> projects = projectDAO.getAllProjects();
        assertTrue(projects.size() >= 3, "Debe haber al menos tres proyectos");
    }

    @Test
    void updateProjectWithNullData() throws Exception {
        ProjectDTO project = new ProjectDTO(null, "Proyecto Null", "Desc",
                new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()),
                String.valueOf(testUserId), testOrganizationId, testDepartmentId);
        projectDAO.insertProject(project);
        List<ProjectDTO> projects = projectDAO.getAllProjects();
        ProjectDTO toUpdate = projects.get(0);

        toUpdate.setName(null);
        toUpdate.setDescription(null);
        toUpdate.setIdUser(null);

        assertThrows(SQLException.class, () -> projectDAO.updateProject(toUpdate),
                "No debe permitir actualizar proyecto con datos nulos");
    }
}