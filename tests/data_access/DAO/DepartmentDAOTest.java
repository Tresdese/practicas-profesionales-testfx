package data_access.DAO;

import logic.DAO.DepartmentDAO;
import logic.DAO.LinkedOrganizationDAO;
import logic.DTO.DepartmentDTO;
import logic.DTO.LinkedOrganizationDTO;
import org.junit.jupiter.api.*;
import data_access.ConnectionDataBase;

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
    void setUpAll() throws SQLException {
        connection = new ConnectionDataBase().connectDB();
        departmentDAO = new DepartmentDAO();
        linkedOrganizationDAO = new LinkedOrganizationDAO();

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM departamento");
            stmt.execute("DELETE FROM organizacion_vinculada");
            stmt.execute("ALTER TABLE departamento AUTO_INCREMENT = 1");
            stmt.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        }

        LinkedOrganizationDTO org = new LinkedOrganizationDTO(null, "Org Test", "Address Test");
        organizationId = linkedOrganizationDAO.insertLinkedOrganizationAndGetId(org);
    }

    @BeforeEach
    void setUp() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM departamento");
        }
    }

    @AfterAll
    void tearDownAll() throws SQLException {
        connection.close();
    }

    @Test
    void insertDepartment() throws SQLException {
        DepartmentDTO department = new DepartmentDTO(0, "Dept Test", "Desc", Integer.parseInt(organizationId));
        boolean inserted = departmentDAO.insertDepartment(department);
        assertTrue(inserted);
    }

    @Test
    void getAllDepartmentsByOrganizationId() throws SQLException {
        DepartmentDTO department = new DepartmentDTO(0, "Dept Test", "Desc", Integer.parseInt(organizationId));
        departmentDAO.insertDepartment(department);
        List<DepartmentDTO> departments = departmentDAO.getAllDepartmentsByOrganizationId(Integer.parseInt(organizationId));
        assertEquals(1, departments.size());
    }

    @Test
    void updateDepartmentSuccessfully() throws SQLException {
        DepartmentDTO department = new DepartmentDTO(0, "Dept Test", "Desc", Integer.parseInt(organizationId));
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
    void deleteDepartmentSuccessfully() throws SQLException {
        DepartmentDTO department = new DepartmentDTO(0, "Dept Test", "Desc", Integer.parseInt(organizationId));
        departmentDAO.insertDepartment(department);
        List<DepartmentDTO> departments = departmentDAO.getAllDepartmentsByOrganizationId(Integer.parseInt(organizationId));
        int id = departments.get(0).getDepartmentId();
        boolean deleted = departmentDAO.deleteDepartment(id);
        assertTrue(deleted);
        assertNull(departmentDAO.searchDepartmentById(id));
    }

    @Test
    void searchDepartmentByIdReturnsNullWhenNotExists() throws SQLException {
        assertNull(departmentDAO.searchDepartmentById(9999));
    }

    @Test
    void getOrganizationIdByDepartmentIdReturnsCorrectId() throws SQLException {
        DepartmentDTO department = new DepartmentDTO(0, "Dept Test", "Desc", Integer.parseInt(organizationId));
        departmentDAO.insertDepartment(department);
        List<DepartmentDTO> departments = departmentDAO.getAllDepartmentsByOrganizationId(Integer.parseInt(organizationId));
        int id = departments.get(0).getDepartmentId();
        int orgId = departmentDAO.getOrganizationIdByDepartmentId(id);
        assertEquals(Integer.parseInt(organizationId), orgId);
    }

    @Test
    void getAllDepartmentsReturnsList() throws SQLException {
        departmentDAO.insertDepartment(new DepartmentDTO(0, "Dept1", "Desc1", Integer.parseInt(organizationId)));
        departmentDAO.insertDepartment(new DepartmentDTO(0, "Dept2", "Desc2", Integer.parseInt(organizationId)));
        List<DepartmentDTO> all = departmentDAO.getAllDepartments();
        assertTrue(all.size() >= 2);
    }

    @Test
    void insertDepartmentFailsWithInvalidOrganizationId() {
        DepartmentDTO department = new DepartmentDTO(0, "Dept Inválido", "Desc", 99999); // idOrganizacion inexistente
        assertThrows(SQLException.class, () -> departmentDAO.insertDepartment(department));
    }

    @Test
    void updateDepartmentFailsWhenNotExists() throws SQLException {
        DepartmentDTO department = new DepartmentDTO(9999, "No existe", "Desc", Integer.parseInt(organizationId));
        boolean updated = departmentDAO.updateDepartment(department);
        assertFalse(updated);
    }

    @Test
    void deleteDepartmentFailsWhenNotExists() throws SQLException {
        boolean deleted = departmentDAO.deleteDepartment(9999);
        assertFalse(deleted);
    }

    @Test
    void getAllDepartmentsByOrganizationIdReturnsEmptyList() throws SQLException {
        List<DepartmentDTO> departments = departmentDAO.getAllDepartmentsByOrganizationId(9999);
        assertNotNull(departments);
        assertTrue(departments.isEmpty());
    }

    @Test
    void getAllDepartmentsReturnsEmptyListWhenNoDepartmentsExist() throws SQLException {
        // Asegura que la tabla está vacía
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM departamento");
        }
        List<DepartmentDTO> departments = departmentDAO.getAllDepartments();
        assertNotNull(departments);
        assertTrue(departments.isEmpty());
    }
}