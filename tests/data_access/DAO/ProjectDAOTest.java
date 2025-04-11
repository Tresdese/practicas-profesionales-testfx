package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DTO.ProjectDTO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProjectDAOTest {

    private static ConecctionDataBase connectionDB;
    private static Connection connection;
    private ProjectDAO projectDAO;

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
        projectDAO = new ProjectDAO();
    }

    @Test
    void testInsertProject() {
        try {
            ProjectDTO project = new ProjectDTO("0", "Proyecto Prueba", "Descripción de prueba",
                    Timestamp.valueOf("2023-12-01 10:00:00"), Timestamp.valueOf("2023-12-05 10:00:00"), "1");
            boolean result = projectDAO.insertProject(project, connection);
            assertTrue(result, "La inserción debería ser exitosa");

            List<ProjectDTO> projects = projectDAO.getAllProjects(connection);
            assertTrue(projects.stream().anyMatch(p -> "Proyecto Prueba".equals(p.getName())), "El proyecto debería existir en la base de datos");
        } catch (SQLException e) {
            fail("Error en testInsertProject: " + e.getMessage());
        }
    }

    @Test
    void testGetProject() {
        try {
            ProjectDTO project = new ProjectDTO("0", "Proyecto Consulta", "Descripción consulta",
                    Timestamp.valueOf("2023-12-02 10:00:00"), Timestamp.valueOf("2023-12-06 10:00:00"), "2");
            projectDAO.insertProject(project, connection);

            List<ProjectDTO> projects = projectDAO.getAllProjects(connection);
            ProjectDTO retrievedProject = projects.stream()
                    .filter(p -> "Proyecto Consulta".equals(p.getName()))
                    .findFirst()
                    .orElse(null);

            assertNotNull(retrievedProject, "El proyecto debería existir");
            assertEquals("Proyecto Consulta", retrievedProject.getName(), "El nombre debería coincidir");
        } catch (SQLException e) {
            fail("Error en testGetProject: " + e.getMessage());
        }
    }

    @Test
    void testUpdateProject() {
        try {
            ProjectDTO project = new ProjectDTO("0", "Proyecto Original", "Descripción original",
                    Timestamp.valueOf("2023-12-03 10:00:00"), Timestamp.valueOf("2023-12-07 10:00:00"), "3");
            projectDAO.insertProject(project, connection);

            List<ProjectDTO> projects = projectDAO.getAllProjects(connection);
            ProjectDTO existingProject = projects.stream()
                    .filter(p -> "Proyecto Original".equals(p.getName()))
                    .findFirst()
                    .orElse(null);

            assertNotNull(existingProject, "El proyecto debería existir antes de actualizarlo");

            ProjectDTO updatedProject = new ProjectDTO(existingProject.getIdProject(), "Proyecto Actualizado", "Descripción actualizada",
                    Timestamp.valueOf("2023-12-04 10:00:00"), Timestamp.valueOf("2023-12-08 10:00:00"), "4");
            boolean result = projectDAO.updateProject(updatedProject, connection);
            assertTrue(result, "La actualización debería ser exitosa");

            ProjectDTO retrievedProject = projectDAO.getProject(existingProject.getIdProject(), connection);
            assertNotNull(retrievedProject, "El proyecto debería existir después de actualizarlo");
            assertEquals("Proyecto Actualizado", retrievedProject.getName(), "El nombre debería actualizarse");
        } catch (SQLException e) {
            fail("Error en testUpdateProject: " + e.getMessage());
        }
    }

    @Test
    void testDeleteProject() {
        try {
            ProjectDTO project = new ProjectDTO("0", "Proyecto Eliminar", "Descripción eliminar",
                    Timestamp.valueOf("2023-12-05 10:00:00"), Timestamp.valueOf("2023-12-09 10:00:00"), "5");
            projectDAO.insertProject(project, connection);

            List<ProjectDTO> projects = projectDAO.getAllProjects(connection);
            ProjectDTO existingProject = projects.stream()
                    .filter(p -> "Proyecto Eliminar".equals(p.getName()))
                    .findFirst()
                    .orElse(null);

            assertNotNull(existingProject, "El proyecto debería existir antes de eliminarlo");

            boolean result = projectDAO.deleteProject(existingProject.getIdProject(), connection);
            assertTrue(result, "La eliminación debería ser exitosa");

            ProjectDTO deletedProject = projectDAO.getProject(existingProject.getIdProject(), connection);
            assertNull(deletedProject, "El proyecto eliminado no debería existir");
        } catch (SQLException e) {
            fail("Error en testDeleteProject: " + e.getMessage());
        }
    }

    @Test
    void testGetAllProjects() {
        try {
            ProjectDTO project1 = new ProjectDTO("0", "Proyecto Lista 1", "Descripción lista 1",
                    Timestamp.valueOf("2023-12-06 10:00:00"), Timestamp.valueOf("2023-12-10 10:00:00"), "6");
            ProjectDTO project2 = new ProjectDTO("0", "Proyecto Lista 2", "Descripción lista 2",
                    Timestamp.valueOf("2023-12-07 10:00:00"), Timestamp.valueOf("2023-12-11 10:00:00"), "7");
            projectDAO.insertProject(project1, connection);
            projectDAO.insertProject(project2, connection);

            List<ProjectDTO> projects = projectDAO.getAllProjects(connection);
            assertNotNull(projects, "La lista no debería ser nula");
            assertTrue(projects.size() >= 2, "Debería haber al menos dos proyectos en la lista");
        } catch (SQLException e) {
            fail("Error en testGetAllProjects: " + e.getMessage());
        }
    }
}