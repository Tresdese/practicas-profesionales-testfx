package data_access.DAO;

import data_access.ConecctionDataBase;
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
        connectionDB.close();
    }

    @BeforeEach
    void setUp() {
        criteriaDAO = new EvaluationCriteriaDAO();
        try {
            // Asegurarse de que existen los registros previos necesarios
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
        try {
            // Limpiar los datos de prueba
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM evaluacion_criterio")) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error("Error al limpiar los datos después de la prueba: " + e.getMessage());
        }
    }

    @Test
    void testInsertEvaluationCriteria() {
        try {
            EvaluationCriteriaDTO criteria = new EvaluationCriteriaDTO("10000", "1");
            boolean result = criteriaDAO.insertEvaluationCriteria(criteria, connection);
            assertTrue(result, "La inserción debería ser exitosa");

            EvaluationCriteriaDTO insertedCriteria = criteriaDAO.searchEvaluationCriteriaById("10000", "1", connection);
            assertEquals("10000", insertedCriteria.getIdEvaluation(), "El ID de evaluación debería coincidir");
            assertEquals("1", insertedCriteria.getIdCriterion(), "El ID de criterio debería coincidir");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testInsertEvaluationCriteriaWithNullEvaluationId() {
        try {
            EvaluationCriteriaDTO criteria = new EvaluationCriteriaDTO(null, "1");
            criteriaDAO.insertEvaluationCriteria(criteria, connection);
            fail("Debería lanzar excepción con ID de evaluación nulo");
        } catch (SQLException | NullPointerException e) {
            assertTrue(true, "Se esperaba una excepción al insertar con ID de evaluación nulo");
        }
    }

    @Test
    void testInsertEvaluationCriteriaWithNullCriterionId() {
        try {
            EvaluationCriteriaDTO criteria = new EvaluationCriteriaDTO("10000", null);
            criteriaDAO.insertEvaluationCriteria(criteria, connection);
            fail("Debería lanzar excepción con ID de criterio nulo");
        } catch (SQLException | NullPointerException e) {
            assertTrue(true, "Se esperaba una excepción al insertar con ID de criterio nulo");
        }
    }

    @Test
    void testInsertEvaluationCriteriaWithEmptyIds() {
        try {
            EvaluationCriteriaDTO criteria = new EvaluationCriteriaDTO("", "");
            boolean result = criteriaDAO.insertEvaluationCriteria(criteria, connection);
            assertFalse(result, "No debería permitir insertar con IDs vacíos");
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("constraint") || e.getMessage().contains("foreign key"),
                    "La excepción debería estar relacionada con restricciones de clave foránea");
        }
    }

    @Test
    void testInsertDuplicateEvaluationCriteria() {
        try {
            EvaluationCriteriaDTO criteria = new EvaluationCriteriaDTO("10000", "1");
            boolean firstResult = criteriaDAO.insertEvaluationCriteria(criteria, connection);
            assertTrue(firstResult, "La primera inserción debería ser exitosa");

            try {
                boolean secondResult = criteriaDAO.insertEvaluationCriteria(criteria, connection);
                assertFalse(secondResult, "No debería permitir duplicados");
            } catch (SQLException e) {
                assertTrue(e.getMessage().contains("duplicate") || e.getMessage().contains("unique"),
                        "La excepción debería estar relacionada con entradas duplicadas");
            }
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería fallar la primera inserción: " + e.getMessage());
        }
    }

    @Test
    void testUpdateEvaluationCriteria() {
        try {
            // Crear entrada para actualizar
            EvaluationCriteriaDTO originalCriteria = new EvaluationCriteriaDTO("10000", "1");
            criteriaDAO.insertEvaluationCriteria(originalCriteria, connection);

            // Preparar otro criterio en la base de datos para la actualización
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT IGNORE INTO criterio_de_evaluacion (idCriterio) VALUES (?)")) {
                stmt.setString(1, "2");
                stmt.executeUpdate();
            }

            // Actualizar al nuevo criterio
            EvaluationCriteriaDTO updatedCriteria = new EvaluationCriteriaDTO("10000", "2");
            boolean updateResult = criteriaDAO.updateEvaluationCriteria(updatedCriteria, connection);
            assertTrue(updateResult, "La actualización debería ser exitosa");

            // Verificar actualización
            EvaluationCriteriaDTO retrievedCriteria = criteriaDAO.searchEvaluationCriteriaById("10000", "2", connection);
            assertEquals("10000", retrievedCriteria.getIdEvaluation(), "El ID de evaluación debería mantenerse");
            assertEquals("2", retrievedCriteria.getIdCriterion(), "El ID de criterio debería haberse actualizado a 2");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testUpdateNonExistentEvaluationCriteria() {
        try {
            // Intentar actualizar algo que no existe
            EvaluationCriteriaDTO nonExistentCriteria = new EvaluationCriteriaDTO("99999", "1");
            boolean updateResult = criteriaDAO.updateEvaluationCriteria(nonExistentCriteria, connection);
            assertFalse(updateResult, "No debería actualizar un registro inexistente");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción al intentar actualizar un registro inexistente");
        }
    }

    @Test
    void testUpdateWithInvalidCriterionId() {
        try {
            // Insertar registro válido primero
            EvaluationCriteriaDTO originalCriteria = new EvaluationCriteriaDTO("10000", "1");
            criteriaDAO.insertEvaluationCriteria(originalCriteria, connection);

            // Intentar actualizar con un ID de criterio no existente
            EvaluationCriteriaDTO invalidCriteria = new EvaluationCriteriaDTO("10000", "999");
            boolean updateResult = criteriaDAO.updateEvaluationCriteria(invalidCriteria, connection);
            assertFalse(updateResult, "No debería actualizar con un ID de criterio inválido");
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("constraint") || e.getMessage().contains("foreign key"),
                    "La excepción debería estar relacionada con restricciones de clave foránea");
        }
    }

    @Test
    void testSearchEvaluationCriteriaById() {
        try {
            // Insertar primero un registro
            EvaluationCriteriaDTO criteria = new EvaluationCriteriaDTO("10000", "1");
            criteriaDAO.insertEvaluationCriteria(criteria, connection);

            // Buscar el registro insertado
            EvaluationCriteriaDTO retrievedCriteria = criteriaDAO.searchEvaluationCriteriaById("10000", "1", connection);
            assertEquals("10000", retrievedCriteria.getIdEvaluation(), "El ID de evaluación debería coincidir");
            assertEquals("1", retrievedCriteria.getIdCriterion(), "El ID de criterio debería coincidir");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testSearchNonExistentEvaluationCriteria() {
        try {
            // Buscar un registro que no existe
            EvaluationCriteriaDTO retrievedCriteria = criteriaDAO.searchEvaluationCriteriaById("99999", "99999", connection);
            assertEquals("N/A", retrievedCriteria.getIdEvaluation(), "Debería devolver N/A para registros inexistentes");
            assertEquals("N/A", retrievedCriteria.getIdCriterion(), "Debería devolver N/A para registros inexistentes");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción al buscar un registro inexistente");
        }
    }

    @Test
    void testSearchWithNullIds() {
        try {
            criteriaDAO.searchEvaluationCriteriaById(null, null, connection);
            fail("Debería lanzar excepción con IDs nulos");
        } catch (SQLException | NullPointerException e) {
            assertTrue(true, "Se esperaba una excepción al buscar con IDs nulos");
        }
    }

    @Test
    void testSearchWithInvalidIds() {
        try {
            EvaluationCriteriaDTO retrievedCriteria = criteriaDAO.searchEvaluationCriteriaById("@", "#", connection);
            assertEquals("N/A", retrievedCriteria.getIdEvaluation(), "Debería devolver N/A para IDs inválidos");
            assertEquals("N/A", retrievedCriteria.getIdCriterion(), "Debería devolver N/A para IDs inválidos");
        } catch (SQLException e) {
            // También es aceptable que lance excepción si valida el formato de los IDs
            assertTrue(e.getMessage().contains("formato") || e.getMessage().contains("inválido"),
                    "La excepción debería estar relacionada con formato inválido de IDs");
        }
    }

    @Test
    void testGetAllEvaluationCriteria() {
        try {
            // Insertar algunos registros de prueba
            EvaluationCriteriaDTO criteria1 = new EvaluationCriteriaDTO("10000", "1");
            criteriaDAO.insertEvaluationCriteria(criteria1, connection);

            // Preparar otro criterio en la base de datos
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT IGNORE INTO criterio_de_evaluacion (idCriterio) VALUES (?)")) {
                stmt.setString(1, "2");
                stmt.executeUpdate();
            }

            // Insertar otra relación
            EvaluationCriteriaDTO criteria2 = new EvaluationCriteriaDTO("10000", "2");
            criteriaDAO.insertEvaluationCriteria(criteria2, connection);

            // Obtener todos los registros
            List<EvaluationCriteriaDTO> criteriaList = criteriaDAO.getAllEvaluationCriteria(connection);
            assertNotNull(criteriaList, "La lista no debería ser nula");
            assertTrue(criteriaList.size() >= 2, "Debería haber al menos dos criterios en la lista");

            // Verificar que están nuestros registros de prueba
            boolean foundCriteria1 = criteriaList.stream()
                    .anyMatch(c -> c.getIdEvaluation().equals("10000") && c.getIdCriterion().equals("1"));
            boolean foundCriteria2 = criteriaList.stream()
                    .anyMatch(c -> c.getIdEvaluation().equals("10000") && c.getIdCriterion().equals("2"));

            assertTrue(foundCriteria1, "Debería encontrarse el primer criterio de prueba");
            assertTrue(foundCriteria2, "Debería encontrarse el segundo criterio de prueba");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testGetAllEvaluationCriteriaEmpty() {
        try {
            // Asegurarse de que la tabla está vacía
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM evaluacion_criterio")) {
                statement.executeUpdate();
            }

            // Obtener todos los registros
            List<EvaluationCriteriaDTO> criteriaList = criteriaDAO.getAllEvaluationCriteria(connection);
            assertNotNull(criteriaList, "La lista no debería ser nula aunque esté vacía");
            assertTrue(criteriaList.isEmpty(), "La lista debería estar vacía");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testDeleteEvaluationCriteria() {
        try {
            // Insertar un registro para luego eliminarlo
            EvaluationCriteriaDTO criteria = new EvaluationCriteriaDTO("10000", "1");
            criteriaDAO.insertEvaluationCriteria(criteria, connection);

            // Verificar que existe
            EvaluationCriteriaDTO beforeDelete = criteriaDAO.searchEvaluationCriteriaById("10000", "1", connection);
            assertEquals("10000", beforeDelete.getIdEvaluation(), "El criterio debería existir antes de eliminarlo");

            // Eliminar el registro
            boolean result = criteriaDAO.deleteEvaluationCriteria("10000", "1", connection);
            assertTrue(result, "La eliminación debería ser exitosa");

            // Verificar que ya no existe
            EvaluationCriteriaDTO afterDelete = criteriaDAO.searchEvaluationCriteriaById("10000", "1", connection);
            assertEquals("N/A", afterDelete.getIdEvaluation(),
                    "Debería devolver N/A después de eliminar el registro");
            assertEquals("N/A", afterDelete.getIdCriterion(),
                    "Debería devolver N/A después de eliminar el registro");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    void testDeleteNonExistentEvaluationCriteria() {
        try {
            // Intentar eliminar un registro que no existe
            boolean result = criteriaDAO.deleteEvaluationCriteria("99999", "99999", connection);
            assertFalse(result, "No debería eliminar un registro inexistente");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción al intentar eliminar un registro inexistente");
        }
    }

    @Test
    void testDeleteWithNullIds() {
        try {
            criteriaDAO.deleteEvaluationCriteria(null, null, connection);
            fail("Debería lanzar excepción con IDs nulos");
        } catch (SQLException | NullPointerException e) {
            assertTrue(true, "Se esperaba una excepción al eliminar con IDs nulos");
        }
    }
}