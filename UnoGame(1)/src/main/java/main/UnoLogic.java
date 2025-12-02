package main;

import java.util.ArrayList;
import java.util.Random;

public class UnoLogic {
    // --- DỮ LIỆU GAME ---
    private Deck deck;
    private ArrayList<Player> players;
    private UnoCard topCard;
    private UnoCard.Mau currentMau;

    // Biến trạng thái
    private int currentPlayerIndex = 0;
    private int direction = 1; // 1: Xuôi (A->B->C), -1: Ngược (C->B->A)
    private boolean isGameOver = false;
    private String gameMessage = "Bắt đầu game!";

    public UnoLogic() {
        resetGame();
    }

    public void resetGame() {
        deck = new Deck();
        deck.shuffle();
        players = new ArrayList<>();

        // Tạo 1 Người + 3 Bot
        players.add(new Player("Bạn"));
        players.add(new Bot("Bot 1", 2));
        players.add(new Bot("Bot 2", 3));
        players.add(new Bot("Bot 3", 4));

        // Chia 7 lá mỗi người
        for (Player p : players) p.firstDraw(deck);

        // Lật lá bài đầu (Đảm bảo không phải Wild)
        do {
            topCard = deck.drawCard();
        } while (topCard.isWild());
        
        currentMau = topCard.getMau();
        currentPlayerIndex = 0;
        direction = 1;
        isGameOver = false;
        gameMessage = "Lượt của bạn!";
    }

    // --- CÁC HÀM GETTER CHO UI ---
    public UnoCard getTopCard() { return topCard; }
    public UnoCard.Mau getCurrentMau() { return currentMau; }
    public ArrayList<Player> getPlayers() { return players; }
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }
    public String getGameMessage() { return gameMessage; }
    public boolean isGameOver() { return isGameOver; }
    public Player getCurrentPlayer() { return players.get(currentPlayerIndex); }

    /**
     * Người chơi đánh bài
     */
    public boolean playCard(int cardIndex, UnoCard.Mau selectedColor) {
        Player p = players.get(currentPlayerIndex);
        UnoCard card = p.getHand().get(cardIndex);

        // 1. Kiểm tra luật cơ bản (Màu hoặc Số/Loại)
        if (!card.matches(topCard, currentMau)) {
            gameMessage = "Bài không hợp lệ! Phải cùng màu " + currentMau + " hoặc cùng số.";
            return false;
        }

        // 2. Đánh bài xuống bàn
        p.getHand().remove(cardIndex);
        topCard = card;

        // Cập nhật màu
        if (card.isWild()) {
            currentMau = selectedColor;
            card.setMau(selectedColor); // Cập nhật màu cho lá Wild
        } else {
            currentMau = card.getMau();
        }

        gameMessage = p.getName() + " đánh " + card.getFullName();

        // 3. Kiểm tra thắng
        if (p.getHand().isEmpty()) {
            isGameOver = true;
            gameMessage = p.getName() + " CHIẾN THẮNG!";
            return true;
        }

        // 4. Xử lý hiệu ứng và chuyển lượt
        handleCardEffectAndNextTurn(card);
        
        return true;
    }

    /**
     * Người chơi rút bài
     */
    public boolean drawCardAction() {
        Player p = players.get(currentPlayerIndex);
        
        UnoCard drawn = deck.drawCard();
        if (drawn == null) {
            refillDeck();
            drawn = deck.drawCard();
        }
        
        p.draw(drawn);
        gameMessage = p.getName() + " rút 1 lá.";
        
        // Luật đơn giản: Rút xong là mất lượt
        moveToNextPlayer();
        return true;
    }

    // --- LOGIC BOT ---
    public void playBotTurn() {
        if (isGameOver) return;
        Bot bot = (Bot) players.get(currentPlayerIndex);

        

        // --- BƯỚC 2: ĐỂ BOT TỰ TÍNH TOÁN ---
        // Gọi hàm thông minh trong Bot.java
        UnoCard cardToPlay = bot.playBotCard(topCard);


        // --- BƯỚC 3: XỬ LÝ KẾT QUẢ ---
        if (cardToPlay != null) {
            // A. NẾU BOT ĐÁNH BÀI
            topCard = cardToPlay;
            currentMau = cardToPlay.getMau();
            gameMessage = bot.getName() + " đánh " + cardToPlay.getFullName();
            // Kiểm tra thắng
            if (bot.getHand().isEmpty()) {
                isGameOver = true;
                gameMessage = bot.getName() + " THẮNG!";
                return;
            }

            // Xử lý hiệu ứng (Skip, Reverse, +2...)
            handleCardEffectAndNextTurn(cardToPlay);

        } else {
            // B. NẾU BOT KHÔNG CÓ BÀI -> RÚT BÀI
            // Gọi hàm rút bài (drawActionLogic hoặc drawCardAction tùy phiên bản bạn đang dùng)
            // Ở đây mình dùng drawActionLogic (bản nâng cấp rút đến chết/nhận phạt)
            drawCardAction(); 
            
            // Nếu bạn dùng bản cũ (rút 1 lá) thì đổi thành: drawCardAction();
            // gameMessage = bot.getName() + " rút bài.";
        }
    }

    // --- XỬ LÝ HIỆU ỨNG & CHUYỂN LƯỢT ---
    private void handleCardEffectAndNextTurn(UnoCard card) {
        // Mặc định là chuyển sang người kế tiếp (step = 1)
        int step = 1;

        switch (card.getLoai()) {
            case REVERSE:
                direction *= -1;
                gameMessage += " (Đảo chiều)";
                // Nếu chỉ có 2 người, Reverse hoạt động như Skip
                if (players.size() == 2) step = 2;
                break;

            case SKIP:
                gameMessage += " (Mất lượt người kế!)";
                step = 2; // Nhảy cóc qua người kế
                break;

            case DRAW_TWO:
                gameMessage += " (+2 cho người kế!)";
                applyPenaltyToNext(2);
                step = 2; // Người bị phạt mất lượt -> Nhảy cóc
                break;

            case WILD_DRAW_FOUR:
                gameMessage += " (+4 cho người kế!)";
                applyPenaltyToNext(4);
                step = 2; // Người bị phạt mất lượt -> Nhảy cóc
                break;
                
            default:
                break;
        }

        // Thực hiện di chuyển index
        moveIndex(step);
    }

    // Hàm phạt người kế tiếp (nhưng chưa chuyển lượt)
    private void applyPenaltyToNext(int amount) {
        int victimIndex = getNextPlayerIndex(1); // Lấy index người kế
        Player victim = players.get(victimIndex);
        
        for (int i = 0; i < amount; i++) {
            UnoCard c = deck.drawCard();
            if (c == null) { refillDeck(); c = deck.drawCard(); }
            victim.draw(c);
        }
    }

    // Hàm chuyển index an toàn (xử lý vòng tròn)
    private void moveIndex(int step) {
        // direction có thể là 1 hoặc -1. step là số bước nhảy.
        // next = current + (step * direction)
        currentPlayerIndex += (step * direction);

        // Xử lý wrap-around (vòng lặp mảng)
        // Dùng while để xử lý trường hợp step lớn hoặc direction âm
        while (currentPlayerIndex < 0) {
            currentPlayerIndex += players.size();
        }
        while (currentPlayerIndex >= players.size()) {
            currentPlayerIndex -= players.size();
        }
    }

    private void moveToNextPlayer() {
        moveIndex(1);
    }
    
    // Helper lấy index người kế tiếp (để phạt) mà không thay đổi current
    private int getNextPlayerIndex(int step) {
        int idx = currentPlayerIndex + (step * direction);
        while (idx < 0) idx += players.size();
        while (idx >= players.size()) idx -= players.size();
        return idx;
    }

    private void refillDeck() {
        deck = new Deck();
        deck.shuffle();
    }
}