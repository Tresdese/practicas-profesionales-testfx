package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.PeriodDAO;
import logic.DTO.PeriodDTO;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PeriodDAOTest {
    private ConnectionDataBase connectionDB;
    private Connection connection;
    private PeriodDAO periodDAO;

    private final String baseId = "1000";
    private final String baseName = "Periodo Base";
    private final Timestamp baseStart = Timestamp.valueOf("2025-01-01 00:00:00");
    private final Timestamp baseEnd = Timestamp.valueOf("2025-06-30 23:59:59");

    @BeforeAll
    void setUpAll() {
        try {
            connectionDB = new ConnectionDataBase();
            connection = connectionDB.connectDataBase();
            periodDAO = new PeriodDAO();
            clearTable();
            PeriodDTO basePeriod = new PeriodDTO(baseId, baseName, baseStart, baseEnd);
            assertTrue(periodDAO.insertPeriod(basePeriod), "No se pudo insertar el periodo base en BeforeAll");
        } catch (SQLException e) {
            fail("Error al conectar o preparar la base de datos: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado al conectar a la base de datos: " + e.getMessage());
        }
    }

    @AfterAll
    void tearDownAll() {
        try {
            clearTable();
            if (connection != null) connection.close();
            if (connectionDB != null) connectionDB.close();
        } catch (SQLException e) {
            fail("Error al limpiar la tabla o cerrar la conexión: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado al cerrar la conexión: " + e.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        try {
            clearTable();
            PeriodDTO basePeriod = new PeriodDTO(baseId, baseName, baseStart, baseEnd);
            assertTrue(periodDAO.insertPeriod(basePeriod), "No se pudo insertar el periodo base en BeforeEach");
        } catch (SQLException e) {
            fail("Error al limpiar la tabla periodo: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado al limpiar la tabla periodo: " + e.getMessage());
        }
    }

    private void clearTable() throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM periodo")) {
            stmt.executeUpdate();
        }
        try (PreparedStatement stmt = connection.prepareStatement("ALTER TABLE periodo AUTO_INCREMENT = 1")) {
            stmt.executeUpdate();
        } catch (SQLException ignored) { }
    }

    @Test
    void insertPeriodSuccessfully() {
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
            fail("Error en insertPeriodSuccessfully: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en insertPeriodSuccessfully: " + e.getMessage());
        }
    }

    @Test
    void searchPeriodByIdSuccessfully() {
        try {
            PeriodDTO retrievedPeriod = periodDAO.searchPeriodById(baseId);
            assertNotNull(retrievedPeriod, "El periodo base debería existir en la base de datos");
            assertEquals(baseName, retrievedPeriod.getName(), "El nombre debería coincidir");
        } catch (SQLException e) {
            fail("Error en searchPeriodByIdSuccessfully: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en searchPeriodByIdSuccessfully: " + e.getMessage());
        }
    }

    @Test
    void updatePeriodSuccessfully() {
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
            fail("Error en updatePeriodSuccessfully: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en updatePeriodSuccessfully: " + e.getMessage());
        }
    }

    @Test
    void deletePeriodSuccessfully() {
        try {
            boolean result = periodDAO.deletePeriodById(baseId);
            assertTrue(result, "La eliminación debería ser exitosa");
            PeriodDTO deletedPeriod = periodDAO.searchPeriodById(baseId);
            assertEquals("N/A", deletedPeriod.getIdPeriod(), "El periodo eliminado no debería existir");
        } catch (SQLException e) {
            fail("Error en deletePeriodSuccessfully: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en deletePeriodSuccessfully: " + e.getMessage());
        }
    }

    @Test
    void getAllPeriodsSuccessfully() {
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
            fail("Error en getAllPeriodsSuccessfully: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en getAllPeriodsSuccessfully: " + e.getMessage());
        }
    }

    @Test
    void insertPeriodFailsWithDuplicateId() {
        try {
            PeriodDTO duplicate = new PeriodDTO(
                    baseId,
                    "Periodo Duplicado",
                    Timestamp.valueOf("2028-01-01 00:00:00"),
                    Timestamp.valueOf("2028-06-30 23:59:59")
            );
            boolean result = periodDAO.insertPeriod(duplicate);
            assertFalse(result, "No debería permitir insertar un periodo con ID duplicado");
        } catch (SQLException e) {
            fail("Error inesperado en insertPeriodFailsWithDuplicateId: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en insertPeriodFailsWithDuplicateId: " + e.getMessage());
        }
    }

    @Test
    void updatePeriodFailsWhenNotExists() {
        try {
            PeriodDTO nonExistent = new PeriodDTO(
                    "9999",
                    "No Existe",
                    Timestamp.valueOf("2030-01-01 00:00:00"),
                    Timestamp.valueOf("2030-06-30 23:59:59")
            );
            boolean result = periodDAO.updatePeriod(nonExistent);
            assertFalse(result, "No debería actualizar un periodo inexistente");
        } catch (SQLException e) {
            fail("Error inesperado en updatePeriodFailsWhenNotExists: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en updatePeriodFailsWhenNotExists: " + e.getMessage());
        }
    }

    @Test
    void deletePeriodFailsWhenNotExists() {
        try {
            boolean result = periodDAO.deletePeriodById("8888");
            assertFalse(result, "No debería eliminar un periodo inexistente");
        } catch (SQLException e) {
            fail("Error inesperado en deletePeriodFailsWhenNotExists: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en deletePeriodFailsWhenNotExists: " + e.getMessage());
        }
    }

    @Test
    void searchPeriodByIdReturnsNAWhenNotExists() {
        try {
            PeriodDTO found = periodDAO.searchPeriodById("7777");
            assertNotNull(found, "El resultado no debe ser nulo");
            assertEquals("N/A", found.getIdPeriod(), "El id debe ser 'N/A' si no existe");
            assertEquals("N/A", found.getName(), "El nombre debe ser 'N/A' si no existe");
        } catch (SQLException e) {
            fail("Error inesperado en searchPeriodByIdReturnsNAWhenNotExists: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en searchPeriodByIdReturnsNAWhenNotExists: " + e.getMessage());
        }
    }

    @Test
    void getAllPeriodsReturnsEmptyListWhenNoPeriodsExist() {
        try {
            clearTable();
            List<PeriodDTO> list = periodDAO.getAllPeriods();
            assertNotNull(list, "La lista no debe ser nula");
            assertTrue(list.isEmpty(), "La lista debe estar vacía si no hay periodos");
        } catch (SQLException e) {
            fail("Error inesperado en getAllPeriodsReturnsEmptyListWhenNoPeriodsExist: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en getAllPeriodsReturnsEmptyListWhenNoPeriodsExist: " + e.getMessage());
        }
    }

    @Test
    void insertPeriodFailsWithNullOrEmptyFields() {
        assertThrows(SQLException.class, () -> {
            PeriodDTO nullFields = new PeriodDTO(null, null, null, null);
            periodDAO.insertPeriod(nullFields);
        });

        assertThrows(SQLException.class, () -> {
            PeriodDTO emptyFields = new PeriodDTO("", "", null, null);
            periodDAO.insertPeriod(emptyFields);
        });
    }

    @Test
    void insertPeriodWithLongFields() {
        String longId = "a".repeat(300);
        String longName = "b".repeat(300);
        PeriodDTO period = new PeriodDTO(longId, longName, baseStart, baseEnd);
        assertThrows(SQLException.class, () -> periodDAO.insertPeriod(period));
    }

    @Test
    void getAllPeriodsReturnsOrderedList() {
        try {
            clearTable();
            PeriodDTO p1 = new PeriodDTO("1", "A", baseStart, baseEnd);
            PeriodDTO p2 = new PeriodDTO("2", "B", baseStart, baseEnd);
            PeriodDTO p3 = new PeriodDTO("3", "C", baseStart, baseEnd);
            periodDAO.insertPeriod(p1);
            periodDAO.insertPeriod(p2);
            periodDAO.insertPeriod(p3);

            List<PeriodDTO> list = periodDAO.getAllPeriods();
            assertEquals(3, list.size());
            assertEquals("1", list.get(0).getIdPeriod());
            assertEquals("2", list.get(1).getIdPeriod());
            assertEquals("3", list.get(2).getIdPeriod());
        } catch (SQLException e) {
            fail("Error inesperado en getAllPeriodsReturnsOrderedList: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en getAllPeriodsReturnsOrderedList: " + e.getMessage());
        }
    }
}