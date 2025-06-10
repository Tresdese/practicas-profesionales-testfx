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
            fail("Error connecting to the database: " + e.getMessage());
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
            assertTrue(result, "Insertion should be successful");

            List<AssessmentCriterionDTO> criteria = assessmentCriterionDAO.getAllAssessmentCriteria();
            AssessmentCriterionDTO insertedCriterion = criteria.stream()
                    .filter(c -> c.getIdCriterion().equals(criterionId))
                    .findFirst()
                    .orElse(null);

            assertNotNull(insertedCriterion, "Criterion should exist in the database");
            assertEquals(criterionName, insertedCriterion.getNameCriterion(), "Name should match");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("Should not throw exception: " + e.getMessage());
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
                assertEquals("", retrievedCriterion.getNameCriterion(), "Empty name should be stored");
            } else {
                assertFalse(result, "Should not allow insert with empty name");
            }
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("constraint") || e.getMessage().contains("validation"),
                    "Exception should be related to validation constraints");
        }
    }

    @Test
    void testInsertAssessmentCriterionWithNullName() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String criterionId = String.valueOf(200000 + randomNumber);

            testCriterion = new AssessmentCriterionDTO(criterionId, null);
            boolean result = assessmentCriterionDAO.insertAssessmentCriterion(testCriterion);
            assertFalse(result, "Should not allow insert with null name");
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("null") || e.getMessage().contains("constraint"),
                    "Exception should be related to null values");
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
            fail("Should not throw exception: " + e.getMessage());
        }
    }

    @Test
    void testSearchNonExistentCriterion() {
        try {
            String nonExistentId = "999999";
            AssessmentCriterionDTO retrievedCriterion = assessmentCriterionDAO.searchAssessmentCriterionById(nonExistentId);

            assertNotNull(retrievedCriterion, "Should always return an object");
            assertEquals("N/A", retrievedCriterion.getIdCriterion(), "Should return N/A as ID for non-existent criteria");
            assertEquals("N/A", retrievedCriterion.getNameCriterion(), "Should return N/A as name for non-existent criteria");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("Should not throw exception: " + e.getMessage());
        }
    }

    @Test
    void testSearchCriterionWithNullId() {
        try {
            AssessmentCriterionDTO retrievedCriterion = assessmentCriterionDAO.searchAssessmentCriterionById(null);
            assertNotNull(retrievedCriterion, "Should always return an object");
            assertEquals("N/A", retrievedCriterion.getIdCriterion(), "Should return N/A as ID for null criteria");
            assertEquals("N/A", retrievedCriterion.getNameCriterion(), "Should return N/A as name for null criteria");
        } catch (SQLException e) {
            fail("Should not throw exception: " + e.getMessage());
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
            assertEquals(originalName, insertedCriterion.getNameCriterion(), "Original name should be correct");

            AssessmentCriterionDTO updatedCriterion = new AssessmentCriterionDTO(criterionId, updatedName);
            boolean updateResult = assessmentCriterionDAO.updateAssessmentCriterion(updatedCriterion);
            assertTrue(updateResult, "Update should be successful");

            AssessmentCriterionDTO retrievedCriterion = assessmentCriterionDAO.searchAssessmentCriterionById(criterionId);
            assertNotNull(retrievedCriterion, "Criterion should exist");
            assertEquals(updatedName, retrievedCriterion.getNameCriterion(), "Name should be updated");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("Should not throw exception: " + e.getMessage());
        }
    }

    @Test
    void testUpdateNonExistentCriterion() {
        try {
            String nonExistentId = "888888";
            AssessmentCriterionDTO nonExistentCriterion = new AssessmentCriterionDTO(nonExistentId, "Nombre Actualizado");
            boolean updateResult = assessmentCriterionDAO.updateAssessmentCriterion(nonExistentCriterion);
            assertFalse(updateResult, "Should not update a non-existent criterion");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("Should not throw exception when trying to update a non-existent criterion");
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
            assertTrue(inserted, "Previous insertion should be successful");

            List<AssessmentCriterionDTO> criteria = assessmentCriterionDAO.getAllAssessmentCriteria();
            assertNotNull(criteria, "List should not be null");
            assertFalse(criteria.isEmpty(), "List should not be empty");

            boolean found = criteria.stream()
                    .anyMatch(c -> c.getIdCriterion().equals(criterionId) && c.getNameCriterion().equals(criterionName));
            assertTrue(found, "Our test criterion should be in the list");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("Should not throw exception: " + e.getMessage());
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
            assertTrue(inserted, "Previous insertion should be successful");

            AssessmentCriterionDTO before = assessmentCriterionDAO.searchAssessmentCriterionById(criterionId);
            assertEquals(criterionName, before.getNameCriterion(), "Criterion should exist before deleting");

            boolean deleted = assessmentCriterionDAO.deleteAssessmentCriterion(criterionId);
            assertTrue(deleted, "Deletion should be successful");

            AssessmentCriterionDTO after = assessmentCriterionDAO.searchAssessmentCriterionById(criterionId);
            assertEquals("N/A", after.getIdCriterion(),
                    "After deleting, should return N/A as ID for non-existent criteria");
            assertEquals("N/A", after.getNameCriterion(),
                    "After deleting, should return N/A as name for non-existent criteria");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("Should not throw exception: " + e.getMessage());
        }
    }

    @Test
    void testDeleteNonExistentCriterion() {
        try {
            String nonExistentId = "777777";
            boolean deleted = assessmentCriterionDAO.deleteAssessmentCriterion(nonExistentId);
            assertFalse(deleted, "Should not delete a non-existent criterion");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("Should not throw exception when trying to delete a non-existent criterion");
        }
    }

    @Test
    void testGetInvalidIdCriterion() {
        try {
            AssessmentCriterionDTO retrievedCriterion = assessmentCriterionDAO.searchAssessmentCriterionById("@");
            assertEquals("N/A", retrievedCriterion.getIdCriterion(), "Should return N/A for invalid ID");
            assertEquals("N/A", retrievedCriterion.getNameCriterion(), "Should return N/A for invalid ID");
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("formato") || e.getMessage().contains("inválido") ||
                    e.getMessage().contains("number"), "Should throw exception for invalid format");
        }
    }
}