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
class ProjectDAOTest {

    private ConnectionDataBase connectionDataBase;
    private Connection databaseConnection;
    private UserDAO userDataAccessObject;
    private LinkedOrganizationDAO linkedOrganizationDataAccessObject;
    private ProjectDAO projectDataAccessObject;

    private int testUserId;
    private int testOrganizationId;
    private int testDepartmentId;

    @BeforeAll
    void setUpAll() throws SQLException, IOException {
        connectionDataBase = new ConnectionDataBase();
        databaseConnection = connectionDataBase.connectDataBase();
        userDataAccessObject = new UserDAO();
        linkedOrganizationDataAccessObject = new LinkedOrganizationDAO();
        projectDataAccessObject = new ProjectDAO();

        clearTablesAndResetAutoIncrement();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createBaseUserAndOrganization();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = databaseConnection.createStatement();
        statement.execute("SET FOREIGN_KEY_CHECKS=0");
        statement.execute("TRUNCATE TABLE proyecto");
        statement.execute("TRUNCATE TABLE usuario");
        statement.execute("TRUNCATE TABLE departamento");
        statement.execute("TRUNCATE TABLE organizacion_vinculada");
        statement.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        statement.execute("SET FOREIGN_KEY_CHECKS=1");
        statement.close();
    }

    private void createBaseUserAndOrganization() throws SQLException, IOException {
        LinkedOrganizationDTO organization = new LinkedOrganizationDTO(null, "Org Test", "Dirección Test", 1);
        testOrganizationId = Integer.parseInt(linkedOrganizationDataAccessObject.insertLinkedOrganizationAndGetId(organization));

        testDepartmentId = createTestDepartment();

        UserDTO user = new UserDTO(null, 1, "12345", "Nombre", "Apellido", "usuarioTest", "passTest", Role.ACADEMIC);
        testUserId = insertUserAndGetId(user);
    }

    private int insertUserAndGetId(UserDTO user) throws SQLException, IOException {
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

    private int createTestDepartment() throws SQLException {
        String sql = "INSERT INTO departamento (nombre, descripcion, idOrganizacion) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, "Dept test");
            preparedStatement.setString(2, "Description test");
            preparedStatement.setInt(3, testOrganizationId);
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el id del departamento insertado");
    }

    @AfterAll
    void tearDownAll() throws SQLException, IOException {
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
    void insertProjectSuccessfully() throws SQLException, IOException {
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
        boolean wasInserted = projectDataAccessObject.insertProject(project);
        assertTrue(wasInserted, "El proyecto debe insertarse correctamente");
    }

    @Test
    void getAllProjectsSuccessfully() throws SQLException, IOException {
        insertProjectSuccessfully();
        List<ProjectDTO> projectList = projectDataAccessObject.getAllProjects();
        assertNotNull(projectList);
        assertFalse(projectList.isEmpty());
    }

    @Test
    void searchProjectByIdSuccessfully() throws SQLException, IOException {
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
        projectDataAccessObject.insertProject(project);

        List<ProjectDTO> projectList = projectDataAccessObject.getAllProjects();
        assertFalse(projectList.isEmpty());
        ProjectDTO insertedProject = projectList.get(0);

        ProjectDTO foundProject = projectDataAccessObject.searchProjectById(insertedProject.getIdProject());
        assertNotNull(foundProject);
        assertEquals(insertedProject.getName(), foundProject.getName());
    }

    @Test
    void updateProjectSuccessfully() throws SQLException, IOException {
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
        projectDataAccessObject.insertProject(project);

        List<ProjectDTO> projectList = projectDataAccessObject.getAllProjects();
        ProjectDTO projectToUpdate = projectList.get(0);

        projectToUpdate.setName("Proyecto Actualizado");
        projectToUpdate.setDescription("Descripción Modificada");
        boolean wasUpdated = projectDataAccessObject.updateProject(projectToUpdate);
        assertTrue(wasUpdated);

        ProjectDTO updatedProject = projectDataAccessObject.searchProjectById(projectToUpdate.getIdProject());
        assertEquals("Proyecto Actualizado", updatedProject.getName());
        assertEquals("Descripción Modificada", updatedProject.getDescription());
    }

    @Test
    void deleteProjectSuccessfully() throws SQLException, IOException {
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
        projectDataAccessObject.insertProject(project);

        List<ProjectDTO> projectList = projectDataAccessObject.getAllProjects();
        ProjectDTO projectToDelete = projectList.get(0);

        boolean wasDeleted = projectDataAccessObject.deleteProject(projectToDelete.getIdProject());
        assertTrue(wasDeleted);

        ProjectDTO deletedProject = projectDataAccessObject.searchProjectById(projectToDelete.getIdProject());
        assertEquals("-1", deletedProject.getIdProject());
    }

    @Test
    void insertProjectWithNullData() {
        ProjectDTO project = new ProjectDTO(null, null, null, null, null, null, 0, 0);
        assertThrows(SQLException.class, () -> projectDataAccessObject.insertProject(project),
                "No debe permitir insertar proyecto con datos nulos");
    }

    @Test
    void updateNonExistentProject() throws SQLException, IOException {
        ProjectDTO project = new ProjectDTO("9999", "No existe", "Desc", new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()), String.valueOf(testUserId), testOrganizationId,
                testDepartmentId);
        boolean wasUpdated = projectDataAccessObject.updateProject(project);
        assertFalse(wasUpdated, "No debe actualizar un proyecto inexistente");
    }

    @Test
    void deleteNonExistentProject() throws SQLException, IOException {
        boolean wasDeleted = projectDataAccessObject.deleteProject("9999");
        assertFalse(wasDeleted, "No debe eliminar un proyecto inexistente");
    }

    @Test
    void insertDuplicateProjectName() throws SQLException, IOException {
        ProjectDTO projectOne = new ProjectDTO(null, "Proyecto Duplicado", "Desc1",
                new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()),
                String.valueOf(testUserId), testOrganizationId, testDepartmentId);
        ProjectDTO projectTwo = new ProjectDTO(null, "Proyecto Duplicado", "Desc2",
                new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()),
                String.valueOf(testUserId), testOrganizationId, testDepartmentId);

        boolean wasInsertedOne = projectDataAccessObject.insertProject(projectOne);
        boolean wasInsertedTwo = projectDataAccessObject.insertProject(projectTwo);

        assertTrue(wasInsertedOne, "Debe permitir insertar el primer proyecto");
        assertTrue(wasInsertedTwo, "Debe permitir insertar duplicados si la base de datos lo permite");
    }

    @Test
    void getAllProjectsWhenTableIsEmpty() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
        List<ProjectDTO> projectList = projectDataAccessObject.getAllProjects();
        assertNotNull(projectList);
        assertTrue(projectList.isEmpty(), "La lista debe estar vacía si no hay proyectos");
    }

    @Test
    void insertAndRetrieveMultipleProjects() throws SQLException, IOException {
        for (int index = 0; index < 3; index++) {
            ProjectDTO project = new ProjectDTO(null, "Proyecto" + index, "Desc" + index,
                    new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()),
                    String.valueOf(testUserId), testOrganizationId, testDepartmentId);
            projectDataAccessObject.insertProject(project);
        }
        List<ProjectDTO> projectList = projectDataAccessObject.getAllProjects();
        assertTrue(projectList.size() >= 3, "Debe haber al menos tres proyectos");
    }

    @Test
    void updateProjectWithNullData() throws SQLException, IOException {
        ProjectDTO project = new ProjectDTO(null, "Proyecto Null", "Desc",
                new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()),
                String.valueOf(testUserId), testOrganizationId, testDepartmentId);
        projectDataAccessObject.insertProject(project);
        List<ProjectDTO> projectList = projectDataAccessObject.getAllProjects();
        ProjectDTO projectToUpdate = projectList.get(0);

        projectToUpdate.setName(null);
        projectToUpdate.setDescription(null);
        projectToUpdate.setIdUser(null);

        assertThrows(SQLException.class, () -> projectDataAccessObject.updateProject(projectToUpdate),
                "No debe permitir actualizar proyecto con datos nulos");
    }
}