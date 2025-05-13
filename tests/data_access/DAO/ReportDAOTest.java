package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.ReportDAO;
import logic.DTO.ReportDTO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        connectionDB.close();
    }

    @BeforeEach
    void setUp() {
        reportDAO = new ReportDAO();
        try {
            // Limpia la tabla antes de cada prueba
            connection.prepareStatement("DELETE FROM reporte").executeUpdate();
        } catch (SQLException e) {
            fail("Error al limpiar la tabla reporte: " + e.getMessage());
        }
    }

    @Test
    void testInsertReport() {
        try {
            ReportDTO report = new ReportDTO("1", "Observaciones de prueba", "1");
            boolean result = reportDAO.insertReport(report, connection);
            assertTrue(result, "La inserción debería ser exitosa");

            String sql = "SELECT * FROM reporte WHERE numReporte = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, "1");
                try (ResultSet rs = stmt.executeQuery()) {
                    assertTrue(rs.next(), "Debería encontrar el reporte insertado");
                    assertEquals("Observaciones de prueba", rs.getString("observaciones"), "Las observaciones deberían coincidir");
                    assertEquals("1", rs.getString("idEvidencia"), "El ID de evidencia debería coincidir");
                }
            }
        } catch (SQLException e) {
            fail("Error en testInsertReport: " + e.getMessage());
        }
    }

    @Test
    void testSearchReportById() {
        try {
            ReportDTO report = new ReportDTO("2", "Observaciones para obtener", "2");
            reportDAO.insertReport(report, connection);

            ReportDTO retrievedReport = reportDAO.searchReportById("2", connection);
            assertNotNull(retrievedReport, "Debería encontrar el reporte");
            assertEquals("Observaciones para obtener", retrievedReport.getObservations(), "Las observaciones deberían coincidir");
            assertEquals("2", retrievedReport.getIdEvidence(), "El ID de evidencia debería coincidir");
        } catch (SQLException e) {
            fail("Error en testSearchReportById: " + e.getMessage());
        }
    }

    @Test
    void testUpdateReport() {
        try {
            ReportDTO report = new ReportDTO("3", "Observaciones iniciales", "3");
            reportDAO.insertReport(report, connection);

            ReportDTO updatedReport = new ReportDTO("3", "Observaciones actualizadas", "4");
            boolean result = reportDAO.updateReport(updatedReport, connection);
            assertTrue(result, "La actualización debería ser exitosa");

            ReportDTO retrievedReport = reportDAO.searchReportById("3", connection);
            assertNotNull(retrievedReport, "El reporte debería existir después de actualizar");
            assertEquals("Observaciones actualizadas", retrievedReport.getObservations(), "Las observaciones deberían actualizarse");
            assertEquals("4", retrievedReport.getIdEvidence(), "El ID de evidencia debería actualizarse");
        } catch (SQLException e) {
            fail("Error en testUpdateReport: " + e.getMessage());
        }
    }

    @Test
    void testDeleteReport() {
        try {
            ReportDTO report = new ReportDTO("4", "Observaciones para eliminar", "5");
            reportDAO.insertReport(report, connection);

            boolean result = reportDAO.deleteReport("4", connection);
            assertTrue(result, "La eliminación debería ser exitosa");

            ReportDTO deletedReport = reportDAO.searchReportById("4", connection);
            assertEquals("N/A", deletedReport.getNumberReport(), "El reporte eliminado no debería existir");
        } catch (SQLException e) {
            fail("Error en testDeleteReport: " + e.getMessage());
        }
    }

    @Test
    void testGetAllReports() {
        try {
            ReportDTO report1 = new ReportDTO("5", "Observaciones 1", "6");
            ReportDTO report2 = new ReportDTO("6", "Observaciones 2", "7");
            reportDAO.insertReport(report1, connection);
            reportDAO.insertReport(report2, connection);

            List<ReportDTO> reports = reportDAO.getAllReports(connection);
            assertNotNull(reports, "La lista de reportes no debería ser nula");
            assertTrue(reports.size() >= 2, "Debería haber al menos dos reportes");
        } catch (SQLException e) {
            fail("Error en testGetAllReports: " + e.getMessage());
        }
    }
}