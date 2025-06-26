package user_interface.controllers;

import gui.GUI_RegisterProjectController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import logic.DAO.DepartmentDAO;
import logic.DTO.*;
import logic.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import logic.services.UserService;
import user_interface.DataProvider;


import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testfx.assertions.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class RegisterProjectControllerTest extends ApplicationTest {

    @Mock
    private UserService userServiceMock;

    @Mock
    private LinkedOrganizationService orgServiceMock;

    @Mock
    private DepartmentDAO departmentDAOMock;

    @Mock
    private ProjectService projectServiceMock;

    @InjectMocks
    private GUI_RegisterProjectController controller;

    @BeforeEach
    void setUp() throws Exception {
        when(userServiceMock.getAllUsers()).thenReturn(DataProvider.getAllUsers());
        when(orgServiceMock.getAllLinkedOrganizations()).thenReturn(DataProvider.getAllLinkedOrganizations());
        when(departmentDAOMock.getAllDepartments()).thenReturn(DataProvider.getAllDepartments());
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterProject.fxml"));
        GUI_RegisterProjectController testController = new GUI_RegisterProjectController(userServiceMock, orgServiceMock, departmentDAOMock, projectServiceMock);
        loader.setController(testController);
        Parent root = loader.load();
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void testSuccessProjectRegister() {
        clickOn("#nameField").write("Proyecto Test");
        clickOn("#descriptionField").write("Descripción de prueba");

        interact(() -> {
            ChoiceBox<UserDTO> academicBox = (ChoiceBox<UserDTO>) lookup("#academicBox").query();
            academicBox.getSelectionModel().selectFirst();

            ChoiceBox<LinkedOrganizationDTO> orgBox = (ChoiceBox<LinkedOrganizationDTO>) lookup("#organizationBox").query();
            orgBox.getSelectionModel().selectFirst();

            ChoiceBox<DepartmentDTO> deptBox = (ChoiceBox<DepartmentDTO>) lookup("#departmentBox").query();
            deptBox.getSelectionModel().selectFirst();

            ((DatePicker) lookup("#startDatePicker").query()).setValue(LocalDate.of(2024, 1, 1));
            ((DatePicker) lookup("#endDatePicker").query()).setValue(LocalDate.of(2024, 12, 31));
        });

        clickOn("#registerProjectButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("Todos los campos son obligatorios.");
    }
}