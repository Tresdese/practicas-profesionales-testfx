package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.ActivityDAO;
import logic.DTO.ActivityDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActivityDAOTest {

    private static final Logger logger = LogManager.getLogger(ActivityDAOTest.class);

    private ConnectionDataBase connectionDataBase;
    private Connection connection;
    private ActivityDAO activityDataAccessObject;
    private int baseActivityId;

    @BeforeAll
    void setUpAll() throws SQLException, IOException {
        connectionDataBase = new ConnectionDataBase();
        connection = connectionDataBase.connectDataBase();
        clearTableAndResetAutoIncrement();
        activityDataAccessObject = new ActivityDAO();
        createBaseActivity();
    }

    @AfterAll
    void tearDownAll() throws SQLException {
        clearTableAndResetAutoIncrement();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @BeforeEach
    void setUp() throws SQLException, IOException {
        clearTableAndResetAutoIncrement();
        createBaseActivity();
    }

    private void clearTableAndResetAutoIncrement() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM actividad")) {
            statement.executeUpdate();
        }
        try (PreparedStatement statement = connection.prepareStatement("ALTER TABLE actividad AUTO_INCREMENT = 1")) {
            statement.executeUpdate();
        }
    }

    private void createBaseActivity() throws SQLException, IOException {
        String sqlInsert = "INSERT INTO actividad (nombreActividad) VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, "Actividad Base");
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    baseActivityId = resultSet.getInt(1);
                }
            }
        }
    }

    @Test
    void testInsertActivitySuccess() throws SQLException, IOException {
        ActivityDTO activity = new ActivityDTO(null, "Nueva Actividad");
        boolean wasInserted = activityDataAccessObject.insertActivity(activity);
        assertTrue(wasInserted, "La actividad debería insertarse correctamente.");
    }

    @Test
    void testGetActivityById() throws SQLException, IOException {
        ActivityDTO activity = activityDataAccessObject.searchActivityById(String.valueOf(baseActivityId));
        assertNotNull(activity, "La actividad no debería ser nula.");
        assertEquals("Actividad Base", activity.getActivityName());
    }

    @Test
    void testDeleteActivity() throws SQLException, IOException {
        ActivityDTO activity = new ActivityDTO(null, "Para Eliminar");
        activityDataAccessObject.insertActivity(activity);
        int activityId = activityDataAccessObject.getActivityByName("Para Eliminar");
        boolean wasDeleted = activityDataAccessObject.deleteActivity(new ActivityDTO(String.valueOf(activityId), null));
        assertTrue(wasDeleted, "La actividad debería eliminarse correctamente.");
    }

    @Test
    void testUpdateActivity() throws SQLException, IOException {
        ActivityDTO activity = new ActivityDTO(null, "Para Actualizar");
        activityDataAccessObject.insertActivity(activity);
        int activityId = activityDataAccessObject.getActivityByName("Para Actualizar");
        ActivityDTO updatedActivity = new ActivityDTO(String.valueOf(activityId), "Actualizada");
        boolean wasUpdated = activityDataAccessObject.updateActivity(updatedActivity);
        assertTrue(wasUpdated, "La actividad debería actualizarse correctamente.");
        ActivityDTO activityUpdated = activityDataAccessObject.searchActivityById(String.valueOf(activityId));
        assertEquals("Actualizada", activityUpdated.getActivityName());
    }

    @Test
    void testGetNonExistentActivity() throws SQLException, IOException {
        ActivityDTO activity = activityDataAccessObject.searchActivityById("99999");
        assertEquals("invalido", activity.getActivityId(), "No debería encontrar una actividad inexistente.");
    }

    @Test
    void testGetAllActivities() throws SQLException, IOException {
        activityDataAccessObject.insertActivity(new ActivityDTO(null, "Otra Actividad"));
        var activities = activityDataAccessObject.getAllActivities();
        assertEquals(2, activities.size(), "Debe haber dos actividades en la base de datos.");
    }

    @Test
    void testGetActivityByNameNonExistent() throws SQLException, IOException {
        int activityId = activityDataAccessObject.getActivityByName("NoExiste");
        assertEquals(-1, activityId, "Debe devolver -1 si la actividad no existe.");
    }

    @Test
    void testInsertActivityWithExistingId() throws SQLException, IOException {
        int existingActivityId = activityDataAccessObject.getActivityByName("Actividad Base");
        ActivityDTO duplicateActivity = new ActivityDTO(String.valueOf(existingActivityId), "Duplicada");
        assertThrows(SQLException.class, () -> activityDataAccessObject.insertActivity(duplicateActivity),
                "Debe lanzar excepción al insertar con id existente.");
    }

    @Test
    void testUpdateNonExistentActivity() throws SQLException, IOException {
        ActivityDTO nonExistentActivity = new ActivityDTO("99999", "NoExiste");
        boolean wasUpdated = activityDataAccessObject.updateActivity(nonExistentActivity);
        assertFalse(wasUpdated, "No debe actualizar una actividad inexistente.");
    }

    @Test
    void testDeleteNonExistentActivity() throws SQLException, IOException {
        ActivityDTO nonExistentActivity = new ActivityDTO("99999", null);
        boolean wasDeleted = activityDataAccessObject.deleteActivity(nonExistentActivity);
        assertFalse(wasDeleted, "No debe eliminar una actividad inexistente.");
    }
}