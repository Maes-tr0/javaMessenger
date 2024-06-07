module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires com.example.shared;
    requires java.logging;

    opens com.example.client to javafx.fxml;
    opens com.example.client.controller to javafx.fxml;

    exports com.example.client;
    exports com.example.client.controller;
}
