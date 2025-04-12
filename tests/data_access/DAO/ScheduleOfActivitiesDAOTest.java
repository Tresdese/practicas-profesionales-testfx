package data_access.DAO;

import data_access.ConecctionDataBase;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

import logic.DAO.ScheduleOfActivitiesDAO;
import logic.DTO.ScheduleOfActivitiesDTO;


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
        connectionDB.closeConnection();
    }

    @BeforeEach
    void setUp() {
        scheduleOfActivitiesDAO = new ScheduleOfActivitiesDAO();
    }

    private String insertTestSchedule(String id, String milestone, java.sql.Timestamp estimatedDate, String tuition, String evidenceId) throws SQLException {
        ScheduleOfActivitiesDTO existingSchedule = scheduleOfActivitiesDAO.getScheduleOfActivities(id, connection);
        if (existingSchedule != null) {
            return id;
        }

        String sql = "INSERT INTO cronograma_de_actividades (idCronograma, hito, fechaEstimada, matricula, idEvidencia) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, milestone);
            stmt.setTimestamp(3, estimatedDate);
            stmt.setString(4, tuition);
            stmt.setString(5, evidenceId);
            stmt.executeUpdate();
            return id;
        }
    }

    @Test
    void testInsertScheduleOfActivities() {
        try {
            ScheduleOfActivitiesDTO schedule = new ScheduleOfActivitiesDTO("1", "Hito 1", new java.sql.Timestamp(System.currentTimeMillis()), "11113", "1");
            boolean result = scheduleOfActivitiesDAO.insertScheduleOfActivities(schedule, connection);
            assertTrue(result, "La inserción debería ser exitosa");

            ScheduleOfActivitiesDTO insertedSchedule = scheduleOfActivitiesDAO.getScheduleOfActivities("1", connection);
            assertNotNull(insertedSchedule, "El cronograma debería existir en la base de datos");
            assertEquals("Hito 1", insertedSchedule.getMilestone(), "El hito debería coincidir");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    void testGetScheduleOfActivities() {
        try {
            String id = insertTestSchedule("2", "Hito 2", new java.sql.Timestamp(System.currentTimeMillis()), "54331", "2");

            ScheduleOfActivitiesDTO retrievedSchedule = scheduleOfActivitiesDAO.getScheduleOfActivities(id, connection);
            assertNotNull(retrievedSchedule, "Debería encontrar el cronograma");
            assertEquals(id, retrievedSchedule.getIdSchedule(), "El ID debería coincidir");
            assertEquals("Hito 2", retrievedSchedule.getMilestone(), "El hito debería coincidir");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    void testUpdateScheduleOfActivities() {
        try {
            String id = insertTestSchedule("3", "Hito 3", new java.sql.Timestamp(System.currentTimeMillis()), "67892", "4");

            ScheduleOfActivitiesDTO updatedSchedule = new ScheduleOfActivitiesDTO(id, "Hito Actualizado", new java.sql.Timestamp(System.currentTimeMillis()), "67892", "4");
            boolean updateResult = scheduleOfActivitiesDAO.updateScheduleOfActivities(updatedSchedule, connection);
            assertTrue(updateResult, "La actualización debería ser exitosa");

            ScheduleOfActivitiesDTO retrievedSchedule = scheduleOfActivitiesDAO.getScheduleOfActivities(id, connection);
            assertNotNull(retrievedSchedule, "El cronograma debería existir");
            assertEquals("Hito Actualizado", retrievedSchedule.getMilestone(), "El hito debería actualizarse");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    void testGetAllSchedulesOfActivities() {
        try {
            insertTestSchedule("4", "Hito Prueba", new java.sql.Timestamp(System.currentTimeMillis()), "11113", "5");

            List<ScheduleOfActivitiesDTO> schedules = scheduleOfActivitiesDAO.getAllSchedulesOfActivities(connection);
            assertNotNull(schedules, "La lista no debería ser nula");
            assertFalse(schedules.isEmpty(), "La lista no debería estar vacía");

            boolean found = schedules.stream()
                    .anyMatch(s -> s.getIdSchedule().equals("4"));
            assertTrue(found, "Nuestro cronograma de prueba debería estar en la lista");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    void testDeleteScheduleOfActivities() {
        try {
            String id = insertTestSchedule("5", "Hito Eliminar", new java.sql.Timestamp(System.currentTimeMillis()), "12351", "6");

            ScheduleOfActivitiesDTO before = scheduleOfActivitiesDAO.getScheduleOfActivities(id, connection);
            assertNotNull(before, "El cronograma debería existir antes de eliminarlo");

            boolean deleted = scheduleOfActivitiesDAO.deleteScheduleOfActivities(before, connection);
            assertTrue(deleted, "La eliminación debería ser exitosa");

            ScheduleOfActivitiesDTO after = scheduleOfActivitiesDAO.getScheduleOfActivities(id, connection);
            assertNull(after, "El cronograma no debería existir después de eliminarlo");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }
}