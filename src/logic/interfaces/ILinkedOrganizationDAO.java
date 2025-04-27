package logic.interfaces;

import logic.DTO.LinkedOrganizationDTO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ILinkedOrganizationDAO {
    boolean insertLinkedOrganization(LinkedOrganizationDTO organization, Connection connection) throws SQLException;

    boolean updateLinkedOrganization(LinkedOrganizationDTO organization, Connection connection) throws SQLException;

    boolean deleteLinkedOrganization(String idOrganization, Connection connection) throws SQLException;

    LinkedOrganizationDTO searchLinkedOrganizationById(String idOrganization, Connection connection) throws SQLException;

    List<LinkedOrganizationDTO> getAllLinkedOrganizations(Connection connection) throws SQLException;
}
