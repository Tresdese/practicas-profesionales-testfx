package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.ScheduleOfActivitiesDAO;
import logic.DTO.ScheduleOfActivitiesDTO;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ScheduleOfActivitiesDAOTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private ScheduleOfActivitiesDAO scheduleOfActivitiesDAO;
    private int baseUserId;
    private String basePeriodId;
    private String baseNrc;
    private String baseTuition;
    private String baseEvidenceId;

    @BeforeAll
    void setUpAll() throws SQLException {
        connectionDB = new ConnectionDataBase();
        connection = connectionDB.connectDB();
        clearTablesAndResetAutoIncrement();
        createBaseRecords();
        scheduleOfActivitiesDAO = new ScheduleOfActivitiesDAO();
    }

    @AfterAll
    void tearDownAll() throws SQLException {
        clearTablesAndResetAutoIncrement();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        connectionDB.close();
    }

    @BeforeEach
    void setUp() throws SQLException {
        clearTablesAndResetAutoIncrement();
        createBaseRecords();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM cronograma_de_actividades");
            stmt.execute("ALTER TABLE cronograma_de_actividades AUTO_INCREMENT = 1");
            stmt.execute("DELETE FROM estudiante");
            stmt.execute("DELETE FROM grupo");
            stmt.execute("DELETE FROM periodo");
            stmt.execute("DELETE FROM evidencia");
            stmt.execute("ALTER TABLE evidencia AUTO_INCREMENT = 1");
            stmt.execute("DELETE FROM usuario");
            stmt.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        }
    }

    private void createBaseRecords() throws SQLException {
        // Insert base user
        String userSql = "INSERT INTO usuario (numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, 1001);
            ps.setString(2, "Academico");
            ps.setString(3, "Prueba");
            ps.setString(4, "academico1");
            ps.setString(5, "password123");
            ps.setString(6, "Academico");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    baseUserId = rs.getInt(1);
                }
            }
        }
        // Insert base period
        String periodSql = "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (?, ?, ?, ?)";
        basePeriodId = "1";
        try (PreparedStatement ps = connection.prepareStatement(periodSql)) {
            ps.setString(1, basePeriodId);
            ps.setString(2, "Periodo Base");
            ps.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            ps.executeUpdate();
        }
        // Insert base group
        String groupSql = "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)";
        baseNrc = "101";
        try (PreparedStatement ps = connection.prepareStatement(groupSql)) {
            ps.setString(1, baseNrc);
            ps.setString(2, "Grupo Base");
            ps.setInt(3, baseUserId);
            ps.setString(4, basePeriodId);
            ps.executeUpdate();
        }
        // Insert base student
        String studentSql = "INSERT INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio, calificacionFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        baseTuition = "A12345678";
        try (PreparedStatement ps = connection.prepareStatement(studentSql)) {
            ps.setString(1, baseTuition);
            ps.setInt(2, 1);
            ps.setString(3, "Juan");
            ps.setString(4, "Pérez");
            ps.setString(5, "1234567890");
            ps.setString(6, "juan.perez@example.com");
            ps.setString(7, "juanperez");
            ps.setString(8, "password123");
            ps.setString(9, baseNrc);
            ps.setInt(10, 50);
            ps.setDouble(11, 85.5);
            ps.executeUpdate();
        }
        // Insert base evidence
        String evidenceSql = "INSERT INTO evidencia (nombreEvidencia, fechaEntrega, ruta) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(evidenceSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Evidencia Base");
            ps.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            ps.setString(3, "/ruta/evidencia/base");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    baseEvidenceId = String.valueOf(rs.getInt(1));
                }
            }
        }
    }

    @Test
    void testInsertScheduleOfActivities() {
        try {
            ScheduleOfActivitiesDTO schedule = new ScheduleOfActivitiesDTO(
                    "1", "Hito 1", new Timestamp(System.currentTimeMillis()), baseTuition, baseEvidenceId
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
                    "2", "Hito 2", new Timestamp(System.currentTimeMillis()), baseTuition, baseEvidenceId
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
                    "3", "Hito 3", new Timestamp(System.currentTimeMillis()), baseTuition, baseEvidenceId
            );
            scheduleOfActivitiesDAO.insertScheduleOfActivities(schedule, connection);

            ScheduleOfActivitiesDTO updatedSchedule = new ScheduleOfActivitiesDTO(
                    "3", "Hito Actualizado", new Timestamp(System.currentTimeMillis()), baseTuition, baseEvidenceId
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
                    "4", "Hito 4", new Timestamp(System.currentTimeMillis()), baseTuition, baseEvidenceId
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
                    "5", "Hito 5", new Timestamp(System.currentTimeMillis()), baseTuition, baseEvidenceId
            );
            ScheduleOfActivitiesDTO schedule2 = new ScheduleOfActivitiesDTO(
                    "6", "Hito 6", new Timestamp(System.currentTimeMillis()), baseTuition, baseEvidenceId
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

    @Test
    void testInsertScheduleOfActivities_Duplicate() {
        try {
            ScheduleOfActivitiesDTO schedule = new ScheduleOfActivitiesDTO(
                    "7", "Hito 7", new Timestamp(System.currentTimeMillis()), baseTuition, baseEvidenceId
            );
            assertTrue(scheduleOfActivitiesDAO.insertScheduleOfActivities(schedule, connection));
            assertThrows(SQLException.class, () -> {
                scheduleOfActivitiesDAO.insertScheduleOfActivities(schedule, connection);
            });
        } catch (SQLException e) {
            fail("Error en testInsertScheduleOfActivities_Duplicate: " + e.getMessage());
        }
    }

    @Test
    void testUpdateScheduleOfActivities_NotExists() {
        try {
            ScheduleOfActivitiesDTO schedule = new ScheduleOfActivitiesDTO(
                    "999", "Hito Inexistente", new Timestamp(System.currentTimeMillis()), baseTuition, baseEvidenceId
            );
            boolean result = scheduleOfActivitiesDAO.updateScheduleOfActivities(schedule, connection);
            assertFalse(result, "No debería actualizar un cronograma inexistente");
        } catch (SQLException e) {
            fail("Error en testUpdateScheduleOfActivities_NotExists: " + e.getMessage());
        }
    }

    @Test
    void testDeleteScheduleOfActivities_NotExists() {
        try {
            ScheduleOfActivitiesDTO schedule = new ScheduleOfActivitiesDTO(
                    "888", "Hito Inexistente", new Timestamp(System.currentTimeMillis()), baseTuition, baseEvidenceId
            );
            boolean result = scheduleOfActivitiesDAO.deleteScheduleOfActivities(schedule, connection);
            assertFalse(result, "No debería eliminar un cronograma inexistente");
        } catch (SQLException e) {
            fail("Error en testDeleteScheduleOfActivities_NotExists: " + e.getMessage());
        }
    }

    @Test
    void testSearchScheduleOfActivitiesById_NotExists() {
        try {
            ScheduleOfActivitiesDTO schedule = scheduleOfActivitiesDAO.searchScheduleOfActivitiesById("777", connection);
            assertEquals("N/A", schedule.getIdSchedule(), "El cronograma no existente debe devolver valores por defecto");
        } catch (SQLException e) {
            fail("Error en testSearchScheduleOfActivitiesById_NotExists: " + e.getMessage());
        }
    }

    @Test
    void testGetAllSchedulesOfActivities_EmptyTable() {
        try {
            clearTablesAndResetAutoIncrement();
            List<ScheduleOfActivitiesDTO> schedules = scheduleOfActivitiesDAO.getAllSchedulesOfActivities(connection);
            assertNotNull(schedules, "La lista no debe ser nula");
            assertEquals(0, schedules.size(), "La lista debe estar vacía si no hay cronogramas");
        } catch (SQLException e) {
            fail("Error en testGetAllSchedulesOfActivities_EmptyTable: " + e.getMessage());
        }
    }

    @Test
    void testInsertScheduleOfActivities_NullOrInvalidData() {
        assertThrows(SQLException.class, () -> {
            ScheduleOfActivitiesDTO schedule = new ScheduleOfActivitiesDTO(
                    null, null, null, null, null
            );
            scheduleOfActivitiesDAO.insertScheduleOfActivities(schedule, connection);
        }, "No debería permitir insertar cronograma con datos nulos");
    }

    @Test
    void testInsertScheduleOfActivities_InvalidForeignKeys() {
        assertThrows(SQLException.class, () -> {
            ScheduleOfActivitiesDTO schedule = new ScheduleOfActivitiesDTO(
                    "20", "Hito FK", new Timestamp(System.currentTimeMillis()), "NO_EXISTE", "99999"
            );
            scheduleOfActivitiesDAO.insertScheduleOfActivities(schedule, connection);
        }, "No debería permitir insertar cronograma con FK inválidas");
    }
}