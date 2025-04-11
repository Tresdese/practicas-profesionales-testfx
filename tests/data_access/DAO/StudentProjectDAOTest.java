package data_access.DAO;

import data_access.ConecctionDataBase;
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
        connectionDB.closeConnection();
    }

    @BeforeEach
    void setUp() {
        studentProjectDAO = new StudentProjectDAO();
        try {
            // Asegurar que el proyecto con idProyecto exista en la tabla proyecto
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT IGNORE INTO proyecto (idProyecto, nombre, descripcion) VALUES (?, ?, ?)")) {
                statement.setString(1, "1");
                statement.setString(2, "Proyecto 1");
                statement.setString(3, "Descripción del proyecto 1");
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Error al preparar los datos iniciales: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        try {
            // Limpiar los datos creados en la tabla proyecto_estudiante
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM proyecto_estudiante")) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Error al limpiar los datos después de la prueba: " + e.getMessage());
        }
    }

    @Test
    void testInsertStudentProject() {
        try {
            StudentProjectDTO studentProject = new StudentProjectDTO("1", "67899");
            boolean result = studentProjectDAO.insertStudentProject(studentProject, connection);
            assertTrue(result, "La inserción debería ser exitosa");
        } catch (SQLException e) {
            fail("Error en testInsertStudentProject: " + e.getMessage());
        }
    }


    @Test
    void testGetStudentProject() {
        try {
            StudentProjectDTO studentProject = new StudentProjectDTO("1", "12345");
            studentProjectDAO.insertStudentProject(studentProject, connection);

            StudentProjectDTO retrievedProject = studentProjectDAO.getStudentProject("1", connection);
            assertNotNull(retrievedProject, "El proyecto debería existir");
            assertEquals("12345", retrievedProject.getTuiton(), "La matrícula debería coincidir");
        } catch (SQLException e) {
            fail("Error en testGetStudentProject: " + e.getMessage());
        }
    }

    @Test
    void testUpdateStudentProject() {
        try {
            StudentProjectDTO studentProject = new StudentProjectDTO("1", "12345");
            studentProjectDAO.insertStudentProject(studentProject, connection);

            StudentProjectDTO updatedProject = new StudentProjectDTO("1", "54321");
            boolean result = studentProjectDAO.updateStudentProject(updatedProject, connection);
            assertTrue(result, "La actualización debería ser exitosa");

            StudentProjectDTO retrievedProject = studentProjectDAO.getStudentProject("1", connection);
            assertNotNull(retrievedProject, "El proyecto debería existir");
            assertEquals("54321", retrievedProject.getTuiton(), "La matrícula debería actualizarse");
        } catch (SQLException e) {
            fail("Error en testUpdateStudentProject: " + e.getMessage());
        }
    }

    @Test
    void testDeleteStudentProject() {
        try {
            StudentProjectDTO studentProject = new StudentProjectDTO("1", "12345");
            studentProjectDAO.insertStudentProject(studentProject, connection);

            boolean result = studentProjectDAO.deleteStudentProject(studentProject, connection);
            assertTrue(result, "La eliminación debería ser exitosa");

            StudentProjectDTO deletedProject = studentProjectDAO.getStudentProject("1", connection);
            assertNull(deletedProject, "El proyecto eliminado no debería existir");
        } catch (SQLException e) {
            fail("Error en testDeleteStudentProject: " + e.getMessage());
        }
    }

    @Test
    void testGetAllStudentProjects() {
        try {
            StudentProjectDTO studentProject1 = new StudentProjectDTO("1", "12345");
            StudentProjectDTO studentProject2 = new StudentProjectDTO("2", "54321");

            // Asegurar que el proyecto con idProyecto 2 exista
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT IGNORE INTO proyecto (idProyecto, nombre, descripcion) VALUES (?, ?, ?)")) {
                statement.setString(1, "2");
                statement.setString(2, "Proyecto 2");
                statement.setString(3, "Descripción del proyecto 2");
                statement.executeUpdate();
            }

            studentProjectDAO.insertStudentProject(studentProject1, connection);
            studentProjectDAO.insertStudentProject(studentProject2, connection);

            List<StudentProjectDTO> projects = studentProjectDAO.getAllStudentProjects(connection);
            assertNotNull(projects, "La lista no debería ser nula");
            assertTrue(projects.size() >= 2, "Debería haber al menos dos proyectos en la lista");
        } catch (SQLException e) {
            fail("Error en testGetAllStudentProjects: " + e.getMessage());
        }
    }
}