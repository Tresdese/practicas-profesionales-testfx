package logic.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.RepresentativeDTO;

public interface IRepresentativeDAO {
    boolean insertRepresentative(RepresentativeDTO representative, Connection connection) throws SQLException;

    boolean updateRepresentative(RepresentativeDTO representative, Connection connection) throws SQLException;

    boolean deleteRepresentative(String idRepresentative, Connection connection) throws SQLException;

    RepresentativeDTO getRepresentative(String idRepresentative, Connection connection) throws SQLException;

    List<RepresentativeDTO> getAllRepresentatives(Connection connection) throws SQLException;
}
