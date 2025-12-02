/**
 * UnoCard.java - PHẦN 1: Lớp UnoCard & Enum
 * Người phụ trách: [Tên bạn]
 * Mô tả: Quản lý thông tin và logic cơ bản của một lá bài UNO
 */
package main;

public class UnoCard {

    public enum Mau { 
        RED, GREEN, BLUE, YELLOW, WILD;
        
        @Override
        public String toString() {
            return switch (this) {
                case RED -> "ĐỎ";
                case GREEN -> "XANH LÁ";
                case BLUE -> "XANH DƯƠNG";
                case YELLOW -> "VÀNG";
                case WILD -> "ĐẶC BIỆT";
            };
        }
    }

    public enum Loai {
        NUMBER,     // có trường số (0-9)
        SKIP,       // skip - mất lượt
        REVERSE,    // reverse - đảo chiều
        DRAW_TWO,   // +2 - rút 2 bài
        WILD,       // wild - chọn màu
        WILD_DRAW_FOUR; // wild +4 - chọn màu + rút 4 bài
        
        @Override
        public String toString() {
            return switch (this) {
                case NUMBER -> "Số";
                case SKIP -> "Mất lượt";
                case REVERSE -> "Đảo chiều";
                case DRAW_TWO -> "Rút 2";
                case WILD -> "Đổi màu";
                case WILD_DRAW_FOUR -> "Đổi màu Rút 4";
            };
        }
    }

    // --- trường dữ liệu ---
    private Mau mau;
    private Loai loai;
    private int so; // chỉ hợp lệ khi loai == NUMBER, ngược lại = -1
    private int diem; // điểm số của lá bài

    // --- constructor ---
    // dùng cho số
    public UnoCard(Mau mau, int so) {
        if (mau == Mau.WILD) throw new IllegalArgumentException("Wild không có số");
        if (so < 0 || so > 9) throw new IllegalArgumentException("Số phải trong 0..9");
        this.mau = mau;
        this.loai = Loai.NUMBER;
        this.so = so;
        this.diem = so;
    }

    // dùng cho các loại không phải số
    public UnoCard(Mau mau, Loai loai) {
        if (loai == Loai.NUMBER) throw new IllegalArgumentException("Số phải được cung cấp cho NUMBER");
        if (mau == Mau.WILD && !(loai == Loai.WILD || loai == Loai.WILD_DRAW_FOUR))
            throw new IllegalArgumentException("Chỉ WILD và WILD_DRAW_FOUR có màu WILD");
        this.mau = mau;
        this.loai = loai;
        this.so = -1;
        this.diem = switch (loai) {
            case SKIP, REVERSE, DRAW_TWO -> 20;
            case WILD, WILD_DRAW_FOUR -> 50;
            default -> 0;
        };
    }

    // getter
    public Mau getMau() { return mau; }
    public Loai getLoai() { return loai; }
    public int getSo() { return so; }
    public int getDiem() { return diem; }
    // setter
    public void setMau(Mau mau) { this.mau = mau; }
    // tiện ích
    public boolean isWild() { return loai == Loai.WILD || loai == Loai.WILD_DRAW_FOUR; }
    public boolean isNumber() { return loai == Loai.NUMBER; }
    public boolean isActionCard() { return !isNumber() && !isWild(); }

    /**
     * Kiểm tra xem lá này có thể đánh lên lá topCard hay không.
     */
    public boolean matches(UnoCard topCard, Mau mauHienTai) {
        if (this.isWild()) return true;

        Mau mauSoSanh = topCard.getMau();
        if (topCard.isWild() && mauHienTai != null) {
            mauSoSanh = mauHienTai;
        }

        if (this.mau == mauSoSanh) return true;
        if (this.isNumber() && topCard.isNumber() && this.so == topCard.so) return true;
        if (this.isActionCard() && topCard.isActionCard() && this.loai == topCard.loai) return true;

        return false;
    }

    public String getDisplayValue() {
        if (loai == Loai.NUMBER) {
            return String.valueOf(so);
        } else {
            return switch (loai) {
                case SKIP -> "Ø";
                case REVERSE -> "↺";
                case DRAW_TWO -> "+2";
                case WILD -> "W";
                case WILD_DRAW_FOUR -> "+4";
                default -> "";
            };
        }
    }

    public String getFullName() {
        if (loai == Loai.NUMBER) {
            return mau.toString() + " " + so;
        } else {
            return mau.toString() + " " + loai.toString();
        }
    }

    @Override
    public String toString() {
        return getFullName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnoCard)) return false;
        UnoCard c = (UnoCard) o;
        return this.mau == c.mau && this.loai == c.loai && this.so == c.so;
    }

    @Override
    public int hashCode() {
        int result = mau.hashCode();
        result = 31 * result + loai.hashCode();
        result = 31 * result + so;
        return result;
    }

    // TEST đơn giản
    public static void main(String[] args) {
        System.out.println("=== TEST UNOCARD ===");
        
        UnoCard red5 = new UnoCard(Mau.RED, 5);
        UnoCard blue5 = new UnoCard(Mau.BLUE, 5);
        UnoCard redSkip = new UnoCard(Mau.RED, Loai.SKIP);
        
        System.out.println("red5: " + red5);
        System.out.println("blue5: " + blue5);
        System.out.println("redSkip: " + redSkip);
        
        System.out.println("red5 matches blue5? " + red5.matches(blue5, null));
        System.out.println("red5 matches redSkip? " + red5.matches(redSkip, null));
        
        System.out.println("✅ UNOCARD TEST THANH CONG!");
    }
}