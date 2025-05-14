package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.ActivityDAO;
import logic.DTO.ActivityDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ActivityDAOTest {

    private static final Logger logger = LogManager.getLogger(ActivityDAOTest.class);

    private static ConecctionDataBase connectionDB;
    private static Connection connection;

    private ActivityDAO activityDAO;

    @BeforeAll
    static void setUpClass() {
        connectionDB = new ConecctionDataBase();
        try {
            connection = connectionDB.connectDB();
        } catch (SQLException e) {
            fail("No se pudo establecer la conexión con la base de datos: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDownClass() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.error("Error al cerrar la conexión: " + e.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        activityDAO = new ActivityDAO(connection);
        try {
            connection.createStatement().execute("DELETE FROM actividad");
        } catch (SQLException e) {
            fail("No se pudo limpiar la tabla actividad: " + e.getMessage());
        }
    }

    @Test
    void testInsertTestActivitySuccess() throws SQLException {
        ActivityDTO activity = new ActivityDTO(null, "Actividad de prueba");
        boolean inserted = activityDAO.insertActivity(activity);
        assertTrue(inserted, "La actividad debería insertarse correctamente.");
    }

    @Test
    void testInsertTestActivityWithEmptyName() {
        ActivityDTO activity = new ActivityDTO(null, "");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityDAO.insertActivity(activity),
                "Debería lanzar excepción por nombre vacío.");
        assertEquals("El nombre de la actividad no puede ser nulo o vacío.", exception.getMessage());
    }

    @Test
    void testInsertTestActivityWithNullName() {
        ActivityDTO activity = new ActivityDTO(null, null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityDAO.insertActivity(activity),
                "Debería lanzar excepción por nombre nulo.");
        assertEquals("El nombre de la actividad no puede ser nulo o vacío.", exception.getMessage());
    }

    @Test
    void testGetActivityConNullId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityDAO.searchActivityById(null),
                "Debería lanzar excepción al pasar un ID nulo.");
        assertEquals("El ID de la actividad no puede ser nulo o vacío.", exception.getMessage());
    }

    @Test
    void testGetNonExistentActivity() throws SQLException {
        ActivityDTO activity = activityDAO.searchActivityById("999");
        assertEquals("invalido", activity.getActivityId(), "No debería encontrar una actividad inexistente.");
    }

    @Test
    void testDeleteActivityFunctionality() throws SQLException {
        ActivityDTO activity = new ActivityDTO(null, "Actividad para eliminar");
        activityDAO.insertActivity(activity);

        int id = activityDAO.getActivityByName("Actividad para eliminar");
        ActivityDTO before = activityDAO.searchActivityById(String.valueOf(id));
        assertNotNull(before, "La actividad debería existir antes de eliminarla.");

        boolean deleted = activityDAO.deleteActivity(new ActivityDTO(String.valueOf(id), null));
        assertTrue(deleted, "La eliminación debería ser exitosa.");

        ActivityDTO after = activityDAO.searchActivityById(String.valueOf(id));
        assertEquals("invalido", after.getActivityId(), "La actividad eliminada no debería existir.");
    }
}