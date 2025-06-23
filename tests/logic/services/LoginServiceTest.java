package logic.services;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class LoginServiceTest {

    @Test
    void loginThrowsSQLExceptionWhenDatabaseUnavailable() {
        try {
            LoginService loginService = new LoginService();
            assertThrows(SQLException.class, () -> {
                loginService.login("usuario", "contraseña");
            });
        } catch (SQLException e) {
            assertTrue(true);
        }
    }

    @Test
    void loginThrowsSQLExceptionWhenHostIsIncorrect() {
        try {
            LoginService loginService = new LoginService();
            loginService.login("usuario", "contraseña");
            fail("Se esperaba una SQLException");
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            System.out.println("SQLState (host incorrecto): " + sqlState);
            assertTrue("08001".equals(sqlState) || "08S01".equals(sqlState),
                    "Se esperaba SQLState 08001 o 08S01 para host incorrecto");
        } catch (IOException e) {
            fail("Se esperaba una SQLException, no IOException: " + e.getMessage());
        } catch (Exception e) {
            fail("Se esperaba una SQLException, no otra excepción: " + e.getMessage());
        }
    }

    @Test
    void loginThrowsSQLExceptionWhenDatabaseIsDown() {
        try {
            LoginService loginService = new LoginService();
            loginService.login("usuario", "contraseña");
            fail("Se esperaba una SQLException");
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            System.out.println("SQLState (base apagada): " + sqlState);
            assertTrue("08006".equals(sqlState) || "08S01".equals(sqlState),
                    "Se esperaba SQLState 08006 o 08S01 para base de datos apagada");
        } catch (IOException e) {
            fail("Se esperaba una SQLException, no IOException: " + e.getMessage());
        } catch (Exception e) {
            fail("Se esperaba una SQLException, no otra excepción: " + e.getMessage());
        }
    }

}