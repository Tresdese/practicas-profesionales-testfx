package logic.interfaces;

import logic.DTO.GroupDTO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IGroupDAO {
    boolean insertGroup(GroupDTO group) throws SQLException, IOException;

    boolean updateGroup(GroupDTO group) throws SQLException, IOException;

    boolean deleteGroup(String NRC) throws SQLException, IOException;

    GroupDTO searchGroupById(String NRC) throws SQLException, IOException;

    List<GroupDTO> getAllGroups() throws SQLException, IOException;
}
