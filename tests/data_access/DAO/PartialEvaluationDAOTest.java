package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.PartialEvaluationDAO;
import logic.DTO.PartialEvaluationDTO;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PartialEvaluationDAOTest {

    private static ConecctionDataBase connectionDB;
    private static Connection connection;
    private PartialEvaluationDAO partialEvaluationDAO;

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
        partialEvaluationDAO = new PartialEvaluationDAO();
    }

    private String insertTestPartialEvaluation(String idEvaluation, double average, String tuiton, String evidence) throws SQLException {
        PartialEvaluationDTO existingEvaluation = partialEvaluationDAO.searchPartialEvaluationById(idEvaluation, connection);
        if (existingEvaluation != null) {
            return idEvaluation;
        }

        String checkTuitonSql = "SELECT matricula FROM estudiante WHERE matricula = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkTuitonSql)) {
            checkStmt.setString(1, tuiton);
            if (!checkStmt.executeQuery().next()) {
                fail("La matrícula proporcionada no existe en la tabla estudiante: " + tuiton);
            }
        }
    
        String sql = "INSERT INTO evaluacion_parcial (idEvaluacion, promedio, matricula, IdEvidencia) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idEvaluation);
            stmt.setDouble(2, average);
            stmt.setString(3, tuiton);
            stmt.setString(4, evidence);
            stmt.executeUpdate();
            return idEvaluation;
        }
    }

    @Test
    void testInsertPartialEvaluation() {
        try {
            PartialEvaluationDTO evaluation = new PartialEvaluationDTO("10000", 85.5, "11113", "1");
            boolean result = partialEvaluationDAO.insertPartialEvaluation(evaluation, connection);
            assertTrue(result, "La inserción debería ser exitosa");

            PartialEvaluationDTO insertedEvaluation = partialEvaluationDAO.searchPartialEvaluationById("10000", connection);
            assertNotNull(insertedEvaluation, "La evaluación debería existir en la base de datos");
            assertEquals(85.5, insertedEvaluation.getAverage(), "El promedio debería coincidir");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    void testSearchPartialEvaluationById() {
        try {
            String idEvaluation = insertTestPartialEvaluation("11111", 90.0, "54331", "2");

            PartialEvaluationDTO retrievedEvaluation = partialEvaluationDAO.searchPartialEvaluationById(idEvaluation, connection);
            assertNotNull(retrievedEvaluation, "Debería encontrar la evaluación");
            assertEquals(idEvaluation, retrievedEvaluation.getIdEvaluation(), "El ID de la evaluación debería coincidir");
            assertEquals(90.0, retrievedEvaluation.getAverage(), "El promedio debería coincidir");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    void testUpdatePartialEvaluation() {
        try {
            String idEvaluation = insertTestPartialEvaluation("33333", 75.0, "67892", "4");

            PartialEvaluationDTO updatedEvaluation = new PartialEvaluationDTO(idEvaluation, 95.0, "67892", "4");
            boolean updateResult = partialEvaluationDAO.updatePartialEvaluation(updatedEvaluation, connection);
            assertTrue(updateResult, "La actualización debería ser exitosa");

            PartialEvaluationDTO retrievedEvaluation = partialEvaluationDAO.searchPartialEvaluationById(idEvaluation, connection);
            assertNotNull(retrievedEvaluation, "La evaluación debería existir");
            assertEquals(95.0, retrievedEvaluation.getAverage(), "El promedio debería actualizarse");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    void testGetAllPartialEvaluations() {
        try {
            insertTestPartialEvaluation("44444", 88.0, "11113", "6");

            List<PartialEvaluationDTO> evaluations = partialEvaluationDAO.getAllPartialEvaluations(connection);
            assertNotNull(evaluations, "La lista no debería ser nula");
            assertFalse(evaluations.isEmpty(), "La lista no debería estar vacía");

            boolean found = evaluations.stream()
                    .anyMatch(e -> e.getIdEvaluation().equals("44444"));
            assertTrue(found, "Nuestra evaluación de prueba debería estar en la lista");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    void testDeletePartialEvaluation() {
        try {
            String idEvaluation = insertTestPartialEvaluation("55555", 70.0, "12351", "7");

            PartialEvaluationDTO before = partialEvaluationDAO.searchPartialEvaluationById(idEvaluation, connection);
            assertNotNull(before, "La evaluación debería existir antes de eliminarla");

            boolean deleted = partialEvaluationDAO.deletePartialEvaluation(idEvaluation, connection);
            assertTrue(deleted, "La eliminación debería ser exitosa");

            PartialEvaluationDTO after = partialEvaluationDAO.searchPartialEvaluationById(idEvaluation, connection);
            assertNull(after, "La evaluación no debería existir después de eliminarla");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }
}