package com.example.unogameprojectoop.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML
    private Slider brightnessSlider;

    @FXML
    private Pane darkOverlay;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        double currentBrightness = GameConfig.getBrightness();

        brightnessSlider.setValue(currentBrightness);
        updateScreenBrightness(currentBrightness);
        brightnessSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double val = newValue.doubleValue();

            GameConfig.setBrightness(val);
            updateScreenBrightness(val);
        });
    }

    private void updateScreenBrightness(double brightness) {
        double overlayOpacity = 1.0 - brightness;// 1.0 max 0.0 min do sang
        darkOverlay.setOpacity(overlayOpacity);
    }

    @FXML
    protected void onBackClick(ActionEvent event) throws IOException {
        switchScene(event, "Menu.fxml");
    }

    private void switchScene(ActionEvent event, String fxmlFile) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/unogameprojectoop/" + fxmlFile));
        Parent root = fxmlLoader.load();

        double currentWidth = stage.getScene().getWidth();
        double currentHeight = stage.getScene().getHeight();
        Scene scene = new Scene(root, currentWidth, currentHeight);
        stage.setScene(scene);
        stage.show();
    }
}