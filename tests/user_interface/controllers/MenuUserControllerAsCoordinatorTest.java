package user_interface.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import logic.DTO.Role;
import gui.GUI_MenuUserController;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MenuUserControllerAsCoordinatorTest extends ApplicationTest {

    private FXMLLoader loader;
    private GUI_MenuUserController controller;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_MenuUser.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        controller.setUserName("Test User");
        controller.setActualUserId(1);
        controller.setUserRole(Role.COORDINATOR);

        Scene scene = new Scene(root);
        stage.setTitle("Menú Usuario");
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    void setUp() {
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void testOpenViewStudentListWindow() {
        clickOn("#viewStudentListButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(getStageByTitle("Lista de Estudiantes")).isNotNull();
    }

    @Test
    public void testOpenViewAcademicListWindow() {
        clickOn("#viewAcademicListButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(getStageByTitle("Lista de Académicos")).isNotNull();
    }

    @Test
    public void testOpenViewOrganizationListWindow() {
        clickOn("#viewOrganizationListButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(getStageByTitle("Lista de Organizaciones")).isNotNull();
    }

    @Test
    public void testOpenViewRepresentativeListWindow() {
        clickOn("#viewRepresentativeListButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(getStageByTitle("Lista de Representantes")).isNotNull();
    }

    @Test
    public void testOpenViewProjectListWindow() {
        clickOn("#viewProjectListButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(getStageByTitle("Lista de Proyectos")).isNotNull();
    }

    @Test
    public void testOpenViewProjectRequestWindow() {
        clickOn("#viewProjectRequestButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(getStageByTitle("Lista de Solicitudes de Prácticas")).isNotNull();
    }

    @Test
    public void testOpenViewPeriodListWindow() {
        clickOn("#viewPeriodListButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(getStageByTitle("Lista de Períodos")).isNotNull();
    }

    @Test
    public void testOpenViewGroupListWindow() {
        clickOn("#viewGroupListButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(getStageByTitle("Lista de Grupos")).isNotNull();
    }

    @Test
    public void testOpenManageAssessmentCriteriaWindow() {
        clickOn("#manageAssessmentCriteriaButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(getStageByTitle("Gestión de Criterios de Evaluación")).isNotNull();
    }

    @Test
    public void testOpenManageSelfAssessmentCriteriaWindow() {
        clickOn("#manageSelfAssessmentCriteriaButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(getStageByTitle("Gestión de Criterios de Autoevaluación")).isNotNull();
    }

    @Test
    public void testOpenManageActivityWindow() {
        clickOn("#manageActivityButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(getStageByTitle("Gestión de Actividades")).isNotNull();
    }

    // Utilidad para buscar ventana por título
    private Stage getStageByTitle(String title) {
        return listTargetWindows().stream()
                .filter(window -> window instanceof Stage && ((Stage) window).getTitle().equals(title))
                .map(window -> (Stage) window)
                .findFirst()
                .orElse(null);
    }
}