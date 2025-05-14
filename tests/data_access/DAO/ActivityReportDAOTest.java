package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.ActivityReportDAO;
import logic.DTO.ActivityReportDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActivityReportDAOTest {
    private Connection connection;
    private ActivityReportDAO activityReportDAO;
    private int idEvidencia;
    private int idReporte;
    private int idActividad;

    @BeforeEach
    void setUp() throws SQLException {
        // Establece conexión a la base de datos
        ConecctionDataBase connectionDataBase = new ConecctionDataBase();
        connection = connectionDataBase.connectDB();
        activityReportDAO = new ActivityReportDAO();

        // Limpia todas las tablas relevantes para evitar conflictos
        limpiarTablasRelacionadas();

        String insertEvidenceSQL = "INSERT INTO evidencia (nombreEvidencia, fechaEntrega, ruta) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertEvidenceSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, "Evidencia de prueba");
            preparedStatement.setString(2, "2023-06-01");
            preparedStatement.setString(3, "/ruta/evidencia");
            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idEvidencia = generatedKeys.getInt(1);
                }
            }
        }

        String insertReportSQL = "INSERT INTO reporte (observaciones, idEvidencia) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertReportSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, "Observaciones de prueba");
            preparedStatement.setInt(2, idEvidencia);
            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idReporte = generatedKeys.getInt(1);
                }
            }
        }

        // Inserta datos en actividad y obtiene el ID generado
        String insertActivitySQL = "INSERT INTO actividad (idActividad, nombreActividad) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertActivitySQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, "1");
            preparedStatement.setString(2, "Descripción de actividad de prueba");
            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idActividad = generatedKeys.getInt(1);
                }
            }
        }

        // Verificar que los datos se hayan insertado correctamente
        verificarDatosInsertados();
    }

    private void verificarDatosInsertados() throws SQLException {
        // Verifica que la evidencia se insertó correctamente
        try (PreparedStatement checkStatement = connection.prepareStatement("SELECT COUNT(*) FROM evidencia WHERE idEvidencia = ?")) {
            checkStatement.setInt(1, idEvidencia);
            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) == 0) {
                    throw new SQLException("La evidencia no se insertó correctamente");
                }
            }
        }

        // Verifica que el reporte se insertó correctamente
        try (PreparedStatement checkStatement = connection.prepareStatement("SELECT COUNT(*) FROM reporte WHERE idReporte = ?")) {
            checkStatement.setInt(1, idReporte);
            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) == 0) {
                    throw new SQLException("El reporte no se insertó correctamente");
                }
            }
        }

        // Verifica que la actividad se insertó correctamente
        try (PreparedStatement checkStatement = connection.prepareStatement("SELECT COUNT(*) FROM actividad WHERE idActividad = ?")) {
            checkStatement.setInt(1, idActividad);
            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) == 0) {
                    throw new SQLException("La actividad no se insertó correctamente");
                }
            }
        }
    }

    private void limpiarTablasRelacionadas() throws SQLException {
        // Elimina los registros de las tablas en orden para evitar conflictos de clave foránea
        connection.createStatement().execute("DELETE FROM reporte_actividad");
        connection.createStatement().execute("DELETE FROM reporte");
        connection.createStatement().execute("DELETE FROM actividad");
        connection.createStatement().execute("DELETE FROM evidencia");

        // Resetea los contadores de autoincremento
        connection.createStatement().execute("ALTER TABLE evidencia AUTO_INCREMENT = 1");
        connection.createStatement().execute("ALTER TABLE reporte AUTO_INCREMENT = 1");
        connection.createStatement().execute("ALTER TABLE actividad AUTO_INCREMENT = 1");

        try {
            connection.createStatement().execute("ALTER TABLE reporte_actividad AUTO_INCREMENT = 1");
        } catch (SQLException ignored) {
            // Es posible que esta tabla no tenga campo AUTO_INCREMENT
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Limpia todas las tablas para dejar la BD en estado inicial
        limpiarTablasRelacionadas();

        // Cierra la conexión
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void insertActivityReportSuccessfully() throws SQLException {
        // Usa los IDs generados en setUp()
        ActivityReportDTO report = new ActivityReportDTO(String.valueOf(idReporte), String.valueOf(idActividad));

        // Asegura que no exista un registro previo para evitar duplicados
        connection.createStatement().execute("DELETE FROM reporte_actividad WHERE numReporte = " + idReporte);

        boolean inserted = activityReportDAO.insertActivityReport(report);

        assertTrue(inserted, "El reporte debería insertarse correctamente.");
    }

    @Test
    void updateActivityReportSuccessfully() throws SQLException {
        // Usa los IDs generados en setUp()
        ActivityReportDTO report = new ActivityReportDTO(String.valueOf(idReporte), String.valueOf(idActividad));

        // Asegura que no exista un registro previo para evitar duplicados
        connection.createStatement().execute("DELETE FROM reporte_actividad WHERE numReporte = " + idReporte);

        // Inserta primero para luego actualizar
        activityReportDAO.insertActivityReport(report);

        ActivityReportDTO updatedReport = new ActivityReportDTO(String.valueOf(idReporte), String.valueOf(idActividad));
        boolean updated = activityReportDAO.updateActivityReport(updatedReport);

        assertTrue(updated, "El reporte debería actualizarse correctamente.");
    }

    @Test
    void updateActivityReportFailsWhenNotExists() throws SQLException {
        // Usa un ID que no existe
        ActivityReportDTO report = new ActivityReportDTO("999", "102");

        boolean updated = activityReportDAO.updateActivityReport(report);

        assertFalse(updated, "No debería permitir actualizar un reporte inexistente.");
    }

    @Test
    void deleteActivityReportSuccessfully() throws SQLException {
        // Usa los IDs generados en setUp()
        ActivityReportDTO report = new ActivityReportDTO(String.valueOf(idReporte), String.valueOf(idActividad));

        // Asegura que no exista un registro previo para evitar duplicados
        connection.createStatement().execute("DELETE FROM reporte_actividad WHERE numReporte = " + idReporte);

        // Inserta primero para luego eliminar
        activityReportDAO.insertActivityReport(report);

        boolean deleted = activityReportDAO.deleteActivityReport(String.valueOf(idReporte));

        assertTrue(deleted, "El reporte debería eliminarse correctamente.");
    }

    @Test
    void deleteActivityReportFailsWhenNotExists() throws SQLException {
        boolean deleted = activityReportDAO.deleteActivityReport("999");

        assertFalse(deleted, "No debería permitir eliminar un reporte inexistente.");
    }

    @Test
    void searchActivityReportByReportNumberWhenExists() throws SQLException {
        // Usa los IDs generados en setUp()
        ActivityReportDTO report = new ActivityReportDTO(String.valueOf(idReporte), String.valueOf(idActividad));

        // Asegura que no exista un registro previo para evitar duplicados
        connection.createStatement().execute("DELETE FROM reporte_actividad WHERE numReporte = " + idReporte);

        // Inserta el reporte para buscarlo luego
        activityReportDAO.insertActivityReport(report);

        ActivityReportDTO result = activityReportDAO.searchActivityReportByReportNumber(String.valueOf(idReporte));

        assertNotNull(result, "El reporte no debería ser nulo.");
        assertEquals(String.valueOf(idReporte), result.getNumberReport());
        assertEquals(String.valueOf(idActividad), result.getIdActivity());
    }

    @Test
    void searchActivityReportByReportNumberWhenNotExists() throws SQLException {
        ActivityReportDTO result = activityReportDAO.searchActivityReportByReportNumber("999");

        assertNotNull(result, "El reporte no debería ser nulo.");
        assertEquals("N/A", result.getNumberReport());
        assertEquals("N/A", result.getIdActivity());
    }

    @Test
    void getAllActivityReportsReturnsList() throws SQLException {
        // Usa los IDs generados en setUp() para el primer reporte
        ActivityReportDTO report1 = new ActivityReportDTO(String.valueOf(idReporte), String.valueOf(idActividad));

        // Crea un segundo reporte con ID diferente
        // Primero inserta un nuevo reporte en la tabla reporte
        String insertReportSQL = "INSERT INTO reporte (observaciones, idEvidencia) VALUES (?, ?)";
        int idReporte2;
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertReportSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, "Observaciones de prueba 2");
            preparedStatement.setInt(2, idEvidencia);
            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idReporte2 = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID generado para el reporte 2");
                }
            }
        }

        ActivityReportDTO report2 = new ActivityReportDTO(String.valueOf(idReporte2), String.valueOf(idActividad));

        // Asegura que no existan registros previos para evitar duplicados
        connection.createStatement().execute("DELETE FROM reporte_actividad");

        // Inserta los reportes para prueba
        activityReportDAO.insertActivityReport(report1);
        activityReportDAO.insertActivityReport(report2);

        List<ActivityReportDTO> result = activityReportDAO.getAllActivityReports();

        assertNotNull(result, "La lista de reportes no debería ser nula.");
        assertEquals(2, result.size(), "Debería haber exactamente 2 reportes en la lista.");
    }

    @Test
    void getAllActivityReportsReturnsEmptyListWhenNoReportsExist() throws SQLException {
        // Limpia la tabla reporte_actividad para asegurar que no haya registros
        connection.createStatement().execute("DELETE FROM reporte_actividad");

        List<ActivityReportDTO> result = activityReportDAO.getAllActivityReports();

        assertNotNull(result, "La lista de reportes no debería ser nula.");
        assertTrue(result.isEmpty(), "La lista de reportes debería estar vacía.");
    }
}