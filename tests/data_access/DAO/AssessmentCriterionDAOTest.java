package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.AssessmentCriterionDAO;
import logic.DTO.AssessmentCriterionDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AssessmentCriterionDAOTest {

    private static final Logger logger = LogManager.getLogger(AssessmentCriterionDAOTest.class);

    private static ConecctionDataBase connectionDB;
    private static Connection connection;

    private AssessmentCriterionDAO assessmentCriterionDAO;
    private AssessmentCriterionDTO testCriterion;

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
        assessmentCriterionDAO = new AssessmentCriterionDAO();
    }

    @Test
    void testInsertAssessmentCriterion() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String idCriterion = String.valueOf(randomNumber);
            String nombreCriterio = "Criterio de Prueba " + randomNumber;

            testCriterion = new AssessmentCriterionDTO(idCriterion, nombreCriterio);
            boolean result = assessmentCriterionDAO.insertAssessmentCriterion(testCriterion);
            assertTrue(result, "La inserción debería ser exitosa");

            List<AssessmentCriterionDTO> criteria = assessmentCriterionDAO.getAllAssessmentCriteria();
            AssessmentCriterionDTO insertedCriterion = criteria.stream()
                    .filter(c -> c.getIdCriterion().equals(idCriterion))
                    .findFirst()
                    .orElse(null);

            assertNotNull(insertedCriterion, "El criterio debería existir en la base de datos");
            assertEquals(nombreCriterio, insertedCriterion.getNameCriterion(), "El nombre debería coincidir");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testInsertAssessmentCriterionWithEmptyName() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String idCriterion = String.valueOf(100000 + randomNumber);

            testCriterion = new AssessmentCriterionDTO(idCriterion, "");
            boolean result = assessmentCriterionDAO.insertAssessmentCriterion(testCriterion);

            if (result) {
                AssessmentCriterionDTO retrievedCriterion = assessmentCriterionDAO.searchAssessmentCriterionById(idCriterion);
                assertEquals("", retrievedCriterion.getNameCriterion(), "El nombre vacío debería almacenarse");
            } else {
                assertFalse(result, "No debería permitir insertar con nombre vacío");
            }
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("constraint") || e.getMessage().contains("validation"),
                    "La excepción debería estar relacionada con restricciones de validación");
        }
    }

    @Test
    void testInsertAssessmentCriterionWithNullName() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String idCriterion = String.valueOf(200000 + randomNumber);

            testCriterion = new AssessmentCriterionDTO(idCriterion, null);
            boolean result = assessmentCriterionDAO.insertAssessmentCriterion(testCriterion);
            assertFalse(result, "No debería permitir insertar con nombre nulo");
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("null") || e.getMessage().contains("constraint"),
                    "La excepción debería estar relacionada con valores nulos");
        }
    }

    @Test
    void testSearchAssessmentCriterionById() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String idCriterio = String.valueOf(300000 + randomNumber);
            String nombreCriterio = "Criterio para Consulta " + randomNumber;

            testCriterion = new AssessmentCriterionDTO(idCriterio, nombreCriterio);
            boolean inserted = assessmentCriterionDAO.insertAssessmentCriterion(testCriterion);
            assertTrue(inserted, "La inserción previa debería ser exitosa");

            AssessmentCriterionDTO retrievedCriterion = assessmentCriterionDAO.searchAssessmentCriterionById(idCriterio);
            assertNotNull(retrievedCriterion, "Debería encontrar el criterio");
            assertEquals(idCriterio, retrievedCriterion.getIdCriterion(), "El ID debería coincidir");
            assertEquals(nombreCriterio, retrievedCriterion.getNameCriterion(), "El nombre debería coincidir");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testSearchNonExistentCriterion() {
        try {
            String idInexistente = "999999";
            AssessmentCriterionDTO retrievedCriterion = assessmentCriterionDAO.searchAssessmentCriterionById(idInexistente);

            assertNotNull(retrievedCriterion, "Siempre debería devolver un objeto");
            assertEquals("N/A", retrievedCriterion.getIdCriterion(), "Debería devolver N/A como ID para criterios inexistentes");
            assertEquals("N/A", retrievedCriterion.getNameCriterion(), "Debería devolver N/A como nombre para criterios inexistentes");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testSearchCriterionWithNullId() {
        try {
            AssessmentCriterionDTO retrievedCriterion = assessmentCriterionDAO.searchAssessmentCriterionById(null);
            fail("Debería lanzar excepción al pasar un ID nulo");
        } catch (SQLException | NullPointerException e) {
            assertTrue(true, "Se esperaba una excepción al pasar un ID nulo");
        }
    }

    @Test
    void testUpdateAssessmentCriterion() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String idCriterio = String.valueOf(400000 + randomNumber);
            String nombreOriginal = "Criterio Original " + randomNumber;
            String nombreActualizado = "Criterio Actualizado " + randomNumber;

            AssessmentCriterionDTO originalCriterion = new AssessmentCriterionDTO(idCriterio, nombreOriginal);
            assessmentCriterionDAO.insertAssessmentCriterion(originalCriterion);

            AssessmentCriterionDTO insertedCriterion = assessmentCriterionDAO.searchAssessmentCriterionById(idCriterio);
            assertEquals(nombreOriginal, insertedCriterion.getNameCriterion(), "El nombre original debe estar correcto");

            AssessmentCriterionDTO updatedCriterion = new AssessmentCriterionDTO(idCriterio, nombreActualizado);
            boolean updateResult = assessmentCriterionDAO.updateAssessmentCriterion(updatedCriterion);
            assertTrue(updateResult, "La actualización debería ser exitosa");

            AssessmentCriterionDTO retrievedCriterion = assessmentCriterionDAO.searchAssessmentCriterionById(idCriterio);
            assertNotNull(retrievedCriterion, "El criterio debería existir");
            assertEquals(nombreActualizado, retrievedCriterion.getNameCriterion(), "El nombre debería actualizarse");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testUpdateNonExistentCriterion() {
        try {
            String idInexistente = "888888";
            AssessmentCriterionDTO nonExistentCriterion = new AssessmentCriterionDTO(idInexistente, "Nombre Actualizado");
            boolean updateResult = assessmentCriterionDAO.updateAssessmentCriterion(nonExistentCriterion);
            assertFalse(updateResult, "No debería actualizar un criterio inexistente");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción al intentar actualizar un criterio inexistente");
        }
    }

    @Test
    void testGetAllAssessmentCriteria() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String idCriterio = String.valueOf(500000 + randomNumber);
            String nombreCriterio = "Criterio para Listar " + randomNumber;

            AssessmentCriterionDTO criterionToInsert = new AssessmentCriterionDTO(idCriterio, nombreCriterio);
            boolean inserted = assessmentCriterionDAO.insertAssessmentCriterion(criterionToInsert);
            assertTrue(inserted, "La inserción previa debería ser exitosa");

            List<AssessmentCriterionDTO> criteria = assessmentCriterionDAO.getAllAssessmentCriteria();
            assertNotNull(criteria, "La lista no debería ser nula");
            assertFalse(criteria.isEmpty(), "La lista no debería estar vacía");

            boolean found = criteria.stream()
                    .anyMatch(c -> c.getIdCriterion().equals(idCriterio) && c.getNameCriterion().equals(nombreCriterio));
            assertTrue(found, "Nuestro criterio de prueba debería estar en la lista");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testDeleteAssessmentCriterion() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String idCriterio = String.valueOf(600000 + randomNumber);
            String nombreCriterio = "Criterio para Eliminar " + randomNumber;

            AssessmentCriterionDTO criterionToDelete = new AssessmentCriterionDTO(idCriterio, nombreCriterio);
            boolean inserted = assessmentCriterionDAO.insertAssessmentCriterion(criterionToDelete);
            assertTrue(inserted, "La inserción previa debería ser exitosa");

            AssessmentCriterionDTO before = assessmentCriterionDAO.searchAssessmentCriterionById(idCriterio);
            assertEquals(nombreCriterio, before.getNameCriterion(), "El criterio debería existir antes de eliminarlo");

            boolean deleted = assessmentCriterionDAO.deleteAssessmentCriterion(idCriterio);
            assertTrue(deleted, "La eliminación debería ser exitosa");

            AssessmentCriterionDTO after = assessmentCriterionDAO.searchAssessmentCriterionById(idCriterio);
            assertEquals("N/A", after.getIdCriterion(),
                    "Después de eliminar, debería devolver N/A como ID para criterios inexistentes");
            assertEquals("N/A", after.getNameCriterion(),
                    "Después de eliminar, debería devolver N/A como nombre para criterios inexistentes");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testDeleteNonExistentCriterion() {
        try {
            String idInexistente = "777777";
            boolean deleted = assessmentCriterionDAO.deleteAssessmentCriterion(idInexistente);
            assertFalse(deleted, "No debería eliminar un criterio inexistente");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción al intentar eliminar un criterio inexistente");
        }
    }

    @Test
    void testGetInvalidIdCriterion() {
        try {
            AssessmentCriterionDTO retrievedCriterion = assessmentCriterionDAO.searchAssessmentCriterionById("@");
            assertEquals("N/A", retrievedCriterion.getIdCriterion(), "Debería devolver N/A para ID inválido");
            assertEquals("N/A", retrievedCriterion.getNameCriterion(), "Debería devolver N/A para ID inválido");
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("formato") || e.getMessage().contains("inválido") ||
                    e.getMessage().contains("number"), "Debería lanzar excepción por formato inválido");
        }
    }
}