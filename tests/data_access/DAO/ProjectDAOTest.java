package data_access.DAO;

import logic.DAO.ProjectDAO;
import logic.DTO.ProjectDTO;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProjectDAOTest {

    private ProjectDAO projectDAO;

    @BeforeEach
    void setUp() {
        projectDAO = new ProjectDAO();
    }

    @Test
    void testInsertProject() {
        try {
            ProjectDTO project = new ProjectDTO("1", "Proyecto Prueba", "Descripción de prueba",
                    Timestamp.valueOf("2023-12-01 10:00:00"), Timestamp.valueOf("2023-12-05 10:00:00"), "1");
            boolean result = projectDAO.insertProject(project);
            assertTrue(result, "La inserción debería ser exitosa");

            List<ProjectDTO> projects = projectDAO.getAllProjects();
            assertTrue(projects.stream().anyMatch(p -> "Proyecto Prueba".equals(p.getName())), "El proyecto debería existir en la base de datos");
        } catch (SQLException e) {
            fail("Error en testInsertProject: " + e.getMessage());
        }
    }

    @Test
    void testSearchProjectById() {
        try {
            ProjectDTO project = new ProjectDTO("2", "Proyecto Consulta", "Descripción consulta",
                    Timestamp.valueOf("2023-12-02 10:00:00"), Timestamp.valueOf("2023-12-06 10:00:00"), "2");
            projectDAO.insertProject(project);

            ProjectDTO retrievedProject = projectDAO.searchProjectById("2");
            assertNotNull(retrievedProject, "El proyecto debería existir");
            assertEquals("Proyecto Consulta", retrievedProject.getName(), "El nombre debería coincidir");
        } catch (SQLException e) {
            fail("Error en testSearchProjectById: " + e.getMessage());
        }
    }

    @Test
    void testUpdateProject() {
        try {
            ProjectDTO project = new ProjectDTO("3", "Proyecto Original", "Descripción original",
                    Timestamp.valueOf("2023-12-03 10:00:00"), Timestamp.valueOf("2023-12-07 10:00:00"), "3");
            projectDAO.insertProject(project);

            ProjectDTO updatedProject = new ProjectDTO("3", "Proyecto Actualizado", "Descripción actualizada",
                    Timestamp.valueOf("2023-12-04 10:00:00"), Timestamp.valueOf("2023-12-08 10:00:00"), "3");
            boolean result = projectDAO.updateProject(updatedProject);
            assertTrue(result, "La actualización debería ser exitosa");

            ProjectDTO retrievedProject = projectDAO.searchProjectById("3");
            assertNotNull(retrievedProject, "El proyecto debería existir después de actualizarlo");
            assertEquals("Proyecto Actualizado", retrievedProject.getName(), "El nombre debería actualizarse");
        } catch (SQLException e) {
            fail("Error en testUpdateProject: " + e.getMessage());
        }
    }

    @Test
    void testDeleteProject() {
        try {
            ProjectDTO project = new ProjectDTO("4", "Proyecto Eliminar", "Descripción eliminar",
                    Timestamp.valueOf("2023-12-05 10:00:00"), Timestamp.valueOf("2023-12-09 10:00:00"), "4");
            projectDAO.insertProject(project);

            boolean result = projectDAO.deleteProject("4");
            assertTrue(result, "La eliminación debería ser exitosa");

            ProjectDTO deletedProject = projectDAO.searchProjectById("4");
            assertNull(deletedProject, "El proyecto eliminado no debería existir");
        } catch (SQLException e) {
            fail("Error en testDeleteProject: " + e.getMessage());
        }
    }

    @Test
    void testGetAllProjects() {
        try {
            ProjectDTO project1 = new ProjectDTO("5", "Proyecto Lista 1", "Descripción lista 1",
                    Timestamp.valueOf("2023-12-06 10:00:00"), Timestamp.valueOf("2023-12-10 10:00:00"), "5");
            ProjectDTO project2 = new ProjectDTO("6", "Proyecto Lista 2", "Descripción lista 2",
                    Timestamp.valueOf("2023-12-07 10:00:00"), Timestamp.valueOf("2023-12-11 10:00:00"), "6");
            projectDAO.insertProject(project1);
            projectDAO.insertProject(project2);

            List<ProjectDTO> projects = projectDAO.getAllProjects();
            assertNotNull(projects, "La lista no debería ser nula");
            assertTrue(projects.size() >= 2, "Debería haber al menos dos proyectos en la lista");
        } catch (SQLException e) {
            fail("Error en testGetAllProjects: " + e.getMessage());
        }
    }
}