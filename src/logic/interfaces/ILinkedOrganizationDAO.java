package logic.interfaces;

import logic.DTO.LinkedOrganizationDTO;
import java.sql.SQLException;
import java.util.List;

public interface ILinkedOrganizationDAO {
    boolean insertLinkedOrganization(LinkedOrganizationDTO organization) throws SQLException;

    boolean updateLinkedOrganization(LinkedOrganizationDTO organization) throws SQLException;

    boolean deleteLinkedOrganization(String idOrganization) throws SQLException;

    LinkedOrganizationDTO searchLinkedOrganizationById(String idOrganization) throws SQLException;

    List<LinkedOrganizationDTO> getAllLinkedOrganizations() throws SQLException;
}
