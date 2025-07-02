package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.RepresentativeDAO;
import logic.DTO.RepresentativeDTO;
import org.junit.jupiter.api.*;

import java.io.IOException;
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
    void setUpAll() throws SQLException, IOException {
        connectionDB = new ConnectionDataBase();
        connection = connectionDB.connectDataBase();
        clearTablesAndResetAutoIncrement();
        createBaseOrganization();
    }

    @BeforeEach
    void setUp() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
        createBaseOrganization();
        createTestDepartment();
        representativeDAO = new RepresentativeDAO();
    }

    @AfterAll
    void tearDownAll() throws SQLException {
        clearTablesAndResetAutoIncrement();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        if (connectionDB != null) {
            connectionDB.close();
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        clearTablesAndResetAutoIncrement();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SET FOREIGN_KEY_CHECKS=0");
        statement.execute("TRUNCATE TABLE representante");
        statement.execute("TRUNCATE TABLE organizacion_vinculada");
        statement.execute("TRUNCATE TABLE departamento");
        statement.execute("ALTER TABLE representante AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        statement.execute("SET FOREIGN_KEY_CHECKS=1");
        statement.close();
    }

    private void createBaseOrganization() throws SQLException {
        String sql = "INSERT INTO organizacion_vinculada (nombre, direccion) VALUES ('Org Test', 'Direccion Test')";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            var resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                testOrganizationId = resultSet.getInt(1);
            } else {
                throw new SQLException("No se pudo obtener el id de la organización de prueba");
            }
        }
    }

    private int createTestDepartment() throws SQLException {
        String sql = "INSERT INTO departamento (nombre, descripcion, idOrganizacion) VALUES (?, ?, ?)";
        try (var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, "Dept test");
            preparedStatement.setString(2, "Description test");
            preparedStatement.setInt(3, testOrganizationId);
            preparedStatement.executeUpdate();
            var resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                testDepartmentId = resultSet.getInt(1);
                return testDepartmentId;
            } else {
                throw new SQLException("No se pudo obtener el id del departamento de prueba");
            }
        }
    }

    @Test
    void insertRepresentativeSuccessfully() throws SQLException, IOException {
        RepresentativeDTO representative = new RepresentativeDTO(
                "1", "Nombre Test", "Apellido Test", "test@example.com",
                String.valueOf(testOrganizationId), String.valueOf(testDepartmentId), 1
        );
        boolean result = representativeDAO.insertRepresentative(representative);
        assertTrue(result, "La inserción debería ser exitosa");

        RepresentativeDTO insertedRepresentative = representativeDAO.searchRepresentativeById("1");
        assertNotNull(insertedRepresentative, "El representante debería existir en la base de datos");
        assertEquals("Nombre Test", insertedRepresentative.getNames());
        assertEquals("Apellido Test", insertedRepresentative.getSurnames());
        assertEquals("test@example.com", insertedRepresentative.getEmail());
        assertEquals(String.valueOf(testOrganizationId), insertedRepresentative.getIdOrganization());
    }

    @Test
    void searchRepresentativeByIdSuccessfully() throws SQLException, IOException {
        insertTestRepresentative("2", "Nombre Consulta", "Apellido Consulta", "consulta@example.com", String.valueOf(testOrganizationId));
        RepresentativeDTO representative = representativeDAO.searchRepresentativeById("2");
        assertNotNull(representative, "Debería encontrar el representante");
        assertEquals("Nombre Consulta", representative.getNames());
        assertEquals("Apellido Consulta", representative.getSurnames());
        assertEquals("consulta@example.com", representative.getEmail());
        assertEquals(String.valueOf(testOrganizationId), representative.getIdOrganization());
    }

    @Test
    void updateRepresentativeSuccessfully() throws SQLException, IOException {
        insertTestRepresentative("3", "Nombre Original", "Apellido Original", "original@example.com", String.valueOf(testOrganizationId));
        RepresentativeDTO representativeToUpdate = new RepresentativeDTO(
                "3", "Nombre Actualizado", "Apellido Actualizado", "actualizado@example.com", String.valueOf(testOrganizationId), String.valueOf(testDepartmentId), 1
        );
        boolean updateResult = representativeDAO.updateRepresentative(representativeToUpdate);
        assertTrue(updateResult, "La actualización debería ser exitosa");

        RepresentativeDTO updatedRepresentative = representativeDAO.searchRepresentativeById("3");
        assertNotNull(updatedRepresentative, "El representante debería existir después de actualizar");
        assertEquals("Nombre Actualizado", updatedRepresentative.getNames());
        assertEquals("Apellido Actualizado", updatedRepresentative.getSurnames());
        assertEquals("actualizado@example.com", updatedRepresentative.getEmail());
        assertEquals(String.valueOf(testOrganizationId), updatedRepresentative.getIdOrganization());
    }

    @Test
    void deleteRepresentativeSuccessfully() throws SQLException, IOException {
        insertTestRepresentative("4", "Nombre Delete", "Apellido Delete", "delete@example.com", String.valueOf(testOrganizationId));
        RepresentativeDTO representativeBeforeDelete = representativeDAO.searchRepresentativeById("4");
        assertNotNull(representativeBeforeDelete, "El representante debería existir antes de eliminarlo");

        boolean deleteResult = representativeDAO.deleteRepresentative("4");
        assertTrue(deleteResult, "La eliminación debería ser exitosa");

        RepresentativeDTO representativeAfterDelete = representativeDAO.searchRepresentativeById("4");
        assertEquals("N/A", representativeAfterDelete.getIdRepresentative(), "El representante eliminado no debería existir");
    }

    @Test
    void getAllRepresentativesSuccessfully() throws SQLException, IOException {
        insertTestRepresentative("5", "Nombre Lista", "Apellido Lista", "lista@example.com", String.valueOf(testOrganizationId));
        List<RepresentativeDTO> representativeList = representativeDAO.getAllRepresentatives();
        assertNotNull(representativeList, "La lista no debería ser nula");
        assertFalse(representativeList.isEmpty(), "La lista no debería estar vacía");
        boolean found = representativeList.stream()
                .anyMatch(representative -> representative.getIdRepresentative().equals("5"));
        assertTrue(found, "El representante de prueba debería estar en la lista");
    }

    private void insertTestRepresentative(String id, String names, String surnames, String email, String orgId) throws SQLException {
        String sql = "INSERT INTO representante (idRepresentante, nombres, apellidos, correo, idOrganizacion) VALUES (?, ?, ?, ?, ?)";
        try (var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, names);
            preparedStatement.setString(3, surnames);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, orgId);
            preparedStatement.executeUpdate();
        }
    }

    @Test
    void insertRepresentativeFailsWithNullOrEmptyFields() {
        RepresentativeDTO nullFields = new RepresentativeDTO(null, null, null, null, null, null, 1);
        assertThrows(SQLException.class, () -> representativeDAO.insertRepresentative(nullFields),
                "Debe lanzar SQLException por campos nulos");

        RepresentativeDTO emptyFields = new RepresentativeDTO("", "", "", "", "", "", 1);
        assertDoesNotThrow(() -> representativeDAO.insertRepresentative(emptyFields),
                "No debe lanzar excepción por campos vacíos (pero la base de datos los permite)");
    }

    @Test
    void insertRepresentativeWithLongFields() {
        String longName = "a".repeat(300);
        String longSurname = "b".repeat(300);
        String longEmail = "c".repeat(300) + "@example.com";
        RepresentativeDTO representative = new RepresentativeDTO(null, longName, longSurname, longEmail, String.valueOf(testOrganizationId), String.valueOf(testDepartmentId), 1);
        assertThrows(SQLException.class, () -> representativeDAO.insertRepresentative(representative));
    }

    @Test
    void updateRepresentativeFailsWhenNotExists() throws SQLException, IOException {
        RepresentativeDTO nonExistent = new RepresentativeDTO("9999", "No", "Existe", "noexiste@example.com", String.valueOf(testOrganizationId), String.valueOf(testDepartmentId), 1);
        boolean result = representativeDAO.updateRepresentative(nonExistent);
        assertFalse(result, "No debería actualizar un representante inexistente");
    }

    @Test
    void deleteRepresentativeFailsWhenNotExists() throws SQLException, IOException {
        boolean result = representativeDAO.deleteRepresentative("8888");
        assertFalse(result, "No debería eliminar un representante inexistente");
    }

    @Test
    void searchRepresentativeByIdReturnsNAWhenNotExists() throws SQLException, IOException {
        RepresentativeDTO found = representativeDAO.searchRepresentativeById("7777");
        assertNotNull(found, "El resultado no debe ser nulo");
        assertEquals("N/A", found.getIdRepresentative());
        assertEquals("N/A", found.getNames());
    }

    @Test
    void getAllRepresentativesReturnsEmptyListWhenNoRepresentativesExist() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
        List<RepresentativeDTO> representativeList = representativeDAO.getAllRepresentatives();
        assertNotNull(representativeList, "La lista no debe ser nula");
        assertTrue(representativeList.isEmpty(), "La lista debe estar vacía si no hay representantes");
    }

    @Test
    void searchRepresentativeByFirstNameReturnsNullWhenNotExists() throws SQLException, IOException {
        RepresentativeDTO found = representativeDAO.searchRepresentativeByFirstName("NombreInexistente");
        assertNull(found, "Debe retornar null si no existe el representante");
    }

    @Test
    void getRepresentativesByDepartmentReturnsCorrectList() throws SQLException, IOException {
        int departmentIdCreated = createTestDepartment();
        RepresentativeDTO representative = new RepresentativeDTO(null, "Dept", "Test", "dept@example.com", String.valueOf(testOrganizationId), String.valueOf(departmentIdCreated), 1);
        representativeDAO.insertRepresentative(representative);

        List<RepresentativeDTO> representativeList = representativeDAO.getRepresentativesByDepartment(String.valueOf(departmentIdCreated));
        assertNotNull(representativeList);
        assertFalse(representativeList.isEmpty());
        assertTrue(representativeList.stream().allMatch(r -> String.valueOf(departmentIdCreated).equals(r.getIdDepartment())));
    }

    @Test
    void getRepresentativesByOrganizationReturnsCorrectList() throws SQLException, IOException {
        RepresentativeDTO representative = new RepresentativeDTO(null, "Org", "Test", "org@example.com", String.valueOf(testOrganizationId), String.valueOf(testDepartmentId), 1);
        representativeDAO.insertRepresentative(representative);

        List<RepresentativeDTO> representativeList = representativeDAO.getRepresentativesByOrganization(String.valueOf(testOrganizationId));
        assertNotNull(representativeList);
        assertFalse(representativeList.isEmpty());
        assertTrue(representativeList.stream().allMatch(r -> String.valueOf(testOrganizationId).equals(r.getIdOrganization())));
    }
}