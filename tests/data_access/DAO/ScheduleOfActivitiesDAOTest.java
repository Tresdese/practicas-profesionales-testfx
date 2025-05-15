package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.ScheduleOfActivitiesDAO;
import logic.DTO.ScheduleOfActivitiesDTO;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ScheduleOfActivitiesDAOTest {

    private ConecctionDataBase connectionDB;
    private Connection connection;
    private ScheduleOfActivitiesDAO scheduleOfActivitiesDAO;
    private int idUsuarioBase;
    private String idPeriodoBase;
    private String nrcBase;
    private String matriculaBase;
    private String idEvidenciaBase;

    @BeforeAll
    void setUpAll() throws SQLException {
        connectionDB = new ConecctionDataBase();
        connection = connectionDB.connectDB();
        limpiarTablasYResetearAutoIncrement();
        crearRegistrosBase();
        scheduleOfActivitiesDAO = new ScheduleOfActivitiesDAO();
    }

    @AfterAll
    void tearDownAll() throws SQLException {
        limpiarTablasYResetearAutoIncrement();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        connectionDB.close();
    }

    @BeforeEach
    void setUp() throws SQLException {
        limpiarTablasYResetearAutoIncrement();
        crearRegistrosBase();
    }

    private void limpiarTablasYResetearAutoIncrement() throws SQLException {
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

    private void crearRegistrosBase() throws SQLException {
        // Insertar usuario base
        String sqlUsuario = "INSERT INTO usuario (numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, 1001);
            ps.setString(2, "Academico");
            ps.setString(3, "Prueba");
            ps.setString(4, "academico1");
            ps.setString(5, "password123");
            ps.setString(6, "Academico");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    idUsuarioBase = rs.getInt(1);
                }
            }
        }
        // Insertar periodo base
        String sqlPeriodo = "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (?, ?, ?, ?)";
        idPeriodoBase = "1";
        try (PreparedStatement ps = connection.prepareStatement(sqlPeriodo)) {
            ps.setString(1, idPeriodoBase);
            ps.setString(2, "Periodo Base");
            ps.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            ps.executeUpdate();
        }
        // Insertar grupo base
        String sqlGrupo = "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)";
        nrcBase = "101";
        try (PreparedStatement ps = connection.prepareStatement(sqlGrupo)) {
            ps.setString(1, nrcBase);
            ps.setString(2, "Grupo Base");
            ps.setInt(3, idUsuarioBase);
            ps.setString(4, idPeriodoBase);
            ps.executeUpdate();
        }
        // Insertar estudiante base
        String sqlEstudiante = "INSERT INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio, calificacionFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        matriculaBase = "A12345678";
        try (PreparedStatement ps = connection.prepareStatement(sqlEstudiante)) {
            ps.setString(1, matriculaBase);
            ps.setInt(2, 1);
            ps.setString(3, "Juan");
            ps.setString(4, "Pérez");
            ps.setString(5, "1234567890");
            ps.setString(6, "juan.perez@example.com");
            ps.setString(7, "juanperez");
            ps.setString(8, "password123");
            ps.setString(9, nrcBase);
            ps.setInt(10, 50);
            ps.setDouble(11, 85.5);
            ps.executeUpdate();
        }
        // Insertar evidencia base
        String sqlEvidencia = "INSERT INTO evidencia (nombreEvidencia, fechaEntrega, ruta) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sqlEvidencia, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Evidencia Base");
            ps.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            ps.setString(3, "/ruta/evidencia/base");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    idEvidenciaBase = String.valueOf(rs.getInt(1));
                }
            }
        }
    }

    @Test
    void testInsertScheduleOfActivities() {
        try {
            ScheduleOfActivitiesDTO schedule = new ScheduleOfActivitiesDTO(
                    "1", "Hito 1", new Timestamp(System.currentTimeMillis()), matriculaBase, idEvidenciaBase
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
                    "2", "Hito 2", new Timestamp(System.currentTimeMillis()), matriculaBase, idEvidenciaBase
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
                    "3", "Hito 3", new Timestamp(System.currentTimeMillis()), matriculaBase, idEvidenciaBase
            );
            scheduleOfActivitiesDAO.insertScheduleOfActivities(schedule, connection);

            ScheduleOfActivitiesDTO updatedSchedule = new ScheduleOfActivitiesDTO(
                    "3", "Hito Actualizado", new Timestamp(System.currentTimeMillis()), matriculaBase, idEvidenciaBase
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
                    "4", "Hito Eliminar", new Timestamp(System.currentTimeMillis()), matriculaBase, idEvidenciaBase
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
                    "5", "Hito Lista 1", new Timestamp(System.currentTimeMillis()), matriculaBase, idEvidenciaBase
            );
            ScheduleOfActivitiesDTO schedule2 = new ScheduleOfActivitiesDTO(
                    "6", "Hito Lista 2", new Timestamp(System.currentTimeMillis()), matriculaBase, idEvidenciaBase
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