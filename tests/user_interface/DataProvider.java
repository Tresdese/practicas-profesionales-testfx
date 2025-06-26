package user_interface;

import java.util.List;

import logic.DTO.DepartmentDTO;
import logic.DTO.LinkedOrganizationDTO;
import logic.DTO.Role;
import logic.DTO.UserDTO;

public class DataProvider {

    public static List<UserDTO> getAllUsers() {
        return List.of(new UserDTO("1", 1, "123", "Juan", "Pérez", "juan", "pass", Role.ACADEMICO));
    }

    public static List<LinkedOrganizationDTO> getAllLinkedOrganizations() {
        return List.of(new LinkedOrganizationDTO("1", "Organization 1", "Director 1", 1),
                       new LinkedOrganizationDTO("2", "Organization 2", "Director 2", 1));
    }

    public static List<DepartmentDTO> getAllDepartments() {
        return List.of(new DepartmentDTO(1, "Department 1", "Description 1", 1, 1),
                       new DepartmentDTO(2, "Department 2", "Description 2", 2, 1));
    }
}
