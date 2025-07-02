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

    private ConnectionDataBase connectionDataBase;
    private Connection databaseConnection;
    private PeriodDAO periodDataAccessObject;

    private final String basePeriodId = "1000";
    private final String basePeriodName = "Periodo Base";
    private final Timestamp basePeriodStart = Timestamp.valueOf("2025-01-01 00:00:00");
    private final Timestamp basePeriodEnd = Timestamp.valueOf("2025-06-30 23:59:59");

    @BeforeAll
    void setUpAll() {
        try {
            connectionDataBase = new ConnectionDataBase();
            databaseConnection = connectionDataBase.connectDataBase();
            periodDataAccessObject = new PeriodDAO();
            clearTable();
            PeriodDTO basePeriod = new PeriodDTO(basePeriodId, basePeriodName, basePeriodStart, basePeriodEnd);
            assertTrue(periodDataAccessObject.insertPeriod(basePeriod), "No se pudo insertar el periodo base en BeforeAll");
        } catch (SQLException exception) {
            fail("Error al conectar o preparar la base de datos: " + exception.getMessage());
        } catch (IOException exception) {
            fail("Error al cargar la configuración de la base de datos: " + exception.getMessage());
        } catch (Exception exception) {
            fail("Error inesperado al conectar a la base de datos: " + exception.getMessage());
        }
    }

    @AfterAll
    void tearDownAll() {
        try {
            clearTable();
            if (databaseConnection != null) databaseConnection.close();
            if (connectionDataBase != null) connectionDataBase.close();
        } catch (SQLException exception) {
            fail("Error al limpiar la tabla o cerrar la conexión: " + exception.getMessage());
        } catch (Exception exception) {
            fail("Error inesperado al cerrar la conexión: " + exception.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        try {
            clearTable();
            PeriodDTO basePeriod = new PeriodDTO(basePeriodId, basePeriodName, basePeriodStart, basePeriodEnd);
            assertTrue(periodDataAccessObject.insertPeriod(basePeriod), "No se pudo insertar el periodo base en BeforeEach");
        } catch (SQLException exception) {
            fail("Error al limpiar la tabla periodo: " + exception.getMessage());
        } catch (IOException exception) {
            fail("Error al cargar la configuración de la base de datos: " + exception.getMessage());
        } catch (Exception exception) {
            fail("Error inesperado al limpiar la tabla periodo: " + exception.getMessage());
        }
    }

    private void clearTable() throws SQLException {
        try (PreparedStatement preparedStatement = databaseConnection.prepareStatement("DELETE FROM periodo")) {
            preparedStatement.executeUpdate();
        }
        try (PreparedStatement preparedStatement = databaseConnection.prepareStatement("ALTER TABLE periodo AUTO_INCREMENT = 1")) {
            preparedStatement.executeUpdate();
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
            boolean wasInserted = periodDataAccessObject.insertPeriod(period);
            assertTrue(wasInserted, "La inserción debería ser exitosa");
            PeriodDTO insertedPeriod = periodDataAccessObject.searchPeriodById("2000");
            assertNotNull(insertedPeriod, "El periodo debería existir en la base de datos");
            assertEquals("Periodo Extra", insertedPeriod.getName(), "El nombre debería coincidir");
        } catch (SQLException exception) {
            fail("Error en insertPeriodSuccessfully: " + exception.getMessage());
        } catch (IOException exception) {
            fail("Error al cargar la configuración de la base de datos: " + exception.getMessage());
        } catch (Exception exception) {
            fail("Error inesperado en insertPeriodSuccessfully: " + exception.getMessage());
        }
    }

    @Test
    void searchPeriodByIdSuccessfully() {
        try {
            PeriodDTO retrievedPeriod = periodDataAccessObject.searchPeriodById(basePeriodId);
            assertNotNull(retrievedPeriod, "El periodo base debería existir en la base de datos");
            assertEquals(basePeriodName, retrievedPeriod.getName(), "El nombre debería coincidir");
        } catch (SQLException exception) {
            fail("Error en searchPeriodByIdSuccessfully: " + exception.getMessage());
        } catch (IOException exception) {
            fail("Error al cargar la configuración de la base de datos: " + exception.getMessage());
        } catch (Exception exception) {
            fail("Error inesperado en searchPeriodByIdSuccessfully: " + exception.getMessage());
        }
    }

    @Test
    void updatePeriodSuccessfully() {
        try {
            PeriodDTO updatedPeriod = new PeriodDTO(
                    basePeriodId,
                    "Periodo Base Actualizado",
                    basePeriodStart,
                    basePeriodEnd
            );
            boolean wasUpdated = periodDataAccessObject.updatePeriod(updatedPeriod);
            assertTrue(wasUpdated, "La actualización debería ser exitosa");
            PeriodDTO retrievedPeriod = periodDataAccessObject.searchPeriodById(basePeriodId);
            assertNotNull(retrievedPeriod, "El periodo debería existir después de actualizar");
            assertEquals("Periodo Base Actualizado", retrievedPeriod.getName(), "El nombre debería actualizarse");
        } catch (SQLException exception) {
            fail("Error en updatePeriodSuccessfully: " + exception.getMessage());
        } catch (IOException exception) {
            fail("Error al cargar la configuración de la base de datos: " + exception.getMessage());
        } catch (Exception exception) {
            fail("Error inesperado en updatePeriodSuccessfully: " + exception.getMessage());
        }
    }

    @Test
    void deletePeriodSuccessfully() {
        try {
            boolean wasDeleted = periodDataAccessObject.deletePeriodById(basePeriodId);
            assertTrue(wasDeleted, "La eliminación debería ser exitosa");
            PeriodDTO deletedPeriod = periodDataAccessObject.searchPeriodById(basePeriodId);
            assertEquals("N/A", deletedPeriod.getIdPeriod(), "El periodo eliminado no debería existir");
        } catch (SQLException exception) {
            fail("Error en deletePeriodSuccessfully: " + exception.getMessage());
        } catch (IOException exception) {
            fail("Error al cargar la configuración de la base de datos: " + exception.getMessage());
        } catch (Exception exception) {
            fail("Error inesperado en deletePeriodSuccessfully: " + exception.getMessage());
        }
    }

    @Test
    void getAllPeriodsSuccessfully() {
        try {
            PeriodDTO periodTwo = new PeriodDTO(
                    "3000",
                    "Periodo Secundario",
                    Timestamp.valueOf("2027-01-01 00:00:00"),
                    Timestamp.valueOf("2027-06-30 23:59:59")
            );
            periodDataAccessObject.insertPeriod(periodTwo);
            List<PeriodDTO> periodList = periodDataAccessObject.getAllPeriods();
            assertNotNull(periodList, "La lista de periodos no debería ser nula");
            assertTrue(periodList.size() >= 2, "Deberían existir al menos dos periodos en la base de datos");
        } catch (SQLException exception) {
            fail("Error en getAllPeriodsSuccessfully: " + exception.getMessage());
        } catch (IOException exception) {
            fail("Error al cargar la configuración de la base de datos: " + exception.getMessage());
        } catch (Exception exception) {
            fail("Error inesperado en getAllPeriodsSuccessfully: " + exception.getMessage());
        }
    }

    @Test
    void insertPeriodFailsWithDuplicateId() {
        try {
            PeriodDTO duplicatePeriod = new PeriodDTO(
                    basePeriodId,
                    "Periodo Duplicado",
                    Timestamp.valueOf("2028-01-01 00:00:00"),
                    Timestamp.valueOf("2028-06-30 23:59:59")
            );
            boolean wasInserted = periodDataAccessObject.insertPeriod(duplicatePeriod);
            assertFalse(wasInserted, "No debería permitir insertar un periodo con ID duplicado");
        } catch (SQLException exception) {
            fail("Error inesperado en insertPeriodFailsWithDuplicateId: " + exception.getMessage());
        } catch (IOException exception) {
            fail("Error al cargar la configuración de la base de datos: " + exception.getMessage());
        } catch (Exception exception) {
            fail("Error inesperado en insertPeriodFailsWithDuplicateId: " + exception.getMessage());
        }
    }

    @Test
    void updatePeriodFailsWhenNotExists() {
        try {
            PeriodDTO nonExistentPeriod = new PeriodDTO(
                    "9999",
                    "No Existe",
                    Timestamp.valueOf("2030-01-01 00:00:00"),
                    Timestamp.valueOf("2030-06-30 23:59:59")
            );
            boolean wasUpdated = periodDataAccessObject.updatePeriod(nonExistentPeriod);
            assertFalse(wasUpdated, "No debería actualizar un periodo inexistente");
        } catch (SQLException exception) {
            fail("Error inesperado en updatePeriodFailsWhenNotExists: " + exception.getMessage());
        } catch (IOException exception) {
            fail("Error al cargar la configuración de la base de datos: " + exception.getMessage());
        } catch (Exception exception) {
            fail("Error inesperado en updatePeriodFailsWhenNotExists: " + exception.getMessage());
        }
    }

    @Test
    void deletePeriodFailsWhenNotExists() {
        try {
            boolean wasDeleted = periodDataAccessObject.deletePeriodById("8888");
            assertFalse(wasDeleted, "No debería eliminar un periodo inexistente");
        } catch (SQLException exception) {
            fail("Error inesperado en deletePeriodFailsWhenNotExists: " + exception.getMessage());
        } catch (IOException exception) {
            fail("Error al cargar la configuración de la base de datos: " + exception.getMessage());
        } catch (Exception exception) {
            fail("Error inesperado en deletePeriodFailsWhenNotExists: " + exception.getMessage());
        }
    }

    @Test
    void searchPeriodByIdReturnsNAWhenNotExists() {
        try {
            PeriodDTO foundPeriod = periodDataAccessObject.searchPeriodById("7777");
            assertNotNull(foundPeriod, "El resultado no debe ser nulo");
            assertEquals("N/A", foundPeriod.getIdPeriod(), "El id debe ser 'N/A' si no existe");
            assertEquals("N/A", foundPeriod.getName(), "El nombre debe ser 'N/A' si no existe");
        } catch (SQLException exception) {
            fail("Error inesperado en searchPeriodByIdReturnsNAWhenNotExists: " + exception.getMessage());
        } catch (IOException exception) {
            fail("Error al cargar la configuración de la base de datos: " + exception.getMessage());
        } catch (Exception exception) {
            fail("Error inesperado en searchPeriodByIdReturnsNAWhenNotExists: " + exception.getMessage());
        }
    }

    @Test
    void getAllPeriodsReturnsEmptyListWhenNoPeriodsExist() {
        try {
            clearTable();
            List<PeriodDTO> periodList = periodDataAccessObject.getAllPeriods();
            assertNotNull(periodList, "La lista no debe ser nula");
            assertTrue(periodList.isEmpty(), "La lista debe estar vacía si no hay periodos");
        } catch (SQLException exception) {
            fail("Error inesperado en getAllPeriodsReturnsEmptyListWhenNoPeriodsExist: " + exception.getMessage());
        } catch (IOException exception) {
            fail("Error al cargar la configuración de la base de datos: " + exception.getMessage());
        } catch (Exception exception) {
            fail("Error inesperado en getAllPeriodsReturnsEmptyListWhenNoPeriodsExist: " + exception.getMessage());
        }
    }

    @Test
    void insertPeriodFailsWithNullOrEmptyFields() {
        assertThrows(SQLException.class, () -> {
            PeriodDTO nullFields = new PeriodDTO(null, null, null, null);
            periodDataAccessObject.insertPeriod(nullFields);
        });

        assertThrows(SQLException.class, () -> {
            PeriodDTO emptyFields = new PeriodDTO("", "", null, null);
            periodDataAccessObject.insertPeriod(emptyFields);
        });
    }

    @Test
    void insertPeriodWithLongFields() {
        String longId = "a".repeat(300);
        String longName = "b".repeat(300);
        PeriodDTO period = new PeriodDTO(longId, longName, basePeriodStart, basePeriodEnd);
        assertThrows(SQLException.class, () -> periodDataAccessObject.insertPeriod(period));
    }

    @Test
    void getAllPeriodsReturnsOrderedList() {
        try {
            clearTable();
            PeriodDTO periodOne = new PeriodDTO("1", "A", basePeriodStart, basePeriodEnd);
            PeriodDTO periodTwo = new PeriodDTO("2", "B", basePeriodStart, basePeriodEnd);
            PeriodDTO periodThree = new PeriodDTO("3", "C", basePeriodStart, basePeriodEnd);
            periodDataAccessObject.insertPeriod(periodOne);
            periodDataAccessObject.insertPeriod(periodTwo);
            periodDataAccessObject.insertPeriod(periodThree);

            List<PeriodDTO> periodList = periodDataAccessObject.getAllPeriods();
            assertEquals(3, periodList.size());
            assertEquals("1", periodList.get(0).getIdPeriod());
            assertEquals("2", periodList.get(1).getIdPeriod());
            assertEquals("3", periodList.get(2).getIdPeriod());
        } catch (SQLException exception) {
            fail("Error inesperado en getAllPeriodsReturnsOrderedList: " + exception.getMessage());
        } catch (IOException exception) {
            fail("Error al cargar la configuración de la base de datos: " + exception.getMessage());
        } catch (Exception exception) {
            fail("Error inesperado en getAllPeriodsReturnsOrderedList: " + exception.getMessage());
        }
    }
}