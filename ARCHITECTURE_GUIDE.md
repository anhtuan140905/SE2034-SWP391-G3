# HƯỚNG DẪN KIẾN TRÚC & KHỞI CHẠY DỰ ÁN - EVENT HUB

Tài liệu này cung cấp chi tiết về **kiến trúc mã nguồn (Source Code Architecture)** của dự án **EventHub** và hướng dẫn thiết lập **Live Reload (Hot Swap)** tương tự như **Live Server** trong VS Code dành cho dự án Spring Boot.

---

## 1. PHÂN TÍCH KIẾN TRÚC DỰ ÁN (PROJECT ARCHITECTURE)

Dự án **EventHub** là một ứng dụng Web quản lý và bán vé sự kiện, được phát triển dựa trên framework **Spring Boot (v3.5.14)** và **Java 21**, sử dụng **SQL Server** làm cơ sở dữ liệu và **Thymeleaf** làm Template Engine cho giao diện người dùng.

Cấu trúc thư mục mã nguồn được phân chia theo mô hình chuẩn **MVC (Model-View-Controller)** và **Layered Architecture** (Kiến trúc phân tầng):

### A. Thư mục `src/main/java/vn/edu/fpt` (Backend Logic)
*   **`EventHubApplication.java`**: Điểm khởi chạy chính của ứng dụng Spring Boot. Lớp này tắt tạm thời cấu hình bảo mật tự động (`exclude = { SecurityAutoConfiguration.class }`) để thuận tiện cho việc phát triển ban đầu.
*   **`configuration/`**: Chứa các lớp cấu hình hệ thống. Hiện tại có [SecurityConfiguration.java](file:///C:/Users/nghlo/OneDrive/Desktop/swp/SE2034-SWP391-G3/src/main/java/vn/edu/fpt/configuration/SecurityConfiguration.java) cấu hình mã hóa mật khẩu bằng `BCryptPasswordEncoder`.
*   **`controller/`**: Tiếp nhận các yêu cầu HTTP (HTTP Requests) từ client, điều hướng và trả về View (Thymeleaf templates) hoặc dữ liệu API. Được phân nhóm theo chức năng/phân quyền:
    *   `auth/`: Xử lý đăng nhập, đăng ký và quên mật khẩu ([AuthController.java](file:///C:/Users/nghlo/OneDrive/Desktop/swp/SE2034-SWP391-G3/src/main/java/vn/edu/fpt/controller/auth/AuthController.java)).
    *   `hompage/` (homepage): Điều hướng trang chủ ([HomepageController.java](file:///C:/Users/nghlo/OneDrive/Desktop/swp/SE2034-SWP391-G3/src/main/java/vn/edu/fpt/controller/hompage/HomepageController.java) ánh xạ `/` tới `homepage/Home.html`).
    *   `admin/`, `organizer/`: Xử lý các tác vụ quản trị và nhà tổ chức sự kiện.
*   **`model/`**: Chứa các thực thể JPA (JPA Entities) ánh xạ trực tiếp xuống cơ sở dữ liệu SQL Server.
    *   **Base audit**: [BaseAuditEntity.java](file:///C:/Users/nghlo/OneDrive/Desktop/swp/SE2034-SWP391-G3/src/main/java/vn/edu/fpt/model/BaseAuditEntity.java) lưu trữ thông tin lịch sử tạo/cập nhật (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`).
    *   **User & Role**: [User.java](file:///C:/Users/nghlo/OneDrive/Desktop/swp/SE2034-SWP391-G3/src/main/java/vn/edu/fpt/model/User.java) và [Role.java](file:///C:/Users/nghlo/OneDrive/Desktop/swp/SE2034-SWP391-G3/src/main/java/vn/edu/fpt/model/Role.java) quản lý tài khoản người dùng và vai trò (Admin, Organizer, User, Staff,...).
    *   **Event & Venue**: `Event`, `EventCategory`, `EventImage`, `Venue`, `VenueZone` quản lý thông tin sự kiện, phân loại sự kiện, hình ảnh và địa điểm tổ chức (phân chia theo khu vực/zone).
    *   **Ticket & Seat**: `Ticket`, `TicketType`, `Seat`, `SeatLock` quản lý các loại vé, sơ đồ ghế ngồi và cơ chế khóa ghế tạm thời khi người dùng đang thanh toán.
    *   **Order & Settlement**: `Order`, `OrderDetail`, `Settlement` quản lý đơn hàng mua vé và thanh toán/đối soát tài chính cho nhà tổ chức.
    *   **Voucher**: `Voucher`, `VoucherUsage` quản lý mã giảm giá.
    *   `constant/`: Lưu trữ các hằng số hoặc Enums như `DiscountType`, `EventStatus`, `OrderStatus`, `SettlementStatus`.
*   **`repository/`**: Lớp truy xuất dữ liệu (Data Access Object - DAO). Kế thừa `JpaRepository` để thực hiện các câu lệnh SQL (CRUD) tự động hoặc tùy chỉnh nâng cao.
*   **`service/`**: Lớp xử lý nghiệp vụ chính (Business Logic Layer). Nhận dữ liệu từ Controller, xử lý các ràng buộc nghiệp vụ, gọi Repository để ghi/đọc DB và trả kết quả về Controller.
*   **`modelview/`**: Chứa các lớp DTO (Data Transfer Objects) đại diện cho dữ liệu gửi lên từ form hoặc trả về cho client, giúp bảo mật và lọc dữ liệu của Entity (ví dụ: `RegisterUserDTO`).

### B. Thư mục `src/main/resources` (Tài nguyên hệ thống)
*   **`application.properties`**: Cấu hình cơ sở dữ liệu SQL Server, cổng chạy ứng dụng (Port: 8081), chế độ tự động tạo bảng của Hibernate (`spring.jpa.hibernate.ddl-auto=update`), và tắt bộ nhớ đệm Thymeleaf (`spring.thymeleaf.cache=false`) để phục vụ Live Reload.
*   **`templates/`**: Giao diện HTML của ứng dụng sử dụng Thymeleaf. Được tổ chức logic theo vai trò người dùng:
    *   `homepage/`: Trang chủ, danh sách sự kiện, trang mua vé, chọn ghế, lịch sử đơn hàng.
    *   `auth/`: Đăng nhập, đăng ký, quên mật khẩu (các trang này có giao diện chi tiết ở [Login.html](file:///C:/Users/nghlo/OneDrive/Desktop/swp/SE2034-SWP391-G3/src/main/resources/templates/auth/Login.html), [RegisterAccount.html](file:///C:/Users/nghlo/OneDrive/Desktop/swp/SE2034-SWP391-G3/src/main/resources/templates/auth/RegisterAccount.html)).
    *   `admin/`, `organizer/`, `staff/`, `Moderator/`, `finance/`: Trang quản lý dành riêng cho từng vai trò tương ứng.
*   **`static/`**: Chứa tài nguyên tĩnh không thay đổi (CSS, JS, Images). Hiện tại có file [auth.css](file:///C:/Users/nghlo/OneDrive/Desktop/swp/SE2034-SWP391-G3/src/main/resources/static/auth/auth.css) phục vụ giao diện đăng nhập/đăng ký.

---

## 2. HƯỚNG DẪN THIẾT LẬP DỰ ÁN & CHẠY LIVE RELOAD (TƯƠNG TỰ VSC LIVE SERVER)

Với dự án Spring Boot sử dụng Thymeleaf, bạn **không thể** dùng trực tiếp extension "Live Server" của VS Code (vốn chỉ chạy cho HTML/CSS tĩnh trên cổng 5500) vì giao diện Thymeleaf cần server Spring Boot xử lý logic backend, kết nối database trước khi render ra HTML.

Tuy nhiên, bạn hoàn toàn có thể thiết lập **Live Reload (Hot Reload)** để **mọi thay đổi ở file HTML, CSS hoặc code Java tự động cập nhật ngay lập tức** mà không cần restart lại server bằng cách kết hợp **Spring Boot DevTools** và thiết lập của IDE.

### Hướng dẫn chi tiết cho VS Code (Visual Studio Code)

#### Bước 1: Chuẩn bị Extensions
Bạn cần cài đặt các extension sau trong VS Code:
1. **Extension Pack for Java** (của Red Hat)
2. **Spring Boot Extension Pack** (của VMware)
3. **LiveReload** (một extension hỗ trợ tự động tải lại trình duyệt, cài trên VS Code và trên Chrome/Edge).

#### Bước 2: Bật tính năng Auto-Compile trên VS Code
Mặc định, Extension Java của VS Code tự động compile các file khi bạn lưu (`Save`), nhưng để đảm bảo tính năng đồng bộ hóa sang thư mục chạy (`target/classes/`) hoạt động mượt mà:
1. Nhấn `Ctrl + ,` để mở **Settings**.
2. Tìm kiếm từ khóa: `Java Compile` hoặc `Auto Build`.
3. Đảm bảo cài đặt `Java > Compile: Auto Build` được bật (mặc định luôn bật).

#### Bước 3: Cài đặt Extension LiveReload trên trình duyệt (Khuyên dùng)
Để trình duyệt tự động refresh mà bạn không cần nhấn F5:
1. Cài đặt extension **LiveReload** cho trình duyệt của bạn:
   - [LiveReload cho Chrome/Edge](https://chrome.google.com/webstore/detail/livereload/jnihnhghbbccihhdaiffocpdnhejenid)
2. Sau khi cài đặt, hãy nhấp vào biểu tượng LiveReload trên thanh công cụ của trình duyệt để kích hoạt (vòng tròn nhỏ ở giữa sẽ chuyển sang màu đen đặc khi kết nối thành công với Spring Boot DevTools).

#### Bước 4: Chạy dự án
1. Mở terminal trong thư mục `SE2034-SWP391-G3`.
2. Khởi chạy ứng dụng bằng cách gõ lệnh Maven:
   ```bash
   ./mvnw spring-boot:run
   ```
   *Hoặc bạn có thể click vào tab **Spring Boot Dashboard** ở cột bên trái VS Code và nhấn nút **Run/Debug**.*
3. Ứng dụng sẽ chạy ở cổng `http://localhost:8081/`.

#### Cách Live Reload hoạt động:
*   **Khi bạn sửa file HTML (Thymeleaf) / CSS**:
    1. Nhấn `Ctrl + S` để lưu file.
    2. VS Code sẽ tự động biên dịch và copy file HTML/CSS mới vào thư mục `target/classes`.
    3. **Spring Boot DevTools** phát hiện tài nguyên tĩnh thay đổi -> Gửi tín hiệu đến extension **LiveReload** trên trình duyệt -> Trình duyệt tự động reload trang web. Bạn sẽ thấy giao diện mới ngay lập tức!
*   **Khi bạn sửa code Java (Controller, Service...)**:
    1. Nhấn `Ctrl + S` để lưu file.
    2. VS Code tự động biên dịch lại class Java.
    3. **Spring Boot DevTools** phát hiện class thay đổi -> Tự động khởi động lại ứng dụng (Restart cực nhanh trong < 1 giây nhờ cơ chế Double Classloader của DevTools).
    4. Trình duyệt tự động reload và hiển thị kết quả.

---

### Hướng dẫn chi tiết cho IntelliJ IDEA (Nếu dùng IntelliJ)

Nếu bạn hoặc thành viên trong nhóm chuyển sang dùng IntelliJ IDEA, hãy cấu hình như sau:

1.  **Bật chế độ tự động Build**:
    *   Mở **Settings (Ctrl + Alt + S)** -> **Build, Execution, Deployment** -> **Compiler**.
    *   Tích chọn: **Build project automatically**.
2.  **Cho phép Auto-Build khi app đang chạy**:
    *   Vào **Settings (Ctrl + Alt + S)** -> **Advanced Settings** (ở cuối menu).
    *   Tích chọn: **Allow auto-make to start even if developed application is currently running**.
3.  **Chạy dự án**:
    *   Nhấp vào nút Run lớp `EventHubApplication.java`.
    *   Khi bạn thay đổi HTML/Java, IntelliJ sẽ tự động đồng bộ sau 2-3 giây hoặc bạn có thể chủ động nhấn `Ctrl + F9` (Build Project) để áp dụng ngay lập tức mà không cần khởi động lại.

---

## 3. DANH SÁCH ĐƯỜNG DẪN GIAO DIỆN CÓ SẴN (FRONTEND RUNTIME URLS)

Vì ứng dụng đang trong quá trình phát triển (Work In Progress), một số trang HTML chỉ là khung mẫu rỗng (placeholder 132 bytes). Dưới đây là danh sách các đường dẫn (URLs) dẫn tới các trang giao diện **đã được thiết kế đầy đủ** mà bạn có thể truy cập để xem thử:

### A. Phân hệ Xác thực (Authentication)
*   **Trang Đăng nhập (Login):**
    *   **URL:** [http://localhost:8081/auth/login](http://localhost:8081/auth/login)
    *   **File HTML hiển thị:** [Login.html](file:///C:/Users/nghlo/OneDrive/Desktop/swp/SE2034-SWP391-G3/src/main/resources/templates/auth/Login.html)
*   **Trang Đăng ký (Register):**
    *   **URL:** [http://localhost:8081/auth/register](http://localhost:8081/auth/register)
    *   **File HTML hiển thị:** [RegisterAccount.html](file:///C:/Users/nghlo/OneDrive/Desktop/swp/SE2034-SWP391-G3/src/main/resources/templates/auth/RegisterAccount.html)

### B. Phân hệ Quản trị viên (Admin)
*   **Trang Dashboard của Admin:**
    *   **URL:** [http://localhost:8081/admin/dashboard](http://localhost:8081/admin/dashboard)
    *   **File HTML hiển thị:** [DashboardAdmin.html](file:///C:/Users/nghlo/OneDrive/Desktop/swp/SE2034-SWP391-G3/src/main/resources/templates/admin/DashboardAdmin.html)
*   **Trang thêm địa điểm tổ chức (Add Venue):**
    *   **URL:** [http://localhost:8081/admin/addvenue](http://localhost:8081/admin/addvenue)
    *   **File HTML hiển thị:** [AddVenue.html](file:///C:/Users/nghlo/OneDrive/Desktop/swp/SE2034-SWP391-G3/src/main/resources/templates/admin/venue/AddVenue.html)

### C. Phân hệ Nhà tổ chức sự kiện (Organizer)
*   **Trang Dashboard của Organizer:**
    *   **URL:** [http://localhost:8081/organizer](http://localhost:8081/organizer)
    *   **File HTML hiển thị:** [DashboardOrganizer.html](file:///C:/Users/nghlo/OneDrive/Desktop/swp/SE2034-SWP391-G3/src/main/resources/templates/organizer/DashboardOrganizer.html)
*   **Trang tạo sự kiện mới (Create Event):**
    *   **URL:** [http://localhost:8081/organizer/create/event](http://localhost:8081/organizer/create/event)
    *   **File HTML hiển thị:** [CreateOrganizerEvent.html](file:///C:/Users/nghlo/OneDrive/Desktop/swp/SE2034-SWP391-G3/src/main/resources/templates/organizer/event/CreateOrganizerEvent.html)

