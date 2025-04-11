package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DTO.PeriodDTO;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PeriodDAOTest {

    private static ConecctionDataBase connectionDB;
    private static Connection connection;
    private PeriodDAO periodDAO;

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
        periodDAO = new PeriodDAO();
    }

    private String insertTestPeriod(String nombre, Date fechaInicio, Date fechaFin) throws SQLException {
        String sql = "INSERT INTO periodo (nombre, fechaInicio, fechaFin) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nombre);
            stmt.setTimestamp(2, new Timestamp(fechaInicio.getTime()));
            stmt.setTimestamp(3, new Timestamp(fechaFin.getTime()));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getString(1);
                } else {
                    throw new SQLException("No se generó ID para el periodo");
                }
            }
        }
    }

    @Test
    void testInsertPeriod() {
        try {
            String nombre = "Periodo Test";
            Date fechaInicio = new Date();
            Date fechaFin = new Date(fechaInicio.getTime() + 86400000);

            PeriodDTO period = new PeriodDTO(null, nombre, new Timestamp(fechaInicio.getTime()), new Timestamp(fechaFin.getTime()));
            boolean result = periodDAO.insertPeriod(period, connection);
            assertTrue(result, "La inserción debería ser exitosa");

            String sql = "SELECT * FROM periodo WHERE nombre = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, nombre);
                try (ResultSet rs = stmt.executeQuery()) {
                    assertTrue(rs.next(), "Debería encontrar el periodo insertado");
                    assertEquals(nombre, rs.getString("nombre"), "El nombre del periodo debería coincidir");
                }
            }
        } catch (SQLException e) {
            fail("Error en testInsertPeriod: " + e.getMessage());
        }
    }

    @Test
    void testGetPeriod() {
        try {
            String nombre = "Periodo Get";
            Date fechaInicio = new Date();
            Date fechaFin = new Date(fechaInicio.getTime() + 86400000);

            String idPeriodo = insertTestPeriod(nombre, fechaInicio, fechaFin);

            PeriodDTO period = periodDAO.getPeriod(idPeriodo, connection);
            assertNotNull(period, "Debería encontrar el periodo");
            assertEquals(nombre, period.getName(), "El nombre del periodo debería coincidir");
        } catch (SQLException e) {
            fail("Error en testGetPeriod: " + e.getMessage());
        }
    }

    @Test
    void testUpdatePeriod() {
        try {
            String nombre = "Periodo Update";
            Date fechaInicio = new Date();
            Date fechaFin = new Date(fechaInicio.getTime() + 86400000);

            String idPeriodo = insertTestPeriod(nombre, fechaInicio, fechaFin);

            PeriodDTO periodToUpdate = new PeriodDTO(idPeriodo, "Periodo Actualizado", new Timestamp(fechaInicio.getTime()), new Timestamp(fechaFin.getTime()));
            boolean result = periodDAO.updatePeriod(periodToUpdate, connection);
            assertTrue(result, "La actualización debería ser exitosa");

            PeriodDTO updatedPeriod = periodDAO.getPeriod(idPeriodo, connection);
            assertNotNull(updatedPeriod, "El periodo debería existir después de actualizar");
            assertEquals("Periodo Actualizado", updatedPeriod.getName(), "El nombre debería actualizarse");
        } catch (SQLException e) {
            fail("Error en testUpdatePeriod: " + e.getMessage());
        }
    }

    @Test
    void testDeletePeriod() {
        try {
            String nombre = "Periodo Delete";
            Date fechaInicio = new Date();
            Date fechaFin = new Date(fechaInicio.getTime() + 86400000);

            String idPeriodo = insertTestPeriod(nombre, fechaInicio, fechaFin);

            boolean result = periodDAO.deletePeriod(idPeriodo, connection);
            assertTrue(result, "La eliminación debería ser exitosa");

            PeriodDTO deletedPeriod = periodDAO.getPeriod(idPeriodo, connection);
            assertNull(deletedPeriod, "El periodo eliminado no debería existir");
        } catch (SQLException e) {
            fail("Error en testDeletePeriod: " + e.getMessage());
        }
    }

    @Test
    void testGetAllPeriods() {
        try {
            String nombre1 = "Periodo All 1";
            String nombre2 = "Periodo All 2";
            Date fechaInicio = new Date();
            Date fechaFin = new Date(fechaInicio.getTime() + 86400000);

            insertTestPeriod(nombre1, fechaInicio, fechaFin);
            insertTestPeriod(nombre2, fechaInicio, fechaFin);

            List<PeriodDTO> periods = periodDAO.getAllPeriods(connection);
            assertTrue(periods.size() >= 2, "Debería haber al menos dos periodos");
        } catch (SQLException e) {
            fail("Error en testGetAllPeriods: " + e.getMessage());
        }
    }
}