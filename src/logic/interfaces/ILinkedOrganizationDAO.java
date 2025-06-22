package logic.interfaces;

import logic.DTO.LinkedOrganizationDTO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface ILinkedOrganizationDAO {

    String insertLinkedOrganizationAndGetId(LinkedOrganizationDTO organization) throws SQLException, IOException;

    boolean updateLinkedOrganization(LinkedOrganizationDTO organization) throws SQLException, IOException;

    boolean deleteLinkedOrganization(String idOrganization) throws SQLException, IOException;

    LinkedOrganizationDTO searchLinkedOrganizationById(String idOrganization) throws SQLException, IOException;

    List<LinkedOrganizationDTO> getAllLinkedOrganizations() throws SQLException, IOException;

    LinkedOrganizationDTO searchLinkedOrganizationByName(String name) throws SQLException, IOException;

    boolean isLinkedOrganizationRegistered(String idOrganization) throws SQLException, IOException;

    boolean isNameRegistered(String name) throws SQLException, IOException;

    boolean isAddressRegistered(String address) throws SQLException, IOException;

    String getOrganizationNameById(String idOrganization) throws SQLException, IOException;

}
