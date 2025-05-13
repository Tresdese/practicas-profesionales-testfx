package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.SelfAssessmentDAO;
import logic.DTO.SelfAssessmentDTO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        connectionDB.close();
    }

    @BeforeEach
    void setUp() {
        selfAssessmentDAO = new SelfAssessmentDAO();
        try {
            connection.prepareStatement("DELETE FROM autoevaluacion").executeUpdate();
        } catch (SQLException e) {
            fail("Error al limpiar la tabla autoevaluacion: " + e.getMessage());
        }
    }

    @Test
    void testInsertSelfAssessment() {
        try {
            SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                    "1", "Comentarios de prueba", 85.0, "12345", "1"
            );

            boolean result = selfAssessmentDAO.insertSelfAssessment(selfAssessment, connection);
            assertTrue(result, "La inserción debería ser exitosa");

            SelfAssessmentDTO insertedSelfAssessment = selfAssessmentDAO.searchSelfAssessmentById("1", connection);
            assertNotNull(insertedSelfAssessment, "La autoevaluación debería existir en la base de datos");
            assertEquals("Comentarios de prueba", insertedSelfAssessment.getComments(), "Los comentarios deberían coincidir");
        } catch (SQLException e) {
            fail("Error en testInsertSelfAssessment: " + e.getMessage());
        }
    }

    @Test
    void testSearchSelfAssessmentById() {
        try {
            SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                    "2", "Comentarios de consulta", 90.0, "54321", "2"
            );

            selfAssessmentDAO.insertSelfAssessment(selfAssessment, connection);

            SelfAssessmentDTO retrievedSelfAssessment = selfAssessmentDAO.searchSelfAssessmentById("2", connection);
            assertNotNull(retrievedSelfAssessment, "La autoevaluación debería existir en la base de datos");
            assertEquals("Comentarios de consulta", retrievedSelfAssessment.getComments(), "Los comentarios deberían coincidir");
        } catch (SQLException e) {
            fail("Error en testSearchSelfAssessmentById: " + e.getMessage());
        }
    }

    @Test
    void testUpdateSelfAssessment() {
        try {
            SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                    "3", "Comentarios originales", 75.0, "67890", "3"
            );

            selfAssessmentDAO.insertSelfAssessment(selfAssessment, connection);

            SelfAssessmentDTO updatedSelfAssessment = new SelfAssessmentDTO(
                    "3", "Comentarios actualizados", 95.0, "67890", "3"
            );

            boolean result = selfAssessmentDAO.updateSelfAssessment(updatedSelfAssessment, connection);
            assertTrue(result, "La actualización debería ser exitosa");

            SelfAssessmentDTO retrievedSelfAssessment = selfAssessmentDAO.searchSelfAssessmentById("3", connection);
            assertNotNull(retrievedSelfAssessment, "La autoevaluación debería existir después de actualizar");
            assertEquals("Comentarios actualizados", retrievedSelfAssessment.getComments(), "Los comentarios deberían actualizarse");
        } catch (SQLException e) {
            fail("Error en testUpdateSelfAssessment: " + e.getMessage());
        }
    }

    @Test
    void testDeleteSelfAssessment() {
        try {
            SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                    "4", "Comentarios a eliminar", 60.0, "11223", "4"
            );

            selfAssessmentDAO.insertSelfAssessment(selfAssessment, connection);

            boolean result = selfAssessmentDAO.deleteSelfAssessment(selfAssessment, connection);
            assertTrue(result, "La eliminación debería ser exitosa");

            SelfAssessmentDTO deletedSelfAssessment = selfAssessmentDAO.searchSelfAssessmentById("4", connection);
            assertEquals("N/A", deletedSelfAssessment.getSelfAssessmentId(), "La autoevaluación eliminada no debería existir");
        } catch (SQLException e) {
            fail("Error en testDeleteSelfAssessment: " + e.getMessage());
        }
    }

    @Test
    void testGetAllSelfAssessments() {
        try {
            SelfAssessmentDTO selfAssessment1 = new SelfAssessmentDTO(
                    "5", "Comentarios 1", 80.0, "33445", "5"
            );

            SelfAssessmentDTO selfAssessment2 = new SelfAssessmentDTO(
                    "6", "Comentarios 2", 85.0, "55667", "6"
            );

            selfAssessmentDAO.insertSelfAssessment(selfAssessment1, connection);
            selfAssessmentDAO.insertSelfAssessment(selfAssessment2, connection);

            List<SelfAssessmentDTO> selfAssessments = selfAssessmentDAO.getAllSelfAssessments(connection);
            assertNotNull(selfAssessments, "La lista de autoevaluaciones no debería ser nula");
            assertEquals(2, selfAssessments.size(), "Deberían existir dos autoevaluaciones en la base de datos");
        } catch (SQLException e) {
            fail("Error en testGetAllSelfAssessments: " + e.getMessage());
        }
    }
}