package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.RepresentativeDAO;
import logic.DTO.RepresentativeDTO;

import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RepresentativeDAOTest {

    private static ConecctionDataBase connectionDB;
    private static Connection connection;
    private RepresentativeDAO representativeDAO;

    @BeforeAll
    static void setUpClass() {
        connectionDB = new ConecctionDataBase();
        try {
            connection = connectionDB.connectDB();
        } catch (SQLException e) {
            fail("Error al conectar a la base de datos: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDownClass() {
        connectionDB.closeConnection();
    }

    @BeforeEach
    void setUp() {
        representativeDAO = new RepresentativeDAO();
    }

    private String insertTestRepresentative(String idRepresentative, String names, String surnames, String email, String idOrganization) throws SQLException {
        RepresentativeDTO existingRepresentative = representativeDAO.searchRepresentativeById(idRepresentative, connection);
        if (existingRepresentative != null) {
            return idRepresentative;
        }

        String sql = "INSERT INTO representante (idRepresentante, nombres, apellidos, correo, idOrganizacion) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idRepresentative);
            stmt.setString(2, names);
            stmt.setString(3, surnames);
            stmt.setString(4, email);
            stmt.setString(5, idOrganization);
            stmt.executeUpdate();
            return idRepresentative;
        }
    }

    @Test
    void testInsertRepresentative() {
        try {
            RepresentativeDTO representative = new RepresentativeDTO(
                    "1",
                    "Nombre Test",
                    "Apellido Test",
                    "test@example.com",
                    "1"
            );

            boolean result = representativeDAO.insertRepresentative(representative, connection);
            assertTrue(result, "La inserción debería ser exitosa");

            RepresentativeDTO insertedRep = representativeDAO.searchRepresentativeById("1", connection);
            assertNotNull(insertedRep, "El representante debería existir en la base de datos");
            assertEquals("Nombre Test", insertedRep.getNames(), "El nombre debería coincidir");
            assertEquals("Apellido Test", insertedRep.getSurnames(), "El apellido debería coincidir");
            assertEquals("test@example.com", insertedRep.getEmail(), "El correo debería coincidir");
            assertEquals("1", insertedRep.getIdOrganization(), "El ID de la organización debería coincidir");
        } catch (SQLException e) {
            fail("Error en testInsertRepresentative: " + e.getMessage());
        }
    }

    @Test
    void testSearchRepresentativeById() {
        try {
            insertTestRepresentative("2", "Nombre Consulta", "Apellido Consulta", "consulta@example.com", "3");

            RepresentativeDTO rep = representativeDAO.searchRepresentativeById("2", connection);
            assertNotNull(rep, "Debería encontrar el representante");
            assertEquals("Nombre Consulta", rep.getNames(), "El nombre debería coincidir");
            assertEquals("Apellido Consulta", rep.getSurnames(), "El apellido debería coincidir");
            assertEquals("consulta@example.com", rep.getEmail(), "El correo debería coincidir");
            assertEquals("3", rep.getIdOrganization(), "El ID de la organización debería coincidir");
        } catch (SQLException e) {
            fail("Error en testGetRepresentative: " + e.getMessage());
        }
    }

    @Test
    void testUpdateRepresentative() {
        try {
            insertTestRepresentative("3", "Nombre Original", "Apellido Original", "original@example.com", "4");

            RepresentativeDTO repToUpdate = new RepresentativeDTO(
                    "3",
                    "Nombre Actualizado",
                    "Apellido Actualizado",
                    "actualizado@example.com",
                    "4"
            );

            boolean updateResult = representativeDAO.updateRepresentative(repToUpdate, connection);
            assertTrue(updateResult, "La actualización debería ser exitosa");

            RepresentativeDTO updatedRep = representativeDAO.searchRepresentativeById("3", connection);
            assertNotNull(updatedRep, "El representante debería existir después de actualizar");
            assertEquals("Nombre Actualizado", updatedRep.getNames(), "El nombre debería actualizarse");
            assertEquals("Apellido Actualizado", updatedRep.getSurnames(), "El apellido debería actualizarse");
            assertEquals("actualizado@example.com", updatedRep.getEmail(), "El correo debería actualizarse");
            assertEquals("4", updatedRep.getIdOrganization(), "El ID de la organización debería actualizarse");
        } catch (SQLException e) {
            fail("Error en testUpdateRepresentative: " + e.getMessage());
        }
    }

    @Test
    void testGetAllRepresentatives() {
        try {
            insertTestRepresentative("4", "Nombre Lista", "Apellido Lista", "lista@example.com", "5");

            List<RepresentativeDTO> representatives = representativeDAO.getAllRepresentatives(connection);
            assertNotNull(representatives, "La lista no debería ser nula");
            assertFalse(representatives.isEmpty(), "La lista no debería estar vacía");

            boolean found = representatives.stream()
                    .anyMatch(rep -> rep.getIdRepresentative().equals("4"));
            assertTrue(found, "El representante de prueba debería estar en la lista");
        } catch (SQLException e) {
            fail("Error en testGetAllRepresentatives: " + e.getMessage());
        }
    }

    @Test
    void testDeleteRepresentative() {
        try {
            insertTestRepresentative("5", "Nombre Delete", "Apellido Delete", "delete@example.com", "6");

            RepresentativeDTO beforeDelete = representativeDAO.searchRepresentativeById("5", connection);
            assertNotNull(beforeDelete, "El representante debería existir antes de eliminarlo");

            boolean deleteResult = representativeDAO.deleteRepresentative("5", connection);
            assertTrue(deleteResult, "La eliminación debería ser exitosa");

            RepresentativeDTO afterDelete = representativeDAO.searchRepresentativeById("5", connection);
            assertNull(afterDelete, "El representante no debería existir después de eliminarlo");
        } catch (SQLException e) {
            fail("Error en testDeleteRepresentative: " + e.getMessage());
        }
    }
}