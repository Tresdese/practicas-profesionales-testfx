//package data_access.DAO;
//
//import logic.DAO.AssessmentCriterionDAO;
//import logic.DTO.AssessmentCriterionDTO;
//import org.junit.jupiter.api.*;
//
//import java.sql.SQLException;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class AssessmentCriterionDAOTest {
//
//    private AssessmentCriterionDAO assessmentCriterionDAO;
//
//    @BeforeEach
//    void setUp() {
//        assessmentCriterionDAO = new AssessmentCriterionDAO();
//    }
//
//    @Test
//    void testInsertAssessmentCriterion() {
//        try {
//            String nombreCriterio = "Criterio de Prueba";
//            AssessmentCriterionDTO criterion = new AssessmentCriterionDTO("1", nombreCriterio);
//
//            boolean result = assessmentCriterionDAO.insertAssessmentCriterion(criterion);
//            assertTrue(result, "La inserción debería ser exitosa");
//
//            List<AssessmentCriterionDTO> criteria = assessmentCriterionDAO.getAllAssessmentCriteria();
//            AssessmentCriterionDTO insertedCriterion = criteria.stream()
//                    .filter(c -> c.getNameCriterion().equals(nombreCriterio))
//                    .findFirst()
//                    .orElse(null);
//
//            assertNotNull(insertedCriterion, "El criterio debería existir en la base de datos");
//            assertEquals(nombreCriterio, insertedCriterion.getNameCriterion(), "El nombre debería coincidir");
//        } catch (SQLException e) {
//            fail("Error: " + e.getMessage());
//        }
//    }
//
//    @Test
//    void testSearchAssessmentCriterionById() {
//        try {
//            String idCriterio = "2";
//            String nombreCriterio = "Criterio para Consulta";
//            assessmentCriterionDAO.insertAssessmentCriterion(new AssessmentCriterionDTO(idCriterio, nombreCriterio));
//
//            AssessmentCriterionDTO retrievedCriterion = assessmentCriterionDAO.searchAssessmentCriterionById(idCriterio);
//            assertNotNull(retrievedCriterion, "Debería encontrar el criterio");
//            assertEquals(idCriterio, retrievedCriterion.getIdCriterion(), "El ID debería coincidir");
//            assertEquals(nombreCriterio, retrievedCriterion.getNameCriterion(), "El nombre debería coincidir");
//        } catch (SQLException e) {
//            fail("Error: " + e.getMessage());
//        }
//    }
//
//    @Test
//    void testUpdateAssessmentCriterion() {
//        try {
//            String idCriterio = "3";
//            assessmentCriterionDAO.insertAssessmentCriterion(new AssessmentCriterionDTO(idCriterio, "Criterio Original"));
//
//            AssessmentCriterionDTO updatedCriterion = new AssessmentCriterionDTO(idCriterio, "Criterio Actualizado");
//            boolean updateResult = assessmentCriterionDAO.updateAssessmentCriterion(updatedCriterion);
//            assertTrue(updateResult, "La actualización debería ser exitosa");
//
//            AssessmentCriterionDTO retrievedCriterion = assessmentCriterionDAO.searchAssessmentCriterionById(idCriterio);
//            assertNotNull(retrievedCriterion, "El criterio debería existir");
//            assertEquals("Criterio Actualizado", retrievedCriterion.getNameCriterion(), "El nombre debería actualizarse");
//        } catch (SQLException e) {
//            fail("Error: " + e.getMessage());
//        }
//    }
//
//    @Test
//    void testGetAllAssessmentCriteria() {
//        try {
//            assessmentCriterionDAO.insertAssessmentCriterion(new AssessmentCriterionDTO("4", "Criterio para Listar"));
//
//            List<AssessmentCriterionDTO> criteria = assessmentCriterionDAO.getAllAssessmentCriteria();
//            assertNotNull(criteria, "La lista no debería ser nula");
//            assertFalse(criteria.isEmpty(), "La lista no debería estar vacía");
//
//            boolean found = criteria.stream()
//                    .anyMatch(c -> c.getIdCriterion().equals("4"));
//            assertTrue(found, "Nuestro criterio de prueba debería estar en la lista");
//        } catch (SQLException e) {
//            fail("Error: " + e.getMessage());
//        }
//    }
//
//    @Test
//    void testDeleteAssessmentCriterion() {
//        try {
//            String idCriterio = "5";
//            assessmentCriterionDAO.insertAssessmentCriterion(new AssessmentCriterionDTO(idCriterio, "Criterio para Eliminar"));
//
//            AssessmentCriterionDTO before = assessmentCriterionDAO.searchAssessmentCriterionById(idCriterio);
//            assertNotNull(before, "El criterio debería existir antes de eliminarlo");
//
//            boolean deleted = assessmentCriterionDAO.deleteAssessmentCriterion(idCriterio);
//            assertTrue(deleted, "La eliminación debería ser exitosa");
//
//            AssessmentCriterionDTO after = assessmentCriterionDAO.searchAssessmentCriterionById(idCriterio);
//            assertNull(after, "El criterio no debería existir después de eliminarlo");
//        } catch (SQLException e) {
//            fail("Error: " + e.getMessage());
//        }
//    }
//}