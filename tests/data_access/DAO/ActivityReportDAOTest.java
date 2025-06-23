package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.*;
import logic.DTO.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActivityReportDAOTest {
    private ConnectionDataBase connectionDB;
    private Connection connection;
    private ActivityReportDAO activityReportDAO;
    private LinkedOrganizationDAO organizationDAO;
    private UserDAO userDAO;
    private DepartmentDAO departmentDAO;
    private ProjectDAO projectDAO;
    private StudentDAO studentDAO;
    private EvidenceDAO evidenceDAO;
    private ActivityDAO activityDAO;
    private ReportDAO reportDAO;

    private int evidenceId;
    private int reportId;
    private int activityId;
    private int projectId;
    private int userId;
    private int organizationId;
    private final int TEST_PERIOD_ID = 1001;
    private final int TEST_NRC = 11111;
    private String studentEnrollment = "S23014958";
    private int departmentId;

    @BeforeAll
    void setUpAll() throws Exception {
        connectionDB = new ConnectionDataBase();
        connection = connectionDB.connectDB();
        activityReportDAO = new ActivityReportDAO();
        organizationDAO = new LinkedOrganizationDAO();
        userDAO = new UserDAO();
        departmentDAO = new DepartmentDAO();
        projectDAO = new ProjectDAO();
        studentDAO = new StudentDAO();
        evidenceDAO = new EvidenceDAO();
        activityDAO = new ActivityDAO();
        reportDAO = new ReportDAO();
        clearTablesAndResetAutoIncrement();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createBaseData();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS=0");
        stmt.execute("TRUNCATE TABLE reporte_actividad");
        stmt.execute("TRUNCATE TABLE reporte");
        stmt.execute("TRUNCATE TABLE actividad");
        stmt.execute("TRUNCATE TABLE evidencia");
        stmt.execute("TRUNCATE TABLE proyecto");
        stmt.execute("TRUNCATE TABLE usuario");
        stmt.execute("TRUNCATE TABLE departamento");
        stmt.execute("TRUNCATE TABLE organizacion_vinculada");
        stmt.execute("TRUNCATE TABLE estudiante");
        stmt.execute("TRUNCATE TABLE grupo");
        stmt.execute("TRUNCATE TABLE periodo");
        stmt.execute("ALTER TABLE evidencia AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE reporte AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE actividad AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        try { stmt.execute("ALTER TABLE reporte_actividad AUTO_INCREMENT = 1"); } catch (SQLException ignored) {}
        stmt.execute("SET FOREIGN_KEY_CHECKS=1");
        stmt.close();
    }

    private void createBaseData() throws SQLException, IOException {
        String sqlPeriod = "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlPeriod)) {
            stmt.setInt(1, TEST_PERIOD_ID);
            stmt.setString(2, "Periodo Test");
            stmt.setDate(3, java.sql.Date.valueOf("2024-01-01"));
            stmt.setDate(4, java.sql.Date.valueOf("2024-12-31"));
            stmt.executeUpdate();
        }

        String sqlGroup = "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlGroup)) {
            stmt.setInt(1, TEST_NRC);
            stmt.setString(2, "Grupo Test");
            stmt.setNull(3, java.sql.Types.INTEGER); // idUsuario puede ser null
            stmt.setInt(4, TEST_PERIOD_ID);
            stmt.executeUpdate();
        }

        StudentDTO student = new StudentDTO(
                studentEnrollment, 1, "Juan", "Perez", "1234567890",
                "juan.perez@example.com", "juanperez", "password", String.valueOf(TEST_NRC), "50", 0.0
        );
        studentDAO.insertStudent(student);

        LinkedOrganizationDTO organization = new LinkedOrganizationDTO(null, "Org Test", "Dirección Test", 1);
        organizationId = Integer.parseInt(organizationDAO.insertLinkedOrganizationAndGetId(organization));

        UserDTO user = new UserDTO(null,1,"12345", "Nombre", "Apellido", "usuarioTest", "contraseñaTest123456789012345678901234567890", Role.ACADEMICO);
        userId = insertUserAndGetId(user);

        DepartmentDTO department = new DepartmentDTO(0, "Dept Test", "Descripción test", organizationId, 1);
        departmentDAO.insertDepartment(department);
        List<DepartmentDTO> departments = departmentDAO.getAllDepartmentsByOrganizationId(organizationId);
        departmentId = departments.get(0).getDepartmentId();

        ProjectDTO project = new ProjectDTO(null, "Proyecto Test", "Descripción Test",
                new java.sql.Timestamp(System.currentTimeMillis()),
                new java.sql.Timestamp(System.currentTimeMillis()),
                String.valueOf(userId), organizationId, departmentId);
        projectId = insertProjectAndGetId(project);

        // 7. Evidence using EvidenceDAO
        int nextEvidenceId = evidenceDAO.getNextEvidenceId();
        EvidenceDTO evidence = new EvidenceDTO(nextEvidenceId, "Evidencia Test", new java.util.Date(), "/ruta/evidencia");
        evidenceDAO.insertEvidence(evidence);
        evidenceId = nextEvidenceId;

        // 8. Activity using ActivityDAO
        activityId = getNextActivityId();
        ActivityDTO activity = new ActivityDTO(String.valueOf(activityId), "Actividad Test");
        activityDAO.insertActivity(activity);

        // 9. Report using ReportDAO
        ReportDTO report = new ReportDTO(
                null,
                new java.util.Date(),
                10,
                "Objetivo Test",
                "Metodologia Test",
                "Resultado Test",
                projectId,
                studentEnrollment,
                "Observaciones Test",
                String.valueOf(evidenceId)
        );
        reportDAO.insertReport(report);
        reportId = Integer.parseInt(report.getNumberReport());
    }

    private int insertUserAndGetId(UserDTO user) throws SQLException, IOException {
        String sql = "INSERT INTO usuario (numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getStaffNumber());
            stmt.setString(2, user.getNames());
            stmt.setString(3, user.getSurnames());
            stmt.setString(4, user.getUserName());
            stmt.setString(5, user.getPassword());
            stmt.setString(6, user.getRole().toString());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el id del usuario insertado");
    }

    private int insertProjectAndGetId(ProjectDTO project) throws SQLException, IOException {
        String sql = "INSERT INTO proyecto (nombre, descripcion, fechaAproximada, fechaInicio, idUsuario, idOrganizacion) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, project.getName());
            stmt.setString(2, project.getDescription());
            stmt.setTimestamp(3, project.getApproximateDate());
            stmt.setTimestamp(4, project.getStartDate());
            stmt.setString(5, project.getIdUser());
            stmt.setInt(6, project.getIdOrganization());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el id del proyecto insertado");
    }

    private int getNextActivityId() throws SQLException {
        String sql = "SELECT IFNULL(MAX(idActividad), 0) + 1 FROM actividad";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 1;
    }

    @AfterAll
    void tearDownAll() throws SQLException, IOException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        if (connectionDB != null) {
            connectionDB.close();
        }
    }

    @AfterEach
    void tearDown() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
    }

    @Test
    void insertActivityReportSuccessfully() throws SQLException, IOException {
        ActivityReportDTO report = new ActivityReportDTO(
                String.valueOf(reportId),
                String.valueOf(activityId),
                50,
                "Observaciones de avance"
        );
        boolean inserted = activityReportDAO.insertActivityReport(report);
        assertTrue(inserted, "El reporte debería insertarse correctamente.");
    }

    @Test
    void updateActivityReportSuccessfully() throws SQLException, IOException {
        ActivityReportDTO report = new ActivityReportDTO(
                String.valueOf(reportId),
                String.valueOf(activityId),
                50,
                "Observaciones de avance"
        );
        activityReportDAO.insertActivityReport(report);
        ActivityReportDTO updatedReport = new ActivityReportDTO(
                String.valueOf(reportId),
                String.valueOf(activityId),
                80,
                "Observaciones actualizadas"
        );
        boolean updated = activityReportDAO.updateActivityReport(updatedReport);
        assertTrue(updated, "El reporte debería actualizarse correctamente.");
    }

    @Test
    void updateActivityReportFailsWhenNotExists() throws SQLException, IOException {
        ActivityReportDTO report = new ActivityReportDTO("999", "102", 10, "No existe");
        boolean updated = activityReportDAO.updateActivityReport(report);
        assertFalse(updated, "No debería permitir actualizar un reporte inexistente.");
    }

    @Test
    void deleteActivityReportSuccessfully() throws SQLException, IOException {
        ActivityReportDTO report = new ActivityReportDTO(
                String.valueOf(reportId),
                String.valueOf(activityId),
                50,
                "Observaciones de avance"
        );
        activityReportDAO.insertActivityReport(report);
        boolean deleted = activityReportDAO.deleteActivityReport(String.valueOf(reportId));
        assertTrue(deleted, "El reporte debería eliminarse correctamente.");
    }

    @Test
    void deleteActivityReportFailsWhenNotExists() throws SQLException, IOException {
        boolean deleted = activityReportDAO.deleteActivityReport("999");
        assertFalse(deleted, "No debería permitir eliminar un reporte inexistente.");
    }

    @Test
    void searchActivityReportByReportNumberWhenExists() throws SQLException, IOException {
        ActivityReportDTO report = new ActivityReportDTO(
                String.valueOf(reportId),
                String.valueOf(activityId),
                50,
                "Observaciones de avance"
        );
        activityReportDAO.insertActivityReport(report);
        ActivityReportDTO result = activityReportDAO.searchActivityReportByReportNumber(String.valueOf(reportId));
        assertNotNull(result, "El reporte no debería ser nulo.");
        assertEquals(String.valueOf(reportId), result.getNumberReport());
        assertEquals(String.valueOf(activityId), result.getIdActivity());
        assertEquals(50, result.getProgressPercentage());
        assertEquals("Observaciones de avance", result.getObservations());
    }

    @Test
    void searchActivityReportByReportNumberWhenNotExists() throws SQLException, IOException {
        ActivityReportDTO result = activityReportDAO.searchActivityReportByReportNumber("999");
        assertNotNull(result, "El reporte no debería ser nulo.");
        assertEquals("N/A", result.getNumberReport());
        assertEquals("N/A", result.getIdActivity());
    }

    @Test
    void getAllActivityReportsReturnsList() throws SQLException, IOException {
        // Insert first activity report
        ActivityReportDTO report1 = new ActivityReportDTO(
                String.valueOf(reportId),
                String.valueOf(activityId),
                50,
                "Observaciones de avance"
        );
        activityReportDAO.insertActivityReport(report1);

        int evidenceId2 = evidenceDAO.getNextEvidenceId();
        EvidenceDTO evidence2 = new EvidenceDTO(evidenceId2, "Evidencia Test 2", new java.util.Date(), "/ruta/evidencia2");
        evidenceDAO.insertEvidence(evidence2);

        int activityId2 = getNextActivityId();
        ActivityDTO activity2 = new ActivityDTO(String.valueOf(activityId2), "Actividad Test 2");
        activityDAO.insertActivity(activity2);

        ReportDTO report2 = new ReportDTO(
                null,
                new java.util.Date(),
                12,
                "Objetivo Test 2",
                "Metodologia Test 2",
                "Resultado Test 2",
                projectId,
                studentEnrollment,
                "Observaciones Test 2",
                String.valueOf(evidenceId2)
        );
        reportDAO.insertReport(report2);
        int reportId2 = Integer.parseInt(report2.getNumberReport());

        ActivityReportDTO activityReport2 = new ActivityReportDTO(
                String.valueOf(reportId2),
                String.valueOf(activityId2),
                80,
                "Observaciones de avance 2"
        );
        activityReportDAO.insertActivityReport(activityReport2);

        List<ActivityReportDTO> result = activityReportDAO.getAllActivityReports();
        assertNotNull(result, "La lista de reportes no debería ser nula.");
        assertEquals(2, result.size(), "Debe haber dos reportes de actividad.");
    }

    @Test
    void insertDuplicateActivityReportFails() throws SQLException, IOException {
        ActivityReportDTO report = new ActivityReportDTO(
                String.valueOf(reportId),
                String.valueOf(activityId),
                50,
                "Observaciones"
        );
        assertTrue(activityReportDAO.insertActivityReport(report));
        // Try to insert the same activity report (same PK)
        assertThrows(SQLException.class, () -> activityReportDAO.insertActivityReport(report));
    }

    @Test
    void insertActivityReportWithInvalidForeignKeysFails() {
        ActivityReportDTO report = new ActivityReportDTO(
                "99999", // non-existent report number
                "88888", // non-existent activity id
                10,
                "Datos inválidos"
        );
        assertThrows(SQLException.class, () -> activityReportDAO.insertActivityReport(report));
    }

    @Test
    void getAllActivityReportsReturnsEmptyListWhenNoneExist() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
        List<ActivityReportDTO> result = activityReportDAO.getAllActivityReports();
        assertNotNull(result);
        assertTrue(result.isEmpty(), "La lista debe estar vacía si no hay reportes.");
    }

    @Test
    void activityReportDTOEqualsAndToStringWork() {
        ActivityReportDTO report1 = new ActivityReportDTO("1", "2", 50, "Obs");
        ActivityReportDTO report2 = new ActivityReportDTO("1", "2", 50, "Obs");
        ActivityReportDTO report3 = new ActivityReportDTO("2", "3", 10, "Otro");
        assertEquals(report1, report2, "equals debe ser true para objetos iguales");
        assertNotEquals(report1, report3, "equals debe ser false para objetos distintos");
        assertTrue(report1.toString().contains("numberReport='1'"), "toString debe contener los campos");
    }

    @Test
    void insertMultipleActivityReportsSuccessfully() throws SQLException, IOException {
        for (int i = 0; i < 5; i++) {
            // Create new evidence, activity and report for each iteration
            int evidenceIdX = evidenceDAO.getNextEvidenceId();
            evidenceDAO.insertEvidence(new EvidenceDTO(evidenceIdX, "Evidencia " + i, new java.util.Date(), "/ruta/evidencia" + i));
            int activityIdX = getNextActivityId();
            activityDAO.insertActivity(new ActivityDTO(String.valueOf(activityIdX), "Actividad " + i));
            ReportDTO report = new ReportDTO(
                    null, new java.util.Date(), 10 + i, "Obj " + i, "Met " + i, "Res " + i,
                    projectId, studentEnrollment, "Obs " + i, String.valueOf(evidenceIdX)
            );
            reportDAO.insertReport(report);
            int reportIdX = Integer.parseInt(report.getNumberReport());
            ActivityReportDTO ar = new ActivityReportDTO(String.valueOf(reportIdX), String.valueOf(activityIdX), 10 * i, "Obs " + i);
            assertTrue(activityReportDAO.insertActivityReport(ar));
        }
        List<ActivityReportDTO> all = activityReportDAO.getAllActivityReports();
        assertEquals(5, all.size(), "Debe haber 5 reportes de actividad insertados.");
    }
}