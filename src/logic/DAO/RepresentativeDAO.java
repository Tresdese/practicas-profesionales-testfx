package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import data_access.ConnectionDataBase;
import logic.DTO.RepresentativeDTO;
import logic.interfaces.IRepresentativeDAO;

public class RepresentativeDAO implements IRepresentativeDAO {

    private final static String SQL_INSERT = "INSERT INTO representante (idRepresentante, nombres, apellidos, correo, idOrganizacion) VALUES (?, ?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE representante SET nombres = ?, apellidos = ?, correo = ?, idOrganizacion = ? WHERE idRepresentante = ?";
    private final static String SQL_DELETE = "DELETE FROM representante WHERE idRepresentante = ?";
    private final static String SQL_SELECT_BY_ID = "SELECT * FROM representante WHERE idRepresentante = ?";
    private final static String SQL_SELECT_BY_EMAIL = "SELECT * FROM representante WHERE correo = ?";
    private final static String SQL_SELECT_BY_FULLNAME = "SELECT * FROM representante WHERE nombres = ? AND apellidos = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM representante";


    public boolean insertRepresentative(RepresentativeDTO representative) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, representative.getIdRepresentative());
            statement.setString(2, representative.getNames());
            statement.setString(3, representative.getSurnames());
            statement.setString(4, representative.getEmail());
            statement.setString(5, representative.getIdOrganization());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateRepresentative(RepresentativeDTO representative) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, representative.getNames());
            statement.setString(2, representative.getSurnames());
            statement.setString(3, representative.getEmail());
            statement.setString(4, representative.getIdOrganization());
            statement.setString(5, representative.getIdRepresentative());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteRepresentative(String idRepresentative) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, idRepresentative);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public RepresentativeDTO searchRepresentativeById(String idRepresentative) throws SQLException {
        RepresentativeDTO representative = new RepresentativeDTO("N/A", "N/A", "N/A", "N/A", "N/A");
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            statement.setString(1, idRepresentative);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                representative = new RepresentativeDTO(
                        resultSet.getString("idRepresentante"),
                        resultSet.getString("nombres"),
                        resultSet.getString("apellidos"),
                        resultSet.getString("correo"),
                        resultSet.getString("idOrganizacion")
                );
            }
        }
        return representative;
    }

    public boolean isRepresentativeRegistered(String idRepresentative) throws SQLException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            statement.setString(1, idRepresentative);
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
        RepresentativeDTO representative = new RepresentativeDTO("N/A", "N/A", "N/A", "N/A", "N/A");
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_FULLNAME)) {
            statement.setString(1, names);
            statement.setString(2, surnames);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                representative = new RepresentativeDTO(
                        resultSet.getString("idRepresentante"),
                        resultSet.getString("nombres"),
                        resultSet.getString("apellidos"),
                        resultSet.getString("correo"),
                        resultSet.getString("idOrganizacion")
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
                        resultSet.getString("idRepresentante"),
                        resultSet.getString("nombres"),
                        resultSet.getString("apellidos"),
                        resultSet.getString("correo"),
                        resultSet.getString("idOrganizacion")
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
            statement.setString(1, idRepresentative);
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

    public List<RepresentativeDTO> getRepresentativesByOrganization(String idOrganization) throws SQLException {
        List<RepresentativeDTO> representatives = new ArrayList<>();
        String sql = "SELECT * FROM representante WHERE idOrganizacion = ?";
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDB();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, idOrganization);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    representatives.add(new RepresentativeDTO(
                            resultSet.getString("idRepresentante"),
                            resultSet.getString("nombres"),
                            resultSet.getString("apellidos"),
                            resultSet.getString("correo"),
                            resultSet.getString("idOrganizacion")
                    ));
                }
            }
        }
        return representatives;
    }
}