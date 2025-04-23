module proyecto.practicas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j;

    opens gui to javafx.fxml;
    opens logic.DTO to javafx.base;

    exports gui;
    exports data_access;
    exports logic.DTO;
}