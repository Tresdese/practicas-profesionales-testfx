package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DTO.ReportDTO;

import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ReportDAOTest {

    private static ConecctionDataBase connectionDB;
    private static Connection connection;
    private ReportDAO reportDAO;

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
        reportDAO = new ReportDAO();
    }

    private String insertTestReport(String numReporte, String observaciones, String idEvidencia) throws SQLException {
        ReportDTO existingReport = reportDAO.getReport(numReporte, connection);
        if (existingReport != null) {
            return numReporte;
        }

        String sql = "INSERT INTO reporte (numReporte, observaciones, idEvidencia) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, numReporte);
            stmt.setString(2, observaciones);
            stmt.setString(3, idEvidencia);
            stmt.executeUpdate();
            return numReporte;
        }
    }

    @Test
    void testInsertReport() {
        try {
            String numReporte = "1";
            String observaciones = "Observaciones de prueba";
            String idEvidencia = "1";

            ReportDTO report = new ReportDTO(numReporte, observaciones, idEvidencia);
            boolean result = reportDAO.insertReport(report, connection);
            assertTrue(result, "La inserción debería ser exitosa");

            String sql = "SELECT * FROM reporte WHERE numReporte = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, numReporte);
                try (ResultSet rs = stmt.executeQuery()) {
                    assertTrue(rs.next(), "Debería encontrar el reporte insertado");
                    assertEquals(observaciones, rs.getString("observaciones"), "Las observaciones deberían coincidir");
                    assertEquals(idEvidencia, rs.getString("idEvidencia"), "El ID de evidencia debería coincidir");
                }
            }
        } catch (SQLException e) {
            fail("Error en testInsertReport: " + e.getMessage());
        }
    }

    @Test
    void testGetReport() {
        try {
            String numReporte = "2";
            String observaciones = "Observaciones para obtener";
            String idEvidencia = "2";

            insertTestReport(numReporte, observaciones, idEvidencia);

            ReportDTO report = reportDAO.getReport(numReporte, connection);
            assertNotNull(report, "Debería encontrar el reporte");
            assertEquals(observaciones, report.getObservations(), "Las observaciones deberían coincidir");
            assertEquals(idEvidencia, report.getIdEvidence(), "El ID de evidencia debería coincidir");
        } catch (SQLException e) {
            fail("Error en testGetReport: " + e.getMessage());
        }
    }

    @Test
    void testUpdateReport() {
        try {
            String numReporte = "3";
            String observaciones = "Observaciones iniciales";
            String idEvidencia = "4";

            insertTestReport(numReporte, observaciones, idEvidencia);

            ReportDTO reportToUpdate = new ReportDTO(numReporte, "Observaciones actualizadas", "4");
            boolean result = reportDAO.updateReport(reportToUpdate, connection);
            assertTrue(result, "La actualización debería ser exitosa");

            ReportDTO updatedReport = reportDAO.getReport(numReporte, connection);
            assertNotNull(updatedReport, "El reporte debería existir después de actualizar");
            assertEquals("Observaciones actualizadas", updatedReport.getObservations(), "Las observaciones deberían actualizarse");
            assertEquals("4", updatedReport.getIdEvidence(), "El ID de evidencia debería actualizarse");
        } catch (SQLException e) {
            fail("Error en testUpdateReport: " + e.getMessage());
        }
    }

    @Test
    void testDeleteReport() {
        try {
            String numReporte = "4";
            String observaciones = "Observaciones para eliminar";
            String idEvidencia = "5";

            insertTestReport(numReporte, observaciones, idEvidencia);

            boolean result = reportDAO.deleteReport(numReporte, connection);
            assertTrue(result, "La eliminación debería ser exitosa");

            ReportDTO deletedReport = reportDAO.getReport(numReporte, connection);
            assertNull(deletedReport, "El reporte eliminado no debería existir");
        } catch (SQLException e) {
            fail("Error en testDeleteReport: " + e.getMessage());
        }
    }

    @Test
    void testGetAllReports() {
        try {
            String numReporte1 = "5";
            String observaciones1 = "Observaciones 1";
            String idEvidencia1 = "6";

            String numReporte2 = "6";
            String observaciones2 = "Observaciones 2";
            String idEvidencia2 = "7";

            insertTestReport(numReporte1, observaciones1, idEvidencia1);
            insertTestReport(numReporte2, observaciones2, idEvidencia2);

            List<ReportDTO> reports = reportDAO.getAllReports(connection);
            assertTrue(reports.size() >= 2, "Debería haber al menos dos reportes");
        } catch (SQLException e) {
            fail("Error en testGetAllReports: " + e.getMessage());
        }
    }
}