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
        activityDAO = new ActivityDAO();
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
        return -1; // Retorno por defecto en caso de error
    }

    @Test
    void testInsertActivity() throws SQLException {
        try {
            int randomNumber = (int) (Math.random() * 1000);
            String nombreActividad = "Actividad de Prueba " + randomNumber;

            ActivityDTO activity = new ActivityDTO("0", nombreActividad);
            boolean result = activityDAO.insertActivity(activity, connection);
            assertTrue(result, "La inserción debería ser exitosa");

            List<ActivityDTO> activities = activityDAO.getAllActivities(connection);
            ActivityDTO insertedActivity = activities.stream()
                    .filter(a -> a.getActivityName().equals(nombreActividad))
                    .findFirst()
                    .orElse(null);

            assertNotNull(insertedActivity, "La actividad debería existir en la base de datos");
            assertEquals(nombreActividad, insertedActivity.getActivityName(), "El nombre debería coincidir");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
        }
    }

    @Test
    void testGetActivity() throws SQLException {
        try {
            testActivityId = insertTestActivity("Actividad para Consulta");

            ActivityDTO retrievedActivity = activityDAO.getActivity(String.valueOf(testActivityId), connection);
            assertNotNull(retrievedActivity, "Debería encontrar la actividad");
            assertEquals(String.valueOf(testActivityId), retrievedActivity.getActivityId(), "El ID debería coincidir");
            assertEquals("Actividad para Consulta", retrievedActivity.getActivityName(), "El nombre debería coincidir");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
        }
    }

    @Test
    void testUpdateActivity() throws SQLException {
        try {
            testActivityId = insertTestActivity("Actividad Original");

            ActivityDTO activity = new ActivityDTO(String.valueOf(testActivityId), "Actividad Actualizada");
            boolean updateResult = activityDAO.updateActivity(activity, connection);
            assertTrue(updateResult, "La actualización debería ser exitosa");

            ActivityDTO updated = activityDAO.getActivity(String.valueOf(testActivityId), connection);
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

            List<ActivityDTO> activities = activityDAO.getAllActivities(connection);
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

            ActivityDTO before = activityDAO.getActivity(String.valueOf(deleteId), connection);
            assertNotNull(before, "La actividad debería existir antes de eliminarla");

            ActivityDTO toDelete = new ActivityDTO(String.valueOf(deleteId), "Actividad para Eliminar");
            boolean deleted = activityDAO.deleteActivity(toDelete, connection);
            assertTrue(deleted, "La eliminación debería ser exitosa");

            ActivityDTO after = activityDAO.getActivity(String.valueOf(deleteId), connection);
            assertNull(after, "La actividad no debería existir después de eliminarla");
        } catch (SQLException e) {
            logger.error("Error: " + e.getMessage());
        }
    }
}