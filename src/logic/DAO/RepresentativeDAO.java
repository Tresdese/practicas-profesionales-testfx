package logic.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import data_access.ConnectionDataBase;
import logic.DTO.RepresentativeDTO;
import logic.interfaces.IRepresentativeDAO;

public class RepresentativeDAO implements IRepresentativeDAO {

    private final static String SQL_INSERT = "INSERT INTO representante (nombres, apellidos, correo, idDepartamento) VALUES (?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE representante SET nombres = ?, apellidos = ?, correo = ?, idDepartamento = ? WHERE idRepresentante = ?";
    private final static String SQL_DELETE = "DELETE FROM representante WHERE idRepresentante = ?";
    private final static String SQL_SELECT_BY_ID = "SELECT * FROM representante WHERE idRepresentante = ?";
    private final static String SQL_SELECT_BY_EMAIL = "SELECT * FROM representante WHERE correo = ?";
    private final static String SQL_SELECT_BY_FULLNAME = "SELECT * FROM representante WHERE nombres = ? AND apellidos = ?";
    private final static String SQL_SELECT_BY_DEPARTMENT = "SELECT * FROM representante WHERE idDepartamento = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM representante";

    public boolean insertRepresentative(RepresentativeDTO representative) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, representative.getNames());
            statement.setString(2, representative.getSurnames());
            statement.setString(3, representative.getEmail());
            if (representative.getIdDepartment() != null && !representative.getIdDepartment().isEmpty()) {
                statement.setInt(4, Integer.parseInt(representative.getIdDepartment()));
            } else {
                statement.setNull(4, Types.INTEGER);
            }
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

    public boolean updateRepresentative(RepresentativeDTO representative) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, representative.getNames());
            statement.setString(2, representative.getSurnames());
            statement.setString(3, representative.getEmail());
            if (representative.getIdDepartment() != null && !representative.getIdDepartment().isEmpty()) {
                statement.setInt(4, Integer.parseInt(representative.getIdDepartment()));
            } else {
                statement.setNull(4, Types.INTEGER);
            }
            statement.setInt(5, Integer.parseInt(representative.getIdRepresentative()));
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteRepresentative(String idRepresentative) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setInt(1, Integer.parseInt(idRepresentative));
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public RepresentativeDTO searchRepresentativeById(String idRepresentative) throws SQLException {
        RepresentativeDTO representative = null;
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            statement.setInt(1, Integer.parseInt(idRepresentative));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                representative = new RepresentativeDTO(
                        String.valueOf(resultSet.getInt("idRepresentante")),
                        resultSet.getString("nombres"),
                        resultSet.getString("apellidos"),
                        resultSet.getString("correo"),
                        resultSet.getObject("idDepartamento") != null ? String.valueOf(resultSet.getInt("idDepartamento")) : ""
                );
            }
        }
        return representative;
    }

    public boolean isRepresentativeRegistered(String idRepresentative) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            statement.setInt(1, Integer.parseInt(idRepresentative));
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    public boolean isRepresentativeEmailRegistered(String email) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_EMAIL)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    public RepresentativeDTO searchRepresentativeByFullname(String names, String surnames) throws SQLException {
        RepresentativeDTO representative = null;
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_FULLNAME)) {
            statement.setString(1, names);
            statement.setString(2, surnames);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                representative = new RepresentativeDTO(
                        String.valueOf(resultSet.getInt("idRepresentante")),
                        resultSet.getString("nombres"),
                        resultSet.getString("apellidos"),
                        resultSet.getString("correo"),
                        resultSet.getObject("idDepartamento") != null ? String.valueOf(resultSet.getInt("idDepartamento")) : ""
                );
            }
        }
        return representative;
    }

    @Override
    public List<RepresentativeDTO> getAllRepresentatives() throws SQLException {
        List<RepresentativeDTO> representatives = new ArrayList<>();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                RepresentativeDTO representative = new RepresentativeDTO(
                        String.valueOf(resultSet.getInt("idRepresentante")),
                        resultSet.getString("nombres"),
                        resultSet.getString("apellidos"),
                        resultSet.getString("correo"),
                        resultSet.getObject("idDepartamento") != null ? String.valueOf(resultSet.getInt("idDepartamento")) : ""
                );
                representatives.add(representative);
            }
        }
        return representatives;
    }

    public String getRepresentativeNameById(String idRepresentative) throws SQLException {
        String representativeName = "";
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            statement.setInt(1, Integer.parseInt(idRepresentative));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String nombres = resultSet.getString("nombres");
                    String apellidos = resultSet.getString("apellidos");
                    if (nombres != null && apellidos != null) {
                        representativeName = nombres + " " + apellidos;
                    } else {
                        representativeName = "Sin nombre";
                    }
                }
            }
        }
        return representativeName;
    }

    public List<RepresentativeDTO> getRepresentativesByDepartment(String idDepartment) throws SQLException {
        List<RepresentativeDTO> representatives = new ArrayList<>();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_DEPARTMENT)) {
            statement.setInt(1, Integer.parseInt(idDepartment));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    representatives.add(new RepresentativeDTO(
                            String.valueOf(resultSet.getInt("idRepresentante")),
                            resultSet.getString("nombres"),
                            resultSet.getString("apellidos"),
                            resultSet.getString("correo"),
                            resultSet.getObject("idDepartamento") != null ? String.valueOf(resultSet.getInt("idDepartamento")) : ""
                    ));
                }
            }
        }
        return representatives;
    }
}