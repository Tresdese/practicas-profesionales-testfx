package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.StudentProjectDAO;
import logic.DTO.StudentProjectDTO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudentProjectDAOTest {

    private static ConecctionDataBase connectionDB;
    private static Connection connection;
    private StudentProjectDAO studentProjectDAO;

    @BeforeAll
    static void setUpClass() {
        connectionDB = new ConecctionDataBase();
        try {
            connection = connectionDB.connectDB();
        } catch (SQLException e) {
            fail("Error al conectar a la base de datos: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDownClass() {
        connectionDB.close();
    }

    @BeforeEach
    void setUp() {
        studentProjectDAO = new StudentProjectDAO();
        try {
            connection.prepareStatement("DELETE FROM proyecto_estudiante").executeUpdate();
        } catch (SQLException e) {
            fail("Error al limpiar la tabla proyecto_estudiante: " + e.getMessage());
        }
    }

    @Test
    void testInsertStudentProject() {
        try {
            StudentProjectDTO studentProject = new StudentProjectDTO("P12345", "S12345");
            boolean result = studentProjectDAO.insertStudentProject(studentProject, connection);
            assertTrue(result, "La inserción debería ser exitosa");

            StudentProjectDTO insertedProject = studentProjectDAO.searchStudentProjectByIdProject("P12345", connection);
            assertNotNull(insertedProject, "El proyecto de estudiante debería existir en la base de datos");
            assertEquals("S12345", insertedProject.getTuiton(), "La matrícula debería coincidir");
        } catch (SQLException e) {
            fail("Error en testInsertStudentProject: " + e.getMessage());
        }
    }

    @Test
    void testSearchStudentProjectByIdProject() {
        try {
            insertTestStudentProject("P54321", "S54321");

            StudentProjectDTO retrievedProject = studentProjectDAO.searchStudentProjectByIdProject("P54321", connection);
            assertNotNull(retrievedProject, "Debería encontrar el proyecto de estudiante");
            assertEquals("S54321", retrievedProject.getTuiton(), "La matrícula debería coincidir");
        } catch (SQLException e) {
            fail("Error en testSearchStudentProjectByIdProject: " + e.getMessage());
        }
    }

    @Test
    void testUpdateStudentProject() {
        try {
            insertTestStudentProject("P67890", "S67890");

            StudentProjectDTO updatedProject = new StudentProjectDTO("P67890", "S98765");
            boolean result = studentProjectDAO.updateStudentProject(updatedProject, connection);
            assertTrue(result, "La actualización debería ser exitosa");

            StudentProjectDTO retrievedProject = studentProjectDAO.searchStudentProjectByIdProject("P67890", connection);
            assertNotNull(retrievedProject, "El proyecto de estudiante debería existir después de actualizar");
            assertEquals("S98765", retrievedProject.getTuiton(), "La matrícula debería actualizarse");
        } catch (SQLException e) {
            fail("Error en testUpdateStudentProject: " + e.getMessage());
        }
    }

    @Test
    void testDeleteStudentProject() {
        try {
            insertTestStudentProject("P22222", "S22222");

            StudentProjectDTO projectToDelete = new StudentProjectDTO("P22222", "S22222");
            boolean result = studentProjectDAO.deleteStudentProject(projectToDelete, connection);
            assertTrue(result, "La eliminación debería ser exitosa");

            StudentProjectDTO deletedProject = studentProjectDAO.searchStudentProjectByIdProject("P22222", connection);
            assertEquals("N/A", deletedProject.getIdProject(), "El proyecto eliminado no debería existir");
        } catch (SQLException e) {
            fail("Error en testDeleteStudentProject: " + e.getMessage());
        }
    }

    @Test
    void testGetAllStudentProjects() {
        try {
            insertTestStudentProject("P33333", "S33333");
            insertTestStudentProject("P44444", "S44444");

            List<StudentProjectDTO> projects = studentProjectDAO.getAllStudentProjects(connection);
            assertNotNull(projects, "La lista no debería ser nula");
            assertTrue(projects.size() >= 2, "Deberían existir al menos dos proyectos en la lista");
        } catch (SQLException e) {
            fail("Error en testGetAllStudentProjects: " + e.getMessage());
        }
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