package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.ReportDAO;
import logic.DTO.ReportDTO;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReportDAOTest {

    private ConecctionDataBase connectionDB;
    private Connection connection;
    private ReportDAO reportDAO;
    private int testEvidenceId;

    @BeforeAll
    void setUpAll() throws Exception {
        connectionDB = new ConecctionDataBase();
        connection = connectionDB.connectDB();
        limpiarTablasYResetearAutoIncrement();
        crearEvidenciaBase();
        reportDAO = new ReportDAO();
    }

    @BeforeEach
    void setUp() throws Exception {
        limpiarTablasYResetearAutoIncrement();
        crearEvidenciaBase();
    }

    @AfterAll
    void tearDownAll() throws Exception {
        limpiarTablasYResetearAutoIncrement();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        if (connectionDB != null) {
            connectionDB.close();
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        limpiarTablasYResetearAutoIncrement();
    }

    private void limpiarTablasYResetearAutoIncrement() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS=0");
        stmt.execute("TRUNCATE TABLE reporte");
        stmt.execute("TRUNCATE TABLE evidencia");
        stmt.execute("ALTER TABLE reporte AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE evidencia AUTO_INCREMENT = 1");
        stmt.execute("SET FOREIGN_KEY_CHECKS=1");
        stmt.close();
    }

    private void crearEvidenciaBase() throws SQLException {
        String sql = "INSERT INTO evidencia (nombreEvidencia, fechaEntrega, ruta) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "Evidencia Test");
            stmt.setDate(2, Date.valueOf("2024-06-01"));
            stmt.setString(3, "/ruta/evidencia.pdf");
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    testEvidenceId = rs.getInt(1);
                }
            }
        }
    }

    @Test
    void testInsertReport() throws SQLException {
        ReportDTO report = new ReportDTO("1", "Observaciones de prueba", String.valueOf(testEvidenceId));
        boolean result = reportDAO.insertReport(report);
        assertTrue(result, "La inserción debería ser exitosa");

        ReportDTO inserted = reportDAO.searchReportById("1");
        assertNotNull(inserted);
        assertEquals("Observaciones de prueba", inserted.getObservations());
        assertEquals(String.valueOf(testEvidenceId), inserted.getIdEvidence());
    }

    @Test
    void testSearchReportById() throws SQLException {
        insertTestReport("2", "Observaciones para obtener", String.valueOf(testEvidenceId));
        ReportDTO retrieved = reportDAO.searchReportById("2", connection);
        assertNotNull(retrieved);
        assertEquals("Observaciones para obtener", retrieved.getObservations());
        assertEquals(String.valueOf(testEvidenceId), retrieved.getIdEvidence());
    }

    @Test
    void testUpdateReport() throws SQLException {
        insertTestReport("3", "Observaciones iniciales", String.valueOf(testEvidenceId));
        ReportDTO updated = new ReportDTO("3", "Observaciones actualizadas", String.valueOf(testEvidenceId));
        boolean result = reportDAO.updateReport(updated, connection);
        assertTrue(result);

        ReportDTO retrieved = reportDAO.searchReportById("3", connection);
        assertEquals("Observaciones actualizadas", retrieved.getObservations());
    }

    @Test
    void testDeleteReport() throws SQLException {
        insertTestReport("4", "Observaciones para eliminar", String.valueOf(testEvidenceId));
        boolean result = reportDAO.deleteReport("4", connection);
        assertTrue(result);

        ReportDTO deleted = reportDAO.searchReportById("4", connection);
        assertEquals("N/A", deleted.getNumberReport());
    }

    @Test
    void testGetAllReports() throws SQLException {
        insertTestReport("5", "Observaciones 1", String.valueOf(testEvidenceId));
        insertTestReport("6", "Observaciones 2", String.valueOf(testEvidenceId));
        List<ReportDTO> reports = reportDAO.getAllReports(connection);
        assertNotNull(reports);
        assertEquals(2, reports.size());
    }

    private void insertTestReport(String numReporte, String observaciones, String idEvidencia) throws SQLException {
        String sql = "INSERT INTO reporte (numReporte, observaciones, idEvidencia) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, numReporte);
            stmt.setString(2, observaciones);
            stmt.setString(3, idEvidencia);
            stmt.executeUpdate();
        }
    }
}