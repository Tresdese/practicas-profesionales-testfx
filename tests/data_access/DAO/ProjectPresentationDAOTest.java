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

    private ConnectionDataBase connectionDataBase;
    private Connection databaseConnection;
    private UserDAO userDataAccessObject;
    private DepartmentDAO departmentDataAccessObject;
    private LinkedOrganizationDAO linkedOrganizationDataAccessObject;
    private ProjectDAO projectDataAccessObject;
    private ProjectPresentationDAO projectPresentationDataAccessObject;

    private int userId;
    private int organizationId;
    private String projectId;
    private int departmentId;

    @BeforeAll
    void setUpAll() throws SQLException, IOException {
        connectionDataBase = new ConnectionDataBase();
        databaseConnection = connectionDataBase.connectDataBase();
        userDataAccessObject = new UserDAO();
        departmentDataAccessObject = new DepartmentDAO();
        linkedOrganizationDataAccessObject = new LinkedOrganizationDAO();
        projectDataAccessObject = new ProjectDAO();
        projectPresentationDataAccessObject = new ProjectPresentationDAO();

        clearTablesAndResetAutoIncrement();
    }

    @BeforeEach
    void setUp() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
        createBaseUserOrganizationAndProject();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = databaseConnection.createStatement();
        statement.execute("SET FOREIGN_KEY_CHECKS=0");
        statement.execute("TRUNCATE TABLE presentacion_proyecto");
        statement.execute("TRUNCATE TABLE proyecto");
        statement.execute("TRUNCATE TABLE usuario");
        statement.execute("TRUNCATE TABLE departamento");
        statement.execute("TRUNCATE TABLE organizacion_vinculada");
        statement.execute("ALTER TABLE presentacion_proyecto AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        statement.execute("SET FOREIGN_KEY_CHECKS=1");
        statement.close();
    }

    private void createBaseUserOrganizationAndProject() throws SQLException, IOException {
        LinkedOrganizationDTO organization = new LinkedOrganizationDTO(null, "Org Test", "Dirección Test", 1);
        organizationId = Integer.parseInt(linkedOrganizationDataAccessObject.insertLinkedOrganizationAndGetId(organization));

        DepartmentDTO department = new DepartmentDTO(0, "Dept Test", "Descripción test", organizationId, 1);
        departmentDataAccessObject.insertDepartment(department);
        List<DepartmentDTO> departmentList = departmentDataAccessObject.getAllDepartmentsByOrganizationId(organizationId);
        departmentId = departmentList.get(0).getDepartmentId();

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
        projectDataAccessObject.insertProject(project);

        List<ProjectDTO> projectList = projectDataAccessObject.getAllProjects();
        projectId = projectList.get(0).getIdProject();
    }

    private int insertUserAndGetId(UserDTO user) throws SQLException {
        String sql = "INSERT INTO usuario (numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getStaffNumber());
            preparedStatement.setString(2, user.getNames());
            preparedStatement.setString(3, user.getSurnames());
            preparedStatement.setString(4, user.getUserName());
            preparedStatement.setString(5, user.getPassword());
            preparedStatement.setString(6, user.getRole().getDataBaseValue());
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el id del usuario insertado");
    }

    @AfterAll
    void tearDownAll() throws Exception {
        if (databaseConnection != null && !databaseConnection.isClosed()) {
            databaseConnection.close();
        }
        if (connectionDataBase != null) {
            connectionDataBase.close();
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
        boolean wasInserted = projectPresentationDataAccessObject.insertProjectPresentation(presentation);
        assertTrue(wasInserted);

        List<ProjectPresentationDTO> presentationList = projectPresentationDataAccessObject.getAllProjectPresentations();
        assertEquals(1, presentationList.size());
        assertEquals(projectId, presentationList.get(0).getIdProject());
    }

    @Test
    void searchProjectPresentationByIdSuccessfully() throws SQLException, IOException {
        ProjectPresentationDTO presentation = new ProjectPresentationDTO(
                1,
                projectId,
                new java.sql.Timestamp(System.currentTimeMillis()),
                Type.Final
        );
        projectPresentationDataAccessObject.insertProjectPresentation(presentation);

        List<ProjectPresentationDTO> presentationList = projectPresentationDataAccessObject.getAllProjectPresentations();
        assertFalse(presentationList.isEmpty());
        int id = presentationList.get(0).getIdPresentation();

        ProjectPresentationDTO foundPresentation = projectPresentationDataAccessObject.searchProjectPresentationById(id);
        assertNotNull(foundPresentation);
        assertEquals(Type.Final, foundPresentation.getType());
    }

    @Test
    void updateProjectPresentationSuccessfully() throws SQLException, IOException {
        ProjectPresentationDTO presentation = new ProjectPresentationDTO(
                1,
                projectId,
                new java.sql.Timestamp(System.currentTimeMillis()),
                Type.Partial
        );
        projectPresentationDataAccessObject.insertProjectPresentation(presentation);

        List<ProjectPresentationDTO> presentationList = projectPresentationDataAccessObject.getAllProjectPresentations();
        ProjectPresentationDTO presentationToUpdate = presentationList.get(0);
        presentationToUpdate.setType(Type.Final);

        boolean wasUpdated = projectPresentationDataAccessObject.updateProjectPresentation(presentationToUpdate);
        assertTrue(wasUpdated);

        ProjectPresentationDTO updatedPresentation = projectPresentationDataAccessObject.searchProjectPresentationById(presentationToUpdate.getIdPresentation());
        assertEquals(Type.Final, updatedPresentation.getType());
    }

    @Test
    void deleteProjectPresentationSuccessfully() throws SQLException, IOException {
        ProjectPresentationDTO presentation = new ProjectPresentationDTO(1, projectId, new Timestamp(System.currentTimeMillis()), Type.Partial);
        assertTrue(projectPresentationDataAccessObject.insertProjectPresentation(presentation));

        assertTrue(projectPresentationDataAccessObject.deleteProjectPresentation(presentation.getIdPresentation()));

        ProjectPresentationDTO deletedPresentation = projectPresentationDataAccessObject.searchProjectPresentationById(presentation.getIdPresentation());
        assertNotNull(deletedPresentation);
        assertEquals(-1, deletedPresentation.getIdPresentation());
        assertEquals("N/A", deletedPresentation.getIdProject());
        assertNull(deletedPresentation.getDate());
        assertNull(deletedPresentation.getType());
    }

    @Test
    void searchProjectPresentationsByProjectIdSuccessfully() throws SQLException, IOException {
        ProjectPresentationDTO presentationOne = new ProjectPresentationDTO(
                1,
                projectId,
                new java.sql.Timestamp(System.currentTimeMillis()),
                Type.Partial
        );
        ProjectPresentationDTO presentationTwo = new ProjectPresentationDTO(
                2,
                projectId,
                new java.sql.Timestamp(System.currentTimeMillis()),
                Type.Final
        );
        projectPresentationDataAccessObject.insertProjectPresentation(presentationOne);
        projectPresentationDataAccessObject.insertProjectPresentation(presentationTwo);

        List<ProjectPresentationDTO> presentationList = projectPresentationDataAccessObject.searchProjectPresentationsByProjectId(projectId);
        assertEquals(2, presentationList.size());
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
            projectPresentationDataAccessObject.insertProjectPresentation(invalidPresentation);
        });
    }

    @Test
    void getAllProjectPresentationsWhenEmpty() throws SQLException, IOException {
        List<ProjectPresentationDTO> presentationList = projectPresentationDataAccessObject.getAllProjectPresentations();
        assertNotNull(presentationList);
        assertTrue(presentationList.isEmpty());
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
            projectPresentationDataAccessObject.insertProjectPresentation(nullTypePresentation);
        });
    }

    @Test
    void updateNonExistentProjectPresentation() throws SQLException, IOException {
        ProjectPresentationDTO nonExistentPresentation = new ProjectPresentationDTO(
                9999,
                projectId,
                new java.sql.Timestamp(System.currentTimeMillis()),
                Type.Partial
        );
        boolean wasUpdated = projectPresentationDataAccessObject.updateProjectPresentation(nonExistentPresentation);
        assertFalse(wasUpdated);
    }

    @Test
    void deleteNonExistentProjectPresentation() throws SQLException, IOException {
        boolean wasDeleted = projectPresentationDataAccessObject.deleteProjectPresentation(9999);
        assertFalse(wasDeleted);
    }

    @Test
    void insertDuplicateProjectPresentation() throws SQLException, IOException {
        ProjectPresentationDTO presentation = new ProjectPresentationDTO(
                1, projectId, new java.sql.Timestamp(System.currentTimeMillis()), Type.Partial
        );
        assertTrue(projectPresentationDataAccessObject.insertProjectPresentation(presentation));
        ProjectPresentationDTO duplicatePresentation = new ProjectPresentationDTO(
                1, projectId, new java.sql.Timestamp(System.currentTimeMillis()), Type.Partial
        );
        assertThrows(SQLException.class, () -> {
            projectPresentationDataAccessObject.insertProjectPresentation(duplicatePresentation);
        });
    }

    @Test
    void searchProjectPresentationsByTypeSuccessfully() throws SQLException, IOException {
        ProjectPresentationDTO partialPresentation = new ProjectPresentationDTO(
                1, projectId, new java.sql.Timestamp(System.currentTimeMillis()), Type.Partial
        );
        ProjectPresentationDTO finalPresentation = new ProjectPresentationDTO(
                2, projectId, new java.sql.Timestamp(System.currentTimeMillis()), Type.Final
        );
        projectPresentationDataAccessObject.insertProjectPresentation(partialPresentation);
        projectPresentationDataAccessObject.insertProjectPresentation(finalPresentation);

        List<ProjectPresentationDTO> allPresentations = projectPresentationDataAccessObject.getAllProjectPresentations();
        long countPartial = allPresentations.stream().filter(p -> p.getType() == Type.Partial).count();
        long countFinal = allPresentations.stream().filter(p -> p.getType() == Type.Final).count();
        assertEquals(1, countPartial);
        assertEquals(1, countFinal);
    }

    @Test
    void getUpcomingPresentationsOrderByDateSuccessfully() throws SQLException, IOException {
        long now = System.currentTimeMillis();
        ProjectPresentationDTO futurePresentationOne = new ProjectPresentationDTO(
                1, projectId, new java.sql.Timestamp(now + 86400000), Type.Partial
        );
        ProjectPresentationDTO futurePresentationTwo = new ProjectPresentationDTO(
                2, projectId, new java.sql.Timestamp(now + 172800000), Type.Final
        );
        projectPresentationDataAccessObject.insertProjectPresentation(futurePresentationTwo);
        projectPresentationDataAccessObject.insertProjectPresentation(futurePresentationOne);

        List<ProjectPresentationDTO> upcomingPresentations = projectPresentationDataAccessObject.getUpcomingPresentations();
        assertTrue(upcomingPresentations.size() >= 2);
        assertTrue(upcomingPresentations.get(0).getDate().before(upcomingPresentations.get(1).getDate()));
    }

    @Test
    void insertProjectPresentationWithNullDate() {
        ProjectPresentationDTO nullDatePresentation = new ProjectPresentationDTO(
                1, projectId, null, Type.Partial
        );
        assertThrows(SQLException.class, () -> {
            projectPresentationDataAccessObject.insertProjectPresentation(nullDatePresentation);
        });
    }

    @Test
    void partialUpdateProjectPresentationSuccessfully() throws SQLException, IOException {
        ProjectPresentationDTO presentation = new ProjectPresentationDTO(
                1, projectId, new java.sql.Timestamp(System.currentTimeMillis()), Type.Partial
        );
        projectPresentationDataAccessObject.insertProjectPresentation(presentation);

        List<ProjectPresentationDTO> presentationList = projectPresentationDataAccessObject.getAllProjectPresentations();
        ProjectPresentationDTO presentationToUpdate = presentationList.get(0);
        Timestamp previousDate = presentationToUpdate.getDate();
        presentationToUpdate.setType(Type.Final);
        boolean wasUpdated = projectPresentationDataAccessObject.updateProjectPresentation(presentationToUpdate);
        assertTrue(wasUpdated);

        ProjectPresentationDTO updatedPresentation = projectPresentationDataAccessObject.searchProjectPresentationById(presentationToUpdate.getIdPresentation());
        assertEquals(Type.Final, updatedPresentation.getType());
        assertEquals(previousDate, updatedPresentation.getDate());
    }

    @Test
    void bulkInsertProjectPresentationsSuccessfully() throws SQLException, IOException {
        int count = 10;
        for (int index = 1; index <= count; index++) {
            ProjectPresentationDTO presentation = new ProjectPresentationDTO(
                    index, projectId, new java.sql.Timestamp(System.currentTimeMillis() + index * 1000), Type.Partial
            );
            assertTrue(projectPresentationDataAccessObject.insertProjectPresentation(presentation));
        }
        List<ProjectPresentationDTO> presentationList = projectPresentationDataAccessObject.getAllProjectPresentations();
        assertEquals(count, presentationList.size());
    }
}