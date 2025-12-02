package com.example.unogameprojectoop.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    // --- KHAI BÁO UI ---
    @FXML private HBox playerHandBox;   // Tay bài người chơi
    @FXML private StackPane discardPile;// Cọc bài đánh
    @FXML private Pane gameOverlay;     // Màn đen chỉnh độ sáng

    // Khai báo khu vực của Bot (Mới thêm)
    @FXML private VBox botLeftHand;
    @FXML private VBox botRightHand;
    @FXML private HBox botTopHand;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        applyBrightness();
        setupHandAnimation();
        discardPile.getChildren().add(createCardCSS("YELLOW", "9"));
    }

    private void applyBrightness() {
        double brightness = GameConfig.getBrightness();
        if (gameOverlay != null) {
            gameOverlay.setOpacity(1.0 - brightness);
        }
    }

    @FXML
    protected void onBackToMenu(javafx.event.ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/unogameprojectoop/Menu.fxml"));
        Parent root = fxmlLoader.load();

        double width = stage.getScene().getWidth();
        double height = stage.getScene().getHeight();

        Scene scene = new Scene(root, width, height);
        stage.setScene(scene);
        stage.show();
    }

    //  HÀM VẼ LÁ BÀI NGỬA
    private StackPane createCardCSS(String color, String value) {
        StackPane card = new StackPane();
        card.setPrefSize(80, 120);

        String hexColor = switch (color) {
            case "RED" -> "#ff5555";
            case "BLUE" -> "#5555ff";
            case "GREEN" -> "#55aa55";
            case "YELLOW" -> "#ffaa00";
            default -> "#1c1c1c";
        };

        String displayText = value;
        switch (value) {
            case "Skip" -> displayText = "Ø";
            case "Reverse" -> displayText = "⇄";
            case "Draw2" -> displayText = "+2";
            case "W" -> displayText = "W";
            case "+4" -> displayText = "+4";
        }

        Rectangle bg = new Rectangle(80, 120);
        bg.setArcWidth(15); bg.setArcHeight(15);

        javafx.scene.shape.Ellipse oval = new javafx.scene.shape.Ellipse(35, 50);

        Label text = new Label(displayText);

        if (color.equals("WILD")) {
            bg.setStyle("-fx-fill: #1c1c1c; -fx-stroke: white; -fx-stroke-width: 3; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0, 2, 2);");
            oval.setStyle("-fx-fill: linear-gradient(to bottom right, #ff5555, #5555ff, #55aa55, #ffaa00); -fx-rotate: -15;");
            text.setStyle("-fx-text-fill: white; -fx-font-family: 'Coiny'; -fx-font-size: 35px; -fx-font-weight: bold; -fx-effect: dropshadow(three-pass-box, black, 2, 0, 0, 0);");
        } else {
            bg.setStyle("-fx-fill: " + hexColor + "; -fx-stroke: white; -fx-stroke-width: 3; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);");
            oval.setStyle("-fx-fill: white; -fx-rotate: -15;");
            text.setStyle("-fx-text-fill: " + hexColor + "; -fx-font-family: 'Coiny'; -fx-font-size: 30px; -fx-font-weight: bold;");
        }

        card.getChildren().addAll(bg, oval, text);

        // Hover Bay lên
        card.setOnMouseEntered(e -> {
            card.setTranslateY(-30);
            card.setViewOrder(-100); // Đè lên tất cả
        });
        card.setOnMouseExited(e -> {
            card.setTranslateY(0);
            card.setViewOrder(0);
        });

        return card;
    }

    //HÀM VẼ MẶT LƯNG LÁ BÀI
    private StackPane createCardBack() {
        StackPane card = new StackPane();
        card.setPrefSize(80, 120);

        // Nền đen
        Rectangle bg = new Rectangle(80, 120);
        bg.setArcWidth(15); bg.setArcHeight(15);
        bg.setStyle("-fx-fill: #1c1c1c; -fx-stroke: white; -fx-stroke-width: 3; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 2, 2);");

        // Bầu dục đỏ
        javafx.scene.shape.Ellipse oval = new javafx.scene.shape.Ellipse(35, 50);
        oval.setStyle("-fx-fill: #ff5555; -fx-rotate: -25;");

        // Chữ UNO vàng
        Label text = new Label("UNO");
        text.setStyle("-fx-text-fill: #ffaa00; -fx-font-family: 'Coiny'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-effect: dropshadow(three-pass-box, black, 1, 0, 1, 1);");
        text.setRotate(-25);

        card.getChildren().addAll(bg, oval, text);
        return card;
    }

    // HÀM HIỆU ỨNG XÒE BÀI
    private void setupHandAnimation() {
        // Mặc định xếp chặt (-40)
        playerHandBox.setSpacing(-40);

        // Khi chuột vào -> Xòe ra (-15)
        playerHandBox.setOnMouseEntered(e -> playerHandBox.setSpacing(-15));

        // Khi chuột ra -> Thu lại (-40)
        playerHandBox.setOnMouseExited(e -> playerHandBox.setSpacing(-40));
    }
    }


