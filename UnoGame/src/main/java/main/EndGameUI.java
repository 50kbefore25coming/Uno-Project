package main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class EndGameUI {

    private Stage gameStage;
    private String winnerName;

    public EndGameUI(Stage gameStage, String winnerName) {
        this.gameStage = gameStage;
        this.winnerName = winnerName;
    }

    public void show() {
        // Main container
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #2F4F4F;"); // Dark teal background

        // Winner Title
        Label winnerLabel = new Label("🎉 GAME OVER 🎉");
        winnerLabel.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

        // Winner Name
        Label nameLabel = new Label("WINNER: " + winnerName);
        nameLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #00FF00;");

        // Separator
        Separator separator = new Separator();

        // Buttons container
        VBox buttonBox = new VBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        // Play Again Button
        Button playAgainButton = new Button("PLAY AGAIN");
        styleButton(playAgainButton, "#FF6B6B");
        playAgainButton.setPrefWidth(200);
        playAgainButton.setPrefHeight(50);
        playAgainButton.setOnAction(e -> playAgain());

        // Back to Menu Button
        Button menuButton = new Button("BACK TO MENU");
        styleButton(menuButton, "#4ECDC4");
        menuButton.setPrefWidth(200);
        menuButton.setPrefHeight(50);
        menuButton.setOnAction(e -> backToMenu());

        // Quit Button
        Button quitButton = new Button("QUIT GAME");
        styleButton(quitButton, "#95A5A6");
        quitButton.setPrefWidth(200);
        quitButton.setPrefHeight(50);
        quitButton.setOnAction(e -> quitGame());

        buttonBox.getChildren().addAll(playAgainButton, menuButton, quitButton);

        root.getChildren().addAll(winnerLabel, nameLabel, separator, buttonBox);

        Scene scene = new Scene(root, 600, 500);
        gameStage.setScene(scene);
        gameStage.setTitle("UNO - Game Over");
        gameStage.show();
    }

    private void playAgain() {
        // Start a new game
        UnoUI newGame = new UnoUI();
        try {
            Stage newGameStage = new Stage();
            newGame.start(newGameStage);
            gameStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void backToMenu() {
        // Show menu
        Menu menu = new Menu();
        try {
            Stage menuStage = new Stage();
            menu.start(menuStage);
            gameStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void quitGame() {
        gameStage.close();
        System.exit(0);
    }

    private void styleButton(Button button, String color) {
        button.setStyle(
            "-fx-font-size: 18px; " +
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
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white; " +
            "-fx-background-color: " + darkenColor(color) + "; " +
            "-fx-border-radius: 10px; " +
            "-fx-background-radius: 10px; " +
            "-fx-padding: 10px 20px; " +
            "-fx-cursor: hand;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
            "-fx-font-size: 18px; " +
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
        return switch(color) {
            case "#FF6B6B" -> "#CC5555";
            case "#4ECDC4" -> "#3BA89D";
            case "#95A5A6" -> "#7F8C8D";
            default -> color;
        };
    }
}
