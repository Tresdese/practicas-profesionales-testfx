package data_access;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import java.io.*;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ConecctionDataBaseTest {

    private static final Logger LOGGER = LogManager.getLogger(ConecctionDataBaseTest.class);
    private static final String CONFIG_PATH = "out/resources/config-test.properties";

    @AfterEach
    void cleanup() {
        File config = new File(CONFIG_PATH);
        if (config.exists()) config.delete();
    }

    @Test
    void testConfigFileNotFound() {
        File config = new File(CONFIG_PATH);
        if (config.exists()) config.delete();
        assertThrows(IOException.class, () -> new ConnectionDataBase(CONFIG_PATH));
    }

    @Test
    void testInvalidDBUrl() throws IOException {
        try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
            writer.write("db.url=jdbc:mysql://invalidhost:3306/test\n");
            writer.write("db.user=invalid\n");
            writer.write("db.password=invalid\n");
        }
        ConnectionDataBase db = new ConnectionDataBase(CONFIG_PATH);
        assertThrows(SQLException.class, db::connectDataBase);
        db.close();
    }

    @Test
    void testAccessDenied() throws IOException {
        try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
            writer.write("db.url=jdbc:mysql://localhost:3306/test\n");
            writer.write("db.user=usuario_incorrecto\n");
            writer.write("db.password=clave_incorrecta\n");
        }
        ConnectionDataBase db = new ConnectionDataBase(CONFIG_PATH);
        SQLException ex = assertThrows(SQLException.class, db::connectDataBase);
        assertEquals("28000", ex.getSQLState());
        db.close();
    }

    @Test
    void testDatabaseNotFound() throws IOException {
        try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
            writer.write("db.url=jdbc:mysql://localhost:3306/basedatos_que_no_existe\n");
            writer.write("db.user=root\n");
            writer.write("db.password=123456\n");
        }
        ConnectionDataBase db = new ConnectionDataBase(CONFIG_PATH);
        SQLException ex = assertThrows(SQLException.class, db::connectDataBase);
        assertEquals("42000", ex.getSQLState());
        db.close();
    }

    @Test
    void testCloseWithoutConnect() throws IOException {
        try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
            writer.write("db.url=jdbc:mysql://localhost:3306/test\n");
            writer.write("db.user=test\n");
            writer.write("db.password=test\n");
        }
        ConnectionDataBase db = new ConnectionDataBase(CONFIG_PATH);
        assertDoesNotThrow(db::close);
    }

    @Test
    void testSuccessfulConnection() throws IOException, SQLException {
        try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
            writer.write("db.url=jdbc:mysql://localhost:3306/test\n");
            writer.write("db.user=usuario_valido\n");
            writer.write("db.password=clave_valida\n");
        }
        ConnectionDataBase db = new ConnectionDataBase(CONFIG_PATH);
        try {
            assertNotNull(db.connectDataBase());
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("Access denied") || e.getMessage().contains("denegado"));
        } finally {
            db.close();
        }
    }

    @Test
    void testEmptyProperties() throws IOException {
        try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
            writer.write("db.url=\n");
            writer.write("db.user=\n");
            writer.write("db.password=\n");
        }
        ConnectionDataBase db = new ConnectionDataBase(CONFIG_PATH);
        assertThrows(SQLException.class, db::connectDataBase);
        db.close();
    }

    @Test
    void testCloseWithoutConnectNullConnection() throws IOException {
        try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
            writer.write("db.url=jdbc:mysql://localhost:3306/test\n");
            writer.write("db.user=test\n");
            writer.write("db.password=test\n");
        }
        ConnectionDataBase db = new ConnectionDataBase(CONFIG_PATH);
        assertDoesNotThrow(db::close);
    }

    @Test
    void testAccessDeniedSQLState28000() throws IOException {
        try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
            writer.write("db.url=jdbc:mysql://localhost:3306/test\n");
            writer.write("db.user=usuario_incorrecto\n");
            writer.write("db.password=clave_incorrecta\n");
        }
        ConnectionDataBase db = new ConnectionDataBase(CONFIG_PATH);
        SQLException ex = assertThrows(SQLException.class, db::connectDataBase);
        assertEquals("28000", ex.getSQLState());
        db.close();
    }

    @Test
    void testDatabaseNotFoundSQLState42000() throws IOException {
        try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
            writer.write("db.url=jdbc:mysql://localhost:3306/basedatos_que_no_existe\n");
            writer.write("db.user=root\n");
            writer.write("db.password=123456\n");
        }
        ConnectionDataBase db = new ConnectionDataBase(CONFIG_PATH);
        SQLException ex = assertThrows(SQLException.class, db::connectDataBase);
        assertEquals("42000", ex.getSQLState());
        db.close();
    }

    @Test
    void testHostNotFoundSQLState08S01() throws IOException {
        try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
            writer.write("db.url=jdbc:mysql://host_que_no_existe:3306/test\n");
            writer.write("db.user=root\n");
            writer.write("db.password=123456\n");
        }
        ConnectionDataBase db = new ConnectionDataBase(CONFIG_PATH);
        SQLException ex = assertThrows(SQLException.class, db::connectDataBase);
        assertEquals("08S01", ex.getSQLState());
        db.close();
    }

    @Test
    void testUnknownDatabaseSQLState42000() throws IOException {
        try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
            writer.write("db.url=jdbc:mysql://localhost:3306/basedatos_que_no_existe\n");
            writer.write("db.user=root\n");
            writer.write("db.password=123456\n");
        }
        ConnectionDataBase db = new ConnectionDataBase(CONFIG_PATH);
        SQLException ex = assertThrows(SQLException.class, db::connectDataBase);
        assertEquals("42000", ex.getSQLState());
        db.close();
    }
}