package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.ScheduleOfActivitiesDAO;
import logic.DTO.ScheduleOfActivitiesDTO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleOfActivitiesDAOTest {

    private static ConecctionDataBase connectionDB;
    private static Connection connection;
    private ScheduleOfActivitiesDAO scheduleOfActivitiesDAO;

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
        scheduleOfActivitiesDAO = new ScheduleOfActivitiesDAO();
        try {
            // Limpia la tabla antes de cada prueba
            connection.prepareStatement("DELETE FROM cronograma_de_actividades").executeUpdate();
        } catch (SQLException e) {
            fail("Error al limpiar la tabla cronograma_de_actividades: " + e.getMessage());
        }
    }

    @Test
    void testInsertScheduleOfActivities() {
        try {
            ScheduleOfActivitiesDTO schedule = new ScheduleOfActivitiesDTO(
                    "1", "Hito 1", new Timestamp(System.currentTimeMillis()), "11113", "1"
            );
            boolean result = scheduleOfActivitiesDAO.insertScheduleOfActivities(schedule, connection);
            assertTrue(result, "La inserción debería ser exitosa");

            ScheduleOfActivitiesDTO insertedSchedule = scheduleOfActivitiesDAO.searchScheduleOfActivitiesById("1", connection);
            assertNotNull(insertedSchedule, "El cronograma debería existir en la base de datos");
            assertEquals("Hito 1", insertedSchedule.getMilestone(), "El hito debería coincidir");
        } catch (SQLException e) {
            fail("Error en testInsertScheduleOfActivities: " + e.getMessage());
        }
    }

    @Test
    void testSearchScheduleOfActivitiesById() {
        try {
            ScheduleOfActivitiesDTO schedule = new ScheduleOfActivitiesDTO(
                    "2", "Hito 2", new Timestamp(System.currentTimeMillis()), "54331", "2"
            );
            scheduleOfActivitiesDAO.insertScheduleOfActivities(schedule, connection);

            ScheduleOfActivitiesDTO retrievedSchedule = scheduleOfActivitiesDAO.searchScheduleOfActivitiesById("2", connection);
            assertNotNull(retrievedSchedule, "Debería encontrar el cronograma");
            assertEquals("Hito 2", retrievedSchedule.getMilestone(), "El hito debería coincidir");
        } catch (SQLException e) {
            fail("Error en testSearchScheduleOfActivitiesById: " + e.getMessage());
        }
    }

    @Test
    void testUpdateScheduleOfActivities() {
        try {
            ScheduleOfActivitiesDTO schedule = new ScheduleOfActivitiesDTO(
                    "3", "Hito 3", new Timestamp(System.currentTimeMillis()), "67892", "4"
            );
            scheduleOfActivitiesDAO.insertScheduleOfActivities(schedule, connection);

            ScheduleOfActivitiesDTO updatedSchedule = new ScheduleOfActivitiesDTO(
                    "3", "Hito Actualizado", new Timestamp(System.currentTimeMillis()), "67892", "4"
            );
            boolean result = scheduleOfActivitiesDAO.updateScheduleOfActivities(updatedSchedule, connection);
            assertTrue(result, "La actualización debería ser exitosa");

            ScheduleOfActivitiesDTO retrievedSchedule = scheduleOfActivitiesDAO.searchScheduleOfActivitiesById("3", connection);
            assertNotNull(retrievedSchedule, "El cronograma debería existir después de actualizar");
            assertEquals("Hito Actualizado", retrievedSchedule.getMilestone(), "El hito debería actualizarse");
        } catch (SQLException e) {
            fail("Error en testUpdateScheduleOfActivities: " + e.getMessage());
        }
    }

    @Test
    void testDeleteScheduleOfActivities() {
        try {
            ScheduleOfActivitiesDTO schedule = new ScheduleOfActivitiesDTO(
                    "4", "Hito Eliminar", new Timestamp(System.currentTimeMillis()), "12351", "5"
            );
            scheduleOfActivitiesDAO.insertScheduleOfActivities(schedule, connection);

            boolean result = scheduleOfActivitiesDAO.deleteScheduleOfActivities(schedule, connection);
            assertTrue(result, "La eliminación debería ser exitosa");

            ScheduleOfActivitiesDTO deletedSchedule = scheduleOfActivitiesDAO.searchScheduleOfActivitiesById("4", connection);
            assertEquals("N/A", deletedSchedule.getIdSchedule(), "El cronograma eliminado no debería existir");
        } catch (SQLException e) {
            fail("Error en testDeleteScheduleOfActivities: " + e.getMessage());
        }
    }

    @Test
    void testGetAllSchedulesOfActivities() {
        try {
            ScheduleOfActivitiesDTO schedule1 = new ScheduleOfActivitiesDTO(
                    "5", "Hito Lista 1", new Timestamp(System.currentTimeMillis()), "11113", "6"
            );
            ScheduleOfActivitiesDTO schedule2 = new ScheduleOfActivitiesDTO(
                    "6", "Hito Lista 2", new Timestamp(System.currentTimeMillis()), "54331", "7"
            );
            scheduleOfActivitiesDAO.insertScheduleOfActivities(schedule1, connection);
            scheduleOfActivitiesDAO.insertScheduleOfActivities(schedule2, connection);

            List<ScheduleOfActivitiesDTO> schedules = scheduleOfActivitiesDAO.getAllSchedulesOfActivities(connection);
            assertNotNull(schedules, "La lista no debería ser nula");
            assertTrue(schedules.size() >= 2, "Deberían existir al menos dos cronogramas en la lista");
        } catch (SQLException e) {
            fail("Error en testGetAllSchedulesOfActivities: " + e.getMessage());
        }
    }
}