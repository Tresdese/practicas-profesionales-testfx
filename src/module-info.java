module proyecto.practicas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j;

    opens gui to javafx.fxml;
    exports gui;
    exports data_access;
}