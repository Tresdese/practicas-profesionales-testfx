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
class StudentProjectDAOTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private StudentProjectDAO studentProjectDAO;
    private int testPeriodId;
    private int testUserId;
    private int testOrganizationId;
    private int testProjectId;
    private String testGroupNRC;
    private String testStudentTuition;

    @BeforeAll
    void setUpAll() throws SQLException, IOException {
        connectionDB = new ConnectionDataBase();
        connection = connectionDB.connectDataBase();
        clearTablesAndResetAutoIncrement();
        createBaseObjects();
    }

    @BeforeEach
    void setUp() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
        createBaseObjects();
        studentProjectDAO = new StudentProjectDAO();
    }

    @AfterAll
    void tearDownAll() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
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

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SET FOREIGN_KEY_CHECKS=0");
        statement.execute("TRUNCATE TABLE proyecto_estudiante");
        statement.execute("TRUNCATE TABLE proyecto");
        statement.execute("TRUNCATE TABLE estudiante");
        statement.execute("TRUNCATE TABLE grupo");
        statement.execute("TRUNCATE TABLE periodo");
        statement.execute("TRUNCATE TABLE usuario");
        statement.execute("TRUNCATE TABLE organizacion_vinculada");
        statement.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        statement.execute("SET FOREIGN_KEY_CHECKS=1");
        statement.close();
    }

    private void createBaseObjects() throws SQLException {
        String sqlPeriod = "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (1, '2024-1', '2024-01-01', '2024-06-30')";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sqlPeriod);
            testPeriodId = 1;
        }

        String sqlUser = "INSERT INTO usuario (idUsuario, numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (1, 1001, 'Juan', 'Pérez', 'juanp', '1234567890123456789012345678901234567890123456789012345678901234', 'Academico')";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sqlUser);
            testUserId = 1;
        }

        String sqlOrg = "INSERT INTO organizacion_vinculada (nombre, direccion) VALUES ('Org Test', 'Direccion Test')";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sqlOrg, Statement.RETURN_GENERATED_KEYS);
            var resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                testOrganizationId = resultSet.getInt(1);
            }
        }

        testGroupNRC = "12345";
        String sqlGroup = "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlGroup)) {
            preparedStatement.setString(1, testGroupNRC);
            preparedStatement.setString(2, "Grupo Test");
            preparedStatement.setInt(3, testUserId);
            preparedStatement.setInt(4, testPeriodId);
            preparedStatement.executeUpdate();
        }

        testStudentTuition = "S12345";
        String sqlStudent = "INSERT INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio, calificacionFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStudent)) {
            preparedStatement.setString(1, testStudentTuition);
            preparedStatement.setInt(2, 1);
            preparedStatement.setString(3, "Estudiante");
            preparedStatement.setString(4, "Prueba");
            preparedStatement.setString(5, "5555555555");
            preparedStatement.setString(6, "estu@test.com");
            preparedStatement.setString(7, "estuuser");
            preparedStatement.setString(8, "1234567890123456789012345678901234567890123456789012345678901234");
            preparedStatement.setString(9, testGroupNRC);
            preparedStatement.setString(10, "100");
            preparedStatement.setDouble(11, 0.0);
            preparedStatement.executeUpdate();
        }

        String sqlProject = "INSERT INTO proyecto (nombre, descripcion, fechaAproximada, fechaInicio, idUsuario, idOrganizacion) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlProject, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, "Proyecto Test");
            preparedStatement.setString(2, "Descripción de prueba");
            preparedStatement.setDate(3, java.sql.Date.valueOf("2024-05-01"));
            preparedStatement.setDate(4, java.sql.Date.valueOf("2024-04-01"));
            preparedStatement.setInt(5, testUserId);
            preparedStatement.setInt(6, testOrganizationId);
            preparedStatement.executeUpdate();
            var resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                testProjectId = resultSet.getInt(1);
            }
        }
    }

    @Test
    void testInsertStudentProject() throws SQLException, IOException {
        StudentProjectDTO studentProject = new StudentProjectDTO(String.valueOf(testProjectId), testStudentTuition);
        boolean result = studentProjectDAO.insertStudentProject(studentProject);
        assertTrue(result, "La inserción debería ser exitosa");

        StudentProjectDTO insertedStudentProject = studentProjectDAO.searchStudentProjectByIdProject(String.valueOf(testProjectId));
        assertNotNull(insertedStudentProject, "El proyecto de estudiante debería existir en la base de datos");
        assertEquals(testStudentTuition, insertedStudentProject.getTuition(), "La matrícula debería coincidir");
    }

    @Test
    void testSearchStudentProjectByIdProject() throws SQLException, IOException {
        insertTestStudentProject(String.valueOf(testProjectId), testStudentTuition);

        StudentProjectDTO retrievedStudentProject = studentProjectDAO.searchStudentProjectByIdProject(String.valueOf(testProjectId));
        assertNotNull(retrievedStudentProject, "Debería encontrar el proyecto de estudiante");
        assertEquals(testStudentTuition, retrievedStudentProject.getTuition(), "La matrícula debería coincidir");
    }

    @Test
    void testUpdateStudentProject() throws SQLException, IOException {
        insertTestStudentProject(String.valueOf(testProjectId), testStudentTuition);

        String newProjectId = String.valueOf(testProjectId + 1);
        String sqlProject = "INSERT INTO proyecto (nombre, descripcion, fechaAproximada, fechaInicio, idUsuario, idOrganizacion) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlProject, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, "Proyecto Nuevo");
            preparedStatement.setString(2, "Descripción nueva");
            preparedStatement.setDate(3, java.sql.Date.valueOf("2024-06-01"));
            preparedStatement.setDate(4, java.sql.Date.valueOf("2024-05-01"));
            preparedStatement.setInt(5, testUserId);
            preparedStatement.setInt(6, testOrganizationId);
            preparedStatement.executeUpdate();
            var resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                newProjectId = String.valueOf(resultSet.getInt(1));
            }
        }

        StudentProjectDTO updatedStudentProject = new StudentProjectDTO(newProjectId, testStudentTuition);
        boolean result = studentProjectDAO.updateStudentProject(updatedStudentProject);
        assertTrue(result, "La actualización debería ser exitosa");

        StudentProjectDTO retrievedStudentProject = studentProjectDAO.searchStudentProjectByIdProject(newProjectId);
        assertNotNull(retrievedStudentProject, "El proyecto de estudiante debería existir después de actualizar");
        assertEquals(testStudentTuition, retrievedStudentProject.getTuition(), "La matrícula debería seguir siendo la misma");
    }

    @Test
    void testDeleteStudentProject() throws SQLException, IOException {
        insertTestStudentProject(String.valueOf(testProjectId), testStudentTuition);

        StudentProjectDTO studentProjectToDelete = new StudentProjectDTO(String.valueOf(testProjectId), testStudentTuition);
        boolean result = studentProjectDAO.deleteStudentProject(studentProjectToDelete);
        assertTrue(result, "La eliminación debería ser exitosa");

        StudentProjectDTO deletedStudentProject = studentProjectDAO.searchStudentProjectByIdProject(String.valueOf(testProjectId));
        assertEquals("N/A", deletedStudentProject.getIdProject(), "El proyecto eliminado no debería existir");
    }

    @Test
    void testGetAllStudentProjects() throws SQLException, IOException {
        insertTestStudentProject(String.valueOf(testProjectId), testStudentTuition);

        List<StudentProjectDTO> studentProjectList = studentProjectDAO.getAllStudentProjects();
        assertNotNull(studentProjectList, "La lista no debería ser nula");
        assertFalse(studentProjectList.isEmpty(), "La lista no debería estar vacía");
        boolean found = studentProjectList.stream()
                .anyMatch(studentProject -> studentProject.getIdProject().equals(String.valueOf(testProjectId)) && studentProject.getTuition().equals(testStudentTuition));
        assertTrue(found, "El proyecto de estudiante de prueba debería estar en la lista");
    }

    private void insertTestStudentProject(String idProject, String tuition) throws SQLException {
        String sql = "INSERT INTO proyecto_estudiante (idProyecto, matricula) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, idProject);
            preparedStatement.setString(2, tuition);
            preparedStatement.executeUpdate();
        }
    }

    @Test
    void testUpdateStudentProjectNotFound() throws SQLException, IOException {
        StudentProjectDTO nonExistent = new StudentProjectDTO("9999", "NO_EXISTE");
        boolean result = studentProjectDAO.updateStudentProject(nonExistent);
        assertFalse(result, "No debe actualizar un registro inexistente");
    }

    @Test
    void testDeleteStudentProjectNotFound() throws SQLException, IOException {
        StudentProjectDTO nonExistent = new StudentProjectDTO("9999", "NO_EXISTE");
        boolean result = studentProjectDAO.deleteStudentProject(nonExistent);
        assertFalse(result, "No debe eliminar un registro inexistente");
    }

    @Test
    void testSearchStudentProjectByIdTuiton() throws SQLException, IOException {
        insertTestStudentProject(String.valueOf(testProjectId), testStudentTuition);

        StudentProjectDTO retrievedStudentProject = studentProjectDAO.searchStudentProjectByIdTuiton(testStudentTuition);
        assertNotNull(retrievedStudentProject, "Debe encontrar el proyecto por matrícula");
        assertEquals(String.valueOf(testProjectId), retrievedStudentProject.getIdProject(), "El idProyecto debe coincidir");
    }

    @Test
    void testInsertDuplicateStudentProject() throws SQLException {
        insertTestStudentProject(String.valueOf(testProjectId), testStudentTuition);
        assertThrows(SQLException.class, () -> {
            insertTestStudentProject(String.valueOf(testProjectId), testStudentTuition);
        }, "Debe lanzar excepción por duplicado si la BD lo restringe");
    }

    @Test
    void testGetAllStudentProjectsEmpty() throws SQLException, IOException {
        List<StudentProjectDTO> studentProjectList = studentProjectDAO.getAllStudentProjects();
        assertNotNull(studentProjectList, "La lista no debe ser nula");
        assertTrue(studentProjectList.isEmpty(), "La lista debe estar vacía si no hay registros");
    }

    @Test
    void testInsertStudentProjectWithNullTuiton() {
        StudentProjectDTO invalidStudentProject = new StudentProjectDTO(String.valueOf(testProjectId), null);
        assertThrows(SQLException.class, () -> {
            studentProjectDAO.insertStudentProject(invalidStudentProject);
        }, "Debe lanzar excepción por matrícula nula");
    }

    @Test
    void testSearchStudentProjectByIdProjectNotFound() throws SQLException, IOException {
        StudentProjectDTO result = studentProjectDAO.searchStudentProjectByIdProject("9999");
        assertEquals("N/A", result.getIdProject(), "Debe devolver DTO con valores por defecto");
    }

    @Test
    void testDeleteStudentProjectSuccess() throws SQLException, IOException {
        insertTestStudentProject(String.valueOf(testProjectId), testStudentTuition);
        StudentProjectDTO studentProjectToDelete = new StudentProjectDTO(String.valueOf(testProjectId), testStudentTuition);
        boolean deleted = studentProjectDAO.deleteStudentProject(studentProjectToDelete);
        assertTrue(deleted, "La eliminación debe ser exitosa");
        StudentProjectDTO result = studentProjectDAO.searchStudentProjectByIdProject(String.valueOf(testProjectId));
        assertEquals("N/A", result.getIdProject(), "El registro debe haber sido eliminado");
    }

    @Test
    void testUpdateStudentProjectWithNullIdProject() {
        StudentProjectDTO invalidUpdate = new StudentProjectDTO(null, testStudentTuition);
        assertThrows(SQLException.class, () -> {
            studentProjectDAO.updateStudentProject(invalidUpdate);
        }, "Debe lanzar excepción por idProyecto nulo");
    }

    @Test
    void testGetAllStudentProjectsWithMultipleRecords() throws SQLException, IOException {
        String tuition2 = "S54321";
        String sqlStudent = "INSERT INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio, calificacionFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStudent)) {
            preparedStatement.setString(1, tuition2);
            preparedStatement.setInt(2, 1);
            preparedStatement.setString(3, "Otro");
            preparedStatement.setString(4, "Estudiante");
            preparedStatement.setString(5, "5555555556");
            preparedStatement.setString(6, "otro@test.com");
            preparedStatement.setString(7, "otrousuario");
            preparedStatement.setString(8, "1234567890123456789012345678901234567890123456789012345678901234");
            preparedStatement.setString(9, testGroupNRC);
            preparedStatement.setString(10, "100");
            preparedStatement.setDouble(11, 0.0);
            preparedStatement.executeUpdate();
        }
        insertTestStudentProject(String.valueOf(testProjectId), testStudentTuition);
        insertTestStudentProject(String.valueOf(testProjectId), tuition2);

        List<StudentProjectDTO> studentProjectList = studentProjectDAO.getAllStudentProjects();
        assertNotNull(studentProjectList, "La lista no debe ser nula");
        assertTrue(studentProjectList.size() >= 2, "Debe haber al menos dos registros");
        boolean found1 = studentProjectList.stream().anyMatch(studentProject -> studentProject.getTuition().equals(testStudentTuition));
        boolean found2 = studentProjectList.stream().anyMatch(studentProject -> studentProject.getTuition().equals(tuition2));
        assertTrue(found1 && found2, "Ambos proyectos deben estar en la lista");
    }
}