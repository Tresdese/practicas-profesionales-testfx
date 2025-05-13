//package data_access.DAO;
//
//import data_access.ConecctionDataBase;
//import logic.DAO.CriterionSelfAssessmentDAO;
//import logic.DTO.CriterionSelfAssessmentDTO;
//import org.junit.jupiter.api.*;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.List;
//import static org.junit.jupiter.api.Assertions.*;
//
//class CriterionSelfAssessmentDAOTest {
//
//    private static ConecctionDataBase connectionDB;
//    private static Connection connection;
//    private CriterionSelfAssessmentDAO criterionDAO;
//
//    @BeforeAll
//    static void setUpClass() {
//        connectionDB = new ConecctionDataBase();
//        try {
//            connection = connectionDB.connectDB();
//        } catch (SQLException e) {
//            fail("Error al conectar a la base de datos: " + e.getMessage());
//        }
//    }
//
//    @AfterAll
//    static void tearDownClass() {
//        connectionDB.close();
//    }
//
//    @BeforeEach
//    void setUp() {
//        criterionDAO = new CriterionSelfAssessmentDAO();
//        try {
//            connection.prepareStatement("DELETE FROM autoevaluacion_criterio").executeUpdate();
//        } catch (SQLException e) {
//            fail("Error al limpiar la tabla autoevaluacion_criterio: " + e.getMessage());
//        }
//    }
//
//    @Test
//    void testInsertCriterionSelfAssessment() {
//        try {
//            CriterionSelfAssessmentDTO criterion = new CriterionSelfAssessmentDTO(
//                    "1",
//                    "1"
//            );
//
//            boolean result = criterionDAO.insertCriterionSelfAssessment(criterion, connection);
//            assertTrue(result, "La inserción debería ser exitosa");
//
//            List<CriterionSelfAssessmentDTO> criterionList = criterionDAO.getAllCriterionSelfAssessments(connection);
//            assertEquals(1, criterionList.size(), "Debería haber un criterio en la base de datos");
//            assertEquals("1", criterionList.get(0).getIdCriteria(), "El ID del criterio debería coincidir");
//        } catch (SQLException e) {
//            fail("Error en testInsertCriterionSelfAssessment: " + e.getMessage());
//        }
//    }
//
//    @Test
//    void testSearchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria() {
//        try {
//            CriterionSelfAssessmentDTO criterion = new CriterionSelfAssessmentDTO(
//                    "1",
//                    "1"
//            );
//
//            criterionDAO.insertCriterionSelfAssessment(criterion, connection);
//
//            CriterionSelfAssessmentDTO retrievedCriterion = criterionDAO.searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria("1", "1", connection);
//            assertNotNull(retrievedCriterion, "El criterio debería existir en la base de datos");
//            assertEquals("1", retrievedCriterion.getIdCriteria(), "El ID del criterio debería coincidir");
//        } catch (SQLException e) {
//            fail("Error en testGetCriterionSelfAssessment: " + e.getMessage());
//        }
//    }
//
//    @Test
//    void testUpdateCriterionSelfAssessment() {
//        try {
//            CriterionSelfAssessmentDTO criterion = new CriterionSelfAssessmentDTO(
//                    "1",
//                    "1"
//            );
//
//            criterionDAO.insertCriterionSelfAssessment(criterion, connection);
//
//            CriterionSelfAssessmentDTO updatedCriterion = new CriterionSelfAssessmentDTO(
//                    "1",
//                    "2"
//            );
//
//            boolean result = criterionDAO.updateCriterionSelfAssessment(updatedCriterion, connection);
//            assertTrue(result, "La actualización debería ser exitosa");
//
//            CriterionSelfAssessmentDTO retrievedCriterion = criterionDAO.searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria("1", "2", connection);
//            assertNotNull(retrievedCriterion, "El criterio debería existir después de actualizar");
//            assertEquals("2", retrievedCriterion.getIdCriteria(), "El ID del criterio debería actualizarse");
//        } catch (SQLException e) {
//            fail("Error en testUpdateCriterionSelfAssessment: " + e.getMessage());
//        }
//    }
//
//    @Test
//    void testDeleteCriterionSelfAssessment() {
//        try {
//            CriterionSelfAssessmentDTO criterion = new CriterionSelfAssessmentDTO(
//                    "1",
//                    "1"
//            );
//
//            criterionDAO.insertCriterionSelfAssessment(criterion, connection);
//
//            boolean result = criterionDAO.deleteCriterionSelfAssessment("1", "1", connection);
//            assertTrue(result, "La eliminación debería ser exitosa");
//
//            CriterionSelfAssessmentDTO deletedCriterion = criterionDAO.searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria("1", "1", connection);
//            assertNull(deletedCriterion, "El criterio eliminado no debería existir");
//        } catch (SQLException e) {
//            fail("Error en testDeleteCriterionSelfAssessment: " + e.getMessage());
//        }
//    }
//
//    @Test
//    void testGetAllCriterionSelfAssessments() {
//        try {
//            CriterionSelfAssessmentDTO criterion1 = new CriterionSelfAssessmentDTO(
//                    "1",
//                    "1"
//            );
//
//            CriterionSelfAssessmentDTO criterion2 = new CriterionSelfAssessmentDTO(
//                    "2",
//                    "2"
//            );
//
//            criterionDAO.insertCriterionSelfAssessment(criterion1, connection);
//            criterionDAO.insertCriterionSelfAssessment(criterion2, connection);
//
//            List<CriterionSelfAssessmentDTO> criterionList = criterionDAO.getAllCriterionSelfAssessments(connection);
//            assertNotNull(criterionList, "La lista de criterios no debería ser nula");
//            assertEquals(2, criterionList.size(), "Deberían existir dos criterios en la base de datos");
//        } catch (SQLException e) {
//            fail("Error en testGetAllCriterionSelfAssessments: " + e.getMessage());
//        }
//    }
//}