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

    private Connection databaseConnection;
    private ActivityScheduleDAO activityScheduleDataAccessObject;

    private int baseScheduleId;
    private int baseActivityId;

    @BeforeAll
    void setUpAll() throws SQLException, IOException {
        databaseConnection = new ConnectionDataBase().connectDataBase();
        activityScheduleDataAccessObject = new ActivityScheduleDAO();
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
        if (databaseConnection != null && !databaseConnection.isClosed()) {
            databaseConnection.close();
        }
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        try (Statement statement = databaseConnection.createStatement()) {
            statement.execute("DELETE FROM cronograma_actividad");
            statement.execute("ALTER TABLE cronograma_actividad AUTO_INCREMENT = 1");
            statement.execute("DELETE FROM cronograma_de_actividades");
            statement.execute("ALTER TABLE cronograma_de_actividades AUTO_INCREMENT = 1");
            statement.execute("DELETE FROM actividad");
            statement.execute("ALTER TABLE actividad AUTO_INCREMENT = 1");
            statement.execute("DELETE FROM evidencia");
            statement.execute("ALTER TABLE evidencia AUTO_INCREMENT = 1");
            statement.execute("DELETE FROM estudiante");
            statement.execute("DELETE FROM grupo");
            statement.execute("DELETE FROM usuario");
            statement.execute("DELETE FROM periodo");
            statement.execute("ALTER TABLE periodo AUTO_INCREMENT = 1");
        }
    }

    private void createBaseRecords() throws SQLException {
        int periodId = 1;
        try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(
                "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setInt(1, periodId);
            preparedStatement.setString(2, "2025-1");
            preparedStatement.setDate(3, Date.valueOf(LocalDate.now()));
            preparedStatement.setDate(4, Date.valueOf(LocalDate.now().plusMonths(4)));
            preparedStatement.executeUpdate();
        }

        int groupId = 1;
        try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(
                "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setInt(1, groupId);
            preparedStatement.setString(2, "Group 1");
            preparedStatement.setNull(3, Types.INTEGER);
            preparedStatement.setInt(4, periodId);
            preparedStatement.executeUpdate();
        }

        String studentId = "A0001";
        try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(
                "INSERT INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio, calificacionFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, studentId);
            preparedStatement.setInt(2, 1);
            preparedStatement.setString(3, "John");
            preparedStatement.setString(4, "Doe");
            preparedStatement.setString(5, "1234567890");
            preparedStatement.setString(6, "john@mail.com");
            preparedStatement.setString(7, "johnuser");
            preparedStatement.setString(8, "1234567890123456789012345678901234567890123456789012345678901234");
            preparedStatement.setInt(9, groupId);
            preparedStatement.setInt(10, 100);
            preparedStatement.setDouble(11, 95.5);
            preparedStatement.executeUpdate();
        }

        int evidenceId = 1;
        try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(
                "INSERT INTO evidencia (idEvidencia, nombreEvidencia, fechaEntrega, ruta) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setInt(1, evidenceId);
            preparedStatement.setString(2, "Evidence 1");
            preparedStatement.setDate(3, Date.valueOf(LocalDate.now().plusDays(10)));
            preparedStatement.setString(4, "/path/evidence1.pdf");
            preparedStatement.executeUpdate();
        }

        baseScheduleId = 1;
        try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(
                "INSERT INTO cronograma_de_actividades (idCronograma, hito, fechaEstimada, matricula, idEvidencia) VALUES (?, ?, ?, ?, ?)")) {
            preparedStatement.setInt(1, baseScheduleId);
            preparedStatement.setString(2, "Milestone 1");
            preparedStatement.setDate(3, Date.valueOf(LocalDate.now().plusDays(15)));
            preparedStatement.setString(4, studentId);
            preparedStatement.setInt(5, evidenceId);
            preparedStatement.executeUpdate();
        }

        baseActivityId = 1;
        try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(
                "INSERT INTO actividad (idActividad, nombreActividad) VALUES (?, ?)")) {
            preparedStatement.setInt(1, baseActivityId);
            preparedStatement.setString(2, "Activity 1");
            preparedStatement.executeUpdate();
        }
    }

    @Test
    void testInsertActivitySchedule() throws SQLException, IOException {
        ActivityScheduleDTO activitySchedule = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        boolean wasInserted = activityScheduleDataAccessObject.insertActivitySchedule(activitySchedule);
        assertTrue(wasInserted, "The scheduled activity should be inserted correctly.");
    }

    @Test
    void testSearchActivityScheduleById() throws SQLException, IOException {
        ActivityScheduleDTO activitySchedule = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        activityScheduleDataAccessObject.insertActivitySchedule(activitySchedule);

        ActivityScheduleDTO searchCriteria = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        ActivityScheduleDTO result = activityScheduleDataAccessObject.searchActivityScheduleByIdScheduleAndIdActivity(searchCriteria);

        assertNotNull(result, "The scheduled activity should not be null.");
        assertEquals(baseScheduleId, result.getIdSchedule());
        assertEquals(baseActivityId, result.getIdActivity());
    }

    @Test
    void testDeleteActivitySchedule() throws SQLException, IOException {
        ActivityScheduleDTO activitySchedule = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        activityScheduleDataAccessObject.insertActivitySchedule(activitySchedule);

        boolean wasDeleted = activityScheduleDataAccessObject.deleteActivitySchedule(activitySchedule);
        assertTrue(wasDeleted, "The scheduled activity should be deleted correctly.");
    }

    @Test
    void testUpdateActivitySchedule() throws SQLException, IOException {
        ActivityScheduleDTO oldActivitySchedule = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        activityScheduleDataAccessObject.insertActivitySchedule(oldActivitySchedule);

        int newActivityId = 2;
        try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(
                "INSERT INTO actividad (idActividad, nombreActividad) VALUES (?, ?)")) {
            preparedStatement.setInt(1, newActivityId);
            preparedStatement.setString(2, "Activity 2");
            preparedStatement.executeUpdate();
        }
        int newScheduleId = 2;
        try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(
                "INSERT INTO cronograma_de_actividades (idCronograma, hito, fechaEstimada, matricula, idEvidencia) VALUES (?, ?, ?, ?, ?)")) {
            preparedStatement.setInt(1, newScheduleId);
            preparedStatement.setString(2, "Milestone 2");
            preparedStatement.setDate(3, Date.valueOf(LocalDate.now().plusDays(20)));
            preparedStatement.setString(4, "A0001");
            preparedStatement.setInt(5, 1);
            preparedStatement.executeUpdate();
        }

        ActivityScheduleDTO newActivitySchedule = new ActivityScheduleDTO(newScheduleId, newActivityId);
        boolean wasUpdated = activityScheduleDataAccessObject.updateActivitySchedule(oldActivitySchedule, newActivitySchedule);
        assertTrue(wasUpdated, "The scheduled activity should be updated correctly.");
    }

    @Test
    void testInsertDuplicateActivitySchedule() throws SQLException, IOException {
        ActivityScheduleDTO activitySchedule = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        assertTrue(activityScheduleDataAccessObject.insertActivitySchedule(activitySchedule));
        assertThrows(SQLException.class, () -> {
            activityScheduleDataAccessObject.insertActivitySchedule(activitySchedule);
        }, "Should throw an exception for duplicate constraint.");
    }

    @Test
    void testSearchNonExistentActivitySchedule() throws SQLException, IOException {
        ActivityScheduleDTO nonExistent = new ActivityScheduleDTO(999, 999);
        ActivityScheduleDTO result = activityScheduleDataAccessObject.searchActivityScheduleByIdScheduleAndIdActivity(nonExistent);
        assertNotNull(result, "El resultado no debe ser null.");
        assertEquals(-1, result.getIdSchedule(), "El idSchedule debe ser -1 para un registro inexistente.");
        assertEquals(-1, result.getIdActivity(), "El idActivity debe ser -1 para un registro inexistente.");
    }

    @Test
    void testDeleteNonExistentActivitySchedule() throws SQLException, IOException {
        ActivityScheduleDTO nonExistent = new ActivityScheduleDTO(999, 999);
        boolean wasDeleted = activityScheduleDataAccessObject.deleteActivitySchedule(nonExistent);
        assertFalse(wasDeleted, "Deleting a non-existent record should return false.");
    }

    @Test
    void testUpdateNonExistentActivitySchedule() throws SQLException, IOException {
        ActivityScheduleDTO oldActivitySchedule = new ActivityScheduleDTO(999, 999);
        ActivityScheduleDTO newActivitySchedule = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        boolean wasUpdated = activityScheduleDataAccessObject.updateActivitySchedule(oldActivitySchedule, newActivitySchedule);
        assertFalse(wasUpdated, "Updating a non-existent record should return false.");
    }

    @Test
    void testGetAllActivitySchedules() throws SQLException, IOException {
        ActivityScheduleDTO activitySchedule1 = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        activityScheduleDataAccessObject.insertActivitySchedule(activitySchedule1);

        int newActivityId = 2;
        try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(
                "INSERT INTO actividad (idActividad, nombreActividad) VALUES (?, ?)")) {
            preparedStatement.setInt(1, newActivityId);
            preparedStatement.setString(2, "Activity 2");
            preparedStatement.executeUpdate();
        }
        ActivityScheduleDTO activitySchedule2 = new ActivityScheduleDTO(baseScheduleId, newActivityId);
        activityScheduleDataAccessObject.insertActivitySchedule(activitySchedule2);

        var allSchedules = activityScheduleDataAccessObject.getAllActivitySchedules();
        assertEquals(2, allSchedules.size(), "There should be two records in the table.");
    }

    @Test
    void testInsertActivityScheduleWithInvalidData() {
        ActivityScheduleDTO invalidSchedule = new ActivityScheduleDTO(0, 0);
        assertThrows(SQLException.class, () -> {
            activityScheduleDataAccessObject.insertActivitySchedule(invalidSchedule);
        }, "Inserting with invalid IDs should throw an exception.");
    }

    @Test
    void testInsertActivityScheduleWithNegativeIds() {
        ActivityScheduleDTO invalidSchedule = new ActivityScheduleDTO(-10, -20);
        assertThrows(SQLException.class, () -> {
            activityScheduleDataAccessObject.insertActivitySchedule(invalidSchedule);
        }, "Inserting with negative IDs should throw an exception.");
    }

    @Test
    void testUpdateActivityScheduleWithInvalidData() throws SQLException, IOException {
        ActivityScheduleDTO oldActivitySchedule = new ActivityScheduleDTO(baseScheduleId, baseActivityId);
        activityScheduleDataAccessObject.insertActivitySchedule(oldActivitySchedule);

        ActivityScheduleDTO newActivitySchedule = new ActivityScheduleDTO(0, 0);
        assertThrows(SQLException.class, () -> {
            activityScheduleDataAccessObject.updateActivitySchedule(oldActivitySchedule, newActivitySchedule);
        }, "Updating with invalid data should throw an exception.");
    }

    @Test
    void testGetAllActivitySchedulesWhenEmpty() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
        var allSchedules = activityScheduleDataAccessObject.getAllActivitySchedules();
        assertTrue(allSchedules.isEmpty(), "The list should be empty if there are no records.");
        createBaseRecords();
    }

    @Test
    void testBulkInsertActivitySchedules() throws SQLException, IOException {
        int bulkCount = 3;
        int startId = 100;
        for (int i = 0; i < bulkCount; i++) {
            int activityIdX = startId + i;
            int scheduleIdX = startId + i;
            try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(
                    "INSERT INTO actividad (idActividad, nombreActividad) VALUES (?, ?)")) {
                preparedStatement.setInt(1, activityIdX);
                preparedStatement.setString(2, "Bulk Activity " + activityIdX);
                preparedStatement.executeUpdate();
            }
            try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(
                    "INSERT INTO evidencia (idEvidencia, nombreEvidencia, fechaEntrega, ruta) VALUES (?, ?, ?, ?)")) {
                preparedStatement.setInt(1, activityIdX);
                preparedStatement.setString(2, "Bulk Evidence " + activityIdX);
                preparedStatement.setDate(3, Date.valueOf(LocalDate.now().plusDays(10 + i)));
                preparedStatement.setString(4, "/path/bulk" + activityIdX + ".pdf");
                preparedStatement.executeUpdate();
            }
            try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(
                    "INSERT INTO cronograma_de_actividades (idCronograma, hito, fechaEstimada, matricula, idEvidencia) VALUES (?, ?, ?, ?, ?)")) {
                preparedStatement.setInt(1, scheduleIdX);
                preparedStatement.setString(2, "Bulk Milestone " + scheduleIdX);
                preparedStatement.setDate(3, Date.valueOf(LocalDate.now().plusDays(20 + i)));
                preparedStatement.setString(4, "A0001");
                preparedStatement.setInt(5, activityIdX);
                preparedStatement.executeUpdate();
            }
            ActivityScheduleDTO schedule = new ActivityScheduleDTO(scheduleIdX, activityIdX);
            assertTrue(activityScheduleDataAccessObject.insertActivitySchedule(schedule));
        }
        var allSchedules = activityScheduleDataAccessObject.getAllActivitySchedules();
        assertTrue(allSchedules.size() >= bulkCount, "There should be at least " + bulkCount + " records in the table.");
    }

    @Test
    void testInsertActivityScheduleWithNonExistentForeignKeys() {
        ActivityScheduleDTO invalidSchedule = new ActivityScheduleDTO(9999, 8888);
        assertThrows(SQLException.class, () -> {
            activityScheduleDataAccessObject.insertActivitySchedule(invalidSchedule);
        }, "Inserting with non-existent foreign keys should throw an exception.");
    }

    @Test
    void testOperationWithClosedConnection() throws Exception {
        ConnectionDataBase testConnectionDataBase = new ConnectionDataBase();
        Connection closedConnection = testConnectionDataBase.connectDataBase();
        closedConnection.close();

        assertThrows(SQLException.class, () -> {
            closedConnection.createStatement();
        }, "Operating with a closed connection should throw an exception.");
    }
}