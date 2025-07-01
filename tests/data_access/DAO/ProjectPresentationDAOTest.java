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
class ProjectPresentationDAOTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private UserDAO userDAO;
    private DepartmentDAO departmentDAO;
    private LinkedOrganizationDAO linkedOrganizationDAO;
    private ProjectDAO projectDAO;
    private ProjectPresentationDAO projectPresentationDAO;

    private int userId;
    private int organizationId;
    private String projectId;
    private int departmentId;

    @BeforeAll
    void setUpAll() throws SQLException, IOException {
        connectionDB = new ConnectionDataBase();
        connection = connectionDB.connectDataBase();
        userDAO = new UserDAO();
        departmentDAO = new DepartmentDAO();
        linkedOrganizationDAO = new LinkedOrganizationDAO();
        projectDAO = new ProjectDAO();
        projectPresentationDAO = new ProjectPresentationDAO();

        clearTablesAndResetAutoIncrement();
    }

    @BeforeEach
    void setUp() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
        createBaseUserOrganizationAndProject();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS=0");
        stmt.execute("TRUNCATE TABLE presentacion_proyecto");
        stmt.execute("TRUNCATE TABLE proyecto");
        stmt.execute("TRUNCATE TABLE usuario");
        stmt.execute("TRUNCATE TABLE departamento");
        stmt.execute("TRUNCATE TABLE organizacion_vinculada");
        stmt.execute("ALTER TABLE presentacion_proyecto AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        stmt.execute("SET FOREIGN_KEY_CHECKS=1");
        stmt.close();
    }

    private void createBaseUserOrganizationAndProject() throws SQLException, IOException {
        LinkedOrganizationDTO organization = new LinkedOrganizationDTO(null, "Org Test", "Dirección Test", 1);
        organizationId = Integer.parseInt(linkedOrganizationDAO.insertLinkedOrganizationAndGetId(organization));

        DepartmentDTO department = new DepartmentDTO(0, "Dept Test", "Descripción test", organizationId, 1);
        departmentDAO.insertDepartment(department);
        List<DepartmentDTO> departments = departmentDAO.getAllDepartmentsByOrganizationId(organizationId);
        departmentId = departments.get(0).getDepartmentId();

        UserDTO user = new UserDTO(null, 1, "12345", "Nombre", "Apellido", "usuarioTest", "passTest", Role.ACADEMIC);
        userId = insertUserAndGetId(user);

        ProjectDTO project = new ProjectDTO(
                null,
                "Proyecto Test",
                "Descripción Test",
                new java.sql.Timestamp(System.currentTimeMillis()),
                new java.sql.Timestamp(System.currentTimeMillis()),
                String.valueOf(userId),
                organizationId,
                departmentId
        );
        projectDAO.insertProject(project);

        List<ProjectDTO> projects = projectDAO.getAllProjects();
        projectId = projects.get(0).getIdProject();
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
    void tearDown() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
    }

    @Test
    void insertProjectPresentationSuccessfully() throws SQLException, IOException {
        ProjectPresentationDTO presentation = new ProjectPresentationDTO(
                1,
                projectId,
                new java.sql.Timestamp(System.currentTimeMillis()),
                Type.Partial
        );
        boolean inserted = projectPresentationDAO.insertProjectPresentation(presentation);
        assertTrue(inserted);

        List<ProjectPresentationDTO> presentations = projectPresentationDAO.getAllProjectPresentations();
        assertEquals(1, presentations.size());
        assertEquals(projectId, presentations.get(0).getIdProject());
    }

    @Test
    void searchProjectPresentationByIdSuccessfully() throws SQLException, IOException {
        ProjectPresentationDTO presentation = new ProjectPresentationDTO(
                1,
                projectId,
                new java.sql.Timestamp(System.currentTimeMillis()),
                Type.Final
        );
        projectPresentationDAO.insertProjectPresentation(presentation);

        List<ProjectPresentationDTO> presentations = projectPresentationDAO.getAllProjectPresentations();
        assertFalse(presentations.isEmpty());
        int id = presentations.get(0).getIdPresentation();

        ProjectPresentationDTO found = projectPresentationDAO.searchProjectPresentationById(id);
        assertNotNull(found);
        assertEquals(Type.Final, found.getType());
    }

    @Test
    void updateProjectPresentationSuccessfully() throws SQLException, IOException {
        ProjectPresentationDTO presentation = new ProjectPresentationDTO(
                1,
                projectId,
                new java.sql.Timestamp(System.currentTimeMillis()),
                Type.Partial
        );
        projectPresentationDAO.insertProjectPresentation(presentation);

        List<ProjectPresentationDTO> presentations = projectPresentationDAO.getAllProjectPresentations();
        ProjectPresentationDTO toUpdate = presentations.get(0);
        toUpdate.setType(Type.Final);

        boolean updated = projectPresentationDAO.updateProjectPresentation(toUpdate);
        assertTrue(updated);

        ProjectPresentationDTO updatedPresentation = projectPresentationDAO.searchProjectPresentationById(toUpdate.getIdPresentation());
        assertEquals(Type.Final, updatedPresentation.getType());
    }

    @Test
    void deleteProjectPresentationSuccessfully() throws SQLException, IOException {
        ProjectPresentationDTO presentation = new ProjectPresentationDTO(1, projectId, new Timestamp(System.currentTimeMillis()), Type.Partial);
        assertTrue(projectPresentationDAO.insertProjectPresentation(presentation));

        assertTrue(projectPresentationDAO.deleteProjectPresentation(presentation.getIdPresentation()));

        ProjectPresentationDTO deleted = projectPresentationDAO.searchProjectPresentationById(presentation.getIdPresentation());
        assertNotNull(deleted);
        assertEquals(-1, deleted.getIdPresentation());
        assertEquals("N/A", deleted.getIdProject());
        assertNull(deleted.getDate());
        assertNull(deleted.getType());
    }

    @Test
    void searchProjectPresentationsByProjectIdSuccessfully() throws SQLException, IOException {
        ProjectPresentationDTO presentation1 = new ProjectPresentationDTO(
                1,
                projectId,
                new java.sql.Timestamp(System.currentTimeMillis()),
                Type.Partial
        );
        ProjectPresentationDTO presentation2 = new ProjectPresentationDTO(
                2,
                projectId,
                new java.sql.Timestamp(System.currentTimeMillis()),
                Type.Final
        );
        projectPresentationDAO.insertProjectPresentation(presentation1);
        projectPresentationDAO.insertProjectPresentation(presentation2);

        List<ProjectPresentationDTO> presentations = projectPresentationDAO.searchProjectPresentationsByProjectId(projectId);
        assertEquals(2, presentations.size());
    }

    @Test
    void insertProjectPresentationWithInvalidProjectId() {
        ProjectPresentationDTO invalidPresentation = new ProjectPresentationDTO(
                1,
                "9999",
                new java.sql.Timestamp(System.currentTimeMillis()),
                Type.Partial
        );
        assertThrows(SQLException.class, () -> {
            projectPresentationDAO.insertProjectPresentation(invalidPresentation);
        });
    }

    @Test
    void getAllProjectPresentationsWhenEmpty() throws SQLException, IOException {
        List<ProjectPresentationDTO> presentations = projectPresentationDAO.getAllProjectPresentations();
        assertNotNull(presentations);
        assertTrue(presentations.isEmpty());
    }

    @Test
    void insertProjectPresentationWithNullType() {
        ProjectPresentationDTO nullTypePresentation = new ProjectPresentationDTO(
                1,
                projectId,
                new java.sql.Timestamp(System.currentTimeMillis()),
                null
        );
        assertThrows(NullPointerException.class, () -> {
            projectPresentationDAO.insertProjectPresentation(nullTypePresentation);
        });
    }

    @Test
    void updateNonExistentProjectPresentation() throws SQLException, IOException {
        ProjectPresentationDTO nonExistent = new ProjectPresentationDTO(
                9999,
                projectId,
                new java.sql.Timestamp(System.currentTimeMillis()),
                Type.Partial
        );
        boolean updated = projectPresentationDAO.updateProjectPresentation(nonExistent);
        assertFalse(updated);
    }

    @Test
    void deleteNonExistentProjectPresentation() throws SQLException, IOException {
        boolean deleted = projectPresentationDAO.deleteProjectPresentation(9999);
        assertFalse(deleted);
    }

    @Test
    void insertDuplicateProjectPresentation() throws SQLException, IOException {
        ProjectPresentationDTO presentation = new ProjectPresentationDTO(
                1, projectId, new java.sql.Timestamp(System.currentTimeMillis()), Type.Partial
        );
        assertTrue(projectPresentationDAO.insertProjectPresentation(presentation));
        ProjectPresentationDTO duplicate = new ProjectPresentationDTO(
                1, projectId, new java.sql.Timestamp(System.currentTimeMillis()), Type.Partial
        );
        assertThrows(SQLException.class, () -> {
            projectPresentationDAO.insertProjectPresentation(duplicate);
        });
    }

    @Test
    void searchProjectPresentationsByTypeSuccessfully() throws SQLException, IOException {
        ProjectPresentationDTO parcial = new ProjectPresentationDTO(
                1, projectId, new java.sql.Timestamp(System.currentTimeMillis()), Type.Partial
        );
        ProjectPresentationDTO fin = new ProjectPresentationDTO(
                2, projectId, new java.sql.Timestamp(System.currentTimeMillis()), Type.Final
        );
        projectPresentationDAO.insertProjectPresentation(parcial);
        projectPresentationDAO.insertProjectPresentation(fin);

        List<ProjectPresentationDTO> all = projectPresentationDAO.getAllProjectPresentations();
        long countParcial = all.stream().filter(p -> p.getType() == Type.Partial).count();
        long countFinal = all.stream().filter(p -> p.getType() == Type.Final).count();
        assertEquals(1, countParcial);
        assertEquals(1, countFinal);
    }

    @Test
    void getUpcomingPresentationsOrderByDateSuccessfully() throws SQLException, IOException {
        long now = System.currentTimeMillis();
        ProjectPresentationDTO future1 = new ProjectPresentationDTO(
                1, projectId, new java.sql.Timestamp(now + 86400000), Type.Partial
        );
        ProjectPresentationDTO future2 = new ProjectPresentationDTO(
                2, projectId, new java.sql.Timestamp(now + 172800000), Type.Final
        );
        projectPresentationDAO.insertProjectPresentation(future2);
        projectPresentationDAO.insertProjectPresentation(future1);

        List<ProjectPresentationDTO> upcoming = projectPresentationDAO.getUpcomingPresentations();
        assertTrue(upcoming.size() >= 2);
        assertTrue(upcoming.get(0).getDate().before(upcoming.get(1).getDate()));
    }

    @Test
    void insertProjectPresentationWithNullDate() {
        ProjectPresentationDTO nullDate = new ProjectPresentationDTO(
                1, projectId, null, Type.Partial
        );
        assertThrows(SQLException.class, () -> {
            projectPresentationDAO.insertProjectPresentation(nullDate);
        });
    }

    @Test
    void partialUpdateProjectPresentationSuccessfully() throws SQLException, IOException {
        ProjectPresentationDTO presentation = new ProjectPresentationDTO(
                1, projectId, new java.sql.Timestamp(System.currentTimeMillis()), Type.Partial
        );
        projectPresentationDAO.insertProjectPresentation(presentation);

        List<ProjectPresentationDTO> list = projectPresentationDAO.getAllProjectPresentations();
        ProjectPresentationDTO toUpdate = list.get(0);
        Timestamp oldDate = toUpdate.getDate();
        toUpdate.setType(Type.Final);
        boolean updated = projectPresentationDAO.updateProjectPresentation(toUpdate);
        assertTrue(updated);

        ProjectPresentationDTO updatedPresentation = projectPresentationDAO.searchProjectPresentationById(toUpdate.getIdPresentation());
        assertEquals(Type.Final, updatedPresentation.getType());
        assertEquals(oldDate, updatedPresentation.getDate());
    }

    @Test
    void bulkInsertProjectPresentationsSuccessfully() throws SQLException, IOException {
        int count = 10;
        for (int i = 1; i <= count; i++) {
            ProjectPresentationDTO pres = new ProjectPresentationDTO(
                    i, projectId, new java.sql.Timestamp(System.currentTimeMillis() + i * 1000), Type.Partial
            );
            assertTrue(projectPresentationDAO.insertProjectPresentation(pres));
        }
        List<ProjectPresentationDTO> presentations = projectPresentationDAO.getAllProjectPresentations();
        assertEquals(count, presentations.size());
    }
}