import java.util.ArrayList;

public class Deck {
    //list card
    private final ArrayList<UnoCard> cards;
    
    public Deck() {
        this.cards = new ArrayList<>();
        Spawn();
    }

    public void Spawn() {
        // List
        UnoCard.Mau[] danhSachMau ={
            UnoCard.Mau.RED,
            UnoCard.Mau.GREEN,
            UnoCard.Mau.YELLOW,
            UnoCard.Mau.BLUE,
        };
        // Sinh bai
        for (UnoCard.Mau mauHienTai : danhSachMau) {
            cards.add(new UnoCard(mauHienTai, 0));

            for (int so = 1; so <= 9; so++) {
                cards.add(new UnoCard(mauHienTai, so));
                cards.add(new UnoCard(mauHienTai, so));
            }
            cards.add(new UnoCard(mauHienTai, UnoCard.Loai.SKIP));
            cards.add(new UnoCard(mauHienTai, UnoCard.Loai.SKIP));

            cards.add(new UnoCard(mauHienTai, UnoCard.Loai.REVERSE));
            cards.add(new UnoCard(mauHienTai, UnoCard.Loai.REVERSE));

            cards.add(new UnoCard(mauHienTai, UnoCard.Loai.DRAW_TWO));
            cards.add(new UnoCard(mauHienTai, UnoCard.Loai.DRAW_TWO));
        }
        // Sinh bai dac biet
        for (int i = 0; i <= 3; i++) {
            cards.add(new UnoCard(UnoCard.Mau.WILD, UnoCard.Loai.WILD));
            cards.add(new UnoCard(UnoCard.Mau.WILD, UnoCard.Loai.WILD_DRAW_FOUR));
        }
    }
    // Xao bai
    public void shuffle() {
        java.util.Collections.shuffle(this.cards);
    }
    // Rut bai
    public UnoCard drawCard() {
        if (cards.isEmpty()) return null;
        return cards.remove(cards.size() - 1);
    }
}
