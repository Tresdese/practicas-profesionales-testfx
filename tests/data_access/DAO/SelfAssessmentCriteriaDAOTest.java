package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.SelfAssessmentCriteriaDAO;
import logic.DTO.SelfAssessmentCriteriaDTO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SelfAssessmentCriteriaDAOTest {

    private static ConecctionDataBase connectionDB;
    private static Connection connection;
    private SelfAssessmentCriteriaDAO selfAssessmentCriteriaDAO;

    @BeforeAll
    static void setUpAll() {
        connectionDB = new ConecctionDataBase();
        try {
            connection = connectionDB.connectDB();
        } catch (SQLException e) {
            fail("Error al conectar a la base de datos: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDownAll() {
        connectionDB.close();
    }

    @BeforeEach
    void setUp() {
        selfAssessmentCriteriaDAO = new SelfAssessmentCriteriaDAO();
        try {
            connection.prepareStatement("DELETE FROM criterio_de_autoevaluacion").executeUpdate();
        } catch (SQLException e) {
            fail("Error al limpiar la tabla criterio_de_autoevaluacion: " + e.getMessage());
        }
    }

    @Test
    void insertSelfAssessmentCriteriaSuccessfully() {
        try {
            SelfAssessmentCriteriaDTO criteria = new SelfAssessmentCriteriaDTO(
                    "1", "Criterio de Prueba"
            );

            boolean result = selfAssessmentCriteriaDAO.insertSelfAssessmentCriteria(criteria);
            assertTrue(result, "La inserción debería ser exitosa");

            List<SelfAssessmentCriteriaDTO> criteriaList = selfAssessmentCriteriaDAO.getAllSelfAssessmentCriteria();
            assertNotNull(criteriaList, "La lista de criterios no debería ser nula");
            assertEquals(1, criteriaList.size(), "Debería haber un criterio en la base de datos");
            assertEquals("Criterio de Prueba", criteriaList.get(0).getNameCriteria(), "El nombre debería coincidir");
        } catch (SQLException e) {
            fail("Error en insertSelfAssessmentCriteriaSuccessfully: " + e.getMessage());
        }
    }

    @Test
    void searchSelfAssessmentCriteriaByIdSuccessfully() {
        try {
            SelfAssessmentCriteriaDTO criteria = new SelfAssessmentCriteriaDTO(
                    "2", "Criterio de Consulta"
            );
            selfAssessmentCriteriaDAO.insertSelfAssessmentCriteria(criteria);

            SelfAssessmentCriteriaDTO found = selfAssessmentCriteriaDAO.searchSelfAssessmentCriteriaById("2");
            assertNotNull(found, "El criterio buscado no debe ser nulo");
            assertEquals("Criterio de Consulta", found.getNameCriteria(), "El nombre debe coincidir");
        } catch (SQLException e) {
            fail("Error en searchSelfAssessmentCriteriaByIdSuccessfully: " + e.getMessage());
        }
    }

    @Test
    void updateSelfAssessmentCriteriaSuccessfully() {
        try {
            SelfAssessmentCriteriaDTO original = new SelfAssessmentCriteriaDTO(
                    "3", "Criterio Original"
            );
            selfAssessmentCriteriaDAO.insertSelfAssessmentCriteria(original);

            SelfAssessmentCriteriaDTO updated = new SelfAssessmentCriteriaDTO(
                    "3", "Criterio Actualizado"
            );
            boolean result = selfAssessmentCriteriaDAO.updateSelfAssessmentCriteria(updated);
            assertTrue(result, "La actualización debería ser exitosa");

            SelfAssessmentCriteriaDTO found = selfAssessmentCriteriaDAO.searchSelfAssessmentCriteriaById("3");
            assertEquals("Criterio Actualizado", found.getNameCriteria(), "El nombre debe actualizarse");
        } catch (SQLException e) {
            fail("Error en updateSelfAssessmentCriteriaSuccessfully: " + e.getMessage());
        }
    }

    @Test
    void deleteSelfAssessmentCriteriaSuccessfully() {
        try {
            SelfAssessmentCriteriaDTO criteria = new SelfAssessmentCriteriaDTO(
                    "4", "Criterio a Eliminar"
            );
            selfAssessmentCriteriaDAO.insertSelfAssessmentCriteria(criteria);

            boolean result = selfAssessmentCriteriaDAO.deleteSelfAssessmentCriteria(criteria);
            assertTrue(result, "La eliminación debería ser exitosa");

            SelfAssessmentCriteriaDTO found = selfAssessmentCriteriaDAO.searchSelfAssessmentCriteriaById("4");
            assertEquals("N/A", found.getIdCriteria(), "El criterio eliminado no debería existir");
        } catch (SQLException e) {
            fail("Error en deleteSelfAssessmentCriteriaSuccessfully: " + e.getMessage());
        }
    }

    @Test
    void getAllSelfAssessmentCriteriaSuccessfully() {
        try {
            SelfAssessmentCriteriaDTO criteria1 = new SelfAssessmentCriteriaDTO(
                    "5", "Criterio 1"
            );

            SelfAssessmentCriteriaDTO criteria2 = new SelfAssessmentCriteriaDTO(
                    "6", "Criterio 2"
            );

            selfAssessmentCriteriaDAO.insertSelfAssessmentCriteria(criteria1);
            selfAssessmentCriteriaDAO.insertSelfAssessmentCriteria(criteria2);

            List<SelfAssessmentCriteriaDTO> criteriaList = selfAssessmentCriteriaDAO.getAllSelfAssessmentCriteria();
            assertNotNull(criteriaList, "La lista de criterios no debería ser nula");
            assertEquals(2, criteriaList.size(), "Deberían existir dos criterios en la base de datos");
        } catch (SQLException e) {
            fail("Error en getAllSelfAssessmentCriteriaSuccessfully: " + e.getMessage());
        }
    }
}