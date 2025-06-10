package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.RepresentativeDAO;
import logic.DTO.RepresentativeDTO;
import logic.DTO.LinkedOrganizationDTO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RepresentativeDAOTest {

    private ConecctionDataBase connectionDB;
    private Connection connection;
    private RepresentativeDAO representativeDAO;
    private int testOrganizationId;

    @BeforeAll
    void setUpAll() throws Exception {
        connectionDB = new ConecctionDataBase();
        connection = connectionDB.connectDB();
        clearTablesAndResetAutoIncrement();
        createBaseOrganization();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createBaseOrganization();
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

    @Test
    void insertRepresentativeSuccessfully() throws SQLException {
        RepresentativeDTO representative = new RepresentativeDTO(
                "1", "Nombre Test", "Apellido Test", "test@example.com", String.valueOf(testOrganizationId)
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
                "3", "Nombre Actualizado", "Apellido Actualizado", "actualizado@example.com", String.valueOf(testOrganizationId)
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
}