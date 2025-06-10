package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.EvaluationCriteriaDAO;
import logic.DTO.EvaluationCriteriaDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EvaluationCriteriaDAOTest {

    private static final Logger logger = LogManager.getLogger(EvaluationCriteriaDAOTest.class);

    private static ConnectionDataBase connectionDB;
    private static Connection connection;
    private EvaluationCriteriaDAO evaluationCriteriaDAO;

    @BeforeAll
    static void setUpClass() {
        connectionDB = new ConnectionDataBase();
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
        evaluationCriteriaDAO = new EvaluationCriteriaDAO();
        try {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT IGNORE INTO evaluacion_parcial (idEvaluacion) VALUES (?)")) {
                stmt.setString(1, "10000");
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT IGNORE INTO criterio_de_evaluacion (idCriterio) VALUES (?)")) {
                stmt.setString(1, "1");
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Error al preparar los datos iniciales: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM evaluacion_criterio")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error cleaning up: " + e.getMessage());
        }
    }

    @Test
    void testInsertEvaluationCriteria() {
        try {
            EvaluationCriteriaDTO criteria = new EvaluationCriteriaDTO("10000", "1");
            boolean result = evaluationCriteriaDAO.insertEvaluationCriteria(criteria, connection);
            assertTrue(result, "La inserción debe ser exitosa");
        } catch (SQLException e) {
            fail("No debe lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testInsertEvaluationCriteriaWithNullEvaluationId() {
        try {
            EvaluationCriteriaDTO criteria = new EvaluationCriteriaDTO(null, "1");
            assertThrows(SQLException.class, () -> {
                evaluationCriteriaDAO.insertEvaluationCriteria(criteria, connection);
            }, "Debe lanzar SQLException con idEvaluacion nulo");
        } catch (Exception e) {
            fail("No debe lanzar excepción fuera de SQLException: " + e.getMessage());
        }
    }

    @Test
    void testInsertEvaluationCriteriaWithNullCriterionId() {
        try {
            EvaluationCriteriaDTO criteria = new EvaluationCriteriaDTO("10000", null);
            assertThrows(SQLException.class, () -> {
                evaluationCriteriaDAO.insertEvaluationCriteria(criteria, connection);
            }, "Debe lanzar SQLException con idCriterio nulo");
        } catch (Exception e) {
            fail("No debe lanzar excepción fuera de SQLException: " + e.getMessage());
        }
    }

    @Test
    void testInsertEvaluationCriteriaWithEmptyIds() {
        EvaluationCriteriaDTO criteria = new EvaluationCriteriaDTO("", "");
        assertThrows(SQLException.class, () -> {
            evaluationCriteriaDAO.insertEvaluationCriteria(criteria, connection);
        }, "Debería lanzar SQLException al insertar con IDs vacíos");
    }

    @Test
    void testInsertDuplicateEvaluationCriteria() {
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM evaluacion_criterio")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error cleaning up: " + e.getMessage());
        }

        EvaluationCriteriaDTO criteria = new EvaluationCriteriaDTO("10000", "1");
        assertDoesNotThrow(() -> evaluationCriteriaDAO.insertEvaluationCriteria(criteria, connection));

        SQLException exception = assertThrows(SQLException.class, () -> {
            evaluationCriteriaDAO.insertEvaluationCriteria(criteria, connection);
        }, "Se esperaba una SQLException por duplicado");

        assertEquals("23000", exception.getSQLState(), "El SQLState debe indicar violación de restricción de unicidad");
    }

    @Test
    void testUpdateEvaluationCriteria() {
        try {
            EvaluationCriteriaDTO criteria = new EvaluationCriteriaDTO("10000", "1");
            evaluationCriteriaDAO.insertEvaluationCriteria(criteria, connection);

            EvaluationCriteriaDTO updatedCriteria = new EvaluationCriteriaDTO("10000", "1");
            boolean updated = evaluationCriteriaDAO.updateEvaluationCriteria(updatedCriteria, connection);
            assertTrue(updated, "La actualización debe ser exitosa");
        } catch (SQLException e) {
            fail("No debe lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testUpdateNonExistentEvaluationCriteria() {
        try {
            EvaluationCriteriaDTO criteria = new EvaluationCriteriaDTO("99999", "99999");
            boolean updated = evaluationCriteriaDAO.updateEvaluationCriteria(criteria, connection);
            assertFalse(updated, "No debe actualizar un registro inexistente");
        } catch (SQLException e) {
            fail("No debe lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testUpdateWithInvalidCriterionId() {
        try {
            EvaluationCriteriaDTO criteria = new EvaluationCriteriaDTO("10000", "1");
            evaluationCriteriaDAO.insertEvaluationCriteria(criteria, connection);

            EvaluationCriteriaDTO updatedCriteria = new EvaluationCriteriaDTO("10000", "99999");
            assertThrows(SQLException.class, () -> {
                evaluationCriteriaDAO.updateEvaluationCriteria(updatedCriteria, connection);
            }, "Debe lanzar SQLException con idCriterio inválido");
        } catch (SQLException e) {
            fail("No debe lanzar excepción fuera de SQLException: " + e.getMessage());
        }
    }

    @Test
    void testSearchEvaluationCriteriaById() {
        try {
            EvaluationCriteriaDTO criteria = new EvaluationCriteriaDTO("10000", "1");
            evaluationCriteriaDAO.insertEvaluationCriteria(criteria, connection);

            EvaluationCriteriaDTO found = evaluationCriteriaDAO.searchEvaluationCriteriaById("10000", "1", connection);
            assertNotNull(found, "Debe encontrar el registro");
            assertEquals("10000", found.getIdEvaluation());
            assertEquals("1", found.getIdCriterion());
        } catch (SQLException e) {
            fail("No debe lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testSearchNonExistentEvaluationCriteria() {
        try {
            EvaluationCriteriaDTO found = evaluationCriteriaDAO.searchEvaluationCriteriaById("99999", "99999", connection);
            assertNotNull(found, "Debe devolver un objeto");
            assertEquals("N/A", found.getIdEvaluation());
            assertEquals("N/A", found.getIdCriterion());
        } catch (SQLException e) {
            fail("No debe lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testSearchWithNullIds() {
        try {
            EvaluationCriteriaDTO found = evaluationCriteriaDAO.searchEvaluationCriteriaById(null, null, connection);
            assertNotNull(found, "Debe devolver un objeto");
            assertEquals("N/A", found.getIdEvaluation());
            assertEquals("N/A", found.getIdCriterion());
        } catch (SQLException e) {
            fail("No debe lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testSearchWithInvalidIds() {
        try {
            EvaluationCriteriaDTO found = evaluationCriteriaDAO.searchEvaluationCriteriaById("@", "@", connection);
            assertNotNull(found, "Debe devolver un objeto");
            assertEquals("N/A", found.getIdEvaluation());
            assertEquals("N/A", found.getIdCriterion());
        } catch (SQLException e) {
            fail("No debe lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testGetAllEvaluationCriteria() {
        try {
            EvaluationCriteriaDTO criteria1 = new EvaluationCriteriaDTO("10000", "1");
            evaluationCriteriaDAO.insertEvaluationCriteria(criteria1, connection);

            List<EvaluationCriteriaDTO> all = evaluationCriteriaDAO.getAllEvaluationCriteria(connection);
            assertNotNull(all, "La lista no debe ser nula");
            assertFalse(all.isEmpty(), "La lista no debe estar vacía");
        } catch (SQLException e) {
            fail("No debe lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testGetAllEvaluationCriteriaEmpty() {
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM evaluacion_criterio")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error cleaning up: " + e.getMessage());
        }
        try {
            List<EvaluationCriteriaDTO> all = evaluationCriteriaDAO.getAllEvaluationCriteria(connection);
            assertNotNull(all, "La lista no debe ser nula");
            assertTrue(all.isEmpty(), "La lista debe estar vacía");
        } catch (SQLException e) {
            fail("No debe lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testDeleteEvaluationCriteria() {
        try {
            EvaluationCriteriaDTO criteria = new EvaluationCriteriaDTO("10000", "1");
            evaluationCriteriaDAO.insertEvaluationCriteria(criteria, connection);

            boolean deleted = evaluationCriteriaDAO.deleteEvaluationCriteria("10000", "1", connection);
            assertTrue(deleted, "La eliminación debe ser exitosa");

            EvaluationCriteriaDTO found = evaluationCriteriaDAO.searchEvaluationCriteriaById("10000", "1", connection);
            assertEquals("N/A", found.getIdEvaluation());
            assertEquals("N/A", found.getIdCriterion());
        } catch (SQLException e) {
            fail("No debe lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testDeleteNonExistentEvaluationCriteria() {
        try {
            boolean deleted = evaluationCriteriaDAO.deleteEvaluationCriteria("99999", "99999", connection);
            assertFalse(deleted, "No debe eliminar un registro inexistente");
        } catch (SQLException e) {
            fail("No debe lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testDeleteWithNullIds() {
        try {
            boolean result = evaluationCriteriaDAO.deleteEvaluationCriteria(null, null, connection);
            assertFalse(result, "No debería eliminar nada con IDs nulos");
        } catch (SQLException e) {
            fail("No debería lanzar excepción al eliminar con IDs nulos: " + e.getMessage());
        }
    }
}