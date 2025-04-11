package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DTO.AssessmentCriterionDTO;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AssessmentCriterionDAOTest {

    private static ConecctionDataBase connectionDB;
    private static Connection connection;
    private AssessmentCriterionDAO assessmentCriterionDAO;

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
        assessmentCriterionDAO = new AssessmentCriterionDAO();
    }

    private String insertTestCriterion(String nombre, double calificacion) throws SQLException {
        String sql = "INSERT INTO criterio_de_evaluacion (nombreCriterio, calificacion) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nombre);
            stmt.setDouble(2, calificacion);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getString(1);
                } else {
                    throw new SQLException("No se generó ID para el criterio de evaluación");
                }
            }
        }
    }

    @Test
    void testInsertAssessmentCriterion() {
        try {
            String nombreCriterio = "Criterio de Prueba";
            double calificacion = 85.5;

            AssessmentCriterionDTO criterion = new AssessmentCriterionDTO("0", nombreCriterio, calificacion);
            boolean result = assessmentCriterionDAO.insertAssessmentCriterion(criterion, connection);
            assertTrue(result, "La inserción debería ser exitosa");

            List<AssessmentCriterionDTO> criteria = assessmentCriterionDAO.getAllAssessmentCriteria(connection);
            AssessmentCriterionDTO insertedCriterion = criteria.stream()
                    .filter(c -> c.getNameCriterion().equals(nombreCriterio))
                    .findFirst()
                    .orElse(null);

            assertNotNull(insertedCriterion, "El criterio debería existir en la base de datos");
            assertEquals(nombreCriterio, insertedCriterion.getNameCriterion(), "El nombre debería coincidir");
            assertEquals(calificacion, insertedCriterion.getGrade(), "La calificación debería coincidir");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    void testGetAssessmentCriterion() {
        try {
            String idCriterio = insertTestCriterion("Criterio para Consulta", 90.0);

            AssessmentCriterionDTO retrievedCriterion = assessmentCriterionDAO.getAssessmentCriterion(idCriterio, connection);
            assertNotNull(retrievedCriterion, "Debería encontrar el criterio");
            assertEquals(idCriterio, retrievedCriterion.getIdCriterion(), "El ID debería coincidir");
            assertEquals("Criterio para Consulta", retrievedCriterion.getNameCriterion(), "El nombre debería coincidir");
            assertEquals(90.0, retrievedCriterion.getGrade(), "La calificación debería coincidir");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    void testUpdateAssessmentCriterion() {
        try {
            String idCriterio = insertTestCriterion("Criterio Original", 75.0);

            AssessmentCriterionDTO criterion = new AssessmentCriterionDTO(idCriterio, "Criterio Actualizado", 95.0);
            boolean updateResult = assessmentCriterionDAO.updateAssessmentCriterion(criterion, connection);
            assertTrue(updateResult, "La actualización debería ser exitosa");

            AssessmentCriterionDTO updated = assessmentCriterionDAO.getAssessmentCriterion(idCriterio, connection);
            assertNotNull(updated, "El criterio debería existir");
            assertEquals("Criterio Actualizado", updated.getNameCriterion(), "El nombre debería actualizarse");
            assertEquals(95.0, updated.getGrade(), "La calificación debería actualizarse");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    void testGetAllAssessmentCriteria() {
        try {
            String idCriterio = insertTestCriterion("Criterio para Listar", 80.0);

            List<AssessmentCriterionDTO> criteria = assessmentCriterionDAO.getAllAssessmentCriteria(connection);
            assertNotNull(criteria, "La lista no debería ser nula");
            assertFalse(criteria.isEmpty(), "La lista no debería estar vacía");

            boolean found = criteria.stream()
                    .anyMatch(c -> c.getIdCriterion().equals(idCriterio));
            assertTrue(found, "Nuestro criterio de prueba debería estar en la lista");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    void testDeleteAssessmentCriterion() {
        try {
            String idCriterio = insertTestCriterion("Criterio para Eliminar", 70.0);

            AssessmentCriterionDTO before = assessmentCriterionDAO.getAssessmentCriterion(idCriterio, connection);
            assertNotNull(before, "El criterio debería existir antes de eliminarlo");

            boolean deleted = assessmentCriterionDAO.deleteAssessmentCriterion(idCriterio, connection);
            assertTrue(deleted, "La eliminación debería ser exitosa");

            AssessmentCriterionDTO after = assessmentCriterionDAO.getAssessmentCriterion(idCriterio, connection);
            assertNull(after, "El criterio no debería existir después de eliminarlo");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }
}