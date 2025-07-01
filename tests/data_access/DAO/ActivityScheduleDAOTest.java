package data_access.DAO;

import logic.DAO.ActivityScheduleDAO;
import logic.DTO.ActivityScheduleDTO;
import org.junit.jupiter.api.*;
import data_access.ConnectionDataBase;

import java.io.IOException;
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
    void setUpAll() throws SQLException, IOException {
        connection = new ConnectionDataBase().connectDataBase();
        activityScheduleDAO = new ActivityScheduleDAO();
        clearTablesAndResetAutoIncrement();
        createBaseRecords();
    }

    @BeforeEach
    void setUp() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
        createBaseRecords();
    }

    @AfterAll
    void tearDownAll() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM cronograma_actividad");
            stmt.execute("ALTER TABLE cronograma_actividad AUTO_INCREMENT = 1");
            stmt.execute("DELETE FROM cronograma_de_actividades");
            stmt.execute("ALTER TABLE cronograma_de_actividades AUTO_INCREMENT = 1");
            stmt.execute("DELETE FROM actividad");
            stmt.execute("ALTER TABLE actividad AUTO_INCREMENT = 1");
            stmt.execute("DELETE FROM evidencia");
            stmt.execute("ALTER TABLE evidencia AUTO_INCREMENT = 1");
            stmt.execute("DELETE FROM estudiante");
            stmt.execute("DELETE FROM grupo");
            stmt.execute("DELETE FROM usuario");
            stmt.execute("DELETE FROM periodo");
            stmt.execute("ALTER TABLE periodo AUTO_INCREMENT = 1");
        }
    }

    private void createBaseRecords() throws SQLException {
        int periodId = 1;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, periodId);
            ps.setString(2, "2025-1");
            ps.setDate(3, Date.valueOf(LocalDate.now()));
            ps.setDate(4, Date.valueOf(LocalDate.now().plusMonths(4)));
            ps.executeUpdate();
        }

        int groupId = 1;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, groupId);
            ps.setString(2, "Group 1");
            ps.setNull(3, Types.INTEGER);
            ps.setInt(4, periodId);
            ps.executeUpdate();
        }

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

        int evidenceId = 1;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO evidencia (idEvidencia, nombreEvidencia, fechaEntrega, ruta) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, evidenceId);
            ps.setString(2, "Evidence 1");
            ps.setDate(3, Date.valueOf(LocalDate.now().plusDays(10)));
            ps.setString(4, "/path/evidence1.pdf");
            ps.executeUpdate();
        }

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

        baseActivityId = 1;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO actividad (idActividad, nombreActividad) VALUES (?, ?)")) {
            ps.setInt(1, baseActivityId);
            ps.setString(2, "Activity 1");
            ps.executeUpdate();
        }
    }

    @Test
    void testInsertActivitySchedule() throws SQLException, IOException {
        ActivityScheduleDTO activitySchedule = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        boolean inserted = activityScheduleDAO.insertActivitySchedule(activitySchedule);
        assertTrue(inserted, "The scheduled activity should be inserted correctly.");
    }

    @Test
    void testSearchActivityScheduleById() throws SQLException, IOException {
        ActivityScheduleDTO activitySchedule = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        activityScheduleDAO.insertActivitySchedule(activitySchedule);

        ActivityScheduleDTO searchCriteria = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        ActivityScheduleDTO result = activityScheduleDAO.searchActivityScheduleByIdScheduleAndIdActivity(searchCriteria);

        assertNotNull(result, "The scheduled activity should not be null.");
        assertEquals(baseScheduleId, result.getIdSchedule());
        assertEquals(baseActivityId, result.getIdActivity());
    }

    @Test
    void testDeleteActivitySchedule() throws SQLException, IOException {
        ActivityScheduleDTO activitySchedule = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        activityScheduleDAO.insertActivitySchedule(activitySchedule);

        boolean deleted = activityScheduleDAO.deleteActivitySchedule(activitySchedule);
        assertTrue(deleted, "The scheduled activity should be deleted correctly.");
    }

    @Test
    void testUpdateActivitySchedule() throws SQLException, IOException {
        ActivityScheduleDTO oldActivitySchedule = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        activityScheduleDAO.insertActivitySchedule(oldActivitySchedule);

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
        boolean updated = activityScheduleDAO.updateActivitySchedule(oldActivitySchedule, newActivitySchedule);
        assertTrue(updated, "The scheduled activity should be updated correctly.");
    }

    @Test
    void testInsertDuplicateActivitySchedule() throws SQLException, IOException {
        ActivityScheduleDTO activitySchedule = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        assertTrue(activityScheduleDAO.insertActivitySchedule(activitySchedule));
        assertThrows(SQLException.class, () -> {
            activityScheduleDAO.insertActivitySchedule(activitySchedule);
        }, "Should throw an exception for duplicate constraint.");
    }

    @Test
    void testSearchNonExistentActivitySchedule() throws SQLException, IOException {
        ActivityScheduleDTO nonExistent = new ActivityScheduleDTO(999, 999);
        ActivityScheduleDTO result = activityScheduleDAO.searchActivityScheduleByIdScheduleAndIdActivity(nonExistent);
        assertNotNull(result, "El resultado no debe ser null.");
        assertEquals(-1, result.getIdSchedule(), "El idSchedule debe ser -1 para un registro inexistente.");
        assertEquals(-1, result.getIdActivity(), "El idActivity debe ser -1 para un registro inexistente.");
    }

    @Test
    void testDeleteNonExistentActivitySchedule() throws SQLException, IOException {
        ActivityScheduleDTO nonExistent = new ActivityScheduleDTO(999, 999);
        boolean deleted = activityScheduleDAO.deleteActivitySchedule(nonExistent);
        assertFalse(deleted, "Deleting a non-existent record should return false.");
    }

    @Test
    void testUpdateNonExistentActivitySchedule() throws SQLException, IOException {
        ActivityScheduleDTO oldActivitySchedule = new ActivityScheduleDTO(999, 999);
        ActivityScheduleDTO newActivitySchedule = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        boolean updated = activityScheduleDAO.updateActivitySchedule(oldActivitySchedule, newActivitySchedule);
        assertFalse(updated, "Updating a non-existent record should return false.");
    }

    @Test
    void testGetAllActivitySchedules() throws SQLException, IOException {
        ActivityScheduleDTO activitySchedule1 = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        activityScheduleDAO.insertActivitySchedule(activitySchedule1);

        int newActivityId = 2;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO actividad (idActividad, nombreActividad) VALUES (?, ?)")) {
            ps.setInt(1, newActivityId);
            ps.setString(2, "Activity 2");
            ps.executeUpdate();
        }
        ActivityScheduleDTO activitySchedule2 = new ActivityScheduleDTO(baseScheduleId, newActivityId);
        activityScheduleDAO.insertActivitySchedule(activitySchedule2);

        var allSchedules = activityScheduleDAO.getAllActivitySchedules();
        assertEquals(2, allSchedules.size(), "There should be two records in the table.");
    }

    @Test
    void testInsertActivityScheduleWithInvalidData() {
        ActivityScheduleDTO invalidSchedule = new ActivityScheduleDTO(0, 0);
        assertThrows(SQLException.class, () -> {
            activityScheduleDAO.insertActivitySchedule(invalidSchedule);
        }, "Inserting with invalid IDs should throw an exception.");
    }

    @Test
    void testInsertActivityScheduleWithNegativeIds() {
        ActivityScheduleDTO invalidSchedule = new ActivityScheduleDTO(-10, -20);
        assertThrows(SQLException.class, () -> {
            activityScheduleDAO.insertActivitySchedule(invalidSchedule);
        }, "Inserting with negative IDs should throw an exception.");
    }

    @Test
    void testUpdateActivityScheduleWithInvalidData() throws SQLException, IOException {
        ActivityScheduleDTO oldActivitySchedule = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        activityScheduleDAO.insertActivitySchedule(oldActivitySchedule);

        ActivityScheduleDTO newActivitySchedule = new ActivityScheduleDTO(0, 0);
        assertThrows(SQLException.class, () -> {
            activityScheduleDAO.updateActivitySchedule(oldActivitySchedule, newActivitySchedule);
        }, "Updating with invalid data should throw an exception.");
    }

    @Test
    void testGetAllActivitySchedulesWhenEmpty() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
        var allSchedules = activityScheduleDAO.getAllActivitySchedules();
        assertTrue(allSchedules.isEmpty(), "The list should be empty if there are no records.");
        createBaseRecords();
    }

    @Test
    void testBulkInsertActivitySchedules() throws SQLException, IOException {
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
            assertTrue(activityScheduleDAO.insertActivitySchedule(schedule));
        }
        var allSchedules = activityScheduleDAO.getAllActivitySchedules();
        assertTrue(allSchedules.size() >= bulkCount, "There should be at least " + bulkCount + " records in the table.");
    }

    @Test
    void testInsertActivityScheduleWithNonExistentForeignKeys() {
        ActivityScheduleDTO invalidSchedule = new ActivityScheduleDTO(9999, 8888);
        assertThrows(SQLException.class, () -> {
            activityScheduleDAO.insertActivitySchedule(invalidSchedule);
        }, "Inserting with non-existent foreign keys should throw an exception.");
    }

    @Test
    void testOperationWithClosedConnection() throws Exception {
        ConnectionDataBase connectionDataBase = new ConnectionDataBase();
        Connection connection = connectionDataBase.connectDataBase();
        connection.close();

        assertThrows(SQLException.class, () -> {
            connection.createStatement();
        }, "Operating with a closed connection should throw an exception.");
    }
}