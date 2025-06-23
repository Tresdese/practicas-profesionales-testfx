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

    private Connection connection;
    private DepartmentDAO departmentDAO;
    private LinkedOrganizationDAO linkedOrganizationDAO;
    private String organizationId;

    @BeforeAll
    void setUpAll() throws SQLException, IOException {
        connection = new ConnectionDataBase().connectDB();
        departmentDAO = new DepartmentDAO();
        linkedOrganizationDAO = new LinkedOrganizationDAO();

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM departamento");
            stmt.execute("DELETE FROM organizacion_vinculada");
            stmt.execute("ALTER TABLE departamento AUTO_INCREMENT = 1");
            stmt.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        }

        LinkedOrganizationDTO org = new LinkedOrganizationDTO(null, "Org Test", "Address Test", 1);
        organizationId = linkedOrganizationDAO.insertLinkedOrganizationAndGetId(org);
    }

    @BeforeEach
    void setUp() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM departamento");
        }
    }

    @AfterAll
    void tearDownAll() throws SQLException, IOException {
        connection.close();
    }

    @Test
    void insertDepartment() throws SQLException, IOException {
        DepartmentDTO department = new DepartmentDTO(0, "Dept Test", "Desc", Integer.parseInt(organizationId), 1);
        boolean inserted = departmentDAO.insertDepartment(department);
        assertTrue(inserted);
    }

    @Test
    void getAllDepartmentsByOrganizationId() throws SQLException, IOException {
        DepartmentDTO department = new DepartmentDTO(0, "Dept Test", "Desc", Integer.parseInt(organizationId), 1);
        departmentDAO.insertDepartment(department);
        List<DepartmentDTO> departments = departmentDAO.getAllDepartmentsByOrganizationId(Integer.parseInt(organizationId));
        assertEquals(1, departments.size());
    }

    @Test
    void updateDepartmentSuccessfully() throws SQLException, IOException {
        DepartmentDTO department = new DepartmentDTO(0, "Dept Test", "Desc", Integer.parseInt(organizationId), 1);
        departmentDAO.insertDepartment(department);
        List<DepartmentDTO> departments = departmentDAO.getAllDepartmentsByOrganizationId(Integer.parseInt(organizationId));
        DepartmentDTO toUpdate = departments.get(0);
        toUpdate.setName("Updated Name");
        boolean updated = departmentDAO.updateDepartment(toUpdate);
        assertTrue(updated);
        DepartmentDTO found = departmentDAO.searchDepartmentById(toUpdate.getDepartmentId());
        assertEquals("Updated Name", found.getName());
    }

    @Test
    void deleteDepartmentSuccessfully() throws SQLException, IOException {
        DepartmentDTO department = new DepartmentDTO(0, "Dept Test", "Desc", Integer.parseInt(organizationId), 1);
        departmentDAO.insertDepartment(department);
        List<DepartmentDTO> departments = departmentDAO.getAllDepartmentsByOrganizationId(Integer.parseInt(organizationId));
        int id = departments.get(0).getDepartmentId();
        boolean deleted = departmentDAO.deleteDepartment(id);
        assertTrue(deleted);
        DepartmentDTO result = departmentDAO.searchDepartmentById(id);
        assertEquals(-1, result.getDepartmentId());
        assertEquals("N/A", result.getName());
        assertEquals("N/A", result.getDescription());
        assertEquals(-1, result.getOrganizationId());
    }

    @Test
    void searchDepartmentByIdReturnsNullWhenNotExists() throws SQLException, IOException {
        DepartmentDAO dao = new DepartmentDAO();
        DepartmentDTO result = dao.searchDepartmentById(-999);
        assertNotNull(result);
        assertEquals(-1, result.getDepartmentId());
        assertEquals("N/A", result.getName());
        assertEquals("N/A", result.getDescription());
        assertEquals(-1, result.getOrganizationId());
    }

    @Test
    void getOrganizationIdByDepartmentIdReturnsCorrectId() throws SQLException, IOException {
        DepartmentDTO department = new DepartmentDTO(0, "Dept Test", "Desc", Integer.parseInt(organizationId), 1);
        departmentDAO.insertDepartment(department);
        List<DepartmentDTO> departments = departmentDAO.getAllDepartmentsByOrganizationId(Integer.parseInt(organizationId));
        int id = departments.get(0).getDepartmentId();
        int orgId = departmentDAO.getOrganizationIdByDepartmentId(id);
        assertEquals(Integer.parseInt(organizationId), orgId);
    }

    @Test
    void getAllDepartmentsReturnsList() throws SQLException, IOException {
        departmentDAO.insertDepartment(new DepartmentDTO(0, "Dept1", "Desc1", Integer.parseInt(organizationId), 1));
        departmentDAO.insertDepartment(new DepartmentDTO(0, "Dept2", "Desc2", Integer.parseInt(organizationId), 1));
        List<DepartmentDTO> all = departmentDAO.getAllDepartments();
        assertTrue(all.size() >= 2);
    }

    @Test
    void insertDepartmentFailsWithInvalidOrganizationId() {
        DepartmentDTO department = new DepartmentDTO(0, "Dept Inválido", "Desc", 99999, 0);
        assertThrows(SQLException.class, () -> departmentDAO.insertDepartment(department));
    }

    @Test
    void updateDepartmentFailsWhenNotExists() throws SQLException, IOException {
        DepartmentDTO department = new DepartmentDTO(9999, "No existe", "Desc", Integer.parseInt(organizationId), 0);
        boolean updated = departmentDAO.updateDepartment(department);
        assertFalse(updated);
    }

    @Test
    void deleteDepartmentFailsWhenNotExists() throws SQLException, IOException {
        boolean deleted = departmentDAO.deleteDepartment(9999);
        assertFalse(deleted);
    }

    @Test
    void getAllDepartmentsByOrganizationIdReturnsEmptyList() throws SQLException, IOException {
        List<DepartmentDTO> departments = departmentDAO.getAllDepartmentsByOrganizationId(9999);
        assertNotNull(departments);
        assertTrue(departments.isEmpty());
    }

    @Test
    void getAllDepartmentsReturnsEmptyListWhenNoDepartmentsExist() throws SQLException, IOException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM departamento");
        }
        List<DepartmentDTO> departments = departmentDAO.getAllDepartments();
        assertNotNull(departments);
        assertTrue(departments.isEmpty());
    }
}