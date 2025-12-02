package main;

import java.util.ArrayList;
import java.util.Scanner;

public class UnoLogic {
    private Deck deck;
    private ArrayList<Player> players;
    private UnoCard topCard; // Lá bài đang nằm trên cùng nọc
    private UnoCard.Mau currentMau; // Màu hiện tại hợp lệ (quan trọng khi topCard là Wild)
    
    private int currentPlayerIndex;
    private int direction = 1; // 1: Chiều kim đồng hồ, -1: Ngược chiều
    private boolean isGameOver = false;
    private Scanner scanner;

    public UnoLogic(String playerName, int botCount) {
        deck = new Deck();
        deck.shuffle();
        players = new ArrayList<>();
        scanner = new Scanner(System.in);

        // 1. Setup Người chơi & Bot
        players.add(new Player(playerName)); // Người chơi thật
        for (int i = 1; i <= botCount; i++) {
            players.add(new Bot("Bot " + i, 5)); // Bot thuật toán ngẫu nhiên (5)
        }

        // 2. Chia bài (7 lá mỗi người)
        for (Player p : players) {
            p.firstDraw(deck);
        }

        // 3. Lật lá bài đầu tiên
        do {
            topCard = deck.drawCard();
        } while (topCard.isWild()); // Luật nhà: Nếu bốc trúng Wild đầu game thì bốc lại cho dễ

        currentMau = topCard.getMau();
        currentPlayerIndex = 0;
        
        System.out.println("TRÒ CHƠI BẮT ĐẦU");
        System.out.println("Lá bài khởi đầu: " + topCard);
    }

    public void start() {
        while (!isGameOver) {
            Player currentPlayer = players.get(currentPlayerIndex);
            System.out.println("\n--------------------------------");
            System.out.println("Lá bài trên bàn: " + topCard + " (Màu đang chọn: " + currentMau + ")");
            System.out.println("Lượt của: " + currentPlayer.getName() + " (Còn " + currentPlayer.getHand().size() + " lá)");

            // Xử lý lượt chơi
            UnoCard playedCard = null;

            if (currentPlayer instanceof Bot) {
                playedCard = handleBotTurn((Bot) currentPlayer);
            } else {
                playedCard = handleHumanTurn(currentPlayer);
            }

            // Kiểm tra kết quả sau lượt đánh
            if (playedCard != null) {
                topCard = playedCard;
                
                // Nếu đánh Wild, cần set màu (đã xử lý trong handleTurn nhưng gán lại cho chắc chắn logic)
                if (!playedCard.isWild()) {
                    currentMau = playedCard.getMau();
                }

                // Kiểm tra thắng
                if (currentPlayer.getHand().isEmpty()) {
                    System.out.println("\n🎉 CHÚC MỪNG! " + currentPlayer.getName() + " ĐÃ CHIẾN THẮNG!");
                    isGameOver = true;
                    return;
                }
                
                // UNO shout
                if (currentPlayer.getHand().size() == 1) {
                    System.out.println(currentPlayer.getName() + " HÔ 'UNO'!");
                }

                // Xử lý hiệu ứng bài đặc biệt
                handleSpecialCardEffect(playedCard);
            } else {
                System.out.println(currentPlayer.getName() + " đã rút bài và bỏ lượt.");
            }

            // Chuyển sang người tiếp theo
            if (!isGameOver) {
                moveToNextPlayer();
            }
        }
    }

    // --- LOGIC NGƯỜI CHƠI ---
    private UnoCard handleHumanTurn(Player p) {
        System.out.println("Bài trên tay bạn:");
        // Hiển thị bài kèm index
        ArrayList<UnoCard> hand = p.getHand();
        p.sortHandByColors(); // Sắp xếp cho dễ nhìn
        for (int i = 0; i < hand.size(); i++) {
            System.out.print("[" + i + "]" + hand.get(i) + "  ");
        }
        System.out.println("\n[-1] Rút bài");

        while (true) {
            System.out.print("Chọn lá bài để đánh (nhập số): ");
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                continue;
            }

            // Rút bài
            if (choice == -1) {
                UnoCard drawn = deck.drawCard();
                if (drawn == null) {
                    System.out.println("Hết bài để rút! Trộn lại bài...");
                    deck = new Deck(); // Thực tế nên gom bài dưới bàn để trộn, đây là demo đơn giản
                    drawn = deck.drawCard();
                }
                System.out.println("Bạn rút được: " + drawn);
                p.draw(drawn);
                
                // Luật: Nếu rút được bài đánh được thì có quyền đánh luôn
                if (drawn.matches(topCard, currentMau)) {
                    System.out.print("Bạn có muốn đánh lá này luôn không? (y/n): ");
                    String ans = scanner.nextLine();
                    if (ans.equalsIgnoreCase("y")) {
                        // Gọi đệ quy logic đánh lá vừa rút (index là lá cuối cùng)
                        return playCardInternal(p, p.getHand().size() - 1);
                    }
                }
                return null; // Bỏ lượt
            }

            // Đánh bài
            if (choice >= 0 && choice < hand.size()) {
                UnoCard card = hand.get(choice);
                // Kiểm tra hợp lệ dựa trên topCard VÀ currentMau
                if (card.matches(topCard, currentMau)) {
                    return playCardInternal(p, choice);
                } else {
                    System.out.println("❌ Lá bài không hợp lệ! Phải cùng màu " + currentMau + " hoặc cùng số/loại.");
                }
            } else {
                System.out.println("Lựa chọn không tồn tại.");
            }
        }
    }

    // --- LOGIC BOT ---
    private UnoCard handleBotTurn(Bot bot) {
        // "Hack": Bot class cũ chỉ kiểm tra màu của lá bài được truyền vào (currentCard.getMau())
        // Nếu topCard là Wild, getMau() trả về WILD khiến Bot không biết đánh màu gì.
        // Ta tạm thời set màu của topCard thành currentMau để Bot hiểu.
        
        UnoCard.Mau originalColor = topCard.getMau();
        
        // Nếu là bài Wild hoặc bài Đen, gán màu hiện tại cho nó để Bot so sánh đúng
        if (topCard.isWild() || topCard.getMau() == UnoCard.Mau.WILD) {
             topCard.setMau(currentMau); 
        }

        UnoCard played = bot.playBotCard(topCard);

        // Trả lại màu gốc cho topCard sau khi Bot suy nghĩ xong
        if (originalColor == UnoCard.Mau.WILD) {
            topCard.setMau(originalColor);
        }

        if (played == null) {
            // Bot rút bài
            UnoCard drawn = deck.drawCard();
            if (drawn == null) {
                // Xử lý khi hết bài (đơn giản là tạo deck mới hoặc thông báo)
                 deck = new Deck(); 
                 deck.shuffle();
                 drawn = deck.drawCard();
            }
            bot.draw(drawn);
            System.out.println(bot.getName() + " rút 1 lá.");
            
            // Bot đánh luôn nếu được
            // Khi check bài rút được, cũng cần dùng currentMau
            if (drawn.matches(topCard, currentMau)) {
                 return playCardInternal(bot, bot.getHand().size() - 1);
            }
            return null;
        }
        return played;
    }

    // --- HÀM HỖ TRỢ ĐÁNH BÀI (CHUNG CHO BOT VÀ NGƯỜI) ---
    private UnoCard playCardInternal(Player p, int index) {
        UnoCard card = p.getHand().get(index);
        p.getHand().remove(index);
        System.out.println("➡️ " + p.getName() + " đánh: " + card);

        // Nếu là Wild, phải chọn màu
        if (card.isWild()) {
            if (p instanceof Bot) {
                // Bot chọn màu (Random hoặc theo màu nhiều nhất)
                currentMau = chooseColorForBot((Bot) p);
                System.out.println(p.getName() + " chọn màu: " + currentMau);
            } else {
                // Người chọn màu
                currentMau = chooseColorHuman();
            }
        }
        return card;
    }

    // --- XỬ LÝ HIỆU ỨNG ĐẶC BIỆT ---
    private void handleSpecialCardEffect(UnoCard card) {
        switch (card.getLoai()) {
            case SKIP:
                System.out.println("🚫 Mất lượt!");
                moveToNextPlayer(); // Nhảy cóc 1 người
                break;
            case REVERSE:
                System.out.println("🔄 Đảo chiều!");
                direction *= -1;
                // Nếu chỉ có 2 người chơi, Reverse hoạt động như Skip
                if (players.size() == 2) {
                    moveToNextPlayer();
                }
                break;
            case DRAW_TWO:
                System.out.println("💥 +2 Bài!");
                int victimIndex = getNextPlayerIndex();
                Player victim = players.get(victimIndex);
                victim.draw(deck.drawCard());
                victim.draw(deck.drawCard());
                System.out.println(victim.getName() + " bị rút 2 lá và mất lượt.");
                moveToNextPlayer(); // Nạn nhân mất lượt
                break;
            case WILD_DRAW_FOUR:
                System.out.println("🔥🔥 +4 Bài & Chọn màu!");
                int victimIndex4 = getNextPlayerIndex();
                Player victim4 = players.get(victimIndex4);
                for(int i=0; i<4; i++) victim4.draw(deck.drawCard());
                System.out.println(victim4.getName() + " bị rút 4 lá và mất lượt.");
                moveToNextPlayer(); // Nạn nhân mất lượt
                break;
            default:
                break;
        }
    }

    // --- CÁC HÀM TIỆN ÍCH ---
    private void moveToNextPlayer() {
        currentPlayerIndex = getNextPlayerIndex();
    }

    private int getNextPlayerIndex() {
        int next = currentPlayerIndex + direction;
        if (next >= players.size()) return 0;
        if (next < 0) return players.size() - 1;
        return next;
    }

    private UnoCard.Mau chooseColorHuman() {
        System.out.println("Chọn màu mới: 1.Đỏ  2.Xanh Lá  3.Xanh Dương  4.Vàng");
        while (true) {
            String input = scanner.nextLine();
            switch (input) {
                case "1": return UnoCard.Mau.RED;
                case "2": return UnoCard.Mau.GREEN;
                case "3": return UnoCard.Mau.BLUE;
                case "4": return UnoCard.Mau.YELLOW;
                default: System.out.print("Chọn lại (1-4): ");
            }
        }
    }

    private UnoCard.Mau chooseColorForBot(Bot bot) {
        // Logic đơn giản: Chọn màu ngẫu nhiên (hoặc bạn có thể nâng cấp để chọn màu bot có nhiều nhất)
        UnoCard.Mau[] colors = {UnoCard.Mau.RED, UnoCard.Mau.GREEN, UnoCard.Mau.BLUE, UnoCard.Mau.YELLOW};
        return colors[(int)(Math.random() * 4)];
    }
}