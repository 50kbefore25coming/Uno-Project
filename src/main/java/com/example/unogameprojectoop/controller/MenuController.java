package com.example.unogameprojectoop.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML private Button btnStart;
    @FXML private Button btnSettings;
    @FXML private Button btnExit;
    @FXML private Pane menuOverlay;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        double savedBrightness = GameConfig.getBrightness();
        double opacity = 1.0 - savedBrightness;
        if (menuOverlay != null) {
            menuOverlay.setOpacity(opacity);
        }
    }
    @FXML
    protected void onStartGameClick(ActionEvent event) throws IOException {
        System.out.println("Vào màn hình chơi game!");
        switchScene(event, "Game.fxml");    }

    @FXML
    protected void onSettingsClick(ActionEvent event) throws IOException {
        switchScene(event, "Settings.fxml");
    }

    @FXML
    protected void onExitClick() {
        System.exit(0);
    }

    private void switchScene(ActionEvent event, String fxmlFile) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/unogameprojectoop/" + fxmlFile));
        Parent root = fxmlLoader.load();

        double width = stage.getScene().getWidth();
        double height = stage.getScene().getHeight();

        Scene scene = new Scene(root, width, height);
        stage.setScene(scene);
        stage.show();
    }
}