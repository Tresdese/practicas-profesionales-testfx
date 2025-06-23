package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.AssessmentCriterionDAO;
import logic.DTO.AssessmentCriterionDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AssessmentCriterionDAOTest {

    private static final Logger logger = LogManager.getLogger(AssessmentCriterionDAOTest.class);

    private static ConnectionDataBase connectionDB;
    private static Connection connection;

    private AssessmentCriterionDAO assessmentCriterionDAO;
    private AssessmentCriterionDTO testCriterion;

    @BeforeAll
    static void setUpClass() {
        try {
            connectionDB = new ConnectionDataBase();
            connection = connectionDB.connectDB();
        } catch (SQLException e) {
            fail("Error connecting to the database: " + e.getMessage());
        } catch (IOException e) {
            fail("Error loading database configuration: " + e.getMessage());
        } catch (Exception e) {
            fail("Unexpected error connecting to the database: " + e.getMessage());
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
            String criterionId = String.valueOf(randomNumber);
            String criterionName = "Criterio de Prueba " + randomNumber;

            testCriterion = new AssessmentCriterionDTO(criterionId, criterionName);
            boolean result = assessmentCriterionDAO.insertAssessmentCriterion(testCriterion);
            assertTrue(result, "Insercion debería ser exitosa");

            List<AssessmentCriterionDTO> criteria = assessmentCriterionDAO.getAllAssessmentCriteria();
            AssessmentCriterionDTO insertedCriterion = criteria.stream()
                    .filter(c -> c.getIdCriterion().equals(criterionId))
                    .findFirst()
                    .orElse(null);

            assertNotNull(insertedCriterion, "Criterio debería estar en la lista después de la inserción");
            assertEquals(criterionName, insertedCriterion.getNameCriterion(), "Nombre del criterio debería coincidir");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No deberia lanzar una SQLException: " + e.getMessage());
        } catch (IOException e) {
            logger.error("Error: " + e.getMessage());
            fail("No deberia lanzar una IOException: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado: " + e.getMessage());
            fail("No deberia lanzar una Exception inesperada: " + e.getMessage());
        }
    }

    @Test
    void testInsertAssessmentCriterionWithEmptyName() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String criterionId = String.valueOf(100000 + randomNumber);

            testCriterion = new AssessmentCriterionDTO(criterionId, "");
            boolean result = assessmentCriterionDAO.insertAssessmentCriterion(testCriterion);

            if (result) {
                AssessmentCriterionDTO retrievedCriterion = assessmentCriterionDAO.searchAssessmentCriterionById(criterionId);
                assertEquals("", retrievedCriterion.getNameCriterion(), "Nombre vacio debería ser permitido");
            } else {
                assertFalse(result, "No debería permitir insertar un criterio con nombre vacío");
            }
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("constraint") || e.getMessage().contains("validation"),
                    "Excepción debería estar relacionada con restricciones de validación");
        } catch (IOException e) {
            logger.error("Error: " + e.getMessage());
            fail("No deberia lanzar una IOException " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error: " + e.getMessage());
            fail("No deberia lanzar una Exception inesperada: " + e.getMessage());
        }
    }

    @Test
    void testInsertAssessmentCriterionWithNullName() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String criterionId = String.valueOf(200000 + randomNumber);

            testCriterion = new AssessmentCriterionDTO(criterionId, null);
            boolean result = assessmentCriterionDAO.insertAssessmentCriterion(testCriterion);
            assertFalse(result, "No debería permitir insertar un criterio con nombre nulo");
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("null") || e.getMessage().contains("constraint"),
                    "Excepción debería estar relacionada con restricciones de null");
        } catch (IOException e) {
            logger.error("Error: " + e.getMessage());
            fail("No deberia lanzar una IOException: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado: " + e.getMessage());
            fail("No deberia lanzar una Excepcion inesperada: " + e.getMessage());
        }
    }

    @Test
    void testSearchAssessmentCriterionById() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String criterionId = String.valueOf(300000 + randomNumber);
            String criterionName = "Criterio para Consulta " + randomNumber;

            testCriterion = new AssessmentCriterionDTO(criterionId, criterionName);
            boolean inserted = assessmentCriterionDAO.insertAssessmentCriterion(testCriterion);
            assertTrue(inserted, "Previous insertion should be successful");

            AssessmentCriterionDTO retrievedCriterion = assessmentCriterionDAO.searchAssessmentCriterionById(criterionId);
            assertNotNull(retrievedCriterion, "Should find the criterion");
            assertEquals(criterionId, retrievedCriterion.getIdCriterion(), "ID should match");
            assertEquals(criterionName, retrievedCriterion.getNameCriterion(), "Name should match");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No deberia lanzar una SQLException: " + e.getMessage());
        } catch (IOException e) {
            logger.error("Error: " + e.getMessage());
            fail("No deberia lanzar una IOException: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado: " + e.getMessage());
            fail("No deberia lanzar una Exception: " + e.getMessage());
        }
    }

    @Test
    void testSearchNonExistentCriterion() {
        try {
            String nonExistentId = "999999";
            AssessmentCriterionDTO retrievedCriterion = assessmentCriterionDAO.searchAssessmentCriterionById(nonExistentId);

            assertNotNull(retrievedCriterion, "Deberia devolver un objeto no nulo");
            assertEquals("N/A", retrievedCriterion.getIdCriterion(), "Deberia retornar N/A como ID para criterios no existentes");
            assertEquals("N/A", retrievedCriterion.getNameCriterion(), "Deberia retornar N/A como nombre para criterios no existentes");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No deberia lanzar una SQLException: " + e.getMessage());
        } catch (IOException e) {
            logger.error("Error: " + e.getMessage());
            fail("No deberia lanzar una IOException: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado: " + e.getMessage());
            fail("No deberia lanzar una Exception inesperada: " + e.getMessage());
        }
    }

    @Test
    void testSearchCriterionWithNullId() {
        try {
            AssessmentCriterionDTO retrievedCriterion = assessmentCriterionDAO.searchAssessmentCriterionById(null);
            assertNotNull(retrievedCriterion, "Deberia devolver un objeto no nulo para ID nulo");
            assertEquals("N/A", retrievedCriterion.getIdCriterion(), "Deberia retornar N/A como ID para criterios nulos");
            assertEquals("N/A", retrievedCriterion.getNameCriterion(), "Deberia retornar N/A como nombre para criterios nulos");
        } catch (SQLException e) {
            fail("No deberia retornar una SQLException: " + e.getMessage());
        } catch (IOException e) {
            logger.error("Error: " + e.getMessage());
            fail("No deberia lanzar una IOException: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado: " + e.getMessage());
            fail("No deberia lanzar una Exception inesperada: " + e.getMessage());
        }
    }

    @Test
    void testUpdateAssessmentCriterion() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String criterionId = String.valueOf(400000 + randomNumber);
            String originalName = "Criterio Original " + randomNumber;
            String updatedName = "Criterio Actualizado " + randomNumber;

            AssessmentCriterionDTO originalCriterion = new AssessmentCriterionDTO(criterionId, originalName);
            assessmentCriterionDAO.insertAssessmentCriterion(originalCriterion);

            AssessmentCriterionDTO insertedCriterion = assessmentCriterionDAO.searchAssessmentCriterionById(criterionId);
            assertEquals(originalName, insertedCriterion.getNameCriterion(), "Nombre original debe coincidir");

            AssessmentCriterionDTO updatedCriterion = new AssessmentCriterionDTO(criterionId, updatedName);
            boolean updateResult = assessmentCriterionDAO.updateAssessmentCriterion(updatedCriterion);
            assertTrue(updateResult, "Actualización debería ser exitosa");

            AssessmentCriterionDTO retrievedCriterion = assessmentCriterionDAO.searchAssessmentCriterionById(criterionId);
            assertNotNull(retrievedCriterion, "Criterio debería existir después de la actualización");
            assertEquals(updatedName, retrievedCriterion.getNameCriterion(), "Nombre actualizado debe coincidir");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No deberia lanzar una SQLException: " + e.getMessage());
        } catch (IOException e) {
            logger.error("Error: " + e.getMessage());
            fail("No deberia lanzar una IOException: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado: " + e.getMessage());
            fail("No deberia lanzar una Exception inesperada: " + e.getMessage());
        }
    }

    @Test
    void testUpdateNonExistentCriterion() {
        try {
            String nonExistentId = "888888";
            AssessmentCriterionDTO nonExistentCriterion = new AssessmentCriterionDTO(nonExistentId, "Nombre Actualizado");
            boolean updateResult = assessmentCriterionDAO.updateAssessmentCriterion(nonExistentCriterion);
            assertFalse(updateResult, "No debería actualizar un criterio inexistente");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No deberia lanzar una SQLException: " + e.getMessage());
        } catch (IOException e) {
            logger.error("Error: " + e.getMessage());
            fail("No deberia lanzar una IOException: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado: " + e.getMessage());
            fail("No deberia lanzar una Exception inesperada: " + e.getMessage());
        }
    }

    @Test
    void testGetAllAssessmentCriteria() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String criterionId = String.valueOf(500000 + randomNumber);
            String criterionName = "Criterio para Listar " + randomNumber;

            AssessmentCriterionDTO criterionToInsert = new AssessmentCriterionDTO(criterionId, criterionName);
            boolean inserted = assessmentCriterionDAO.insertAssessmentCriterion(criterionToInsert);
            assertTrue(inserted, "Insercion previa debería ser exitosa");

            List<AssessmentCriterionDTO> criteria = assessmentCriterionDAO.getAllAssessmentCriteria();
            assertNotNull(criteria, "Lista de criterios no debería ser nula");
            assertFalse(criteria.isEmpty(), "Lista de criterios no debería estar vacía");

            boolean found = criteria.stream()
                    .anyMatch(c -> c.getIdCriterion().equals(criterionId) && c.getNameCriterion().equals(criterionName));
            assertTrue(found, "Nuestro criterio insertado debería estar en la lista");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No deberia lanzar una SQLException: " + e.getMessage());
        } catch (IOException e) {
            logger.error("Error: " + e.getMessage());
            fail("No deberia lanzar una IOException: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado: " + e.getMessage());
            fail("No deberia lanzar una Exception inesperada: " + e.getMessage());
        }
    }

    @Test
    void testDeleteAssessmentCriterion() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String criterionId = String.valueOf(600000 + randomNumber);
            String criterionName = "Criterio para Eliminar " + randomNumber;

            AssessmentCriterionDTO criterionToDelete = new AssessmentCriterionDTO(criterionId, criterionName);
            boolean inserted = assessmentCriterionDAO.insertAssessmentCriterion(criterionToDelete);
            assertTrue(inserted, "Insercion previa debería ser exitosa");

            AssessmentCriterionDTO before = assessmentCriterionDAO.searchAssessmentCriterionById(criterionId);
            assertEquals(criterionName, before.getNameCriterion(), "Criterio debería existir antes de eliminar");

            boolean deleted = assessmentCriterionDAO.deleteAssessmentCriterion(criterionId);
            assertTrue(deleted, "Eliminación debería ser exitosa");

            AssessmentCriterionDTO after = assessmentCriterionDAO.searchAssessmentCriterionById(criterionId);
            assertEquals("N/A", after.getIdCriterion(),
                    "Despues de eliminar, debería retornar N/A como ID para criterios no existentes");
            assertEquals("N/A", after.getNameCriterion(),
                    "Despues de eliminar, debería retornar N/A como nombre para criterios no existentes");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No deberia lanzar una SQLException: " + e.getMessage());
        } catch (IOException e) {
            logger.error("Error: " + e.getMessage());
            fail("No deberia lanzar una IOException: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado: " + e.getMessage());
            fail("No deberia lanzar una Exception inesperada: " + e.getMessage());
        }
    }

    @Test
    void testDeleteNonExistentCriterion() {
        try {
            String nonExistentId = "777777";
            boolean deleted = assessmentCriterionDAO.deleteAssessmentCriterion(nonExistentId);
            assertFalse(deleted, "No debería eliminar un criterio inexistente");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No deberia lanzar una SQLException: " + e.getMessage());
        } catch (IOException e) {
            logger.error("Error: " + e.getMessage());
            fail("No deberia lanzar una IOException: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado: " + e.getMessage());
            fail("No deberia lanzar una Exception inesperada: " + e.getMessage());
        }
    }

    @Test
    void testGetInvalidIdCriterion() {
        try {
            AssessmentCriterionDTO retrievedCriterion = assessmentCriterionDAO.searchAssessmentCriterionById("@");
            assertEquals("N/A", retrievedCriterion.getIdCriterion(), "Deberia retornar N/A para ID inválido");
            assertEquals("N/A", retrievedCriterion.getNameCriterion(), "Deberia retornar N/A para nombre de criterio inválido");
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("formato") || e.getMessage().contains("inválido") ||
                    e.getMessage().contains("number"), "Deberia lanzar una SQLException relacionada con formato inválido");
        } catch (IOException e) {
            logger.error("Error: " + e.getMessage());
            fail("No deberia lanzar una IOException: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado: " + e.getMessage());
            fail("No deberia lanzar una Exception inesperada: " + e.getMessage());
        }
    }
}