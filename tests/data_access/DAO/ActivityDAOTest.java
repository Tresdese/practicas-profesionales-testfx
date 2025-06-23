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

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private ActivityDAO activityDAO;
    private int idActividadBase;

    @BeforeAll
    void setUpAll() throws SQLException, IOException {
        connectionDB = new ConnectionDataBase();
        connection = connectionDB.connectDB();
        limpiarTablaYResetearAutoIncrement();
        activityDAO = new ActivityDAO();
        crearActividadBase();
    }

    @AfterAll
    void tearDownAll() throws SQLException {
        limpiarTablaYResetearAutoIncrement();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @BeforeEach
    void setUp() throws SQLException, IOException {
        limpiarTablaYResetearAutoIncrement();
        crearActividadBase();
    }

    private void limpiarTablaYResetearAutoIncrement() throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM actividad")) {
            stmt.executeUpdate();
        }
        try (PreparedStatement stmt = connection.prepareStatement("ALTER TABLE actividad AUTO_INCREMENT = 1")) {
            stmt.executeUpdate();
        }
    }

    private void crearActividadBase() throws SQLException, IOException {
        String sql = "INSERT INTO actividad (nombreActividad) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Actividad Base");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    idActividadBase = rs.getInt(1);
                }
            }
        }
    }

    @Test
    void testInsertActivitySuccess() throws SQLException, IOException {
        ActivityDTO activity = new ActivityDTO(null, "Nueva Actividad");
        boolean inserted = activityDAO.insertActivity(activity);
        assertTrue(inserted, "La actividad debería insertarse correctamente.");
    }

    @Test
    void testGetActivityById() throws SQLException, IOException {
        ActivityDTO activity = activityDAO.searchActivityById(String.valueOf(idActividadBase));
        assertNotNull(activity, "La actividad no debería ser nula.");
        assertEquals("Actividad Base", activity.getActivityName());
    }

    @Test
    void testDeleteActivity() throws SQLException, IOException {
        ActivityDTO activity = new ActivityDTO(null, "Para Eliminar");
        activityDAO.insertActivity(activity);
        int id = activityDAO.getActivityByName("Para Eliminar");
        boolean deleted = activityDAO.deleteActivity(new ActivityDTO(String.valueOf(id), null));
        assertTrue(deleted, "La actividad debería eliminarse correctamente.");
    }

    @Test
    void testUpdateActivity() throws SQLException, IOException {
        ActivityDTO activity = new ActivityDTO(null, "Para Actualizar");
        activityDAO.insertActivity(activity);
        int id = activityDAO.getActivityByName("Para Actualizar");
        ActivityDTO updated = new ActivityDTO(String.valueOf(id), "Actualizada");
        boolean result = activityDAO.updateActivity(updated);
        assertTrue(result, "La actividad debería actualizarse correctamente.");
        ActivityDTO activityUpdated = activityDAO.searchActivityById(String.valueOf(id));
        assertEquals("Actualizada", activityUpdated.getActivityName());
    }

    @Test
    void testGetNonExistentActivity() throws SQLException, IOException {
        ActivityDTO activity = activityDAO.searchActivityById("99999");
        assertEquals("invalido", activity.getActivityId(), "No debería encontrar una actividad inexistente.");
    }

    @Test
    void testGetAllActivities() throws SQLException, IOException {
        activityDAO.insertActivity(new ActivityDTO(null, "Otra Actividad"));
        var actividades = activityDAO.getAllActivities();
        assertEquals(2, actividades.size(), "Debe haber dos actividades en la base de datos.");
    }

    @Test
    void testGetActivityByNameNoExiste() throws SQLException, IOException {
        int id = activityDAO.getActivityByName("NoExiste");
        assertEquals(-1, id, "Debe devolver -1 si la actividad no existe.");
    }

    @Test
    void testInsertActivityConIdExistente() throws SQLException, IOException {
        int id = activityDAO.getActivityByName("Actividad Base");
        ActivityDTO actividadDuplicada = new ActivityDTO(String.valueOf(id), "Duplicada");
        assertThrows(SQLException.class, () -> activityDAO.insertActivity(actividadDuplicada),
                "Debe lanzar excepción al insertar con id existente.");
    }

    @Test
    void testUpdateActivityNoExistente() throws SQLException, IOException {
        ActivityDTO actividadNoExiste = new ActivityDTO("99999", "NoExiste");
        boolean actualizado = activityDAO.updateActivity(actividadNoExiste);
        assertFalse(actualizado, "No debe actualizar una actividad inexistente.");
    }

    @Test
    void testDeleteActivityNoExistente() throws SQLException, IOException {
        ActivityDTO actividadNoExiste = new ActivityDTO("99999", null);
        boolean eliminado = activityDAO.deleteActivity(actividadNoExiste);
        assertFalse(eliminado, "No debe eliminar una actividad inexistente.");
    }
}