package logic.interfaces;

import logic.DTO.ProjectPresentationDTO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IProjectPresentationDAO {

    boolean insertProjectPresentation(ProjectPresentationDTO projectPresentation) throws SQLException, IOException;

    boolean updateProjectPresentation(ProjectPresentationDTO projectPresentation) throws SQLException, IOException;

    boolean deleteProjectPresentation(int idPresentation) throws SQLException, IOException;

    ProjectPresentationDTO searchProjectPresentationById(int idPresentation) throws SQLException, IOException;

    List<ProjectPresentationDTO> searchProjectPresentationsByProjectId(String idProject) throws SQLException, IOException;

    List<ProjectPresentationDTO> getAllProjectPresentations() throws SQLException, IOException;

    List<ProjectPresentationDTO> getUpcomingPresentations() throws SQLException, IOException;

}
