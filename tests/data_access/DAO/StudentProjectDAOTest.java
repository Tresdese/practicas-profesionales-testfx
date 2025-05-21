package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.*;
import logic.DTO.*;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StudentProjectDAOTest {

    private ConecctionDataBase connectionDB;
    private Connection connection;
    private StudentProjectDAO studentProjectDAO;
    private int testPeriodId;
    private int testUserId;
    private int testOrganizationId;
    private int testProjectId;
    private String testGroupNRC;
    private String testStudentMatricula;

    @BeforeAll
    void setUpAll() throws Exception {
        connectionDB = new ConecctionDataBase();
        connection = connectionDB.connectDB();
        limpiarTablasYResetearAutoIncrement();
        crearObjetosBase();
    }

    @BeforeEach
    void setUp() throws Exception {
        limpiarTablasYResetearAutoIncrement();
        crearObjetosBase();
        studentProjectDAO = new StudentProjectDAO();
    }

    @AfterAll
    void tearDownAll() throws Exception {
        limpiarTablasYResetearAutoIncrement();
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

    private void limpiarTablasYResetearAutoIncrement() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS=0");
        stmt.execute("TRUNCATE TABLE proyecto_estudiante");
        stmt.execute("TRUNCATE TABLE proyecto");
        stmt.execute("TRUNCATE TABLE estudiante");
        stmt.execute("TRUNCATE TABLE grupo");
        stmt.execute("TRUNCATE TABLE periodo");
        stmt.execute("TRUNCATE TABLE usuario");
        stmt.execute("TRUNCATE TABLE organizacion_vinculada");
        stmt.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        stmt.execute("SET FOREIGN_KEY_CHECKS=1");
        stmt.close();
    }

    private void crearObjetosBase() throws SQLException {
        // Period
        String sqlPeriod = "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (1, '2024-1', '2024-01-01', '2024-06-30')";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sqlPeriod);
            testPeriodId = 1;
        }

        // User
        String sqlUser = "INSERT INTO usuario (idUsuario, numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (1, 1001, 'Juan', 'Pérez', 'juanp', '1234567890123456789012345678901234567890123456789012345678901234', 'Academico')";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sqlUser);
            testUserId = 1;
        }

        // LinkedOrganization
        String sqlOrg = "INSERT INTO organizacion_vinculada (nombre, direccion) VALUES ('Org Test', 'Direccion Test')";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sqlOrg, Statement.RETURN_GENERATED_KEYS);
            var rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                testOrganizationId = rs.getInt(1);
            }
        }

        // Group
        testGroupNRC = "12345";
        String sqlGroup = "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlGroup)) {
            stmt.setString(1, testGroupNRC);
            stmt.setString(2, "Grupo Test");
            stmt.setInt(3, testUserId);
            stmt.setInt(4, testPeriodId);
            stmt.executeUpdate();
        }

        // Student
        testStudentMatricula = "S12345";
        String sqlStudent = "INSERT INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio, calificacionFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlStudent)) {
            stmt.setString(1, testStudentMatricula);
            stmt.setInt(2, 1);
            stmt.setString(3, "Estudiante");
            stmt.setString(4, "Prueba");
            stmt.setString(5, "5555555555");
            stmt.setString(6, "estu@test.com");
            stmt.setString(7, "estuuser");
            stmt.setString(8, "1234567890123456789012345678901234567890123456789012345678901234");
            stmt.setString(9, testGroupNRC);
            stmt.setString(10, "100");
            stmt.setDouble(11, 0.0);
            stmt.executeUpdate();
        }

        // Project
        String sqlProject = "INSERT INTO proyecto (nombre, descripcion, fechaAproximada, fechaInicio, idUsuario, idOrganizacion) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlProject, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "Proyecto Test");
            stmt.setString(2, "Descripción de prueba");
            stmt.setDate(3, java.sql.Date.valueOf("2024-05-01"));
            stmt.setDate(4, java.sql.Date.valueOf("2024-04-01"));
            stmt.setInt(5, testUserId);
            stmt.setInt(6, testOrganizationId);
            stmt.executeUpdate();
            var rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                testProjectId = rs.getInt(1);
            }
        }
    }

    @Test
    void testInsertStudentProject() throws SQLException {
        StudentProjectDTO studentProject = new StudentProjectDTO(String.valueOf(testProjectId), testStudentMatricula);
        boolean result = studentProjectDAO.insertStudentProject(studentProject);
        assertTrue(result, "La inserción debería ser exitosa");

        StudentProjectDTO insertedProject = studentProjectDAO.searchStudentProjectByIdProject(String.valueOf(testProjectId));
        assertNotNull(insertedProject, "El proyecto de estudiante debería existir en la base de datos");
        assertEquals(testStudentMatricula, insertedProject.getTuiton(), "La matrícula debería coincidir");
    }

    @Test
    void testSearchStudentProjectByIdProject() throws SQLException {
        insertTestStudentProject(String.valueOf(testProjectId), testStudentMatricula);

        StudentProjectDTO retrievedProject = studentProjectDAO.searchStudentProjectByIdProject(String.valueOf(testProjectId));
        assertNotNull(retrievedProject, "Debería encontrar el proyecto de estudiante");
        assertEquals(testStudentMatricula, retrievedProject.getTuiton(), "La matrícula debería coincidir");
    }

    @Test
    void testUpdateStudentProject() throws SQLException {
        insertTestStudentProject(String.valueOf(testProjectId), testStudentMatricula);

        String newMatricula = "S54321";
        // Insertar nuevo estudiante para actualizar
        String sqlStudent = "INSERT INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio, calificacionFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlStudent)) {
            stmt.setString(1, newMatricula);
            stmt.setInt(2, 1);
            stmt.setString(3, "Nuevo");
            stmt.setString(4, "Estudiante");
            stmt.setString(5, "5555555556");
            stmt.setString(6, "nuevo@test.com");
            stmt.setString(7, "nuevoestu");
            stmt.setString(8, "1234567890123456789012345678901234567890123456789012345678901234");
            stmt.setString(9, testGroupNRC);
            stmt.setString(10, "100");
            stmt.setDouble(11, 0.0);
            stmt.executeUpdate();
        }

        StudentProjectDTO updatedProject = new StudentProjectDTO(String.valueOf(testProjectId), newMatricula);
        boolean result = studentProjectDAO.updateStudentProject(updatedProject);
        assertTrue(result, "La actualización debería ser exitosa");

        StudentProjectDTO retrievedProject = studentProjectDAO.searchStudentProjectByIdProject(String.valueOf(testProjectId));
        assertNotNull(retrievedProject, "El proyecto de estudiante debería existir después de actualizar");
        assertEquals(newMatricula, retrievedProject.getTuiton(), "La matrícula debería actualizarse");
    }

    @Test
    void testDeleteStudentProject() throws SQLException {
        insertTestStudentProject(String.valueOf(testProjectId), testStudentMatricula);

        StudentProjectDTO projectToDelete = new StudentProjectDTO(String.valueOf(testProjectId), testStudentMatricula);
        boolean result = studentProjectDAO.deleteStudentProject(projectToDelete);
        assertTrue(result, "La eliminación debería ser exitosa");

        StudentProjectDTO deletedProject = studentProjectDAO.searchStudentProjectByIdProject(String.valueOf(testProjectId));
        assertEquals("N/A", deletedProject.getIdProject(), "El proyecto eliminado no debería existir");
    }

    @Test
    void testGetAllStudentProjects() throws SQLException {
        insertTestStudentProject(String.valueOf(testProjectId), testStudentMatricula);

        List<StudentProjectDTO> projects = studentProjectDAO.getAllStudentProjects();
        assertNotNull(projects, "La lista no debería ser nula");
        assertFalse(projects.isEmpty(), "La lista no debería estar vacía");
        boolean found = projects.stream()
                .anyMatch(p -> p.getIdProject().equals(String.valueOf(testProjectId)) && p.getTuiton().equals(testStudentMatricula));
        assertTrue(found, "El proyecto de estudiante de prueba debería estar en la lista");
    }

    private void insertTestStudentProject(String idProject, String tuiton) throws SQLException {
        String sql = "INSERT INTO proyecto_estudiante (idProyecto, matricula) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idProject);
            stmt.setString(2, tuiton);
            stmt.executeUpdate();
        }
    }
}