package logic.DAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import data_access.ConnectionDataBase;
import logic.DTO.GroupDTO;
import logic.interfaces.IGroupDAO;

public class GroupDAO  implements IGroupDAO {
    private final static String SQL_INSERT = "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE grupo SET nombre = ?, idUsuario = ?, idPeriodo = ? WHERE NRC = ?";
    private final static String SQL_DELETE = "DELETE FROM grupo WHERE NRC = ?";
    private final static String SQL_SELECT = "SELECT * FROM grupo WHERE NRC = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM grupo";

    public boolean insertGroup(GroupDTO group) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, group.getNRC());
            statement.setString(2, group.getName());
            statement.setString(3, group.getIdUser());
            statement.setString(4, group.getIdPeriod());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateGroup(GroupDTO group) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, group.getName());
            statement.setString(2, group.getIdUser());
            statement.setString(3, group.getIdPeriod());
            statement.setString(4, group.getNRC());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteGroup(String NRC) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, NRC);
            return statement.executeUpdate() > 0;
        }
    }

    public GroupDTO searchGroupById(String NRC) throws SQLException, IOException {
        GroupDTO group = new GroupDTO("N/A", "N/A", "N/A", "N/A");
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, NRC);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    group = new GroupDTO(resultSet.getString("NRC"), resultSet.getString("nombre"), resultSet.getString("idUsuario"), resultSet.getString("idPeriodo"));
                }
            }
        }
        return group;
    }

    public List<GroupDTO> getAllGroups() throws SQLException, IOException {
        List<GroupDTO> groups = new ArrayList<>();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                groups.add(new GroupDTO(resultSet.getString("NRC"), resultSet.getString("nombre"), resultSet.getString("idUsuario"), resultSet.getString("idPeriodo")));
            }
        }
        return groups;
    }
}
