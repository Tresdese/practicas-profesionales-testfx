package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.SelfAssessmentCriteriaDAO;
import logic.DTO.SelfAssessmentCriteriaDTO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SelfAssessmentCriteriaDAOTest {

    private static ConecctionDataBase connectionDB;
    private static Connection connection;
    private SelfAssessmentCriteriaDAO criteriaDAO;

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
        criteriaDAO = new SelfAssessmentCriteriaDAO();
        try {
            connection.prepareStatement("DELETE FROM criterio_de_autoevaluacion").executeUpdate();
        } catch (SQLException e) {
            fail("Error al limpiar la tabla criterio_de_autoevaluacion: " + e.getMessage());
        }
    }

    @Test
    void testInsertSelfAssessmentCriteria() {
        try {
            SelfAssessmentCriteriaDTO criteria = new SelfAssessmentCriteriaDTO(
                    "1",
                    "Criterio de Prueba",
                    85.5
            );

            boolean result = criteriaDAO.insertSelfAssessmentCriteria(criteria, connection);
            assertTrue(result, "La inserción debería ser exitosa");

            List<SelfAssessmentCriteriaDTO> criteriaList = criteriaDAO.getAllSelfAssessmentCriteria(connection);
            assertNotNull(criteriaList, "La lista de criterios no debería ser nula");
            assertEquals(1, criteriaList.size(), "Debería haber un criterio en la base de datos");
            assertEquals("Criterio de Prueba", criteriaList.get(0).getNameCriteria(), "El nombre debería coincidir");
        } catch (SQLException e) {
            fail("Error en testInsertSelfAssessmentCriteria: " + e.getMessage());
        }
    }

    @Test
    void testSearchSelfAssessmentCriteriaById() {
        try {
            SelfAssessmentCriteriaDTO criteria = new SelfAssessmentCriteriaDTO(
                    "8",
                    "Criterio de Consulta",
                    90.0
            );

            boolean insertResult = criteriaDAO.insertSelfAssessmentCriteria(criteria, connection);
            assertTrue(insertResult, "La inserción debería ser exitosa");

            SelfAssessmentCriteriaDTO retrievedCriteria = criteriaDAO.searchSelfAssessmentCriteriaById("8", connection);
            assertNotNull(retrievedCriteria, "El criterio debería existir en la base de datos");
            assertEquals("Criterio de Consulta", retrievedCriteria.getNameCriteria(), "El nombre debería coincidir");
        } catch (SQLException e) {
            fail("Error en testGetSelfAssessmentCriteria: " + e.getMessage());
        }
    }

    @Test
    void testUpdateSelfAssessmentCriteria() {
        try {
            SelfAssessmentCriteriaDTO criteria = new SelfAssessmentCriteriaDTO(
                    "3",
                    "Criterio Original",
                    75.0
            );

            criteriaDAO.insertSelfAssessmentCriteria(criteria, connection);

            SelfAssessmentCriteriaDTO updatedCriteria = new SelfAssessmentCriteriaDTO(
                    "3",
                    "Criterio Actualizado",
                    95.0
            );

            boolean result = criteriaDAO.updateSelfAssessmentCriteria(updatedCriteria, connection);
            assertTrue(result, "La actualización debería ser exitosa");

            SelfAssessmentCriteriaDTO retrievedCriteria = criteriaDAO.searchSelfAssessmentCriteriaById("3", connection);
            assertNotNull(retrievedCriteria, "El criterio debería existir después de actualizar");
            assertEquals("Criterio Actualizado", retrievedCriteria.getNameCriteria(), "El nombre debería actualizarse");
            assertEquals(95.0, retrievedCriteria.getGrade(), "La calificación debería actualizarse");
        } catch (SQLException e) {
            fail("Error en testUpdateSelfAssessmentCriteria: " + e.getMessage());
        }
    }

    @Test
    void testDeleteSelfAssessmentCriteria() {
        try {
            SelfAssessmentCriteriaDTO criteria = new SelfAssessmentCriteriaDTO(
                    "4",
                    "Criterio a Eliminar",
                    60.0
            );

            criteriaDAO.insertSelfAssessmentCriteria(criteria, connection);

            boolean result = criteriaDAO.deleteSelfAssessmentCriteria(criteria, connection);
            assertTrue(result, "La eliminación debería ser exitosa");

            SelfAssessmentCriteriaDTO deletedCriteria = criteriaDAO.searchSelfAssessmentCriteriaById("4", connection);
            assertNull(deletedCriteria, "El criterio eliminado no debería existir");
        } catch (SQLException e) {
            fail("Error en testDeleteSelfAssessmentCriteria: " + e.getMessage());
        }
    }

    @Test
    void testGetAllSelfAssessmentCriteria() {
        try {
            SelfAssessmentCriteriaDTO criteria1 = new SelfAssessmentCriteriaDTO(
                    "5",
                    "Criterio 1",
                    80.0
            );

            SelfAssessmentCriteriaDTO criteria2 = new SelfAssessmentCriteriaDTO(
                    "6",
                    "Criterio 2",
                    85.0
            );

            criteriaDAO.insertSelfAssessmentCriteria(criteria1, connection);
            criteriaDAO.insertSelfAssessmentCriteria(criteria2, connection);

            List<SelfAssessmentCriteriaDTO> criteriaList = criteriaDAO.getAllSelfAssessmentCriteria(connection);
            assertNotNull(criteriaList, "La lista de criterios no debería ser nula");
            assertEquals(2, criteriaList.size(), "Deberían existir dos criterios en la base de datos");
        } catch (SQLException e) {
            fail("Error en testGetAllSelfAssessmentCriteria: " + e.getMessage());
        }
    }
}