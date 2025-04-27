package logic.interfaces;

import logic.DTO.EvidenceDTO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IEvidenceDAO {
    boolean insertEvidence(EvidenceDTO evidence, Connection connection) throws SQLException;

    boolean updateEvidence(EvidenceDTO evidence, Connection connection) throws SQLException;

    boolean deleteEvidence(int idEvidence, Connection connection) throws SQLException;

    EvidenceDTO searchEvidenceById(int idEvidence, Connection connection) throws SQLException;

    List<EvidenceDTO> getAllEvidences(Connection connection) throws SQLException;
}
