package logic.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.RepresentativeDTO;

public interface IRepresentativeDAO {
    boolean insertRepresentative(RepresentativeDTO representative) throws SQLException;

    boolean updateRepresentative(RepresentativeDTO representative) throws SQLException;

    boolean deleteRepresentative(String idRepresentative) throws SQLException;

    RepresentativeDTO searchRepresentativeById(String idRepresentative) throws SQLException;

    List<RepresentativeDTO> getAllRepresentatives() throws SQLException;
}
