package logic.interfaces;

import logic.DTO.ProjectPresentationDTO;

import java.sql.SQLException;
import java.util.List;

public interface IProjectPresentationDAO {

    boolean insertProjectPresentation(ProjectPresentationDTO projectPresentation) throws SQLException;

    boolean updateProjectPresentation(ProjectPresentationDTO projectPresentation) throws SQLException;

    boolean deleteProjectPresentation(int idPresentation) throws SQLException;

    ProjectPresentationDTO searchProjectPresentationById(int idPresentation) throws SQLException;

    List<ProjectPresentationDTO> searchProjectPresentationsByProjectId(String idProject) throws SQLException;

    List<ProjectPresentationDTO> getAllProjectPresentations() throws SQLException;

    List<ProjectPresentationDTO> getUpcomingPresentations() throws SQLException;

}
