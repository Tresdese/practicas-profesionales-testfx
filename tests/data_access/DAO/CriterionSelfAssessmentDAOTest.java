package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.CriterionSelfAssessmentDAO;
import logic.DTO.CriterionSelfAssessmentDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CriterionSelfAssessmentDAOTest {

    private static final Logger logger = LogManager.getLogger(CriterionSelfAssessmentDAOTest.class);

    private static ConecctionDataBase connectionDB;
    private static Connection connection;

    private CriterionSelfAssessmentDAO criterionSelfAssessmentDAO;

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
        criterionSelfAssessmentDAO = new CriterionSelfAssessmentDAO(connection);
    }

    @Test
    void testInsertCriterionSelfAssessment() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String idSelfAssessment = String.valueOf(randomNumber);
            String idCriteria = String.valueOf(randomNumber + 5000);

            CriterionSelfAssessmentDTO criterionSelfAssessment = new CriterionSelfAssessmentDTO(idSelfAssessment, idCriteria);
            boolean result = criterionSelfAssessmentDAO.insertCriterionSelfAssessment(criterionSelfAssessment);
            assertTrue(result, "La inserción debería ser exitosa");

            List<CriterionSelfAssessmentDTO> allCriteria = criterionSelfAssessmentDAO.getAllCriterionSelfAssessments();
            CriterionSelfAssessmentDTO inserted = allCriteria.stream()
                    .filter(c -> c.getIdSelfAssessment().equals(idSelfAssessment) && c.getIdCriteria().equals(idCriteria))
                    .findFirst()
                    .orElse(null);

            assertNotNull(inserted, "El criterio debería existir en la base de datos");
            assertEquals(idSelfAssessment, inserted.getIdSelfAssessment(), "El ID de autoevaluación debería coincidir");
            assertEquals(idCriteria, inserted.getIdCriteria(), "El ID de criterio debería coincidir");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testInsertCriterionSelfAssessmentWithEmptyIds() {
        try {
            CriterionSelfAssessmentDTO criterionSelfAssessment = new CriterionSelfAssessmentDTO("", "");
            boolean result = criterionSelfAssessmentDAO.insertCriterionSelfAssessment(criterionSelfAssessment);
            assertFalse(result, "No debería permitir insertar con IDs vacíos");
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("constraint") || e.getMessage().contains("validation"),
                    "La excepción debería estar relacionada con restricciones de validación");
        }
    }

    @Test
    void testInsertCriterionSelfAssessmentWithNullIds() {
        try {
            CriterionSelfAssessmentDTO criterionSelfAssessment = new CriterionSelfAssessmentDTO(null, null);
            boolean result = criterionSelfAssessmentDAO.insertCriterionSelfAssessment(criterionSelfAssessment);
            assertFalse(result, "No debería permitir insertar con IDs nulos");
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("null") || e.getMessage().contains("constraint"),
                    "La excepción debería estar relacionada con valores nulos");
        } catch (NullPointerException e) {
            assertTrue(true, "Es aceptable lanzar NullPointerException con valores nulos");
        }
    }

    @Test
    void testSearchCriterionSelfAssessmentByIds() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String idSelfAssessment = String.valueOf(100000 + randomNumber);
            String idCriteria = String.valueOf(150000 + randomNumber);

            CriterionSelfAssessmentDTO criterionSelfAssessment = new CriterionSelfAssessmentDTO(idSelfAssessment, idCriteria);
            boolean inserted = criterionSelfAssessmentDAO.insertCriterionSelfAssessment(criterionSelfAssessment);
            assertTrue(inserted, "La inserción previa debería ser exitosa");

            CriterionSelfAssessmentDTO retrieved = criterionSelfAssessmentDAO.searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(idSelfAssessment, idCriteria);
            assertNotNull(retrieved, "Debería encontrar el criterio");
            assertEquals(idSelfAssessment, retrieved.getIdSelfAssessment(), "El ID de autoevaluación debería coincidir");
            assertEquals(idCriteria, retrieved.getIdCriteria(), "El ID de criterio debería coincidir");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testSearchNonExistentCriterionSelfAssessment() {
        try {
            String idSelfAssessment = "999999";
            String idCriteria = "888888";

            CriterionSelfAssessmentDTO retrieved = criterionSelfAssessmentDAO.searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(idSelfAssessment, idCriteria);

            assertNotNull(retrieved, "Siempre debería devolver un objeto");
            assertEquals("N/A", retrieved.getIdSelfAssessment(), "Debería devolver N/A como ID para registros inexistentes");
            assertEquals("N/A", retrieved.getIdCriteria(), "Debería devolver N/A como ID para registros inexistentes");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testSearchCriterionSelfAssessmentWithNullIds() {
        try {
            CriterionSelfAssessmentDTO retrieved = criterionSelfAssessmentDAO.searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(null, null);
            fail("Debería lanzar excepción al pasar IDs nulos");
        } catch (SQLException | NullPointerException e) {
            assertTrue(true, "Se esperaba una excepción al pasar IDs nulos");
        }
    }

    @Test
    void testUpdateCriterionSelfAssessment() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String idSelfAssessment = String.valueOf(200000 + randomNumber);
            String idCriteriaOriginal = String.valueOf(250000 + randomNumber);
            String idCriteriaActualizado = String.valueOf(300000 + randomNumber);

            CriterionSelfAssessmentDTO original = new CriterionSelfAssessmentDTO(idSelfAssessment, idCriteriaOriginal);
            criterionSelfAssessmentDAO.insertCriterionSelfAssessment(original);

            CriterionSelfAssessmentDTO inserted = criterionSelfAssessmentDAO.searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(idSelfAssessment, idCriteriaOriginal);
            assertEquals(idCriteriaOriginal, inserted.getIdCriteria(), "El ID de criterio original debe estar correcto");

            CriterionSelfAssessmentDTO updated = new CriterionSelfAssessmentDTO(idSelfAssessment, idCriteriaActualizado);
            boolean updateResult = criterionSelfAssessmentDAO.updateCriterionSelfAssessment(updated);
            assertTrue(updateResult, "La actualización debería ser exitosa");

            CriterionSelfAssessmentDTO retrievedAfterUpdate = criterionSelfAssessmentDAO.searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(idSelfAssessment, idCriteriaActualizado);
            assertNotNull(retrievedAfterUpdate, "El registro debería existir");
            assertEquals(idCriteriaActualizado, retrievedAfterUpdate.getIdCriteria(), "El ID de criterio debería actualizarse");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testUpdateNonExistentCriterionSelfAssessment() {
        try {
            String idSelfAssessment = "777777";
            String idCriteria = "666666";

            CriterionSelfAssessmentDTO nonExistent = new CriterionSelfAssessmentDTO(idSelfAssessment, idCriteria);
            boolean updateResult = criterionSelfAssessmentDAO.updateCriterionSelfAssessment(nonExistent);
            assertFalse(updateResult, "No debería actualizar un registro inexistente");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción al intentar actualizar un registro inexistente");
        }
    }

    @Test
    void testGetAllCriterionSelfAssessments() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String idSelfAssessment = String.valueOf(400000 + randomNumber);
            String idCriteria = String.valueOf(450000 + randomNumber);

            CriterionSelfAssessmentDTO toInsert = new CriterionSelfAssessmentDTO(idSelfAssessment, idCriteria);
            boolean inserted = criterionSelfAssessmentDAO.insertCriterionSelfAssessment(toInsert);
            assertTrue(inserted, "La inserción previa debería ser exitosa");

            List<CriterionSelfAssessmentDTO> allCriteria = criterionSelfAssessmentDAO.getAllCriterionSelfAssessments();
            assertNotNull(allCriteria, "La lista no debería ser nula");
            assertFalse(allCriteria.isEmpty(), "La lista no debería estar vacía");

            boolean found = allCriteria.stream()
                    .anyMatch(c -> c.getIdSelfAssessment().equals(idSelfAssessment) && c.getIdCriteria().equals(idCriteria));
            assertTrue(found, "Nuestro criterio de prueba debería estar en la lista");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testDeleteCriterionSelfAssessment() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String idSelfAssessment = String.valueOf(500000 + randomNumber);
            String idCriteria = String.valueOf(550000 + randomNumber);

            CriterionSelfAssessmentDTO toDelete = new CriterionSelfAssessmentDTO(idSelfAssessment, idCriteria);
            boolean inserted = criterionSelfAssessmentDAO.insertCriterionSelfAssessment(toDelete);
            assertTrue(inserted, "La inserción previa debería ser exitosa");

            CriterionSelfAssessmentDTO before = criterionSelfAssessmentDAO.searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(idSelfAssessment, idCriteria);
            assertEquals(idSelfAssessment, before.getIdSelfAssessment(), "El registro debería existir antes de eliminarlo");
            assertEquals(idCriteria, before.getIdCriteria(), "El registro debería existir antes de eliminarlo");

            boolean deleted = criterionSelfAssessmentDAO.deleteCriterionSelfAssessment(idSelfAssessment, idCriteria);
            assertTrue(deleted, "La eliminación debería ser exitosa");

            CriterionSelfAssessmentDTO after = criterionSelfAssessmentDAO.searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(idSelfAssessment, idCriteria);
            assertEquals("N/A", after.getIdSelfAssessment(), "Después de eliminar, debería devolver N/A como ID");
            assertEquals("N/A", after.getIdCriteria(), "Después de eliminar, debería devolver N/A como ID");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testDeleteNonExistentCriterionSelfAssessment() {
        try {
            String idSelfAssessment = "555555";
            String idCriteria = "444444";

            boolean deleted = criterionSelfAssessmentDAO.deleteCriterionSelfAssessment(idSelfAssessment, idCriteria);
            assertFalse(deleted, "No debería eliminar un registro inexistente");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción al intentar eliminar un registro inexistente");
        }
    }

    @Test
    void testGetInvalidIdsCriterionSelfAssessment() {
        try {
            CriterionSelfAssessmentDTO retrieved = criterionSelfAssessmentDAO.searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria("@", "#");
            assertEquals("N/A", retrieved.getIdSelfAssessment(), "Debería devolver N/A para IDs inválidos");
            assertEquals("N/A", retrieved.getIdCriteria(), "Debería devolver N/A para IDs inválidos");
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("formato") || e.getMessage().contains("inválido") ||
                    e.getMessage().contains("number"), "Debería lanzar excepción por formato inválido");
        }
    }

    @Test
    void testInsertDuplicateCriterionSelfAssessment() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String idSelfAssessment = String.valueOf(600000 + randomNumber);
            String idCriteria = String.valueOf(650000 + randomNumber);

            // Primera inserción
            CriterionSelfAssessmentDTO firstInsert = new CriterionSelfAssessmentDTO(idSelfAssessment, idCriteria);
            boolean firstResult = criterionSelfAssessmentDAO.insertCriterionSelfAssessment(firstInsert);
            assertTrue(firstResult, "La primera inserción debería ser exitosa");

            CriterionSelfAssessmentDTO duplicateInsert = new CriterionSelfAssessmentDTO(idSelfAssessment, idCriteria);
            try {
                boolean secondResult = criterionSelfAssessmentDAO.insertCriterionSelfAssessment(duplicateInsert);
                assertFalse(secondResult, "No debería permitir duplicados");
            } catch (SQLException e) {
                assertTrue(e.getMessage().contains("duplicate") || e.getMessage().contains("unique") ||
                        e.getMessage().contains("constraint"), "La excepción debería estar relacionada con duplicados");
            }
        } catch (SQLException e) {
            logger.error("Error en la primera inserción: " + e.getMessage());
            fail("No debería fallar la primera inserción: " + e.getMessage());
        }
    }
}