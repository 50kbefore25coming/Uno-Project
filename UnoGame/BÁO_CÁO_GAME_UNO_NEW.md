# BÁO CÁO DỰ ÁN GAME UNO

---

## MỤC LỤC

1. Lời Nói Đầu
2. Giới Thiệu Game
3. Triển Khai
4. Tổng Kết

---

## LỜI NÓI ĐẦU

Dự án Game UNO được xây dựng với mục đích tạo ra một trò chơi bài hoàn chỉnh bằng ngôn ngữ Java, kết hợp giao diện đồ họa JavaFX và hệ thống Bot AI thông minh. Đây là bài tập áp dụng các kiến thức lập trình hướng đối tượng (OOP) bao gồm kế thừa, đa hình, và quản lý trạng thái phức tạp.

Báo cáo này trình bày chi tiết về cấu trúc dự án, các công nghệ sử dụng, và giải thích các thành phần chính của hệ thống.

---

## 1. GIỚI THIỆU GAME

### 1.1 Tổng Quan

Game UNO là trò chơi bài nổi tiếng thế giới. Trong phiên bản này, người chơi cạnh tranh với 3 Bot AI để trở thành người hết bài trước tiên. Trò chơi hỗ trợ đầy đủ luật chuẩn của UNO cùng với giao diện đồ họa trực quan.

### 1.2 Luật Chơi Cơ Bản

**Mục tiêu trò chơi**: Là người đầu tiên hết hết bài trên tay.

**Cách đánh bài**: 
- Lá bài được đánh phải cùng màu hoặc cùng số/loại với lá bài trên cùng của bàn
- Nếu không có bài hợp lệ, người chơi bắt buộc rút 1 lá bài từ deck
- Khi chỉ còn 1 lá bài, người chơi phải nói "UNO"

**Phân loại các loại bài**:
- Bài số (0-9): Bài thường, không có hiệu ứng đặc biệt
- Bài Skip: Người tiếp theo mất lượt
- Bài Reverse: Đảo chiều chơi
- Bài Draw Two (+2): Người tiếp theo rút 2 lá bài và mất lượt
- Bài Wild: Người chơi chọn lại màu (không cần match)
- Bài Wild Draw Four (+4): Chọn lại màu và người tiếp theo rút 4 lá bài

**Cấu trúc deck**:
- Mỗi màu (Đỏ, Xanh Lá, Xanh Dương, Vàng) có 25 lá (1 số 0, 2 lá số 1-9, 2 Skip, 2 Reverse, 2 Draw Two)
- 8 lá Wild (4 Wild + 4 Wild Draw Four)
- Tổng cộng: 108 lá bài

### 1.3 Luồng Chơi

1. Game khởi động: Tạo 4 người chơi (1 người chơi thực + 3 Bot)
2. Mỗi người rút 7 lá bài ban đầu
3. Lật lá bài đầu tiên (nếu là Wild sẽ bốc lại)
4. Lần lượt mỗi người chơi:
   - Đánh bài hợp lệ hoặc rút bài
   - Nếu đánh bài đặc biệt, thực hiện hiệu ứng
   - Chuyển sang người tiếp theo
5. Game kết thúc khi một người hết bài, người đó là người chiến thắng

---

## 2. TRIỂN KHAI

### 2.1 Công Nghệ Sử Dụng

**Ngôn ngữ lập trình**: Java 17+
- Sử dụng các tính năng hiện đại của Java như Lambda, Stream, và Record types
- Tập trung vào lập trình hướng đối tượng với các nguyên tắc SOLID

**Framework giao diện đạo**: JavaFX 21
- Xây dựng giao diện người dùng đồ họa (GUI)
- Hỗ trợ layout linh hoạt với BorderPane, HBox, VBox
- Xử lý sự kiện người dùng (click, hover)
- Tạo hiệu ứng animation và transition

**Công cụ build**: Maven
- Quản lý dependencies (thư viện JavaFX)
- Biên dịch và đóng gói ứng dụng
- Tự động hóa quy trình xây dựng

**Công cụ phát triển**: NetBeans hoặc VS Code với Java Extension Pack

### 2.2 Cấu Trúc Thư Mục Dự Án

```
UnoGame/
├── pom.xml                              (Maven configuration)
├── src/
│   ├── main/java/main/
│   │   ├── Main.java                   (Điểm vào chương trình)
│   │   ├── UnoCard.java                (Lớp đại diện lá bài)
│   │   ├── Deck.java                   (Lớp quản lý bộ bài)
│   │   ├── Player.java                 (Lớp cơ sở cho người chơi)
│   │   ├── Bot.java                    (Lớp Bot AI, kế thừa Player)
│   │   ├── UnoUI.java                  (Giao diện game chính)
│   │   ├── Menu.java                   (Màn hình menu chính)
│   │   ├── EndGameUI.java              (Màn hình kết thúc)
│   │   └── UnoLogic.java               (Logic console version)
│   └── test/java/
├── target/                             (Thư mục build output)
└── BÁO_CÁO_GAME_UNO.md                 (Báo cáo này)
```

### 2.3 Giải Thích Các Lớp Chính

#### 2.3.1 Lớp UnoCard

**Mục đích**: Đại diện cho một lá bài UNO, lưu trữ thông tin về màu sắc, loại bài, và giá trị.

**Enum Mau (Màu)**:
```
- RED: Màu đỏ
- GREEN: Màu xanh lá
- BLUE: Màu xanh dương
- YELLOW: Màu vàng
- WILD: Màu đặc biệt cho Wild cards
```

**Enum Loai (Loại)**:
```
- NUMBER: Bài số (0-9)
- SKIP: Bài mất lượt
- REVERSE: Bài đảo chiều
- DRAW_TWO: Bài rút 2 lá
- WILD: Bài đổi màu
- WILD_DRAW_FOUR: Bài đổi màu rút 4 lá
```

**Các thuộc tính**:
- mau: Lưu loại màu của bài
- loai: Lưu loại bài
- so: Lưu số trên bài (chỉ dùng cho loại NUMBER)
- diem: Lưu giá trị điểm của bài

**Các phương thức quan trọng**:

| Phương thức | Mục đích |
|-------------|---------|
| matches(UnoCard topCard, Mau mauHienTai) | Kiểm tra xem lá bài này có thể đánh được lên topCard không. Trả về true nếu: bài là Wild, hoặc cùng màu, hoặc cùng số/loại |
| getDisplayValue() | Trả về ký hiệu hiển thị: "0"-"9" cho số, "Ø" cho Skip, "↺" cho Reverse, "+2" cho Draw Two, "W" cho Wild, "+4" cho Wild Draw Four |
| getFullName() | Trả về tên đầy đủ của bài (ví dụ: "ĐỎ 5", "XANH LÁ Skip") |
| isWild() | Kiểm tra bài có phải Wild card không |
| isNumber() | Kiểm tra bài có phải bài số không |

#### 2.3.2 Lớp Deck

**Mục đích**: Quản lý bộ bài UNO gồm 108 lá, cung cấp các hàm để rút bài và trộn bài.

**Các thuộc tính**:
- cards: ArrayList chứa tất cả lá bài trong deck

**Các phương thức**:

| Phương thức | Mục đích |
|-------------|---------|
| Deck() | Constructor khởi tạo và tạo ra 108 lá bài chuẩn UNO |
| Spawn() | Tạo ra tất cả 108 lá bài: 4 màu x 25 lá mỗi màu + 8 Wild cards |
| shuffle() | Trộn bộ bài sử dụng Collections.shuffle() để đảm bảo ngẫu nhiên |
| drawCard() | Rút 1 lá bài từ cuối danh sách, trả về lá bài đó, hoặc null nếu deck hết |

#### 2.3.3 Lớp Player

**Mục đích**: Đại diện cho một người chơi, lưu trữ thông tin người chơi và quản lý bài trên tay.

**Các thuộc tính**:
- name: Tên của người chơi
- hand: ArrayList<UnoCard> chứa các lá bài trên tay
- hasWon: Boolean cờ trạng thái thắng/thua

**Các phương thức**:

| Phương thức | Mục đích |
|-------------|---------|
| firstDraw(Deck deck) | Rút 7 lá bài ban đầu và thêm vào hand |
| draw(UnoCard card) | Thêm 1 lá bài vào hand |
| playCard(int index, UnoCard currentCard) | Lấy lá bài tại vị trí index ra khỏi hand, kiểm tra hợp lệ, cập nhật trạng thái, trả về lá bài đó |
| checkValidCard(int index, UnoCard currentCard) | Kiểm tra xem bài tại vị trí index có thể đánh được trên currentCard không |
| hasValidCard(UnoCard currentCard) | Kiểm tra xem có bất kỳ lá bài hợp lệ trong hand không |
| sortHandByColors() | Sắp xếp hand theo màu để dễ nhìn |
| sortHandByType() | Sắp xếp hand theo loại bài |

#### 2.3.4 Lớp Bot (kế thừa từ Player)

**Mục đích**: Triển khai AI cho Bot với 5 thuật toán khác nhau, cho phép Bot tự quyết định nước đi.

**Các thuộc tính bổ sung**:
- botAlgorithm: int từ 1 đến 5, xác định thuật toán nào sẽ sử dụng

**Năm thuật toán**:

1. **Algorithm 1 - Đơn Giản**: Tìm lá bài hợp lệ đầu tiên trong hand và đánh nó. Nhanh nhưng không thông minh.

2. **Algorithm 2 - Ngẫu Nhiên**: Tìm tất cả lá bài hợp lệ, sau đó chọn ngẫu nhiên 1 cái. Công bằng, không dễ đoán.

3. **Algorithm 3 - Ưu Tiên Hành Động**: Sắp xếp hand theo loại bài, ưu tiên đánh các bài đặc biệt (Skip, Reverse, Draw Two, Wild). Gây áp lực lên đối thủ.

4. **Algorithm 4 - Ưu Tiên Màu**: Đếm số lượng bài theo từng màu, ưu tiên đánh màu có bài nhiều nhất. Chiến lược dài hạn để tăng cơ hội có bài hợp lệ.

5. **Algorithm 5 - Kết Hợp**: Chọn ngẫu nhiên 1 trong 4 thuật toán trên để sử dụng. Khó đoán nhất.

**Các phương thức**:

| Phương thức | Mục đích |
|-------------|---------|
| playBotCard(UnoCard currentCard) | Kiểm tra xem Bot có bài hợp lệ không. Nếu có, gọi thuật toán tương ứng để chọn bài, sau đó playCard và trả về bài đó. Nếu không có, trả về null |
| BotAlgorithm1(UnoCard currentCard) | Lặp qua hand, trả về index của lá bài hợp lệ đầu tiên |
| BotAlgorithm2(UnoCard currentCard) | Tìm tất cả bài hợp lệ, random chọn 1 cái |
| BotAlgorithm3(UnoCard currentCard) | Sắp xếp hand, duyệt từ cuối (bài đặc biệt ở cuối), trả về index bài hợp lệ đầu tiên tìm thấy |
| BotAlgorithm4(UnoCard currentCard) | Đếm bài theo màu, chỉ xét các bài hợp lệ, ưu tiên màu nhiều nhất |
| BotAlgorithm5(UnoCard currentCard) | Random chọn thuật toán 1-4, gọi phương thức tương ứng |

#### 2.3.5 Lớp UnoUI

**Mục đích**: Tạo giao diện đồ họa game chính sử dụng JavaFX, hiển thị bàn chơi với 4 vị trí, xử lý sự kiện người chơi.

**Các thành phần giao diện**:
- Top Box: Hiển thị bài của Bot 2 (phía trên)
- Left Box: Hiển thị bài của Bot 1 (bên trái)
- Right Box: Hiển thị bài của Bot 3 (bên phải)
- Center Box: Hiển thị deck, top card, màu hiện tại, status, nút rút bài
- Bottom Box: Hiển thị bài của người chơi thực (phía dưới), có thể click để đánh

**Các thuộc tính**:
- deck: Đối tượng Deck quản lý bài
- players: ArrayList<Player> chứa 4 người chơi
- topCard: Lá bài nằm trên cùng bàn
- currentMau: Màu hiện tại hợp lệ
- currentPlayerIndex: Chỉ số người chơi hiện tại (0-3)
- direction: Hướng chơi (1 = kim đồng hồ, -1 = ngược)
- isGameOver: Cờ kết thúc game

**Các phương thức**:

| Phương thức | Mục đích |
|-------------|---------|
| start(Stage primaryStage) | Khởi tạo layout JavaFX, thêm các component, hiển thị cửa sổ game |
| initGameLogic() | Tạo Deck, tạo 4 Player, chia bài, lật bài đầu tiên |
| renderAllHands() | Vẽ lại bài của tất cả 4 người chơi trên giao diện |
| renderBotHand(Player bot, Pane container, boolean isVertical) | Vẽ bài của một Bot (chỉ hiển thị mặt lưng) vào container tương ứng |
| handleHumanPlay(int index) | Xử lý sự kiện người chơi click vào bài tại vị trí index. Kiểm tra hợp lệ, nếu hợp lệ thì đánh, cập nhật topCard, xử lý hiệu ứng, kết thúc lượt |
| handleHumanDraw() | Xử lý sự kiện người chơi click vào deck để rút bài. Rút 1 lá, thêm vào hand, kiểm tra match, kết thúc lượt |
| runBotTurn() | Thực hiện lượt của Bot hiện tại. Gọi playBotCard của Bot, nếu có bài thì đánh, nếu không thì rút. Xử lý hiệu ứng, kết thúc lượt |
| endTurn() | Cập nhật giao diện, tính người chơi tiếp theo, nếu là Bot thì tự động chơi sau delay 1.5s, nếu là người thì enable input |
| handleSpecialCard(UnoCard card) | Nếu bài là Skip/Reverse/Draw Two/Wild Draw Four thì thực hiện hiệu ứng tương ứng |
| checkWin(Player p) | Kiểm tra nếu hand của người chơi rỗng thì game over, hiển thị EndGameUI |
| highlightCurrentPlayer() | Đánh dấu người chơi hiện tại bằng border vàng |
| showColorChooser() | Mở dialog cho người chơi chọn màu khi đánh Wild |

#### 2.3.6 Lớp Menu

**Mục đích**: Tạo màn hình menu chính với các nút START GAME và QUIT GAME.

**Các phương thức**:

| Phương thức | Mục đích |
|-------------|---------|
| start(Stage stage) | Khởi tạo giao diện menu, hiển thị cửa sổ |
| showMainMenu() | Tạo layout menu với tiêu đề, nút START, nút QUIT |
| startGame() | Khi nhấn START, tạo đối tượng UnoUI mới, hiển thị window game |
| quitGame() | Khi nhấn QUIT, gọi System.exit(0) để thoát ứng dụng |
| styleButton(Button button, String color) | Thiết lập style cho button với màu đã cho, thêm hiệu ứng hover |
| darkenColor(String color) | Trả về phiên bản tối hơn của màu để dùng khi hover |

#### 2.3.7 Lớp EndGameUI

**Mục đích**: Tạo màn hình kết thúc game hiển thị người chiến thắng và các tùy chọn.

**Các thuộc tính**:
- gameStage: Stage của cửa sổ game
- winnerName: Tên của người chơi chiến thắng

**Các phương thức**:

| Phương thức | Mục đích |
|-------------|---------|
| show() | Tạo layout end game screen với tên người thắng, 3 nút (Play Again, Back to Menu, Quit) |
| playAgain() | Tạo game mới bằng cách khởi tạo UnoUI, đóng cửa sổ hiện tại |
| backToMenu() | Tạo Menu mới, đóng cửa sổ game |
| quitGame() | Đóng cửa sổ game, gọi System.exit(0) |
| styleButton(Button button, String color) | Thiết lập style button |
| darkenColor(String color) | Lấy phiên bản tối hơn của màu |

#### 2.3.8 Lớp Main

**Mục đích**: Điểm vào của chương trình.

**Phương thức**: 
- main(String[] args): Khởi động Menu.main(args) để bắt đầu ứng dụng

#### 2.3.9 Lớp UnoLogic

**Mục đích**: Phiên bản console/text của game, dùng để test logic mà không cần giao diện đồ họa.

**Các phương thức chính**: Tương tự UnoUI nhưng sử dụng Scanner để nhập từ console và println để in output thay vì GUI.

### 2.4 Luồng Thực Thi Game

**Khởi động**:
1. Main gọi Menu.main()
2. Menu hiển thị 2 nút: START GAME và QUIT GAME
3. Khi nhấn START GAME, khởi tạo UnoUI
4. UnoUI: initGameLogic() tạo deck, 4 players, chia 7 bài mỗi người, lật bài đầu
5. renderAllHands() vẽ bài lên giao diện

**Lượt chơi người chơi**:
1. Người chơi thực click vào bài hoặc click deck
2. handleHumanPlay() hoặc handleHumanDraw() xử lý
3. Nếu hợp lệ, cập nhật topCard, xử lý hiệu ứng nếu có
4. endTurn() tính người tiếp theo

**Lượt chơi Bot**:
1. endTurn() delay 1.5s
2. runBotTurn() gọi playBotCard() của Bot
3. Bot chọn bài theo thuật toán, đánh hoặc rút
4. Xử lý hiệu ứng, endTurn()

**Kết thúc**:
1. checkWin() phát hiện hand rỗng
2. Tạo EndGameUI với tên người thắng
3. Người chơi chọn: Play Again, Back to Menu, hoặc Quit

### 2.5 Cách Sử Dụng

**Yêu cầu hệ thống**:
- Java 17 trở lên
- Maven
- JavaFX 21

**Build và chạy**:
```
cd UnoGame
mvn clean package
mvn javafx:run
```

Hoặc chạy từ IDE: Chuột phải Main.java, chọn Run.

**Cách chơi**:
1. Menu: Bấm "START GAME"
2. Game: Click lá bài để đánh (phải match), hoặc click Deck để rút
3. Nếu đánh Wild, chọn màu từ dialog
4. Khi game over: Chọn Play Again, Back to Menu, hoặc Quit

---

## 3. DEMO

[Phần này sẽ được thêm sau với hình ảnh/video minh họa]

---

## 4. TỔNG KẾT

### 4.1 Các Tính Năng Đã Thực Hiện

- Triển khai đầy đủ luật chơi UNO chuẩn
- Giao diện JavaFX chuyên nghiệp với bàn chơi 4 vị trí
- 5 thuật toán Bot AI với độ khó khác nhau
- Xử lý chính xác các bài đặc biệt (Skip, Reverse, +2, Wild, +4)
- Menu chính và màn hình kết thúc hoàn chỉnh
- Quản lý trạng thái game phức tạp

### 4.2 Kiến Thức Áp Dụng

- Lập trình hướng đối tượng: Kế thừa (Bot extends Player), Đa hình, Đóng gói
- Cấu trúc dữ liệu: ArrayList, HashMap, Enum
- Thiết kế mô hình: MVC (Model-View-Controller)
- JavaFX: Layout, Event Handling, Animation
- Thuật toán: Sắp xếp, Tìm kiếm, Thuật toán chọn tối ưu

### 4.3 Hạn Chế Hiện Tại

- Không có hệ thống lưu trữ điểm/thống kê
- Không hỗ trợ 2 người chơi thực
- Không có âm thanh hoặc animation phức tạp
- Bot không học từ các trận trước

### 4.4 Hướng Phát Triển Tương Lai

- Thêm database để lưu trữ thông tin người chơi
- Hỗ trợ multiplayer online qua network
- Thêm hiệu ứng âm thanh và animation card flip
- Machine Learning để Bot có thể học và cải thiện kỹ năng
- Chế độ story/campaign với các mục tiêu đặc biệt
- Theme/skin tùy chỉnh

### 4.5 Kết Luận

Dự án Game UNO đã hoàn thành với các tính năng cơ bản hoạt động ổn định. Cấu trúc code rõ ràng, dễ bảo trì và mở rộng. Các kiến thức OOP, JavaFX, và design pattern đã được áp dụng hiệu quả. Dự án này là nền tảng tốt để tiếp tục nâng cao hoặc mở rộng các tính năng mới.

---

**Ngày báo cáo**: 02/12/2025  
**Phiên bản**: 2.0  
**Trạng thái**: Hoàn thành
