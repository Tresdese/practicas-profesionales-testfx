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
    private ConnectionDataBase connectionDataBase;
    private Connection connection;
    private ActivityReportDAO activityReportDataAccessObject;
    private LinkedOrganizationDAO linkedOrganizationDataAccessObject;
    private UserDAO userDataAccessObject;
    private DepartmentDAO departmentDataAccessObject;
    private ProjectDAO projectDataAccessObject;
    private StudentDAO studentDataAccessObject;
    private EvidenceDAO evidenceDataAccessObject;
    private ActivityDAO activityDataAccessObject;
    private ReportDAO reportDataAccessObject;

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
        connectionDataBase = new ConnectionDataBase();
        connection = connectionDataBase.connectDataBase();
        activityReportDataAccessObject = new ActivityReportDAO();
        linkedOrganizationDataAccessObject = new LinkedOrganizationDAO();
        userDataAccessObject = new UserDAO();
        departmentDataAccessObject = new DepartmentDAO();
        projectDataAccessObject = new ProjectDAO();
        studentDataAccessObject = new StudentDAO();
        evidenceDataAccessObject = new EvidenceDAO();
        activityDataAccessObject = new ActivityDAO();
        reportDataAccessObject = new ReportDAO();
        clearTablesAndResetAutoIncrement();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createBaseData();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SET FOREIGN_KEY_CHECKS=0");
        statement.execute("TRUNCATE TABLE reporte_actividad");
        statement.execute("TRUNCATE TABLE reporte");
        statement.execute("TRUNCATE TABLE actividad");
        statement.execute("TRUNCATE TABLE evidencia");
        statement.execute("TRUNCATE TABLE proyecto");
        statement.execute("TRUNCATE TABLE usuario");
        statement.execute("TRUNCATE TABLE departamento");
        statement.execute("TRUNCATE TABLE organizacion_vinculada");
        statement.execute("TRUNCATE TABLE estudiante");
        statement.execute("TRUNCATE TABLE grupo");
        statement.execute("TRUNCATE TABLE periodo");
        statement.execute("ALTER TABLE evidencia AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE reporte AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE actividad AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        try { statement.execute("ALTER TABLE reporte_actividad AUTO_INCREMENT = 1"); } catch (SQLException ignored) {}
        statement.execute("SET FOREIGN_KEY_CHECKS=1");
        statement.close();
    }

    private void createBaseData() throws SQLException, IOException {
        String sqlPeriod = "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlPeriod)) {
            statement.setInt(1, TEST_PERIOD_ID);
            statement.setString(2, "Periodo Test");
            statement.setDate(3, java.sql.Date.valueOf("2024-01-01"));
            statement.setDate(4, java.sql.Date.valueOf("2024-12-31"));
            statement.executeUpdate();
        }

        String sqlGroup = "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlGroup)) {
            statement.setInt(1, TEST_NRC);
            statement.setString(2, "Grupo Test");
            statement.setNull(3, java.sql.Types.INTEGER);
            statement.setInt(4, TEST_PERIOD_ID);
            statement.executeUpdate();
        }

        StudentDTO student = new StudentDTO(
                studentEnrollment, 1, "Juan", "Perez", "1234567890",
                "juan.perez@example.com", "juanperez", "password", String.valueOf(TEST_NRC), "50", 0.0
        );
        studentDataAccessObject.insertStudent(student);

        LinkedOrganizationDTO linkedOrganization = new LinkedOrganizationDTO(null, "Org Test", "Dirección Test", 1);
        organizationId = Integer.parseInt(linkedOrganizationDataAccessObject.insertLinkedOrganizationAndGetId(linkedOrganization));

        UserDTO user = new UserDTO(null,1,"12345", "Nombre", "Apellido", "usuarioTest", "contraseñaTest123456789012345678901234567890", Role.ACADEMIC);
        userId = insertUserAndGetId(user);

        DepartmentDTO department = new DepartmentDTO(0, "Dept Test", "Descripción test", organizationId, 1);
        departmentDataAccessObject.insertDepartment(department);
        List<DepartmentDTO> departmentList = departmentDataAccessObject.getAllDepartmentsByOrganizationId(organizationId);
        departmentId = departmentList.get(0).getDepartmentId();

        ProjectDTO project = new ProjectDTO(null, "Proyecto Test", "Descripción Test",
                new java.sql.Timestamp(System.currentTimeMillis()),
                new java.sql.Timestamp(System.currentTimeMillis()),
                String.valueOf(userId), organizationId, departmentId);
        projectId = insertProjectAndGetId(project);

        int nextEvidenceId = evidenceDataAccessObject.getNextEvidenceId();
        EvidenceDTO evidence = new EvidenceDTO(nextEvidenceId, "Evidencia Test", new java.util.Date(), "/ruta/evidencia");
        evidenceDataAccessObject.insertEvidence(evidence);
        evidenceId = nextEvidenceId;

        activityId = getNextActivityId();
        ActivityDTO activity = new ActivityDTO(String.valueOf(activityId), "Actividad Test");
        activityDataAccessObject.insertActivity(activity);

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
        reportDataAccessObject.insertReport(report);
        reportId = Integer.parseInt(report.getNumberReport());
    }

    private int insertUserAndGetId(UserDTO user) throws SQLException, IOException {
        String sql = "INSERT INTO usuario (numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getStaffNumber());
            statement.setString(2, user.getNames());
            statement.setString(3, user.getSurnames());
            statement.setString(4, user.getUserName());
            statement.setString(5, user.getPassword());
            statement.setString(6, user.getRole().toString());
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el id del usuario insertado");
    }

    private int insertProjectAndGetId(ProjectDTO project) throws SQLException, IOException {
        String sql = "INSERT INTO proyecto (nombre, descripcion, fechaAproximada, fechaInicio, idUsuario, idOrganizacion) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, project.getName());
            statement.setString(2, project.getDescription());
            statement.setTimestamp(3, project.getApproximateDate());
            statement.setTimestamp(4, project.getStartDate());
            statement.setString(5, project.getIdUser());
            statement.setInt(6, project.getIdOrganization());
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el id del proyecto insertado");
    }

    private int getNextActivityId() throws SQLException {
        String sql = "SELECT IFNULL(MAX(idActividad), 0) + 1 FROM actividad";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 1;
    }

    @AfterAll
    void tearDownAll() throws SQLException, IOException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        if (connectionDataBase != null) {
            connectionDataBase.close();
        }
    }

    @AfterEach
    void tearDown() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
    }

    @Test
    void insertActivityReportSuccessfully() throws SQLException, IOException {
        ActivityReportDTO activityReport = new ActivityReportDTO(
                String.valueOf(reportId),
                String.valueOf(activityId),
                50,
                "Observaciones de avance"
        );
        boolean wasInserted = activityReportDataAccessObject.insertActivityReport(activityReport);
        assertTrue(wasInserted, "El reporte debería insertarse correctamente.");
    }

    @Test
    void updateActivityReportSuccessfully() throws SQLException, IOException {
        ActivityReportDTO activityReport = new ActivityReportDTO(
                String.valueOf(reportId),
                String.valueOf(activityId),
                50,
                "Observaciones de avance"
        );
        activityReportDataAccessObject.insertActivityReport(activityReport);
        ActivityReportDTO updatedActivityReport = new ActivityReportDTO(
                String.valueOf(reportId),
                String.valueOf(activityId),
                80,
                "Observaciones actualizadas"
        );
        boolean wasUpdated = activityReportDataAccessObject.updateActivityReport(updatedActivityReport);
        assertTrue(wasUpdated, "El reporte debería actualizarse correctamente.");
    }

    @Test
    void updateActivityReportFailsWhenNotExists() throws SQLException, IOException {
        ActivityReportDTO nonExistentActivityReport = new ActivityReportDTO("999", "102", 10, "No existe");
        boolean wasUpdated = activityReportDataAccessObject.updateActivityReport(nonExistentActivityReport);
        assertFalse(wasUpdated, "No debería permitir actualizar un reporte inexistente.");
    }

    @Test
    void deleteActivityReportSuccessfully() throws SQLException, IOException {
        ActivityReportDTO activityReport = new ActivityReportDTO(
                String.valueOf(reportId),
                String.valueOf(activityId),
                50,
                "Observaciones de avance"
        );
        activityReportDataAccessObject.insertActivityReport(activityReport);
        boolean wasDeleted = activityReportDataAccessObject.deleteActivityReport(String.valueOf(reportId));
        assertTrue(wasDeleted, "El reporte debería eliminarse correctamente.");
    }

    @Test
    void deleteActivityReportFailsWhenNotExists() throws SQLException, IOException {
        boolean wasDeleted = activityReportDataAccessObject.deleteActivityReport("999");
        assertFalse(wasDeleted, "No debería permitir eliminar un reporte inexistente.");
    }

    @Test
    void searchActivityReportByReportNumberWhenExists() throws SQLException, IOException {
        ActivityReportDTO activityReport = new ActivityReportDTO(
                String.valueOf(reportId),
                String.valueOf(activityId),
                50,
                "Observaciones de avance"
        );
        activityReportDataAccessObject.insertActivityReport(activityReport);
        ActivityReportDTO result = activityReportDataAccessObject.searchActivityReportByReportNumber(String.valueOf(reportId));
        assertNotNull(result, "El reporte no debería ser nulo.");
        assertEquals(String.valueOf(reportId), result.getNumberReport());
        assertEquals(String.valueOf(activityId), result.getIdActivity());
        assertEquals(50, result.getProgressPercentage());
        assertEquals("Observaciones de avance", result.getObservations());
    }

    @Test
    void searchActivityReportByReportNumberWhenNotExists() throws SQLException, IOException {
        ActivityReportDTO result = activityReportDataAccessObject.searchActivityReportByReportNumber("999");
        assertNotNull(result, "El reporte no debería ser nulo.");
        assertEquals("N/A", result.getNumberReport());
        assertEquals("N/A", result.getIdActivity());
    }

    @Test
    void getAllActivityReportsReturnsList() throws SQLException, IOException {
        ActivityReportDTO firstActivityReport = new ActivityReportDTO(
                String.valueOf(reportId),
                String.valueOf(activityId),
                50,
                "Observaciones de avance"
        );
        activityReportDataAccessObject.insertActivityReport(firstActivityReport);

        int evidenceId2 = evidenceDataAccessObject.getNextEvidenceId();
        EvidenceDTO secondEvidence = new EvidenceDTO(evidenceId2, "Evidencia Test 2", new java.util.Date(), "/ruta/evidencia2");
        evidenceDataAccessObject.insertEvidence(secondEvidence);

        int activityId2 = getNextActivityId();
        ActivityDTO secondActivity = new ActivityDTO(String.valueOf(activityId2), "Actividad Test 2");
        activityDataAccessObject.insertActivity(secondActivity);

        ReportDTO secondReport = new ReportDTO(
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
        reportDataAccessObject.insertReport(secondReport);
        int reportId2 = Integer.parseInt(secondReport.getNumberReport());

        ActivityReportDTO secondActivityReport = new ActivityReportDTO(
                String.valueOf(reportId2),
                String.valueOf(activityId2),
                80,
                "Observaciones de avance 2"
        );
        activityReportDataAccessObject.insertActivityReport(secondActivityReport);

        List<ActivityReportDTO> activityReportList = activityReportDataAccessObject.getAllActivityReports();
        assertNotNull(activityReportList, "La lista de reportes no debería ser nula.");
        assertEquals(2, activityReportList.size(), "Debe haber dos reportes de actividad.");
    }

    @Test
    void insertDuplicateActivityReportFails() throws SQLException, IOException {
        ActivityReportDTO activityReport = new ActivityReportDTO(
                String.valueOf(reportId),
                String.valueOf(activityId),
                50,
                "Observaciones"
        );
        assertTrue(activityReportDataAccessObject.insertActivityReport(activityReport));
        assertThrows(SQLException.class, () -> activityReportDataAccessObject.insertActivityReport(activityReport));
    }

    @Test
    void insertActivityReportWithInvalidForeignKeysFails() {
        ActivityReportDTO invalidActivityReport = new ActivityReportDTO(
                "99999",
                "88888",
                10,
                "Datos inválidos"
        );
        assertThrows(SQLException.class, () -> activityReportDataAccessObject.insertActivityReport(invalidActivityReport));
    }

    @Test
    void getAllActivityReportsReturnsEmptyListWhenNoneExist() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
        List<ActivityReportDTO> activityReportList = activityReportDataAccessObject.getAllActivityReports();
        assertNotNull(activityReportList);
        assertTrue(activityReportList.isEmpty(), "La lista debe estar vacía si no hay reportes.");
    }

    @Test
    void activityReportDTOEqualsAndToStringWork() {
        ActivityReportDTO activityReportOne = new ActivityReportDTO("1", "2", 50, "Obs");
        ActivityReportDTO activityReportTwo = new ActivityReportDTO("1", "2", 50, "Obs");
        ActivityReportDTO activityReportThree = new ActivityReportDTO("2", "3", 10, "Otro");
        assertEquals(activityReportOne, activityReportTwo, "equals debe ser true para objetos iguales");
        assertNotEquals(activityReportOne, activityReportThree, "equals debe ser false para objetos distintos");
        assertTrue(activityReportOne.toString().contains("numberReport='1'"), "toString debe contener los campos");
    }

    @Test
    void insertMultipleActivityReportsSuccessfully() throws SQLException, IOException {
        for (int i = 0; i < 5; i++) {
            int evidenceIdX = evidenceDataAccessObject.getNextEvidenceId();
            evidenceDataAccessObject.insertEvidence(new EvidenceDTO(evidenceIdX, "Evidencia " + i, new java.util.Date(), "/ruta/evidencia" + i));
            int activityIdX = getNextActivityId();
            activityDataAccessObject.insertActivity(new ActivityDTO(String.valueOf(activityIdX), "Actividad " + i));
            ReportDTO report = new ReportDTO(
                    null, new java.util.Date(), 10 + i, "Obj " + i, "Met " + i, "Res " + i,
                    projectId, studentEnrollment, "Obs " + i, String.valueOf(evidenceIdX)
            );
            reportDataAccessObject.insertReport(report);
            int reportIdX = Integer.parseInt(report.getNumberReport());
            ActivityReportDTO activityReport = new ActivityReportDTO(String.valueOf(reportIdX), String.valueOf(activityIdX), 10 * i, "Obs " + i);
            assertTrue(activityReportDataAccessObject.insertActivityReport(activityReport));
        }
        List<ActivityReportDTO> activityReportList = activityReportDataAccessObject.getAllActivityReports();
        assertEquals(5, activityReportList.size(), "Debe haber 5 reportes de actividad insertados.");
    }
}