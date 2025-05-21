package data_access.DAO;

import logic.DAO.ActivityScheduleDAO;
import logic.DTO.ActivityScheduleDTO;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActivityScheduleDAOTest {

    private Connection connection;
    private ActivityScheduleDAO activityScheduleDAO;

    private int idScheduleBase;
    private int idActivityBase;

    @BeforeAll
    void setUpAll() throws Exception {
        connection = new data_access.ConecctionDataBase().connectDB();
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
        idScheduleBase = 1;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO cronograma_de_actividades (idCronograma, hito, fechaEstimada, matricula, idEvidencia) VALUES (?, ?, ?, ?, ?)")) {
            ps.setInt(1, idScheduleBase);
            ps.setString(2, "Milestone 1");
            ps.setDate(3, Date.valueOf(LocalDate.now().plusDays(15)));
            ps.setString(4, studentId);
            ps.setInt(5, evidenceId);
            ps.executeUpdate();
        }

        // 6. Insert activity
        idActivityBase = 1;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO actividad (idActividad, nombreActividad) VALUES (?, ?)")) {
            ps.setInt(1, idActivityBase);
            ps.setString(2, "Activity 1");
            ps.executeUpdate();
        }
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
        assertTrue(deleted, "The scheduled activity should be deleted correctly.");
    }

    @Test
    void testUpdateActivitySchedule() throws SQLException {
        ActivityScheduleDTO oldActivitySchedule = new ActivityScheduleDTO(idScheduleBase, idActivityBase);
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
}

