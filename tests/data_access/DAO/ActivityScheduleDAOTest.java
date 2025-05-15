package data_access.DAO;

import logic.DAO.ActivityScheduleDAO;
import logic.DTO.ActivityScheduleDTO;
import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActivityScheduleDAOTest {

    private Connection connection;
    private ActivityScheduleDAO activityScheduleDAO;

    // IDs base para pruebas
    private String idScheduleBase;
    private String idActivityBase;

    @BeforeAll
    void setUpAll() throws SQLException {
        connection = new data_access.ConecctionDataBase().connectDB();
        activityScheduleDAO = new ActivityScheduleDAO();
        limpiarTablasYResetearAutoIncrement();
        crearRegistrosBase();
    }

    @BeforeEach
    void setUp() throws SQLException {
        limpiarTablasYResetearAutoIncrement();
        crearRegistrosBase();
    }

    @AfterAll
    void tearDownAll() throws SQLException {
        limpiarTablasYResetearAutoIncrement();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private void limpiarTablasYResetearAutoIncrement() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM actividad_programada");
            stmt.execute("ALTER TABLE actividad_programada AUTO_INCREMENT = 1");
        }
    }

    private void crearRegistrosBase() throws SQLException {
        // Insertar idHorario (simulado como string "1")
        idScheduleBase = "1";
        // Insertar idActividad (simulado como string "1")
        idActivityBase = "1";
        // Si necesitas insertar en tablas relacionadas, hazlo aquí
    }

    @Test
    void testInsertActivitySchedule() throws SQLException {
        ActivityScheduleDTO activitySchedule = new ActivityScheduleDTO(idScheduleBase, idActivityBase);
        boolean inserted = activityScheduleDAO.insertActivitySchedule(activitySchedule, connection);
        assertTrue(inserted, "La actividad programada debería insertarse correctamente.");
    }

    @Test
    void testSearchActivityScheduleById() throws SQLException {
        ActivityScheduleDTO activitySchedule = new ActivityScheduleDTO(idScheduleBase, idActivityBase);
        activityScheduleDAO.insertActivitySchedule(activitySchedule, connection);

        ActivityScheduleDTO searchCriteria = new ActivityScheduleDTO(idScheduleBase, idActivityBase);
        ActivityScheduleDTO result = activityScheduleDAO.searchActivityScheduleByIdScheduleAndIdActivity(searchCriteria, connection);

        assertNotNull(result, "La actividad programada no debería ser nula.");
        assertEquals(idScheduleBase, result.getIdSchedule());
        assertEquals(idActivityBase, result.getIdActivity());
    }

    @Test
    void testDeleteActivitySchedule() throws SQLException {
        ActivityScheduleDTO activitySchedule = new ActivityScheduleDTO(idScheduleBase, idActivityBase);
        activityScheduleDAO.insertActivitySchedule(activitySchedule, connection);

        boolean deleted = activityScheduleDAO.deleteActivitySchedule(activitySchedule, connection);
        assertTrue(deleted, "La actividad programada debería eliminarse correctamente.");
    }

    @Test
    void testUpdateActivitySchedule() throws SQLException {
        ActivityScheduleDTO oldActivitySchedule = new ActivityScheduleDTO(idScheduleBase, idActivityBase);
        activityScheduleDAO.insertActivitySchedule(oldActivitySchedule, connection);

        ActivityScheduleDTO newActivitySchedule = new ActivityScheduleDTO("2", "2");
        boolean updated = activityScheduleDAO.updateActivitySchedule(oldActivitySchedule, newActivitySchedule, connection);
        assertTrue(updated, "La actividad programada debería actualizarse correctamente.");
    }
}