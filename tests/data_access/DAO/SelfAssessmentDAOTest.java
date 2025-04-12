package data_access.DAO;

import data_access.ConecctionDataBase;

import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

import logic.DAO.SelfAssessmentDAO;
import logic.DTO.SelfAssessmentDTO;

class SelfAssessmentDAOTest {

    private static ConecctionDataBase connectionDB;
    private static Connection connection;
    private SelfAssessmentDAO selfAssessmentDAO;

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
        selfAssessmentDAO = new SelfAssessmentDAO();
    }

    private String insertTestSelfAssessment(String id, String comments, double grade, String registration, String evidenceId) throws SQLException {
        SelfAssessmentDTO existingSelfAssessment = selfAssessmentDAO.getSelfAssessment(id, connection);
        if (existingSelfAssessment != null) {
            return id;
        }

        String sql = "INSERT INTO autoevaluacion (idAutoevaluacion, comentarios, calificacion, matricula, idEvidencia) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, comments);
            stmt.setDouble(3, grade);
            stmt.setString(4, registration);
            stmt.setString(5, evidenceId);
            stmt.executeUpdate();
            return id;
        }
    }

    @Test
    void testInsertSelfAssessment() {
        try {
            SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO("1", "Buen trabajo", 9.5, "11113", "1");
            boolean result = selfAssessmentDAO.insertSelfAssessment(selfAssessment, connection);
            assertTrue(result, "La inserción debería ser exitosa");

            SelfAssessmentDTO insertedSelfAssessment = selfAssessmentDAO.getSelfAssessment("1", connection);
            assertNotNull(insertedSelfAssessment, "La autoevaluación debería existir en la base de datos");
            assertEquals("Buen trabajo", insertedSelfAssessment.getComments(), "Los comentarios deberían coincidir");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    void testGetSelfAssessment() {
        try {
            String id = insertTestSelfAssessment("2", "Excelente", 10.0, "54331", "2");

            SelfAssessmentDTO retrievedSelfAssessment = selfAssessmentDAO.getSelfAssessment(id, connection);
            assertNotNull(retrievedSelfAssessment, "Debería encontrar la autoevaluación");
            assertEquals(id, retrievedSelfAssessment.getSelfAssessmentId(), "El ID debería coincidir");
            assertEquals("Excelente", retrievedSelfAssessment.getComments(), "Los comentarios deberían coincidir");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    void testUpdateSelfAssessment() {
        try {
            String id = insertTestSelfAssessment("3", "Regular", 7.0, "67892", "4");

            SelfAssessmentDTO updatedSelfAssessment = new SelfAssessmentDTO(id, "Actualizado", 8.5, "67892", "4");
            boolean updateResult = selfAssessmentDAO.updateSelfAssessment(updatedSelfAssessment, connection);
            assertTrue(updateResult, "La actualización debería ser exitosa");

            SelfAssessmentDTO retrievedSelfAssessment = selfAssessmentDAO.getSelfAssessment(id, connection);
            assertNotNull(retrievedSelfAssessment, "La autoevaluación debería existir");
            assertEquals("Actualizado", retrievedSelfAssessment.getComments(), "Los comentarios deberían actualizarse");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    void testGetAllSelfAssessments() {
        try {
            insertTestSelfAssessment("4", "Prueba", 6.0, "11113", "5");

            List<SelfAssessmentDTO> selfAssessments = selfAssessmentDAO.getAllSelfAssessments(connection);
            assertNotNull(selfAssessments, "La lista no debería ser nula");
            assertFalse(selfAssessments.isEmpty(), "La lista no debería estar vacía");

            boolean found = selfAssessments.stream()
                    .anyMatch(s -> s.getSelfAssessmentId().equals("4"));
            assertTrue(found, "Nuestra autoevaluación de prueba debería estar en la lista");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    void testDeleteSelfAssessment() {
        try {
            String id = insertTestSelfAssessment("5", "Eliminar", 5.0, "12351", "6");

            SelfAssessmentDTO before = selfAssessmentDAO.getSelfAssessment(id, connection);
            assertNotNull(before, "La autoevaluación debería existir antes de eliminarla");

            boolean deleted = selfAssessmentDAO.deleteSelfAssessment(before, connection);
            assertTrue(deleted, "La eliminación debería ser exitosa");

            SelfAssessmentDTO after = selfAssessmentDAO.getSelfAssessment(id, connection);
            assertNull(after, "La autoevaluación no debería existir después de eliminarla");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }
}