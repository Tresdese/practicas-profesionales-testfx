package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.LinkedOrganizationDAO;
import logic.DAO.ProjectDAO;
import logic.DAO.ProjectPresentationDAO;
import logic.DAO.UserDAO;
import logic.DTO.*;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectPresentationDAOTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private UserDAO userDAO;
    private LinkedOrganizationDAO linkedOrganizationDAO;
    private ProjectDAO projectDAO;
    private ProjectPresentationDAO projectPresentationDAO;

    private int userId;
    private int organizationId;
    private String projectId;

    @BeforeAll
    void setUpAll() throws Exception {
        connectionDB = new ConnectionDataBase();
        connection = connectionDB.connectDB();
        userDAO = new UserDAO();
        linkedOrganizationDAO = new LinkedOrganizationDAO();
        projectDAO = new ProjectDAO();
        projectPresentationDAO = new ProjectPresentationDAO();

        clearTablesAndResetAutoIncrement();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createBaseUserOrganizationAndProject();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS=0");
        stmt.execute("TRUNCATE TABLE presentacion_proyecto");
        stmt.execute("TRUNCATE TABLE proyecto");
        stmt.execute("TRUNCATE TABLE usuario");
        stmt.execute("TRUNCATE TABLE organizacion_vinculada");
        stmt.execute("ALTER TABLE presentacion_proyecto AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        stmt.execute("SET FOREIGN_KEY_CHECKS=1");
        stmt.close();
    }

    private void createBaseUserOrganizationAndProject() throws SQLException {
        LinkedOrganizationDTO organization = new LinkedOrganizationDTO(null, "Org Test", "Dirección Test");
        organizationId = Integer.parseInt(linkedOrganizationDAO.insertLinkedOrganizationAndGetId(organization));

        UserDTO user = new UserDTO(null, "12345", "Nombre", "Apellido", "usuarioTest", "passTest", Role.ACADEMICO);
        userId = insertUserAndGetId(user);

        ProjectDTO project = new ProjectDTO(
                null,
                "Proyecto Test",
                "Descripción Test",
                new java.sql.Timestamp(System.currentTimeMillis()),
                new java.sql.Timestamp(System.currentTimeMillis()),
                String.valueOf(userId),
                organizationId
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
    void tearDown() throws Exception {
        clearTablesAndResetAutoIncrement();
    }

    @Test
    void insertProjectPresentationSuccessfully() throws Exception {
        ProjectPresentationDTO presentation = new ProjectPresentationDTO(
                1,
                projectId,
                new java.sql.Timestamp(System.currentTimeMillis()),
                Tipe.Parcial
        );
        boolean inserted = projectPresentationDAO.insertProjectPresentation(presentation);
        assertTrue(inserted);

        List<ProjectPresentationDTO> presentations = projectPresentationDAO.getAllProjectPresentations();
        assertEquals(1, presentations.size());
        assertEquals(projectId, presentations.get(0).getIdProject());
    }

    @Test
    void searchProjectPresentationByIdSuccessfully() throws Exception {
        ProjectPresentationDTO presentation = new ProjectPresentationDTO(
                1,
                projectId,
                new java.sql.Timestamp(System.currentTimeMillis()),
                Tipe.Final
        );
        projectPresentationDAO.insertProjectPresentation(presentation);

        List<ProjectPresentationDTO> presentations = projectPresentationDAO.getAllProjectPresentations();
        assertFalse(presentations.isEmpty());
        int id = presentations.get(0).getIdPresentation();

        ProjectPresentationDTO found = projectPresentationDAO.searchProjectPresentationById(id);
        assertNotNull(found);
        assertEquals(Tipe.Final, found.getTipe());
    }

    @Test
    void updateProjectPresentationSuccessfully() throws Exception {
        ProjectPresentationDTO presentation = new ProjectPresentationDTO(
                1,
                projectId,
                new java.sql.Timestamp(System.currentTimeMillis()),
                Tipe.Parcial
        );
        projectPresentationDAO.insertProjectPresentation(presentation);

        List<ProjectPresentationDTO> presentations = projectPresentationDAO.getAllProjectPresentations();
        ProjectPresentationDTO toUpdate = presentations.get(0);
        toUpdate.setTipe(Tipe.Final);

        boolean updated = projectPresentationDAO.updateProjectPresentation(toUpdate);
        assertTrue(updated);

        ProjectPresentationDTO updatedPresentation = projectPresentationDAO.searchProjectPresentationById(toUpdate.getIdPresentation());
        assertEquals(Tipe.Final, updatedPresentation.getTipe());
    }

    @Test
    void deleteProjectPresentationSuccessfully() throws Exception {
        ProjectPresentationDTO presentation = new ProjectPresentationDTO(
                1,
                projectId,
                new java.sql.Timestamp(System.currentTimeMillis()),
                Tipe.Parcial
        );
        projectPresentationDAO.insertProjectPresentation(presentation);

        List<ProjectPresentationDTO> presentations = projectPresentationDAO.getAllProjectPresentations();
        int id = presentations.get(0).getIdPresentation();

        boolean deleted = projectPresentationDAO.deleteProjectPresentation(id);
        assertTrue(deleted);

        ProjectPresentationDTO found = projectPresentationDAO.searchProjectPresentationById(id);
        assertNull(found);
    }

    @Test
    void searchProjectPresentationsByProjectIdSuccessfully() throws Exception {
        ProjectPresentationDTO presentation1 = new ProjectPresentationDTO(
                1,
                projectId,
                new java.sql.Timestamp(System.currentTimeMillis()),
                Tipe.Parcial
        );
        ProjectPresentationDTO presentation2 = new ProjectPresentationDTO(
                2,
                projectId,
                new java.sql.Timestamp(System.currentTimeMillis()),
                Tipe.Final
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
                Tipe.Parcial
        );
        assertThrows(SQLException.class, () -> {
            projectPresentationDAO.insertProjectPresentation(invalidPresentation);
        });
    }

    @Test
    void getAllProjectPresentationsWhenEmpty() throws Exception {
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
    void updateNonExistentProjectPresentation() throws Exception {
        ProjectPresentationDTO nonExistent = new ProjectPresentationDTO(
                9999,
                projectId,
                new java.sql.Timestamp(System.currentTimeMillis()),
                Tipe.Parcial
        );
        boolean updated = projectPresentationDAO.updateProjectPresentation(nonExistent);
        assertFalse(updated);
    }

    @Test
    void deleteNonExistentProjectPresentation() throws Exception {
        boolean deleted = projectPresentationDAO.deleteProjectPresentation(9999);
        assertFalse(deleted);
    }

    @Test
    void insertDuplicateProjectPresentation() throws Exception {
        ProjectPresentationDTO presentation = new ProjectPresentationDTO(
                1, projectId, new java.sql.Timestamp(System.currentTimeMillis()), Tipe.Parcial
        );
        assertTrue(projectPresentationDAO.insertProjectPresentation(presentation));
        ProjectPresentationDTO duplicate = new ProjectPresentationDTO(
                1, projectId, new java.sql.Timestamp(System.currentTimeMillis()), Tipe.Parcial
        );
        assertThrows(SQLException.class, () -> {
            projectPresentationDAO.insertProjectPresentation(duplicate);
        });
    }

    @Test
    void searchProjectPresentationsByTypeSuccessfully() throws Exception {
        ProjectPresentationDTO parcial = new ProjectPresentationDTO(
                1, projectId, new java.sql.Timestamp(System.currentTimeMillis()), Tipe.Parcial
        );
        ProjectPresentationDTO fin = new ProjectPresentationDTO(
                2, projectId, new java.sql.Timestamp(System.currentTimeMillis()), Tipe.Final
        );
        projectPresentationDAO.insertProjectPresentation(parcial);
        projectPresentationDAO.insertProjectPresentation(fin);

        List<ProjectPresentationDTO> all = projectPresentationDAO.getAllProjectPresentations();
        long countParcial = all.stream().filter(p -> p.getTipe() == Tipe.Parcial).count();
        long countFinal = all.stream().filter(p -> p.getTipe() == Tipe.Final).count();
        assertEquals(1, countParcial);
        assertEquals(1, countFinal);
    }

    @Test
    void getUpcomingPresentationsOrderByDateSuccessfully() throws Exception {
        long now = System.currentTimeMillis();
        ProjectPresentationDTO future1 = new ProjectPresentationDTO(
                1, projectId, new java.sql.Timestamp(now + 86400000), Tipe.Parcial
        );
        ProjectPresentationDTO future2 = new ProjectPresentationDTO(
                2, projectId, new java.sql.Timestamp(now + 172800000), Tipe.Final
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
                1, projectId, null, Tipe.Parcial
        );
        assertThrows(SQLException.class, () -> {
            projectPresentationDAO.insertProjectPresentation(nullDate);
        });
    }

    @Test
    void partialUpdateProjectPresentationSuccessfully() throws Exception {
        ProjectPresentationDTO presentation = new ProjectPresentationDTO(
                1, projectId, new java.sql.Timestamp(System.currentTimeMillis()), Tipe.Parcial
        );
        projectPresentationDAO.insertProjectPresentation(presentation);

        List<ProjectPresentationDTO> list = projectPresentationDAO.getAllProjectPresentations();
        ProjectPresentationDTO toUpdate = list.get(0);
        Timestamp oldDate = toUpdate.getDate();
        toUpdate.setTipe(Tipe.Final);
        boolean updated = projectPresentationDAO.updateProjectPresentation(toUpdate);
        assertTrue(updated);

        ProjectPresentationDTO updatedPresentation = projectPresentationDAO.searchProjectPresentationById(toUpdate.getIdPresentation());
        assertEquals(Tipe.Final, updatedPresentation.getTipe());
        assertEquals(oldDate, updatedPresentation.getDate());
    }

    @Test
    void bulkInsertProjectPresentationsSuccessfully() throws Exception {
        int count = 10;
        for (int i = 1; i <= count; i++) {
            ProjectPresentationDTO pres = new ProjectPresentationDTO(
                    i, projectId, new java.sql.Timestamp(System.currentTimeMillis() + i * 1000), Tipe.Parcial
            );
            assertTrue(projectPresentationDAO.insertProjectPresentation(pres));
        }
        List<ProjectPresentationDTO> presentations = projectPresentationDAO.getAllProjectPresentations();
        assertEquals(count, presentations.size());
    }
}