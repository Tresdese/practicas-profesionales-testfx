package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.*;
import logic.DTO.*;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectDAOTest {

    private ConecctionDataBase connectionDB;
    private Connection connection;
    private UserDAO userDAO;
    private LinkedOrganizationDAO organizationDAO;
    private ProjectDAO projectDAO;

    private int userId;
    private int organizationId;

    @BeforeAll
    void setUpAll() throws Exception {
        connectionDB = new ConecctionDataBase();
        connection = connectionDB.connectDB();
        userDAO = new UserDAO();
        organizationDAO = new LinkedOrganizationDAO(connection);
        projectDAO = new ProjectDAO();

        limpiarTablasYResetearAutoIncrement();
    }

    @BeforeEach
    void setUp() throws Exception {
        limpiarTablasYResetearAutoIncrement();
        crearUsuarioYOrganizacionBase();
    }

    private void limpiarTablasYResetearAutoIncrement() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS=0");
        stmt.execute("TRUNCATE TABLE proyecto");
        stmt.execute("TRUNCATE TABLE usuario");
        stmt.execute("TRUNCATE TABLE organizacion_vinculada");
        stmt.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        stmt.execute("SET FOREIGN_KEY_CHECKS=1");
        stmt.close();
    }

    private void crearUsuarioYOrganizacionBase() throws SQLException {
        LinkedOrganizationDTO org = new LinkedOrganizationDTO(null, "Org Test", "Dirección Test");
        organizationId = Integer.parseInt(organizationDAO.insertLinkedOrganizationAndGetId(org));

        UserDTO user = new UserDTO(null, "12345", "Nombre", "Apellido", "usuarioTest", "passTest", Role.ACADEMICO);
        userId = insertarUsuarioYObtenerId(user);
    }

    private int insertarUsuarioYObtenerId(UserDTO user) throws SQLException {
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
        limpiarTablasYResetearAutoIncrement();
    }

    @Test
    void testInsertProject() throws Exception {
        ProjectDTO project = new ProjectDTO(
                null,
                "Proyecto Test",
                "Descripción Test",
                new java.sql.Timestamp(System.currentTimeMillis()),
                new java.sql.Timestamp(System.currentTimeMillis()),
                String.valueOf(userId),
                organizationId
        );
        boolean inserted = projectDAO.insertProject(project);
        assertTrue(inserted, "El proyecto debe insertarse correctamente");
    }

    @Test
    void testGetAllProjects() throws Exception {
        testInsertProject();
        List<ProjectDTO> projects = projectDAO.getAllProjects();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
    }

    @Test
    void testSearchProjectById() throws Exception {
        ProjectDTO project = new ProjectDTO(
                null,
                "Proyecto Buscar",
                "Descripción Buscar",
                new java.sql.Timestamp(System.currentTimeMillis()),
                new java.sql.Timestamp(System.currentTimeMillis()),
                String.valueOf(userId),
                organizationId
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
    void testUpdateProject() throws Exception {
        ProjectDTO project = new ProjectDTO(
                null,
                "Proyecto Actualizar",
                "Descripción Original",
                new java.sql.Timestamp(System.currentTimeMillis()),
                new java.sql.Timestamp(System.currentTimeMillis()),
                String.valueOf(userId),
                organizationId
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
    void testDeleteProject() throws Exception {
        ProjectDTO project = new ProjectDTO(
                null,
                "Proyecto Eliminar",
                "Descripción Eliminar",
                new java.sql.Timestamp(System.currentTimeMillis()),
                new java.sql.Timestamp(System.currentTimeMillis()),
                String.valueOf(userId),
                organizationId
        );
        projectDAO.insertProject(project);

        List<ProjectDTO> projects = projectDAO.getAllProjects();
        ProjectDTO toDelete = projects.get(0);

        boolean deleted = projectDAO.deleteProject(toDelete.getIdProject());
        assertTrue(deleted);

        ProjectDTO deletedProject = projectDAO.searchProjectById(toDelete.getIdProject());
        assertEquals("-1", deletedProject.getIdProject());
    }
}