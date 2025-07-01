package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.*;
import logic.DTO.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.*;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReportDAOTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private ReportDAO reportDAO;
    private UserDAO userDAO;
    private DepartmentDAO departmentDAO;
    private LinkedOrganizationDAO organizationDAO;
    private ProjectDAO projectDAO;
    private StudentDAO studentDAO;
    private GroupDAO groupDAO;
    private PeriodDAO periodDAO;

    private int testEvidenceId;
    private int userId;
    private int organizationId;
    private int projectId;
    private String studentTuition;
    private String testNRC = "11111";
    private String testPeriodId = "1001";
    private int departmentId;

    @BeforeAll
    void setUpAll() throws SQLException, IOException {
        connectionDB = new ConnectionDataBase();
        connection = connectionDB.connectDataBase();
        userDAO = new UserDAO();
        departmentDAO = new DepartmentDAO();
        organizationDAO = new LinkedOrganizationDAO();
        projectDAO = new ProjectDAO();
        studentDAO = new StudentDAO();
        groupDAO = new GroupDAO();
        periodDAO = new PeriodDAO();
        reportDAO = new ReportDAO();

        clearTablesAndResetAutoIncrement();
        createBasePeriod();
        createBaseGroup();
        createBaseUserAndOrganization();
        createBaseProject();
        createBaseStudent();
        createBaseEvidence();
    }

    @BeforeEach
    void setUp() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
        createBasePeriod();
        createBaseGroup();
        createBaseUserAndOrganization();
        createBaseProject();
        createBaseStudent();
        createBaseEvidence();
    }

    @AfterAll
    void tearDownAll() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        if (connectionDB != null) {
            connectionDB.close();
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        clearTablesAndResetAutoIncrement();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS=0");
        stmt.execute("TRUNCATE TABLE reporte");
        stmt.execute("TRUNCATE TABLE evidencia");
        stmt.execute("TRUNCATE TABLE estudiante");
        stmt.execute("TRUNCATE TABLE grupo");
        stmt.execute("TRUNCATE TABLE departamento");
        stmt.execute("TRUNCATE TABLE periodo");
        stmt.execute("TRUNCATE TABLE proyecto");
        stmt.execute("TRUNCATE TABLE usuario");
        stmt.execute("TRUNCATE TABLE organizacion_vinculada");
        stmt.execute("ALTER TABLE reporte AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE evidencia AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE estudiante AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE grupo AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE periodo AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        stmt.execute("SET FOREIGN_KEY_CHECKS=1");
        stmt.close();
    }

    private void createBasePeriod() throws SQLException, IOException {
        PeriodDTO period = new PeriodDTO(
                testPeriodId,
                "Periodo Test",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis())
        );
        periodDAO.insertPeriod(period);
    }

    private void createBaseGroup() throws SQLException, IOException {
        GroupDTO group = new GroupDTO(
                testNRC,
                "Grupo Test",
                null,
                testPeriodId
        );
        groupDAO.insertGroup(group);
    }

    private void createBaseUserAndOrganization() throws SQLException, IOException {
        LinkedOrganizationDTO org = new LinkedOrganizationDTO(null, "Org Test", "Dirección Test", 1);
        organizationId = Integer.parseInt(organizationDAO.insertLinkedOrganizationAndGetId(org));

        departmentId = createTestDepartment();

        UserDTO user = new UserDTO(null, 1, "12345", "Nombre", "Apellido", "usuarioTest", "passTest", Role.ACADEMIC);
        userId = insertUserAndGetId(user);
    }

    private int insertUserAndGetId(UserDTO user) throws SQLException {
        String sql = "INSERT INTO usuario (numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getStaffNumber());
            stmt.setString(2, user.getNames());
            stmt.setString(3, user.getSurnames());
            stmt.setString(4, user.getUserName());
            stmt.setString(5, user.getPassword());
            stmt.setString(6, user.getRole().toString());
            stmt.setInt(7, user.getStatus());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el id del usuario insertado");
    }

    private int createTestDepartment() throws SQLException {
        String sql = "INSERT INTO departamento (nombre, descripcion, idOrganizacion, estado) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "Dept test");
            stmt.setString(2, "Description test");
            stmt.setInt(3, organizationId);
            stmt.setInt(4, 1);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el id del departamento insertado");
    }

    private void createBaseProject() throws SQLException, IOException {
        ProjectDTO project = new ProjectDTO(
                null,
                "Proyecto Test",
                "Descripción Test",
                new java.sql.Timestamp(System.currentTimeMillis()),
                new java.sql.Timestamp(System.currentTimeMillis()),
                String.valueOf(userId),
                organizationId,
                departmentId
        );
        boolean inserted = projectDAO.insertProject(project);
        assertTrue(inserted, "El proyecto debe insertarse correctamente");
        List<ProjectDTO> projects = projectDAO.getAllProjects();
        assertFalse(projects.isEmpty());
        projectId = Integer.parseInt(projects.get(0).getIdProject());
    }

    private void createBaseStudent() throws SQLException, IOException {
        studentTuition = "2023123456";
        StudentDTO student = new StudentDTO(
                studentTuition,
                1,
                "Juan",
                "Pérez",
                "1234567890",
                "juan.perez@example.com",
                "juanperez",
                "password",
                testNRC,
                "50",
                0.0
        );
        boolean inserted = studentDAO.insertStudent(student);
        assertTrue(inserted, "El estudiante debe insertarse correctamente");
    }

    private void createBaseEvidence() throws SQLException {
        String sql = "INSERT INTO evidencia (nombreEvidencia, fechaEntrega, ruta) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "Evidencia Test");
            stmt.setDate(2, java.sql.Date.valueOf("2024-06-01"));
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
    void insertReportSuccessfully() throws SQLException, IOException {
        ReportDTO report = new ReportDTO(
                null,
                new Date(),
                10,
                "Objetivo general",
                "Metodología",
                "Resultado obtenido",
                projectId,
                studentTuition,
                "Observaciones de prueba",
                String.valueOf(testEvidenceId)
        );
        boolean result = reportDAO.insertReport(report);
        assertTrue(result, "La inserción debería ser exitosa");

        ReportDTO inserted = reportDAO.searchReportById(report.getNumberReport());
        assertNotNull(inserted);
        assertEquals("Observaciones de prueba", inserted.getObservations());
        assertEquals(String.valueOf(testEvidenceId), inserted.getIdEvidence());
        assertEquals(studentTuition, inserted.getTuition());
    }

    @Test
    void searchReportByIdSuccessfully() throws SQLException, IOException {
        String reportNumber = insertTestReport("Observaciones para obtener", String.valueOf(testEvidenceId));
        ReportDTO retrieved = reportDAO.searchReportById(reportNumber);
        assertNotNull(retrieved);
        assertEquals("Observaciones para obtener", retrieved.getObservations());
        assertEquals(String.valueOf(testEvidenceId), retrieved.getIdEvidence());
        assertEquals(studentTuition, retrieved.getTuition());
    }

    @Test
    void updateReportSuccessfully() throws SQLException, IOException {
        String reportNumber = insertTestReport("Observaciones iniciales", String.valueOf(testEvidenceId));
        ReportDTO updated = new ReportDTO(
                reportNumber,
                new Date(),
                12,
                "Objetivo actualizado",
                "Metodología actualizada",
                "Resultado actualizado",
                projectId,
                studentTuition,
                "Observaciones actualizadas",
                String.valueOf(testEvidenceId)
        );
        boolean result = reportDAO.updateReport(updated);
        assertTrue(result);

        ReportDTO retrieved = reportDAO.searchReportById(reportNumber);
        assertEquals("Observaciones actualizadas", retrieved.getObservations());
        assertEquals(studentTuition, retrieved.getTuition());
    }

    @Test
    void deleteReportSuccessfully() throws SQLException, IOException {
        ReportDTO report = new ReportDTO(
                null,
                new java.util.Date(),
                10,
                "Objetivo",
                "Metodología",
                "Resultado",
                projectId,
                studentTuition,
                "Observaciones",
                String.valueOf(testEvidenceId)
        );
        reportDAO.insertReport(report);
        String id = report.getNumberReport();

        boolean deleted = reportDAO.deleteReport(id);
        assertTrue(deleted);

        ReportDTO deletedReport = reportDAO.searchReportById(id);
        assertEquals("N/A", deletedReport.getNumberReport());
    }

    @Test
    void getAllReportsSuccessfully() throws SQLException, IOException {
        insertTestReport("Observaciones 1", String.valueOf(testEvidenceId));
        insertTestReport("Observaciones 2", String.valueOf(testEvidenceId));
        List<ReportDTO> reports = reportDAO.getAllReports();
        assertNotNull(reports);
        assertEquals(2, reports.size());
    }

    private String insertTestReport(String observations, String evidenceId) throws SQLException, IOException {
        ReportDTO report = new ReportDTO(
                null,
                new Date(),
                10,
                "Objetivo test",
                "Metodología test",
                "Resultado test",
                projectId,
                studentTuition,
                observations,
                evidenceId
        );
        boolean result = reportDAO.insertReport(report);
        assertTrue(result, "La inserción de prueba debe ser exitosa");
        return report.getNumberReport();
    }

    @Test
    void insertReportWithNullRequiredFields() {
        ReportDTO report = new ReportDTO(
                null,
                null, // Null date
                10,
                null, // Null objective
                "Metodología",
                "Resultado",
                projectId,
                studentTuition,
                "Observaciones",
                String.valueOf(testEvidenceId)
        );
        assertThrows(NullPointerException.class, () -> reportDAO.insertReport(report));
    }

    @Test
    void searchNonExistentReport() throws SQLException, IOException {
        ReportDTO report = reportDAO.searchReportById("99999");
        assertNotNull(report, "El método nunca retorna null");
        assertEquals("N/A", report.getNumberReport());
        assertNull(report.getReportDate());
        assertEquals(0, report.getTotalHours());
        assertEquals("N/A", report.getGeneralObjective());
        assertEquals("N/A", report.getMethodology());
        assertEquals("N/A", report.getObtainedResult());
        assertEquals(0, report.getProjectId());
        assertEquals("N/A", report.getTuition());
        assertEquals("N/A", report.getObservations());
        assertEquals("0", report.getIdEvidence());
    }

    @Test
    void updateNonExistentReport() throws SQLException, IOException {
        ReportDTO report = new ReportDTO(
                "99999",
                new Date(),
                10,
                "Objetivo",
                "Metodología",
                "Resultado",
                projectId,
                studentTuition,
                "Observaciones",
                String.valueOf(testEvidenceId)
        );
        boolean result = reportDAO.updateReport(report);
        assertFalse(result, "No debe actualizar un reporte inexistente");
    }

    @Test
    void deleteNonExistentReport() throws SQLException, IOException {
        boolean result = reportDAO.deleteReport("99999");
        assertFalse(result, "No debe eliminar un reporte inexistente");
    }

    @Test
    void insertMultipleReportsSuccessfully() throws SQLException, IOException {
        String num1 = insertTestReport("Observaciones 1", String.valueOf(testEvidenceId));
        String num2 = insertTestReport("Observaciones 2", String.valueOf(testEvidenceId));
        List<ReportDTO> reports = reportDAO.getAllReports();
        assertEquals(2, reports.size(), "Debe haber dos reportes insertados");
        assertTrue(reports.stream().anyMatch(r -> r.getNumberReport().equals(num1)));
        assertTrue(reports.stream().anyMatch(r -> r.getNumberReport().equals(num2)));
    }

    @Test
    void insertReportWithInvalidForeignKeys() {
        ReportDTO report = new ReportDTO(
                null,
                new Date(),
                10,
                "Objetivo",
                "Metodología",
                "Resultado",
                99999,
                "9999999999",
                "Observaciones",
                String.valueOf(testEvidenceId)
        );
        assertThrows(SQLException.class, () -> reportDAO.insertReport(report));
    }
}