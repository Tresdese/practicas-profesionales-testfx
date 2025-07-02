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

    private static ConnectionDataBase connectionDataBase;
    private static Connection databaseConnection;

    private AssessmentCriterionDAO assessmentCriterionDataAccessObject;
    private AssessmentCriterionDTO testAssessmentCriterion;

    @BeforeAll
    static void setUpClass() {
        try {
            connectionDataBase = new ConnectionDataBase();
            databaseConnection = connectionDataBase.connectDataBase();
        } catch (SQLException exception) {
            fail("Error al conectar con la base de datos: " + exception.getMessage());
        } catch (IOException exception) {
            fail("Error al cargar la configuración de la base de datos: " + exception.getMessage());
        } catch (Exception exception) {
            fail("Error inesperado al conectar con la base de datos: " + exception.getMessage());
        }
    }

    @AfterAll
    static void tearDownClass() {
        connectionDataBase.close();
    }

    @BeforeEach
    void setUp() {
        assessmentCriterionDataAccessObject = new AssessmentCriterionDAO();
    }

    @Test
    void testInsertAssessmentCriterion() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String criterionId = String.valueOf(randomNumber);
            String criterionName = "Criterio de Prueba " + randomNumber;

            testAssessmentCriterion = new AssessmentCriterionDTO(criterionId, criterionName);
            boolean wasInserted = assessmentCriterionDataAccessObject.insertAssessmentCriterion(testAssessmentCriterion);
            assertTrue(wasInserted, "Inserción debería ser exitosa");

            List<AssessmentCriterionDTO> assessmentCriteriaList = assessmentCriterionDataAccessObject.getAllAssessmentCriteria();
            AssessmentCriterionDTO insertedAssessmentCriterion = assessmentCriteriaList.stream()
                    .filter(criterion -> criterion.getIdCriterion().equals(criterionId))
                    .findFirst()
                    .orElse(null);

            assertNotNull(insertedAssessmentCriterion, "El criterio debería estar en la lista después de la inserción");
            assertEquals(criterionName, insertedAssessmentCriterion.getNameCriterion(), "El nombre del criterio debería coincidir");
        } catch (SQLException exception) {
            logger.error("Error: " + exception.getMessage());
            fail("No debería lanzar una SQLException: " + exception.getMessage());
        } catch (IOException exception) {
            logger.error("Error: " + exception.getMessage());
            fail("No debería lanzar una IOException: " + exception.getMessage());
        } catch (Exception exception) {
            logger.error("Error inesperado: " + exception.getMessage());
            fail("No debería lanzar una Exception inesperada: " + exception.getMessage());
        }
    }

    @Test
    void testInsertAssessmentCriterionWithEmptyName() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String criterionId = String.valueOf(100000 + randomNumber);

            testAssessmentCriterion = new AssessmentCriterionDTO(criterionId, "");
            boolean wasInserted = assessmentCriterionDataAccessObject.insertAssessmentCriterion(testAssessmentCriterion);

            if (wasInserted) {
                AssessmentCriterionDTO retrievedAssessmentCriterion = assessmentCriterionDataAccessObject.searchAssessmentCriterionById(criterionId);
                assertEquals("", retrievedAssessmentCriterion.getNameCriterion(), "Nombre vacío debería ser permitido");
            } else {
                assertFalse(wasInserted, "No debería permitir insertar un criterio con nombre vacío");
            }
        } catch (SQLException exception) {
            assertTrue(exception.getMessage().contains("constraint") || exception.getMessage().contains("validation"),
                    "Excepción debería estar relacionada con restricciones de validación");
        } catch (IOException exception) {
            logger.error("Error: " + exception.getMessage());
            fail("No debería lanzar una IOException: " + exception.getMessage());
        } catch (Exception exception) {
            logger.error("Error inesperado: " + exception.getMessage());
            fail("No debería lanzar una Exception inesperada: " + exception.getMessage());
        }
    }

    @Test
    void testInsertAssessmentCriterionWithNullName() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String criterionId = String.valueOf(200000 + randomNumber);

            testAssessmentCriterion = new AssessmentCriterionDTO(criterionId, null);
            boolean wasInserted = assessmentCriterionDataAccessObject.insertAssessmentCriterion(testAssessmentCriterion);
            assertFalse(wasInserted, "No debería permitir insertar un criterio con nombre nulo");
        } catch (SQLException exception) {
            assertTrue(exception.getMessage().contains("null") || exception.getMessage().contains("constraint"),
                    "Excepción debería estar relacionada con restricciones de null");
        } catch (IOException exception) {
            logger.error("Error: " + exception.getMessage());
            fail("No debería lanzar una IOException: " + exception.getMessage());
        } catch (Exception exception) {
            logger.error("Error inesperado: " + exception.getMessage());
            fail("No debería lanzar una Exception inesperada: " + exception.getMessage());
        }
    }

    @Test
    void testSearchAssessmentCriterionById() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String criterionId = String.valueOf(300000 + randomNumber);
            String criterionName = "Criterio para Consulta " + randomNumber;

            testAssessmentCriterion = new AssessmentCriterionDTO(criterionId, criterionName);
            boolean wasInserted = assessmentCriterionDataAccessObject.insertAssessmentCriterion(testAssessmentCriterion);
            assertTrue(wasInserted, "La inserción previa debería ser exitosa");

            AssessmentCriterionDTO retrievedAssessmentCriterion = assessmentCriterionDataAccessObject.searchAssessmentCriterionById(criterionId);
            assertNotNull(retrievedAssessmentCriterion, "El criterio debería encontrarse");
            assertEquals(criterionId, retrievedAssessmentCriterion.getIdCriterion(), "El ID debería coincidir");
            assertEquals(criterionName, retrievedAssessmentCriterion.getNameCriterion(), "El nombre debería coincidir");
        } catch (SQLException exception) {
            logger.error("Error: " + exception.getMessage());
            fail("No debería lanzar una SQLException: " + exception.getMessage());
        } catch (IOException exception) {
            logger.error("Error: " + exception.getMessage());
            fail("No debería lanzar una IOException: " + exception.getMessage());
        } catch (Exception exception) {
            logger.error("Error inesperado: " + exception.getMessage());
            fail("No debería lanzar una Exception inesperada: " + exception.getMessage());
        }
    }

    @Test
    void testSearchNonExistentCriterion() {
        try {
            String nonExistentId = "999999";
            AssessmentCriterionDTO retrievedAssessmentCriterion = assessmentCriterionDataAccessObject.searchAssessmentCriterionById(nonExistentId);

            assertNotNull(retrievedAssessmentCriterion, "Debería devolver un objeto no nulo");
            assertEquals("N/A", retrievedAssessmentCriterion.getIdCriterion(), "Debería retornar N/A como ID para criterios no existentes");
            assertEquals("N/A", retrievedAssessmentCriterion.getNameCriterion(), "Debería retornar N/A como nombre para criterios no existentes");
        } catch (SQLException exception) {
            logger.error("Error: " + exception.getMessage());
            fail("No debería lanzar una SQLException: " + exception.getMessage());
        } catch (IOException exception) {
            logger.error("Error: " + exception.getMessage());
            fail("No debería lanzar una IOException: " + exception.getMessage());
        } catch (Exception exception) {
            logger.error("Error inesperado: " + exception.getMessage());
            fail("No debería lanzar una Exception inesperada: " + exception.getMessage());
        }
    }

    @Test
    void testSearchCriterionWithNullId() {
        try {
            AssessmentCriterionDTO retrievedAssessmentCriterion = assessmentCriterionDataAccessObject.searchAssessmentCriterionById(null);
            assertNotNull(retrievedAssessmentCriterion, "Debería devolver un objeto no nulo para ID nulo");
            assertEquals("N/A", retrievedAssessmentCriterion.getIdCriterion(), "Debería retornar N/A como ID para criterios nulos");
            assertEquals("N/A", retrievedAssessmentCriterion.getNameCriterion(), "Debería retornar N/A como nombre para criterios nulos");
        } catch (SQLException exception) {
            fail("No debería lanzar una SQLException: " + exception.getMessage());
        } catch (IOException exception) {
            logger.error("Error: " + exception.getMessage());
            fail("No debería lanzar una IOException: " + exception.getMessage());
        } catch (Exception exception) {
            logger.error("Error inesperado: " + exception.getMessage());
            fail("No debería lanzar una Exception inesperada: " + exception.getMessage());
        }
    }

    @Test
    void testUpdateAssessmentCriterion() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String criterionId = String.valueOf(400000 + randomNumber);
            String originalName = "Criterio Original " + randomNumber;
            String updatedName = "Criterio Actualizado " + randomNumber;

            AssessmentCriterionDTO originalAssessmentCriterion = new AssessmentCriterionDTO(criterionId, originalName);
            assessmentCriterionDataAccessObject.insertAssessmentCriterion(originalAssessmentCriterion);

            AssessmentCriterionDTO insertedAssessmentCriterion = assessmentCriterionDataAccessObject.searchAssessmentCriterionById(criterionId);
            assertEquals(originalName, insertedAssessmentCriterion.getNameCriterion(), "El nombre original debe coincidir");

            AssessmentCriterionDTO updatedAssessmentCriterion = new AssessmentCriterionDTO(criterionId, updatedName);
            boolean wasUpdated = assessmentCriterionDataAccessObject.updateAssessmentCriterion(updatedAssessmentCriterion);
            assertTrue(wasUpdated, "La actualización debería ser exitosa");

            AssessmentCriterionDTO retrievedAssessmentCriterion = assessmentCriterionDataAccessObject.searchAssessmentCriterionById(criterionId);
            assertNotNull(retrievedAssessmentCriterion, "El criterio debería existir después de la actualización");
            assertEquals(updatedName, retrievedAssessmentCriterion.getNameCriterion(), "El nombre actualizado debe coincidir");
        } catch (SQLException exception) {
            logger.error("Error: " + exception.getMessage());
            fail("No debería lanzar una SQLException: " + exception.getMessage());
        } catch (IOException exception) {
            logger.error("Error: " + exception.getMessage());
            fail("No debería lanzar una IOException: " + exception.getMessage());
        } catch (Exception exception) {
            logger.error("Error inesperado: " + exception.getMessage());
            fail("No debería lanzar una Exception inesperada: " + exception.getMessage());
        }
    }

    @Test
    void testUpdateNonExistentCriterion() {
        try {
            String nonExistentId = "888888";
            AssessmentCriterionDTO nonExistentAssessmentCriterion = new AssessmentCriterionDTO(nonExistentId, "Nombre Actualizado");
            boolean wasUpdated = assessmentCriterionDataAccessObject.updateAssessmentCriterion(nonExistentAssessmentCriterion);
            assertFalse(wasUpdated, "No debería actualizar un criterio inexistente");
        } catch (SQLException exception) {
            logger.error("Error: " + exception.getMessage());
            fail("No debería lanzar una SQLException: " + exception.getMessage());
        } catch (IOException exception) {
            logger.error("Error: " + exception.getMessage());
            fail("No debería lanzar una IOException: " + exception.getMessage());
        } catch (Exception exception) {
            logger.error("Error inesperado: " + exception.getMessage());
            fail("No debería lanzar una Exception inesperada: " + exception.getMessage());
        }
    }

    @Test
    void testGetAllAssessmentCriteria() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String criterionId = String.valueOf(500000 + randomNumber);
            String criterionName = "Criterio para Listar " + randomNumber;

            AssessmentCriterionDTO assessmentCriterionToInsert = new AssessmentCriterionDTO(criterionId, criterionName);
            boolean wasInserted = assessmentCriterionDataAccessObject.insertAssessmentCriterion(assessmentCriterionToInsert);
            assertTrue(wasInserted, "La inserción previa debería ser exitosa");

            List<AssessmentCriterionDTO> assessmentCriteriaList = assessmentCriterionDataAccessObject.getAllAssessmentCriteria();
            assertNotNull(assessmentCriteriaList, "La lista de criterios no debería ser nula");
            assertFalse(assessmentCriteriaList.isEmpty(), "La lista de criterios no debería estar vacía");

            boolean found = assessmentCriteriaList.stream()
                    .anyMatch(criterion -> criterion.getIdCriterion().equals(criterionId) && criterion.getNameCriterion().equals(criterionName));
            assertTrue(found, "Nuestro criterio insertado debería estar en la lista");
        } catch (SQLException exception) {
            logger.error("Error: " + exception.getMessage());
            fail("No debería lanzar una SQLException: " + exception.getMessage());
        } catch (IOException exception) {
            logger.error("Error: " + exception.getMessage());
            fail("No debería lanzar una IOException: " + exception.getMessage());
        } catch (Exception exception) {
            logger.error("Error inesperado: " + exception.getMessage());
            fail("No debería lanzar una Exception inesperada: " + exception.getMessage());
        }
    }

    @Test
    void testDeleteAssessmentCriterion() {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String criterionId = String.valueOf(600000 + randomNumber);
            String criterionName = "Criterio para Eliminar " + randomNumber;

            AssessmentCriterionDTO assessmentCriterionToDelete = new AssessmentCriterionDTO(criterionId, criterionName);
            boolean wasInserted = assessmentCriterionDataAccessObject.insertAssessmentCriterion(assessmentCriterionToDelete);
            assertTrue(wasInserted, "La inserción previa debería ser exitosa");

            AssessmentCriterionDTO assessmentCriterionBeforeDeletion = assessmentCriterionDataAccessObject.searchAssessmentCriterionById(criterionId);
            assertEquals(criterionName, assessmentCriterionBeforeDeletion.getNameCriterion(), "El criterio debería existir antes de eliminar");

            boolean wasDeleted = assessmentCriterionDataAccessObject.deleteAssessmentCriterion(criterionId);
            assertTrue(wasDeleted, "La eliminación debería ser exitosa");

            AssessmentCriterionDTO assessmentCriterionAfterDeletion = assessmentCriterionDataAccessObject.searchAssessmentCriterionById(criterionId);
            assertEquals("N/A", assessmentCriterionAfterDeletion.getIdCriterion(),
                    "Después de eliminar, debería retornar N/A como ID para criterios no existentes");
            assertEquals("N/A", assessmentCriterionAfterDeletion.getNameCriterion(),
                    "Después de eliminar, debería retornar N/A como nombre para criterios no existentes");
        } catch (SQLException exception) {
            logger.error("Error: " + exception.getMessage());
            fail("No debería lanzar una SQLException: " + exception.getMessage());
        } catch (IOException exception) {
            logger.error("Error: " + exception.getMessage());
            fail("No debería lanzar una IOException: " + exception.getMessage());
        } catch (Exception exception) {
            logger.error("Error inesperado: " + exception.getMessage());
            fail("No debería lanzar una Exception inesperada: " + exception.getMessage());
        }
    }

    @Test
    void testDeleteNonExistentCriterion() {
        try {
            String nonExistentId = "777777";
            boolean wasDeleted = assessmentCriterionDataAccessObject.deleteAssessmentCriterion(nonExistentId);
            assertFalse(wasDeleted, "No debería eliminar un criterio inexistente");
        } catch (SQLException exception) {
            logger.error("Error: " + exception.getMessage());
            fail("No debería lanzar una SQLException: " + exception.getMessage());
        } catch (IOException exception) {
            logger.error("Error: " + exception.getMessage());
            fail("No debería lanzar una IOException: " + exception.getMessage());
        } catch (Exception exception) {
            logger.error("Error inesperado: " + exception.getMessage());
            fail("No debería lanzar una Exception inesperada: " + exception.getMessage());
        }
    }

    @Test
    void testGetInvalidIdCriterion() {
        try {
            AssessmentCriterionDTO retrievedAssessmentCriterion = assessmentCriterionDataAccessObject.searchAssessmentCriterionById("@");
            assertEquals("N/A", retrievedAssessmentCriterion.getIdCriterion(), "Debería retornar N/A para ID inválido");
            assertEquals("N/A", retrievedAssessmentCriterion.getNameCriterion(), "Debería retornar N/A para nombre de criterio inválido");
        } catch (SQLException exception) {
            assertTrue(exception.getMessage().contains("formato") || exception.getMessage().contains("inválido") ||
                    exception.getMessage().contains("number"), "Debería lanzar una SQLException relacionada con formato inválido");
        } catch (IOException exception) {
            logger.error("Error: " + exception.getMessage());
            fail("No debería lanzar una IOException: " + exception.getMessage());
        } catch (Exception exception) {
            logger.error("Error inesperado: " + exception.getMessage());
            fail("No debería lanzar una Exception inesperada: " + exception.getMessage());
        }
    }
}