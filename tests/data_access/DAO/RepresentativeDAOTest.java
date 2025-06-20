package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.RepresentativeDAO;
import logic.DTO.RepresentativeDTO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RepresentativeDAOTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private RepresentativeDAO representativeDAO;
    private int testOrganizationId;
    private int testDepartmentId;

    @BeforeAll
    void setUpAll() throws Exception {
        connectionDB = new ConnectionDataBase();
        connection = connectionDB.connectDB();
        clearTablesAndResetAutoIncrement();
        createBaseOrganization();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createBaseOrganization();
        createTestDepartment();
        representativeDAO = new RepresentativeDAO();
    }

    @AfterAll
    void tearDownAll() throws Exception {
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
        stmt.execute("TRUNCATE TABLE representante");
        stmt.execute("TRUNCATE TABLE organizacion_vinculada");
        stmt.execute("TRUNCATE TABLE departamento");
        stmt.execute("ALTER TABLE representante AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        stmt.execute("SET FOREIGN_KEY_CHECKS=1");
        stmt.close();
    }

    private void createBaseOrganization() throws SQLException {
        String sql = "INSERT INTO organizacion_vinculada (nombre, direccion) VALUES ('Org Test', 'Direccion Test')";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            var rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                testOrganizationId = rs.getInt(1);
            } else {
                throw new SQLException("No se pudo obtener el id de la organización de prueba");
            }
        }
    }

    private int createTestDepartment() throws SQLException {
        String sql = "INSERT INTO departamento (nombre, descripcion, idOrganizacion) VALUES (?, ?, ?)";
        try (var stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "Dept test");
            stmt.setString(2, "Description test");
            stmt.setInt(3, testOrganizationId);
            stmt.executeUpdate();
            var rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                testDepartmentId = rs.getInt(1);
                return testDepartmentId;
            } else {
                throw new SQLException("No se pudo obtener el id del departamento de prueba");
            }
        }
    }

    @Test
    void insertRepresentativeSuccessfully() throws SQLException {
        RepresentativeDTO representative = new RepresentativeDTO(
                "1", "Nombre Test", "Apellido Test", "test@example.com",
                String.valueOf(testOrganizationId), String.valueOf(testDepartmentId)
        );
        boolean result = representativeDAO.insertRepresentative(representative);
        assertTrue(result, "La inserción debería ser exitosa");

        RepresentativeDTO insertedRep = representativeDAO.searchRepresentativeById("1");
        assertNotNull(insertedRep, "El representante debería existir en la base de datos");
        assertEquals("Nombre Test", insertedRep.getNames());
        assertEquals("Apellido Test", insertedRep.getSurnames());
        assertEquals("test@example.com", insertedRep.getEmail());
        assertEquals(String.valueOf(testOrganizationId), insertedRep.getIdOrganization());
    }

    @Test
    void searchRepresentativeByIdSuccessfully() throws SQLException {
        insertTestRepresentative("2", "Nombre Consulta", "Apellido Consulta", "consulta@example.com", String.valueOf(testOrganizationId));
        RepresentativeDTO rep = representativeDAO.searchRepresentativeById("2");
        assertNotNull(rep, "Debería encontrar el representante");
        assertEquals("Nombre Consulta", rep.getNames());
        assertEquals("Apellido Consulta", rep.getSurnames());
        assertEquals("consulta@example.com", rep.getEmail());
        assertEquals(String.valueOf(testOrganizationId), rep.getIdOrganization());
    }

    @Test
    void updateRepresentativeSuccessfully() throws SQLException {
        insertTestRepresentative("3", "Nombre Original", "Apellido Original", "original@example.com", String.valueOf(testOrganizationId));
        RepresentativeDTO repToUpdate = new RepresentativeDTO(
                "3", "Nombre Actualizado", "Apellido Actualizado", "actualizado@example.com", String.valueOf(testOrganizationId), String.valueOf(testDepartmentId)
        );
        boolean updateResult = representativeDAO.updateRepresentative(repToUpdate);
        assertTrue(updateResult, "La actualización debería ser exitosa");

        RepresentativeDTO updatedRep = representativeDAO.searchRepresentativeById("3");
        assertNotNull(updatedRep, "El representante debería existir después de actualizar");
        assertEquals("Nombre Actualizado", updatedRep.getNames());
        assertEquals("Apellido Actualizado", updatedRep.getSurnames());
        assertEquals("actualizado@example.com", updatedRep.getEmail());
        assertEquals(String.valueOf(testOrganizationId), updatedRep.getIdOrganization());
    }

    @Test
    void deleteRepresentativeSuccessfully() throws SQLException {
        insertTestRepresentative("4", "Nombre Delete", "Apellido Delete", "delete@example.com", String.valueOf(testOrganizationId));
        RepresentativeDTO beforeDelete = representativeDAO.searchRepresentativeById("4");
        assertNotNull(beforeDelete, "El representante debería existir antes de eliminarlo");

        boolean deleteResult = representativeDAO.deleteRepresentative("4");
        assertTrue(deleteResult, "La eliminación debería ser exitosa");

        RepresentativeDTO afterDelete = representativeDAO.searchRepresentativeById("4");
        assertEquals("N/A", afterDelete.getIdRepresentative(), "El representante eliminado no debería existir");
    }

    @Test
    void getAllRepresentativesSuccessfully() throws SQLException {
        insertTestRepresentative("5", "Nombre Lista", "Apellido Lista", "lista@example.com", String.valueOf(testOrganizationId));
        List<RepresentativeDTO> representatives = representativeDAO.getAllRepresentatives();
        assertNotNull(representatives, "La lista no debería ser nula");
        assertFalse(representatives.isEmpty(), "La lista no debería estar vacía");
        boolean found = representatives.stream()
                .anyMatch(rep -> rep.getIdRepresentative().equals("5"));
        assertTrue(found, "El representante de prueba debería estar en la lista");
    }

    private void insertTestRepresentative(String id, String names, String surnames, String email, String orgId) throws SQLException {
        String sql = "INSERT INTO representante (idRepresentante, nombres, apellidos, correo, idOrganizacion) VALUES (?, ?, ?, ?, ?)";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, names);
            stmt.setString(3, surnames);
            stmt.setString(4, email);
            stmt.setString(5, orgId);
            stmt.executeUpdate();
        }
    }

    @Test
    void insertRepresentativeFailsWithNullOrEmptyFields() {
        RepresentativeDTO nullFields = new RepresentativeDTO(null, null, null, null, null, null);
        assertThrows(SQLException.class, () -> representativeDAO.insertRepresentative(nullFields),
                "Debe lanzar SQLException por campos nulos");

        RepresentativeDTO emptyFields = new RepresentativeDTO("", "", "", "", "", "");
        assertDoesNotThrow(() -> representativeDAO.insertRepresentative(emptyFields),
                "No debe lanzar excepción por campos vacíos (pero la base de datos los permite)");
    }

    @Test
    void insertRepresentativeWithLongFields() {
        String longName = "a".repeat(300);
        String longSurname = "b".repeat(300);
        String longEmail = "c".repeat(300) + "@example.com";
        RepresentativeDTO rep = new RepresentativeDTO(null, longName, longSurname, longEmail, String.valueOf(testOrganizationId), String.valueOf(testDepartmentId));
        assertThrows(SQLException.class, () -> representativeDAO.insertRepresentative(rep));
    }

    @Test
    void updateRepresentativeFailsWhenNotExists() throws SQLException {
        RepresentativeDTO nonExistent = new RepresentativeDTO("9999", "No", "Existe", "noexiste@example.com", String.valueOf(testOrganizationId), String.valueOf(testDepartmentId));
        boolean result = representativeDAO.updateRepresentative(nonExistent);
        assertFalse(result, "No debería actualizar un representante inexistente");
    }

    // Intenta eliminar un representante inexistente
    @Test
    void deleteRepresentativeFailsWhenNotExists() throws SQLException {
        boolean result = representativeDAO.deleteRepresentative("8888");
        assertFalse(result, "No debería eliminar un representante inexistente");
    }

    // Verifica que buscar un ID inexistente retorna un DTO con valores "N/A"
    @Test
    void searchRepresentativeByIdReturnsNAWhenNotExists() throws SQLException {
        RepresentativeDTO found = representativeDAO.searchRepresentativeById("7777");
        assertNotNull(found, "El resultado no debe ser nulo");
        assertEquals("N/A", found.getIdRepresentative());
        assertEquals("N/A", found.getNames());
    }

    // Verifica que retorna una lista vacía si no hay representantes
    @Test
    void getAllRepresentativesReturnsEmptyListWhenNoRepresentativesExist() throws SQLException {
        clearTablesAndResetAutoIncrement();
        List<RepresentativeDTO> list = representativeDAO.getAllRepresentatives();
        assertNotNull(list, "La lista no debe ser nula");
        assertTrue(list.isEmpty(), "La lista debe estar vacía si no hay representantes");
    }

    // Busca por nombre y apellido que no existen y espera un resultado null
    @Test
    void searchRepresentativeByFullnameReturnsNullWhenNotExists() throws SQLException {
        RepresentativeDTO found = representativeDAO.searchRepresentativeByFullname("NombreInexistente", "ApellidoInexistente");
        assertNull(found, "Debe retornar null si no existe el representante");
    }

    // Verifica que solo se obtienen los representantes del departamento indicado
    @Test
    void getRepresentativesByDepartmentReturnsCorrectList() throws SQLException {
        int deptId = createTestDepartment();
        RepresentativeDTO rep = new RepresentativeDTO(null, "Dept", "Test", "dept@example.com", String.valueOf(testOrganizationId), String.valueOf(deptId));
        representativeDAO.insertRepresentative(rep);

        List<RepresentativeDTO> reps = representativeDAO.getRepresentativesByDepartment(String.valueOf(deptId));
        assertNotNull(reps);
        assertFalse(reps.isEmpty());
        assertTrue(reps.stream().allMatch(r -> String.valueOf(deptId).equals(r.getIdDepartment())));
    }

    // Verifica que solo se obtienen los representantes de la organización indicada
    @Test
    void getRepresentativesByOrganizationReturnsCorrectList() throws SQLException {
        RepresentativeDTO rep = new RepresentativeDTO(null, "Org", "Test", "org@example.com", String.valueOf(testOrganizationId), String.valueOf(testDepartmentId));
        representativeDAO.insertRepresentative(rep);

        List<RepresentativeDTO> reps = representativeDAO.getRepresentativesByOrganization(String.valueOf(testOrganizationId));
        assertNotNull(reps);
        assertFalse(reps.isEmpty());
        assertTrue(reps.stream().allMatch(r -> String.valueOf(testOrganizationId).equals(r.getIdOrganization())));
    }
}