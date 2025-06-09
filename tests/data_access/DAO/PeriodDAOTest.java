package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.PeriodDAO;
import logic.DTO.PeriodDTO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PeriodDAOTest {
    private ConecctionDataBase connectionDB;
    private Connection connection;
    private PeriodDAO periodDAO;

    // Datos base para pruebas
    private final String baseId = "1000";
    private final String baseName = "Periodo Base";
    private final Timestamp baseStart = Timestamp.valueOf("2025-01-01 00:00:00");
    private final Timestamp baseEnd = Timestamp.valueOf("2025-06-30 23:59:59");

    @BeforeAll
    void setUpAll() {
        connectionDB = new ConecctionDataBase();
        try {
            connection = connectionDB.connectDB();
            periodDAO = new PeriodDAO();
            clearTable();
            // Insertar registro base necesario para los tests
            PeriodDTO basePeriod = new PeriodDTO(baseId, baseName, baseStart, baseEnd);
            assertTrue(periodDAO.insertPeriod(basePeriod), "No se pudo insertar el periodo base en BeforeAll");
        } catch (SQLException e) {
            fail("Error al conectar o preparar la base de datos: " + e.getMessage());
        }
    }

    @AfterAll
    void tearDownAll() {
        try {
            clearTable();
            if (connection != null) connection.close();
            if (connectionDB != null) connectionDB.close();
        } catch (SQLException ignored) {}
    }

    @BeforeEach
    void setUp() {
        try {
            clearTable();
            // Insertar el periodo base usando el DAO, igual que en StudentDAOTest
            PeriodDTO basePeriod = new PeriodDTO(baseId, baseName, baseStart, baseEnd);
            assertTrue(periodDAO.insertPeriod(basePeriod), "No se pudo insertar el periodo base en BeforeEach");
        } catch (SQLException e) {
            fail("Error al limpiar la tabla periodo: " + e.getMessage());
        }
    }

    private void clearTable() throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM periodo")) {
            stmt.executeUpdate();
        }
        try (PreparedStatement stmt = connection.prepareStatement("ALTER TABLE periodo AUTO_INCREMENT = 1")) {
            stmt.executeUpdate();
        } catch (SQLException ignored) { /* Puede fallar si la BD no soporta AUTO_INCREMENT en PK manual */ }
    }

    @Test
    void testInsertPeriod() {
        try {
            PeriodDTO period = new PeriodDTO(
                    "2000",
                    "Periodo Extra",
                    Timestamp.valueOf("2026-01-01 00:00:00"),
                    Timestamp.valueOf("2026-06-30 23:59:59")
            );
            boolean result = periodDAO.insertPeriod(period);
            assertTrue(result, "La inserción debería ser exitosa");
            PeriodDTO insertedPeriod = periodDAO.searchPeriodById("2000");
            assertNotNull(insertedPeriod, "El periodo debería existir en la base de datos");
            assertEquals("Periodo Extra", insertedPeriod.getName(), "El nombre debería coincidir");
        } catch (SQLException e) {
            fail("Error en testInsertPeriod: " + e.getMessage());
        }
    }

    @Test
    void testSearchPeriodById() {
        try {
            PeriodDTO retrievedPeriod = periodDAO.searchPeriodById(baseId);
            assertNotNull(retrievedPeriod, "El periodo base debería existir en la base de datos");
            assertEquals(baseName, retrievedPeriod.getName(), "El nombre debería coincidir");
        } catch (SQLException e) {
            fail("Error en testSearchPeriodById: " + e.getMessage());
        }
    }

    @Test
    void testUpdatePeriod() {
        try {
            PeriodDTO updatedPeriod = new PeriodDTO(
                    baseId,
                    "Periodo Base Actualizado",
                    baseStart,
                    baseEnd
            );
            boolean result = periodDAO.updatePeriod(updatedPeriod);
            assertTrue(result, "La actualización debería ser exitosa");
            PeriodDTO retrievedPeriod = periodDAO.searchPeriodById(baseId);
            assertNotNull(retrievedPeriod, "El periodo debería existir después de actualizar");
            assertEquals("Periodo Base Actualizado", retrievedPeriod.getName(), "El nombre debería actualizarse");
        } catch (SQLException e) {
            fail("Error en testUpdatePeriod: " + e.getMessage());
        }
    }

    @Test
    void testDeletePeriod() {
        try {
            boolean result = periodDAO.deletePeriodById(baseId);
            assertTrue(result, "La eliminación debería ser exitosa");
            PeriodDTO deletedPeriod = periodDAO.searchPeriodById(baseId);
            assertEquals("N/A", deletedPeriod.getIdPeriod(), "El periodo eliminado no debería existir");
        } catch (SQLException e) {
            fail("Error en testDeletePeriod: " + e.getMessage());
        }
    }

    @Test
    void testGetAllPeriods() {
        try {
            PeriodDTO period2 = new PeriodDTO(
                    "3000",
                    "Periodo Secundario",
                    Timestamp.valueOf("2027-01-01 00:00:00"),
                    Timestamp.valueOf("2027-06-30 23:59:59")
            );
            periodDAO.insertPeriod(period2);
            List<PeriodDTO> periods = periodDAO.getAllPeriods();
            assertNotNull(periods, "La lista de periodos no debería ser nula");
            assertTrue(periods.size() >= 2, "Deberían existir al menos dos periodos en la base de datos");
        } catch (SQLException e) {
            fail("Error en testGetAllPeriods: " + e.getMessage());
        }
    }
}
