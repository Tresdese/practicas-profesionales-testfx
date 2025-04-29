package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.PeriodDAO;
import logic.DTO.PeriodDTO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
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
        try {
            // Limpia la tabla antes de cada prueba
            connection.prepareStatement("DELETE FROM periodo").executeUpdate();
        } catch (SQLException e) {
            fail("Error al limpiar la tabla periodo: " + e.getMessage());
        }
    }

    private PeriodDTO createTestPeriod(String idPeriod, String name, Timestamp startDate, Timestamp endDate) {
        return new PeriodDTO(idPeriod, name, startDate, endDate);
    }

    @Test
    void testInsertPeriod() {
        try {
            PeriodDTO period = createTestPeriod(
                    "222601",
                    "Agosto 25 Enero 26",
                    Timestamp.valueOf("2025-08-01 00:00:00"),
                    Timestamp.valueOf("2026-01-31 23:59:59")
            );

            boolean result = periodDAO.insertPeriod(period, connection);
            assertTrue(result, "La inserción debería ser exitosa");

            PeriodDTO insertedPeriod = periodDAO.searchPeriodById("222601", connection);
            assertNotNull(insertedPeriod, "El periodo debería existir en la base de datos");
            assertEquals("Agosto 25 Enero 26", insertedPeriod.getName(), "El nombre debería coincidir");
        } catch (SQLException e) {
            fail("Error en testInsertPeriod: " + e.getMessage());
        }
    }

    @Test
    void testSearchPeriodById() {
        try {
            PeriodDTO period = createTestPeriod(
                    "222651",
                    "Febrero 26 Julio 26",
                    Timestamp.valueOf("2026-02-01 00:00:00"),
                    Timestamp.valueOf("2026-07-31 23:59:59")
            );

            periodDAO.insertPeriod(period, connection);

            PeriodDTO retrievedPeriod = periodDAO.searchPeriodById("222651", connection);
            assertNotNull(retrievedPeriod, "El periodo debería existir en la base de datos");
            assertEquals("Febrero 26 Julio 26", retrievedPeriod.getName(), "El nombre debería coincidir");
        } catch (SQLException e) {
            fail("Error en testGetPeriod: " + e.getMessage());
        }
    }

    @Test
    void testUpdatePeriod() {
        try {
            PeriodDTO period = createTestPeriod(
                    "222701",
                    "Agosto 26 Enero 27",
                    Timestamp.valueOf("2026-08-01 00:00:00"),
                    Timestamp.valueOf("2027-01-31 23:59:59")
            );

            periodDAO.insertPeriod(period, connection);

            PeriodDTO updatedPeriod = new PeriodDTO(
                    "222701",
                    "Agosto 26 Febrero 27",
                    Timestamp.valueOf("2026-08-01 00:00:00"),
                    Timestamp.valueOf("2027-02-28 23:59:59")
            );

            boolean result = periodDAO.updatePeriod(updatedPeriod, connection);
            assertTrue(result, "La actualización debería ser exitosa");

            PeriodDTO retrievedPeriod = periodDAO.searchPeriodById("222701", connection);
            assertNotNull(retrievedPeriod, "El periodo debería existir después de actualizar");
            assertEquals("Agosto 26 Febrero 27", retrievedPeriod.getName(), "El nombre debería actualizarse");
        } catch (SQLException e) {
            fail("Error en testUpdatePeriod: " + e.getMessage());
        }
    }

    @Test
    void testDeletePeriod() {
        try {
            PeriodDTO period = createTestPeriod(
                    "222651",
                    "Febrero 26 Julio 26",
                    Timestamp.valueOf("2026-02-01 00:00:00"),
                    Timestamp.valueOf("2026-07-31 23:59:59")
            );

            periodDAO.insertPeriod(period, connection);

            boolean result = periodDAO.deletePeriodById("222651", connection);
            assertTrue(result, "La eliminación debería ser exitosa");

            PeriodDTO deletedPeriod = periodDAO.searchPeriodById("222651", connection);
            assertNull(deletedPeriod, "El periodo eliminado no debería existir");
        } catch (SQLException e) {
            fail("Error en testDeletePeriod: " + e.getMessage());
        }
    }

    @Test
    void testGetAllPeriods() {
        try {
            PeriodDTO period1 = createTestPeriod(
                    "222601",
                    "Agosto 25 Enero 26",
                    Timestamp.valueOf("2025-08-01 00:00:00"),
                    Timestamp.valueOf("2026-01-31 23:59:59")
            );

            PeriodDTO period2 = createTestPeriod(
                    "222651",
                    "Febrero 26 Julio 26",
                    Timestamp.valueOf("2026-02-01 00:00:00"),
                    Timestamp.valueOf("2026-07-31 23:59:59")
            );

            periodDAO.insertPeriod(period1, connection);
            periodDAO.insertPeriod(period2, connection);

            List<PeriodDTO> periods = periodDAO.getAllPeriods(connection);
            assertNotNull(periods, "La lista de periodos no debería ser nula");
            assertTrue(periods.size() >= 2, "Deberían existir al menos dos periodos en la base de datos");
        } catch (SQLException e) {
            fail("Error en testGetAllPeriods: " + e.getMessage());
        }
    }
}