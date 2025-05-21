package logic.interfaces;

import logic.DTO.GroupDTO;

import java.sql.SQLException;
import java.util.List;

public interface IGroupDAO {
    boolean insertGroup(GroupDTO group) throws SQLException;

    boolean updateGroup(GroupDTO group) throws SQLException;

    boolean deleteGroup(String NRC) throws SQLException;

    GroupDTO searchGroupById(String NRC) throws SQLException;

    List<GroupDTO> getAllGroups() throws SQLException;
}
