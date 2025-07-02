package user_interface.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import logic.DTO.StudentDTO;
import logic.services.StudentService;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import gui.GUI_MenuStudentController;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MenuStudentControllerTest extends ApplicationTest {

    private FXMLLoader loader;
    private StudentDTO testStudent;
    private StudentService studentService;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_MenuStudent.fxml"));
        Parent root = loader.load();
        GUI_MenuStudentController controller = loader.getController();

        testStudent = createTestStudent();
        studentService = new StudentService();

        controller.setStudent(testStudent);
        controller.setStudentService(studentService);

        Scene scene = new Scene(root);
        stage.setTitle("Menú Estudiante");
        stage.setScene(scene);
        stage.show();
    }

    private StudentDTO createTestStudent() {
        StudentDTO student = new StudentDTO();
        student.setNames("Ana");
        student.setSurnames("García");
        student.setPhone("1234567890");
        student.setEmail("ana.garcia@test.com");
        return student;
    }

    @BeforeEach
    void setUp() {
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void testRegisterActivityScheduleButtonExists() {
        Button button = lookup("#registerActivityScheduleButton").query();
        assertThat(button).isNotNull();
        assertThat(button.isDisable()).isFalse();
    }

    @Test
    public void testLinkActivityToScheduleButtonExists() {
        Button button = lookup("#linkActivityToScheduleButton").query();
        assertThat(button).isNotNull();
        assertThat(button.isDisable()).isFalse();
    }

    @Test
    public void testLogoutButtonExists() {
        Button button = lookup("#logoutButton").query();
        assertThat(button).isNotNull();
        assertThat(button.isDisable()).isFalse();
    }

    @Test
    public void testOpenRegisterActivityScheduleWindow() {
        Button button = lookup("#registerActivityScheduleButton").query();
        clickOn(button);
        WaitForAsyncUtils.waitForFxEvents();

        Stage newStage = listTargetWindows().stream()
                .filter(window -> window instanceof Stage && ((Stage) window).getTitle().equals("Registrar Evidencia y Cronograma"))
                .map(window -> (Stage) window)
                .findFirst()
                .orElse(null);

        assertThat(newStage).isNotNull();
    }

    @Test
    public void testOpenLinkActivityToScheduleWindow() {
        Button button = lookup("#linkActivityToScheduleButton").query();
        clickOn(button);
        WaitForAsyncUtils.waitForFxEvents();

        Stage newStage = listTargetWindows().stream()
                .filter(window -> window instanceof Stage && ((Stage) window).getTitle().equals("Vincular Actividad a Cronograma"))
                .map(window -> (Stage) window)
                .findFirst()
                .orElse(null);

        assertThat(newStage).isNotNull();
    }
}