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
        connectionDB.close();
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
                    "1", "Criterio de Prueba"
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
                    "2", "Criterio de Consulta"
            );
            criteriaDAO.insertSelfAssessmentCriteria(criteria, connection);

            SelfAssessmentCriteriaDTO found = criteriaDAO.searchSelfAssessmentCriteriaById("2", connection);
            assertNotNull(found, "El criterio buscado no debe ser nulo");
            assertEquals("Criterio de Consulta", found.getNameCriteria(), "El nombre debe coincidir");
        } catch (SQLException e) {
            fail("Error en testSearchSelfAssessmentCriteriaById: " + e.getMessage());
        }
    }

    @Test
    void testUpdateSelfAssessmentCriteria() {
        try {
            SelfAssessmentCriteriaDTO original = new SelfAssessmentCriteriaDTO(
                    "3", "Criterio Original"
            );
            criteriaDAO.insertSelfAssessmentCriteria(original, connection);

            SelfAssessmentCriteriaDTO updated = new SelfAssessmentCriteriaDTO(
                    "3", "Criterio Actualizado"
            );
            boolean result = criteriaDAO.updateSelfAssessmentCriteria(updated, connection);
            assertTrue(result, "La actualización debería ser exitosa");

            SelfAssessmentCriteriaDTO found = criteriaDAO.searchSelfAssessmentCriteriaById("3", connection);
            assertEquals("Criterio Actualizado", found.getNameCriteria(), "El nombre debe actualizarse");
        } catch (SQLException e) {
            fail("Error en testUpdateSelfAssessmentCriteria: " + e.getMessage());
        }
    }

    @Test
    void testDeleteSelfAssessmentCriteria() {
        try {
            SelfAssessmentCriteriaDTO criteria = new SelfAssessmentCriteriaDTO(
                    "4", "Criterio a Eliminar"
            );
            criteriaDAO.insertSelfAssessmentCriteria(criteria, connection);

            boolean result = criteriaDAO.deleteSelfAssessmentCriteria(criteria, connection);
            assertTrue(result, "La eliminación debería ser exitosa");

            SelfAssessmentCriteriaDTO found = criteriaDAO.searchSelfAssessmentCriteriaById("4", connection);
            assertEquals("N/A", found.getIdCriteria(), "El criterio eliminado no debería existir");
        } catch (SQLException e) {
            fail("Error en testDeleteSelfAssessmentCriteria: " + e.getMessage());
        }
    }

    @Test
    void testGetAllSelfAssessmentCriteria() {
        try {
            SelfAssessmentCriteriaDTO criteria1 = new SelfAssessmentCriteriaDTO(
                    "5", "Criterio 1"
            );

            SelfAssessmentCriteriaDTO criteria2 = new SelfAssessmentCriteriaDTO(
                    "6", "Criterio 2"
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