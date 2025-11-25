package main;

import java.util.ArrayList;
import java.util.Scanner;

public class UnoProject {

    private final ArrayList<Player> players = new ArrayList<>();
    private final Deck deck = new Deck();
    private UnoCard currentCard;     // lá đang ở trên bàn
    private int currentPlayerIndex = 0;
    private int direction = 1;       // 1 = xuôi, -1 = ngược
    private Scanner scanner = new Scanner(System.in);

    public UnoProject(int soNguoiChoi) {
        // Tạo người chơi
        for (int i = 1; i <= soNguoiChoi; i++) {
            players.add(new Player("Người chơi " + i));
        }

        deck.shuffle();

        // Mỗi người rút 7 lá
        for (Player p : players) {
            for (int i = 0; i < 7; i++) {
                p.drawCard(deck.drawCard());
            }
        }

        // Lật lá đầu tiên
        currentCard = deck.drawCard();
        System.out.println("Lá đầu tiên: " + currentCard);
    }

    // Lấy người chơi hiện tại
    private Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    // Chuyển lượt (tự động tính theo direction)
    private void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + direction + players.size()) % players.size();
    }

    // Chọn màu khi chơi WILD
    private UnoCard.Mau chooseColor() {
        System.out.println("Chọn màu: 1=RED  2=GREEN  3=BLUE  4=YELLOW");
        int c = scanner.nextInt();

        return switch (c) {
            case 1 -> UnoCard.Mau.BLUE;
            case 2 -> UnoCard.Mau.GREEN;
            case 3 -> UnoCard.Mau.RED;
            case 4 -> UnoCard.Mau.WILD;
            default -> UnoCard.Mau.YELLOW;
        };
    }

    // Chạy game
    public void start() {
        while (true) {
            Player player = getCurrentPlayer();
            System.out.println("\n===== Lượt của " + player.getName() + " =====");
            System.out.println("Lá hiện tại: " + currentCard);
            player.printHand();

            // Lấy các lá chơi được
            ArrayList<UnoCard> playable = player.getPlayableCards(currentCard);

            if (playable.isEmpty()) {
                System.out.println("Không có lá phù hợp → Rút một lá.");
                UnoCard drawn = deck.drawCard();
                player.drawCard(drawn);
                System.out.println("Bốc được: " + drawn);

                // Nếu chơi được ngay sau khi rút
                if (drawn.matches(currentCard)) {
                    System.out.println("→ Bạn có thể đánh lá vừa rút! Đánh luôn.");
                    playCardEffect(player, drawn);
                } else {
                    nextPlayer();
                }

                continue;
            }

            // Cho chọn lá
            System.out.println("Chọn lá để đánh (0 để rút bài):");
            for (int i = 0; i < playable.size(); i++) {
                System.out.println((i + 1) + ". " + playable.get(i));
            }

            int choice = scanner.nextInt();

            if (choice == 0) {
                UnoCard drawn = deck.drawCard();
                player.drawCard(drawn);
                System.out.println("Bốc được: " + drawn);
                nextPlayer();
                continue;
            }

            UnoCard chosen = playable.get(choice - 1);
            playCardEffect(player, chosen);

            // Kiểm tra thắng
            if (player.hasWon()) {
                System.out.println("\n🎉 " + player.getName() + " đã thắng game! 🎉");
                break;
            }
        }
    }

    // Xử lý hiệu ứng lá bài
        private void playCardEffect(Player player, UnoCard card) {
            System.out.println(player.getName() + " đánh: " + card);
            player.playCard(card);
            currentCard = card;

            // Xử lý lá đặc biệt
            switch (card.getLoai()) {

                case REVERSE:
                    direction *= -1;
                    System.out.println("➡️ Đổi chiều!");
                    nextPlayer();
                    break;

                case SKIP:
                    System.out.println("⛔ Bỏ lượt người kế tiếp.");
                    nextPlayer(); // bỏ người kế
                    nextPlayer(); // đến người kế tiếp của người bị skip
                    break;

                case DRAW_TWO:
                    nextPlayer();
                    Player target = getCurrentPlayer();
                    System.out.println("➕ " + target.getName() + " phải rút 2 lá!");
                    target.drawCard(deck.drawCard());
                    target.drawCard(deck.drawCard());
                    nextPlayer();
                    break;

                case WILD:
                    UnoCard.Color newColor = chooseColor();
                    card.setColor(newColor);
                    System.out.println("🎨 Chọn màu: " + newColor);
                    nextPlayer();
                    break;

                case WILD_DRAW_FOUR:
                    UnoCard.Mau newColor2 = chooseColor();
                    card.setColor(newColor2);
                    nextPlayer();
                    Player target2 = getCurrentPlayer();
                    System.out.println("🔥 " + target2.getName() + " bốc 4 lá!");
                    for (int i = 0; i < 4; i++) target2.drawCard(deck.drawCard());
                    nextPlayer();
                    break;

                default:
                    nextPlayer();
                    break;
            }
        }
      public static void main(String[] args) {
        UnoProject game = new UnoProject(2); // 2 người chơi
        game.start();
    }
}
