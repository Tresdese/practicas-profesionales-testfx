package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DTO.EvaluationCriteriaDTO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EvaluationCriteriaDAOTest {

    private static ConecctionDataBase connectionDB;
    private static Connection connection;
    private EvaluationCriteriaDAO criteriaDAO;

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
        criteriaDAO = new EvaluationCriteriaDAO();
    }

    @AfterEach
    void tearDown() {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM evaluacion_criterio")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            fail("Error al limpiar los datos después de la prueba: " + e.getMessage());
        }
    }

    @Test
    void testInsertEvaluationCriteria() {
        try {
            EvaluationCriteriaDTO criteria = new EvaluationCriteriaDTO("10000", "1");
            boolean result = criteriaDAO.insertEvaluationCriteria(criteria, connection);
            assertTrue(result, "La inserción debería ser exitosa");

            EvaluationCriteriaDTO insertedCriteria = criteriaDAO.getEvaluationCriteria("1", "101", connection);
            assertNotNull(insertedCriteria, "El criterio debería existir en la base de datos");
            assertEquals("10000", insertedCriteria.getIdEvaluation(), "El ID de evaluación debería coincidir");
            assertEquals("1", insertedCriteria.getIdCriterion(), "El ID de criterio debería coincidir");
        } catch (SQLException e) {
            fail("Error en testInsertEvaluationCriteria: " + e.getMessage());
        }
    }

    @Test
    void testGetEvaluationCriteria() {
        try {
            EvaluationCriteriaDTO criteria = new EvaluationCriteriaDTO("11111", "3");
            criteriaDAO.insertEvaluationCriteria(criteria, connection);

            EvaluationCriteriaDTO retrievedCriteria = criteriaDAO.getEvaluationCriteria("11111", "3", connection);
            assertNotNull(retrievedCriteria, "El criterio debería existir en la base de datos");
            assertEquals("11111", retrievedCriteria.getIdEvaluation(), "El ID de evaluación debería coincidir");
            assertEquals("3", retrievedCriteria.getIdCriterion(), "El ID de criterio debería coincidir");
        } catch (SQLException e) {
            fail("Error en testGetEvaluationCriteria: " + e.getMessage());
        }
    }

    @Test
    void testDeleteEvaluationCriteria() {
        try {
            EvaluationCriteriaDTO criteria = new EvaluationCriteriaDTO("33333", "105");
            criteriaDAO.insertEvaluationCriteria(criteria, connection);

            boolean result = criteriaDAO.deleteEvaluationCriteria("4", "105", connection);
            assertTrue(result, "La eliminación debería ser exitosa");

            EvaluationCriteriaDTO deletedCriteria = criteriaDAO.getEvaluationCriteria("4", "105", connection);
            assertNull(deletedCriteria, "El criterio eliminado no debería existir en la base de datos");
        } catch (SQLException e) {
            fail("Error en testDeleteEvaluationCriteria: " + e.getMessage());
        }
    }

    @Test
    void testGetAllEvaluationCriteria() {
        try {
            EvaluationCriteriaDTO criteria1 = new EvaluationCriteriaDTO("5", "106");
            EvaluationCriteriaDTO criteria2 = new EvaluationCriteriaDTO("6", "107");
            criteriaDAO.insertEvaluationCriteria(criteria1, connection);
            criteriaDAO.insertEvaluationCriteria(criteria2, connection);

            List<EvaluationCriteriaDTO> criteriaList = criteriaDAO.getAllEvaluationCriteria(connection);
            assertNotNull(criteriaList, "La lista no debería ser nula");
            assertTrue(criteriaList.size() >= 2, "Debería haber al menos dos criterios en la lista");
        } catch (SQLException e) {
            fail("Error en testGetAllEvaluationCriteria: " + e.getMessage());
        }
    }
}