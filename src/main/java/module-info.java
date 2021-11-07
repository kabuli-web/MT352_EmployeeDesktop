module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires gson;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires httpcore;
    requires httpclient;
    opens org.example to javafx.fxml;
    exports org.example;
}