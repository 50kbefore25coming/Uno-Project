package main;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.Optional;

public class UnoUI extends Application {

    private UnoLogic logic;
    private BorderPane root;
    private Stage primaryStage;
    
    // UI Elements
    private Label topCardLabel, statusLabel, deckLabel, currentColorLabel;
    private Button drawButton;
    
    // Player Containers
    private HBox bottomBox; // Human
    private VBox leftBox;   // Bot 1
    private HBox topBox;    // Bot 2
    private VBox rightBox;  // Bot 3

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        logic = new UnoLogic(); 

        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #006633;"); // Màu xanh lá đậm UNO

        // --- CENTER AREA ---
        VBox centerArea = new VBox(15);
        centerArea.setAlignment(Pos.CENTER);

        HBox pilesBox = new HBox(30);
        pilesBox.setAlignment(Pos.CENTER);

        deckLabel = new Label("UNO\nDeck");
        styleDeck(deckLabel);
        deckLabel.setOnMouseClicked(e -> handleHumanDraw());

        topCardLabel = new Label();
        styleCardLabel(topCardLabel, logic.getTopCard());

        pilesBox.getChildren().addAll(deckLabel, topCardLabel);

        currentColorLabel = new Label();
        currentColorLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        statusLabel = new Label("Chào mừng!");
        statusLabel.setStyle("-fx-text-fill: yellow; -fx-font-size: 18px; -fx-font-weight: bold;");

        drawButton = new Button("RÚT BÀI");
        drawButton.setStyle("-fx-font-size: 14px; -fx-background-color: #ff9900; -fx-text-fill: white;");
        drawButton.setOnAction(e -> handleHumanDraw());

        centerArea.getChildren().addAll(currentColorLabel, pilesBox, statusLabel, drawButton);
        root.setCenter(centerArea);

        // --- PLAYERS AREAS ---
        bottomBox = createBox(Pos.CENTER);
        leftBox = createVBox(Pos.CENTER_LEFT);
        topBox = createBox(Pos.CENTER);
        rightBox = createVBox(Pos.CENTER_RIGHT);

        root.setBottom(bottomBox);
        root.setLeft(leftBox);
        root.setTop(topBox);
        root.setRight(rightBox);

        updateUI(); // Vẽ lần đầu

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("UNO Game - Standard Rules");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- XỬ LÝ SỰ KIỆN ---

    private void handleHumanPlay(int index) {
        if (logic.getCurrentPlayerIndex() != 0) return;

        UnoCard card = logic.getCurrentPlayer().getHand().get(index);
        UnoCard.Mau selectedColor = null;

        if (card.isWild()) {
            selectedColor = showColorChooser();
            if (selectedColor == null) return;
        }

        boolean success = logic.playCard(index, selectedColor);

        if (success) {
            updateUI();
            checkGameLoop();
        } else {
            flashMessage(logic.getGameMessage());
        }
    }

    private void handleHumanDraw() {
        if (logic.getCurrentPlayerIndex() != 0) return;

        logic.drawCardAction();
        updateUI();
        checkGameLoop();
    }

    // --- GAME LOOP ---
    private void checkGameLoop() {
        if (logic.isGameOver()) {
            showWinAlert(logic.getGameMessage());
            return;
        }

        // Nếu lượt tiếp theo không phải người chơi (index != 0) -> Bot chạy
        if (logic.getCurrentPlayerIndex() != 0) {
            setHumanControls(false);
            
            PauseTransition pause = new PauseTransition(Duration.seconds(1.2));
            pause.setOnFinished(e -> {
                logic.playBotTurn();
                updateUI();
                checkGameLoop();
            });
            pause.play();
        } else {
            setHumanControls(true);
            statusLabel.setText("Đến lượt BẠN!");
        }
    }

    // --- CẬP NHẬT GIAO DIỆN ---
    private void updateUI() {
        // 1. Thông tin bàn
        styleCardLabel(topCardLabel, logic.getTopCard());
        currentColorLabel.setText("Màu hiện tại: " + logic.getCurrentMau());
        statusLabel.setText(logic.getGameMessage());

        // 2. Vẽ bài người chơi
        bottomBox.getChildren().clear();
        Player human = logic.getPlayers().get(0);
        human.sortHandByColors();
        
        for (int i = 0; i < human.getHand().size(); i++) {
            int idx = i;
            UnoCard card = human.getHand().get(i);
            Button cardBtn = new Button();
            Label visual = new Label();
            styleCardLabel(visual, card);
            cardBtn.setGraphic(visual);
            cardBtn.setStyle("-fx-background-color: transparent;");
            
            // Hiệu ứng hover
            cardBtn.setOnMouseEntered(e -> cardBtn.setTranslateY(-20));
            cardBtn.setOnMouseExited(e -> cardBtn.setTranslateY(0));
            
            cardBtn.setOnAction(e -> handleHumanPlay(idx));
            bottomBox.getChildren().add(cardBtn);
        }

        // 3. Vẽ bài Bot
        renderBotHand(logic.getPlayers().get(1), leftBox, true);
        renderBotHand(logic.getPlayers().get(2), topBox, false);
        renderBotHand(logic.getPlayers().get(3), rightBox, true);
        
        // 4. Highlight lượt
        highlightActivePlayer();
    }

    // --- CÁC HÀM HỖ TRỢ HIỂN THỊ ---
    
    private void renderBotHand(Player bot, Pane container, boolean isVertical) {
        container.getChildren().clear();
        Label name = new Label(bot.getName() + "\n(" + bot.getHand().size() + ")");
        name.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-text-alignment: center;");
        container.getChildren().add(name);
        
        int displayCount = Math.min(bot.getHand().size(), 7); 
        for(int i=0; i<displayCount; i++) {
            Label back = new Label("UNO");
            back.setPrefSize(isVertical?50:35, isVertical?35:50);
            back.setStyle("-fx-background-color: black; -fx-text-fill: red; -fx-border-color: white; -fx-alignment: center; -fx-font-size: 9px;");
            container.getChildren().add(back);
        }
    }

    private void styleCardLabel(Label lbl, UnoCard card) {
        if(card == null) return;
        String color = switch(card.getMau()) {
            case RED -> "#FF5555"; case GREEN -> "#55AA55";
            case BLUE -> "#5555FF"; case YELLOW -> "#FFAA00";
            default -> "#333333";
        };
        // Hiển thị màu thực tế nếu là Wild
        if(card == logic.getTopCard() && card.isWild()) {
            color = switch(logic.getCurrentMau()) {
                case RED -> "#FF5555"; case GREEN -> "#55AA55";
                case BLUE -> "#5555FF"; case YELLOW -> "#FFAA00";
                default -> "#333333";
            };
        }
        
        lbl.setText(card.getDisplayValue());
        lbl.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; "
                   + "-fx-font-size: 18px; -fx-padding: 5px; -fx-border-radius: 8px; -fx-background-radius: 8px; "
                   + "-fx-min-width: 60px; -fx-min-height: 90px; -fx-alignment: center; "
                   + "-fx-border-color: white; -fx-border-width: 2px;");
    }

    private void styleDeck(Label lbl) {
        lbl.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-font-weight: bold; "
                   + "-fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 10px; "
                   + "-fx-min-width: 60px; -fx-min-height: 90px; -fx-alignment: center;");
    }

    private void highlightActivePlayer() {
        String activeStyle = "-fx-border-color: yellow; -fx-border-width: 3px; -fx-padding: 5px; -fx-border-style: solid;";
        bottomBox.setStyle(""); leftBox.setStyle(""); topBox.setStyle(""); rightBox.setStyle("");
        
        switch(logic.getCurrentPlayerIndex()) {
            case 0: bottomBox.setStyle(activeStyle); break;
            case 1: leftBox.setStyle(activeStyle); break;
            case 2: topBox.setStyle(activeStyle); break;
            case 3: rightBox.setStyle(activeStyle); break;
        }
    }

    private void setHumanControls(boolean active) {
        bottomBox.setDisable(!active);
        drawButton.setDisable(!active);
        deckLabel.setDisable(!active);
    }

    private UnoCard.Mau showColorChooser() {
        ChoiceDialog<UnoCard.Mau> d = new ChoiceDialog<>(UnoCard.Mau.RED, UnoCard.Mau.RED, UnoCard.Mau.GREEN, UnoCard.Mau.BLUE, UnoCard.Mau.YELLOW);
        d.setTitle("Chọn màu");
        d.setHeaderText("Bạn đánh lá Wild!");
        d.setContentText("Chọn màu tiếp theo:");
        Optional<UnoCard.Mau> res = d.showAndWait();
        return res.orElse(null);
    }
    
    private void showWinAlert(String msg) {
        primaryStage.hide();
        EndGameUI endGame = new EndGameUI(primaryStage, msg);
        endGame.show();
    }

    private void flashMessage(String msg) {
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-text-fill: #FF5555; -fx-font-size: 20px; -fx-font-weight: bold;");
        PauseTransition p = new PauseTransition(Duration.seconds(1.5));
        p.setOnFinished(e -> {
            statusLabel.setText(logic.getGameMessage());
            statusLabel.setStyle("-fx-text-fill: yellow; -fx-font-size: 18px; -fx-font-weight: bold;");
        });
        p.play();
    }

    private HBox createBox(Pos align) { HBox b = new HBox(5); b.setAlignment(align); b.setPadding(new Insets(5)); return b; }
    private VBox createVBox(Pos align) { VBox b = new VBox(5); b.setAlignment(align); b.setPadding(new Insets(5)); return b; }

    public static void main(String[] args) { launch(args); }
}