package logic.DAO;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import data_access.ConnectionDataBase;
import logic.DTO.RepresentativeDTO;
import logic.interfaces.IRepresentativeDAO;

public class RepresentativeDAO implements IRepresentativeDAO {

    private final static String SQL_INSERT = "INSERT INTO representante (nombres, apellidos, correo, idOrganizacion, idDepartamento, estado) VALUES (?, ?, ?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE representante SET nombres = ?, apellidos = ?, correo = ?, idOrganizacion = ?, idDepartamento = ? WHERE idRepresentante = ?";
    private final static String SQL_UPDATE_STATUS = "UPDATE representante SET estado = ? WHERE idRepresentante = ?";
    private final static String SQL_DELETE = "DELETE FROM representante WHERE idRepresentante = ?";
    private final static String SQL_SELECT_BY_ID = "SELECT * FROM representante WHERE idRepresentante = ?";
    private final static String SQL_SELECT_BY_FIRSTNAME = "SELECT * FROM representante WHERE nombres = ?";
    private final static String SQL_SELECT_DEPARTMENT_BY_ID = "SELECT * FROM representante WHERE idDepartamento = ?";
    private final static String SQL_SELECT_ORGANIZATION_BY_ID = "SELECT * FROM representante WHERE idOrganizacion = ?";
    private final static String SQL_SELECT_NAME_BY_ID = "SELECT nombres, apellidos FROM representante WHERE idRepresentante = ?";
    private final static String SQL_SELECT_REGISTERED_ID = "SELECT 1 FROM representante WHERE idRepresentante = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM representante";

    public boolean insertRepresentative(RepresentativeDTO representative) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, representative.getNames());
            statement.setString(2, representative.getSurnames());
            statement.setString(3, representative.getEmail());
            if (representative.getIdOrganization() != null && !representative.getIdOrganization().isEmpty()) {
                statement.setInt(4, Integer.parseInt(representative.getIdOrganization()));
            } else {
                statement.setNull(4, Types.INTEGER);
            }
            if (representative.getIdDepartment() != null && !representative.getIdDepartment().isEmpty()) {
                statement.setInt(5, Integer.parseInt(representative.getIdDepartment()));
            } else {
                statement.setNull(5, Types.INTEGER);
            }
            statement.setInt(6, representative.getStatus());
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        representative.setIdRepresentative(String.valueOf(generatedKeys.getInt(1)));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public boolean updateRepresentative(RepresentativeDTO representative) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, representative.getNames());
            statement.setString(2, representative.getSurnames());
            statement.setString(3, representative.getEmail());
            if (representative.getIdOrganization() != null && !representative.getIdOrganization().isEmpty()) {
                statement.setInt(4, Integer.parseInt(representative.getIdOrganization()));
            } else {
                statement.setNull(4, Types.INTEGER);
            }
            if (representative.getIdDepartment() != null && !representative.getIdDepartment().isEmpty()) {
                statement.setInt(5, Integer.parseInt(representative.getIdDepartment()));
            } else {
                statement.setNull(5, Types.INTEGER);
            }
            statement.setInt(6, Integer.parseInt(representative.getIdRepresentative()));
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateRepresentativeStatus(String idRepresentative, int status) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_STATUS)) {
            statement.setInt(1, status);
            statement.setInt(2, Integer.parseInt(idRepresentative));
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteRepresentative(String idRepresentative) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setInt(1, Integer.parseInt(idRepresentative));
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public RepresentativeDTO searchRepresentativeById(String idRepresentative) throws SQLException, IOException {
        RepresentativeDTO representative = new RepresentativeDTO("N/A", "N/A", "N/A", "N/A", "N/A", "N/A", 0);        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
                                                                                                                           Connection connection = connectionDataBase.connectDataBase();
                                                                                                                           PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            statement.setInt(1, Integer.parseInt(idRepresentative));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                representative = new RepresentativeDTO(
                        String.valueOf(resultSet.getInt("idRepresentante")),
                        resultSet.getString("nombres"),
                        resultSet.getString("apellidos"),
                        resultSet.getString("correo"),
                        resultSet.getObject("idOrganizacion") != null ? String.valueOf(resultSet.getInt("idOrganizacion")) : "",
                        resultSet.getObject("idDepartamento") != null ? String.valueOf(resultSet.getInt("idDepartamento")) : "",
                        resultSet.getInt("estado")
                );
            }
        }
        return representative;
    }

    @Override
    public boolean isRepresentativeRegistered(String idRepresentative) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM representante WHERE idRepresentante = ?")) {
            statement.setInt(1, Integer.parseInt(idRepresentative));
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public boolean isRepresentativeEmailRegistered(String email) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_REGISTERED_ID)) {
            statement.setString(1, email);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    public RepresentativeDTO searchRepresentativeByFirstName(String names) throws SQLException, IOException {
        RepresentativeDTO representative = null;
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_FIRSTNAME)) {
            statement.setString(1, names);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    representative = new RepresentativeDTO(
                            String.valueOf(resultSet.getInt("idRepresentante")),
                            resultSet.getString("nombres"),
                            resultSet.getString("apellidos"),
                            resultSet.getString("correo"),
                            resultSet.getObject("idOrganizacion") != null ? String.valueOf(resultSet.getInt("idOrganizacion")) : "",
                            resultSet.getObject("idDepartamento") != null ? String.valueOf(resultSet.getInt("idDepartamento")) : "",
                            resultSet.getInt("estado")
                    );
                }
            }
        }
        return representative;
    }

    @Override
    public String getRepresentativeNameById(String idRepresentative) throws SQLException, IOException {
        String name = "";
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_NAME_BY_ID)) {
            statement.setInt(1, Integer.parseInt(idRepresentative));
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString("nombres") + " " + rs.getString("apellidos");
                }
            }
        }
        return name;
    }

    @Override
    public List<RepresentativeDTO> getRepresentativesByDepartment(String idDepartment) throws SQLException, IOException {
        List<RepresentativeDTO> representatives = new ArrayList<>();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_DEPARTMENT_BY_ID)) {
            statement.setInt(1, Integer.parseInt(idDepartment));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    representatives.add(new RepresentativeDTO(
                            String.valueOf(resultSet.getInt("idRepresentante")),
                            resultSet.getString("nombres"),
                            resultSet.getString("apellidos"),
                            resultSet.getString("correo"),
                            resultSet.getObject("idOrganizacion") != null ? String.valueOf(resultSet.getInt("idOrganizacion")) : "",
                            resultSet.getObject("idDepartamento") != null ? String.valueOf(resultSet.getInt("idDepartamento")) : "",
                            resultSet.getInt("estado")
                    ));
                }
            }
        }
        return representatives;
    }

    public List<RepresentativeDTO> getRepresentativesByOrganization(String idOrganization) throws SQLException, IOException {
        List<RepresentativeDTO> representatives = new ArrayList<>();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ORGANIZATION_BY_ID)) {
            statement.setInt(1, Integer.parseInt(idOrganization));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    representatives.add(new RepresentativeDTO(
                            String.valueOf(resultSet.getInt("idRepresentante")),
                            resultSet.getString("nombres"),
                            resultSet.getString("apellidos"),
                            resultSet.getString("correo"),
                            resultSet.getObject("idOrganizacion") != null ? String.valueOf(resultSet.getInt("idOrganizacion")) : "",
                            resultSet.getObject("idDepartamento") != null ? String.valueOf(resultSet.getInt("idDepartamento")) : "",
                            resultSet.getInt("estado")
                    ));
                }
            }
        }
        return representatives;
    }

    @Override
    public List<RepresentativeDTO> getAllRepresentatives() throws SQLException, IOException {
        List<RepresentativeDTO> representatives = new ArrayList<>();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                RepresentativeDTO representative = new RepresentativeDTO(
                        String.valueOf(resultSet.getInt("idRepresentante")),
                        resultSet.getString("nombres"),
                        resultSet.getString("apellidos"),
                        resultSet.getString("correo"),
                        resultSet.getObject("idOrganizacion") != null ? String.valueOf(resultSet.getInt("idOrganizacion")) : "",
                        resultSet.getObject("idDepartamento") != null ? String.valueOf(resultSet.getInt("idDepartamento")) : "",
                        resultSet.getInt("estado")
                );
                representatives.add(representative);
            }
        }
        return representatives;
    }

}