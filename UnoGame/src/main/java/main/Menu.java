package main;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Menu extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        showMainMenu();
        
        primaryStage.setTitle("UNO Game");
        primaryStage.setWidth(600);
        primaryStage.setHeight(500);
        primaryStage.show();
    }

    private void showMainMenu() {
        // Main container
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #2F4F4F;"); // Dark teal background

        // Title
        Label titleLabel = new Label("UNO GAME");
        titleLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

        // Start Game Button
        Button startButton = new Button("START GAME");
        styleButton(startButton, "#FF6B6B");
        startButton.setPrefWidth(200);
        startButton.setPrefHeight(60);
        startButton.setOnAction(e -> startGame());

        // Quit Button
        Button quitButton = new Button("QUIT GAME");
        styleButton(quitButton, "#4ECDC4");
        quitButton.setPrefWidth(200);
        quitButton.setPrefHeight(60);
        quitButton.setOnAction(e -> quitGame());

        // Button container
        VBox buttonBox = new VBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(startButton, quitButton);

        root.getChildren().addAll(titleLabel, buttonBox);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }

    private void startGame() {
        // Launch UnoUI
        UnoUI gameUI = new UnoUI();
        try {
            Stage gameStage = new Stage();
            gameUI.start(gameStage);
            primaryStage.hide(); // Hide menu while game is running
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void quitGame() {
        System.exit(0);
    }

    private void styleButton(Button button, String color) {
        button.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white; " +
            "-fx-background-color: " + color + "; " +
            "-fx-border-radius: 10px; " +
            "-fx-background-radius: 10px; " +
            "-fx-padding: 10px 20px; " +
            "-fx-cursor: hand;"
        );

        // Hover effect
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white; " +
            "-fx-background-color: " + darkenColor(color) + "; " +
            "-fx-border-radius: 10px; " +
            "-fx-background-radius: 10px; " +
            "-fx-padding: 10px 20px; " +
            "-fx-cursor: hand;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white; " +
            "-fx-background-color: " + color + "; " +
            "-fx-border-radius: 10px; " +
            "-fx-background-radius: 10px; " +
            "-fx-padding: 10px 20px; " +
            "-fx-cursor: hand;"
        ));
    }

    private String darkenColor(String color) {
        // Simple darkening by replacing the color with a darker version
        return switch(color) {
            case "#FF6B6B" -> "#CC5555";
            case "#4ECDC4" -> "#3BA89D";
            default -> color;
        };
    }

    public static void main(String[] args) {
        launch(args);
    }
}
