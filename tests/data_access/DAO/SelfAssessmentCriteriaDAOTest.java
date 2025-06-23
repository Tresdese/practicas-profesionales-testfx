package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.SelfAssessmentCriteriaDAO;
import logic.DTO.SelfAssessmentCriteriaDTO;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SelfAssessmentCriteriaDAOTest {

    private static ConnectionDataBase connectionDB;
    private static Connection connection;
    private SelfAssessmentCriteriaDAO selfAssessmentCriteriaDAO;

    @BeforeAll
    static void setUpAll() {
        try {
            connectionDB = new ConnectionDataBase();
            connection = connectionDB.connectDB();
        } catch (SQLException e) {
            fail("Error al conectar a la base de datos: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado al conectar a la base de datos: " + e.getMessage());
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
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en insertSelfAssessmentCriteriaSuccessfully: " + e.getMessage());
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
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en searchSelfAssessmentCriteriaByIdSuccessfully: " + e.getMessage());
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
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en updateSelfAssessmentCriteriaSuccessfully: " + e.getMessage());
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
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en deleteSelfAssessmentCriteriaSuccessfully: " + e.getMessage());
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
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en getAllSelfAssessmentCriteriaSuccessfully: " + e.getMessage());
        }
    }

    @Test
    void insertSelfAssessmentCriteriaFailsWithDuplicateId() {
        try {
            SelfAssessmentCriteriaDTO criteria = new SelfAssessmentCriteriaDTO("10", "Criterio Único");
            assertTrue(selfAssessmentCriteriaDAO.insertSelfAssessmentCriteria(criteria));

            SelfAssessmentCriteriaDTO duplicate = new SelfAssessmentCriteriaDTO("10", "Criterio Duplicado");
            assertThrows(SQLException.class, () -> selfAssessmentCriteriaDAO.insertSelfAssessmentCriteria(duplicate));
        } catch (SQLException e) {
            fail("Error inesperado en insertSelfAssessmentCriteriaFailsWithDuplicateId: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en insertSelfAssessmentCriteriaFailsWithDuplicateId: " + e.getMessage());
        }
    }

    @Test
    void updateSelfAssessmentCriteriaFailsWhenNotExists() {
        try {
            SelfAssessmentCriteriaDTO nonExistent = new SelfAssessmentCriteriaDTO("999", "No Existe");
            boolean result = selfAssessmentCriteriaDAO.updateSelfAssessmentCriteria(nonExistent);
            assertFalse(result, "No debería actualizar un criterio inexistente");
        } catch (SQLException e) {
            fail("Error inesperado en updateSelfAssessmentCriteriaFailsWhenNotExists: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en updateSelfAssessmentCriteriaFailsWhenNotExists: " + e.getMessage());
        }
    }

    @Test
    void deleteSelfAssessmentCriteriaFailsWhenNotExists() {
        try {
            SelfAssessmentCriteriaDTO nonExistent = new SelfAssessmentCriteriaDTO("888", "No Existe");
            boolean result = selfAssessmentCriteriaDAO.deleteSelfAssessmentCriteria(nonExistent);
            assertFalse(result, "No debería eliminar un criterio inexistente");
        } catch (SQLException e) {
            fail("Error inesperado en deleteSelfAssessmentCriteriaFailsWhenNotExists: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en deleteSelfAssessmentCriteriaFailsWhenNotExists: " + e.getMessage());
        }
    }

    @Test
    void searchSelfAssessmentCriteriaByIdReturnsNAWhenNotExists() {
        try {
            SelfAssessmentCriteriaDTO found = selfAssessmentCriteriaDAO.searchSelfAssessmentCriteriaById("777");
            assertNotNull(found, "El resultado no debe ser nulo");
            assertEquals("N/A", found.getIdCriteria(), "El id debe ser 'N/A' si no existe");
            assertEquals("N/A", found.getNameCriteria(), "El nombre debe ser 'N/A' si no existe");
        } catch (SQLException e) {
            fail("Error inesperado en searchSelfAssessmentCriteriaByIdReturnsNAWhenNotExists: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en searchSelfAssessmentCriteriaByIdReturnsNAWhenNotExists: " + e.getMessage());
        }
    }

    @Test
    void getAllSelfAssessmentCriteriaReturnsEmptyListWhenNoCriteriaExist() {
        try {
            connection.prepareStatement("DELETE FROM criterio_de_autoevaluacion").executeUpdate();
            List<SelfAssessmentCriteriaDTO> list = selfAssessmentCriteriaDAO.getAllSelfAssessmentCriteria();
            assertNotNull(list, "La lista no debe ser nula");
            assertTrue(list.isEmpty(), "La lista debe estar vacía si no hay criterios");
        } catch (SQLException e) {
            fail("Error inesperado en getAllSelfAssessmentCriteriaReturnsEmptyListWhenNoCriteriaExist: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en getAllSelfAssessmentCriteriaReturnsEmptyListWhenNoCriteriaExist: " + e.getMessage());
        }
    }

    @Test
    void insertSelfAssessmentCriteriaFailsWithNullOrEmptyFields() {
        assertThrows(SQLException.class, () -> {
            SelfAssessmentCriteriaDTO nullFields = new SelfAssessmentCriteriaDTO(null, null);
            selfAssessmentCriteriaDAO.insertSelfAssessmentCriteria(nullFields);
        });

        assertThrows(SQLException.class, () -> {
            SelfAssessmentCriteriaDTO emptyFields = new SelfAssessmentCriteriaDTO("", "");
            selfAssessmentCriteriaDAO.insertSelfAssessmentCriteria(emptyFields);
        });
    }

    @Test
    void getAllSelfAssessmentCriteriaReturnsOrderedList() {
        try {
            selfAssessmentCriteriaDAO.insertSelfAssessmentCriteria(new SelfAssessmentCriteriaDTO("1", "A"));
            selfAssessmentCriteriaDAO.insertSelfAssessmentCriteria(new SelfAssessmentCriteriaDTO("2", "B"));
            selfAssessmentCriteriaDAO.insertSelfAssessmentCriteria(new SelfAssessmentCriteriaDTO("3", "C"));

            List<SelfAssessmentCriteriaDTO> list = selfAssessmentCriteriaDAO.getAllSelfAssessmentCriteria();
            assertEquals(3, list.size());
            // Verifica orden por id (ajusta si tu consulta tiene ORDER BY)
            assertEquals("1", list.get(0).getIdCriteria());
            assertEquals("2", list.get(1).getIdCriteria());
            assertEquals("3", list.get(2).getIdCriteria());
        } catch (SQLException e) {
            fail("Error inesperado en getAllSelfAssessmentCriteriaReturnsOrderedList: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en getAllSelfAssessmentCriteriaReturnsOrderedList: " + e.getMessage());
        }
    }

    @Test
    void insertMultipleSelfAssessmentCriteriaAndCheckCount() {
        try {
            for (int i = 100; i < 110; i++) {
                selfAssessmentCriteriaDAO.insertSelfAssessmentCriteria(
                        new SelfAssessmentCriteriaDTO(String.valueOf(i), "Criterio " + i)
                );
            }
            List<SelfAssessmentCriteriaDTO> list = selfAssessmentCriteriaDAO.getAllSelfAssessmentCriteria();
            assertEquals(10, list.size());
        } catch (SQLException e) {
            fail("Error inesperado en insertMultipleSelfAssessmentCriteriaAndCheckCount: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en insertMultipleSelfAssessmentCriteriaAndCheckCount: " + e.getMessage());
        }
    }

    @Test
    void deleteAllSelfAssessmentCriteria() {
        try {
            selfAssessmentCriteriaDAO.insertSelfAssessmentCriteria(new SelfAssessmentCriteriaDTO("50", "A"));
            selfAssessmentCriteriaDAO.insertSelfAssessmentCriteria(new SelfAssessmentCriteriaDTO("51", "B"));

            List<SelfAssessmentCriteriaDTO> list = selfAssessmentCriteriaDAO.getAllSelfAssessmentCriteria();
            for (SelfAssessmentCriteriaDTO c : list) {
                selfAssessmentCriteriaDAO.deleteSelfAssessmentCriteria(c);
            }
            List<SelfAssessmentCriteriaDTO> afterDelete = selfAssessmentCriteriaDAO.getAllSelfAssessmentCriteria();
            assertTrue(afterDelete.isEmpty());
        } catch (SQLException e) {
            fail("Error inesperado en deleteAllSelfAssessmentCriteria: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en deleteAllSelfAssessmentCriteria: " + e.getMessage());
        }
    }

    @Test
    void searchSelfAssessmentCriteriaByIdWithNullOrEmptyId() {
        try {
            SelfAssessmentCriteriaDTO foundNull = selfAssessmentCriteriaDAO.searchSelfAssessmentCriteriaById(null);
            assertNotNull(foundNull);
            assertEquals("N/A", foundNull.getIdCriteria());

            SelfAssessmentCriteriaDTO foundEmpty = selfAssessmentCriteriaDAO.searchSelfAssessmentCriteriaById("");
            assertNotNull(foundEmpty);
            assertEquals("N/A", foundEmpty.getIdCriteria());
        } catch (SQLException e) {
            fail("Error inesperado en searchSelfAssessmentCriteriaByIdWithNullOrEmptyId: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en searchSelfAssessmentCriteriaByIdWithNullOrEmptyId: " + e.getMessage());
        }
    }
}