package data_access.DAO;

import logic.DAO.ActivityScheduleDAO;
import logic.DTO.ActivityScheduleDTO;
import org.junit.jupiter.api.*;
import data_access.ConnectionDataBase;

import java.sql.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActivityScheduleDAOTest {

    private Connection connection;
    private ActivityScheduleDAO activityScheduleDAO;

    private int baseScheduleId;
    private int baseActivityId;

    @BeforeAll
    void setUpAll() throws Exception {
        connection = new ConnectionDataBase().connectDB();
        activityScheduleDAO = new ActivityScheduleDAO();
        clearTablesAndResetAutoIncrement();
        createBaseRecords();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createBaseRecords();
    }

    @AfterAll
    void tearDownAll() throws Exception {
        clearTablesAndResetAutoIncrement();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM cronograma_actividad");
            stmt.execute("DELETE FROM cronograma_de_actividades");
            stmt.execute("DELETE FROM actividad");
            stmt.execute("DELETE FROM evidencia");
            stmt.execute("DELETE FROM estudiante");
            stmt.execute("DELETE FROM grupo");
            stmt.execute("DELETE FROM periodo");
            stmt.execute("ALTER TABLE cronograma_de_actividades AUTO_INCREMENT = 1");
            stmt.execute("ALTER TABLE actividad AUTO_INCREMENT = 1");
            stmt.execute("ALTER TABLE evidencia AUTO_INCREMENT = 1");
            stmt.execute("ALTER TABLE periodo AUTO_INCREMENT = 1");
        }
    }

    private void createBaseRecords() throws SQLException {
        // 1. Insert period
        int periodId = 1;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, periodId);
            ps.setString(2, "2025-1");
            ps.setDate(3, Date.valueOf(LocalDate.now()));
            ps.setDate(4, Date.valueOf(LocalDate.now().plusMonths(4)));
            ps.executeUpdate();
        }

        // 2. Insert group
        int groupId = 1;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, groupId);
            ps.setString(2, "Group 1");
            ps.setNull(3, Types.INTEGER);
            ps.setInt(4, periodId);
            ps.executeUpdate();
        }

        // 3. Insert student
        String studentId = "A0001";
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio, calificacionFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, studentId);
            ps.setInt(2, 1);
            ps.setString(3, "John");
            ps.setString(4, "Doe");
            ps.setString(5, "1234567890");
            ps.setString(6, "john@mail.com");
            ps.setString(7, "johnuser");
            ps.setString(8, "1234567890123456789012345678901234567890123456789012345678901234");
            ps.setInt(9, groupId);
            ps.setInt(10, 100);
            ps.setDouble(11, 95.5);
            ps.executeUpdate();
        }

        // 4. Insert evidence
        int evidenceId = 1;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO evidencia (idEvidencia, nombreEvidencia, fechaEntrega, ruta) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, evidenceId);
            ps.setString(2, "Evidence 1");
            ps.setDate(3, Date.valueOf(LocalDate.now().plusDays(10)));
            ps.setString(4, "/path/evidence1.pdf");
            ps.executeUpdate();
        }

        // 5. Insert schedule of activities
        baseScheduleId = 1;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO cronograma_de_actividades (idCronograma, hito, fechaEstimada, matricula, idEvidencia) VALUES (?, ?, ?, ?, ?)")) {
            ps.setInt(1, baseScheduleId);
            ps.setString(2, "Milestone 1");
            ps.setDate(3, Date.valueOf(LocalDate.now().plusDays(15)));
            ps.setString(4, studentId);
            ps.setInt(5, evidenceId);
            ps.executeUpdate();
        }

        // 6. Insert activity
        baseActivityId = 1;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO actividad (idActividad, nombreActividad) VALUES (?, ?)")) {
            ps.setInt(1, baseActivityId);
            ps.setString(2, "Activity 1");
            ps.executeUpdate();
        }
    }

    @Test
    void testInsertActivitySchedule() throws SQLException {
        ActivityScheduleDTO activitySchedule = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        boolean inserted = activityScheduleDAO.insertActivitySchedule(activitySchedule, connection);
        assertTrue(inserted, "The scheduled activity should be inserted correctly.");
    }

    @Test
    void testSearchActivityScheduleById() throws SQLException {
        ActivityScheduleDTO activitySchedule = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        activityScheduleDAO.insertActivitySchedule(activitySchedule, connection);

        ActivityScheduleDTO searchCriteria = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        ActivityScheduleDTO result = activityScheduleDAO.searchActivityScheduleByIdScheduleAndIdActivity(searchCriteria, connection);

        assertNotNull(result, "The scheduled activity should not be null.");
        assertEquals(baseScheduleId, result.getIdSchedule());
        assertEquals(baseActivityId, result.getIdActivity());
    }

    @Test
    void testDeleteActivitySchedule() throws SQLException {
        ActivityScheduleDTO activitySchedule = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        activityScheduleDAO.insertActivitySchedule(activitySchedule, connection);

        boolean deleted = activityScheduleDAO.deleteActivitySchedule(activitySchedule, connection);
        assertTrue(deleted, "The scheduled activity should be deleted correctly.");
    }

    @Test
    void testUpdateActivitySchedule() throws SQLException {
        ActivityScheduleDTO oldActivitySchedule = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        activityScheduleDAO.insertActivitySchedule(oldActivitySchedule, connection);

        // Insert new dependent records for the update
        int newActivityId = 2;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO actividad (idActividad, nombreActividad) VALUES (?, ?)")) {
            ps.setInt(1, newActivityId);
            ps.setString(2, "Activity 2");
            ps.executeUpdate();
        }
        int newScheduleId = 2;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO cronograma_de_actividades (idCronograma, hito, fechaEstimada, matricula, idEvidencia) VALUES (?, ?, ?, ?, ?)")) {
            ps.setInt(1, newScheduleId);
            ps.setString(2, "Milestone 2");
            ps.setDate(3, Date.valueOf(LocalDate.now().plusDays(20)));
            ps.setString(4, "A0001");
            ps.setInt(5, 1);
            ps.executeUpdate();
        }

        ActivityScheduleDTO newActivitySchedule = new ActivityScheduleDTO(newScheduleId, newActivityId);
        boolean updated = activityScheduleDAO.updateActivitySchedule(oldActivitySchedule, newActivitySchedule, connection);
        assertTrue(updated, "The scheduled activity should be updated correctly.");
    }

    @Test
    void testInsertDuplicateActivitySchedule() throws SQLException {
        ActivityScheduleDTO activitySchedule = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        assertTrue(activityScheduleDAO.insertActivitySchedule(activitySchedule, connection));
        assertThrows(SQLException.class, () -> {
            activityScheduleDAO.insertActivitySchedule(activitySchedule, connection);
        }, "Should throw an exception for duplicate constraint.");
    }

    @Test
    void testSearchNonExistentActivitySchedule() throws SQLException {
        ActivityScheduleDTO nonExistent = new ActivityScheduleDTO(999, 999);
        ActivityScheduleDTO result = activityScheduleDAO.searchActivityScheduleByIdScheduleAndIdActivity(nonExistent, connection);
        assertNull(result, "Searching for a non-existent record should return null.");
    }

    @Test
    void testDeleteNonExistentActivitySchedule() throws SQLException {
        ActivityScheduleDTO nonExistent = new ActivityScheduleDTO(999, 999);
        boolean deleted = activityScheduleDAO.deleteActivitySchedule(nonExistent, connection);
        assertFalse(deleted, "Deleting a non-existent record should return false.");
    }

    @Test
    void testUpdateNonExistentActivitySchedule() throws SQLException {
        ActivityScheduleDTO oldActivitySchedule = new ActivityScheduleDTO(999, 999);
        ActivityScheduleDTO newActivitySchedule = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        boolean updated = activityScheduleDAO.updateActivitySchedule(oldActivitySchedule, newActivitySchedule, connection);
        assertFalse(updated, "Updating a non-existent record should return false.");
    }

    @Test
    void testGetAllActivitySchedules() throws SQLException {
        ActivityScheduleDTO activitySchedule1 = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        activityScheduleDAO.insertActivitySchedule(activitySchedule1, connection);

        int newActivityId = 2;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO actividad (idActividad, nombreActividad) VALUES (?, ?)")) {
            ps.setInt(1, newActivityId);
            ps.setString(2, "Activity 2");
            ps.executeUpdate();
        }
        ActivityScheduleDTO activitySchedule2 = new ActivityScheduleDTO(baseScheduleId, newActivityId);
        activityScheduleDAO.insertActivitySchedule(activitySchedule2, connection);

        var allSchedules = activityScheduleDAO.getAllActivitySchedules(connection);
        assertEquals(2, allSchedules.size(), "There should be two records in the table.");
    }

    @Test
    void testInsertActivityScheduleWithInvalidData() {
        ActivityScheduleDTO invalidSchedule = new ActivityScheduleDTO(0, 0);
        assertThrows(SQLException.class, () -> {
            activityScheduleDAO.insertActivitySchedule(invalidSchedule, connection);
        }, "Inserting with invalid IDs should throw an exception.");
    }

    @Test
    void testInsertActivityScheduleWithNegativeIds() {
        ActivityScheduleDTO invalidSchedule = new ActivityScheduleDTO(-10, -20);
        assertThrows(SQLException.class, () -> {
            activityScheduleDAO.insertActivitySchedule(invalidSchedule, connection);
        }, "Inserting with negative IDs should throw an exception.");
    }

    @Test
    void testUpdateActivityScheduleWithInvalidData() throws SQLException {
        ActivityScheduleDTO oldActivitySchedule = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        activityScheduleDAO.insertActivitySchedule(oldActivitySchedule, connection);

        ActivityScheduleDTO newActivitySchedule = new ActivityScheduleDTO(0, 0);
        assertThrows(SQLException.class, () -> {
            activityScheduleDAO.updateActivitySchedule(oldActivitySchedule, newActivitySchedule, connection);
        }, "Updating with invalid data should throw an exception.");
    }

    @Test
    void testGetAllActivitySchedulesWhenEmpty() throws Exception {
        clearTablesAndResetAutoIncrement();
        var allSchedules = activityScheduleDAO.getAllActivitySchedules(connection);
        assertTrue(allSchedules.isEmpty(), "The list should be empty if there are no records.");
        createBaseRecords();
    }

    @Test
    void testBulkInsertActivitySchedules() throws SQLException {
        int bulkCount = 3;
        int startId = 100;
        for (int i = 0; i < bulkCount; i++) {
            int actId = startId + i;
            int schId = startId + i;
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO actividad (idActividad, nombreActividad) VALUES (?, ?)")) {
                ps.setInt(1, actId);
                ps.setString(2, "Bulk Activity " + actId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO evidencia (idEvidencia, nombreEvidencia, fechaEntrega, ruta) VALUES (?, ?, ?, ?)")) {
                ps.setInt(1, actId);
                ps.setString(2, "Bulk Evidence " + actId);
                ps.setDate(3, Date.valueOf(LocalDate.now().plusDays(10 + i)));
                ps.setString(4, "/path/bulk" + actId + ".pdf");
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO cronograma_de_actividades (idCronograma, hito, fechaEstimada, matricula, idEvidencia) VALUES (?, ?, ?, ?, ?)")) {
                ps.setInt(1, schId);
                ps.setString(2, "Bulk Milestone " + schId);
                ps.setDate(3, Date.valueOf(LocalDate.now().plusDays(20 + i)));
                ps.setString(4, "A0001");
                ps.setInt(5, actId);
                ps.executeUpdate();
            }
            ActivityScheduleDTO schedule = new ActivityScheduleDTO(schId, actId);
            assertTrue(activityScheduleDAO.insertActivitySchedule(schedule, connection));
        }
        var allSchedules = activityScheduleDAO.getAllActivitySchedules(connection);
        assertTrue(allSchedules.size() >= bulkCount, "There should be at least " + bulkCount + " records in the table.");
    }

    @Test
    void testInsertActivityScheduleWithNonExistentForeignKeys() {
        ActivityScheduleDTO invalidSchedule = new ActivityScheduleDTO(9999, 8888);
        assertThrows(SQLException.class, () -> {
            activityScheduleDAO.insertActivitySchedule(invalidSchedule, connection);
        }, "Inserting with non-existent foreign keys should throw an exception.");
    }

    @Test
    void testOperationWithClosedConnection() throws Exception {
        connection.close();
        ActivityScheduleDTO activitySchedule = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        assertThrows(SQLException.class, () -> {
            activityScheduleDAO.insertActivitySchedule(activitySchedule, connection);
        }, "Operating with a closed connection should throw an exception.");
        connection = new ConnectionDataBase().connectDB();
    }
}