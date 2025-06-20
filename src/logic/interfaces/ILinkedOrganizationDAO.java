package logic.interfaces;

import logic.DTO.LinkedOrganizationDTO;
import java.sql.SQLException;
import java.util.List;

public interface ILinkedOrganizationDAO {
    String insertLinkedOrganizationAndGetId(LinkedOrganizationDTO organization) throws SQLException;

    boolean updateLinkedOrganization(LinkedOrganizationDTO organization) throws SQLException;

    boolean deleteLinkedOrganization(String idOrganization) throws SQLException;

    LinkedOrganizationDTO searchLinkedOrganizationById(String idOrganization) throws SQLException;

    List<LinkedOrganizationDTO> getAllLinkedOrganizations() throws SQLException;

    LinkedOrganizationDTO searchLinkedOrganizationByName(String name) throws SQLException;

    boolean isLinkedOrganizationRegistered(String idOrganization) throws SQLException;

    boolean isNameRegistered(String name) throws SQLException;

    boolean isAddressRegistered(String address) throws SQLException;

    String getOrganizationNameById(String idOrganization) throws SQLException;

}
