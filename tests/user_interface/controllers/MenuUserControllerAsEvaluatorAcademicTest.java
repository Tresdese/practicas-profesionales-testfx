package user_interface.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import logic.DTO.Role;
import gui.GUI_MenuUserController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MenuUserControllerAsEvaluatorAcademicTest extends ApplicationTest {

    private FXMLLoader loader;
    private GUI_MenuUserController controller;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_MenuUser.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        controller.setUserName("Test Evaluator Academic");
        controller.setActualUserId(3);
        controller.setUserRole(Role.EVALUATOR_ACADEMIC);

        Scene scene = new Scene(root);
        stage.setTitle("Menú Usuario");
        stage.setScene(scene);
        stage.show();
    }

    private Stage getStageByTitle(String title) {
        return listTargetWindows().stream()
                .filter(window -> window instanceof Stage && ((Stage) window).getTitle().equals(title))
                .map(window -> (Stage) window)
                .findFirst()
                .orElse(null);
    }

    @BeforeEach
    void setUp() {
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void testEvaluatePresentationButtonExists() {
        Button button = lookup("#evaluatePresentationButton").query();
        assertThat(button).isNotNull();
    }

    @Test
    public void testLogoutButtonExists() {
        Button button = lookup("#logoutButton").query();
        assertThat(button).isNotNull();
        Assertions.assertThat(button.isVisible()).isTrue();
        Assertions.assertThat(button.isDisable()).isFalse();
    }

    @Test
    public void testOpenEvaluatePresentationWindowIfEnabled() {
        Button button = lookup("#evaluatePresentationButton").query();
        if (button.isVisible() && !button.isDisable()) {
            clickOn(button);
            WaitForAsyncUtils.waitForFxEvents();
            assertThat(getStageByTitle("Calificar Presentación")).isNotNull();
        }
    }
}