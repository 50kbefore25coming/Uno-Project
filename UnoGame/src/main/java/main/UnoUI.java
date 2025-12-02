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

import java.util.ArrayList;
import java.util.Optional;

public class UnoUI extends Application {

    // --- LOGIC GAME ---
    private Deck deck;
    private ArrayList<Player> players;
    private UnoCard topCard;
    private UnoCard.Mau currentMau;
    private int currentPlayerIndex = 0;
    private int direction = 1;
    private boolean isGameOver = false;

    // --- UI COMPONENTS ---
    private BorderPane root;
    private Label topCardLabel;      // Lá bài giữa bàn
    private Label statusLabel;       // Thông báo trạng thái
    private Label deckLabel;         // Hình ảnh bộ bài rút
    private Button drawButton;       // Nút rút bài
    private Label currentColorLabel; // Màu hiện tại

    // Các Container chứa bài của 4 người
    private HBox bottomBox; // Người chơi (Human)
    private VBox leftBox;   // Bot 1
    private HBox topBox;    // Bot 2
    private VBox rightBox;  // Bot 3

    @Override
    public void start(Stage primaryStage) {
        // 1. Khởi tạo Logic
        initGameLogic();

        // 2. Khởi tạo Layout chính
        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #2F4F4F;"); // Màu xanh rêu bàn chơi

        // --- KHU VỰC GIỮA (CENTER): Bàn chơi ---
        VBox centerArea = new VBox(20);
        centerArea.setAlignment(Pos.CENTER);
        
        // Hàng chứa: Bộ bài rút [Deck] -- [Top Card]
        HBox pilesBox = new HBox(30);
        pilesBox.setAlignment(Pos.CENTER);

        // Hình ảnh bộ bài rút (mặt sau)
        deckLabel = new Label("UNO\nDeck");
        deckLabel.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-font-weight: bold; "
                + "-fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; "
                + "-fx-min-width: 80px; -fx-min-height: 120px; -fx-alignment: center;");
        // Sự kiện: Bấm vào bộ bài để rút
        deckLabel.setOnMouseClicked(e -> handleHumanDraw());

        // Lá bài trên cùng (Top Card)
        topCardLabel = new Label();
        styleCardLabel(topCardLabel, topCard);

        pilesBox.getChildren().addAll(deckLabel, topCardLabel);

        currentColorLabel = new Label("Màu hiện tại: " + currentMau);
        currentColorLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        statusLabel = new Label("Lượt của bạn!");
        statusLabel.setStyle("-fx-text-fill: yellow; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Nút rút bài (phụ trợ, có thể bấm vào deckLabel cũng được)
        drawButton = new Button("RÚT BÀI");
        drawButton.setStyle("-fx-font-size: 14px; -fx-background-color: #ff9900; -fx-text-fill: white;");
        drawButton.setOnAction(e -> handleHumanDraw());

        centerArea.getChildren().addAll(currentColorLabel, pilesBox, statusLabel, drawButton);
        root.setCenter(centerArea);

        // --- KHU VỰC NGƯỜI CHƠI (BOTTOM, LEFT, TOP, RIGHT) ---
        bottomBox = new HBox(5); bottomBox.setAlignment(Pos.CENTER); bottomBox.setPadding(new Insets(10));
        leftBox = new VBox(5);   leftBox.setAlignment(Pos.CENTER_LEFT); leftBox.setPadding(new Insets(10));
        topBox = new HBox(5);    topBox.setAlignment(Pos.CENTER); topBox.setPadding(new Insets(10));
        rightBox = new VBox(5);  rightBox.setAlignment(Pos.CENTER_RIGHT); rightBox.setPadding(new Insets(10));

        // Gán vào BorderPane
        root.setBottom(bottomBox);
        root.setLeft(leftBox);
        root.setTop(topBox);
        root.setRight(rightBox);

        // Vẽ bài lần đầu
        renderAllHands();

        // 3. Hiển thị
        Scene scene = new Scene(root, 1000, 700); // Mở rộng cửa sổ chút
        primaryStage.setTitle("UNO JavaFX - Full Table Layout");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- LOGIC GAME & UI HELPER ---

    private void initGameLogic() {
        deck = new Deck();
        deck.shuffle();
        players = new ArrayList<>();
        players.add(new Player("Bạn"));      // Index 0: Bottom
        players.add(new Bot("Bot 1", 2));    // Index 1: Left
        players.add(new Bot("Bot 2", 3));    // Index 2: Top
        players.add(new Bot("Bot 3", 4));    // Index 3: Right

        for (Player p : players) p.firstDraw(deck);

        do {
            topCard = deck.drawCard();
        } while (topCard.isWild());
        currentMau = topCard.getMau();
    }

    // Hàm quan trọng: Vẽ bài cho cả 4 vị trí
    private void renderAllHands() {
        // 1. Human (Bottom) - Hiện mặt bài
        bottomBox.getChildren().clear();
        Player human = players.get(0);
        human.sortHandByColors();
        
        // Label tên người chơi
        Label humanName = new Label(human.getName() + " (" + human.getHand().size() + ")");
        humanName.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        // Vì BottomBox là HBox chứa bài, ta nên bọc tên và bài vào VBox riêng để căn chỉnh đẹp hơn
        // Nhưng để đơn giản, ta hiển thị bài thôi. (Hoặc bạn có thể nâng cấp thêm)
        
        for (int i = 0; i < human.getHand().size(); i++) {
            int index = i;
            UnoCard card = human.getHand().get(i);
            Button cardBtn = new Button();
            Label cardVisual = new Label();
            styleCardLabel(cardVisual, card);
            cardBtn.setGraphic(cardVisual);
            cardBtn.setStyle("-fx-background-color: transparent;"); // Xóa viền button
            
            // Hiệu ứng hover cho đẹp
            cardBtn.setOnMouseEntered(e -> cardBtn.setTranslateY(-10));
            cardBtn.setOnMouseExited(e -> cardBtn.setTranslateY(0));
            
            cardBtn.setOnAction(e -> handleHumanPlay(index));
            bottomBox.getChildren().add(cardBtn);
        }

        // 2. Bot 1 (Left) - Bot 2 (Top) - Bot 3 (Right) : Hiện mặt lưng
        renderBotHand(players.get(1), leftBox, true);  // True = Dọc
        renderBotHand(players.get(2), topBox, false);  // False = Ngang
        renderBotHand(players.get(3), rightBox, true); // True = Dọc
    }

    // Hàm render bài cho Bot (Chỉ hiện lưng bài)
    private void renderBotHand(Player bot, Pane container, boolean isVertical) {
        container.getChildren().clear();
        
        // Tên Bot
        Label nameLbl = new Label(bot.getName() + "\n(" + bot.getHand().size() + ")");
        nameLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: center;");
        container.getChildren().add(nameLbl);

        // Vẽ các lá bài úp
        for (UnoCard c : bot.getHand()) {
            Label cardBack = createCardBack(isVertical);
            container.getChildren().add(cardBack);
        }
    }

    // Tạo hình ảnh mặt lưng lá bài
    private Label createCardBack(boolean isVertical) {
        Label lbl = new Label("UNO");
        // Kích thước nhỏ hơn bài thật một chút để tiết kiệm chỗ
        double w = isVertical ? 60 : 40; 
        double h = isVertical ? 40 : 60; 
        
        // Nếu là Bot Top (ngang) thì bài đứng (40x60), nếu Bot bên cạnh (dọc) thì bài nằm ngang (60x40) hoặc tùy ý
        // Chuẩn: Bài luôn đứng, chỉ là cách xếp là HBox hay VBox.
        // Hãy làm bài nhỏ (Thumbnail)
        lbl.setPrefSize(40, 60); 
        lbl.setStyle("-fx-background-color: #000; -fx-text-fill: #e74c3c; -fx-font-weight: bold; "
                + "-fx-border-color: white; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px; "
                + "-fx-alignment: center; -fx-font-size: 10px;");
        return lbl;
    }

    // Style cho lá bài ngửa (của người chơi & trên bàn)
    private void styleCardLabel(Label lbl, UnoCard card) {
        String colorHex = switch (card.getMau()) {
            case RED -> "#FF5555"; case GREEN -> "#55AA55";
            case BLUE -> "#5555FF"; case YELLOW -> "#FFAA00";
            case WILD -> "#222222";
        };
        // Xử lý hiển thị màu Wild khi đã chọn
        if (card.isWild() && card == topCard && currentMau != UnoCard.Mau.WILD) {
             colorHex = switch (currentMau) {
                case RED -> "#FF5555"; case GREEN -> "#55AA55";
                case BLUE -> "#5555FF"; case YELLOW -> "#FFAA00";
                default -> "#222222";
             };
        }

        lbl.setText(card.getDisplayValue());
        // Thêm ký hiệu nhỏ ở góc để dễ nhìn nếu mù màu
        String symbol = switch(card.getLoai()){
            case SKIP -> "Ø"; case REVERSE -> "⇄"; case DRAW_TWO -> "+2"; case WILD_DRAW_FOUR -> "+4"; default -> "";
        };
        if(!symbol.isEmpty()) lbl.setText(symbol + "\n" + card.getDisplayValue());
        
        lbl.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-weight: bold; "
                + "-fx-font-size: 18px; -fx-padding: 10px; "
                + "-fx-border-radius: 8px; -fx-background-radius: 8px; "
                + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 2, 2);"
                + "-fx-min-width: 80px; -fx-min-height: 120px; -fx-alignment: center;");
    }

    // --- XỬ LÝ SỰ KIỆN (EVENT HANDLERS) ---

    private void handleHumanPlay(int index) {
        if (currentPlayerIndex != 0) return;
        Player human = players.get(0);
        UnoCard card = human.getHand().get(index);

        if (card.matches(topCard, currentMau)) {
            human.getHand().remove(index);
            topCard = card;
            
            if (card.isWild()) {
                currentMau = showColorChooser();
            } else {
                currentMau = card.getMau();
            }
            statusLabel.setText("Bạn đánh: " + card.getFullName());
            checkWin(human);
            handleSpecialCard(card);
            endTurn();
        } else {
            flashMessage("Bài không hợp lệ!");
        }
    }

    private void handleHumanDraw() {
        if (currentPlayerIndex != 0) return;
        Player human = players.get(0);
        UnoCard drawn = deck.drawCard();
        if(drawn == null) { deck = new Deck(); deck.shuffle(); drawn = deck.drawCard(); }
        
        human.draw(drawn);
        statusLabel.setText("Bạn rút: " + drawn.getFullName());
        
        // Nếu rút được bài đánh được, tự động đánh luôn (hoặc hỏi - ở đây làm auto cho nhanh)
        if (drawn.matches(topCard, currentMau)) {
            // Optional: Auto play newly drawn card
        }
        endTurn();
    }

    private void endTurn() {
        // Cập nhật giao diện
        styleCardLabel(topCardLabel, topCard);
        currentColorLabel.setText("Màu hiện tại: " + currentMau);
        
        // Quan trọng: Vẽ lại bài các Bot (số lượng bài thay đổi)
        renderAllHands(); 

        if (isGameOver) return;

        // Tính người tiếp theo
        currentPlayerIndex = (currentPlayerIndex + direction + players.size()) % players.size();
        
        // Cập nhật trạng thái ai đang chơi
        highlightCurrentPlayer();

        if (currentPlayerIndex != 0) {
            // Lượt Bot
            bottomBox.setDisable(true);
            drawButton.setDisable(true);
            deckLabel.setDisable(true);
            
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(e -> runBotTurn());
            pause.play();
        } else {
            // Lượt Người
            bottomBox.setDisable(false);
            drawButton.setDisable(false);
            deckLabel.setDisable(false);
            statusLabel.setText("Đến lượt bạn!");
        }
    }

    private void runBotTurn() {
        if (isGameOver) return;
        Bot bot = (Bot) players.get(currentPlayerIndex);
        
        // Logic Bot (Hack màu như cũ)
        UnoCard.Mau originalColor = topCard.getMau();
        if (topCard.isWild() || topCard.getMau() == UnoCard.Mau.WILD) topCard.setMau(currentMau);

        UnoCard played = bot.playBotCard(topCard);

        if (originalColor == UnoCard.Mau.WILD) topCard.setMau(originalColor);

        if (played != null) {
            topCard = played;
            if (played.isWild()) {
                currentMau = UnoCard.Mau.values()[(int)(Math.random()*4)]; // Bot chọn màu ngẫu nhiên
            } else {
                currentMau = played.getMau();
            }
            statusLabel.setText(bot.getName() + " đánh " + played.getFullName());
            checkWin(bot);
            handleSpecialCard(played);
        } else {
            UnoCard drawn = deck.drawCard();
            if(drawn == null) { deck = new Deck(); deck.shuffle(); drawn = deck.drawCard(); }
            bot.draw(drawn);
            statusLabel.setText(bot.getName() + " rút bài.");
        }
        
        if (!isGameOver) endTurn();
    }
    
    private void highlightCurrentPlayer() {
        // Reset style
        bottomBox.setStyle(""); leftBox.setStyle(""); topBox.setStyle(""); rightBox.setStyle("");
        String activeStyle = "-fx-border-color: yellow; -fx-border-width: 3px; -fx-border-radius: 10px; -fx-padding: 10px;";
        
        switch(currentPlayerIndex) {
            case 0: bottomBox.setStyle(activeStyle); break;
            case 1: leftBox.setStyle(activeStyle); break;
            case 2: topBox.setStyle(activeStyle); break;
            case 3: rightBox.setStyle(activeStyle); break;
        }
    }

    private void handleSpecialCard(UnoCard card) {
        if (card.getLoai() == UnoCard.Loai.SKIP) {
            currentPlayerIndex = (currentPlayerIndex + direction + players.size()) % players.size();
        } else if (card.getLoai() == UnoCard.Loai.REVERSE) {
            direction *= -1;
            if (players.size() == 2) currentPlayerIndex = (currentPlayerIndex + direction + players.size()) % players.size();
        } else if (card.getLoai() == UnoCard.Loai.DRAW_TWO) {
            int next = (currentPlayerIndex + direction + players.size()) % players.size();
            players.get(next).draw(deck.drawCard());
            players.get(next).draw(deck.drawCard());
            currentPlayerIndex = next; 
        } else if (card.getLoai() == UnoCard.Loai.WILD_DRAW_FOUR) {
            int next = (currentPlayerIndex + direction + players.size()) % players.size();
            for(int i=0; i<4; i++) players.get(next).draw(deck.drawCard());
            currentPlayerIndex = next;
        }
    }

    private void checkWin(Player p) {
        if (p.getHand().isEmpty()) {
            isGameOver = true;
            statusLabel.setText("CHIẾN THẮNG: " + p.getName());
            
            // Show EndGameUI
            Stage gameStage = (Stage) root.getScene().getWindow();
            EndGameUI endGameUI = new EndGameUI(gameStage, p.getName());
            
            // Delay to allow current state to be seen before showing end screen
            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(e -> endGameUI.show());
            delay.play();
        }
    }

    private void flashMessage(String msg) {
        String old = statusLabel.getText();
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 20px; -fx-font-weight: bold;");
        PauseTransition p = new PauseTransition(Duration.seconds(1));
        p.setOnFinished(e -> {
            statusLabel.setText(old);
            statusLabel.setStyle("-fx-text-fill: yellow; -fx-font-size: 18px; -fx-font-weight: bold;");
        });
        p.play();
    }

    private UnoCard.Mau showColorChooser() {
        ChoiceDialog<UnoCard.Mau> dialog = new ChoiceDialog<>(UnoCard.Mau.RED, 
                UnoCard.Mau.RED, UnoCard.Mau.GREEN, UnoCard.Mau.BLUE, UnoCard.Mau.YELLOW);
        dialog.setTitle("Chọn màu");
        dialog.setHeaderText("WILD CARD!");
        dialog.setContentText("Chọn màu tiếp theo:");
        Optional<UnoCard.Mau> result = dialog.showAndWait();
        return result.orElse(UnoCard.Mau.RED);
    }

    public static void main(String[] args) {
        launch(args);
    }
}