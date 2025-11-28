public class Main {
    public static void main(String[] args) {
        System.out.println("Chào mừng đến với UNO Console Game!");
        System.out.println("Bạn sẽ chơi với 3 Bot.");

        // Tạo game với 1 người chơi tên "Bạn" và 3 Bot
        UnoLogic game = new UnoLogic("Trần Hán Nam", 3);

        // Bắt đầu vòng lặp
        game.start();
    }
}
// chơi thử trên conslove
