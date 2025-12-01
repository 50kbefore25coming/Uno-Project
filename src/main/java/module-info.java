module com.example.unogameprojectoop {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.unogameprojectoop to javafx.fxml;
    exports com.example.unogameprojectoop;
    opens com.example.unogameprojectoop.controller to javafx.fxml;
    exports com.example.unogameprojectoop.controller;
}