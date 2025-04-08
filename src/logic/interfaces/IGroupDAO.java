package logic.interfaces;

import logic.DTO.GroupDTO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IGroupDAO {
    boolean insertGroup(GroupDTO group, Connection connection) throws SQLException;

    boolean updateGroup(GroupDTO group, Connection connection) throws SQLException;

    boolean deleteGroup(String NRC, Connection connection) throws SQLException;

    GroupDTO getGroup(String NRC, Connection connection) throws SQLException;

    List<GroupDTO> getAllGroups(Connection connection) throws SQLException;
}
