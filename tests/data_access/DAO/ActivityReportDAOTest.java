package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.ActivityReportDAO;
import logic.DTO.ActivityReportDTO;
import org.junit.jupiter.api.*;

import java.sql.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActivityReportDAOTest {
    private Connection connection;
    private ActivityReportDAO activityReportDAO;
    private int idEvidencia;
    private int idReporte;
    private int idActividad;

    @BeforeAll
    void setUpAll() throws SQLException {
        ConecctionDataBase connectionDataBase = new ConecctionDataBase();
        connection = connectionDataBase.connectDB();
        activityReportDAO = new ActivityReportDAO();
        limpiarTablasYResetearAutoIncrement();
        crearDatosBase();
    }

    @BeforeEach
    void setUp() throws SQLException {
        limpiarTablasYResetearAutoIncrement();
        crearDatosBase();
    }

    @AfterAll
    void tearDownAll() throws SQLException {
        limpiarTablasYResetearAutoIncrement();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private void limpiarTablasYResetearAutoIncrement() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("DELETE FROM reporte_actividad");
        stmt.execute("DELETE FROM reporte");
        stmt.execute("DELETE FROM actividad");
        stmt.execute("DELETE FROM evidencia");
        stmt.execute("ALTER TABLE evidencia AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE reporte AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE actividad AUTO_INCREMENT = 1");
        // reporte_actividad no suele tener autoincrement, pero por si acaso:
        try { stmt.execute("ALTER TABLE reporte_actividad AUTO_INCREMENT = 1"); } catch (SQLException ignored) {}
        stmt.close();
    }

    private void crearDatosBase() throws SQLException {
        // Insertar evidencia
        String insertEvidenceSQL = "INSERT INTO evidencia (nombreEvidencia, fechaEntrega, ruta) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertEvidenceSQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Evidencia Test");
            ps.setDate(2, Date.valueOf("2024-06-01"));
            ps.setString(3, "/ruta/evidencia");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) idEvidencia = rs.getInt(1);
            }
        }

        // Insertar reporte
        String insertReportSQL = "INSERT INTO reporte (observaciones, idEvidencia) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertReportSQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Observaciones Test");
            ps.setInt(2, idEvidencia);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) idReporte = rs.getInt(1);
            }
        }

        // Insertar actividad
        String insertActivitySQL = "INSERT INTO actividad (nombreActividad) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(insertActivitySQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Actividad Test");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) idActividad = rs.getInt(1);
            }
        }
    }

    @Test
    void insertActivityReportSuccessfully() throws SQLException {
        ActivityReportDTO report = new ActivityReportDTO(String.valueOf(idReporte), String.valueOf(idActividad));
        boolean inserted = activityReportDAO.insertActivityReport(report);
        assertTrue(inserted, "El reporte debería insertarse correctamente.");
    }

    @Test
    void updateActivityReportSuccessfully() throws SQLException {
        ActivityReportDTO report = new ActivityReportDTO(String.valueOf(idReporte), String.valueOf(idActividad));
        activityReportDAO.insertActivityReport(report);
        ActivityReportDTO updatedReport = new ActivityReportDTO(String.valueOf(idReporte), String.valueOf(idActividad));
        boolean updated = activityReportDAO.updateActivityReport(updatedReport);
        assertTrue(updated, "El reporte debería actualizarse correctamente.");
    }

    @Test
    void updateActivityReportFailsWhenNotExists() throws SQLException {
        ActivityReportDTO report = new ActivityReportDTO("999", "102");
        boolean updated = activityReportDAO.updateActivityReport(report);
        assertFalse(updated, "No debería permitir actualizar un reporte inexistente.");
    }

    @Test
    void deleteActivityReportSuccessfully() throws SQLException {
        ActivityReportDTO report = new ActivityReportDTO(String.valueOf(idReporte), String.valueOf(idActividad));
        activityReportDAO.insertActivityReport(report);
        boolean deleted = activityReportDAO.deleteActivityReport(String.valueOf(idReporte));
        assertTrue(deleted, "El reporte debería eliminarse correctamente.");
    }

    @Test
    void deleteActivityReportFailsWhenNotExists() throws SQLException {
        boolean deleted = activityReportDAO.deleteActivityReport("999");
        assertFalse(deleted, "No debería permitir eliminar un reporte inexistente.");
    }

    @Test
    void searchActivityReportByReportNumberWhenExists() throws SQLException {
        ActivityReportDTO report = new ActivityReportDTO(String.valueOf(idReporte), String.valueOf(idActividad));
        activityReportDAO.insertActivityReport(report);
        ActivityReportDTO result = activityReportDAO.searchActivityReportByReportNumber(String.valueOf(idReporte));
        assertNotNull(result, "El reporte no debería ser nulo.");
        assertEquals(String.valueOf(idReporte), result.getNumberReport());
        assertEquals(String.valueOf(idActividad), result.getIdActivity());
    }

    @Test
    void searchActivityReportByReportNumberWhenNotExists() throws SQLException {
        ActivityReportDTO result = activityReportDAO.searchActivityReportByReportNumber("999");
        assertNotNull(result, "El reporte no debería ser nulo.");
        assertEquals("N/A", result.getNumberReport());
        assertEquals("N/A", result.getIdActivity());
    }

    @Test
    void getAllActivityReportsReturnsList() throws SQLException {
        ActivityReportDTO report1 = new ActivityReportDTO(String.valueOf(idReporte), String.valueOf(idActividad));
        activityReportDAO.insertActivityReport(report1);

        // Crear un segundo reporte y actividad
        int idReporte2, idActividad2;
        // Insertar actividad
        String insertActivitySQL = "INSERT INTO actividad (nombreActividad) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(insertActivitySQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Actividad Test 2");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                idActividad2 = rs.getInt(1);
            }
        }
        // Insertar reporte
        String insertReportSQL = "INSERT INTO reporte (observaciones, idEvidencia) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertReportSQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Observaciones Test 2");
            ps.setInt(2, idEvidencia);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                idReporte2 = rs.getInt(1);
            }
        }
        ActivityReportDTO report2 = new ActivityReportDTO(String.valueOf(idReporte2), String.valueOf(idActividad2));
        activityReportDAO.insertActivityReport(report2);

        List<ActivityReportDTO> result = activityReportDAO.getAllActivityReports();
        assertNotNull(result, "La lista de reportes no debería ser nula.");
        assertEquals(2, result.size(), "Debería haber exactamente 2 reportes en la lista.");
    }

    @Test
    void getAllActivityReportsReturnsEmptyListWhenNoReportsExist() throws SQLException {
        // Limpia la tabla reporte_actividad para asegurar que no haya registros
        connection.createStatement().execute("DELETE FROM reporte_actividad");
        List<ActivityReportDTO> result = activityReportDAO.getAllActivityReports();
        assertNotNull(result, "La lista de reportes no debería ser nula.");
        assertTrue(result.isEmpty(), "La lista de reportes debería estar vacía.");
    }
}