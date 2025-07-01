package logic.interfaces;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.RepresentativeDTO;

public interface IRepresentativeDAO {
    boolean insertRepresentative(RepresentativeDTO representative) throws SQLException, IOException;

    boolean updateRepresentative(RepresentativeDTO representative) throws SQLException, IOException;

    boolean deleteRepresentative(String idRepresentative) throws SQLException, IOException;

    RepresentativeDTO searchRepresentativeById(String idRepresentative) throws SQLException, IOException;

    boolean isRepresentativeRegistered(String idRepresentative) throws SQLException, IOException;

    boolean isRepresentativeEmailRegistered(String email) throws SQLException, IOException;

    RepresentativeDTO searchRepresentativeByFirstName(String names) throws SQLException, IOException;

    String getRepresentativeNameById(String idRepresentative) throws SQLException, IOException;

    List<RepresentativeDTO> getAllRepresentatives() throws SQLException, IOException;

    List<RepresentativeDTO> getRepresentativesByDepartment(String idDepartment) throws SQLException, IOException;

    List<RepresentativeDTO> getRepresentativesByOrganization(String idOrganization) throws SQLException, IOException;
}
