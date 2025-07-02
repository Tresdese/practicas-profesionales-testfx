package data_access.DAO;

import logic.DAO.DepartmentDAO;
import logic.DAO.LinkedOrganizationDAO;
import logic.DTO.DepartmentDTO;
import logic.DTO.LinkedOrganizationDTO;
import org.junit.jupiter.api.*;
import data_access.ConnectionDataBase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DepartmentDAOTest {

    private Connection databaseConnection;
    private DepartmentDAO departmentDataAccessObject;
    private LinkedOrganizationDAO linkedOrganizationDataAccessObject;
    private String organizationId;

    @BeforeAll
    void setUpAll() throws SQLException, IOException {
        databaseConnection = new ConnectionDataBase().connectDataBase();
        departmentDataAccessObject = new DepartmentDAO();
        linkedOrganizationDataAccessObject = new LinkedOrganizationDAO();

        try (Statement statement = databaseConnection.createStatement()) {
            statement.execute("DELETE FROM departamento");
            statement.execute("DELETE FROM organizacion_vinculada");
            statement.execute("ALTER TABLE departamento AUTO_INCREMENT = 1");
            statement.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        }

        LinkedOrganizationDTO organization = new LinkedOrganizationDTO(null, "Org Test", "Address Test", 1);
        organizationId = linkedOrganizationDataAccessObject.insertLinkedOrganizationAndGetId(organization);
    }

    @BeforeEach
    void setUp() throws SQLException {
        try (Statement statement = databaseConnection.createStatement()) {
            statement.execute("DELETE FROM departamento");
        }
    }

    @AfterAll
    void tearDownAll() throws SQLException, IOException {
        databaseConnection.close();
    }

    @Test
    void insertDepartment() throws SQLException, IOException {
        DepartmentDTO department = new DepartmentDTO(0, "Dept Test", "Desc", Integer.parseInt(organizationId), 1);
        boolean wasInserted = departmentDataAccessObject.insertDepartment(department);
        assertTrue(wasInserted);
    }

    @Test
    void getAllDepartmentsByOrganizationId() throws SQLException, IOException {
        DepartmentDTO department = new DepartmentDTO(0, "Dept Test", "Desc", Integer.parseInt(organizationId), 1);
        departmentDataAccessObject.insertDepartment(department);
        List<DepartmentDTO> departments = departmentDataAccessObject.getAllDepartmentsByOrganizationId(Integer.parseInt(organizationId));
        assertEquals(1, departments.size());
    }

    @Test
    void updateDepartmentSuccessfully() throws SQLException, IOException {
        DepartmentDTO department = new DepartmentDTO(0, "Dept Test", "Desc", Integer.parseInt(organizationId), 1);
        departmentDataAccessObject.insertDepartment(department);
        List<DepartmentDTO> departments = departmentDataAccessObject.getAllDepartmentsByOrganizationId(Integer.parseInt(organizationId));
        DepartmentDTO toUpdate = departments.get(0);
        toUpdate.setName("Updated Name");
        boolean wasUpdated = departmentDataAccessObject.updateDepartment(toUpdate);
        assertTrue(wasUpdated);
        DepartmentDTO found = departmentDataAccessObject.searchDepartmentById(toUpdate.getDepartmentId());
        assertEquals("Updated Name", found.getName());
    }

    @Test
    void deleteDepartmentSuccessfully() throws SQLException, IOException {
        DepartmentDTO department = new DepartmentDTO(0, "Dept Test", "Desc", Integer.parseInt(organizationId), 1);
        departmentDataAccessObject.insertDepartment(department);
        List<DepartmentDTO> departments = departmentDataAccessObject.getAllDepartmentsByOrganizationId(Integer.parseInt(organizationId));
        int id = departments.get(0).getDepartmentId();
        boolean wasDeleted = departmentDataAccessObject.deleteDepartment(id);
        assertTrue(wasDeleted);
        DepartmentDTO result = departmentDataAccessObject.searchDepartmentById(id);
        assertEquals(-1, result.getDepartmentId());
        assertEquals("N/A", result.getName());
        assertEquals("N/A", result.getDescription());
        assertEquals(-1, result.getOrganizationId());
    }

    @Test
    void searchDepartmentByIdReturnsNullWhenNotExists() throws SQLException, IOException {
        DepartmentDAO departmentDataAccessObject = new DepartmentDAO();
        DepartmentDTO result = departmentDataAccessObject.searchDepartmentById(-999);
        assertNotNull(result);
        assertEquals(-1, result.getDepartmentId());
        assertEquals("N/A", result.getName());
        assertEquals("N/A", result.getDescription());
        assertEquals(-1, result.getOrganizationId());
    }

    @Test
    void getOrganizationIdByDepartmentIdReturnsCorrectId() throws SQLException, IOException {
        DepartmentDTO department = new DepartmentDTO(0, "Dept Test", "Desc", Integer.parseInt(organizationId), 1);
        departmentDataAccessObject.insertDepartment(department);
        List<DepartmentDTO> departments = departmentDataAccessObject.getAllDepartmentsByOrganizationId(Integer.parseInt(organizationId));
        int id = departments.get(0).getDepartmentId();
        int organizationIdByDepartmentId = departmentDataAccessObject.getOrganizationIdByDepartmentId(id);
        assertEquals(Integer.parseInt(this.organizationId), organizationIdByDepartmentId);
    }

    @Test
    void getAllDepartmentsReturnsList() throws SQLException, IOException {
        departmentDataAccessObject.insertDepartment(new DepartmentDTO(0, "Dept1", "Desc1", Integer.parseInt(organizationId), 1));
        departmentDataAccessObject.insertDepartment(new DepartmentDTO(0, "Dept2", "Desc2", Integer.parseInt(organizationId), 1));
        List<DepartmentDTO> all = departmentDataAccessObject.getAllDepartments();
        assertTrue(all.size() >= 2);
    }

    @Test
    void insertDepartmentFailsWithInvalidOrganizationId() {
        DepartmentDTO department = new DepartmentDTO(0, "Dept Inválido", "Desc", 99999, 0);
        assertThrows(SQLException.class, () -> departmentDataAccessObject.insertDepartment(department));
    }

    @Test
    void updateDepartmentFailsWhenNotExists() throws SQLException, IOException {
        DepartmentDTO department = new DepartmentDTO(9999, "No existe", "Desc", Integer.parseInt(organizationId), 0);
        boolean wasUpdated = departmentDataAccessObject.updateDepartment(department);
        assertFalse(wasUpdated);
    }

    @Test
    void deleteDepartmentFailsWhenNotExists() throws SQLException, IOException {
        boolean wasDeleted = departmentDataAccessObject.deleteDepartment(9999);
        assertFalse(wasDeleted);
    }

    @Test
    void getAllDepartmentsByOrganizationIdReturnsEmptyList() throws SQLException, IOException {
        List<DepartmentDTO> departments = departmentDataAccessObject.getAllDepartmentsByOrganizationId(9999);
        assertNotNull(departments);
        assertTrue(departments.isEmpty());
    }

    @Test
    void getAllDepartmentsReturnsEmptyListWhenNoDepartmentsExist() throws SQLException, IOException {
        try (Statement statement = databaseConnection.createStatement()) {
            statement.execute("DELETE FROM departamento");
        }
        List<DepartmentDTO> departments = departmentDataAccessObject.getAllDepartments();
        assertNotNull(departments);
        assertTrue(departments.isEmpty());
    }
}