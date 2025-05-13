package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.ActivityDAO;
import logic.DTO.ActivityDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActivityDAOTest {

    private static final Logger logger = LogManager.getLogger(ActivityDAOTest.class);

    private static ConecctionDataBase connectionDB;
    private static Connection connection;

    private ActivityDAO activityDAO;
    private int testActivityId;

    ActivityDTO insertActivityObject;

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
        activityDAO = new ActivityDAO(connection);
    }

    private int insertTestActivity(String nombre) throws SQLException {
        String sql = "INSERT INTO actividad (nombreActividad) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nombre);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    logger.error("No se generó ID para la actividad");
                }
            }
        }
        return -1;
    }

    @Test
    void testInsertTestActivitySuccess() throws SQLException {
        insertActivityObject = new ActivityDTO("0", "Actividad de Prueba1");
        activityDAO.insertActivity(insertActivityObject);
        int id = activityDAO.getActivityByName(insertActivityObject.getActivityName());

        assertTrue(id > 0, "Debería generarse un ID positivo");

        String sql = "SELECT nombreActividad FROM actividad WHERE idActividad = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next(), "Debería encontrarse la actividad");
                assertEquals(insertActivityObject.getActivityName(), rs.getString("nombreActividad"), "El nombre debería coincidir");
            }
        }
    }

    @Test
    void testInsertTestActivityWithEmptyName() {
        insertActivityObject = new ActivityDTO("0", "");
        try {
            boolean result = activityDAO.insertActivity(insertActivityObject);
            assertFalse(result, "No debería permitir insertar con nombre vacío");
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("constraint") || e.getMessage().contains("validation"),
                    "La excepción debería estar relacionada con restricciones de validación");
        }
    }

    @Test
    void testInsertTestActivityWithNullName() {
        insertActivityObject = new ActivityDTO("0", null);
        try {
            boolean result = activityDAO.insertActivity(insertActivityObject);
            assertFalse(result, "No debería permitir insertar con nombre nulo");
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("null") || e.getMessage().contains("constraint"), "La excepción debería estar relacionada con valores nulos");
        }
    }

    @Test
    void testInsertActivity() throws SQLException {
        int randomNumber = (int) (Math.random() * 1000);
        try {
            String nombreActividad = "Actividad de Prueba " + randomNumber;

            insertActivityObject = new ActivityDTO("0", nombreActividad);
            boolean result = activityDAO.insertActivity(insertActivityObject);
            assertTrue(result, "La inserción debería ser exitosa");

            List<ActivityDTO> activities = activityDAO.getAllActivities();
            ActivityDTO insertedActivity = activities.stream()
                    .filter(a -> nombreActividad.equals(a.getActivityName()))
                    .findFirst()
                    .orElse(null);

            assertNotNull(insertedActivity, "La actividad debería existir en la base de datos");
            assertEquals(nombreActividad, insertedActivity.getActivityName(), "El nombre debería coincidir");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
        }
    }

    @Test
    void testGetExistingActivity() throws SQLException {
        testActivityId = insertTestActivity("Actividad para Consulta");

        ActivityDTO retrievedActivity = activityDAO.searchActivityById(String.valueOf(testActivityId));

        assertNotNull(retrievedActivity, "Debería encontrar la actividad");
        assertEquals(String.valueOf(testActivityId), retrievedActivity.getActivityId(), "El ID debería coincidir");
        assertEquals("Actividad para Consulta", retrievedActivity.getActivityName(), "El nombre debería coincidir");
    }

    @Test
    void testGetNonExistentActivity() throws SQLException {
        String idInexistente = "999999";

        ActivityDTO retrievedActivity = activityDAO.searchActivityById(idInexistente);

        assertNull(retrievedActivity, "No debería encontrar una actividad inexistente");
        assertEquals("invalido", retrievedActivity.getActivityId(), "Debería devolver un ID inválido");
    }

    @Test
    void testGetInvalidIdActivity() throws SQLException {
        try {
            ActivityDTO retrievedActivity = activityDAO.searchActivityById("@");
            assertEquals("invalido", retrievedActivity.getActivityId(), "Debería devolver un ID inválido");
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("formato") || e.getMessage().contains("number"),
                    "Debería lanzar excepción por formato inválido");
        }
    }

    @Test
    void testGetActivityConIdNulo() throws SQLException {
        try {
            ActivityDTO retrievedActivity = activityDAO.searchActivityById(null);
            fail("Debería lanzar excepción al pasar un ID nulo");
        } catch (SQLException | NullPointerException e) {
            assertTrue(true, "Se esperaba una excepción al pasar un ID nulo");
        }
    }

    @Test
    void testUpdateActivity() throws SQLException {
        try {
            testActivityId = insertTestActivity("Actividad Original");

            ActivityDTO activity = new ActivityDTO(String.valueOf(testActivityId), "Actividad Actualizada");
            boolean updateResult = activityDAO.updateActivity(activity);
            assertTrue(updateResult, "La actualización debería ser exitosa");

            ActivityDTO updated = activityDAO.searchActivityById(String.valueOf(testActivityId));
            assertNotNull(updated, "La actividad debería existir");
            assertEquals("Actividad Actualizada", updated.getActivityName(), "El nombre debería actualizarse");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
        }
    }

    @Test
    void testGetAllActivities() throws SQLException {
        try {
            testActivityId = insertTestActivity("Actividad para Listar");

            List<ActivityDTO> activities = activityDAO.getAllActivities();
            assertNotNull(activities, "La lista no debería ser nula");
            assertFalse(activities.isEmpty(), "La lista no debería estar vacía");

            boolean found = activities.stream()
                    .anyMatch(a -> a.getActivityId().equals(String.valueOf(testActivityId)));
            assertTrue(found, "Nuestra actividad de prueba debería estar en la lista");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
        }
    }

    @Test
    void testDeleteActivityFunctionality() throws SQLException {
        try {
            int deleteId = insertTestActivity("Actividad para Eliminar");

            ActivityDTO before = activityDAO.searchActivityById(String.valueOf(deleteId));
            assertNotNull(before, "La actividad debería existir antes de eliminarla");

            ActivityDTO toDelete = new ActivityDTO(String.valueOf(deleteId), "Actividad para Eliminar");
            boolean deleted = activityDAO.deleteActivity(toDelete);
            assertTrue(deleted, "La eliminación debería ser exitosa");

            ActivityDTO after = activityDAO.searchActivityById(String.valueOf(deleteId));
            if (after != null) {
                assertEquals("invalido", after.getActivityId(), "El ID debería ser inválido después de eliminar");
            }
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }
}