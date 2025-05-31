module proyecto.practicas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j;
    requires mysql.connector.j;

    requires google.api.client;


    requires com.google.api.client;
    requires com.google.api.client.auth;
    requires com.google.api.services.drive;
    requires com.google.api.client.extensions.jetty.auth;
    requires com.google.api.client.extensions.java6.auth;
    requires com.google.api.client.json.gson;

    requires kernel;
    requires io;

    opens logic.drive to com.google.api.client.json.gson;
    opens gui to javafx.fxml;
    opens logic.DTO to javafx.base;

    exports gui;
    exports data_access;
    exports logic.DTO;
    exports logic.drive;
}