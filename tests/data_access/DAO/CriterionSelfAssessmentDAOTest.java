package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.*;
import logic.DTO.*;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CriterionSelfAssessmentDAOTest {

    private ConecctionDataBase connectionDB;
    private Connection connection;

    private PeriodDAO periodDAO;
    private GroupDAO groupDAO;
    private EvidenceDAO evidenceDAO;
    private SelfAssessmentCriteriaDAO selfAssessmentCriteriaDAO;
    private SelfAssessmentDAO selfAssessmentDAO;
    private CriterionSelfAssessmentDAO criterionSelfAssessmentDAO;
    private StudentDAO studentDAO;

    // IDs base para los registros
    private String idPeriodoBase = "1001";
    private String nrcBase = "2001";
    private String matriculaBase = "3001";
    private String idEvidenceBase = "4001";
    private String idCriteriaBase = "5001";
    private String idSelfAssessmentBase = "6001";

    @BeforeAll
    void setUpAll() throws Exception {
        connectionDB = new ConecctionDataBase();
        connection = connectionDB.connectDB();

        periodDAO = new PeriodDAO();
        groupDAO = new GroupDAO();
        evidenceDAO = new EvidenceDAO();
        selfAssessmentCriteriaDAO = new SelfAssessmentCriteriaDAO();
        selfAssessmentDAO = new SelfAssessmentDAO();
        criterionSelfAssessmentDAO = new CriterionSelfAssessmentDAO(connection);
        studentDAO = new StudentDAO();
    }

    @AfterAll
    void tearDownAll() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        if (connectionDB != null) {
            connectionDB.close();
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        limpiarTablasYResetearAutoIncrement();
        crearRegistrosBase();
    }

    private void limpiarTablasYResetearAutoIncrement() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS=0");
        stmt.execute("TRUNCATE TABLE autoevaluacion_criterio");
        stmt.execute("TRUNCATE TABLE autoevaluacion");
        stmt.execute("TRUNCATE TABLE criterio_de_autoevaluacion");
        stmt.execute("TRUNCATE TABLE evidencia");
        stmt.execute("TRUNCATE TABLE estudiante");
        stmt.execute("TRUNCATE TABLE grupo");
        stmt.execute("TRUNCATE TABLE periodo");
        stmt.execute("SET FOREIGN_KEY_CHECKS=1");
        stmt.close();
    }

    private void crearRegistrosBase() throws SQLException {
        // 1. Insertar periodo
        PeriodDTO periodo = new PeriodDTO(idPeriodoBase, "Periodo Test", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        periodDAO.insertPeriod(periodo, connection);

        // 2. Insertar grupo (con NRC igual al que usará el estudiante)
        GroupDTO grupo = new GroupDTO(nrcBase, "Grupo Test", "usuarioTest", idPeriodoBase);
        groupDAO.insertGroup(grupo, connection);

        // 3. Insertar evidencia (idEvidenceBase como String, pero EvidenceDTO espera int)
        EvidenceDTO evidencia = new EvidenceDTO(Integer.parseInt(idEvidenceBase), "Evidencia Test", new Date(), "ruta/test");
        evidenceDAO.insertEvidence(evidencia, connection);

        // 4. Insertar criterio de autoevaluación
        SelfAssessmentCriteriaDTO criterio = new SelfAssessmentCriteriaDTO(idCriteriaBase, "Criterio Test", 10.0);
        selfAssessmentCriteriaDAO.insertSelfAssessmentCriteria(criterio, connection);

        // 5. Insertar estudiante (usa el mismo NRC que el grupo)
        StudentDTO estudiante = new StudentDTO(
                matriculaBase, 1, "Nombre", "Apellido", "1234567890", "correo@test.com",
                "usuarioTest", "passTest", nrcBase, "80", 9.5
        );
        // StudentDAO usa su propia conexión, pero el grupo ya existe en la base real
        studentDAO.insertStudent(estudiante);

        // 6. Insertar autoevaluación (todos los parámetros como String excepto grade)
        SelfAssessmentDTO autoevaluacion = new SelfAssessmentDTO(
                idSelfAssessmentBase,           // idAutoevaluacion (String)
                idEvidenceBase,                 // idEvidencia (String)
                10.0,                           // calificacion (double)
                matriculaBase,                  // matricula (String)
                "Comentario test"               // comentario (String)
        );
        selfAssessmentDAO.insertSelfAssessment(autoevaluacion, connection);
    }

    @Test
    void testInsertCriterionSelfAssessment() {
        try {
            CriterionSelfAssessmentDTO criterionSelfAssessment = new CriterionSelfAssessmentDTO(idSelfAssessmentBase, idCriteriaBase);
            boolean inserted = criterionSelfAssessmentDAO.insertCriterionSelfAssessment(criterionSelfAssessment);
            assertTrue(inserted, "El registro debe insertarse correctamente");
        } catch (SQLException e) {
            fail("Error en testInsertCriterionSelfAssessment: " + e.getMessage());
        }
    }

    @Test
    void testSearchCriterionSelfAssessmentByIds() {
        try {
            CriterionSelfAssessmentDTO criterionSelfAssessment = new CriterionSelfAssessmentDTO(idSelfAssessmentBase, idCriteriaBase);
            criterionSelfAssessmentDAO.insertCriterionSelfAssessment(criterionSelfAssessment);

            CriterionSelfAssessmentDTO found = criterionSelfAssessmentDAO
                    .searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(idSelfAssessmentBase, idCriteriaBase);
            assertEquals(criterionSelfAssessment, found, "El registro buscado debe coincidir con el insertado");
        } catch (SQLException e) {
            fail("Error en testSearchCriterionSelfAssessmentByIds: " + e.getMessage());
        }
    }

    @Test
    void testDeleteCriterionSelfAssessment() {
        try {
            CriterionSelfAssessmentDTO criterionSelfAssessment = new CriterionSelfAssessmentDTO(idSelfAssessmentBase, idCriteriaBase);
            criterionSelfAssessmentDAO.insertCriterionSelfAssessment(criterionSelfAssessment);

            boolean deleted = criterionSelfAssessmentDAO.deleteCriterionSelfAssessment(idSelfAssessmentBase, idCriteriaBase);
            assertTrue(deleted, "El registro debe eliminarse correctamente");
        } catch (SQLException e) {
            fail("Error en testDeleteCriterionSelfAssessment: " + e.getMessage());
        }
    }

    @Test
    void testGetAllCriterionSelfAssessments() {
        try {
            CriterionSelfAssessmentDTO criterionSelfAssessment = new CriterionSelfAssessmentDTO(idSelfAssessmentBase, idCriteriaBase);
            criterionSelfAssessmentDAO.insertCriterionSelfAssessment(criterionSelfAssessment);

            List<CriterionSelfAssessmentDTO> all = criterionSelfAssessmentDAO.getAllCriterionSelfAssessments();
            assertNotNull(all, "La lista no debería ser nula");
            assertFalse(all.isEmpty(), "La lista no debería estar vacía");
        } catch (SQLException e) {
            fail("Error en testGetAllCriterionSelfAssessments: " + e.getMessage());
        }
    }
}