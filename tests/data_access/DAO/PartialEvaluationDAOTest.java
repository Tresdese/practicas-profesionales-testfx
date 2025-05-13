package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.PartialEvaluationDAO;
import logic.DTO.PartialEvaluationDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PartialEvaluationDAOTest {
    private Connection connection;
    private PartialEvaluationDAO partialEvaluationDAO;

    @BeforeEach
    void setUp() throws SQLException {
        // Configura la conexión a la base de datos real
        ConecctionDataBase conecctionDataBase = new ConecctionDataBase();
        connection = conecctionDataBase.connectDB();
        partialEvaluationDAO = new PartialEvaluationDAO();

        // Limpia la tabla antes de cada prueba
        connection.createStatement().execute("DELETE FROM evaluacion_parcial");
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Limpia la tabla después de cada prueba
        connection.createStatement().execute("DELETE FROM evaluacion_parcial");
        connection.close();
    }

    @Test
    void insertPartialEvaluationSuccessfully() throws SQLException {
        PartialEvaluationDTO evaluation = new PartialEvaluationDTO("1", 85.5, "12345", "Evidencia1");

        boolean result = partialEvaluationDAO.insertPartialEvaluation(evaluation, connection);

        assertTrue(result, "La evaluación parcial debería insertarse correctamente");
    }

    @Test
    void updatePartialEvaluationSuccessfully() throws SQLException {
        PartialEvaluationDTO evaluation = new PartialEvaluationDTO("1", 85.5, "12345", "Evidencia1");
        partialEvaluationDAO.insertPartialEvaluation(evaluation, connection);

        PartialEvaluationDTO updatedEvaluation = new PartialEvaluationDTO("1", 90.0, "54321", "Evidencia2");
        boolean result = partialEvaluationDAO.updatePartialEvaluation(updatedEvaluation, connection);

        assertTrue(result, "La evaluación parcial debería actualizarse correctamente");
    }

    @Test
    void updatePartialEvaluationFailsWhenNotExists() throws SQLException {
        PartialEvaluationDTO evaluation = new PartialEvaluationDTO("999", 90.0, "54321", "Evidencia2");

        boolean result = partialEvaluationDAO.updatePartialEvaluation(evaluation, connection);

        assertFalse(result, "No debería permitir actualizar una evaluación parcial inexistente");
    }

    @Test
    void deletePartialEvaluationSuccessfully() throws SQLException {
        PartialEvaluationDTO evaluation = new PartialEvaluationDTO("1", 85.5, "12345", "Evidencia1");
        partialEvaluationDAO.insertPartialEvaluation(evaluation, connection);

        boolean result = partialEvaluationDAO.deletePartialEvaluation("1", connection);

        assertTrue(result, "La evaluación parcial debería eliminarse correctamente");
    }

    @Test
    void deletePartialEvaluationFailsWhenNotExists() throws SQLException {
        boolean result = partialEvaluationDAO.deletePartialEvaluation("999", connection);

        assertFalse(result, "No debería permitir eliminar una evaluación parcial inexistente");
    }

    @Test
    void searchPartialEvaluationByIdWhenExists() throws SQLException {
        PartialEvaluationDTO evaluation = new PartialEvaluationDTO("1", 85.5, "12345", "Evidencia1");
        partialEvaluationDAO.insertPartialEvaluation(evaluation, connection);

        PartialEvaluationDTO result = partialEvaluationDAO.searchPartialEvaluationById("1", connection);

        assertNotNull(result, "La evaluación parcial no debería ser nula");
        assertEquals("1", result.getIdEvaluation());
        assertEquals(85.5, result.getAverage());
        assertEquals("12345", result.getTuiton());
        assertEquals("Evidencia1", result.getEvidence());
    }

    @Test
    void searchPartialEvaluationByIdWhenNotExists() throws SQLException {
        PartialEvaluationDTO result = partialEvaluationDAO.searchPartialEvaluationById("999", connection);

        assertNotNull(result, "La evaluación parcial no debería ser nula");
        assertEquals("N/A", result.getIdEvaluation());
        assertEquals(-1, result.getAverage());
        assertEquals("N/A", result.getTuiton());
        assertEquals("N/A", result.getEvidence());
    }

    @Test
    void getAllPartialEvaluationsReturnsList() throws SQLException {
        PartialEvaluationDTO eval1 = new PartialEvaluationDTO("1", 85.5, "12345", "Evidencia1");
        PartialEvaluationDTO eval2 = new PartialEvaluationDTO("2", 90.0, "54321", "Evidencia2");
        partialEvaluationDAO.insertPartialEvaluation(eval1, connection);
        partialEvaluationDAO.insertPartialEvaluation(eval2, connection);

        List<PartialEvaluationDTO> result = partialEvaluationDAO.getAllPartialEvaluations(connection);

        assertNotNull(result, "La lista de evaluaciones no debería ser nula");
        assertEquals(2, result.size(), "Debería haber 2 evaluaciones en la lista");
    }

    @Test
    void getAllPartialEvaluationsReturnsEmptyListWhenNoEvaluationsExist() throws SQLException {
        List<PartialEvaluationDTO> result = partialEvaluationDAO.getAllPartialEvaluations(connection);

        assertNotNull(result, "La lista de evaluaciones no debería ser nula");
        assertTrue(result.isEmpty(), "La lista de evaluaciones debería estar vacía");
    }
}