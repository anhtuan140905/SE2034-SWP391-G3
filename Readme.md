\# EventHub



EventHub là nền tảng quản lý sự kiện và đặt vé trực tuyến được xây dựng bằng Spring Boot. Hệ thống hỗ trợ toàn bộ vòng đời của một sự kiện: từ khởi tạo, kiểm duyệt, mở bán vé, chọn ghế, thanh toán, phát hành vé, check-in cho đến quyết toán tài chính.



!\[Giao diện chọn ghế EventHub](readme.png)



\## Tính năng chính



\### Người tham dự



\- Đăng ký, đăng nhập, đặt lại mật khẩu và đăng nhập bằng Google OAuth2

\- Khám phá sự kiện theo từ khóa, danh mục, thành phố, tháng và khoảng giá

\- Xem thông tin chi tiết, lịch trình, địa điểm và loại vé của sự kiện

\- Xem sơ đồ ghế và trạng thái ghế

\- Khóa hoặc bỏ khóa ghế trong phiên đặt vé

\- Áp dụng và kiểm tra voucher

\- Thanh toán qua VNPay hoặc chuyển khoản ngân hàng/VietQR

\- Nhận vé điện tử kèm mã QR

\- Lưu sự kiện yêu thích và nhận gợi ý sự kiện bằng AI



\### Ban tổ chức



\- Tạo và quản lý sự kiện

\- Cấu hình địa điểm, loại vé, khu vực và sơ đồ ghế

\- Tạo và quản lý voucher

\- Quản lý nhân viên và phân quyền

\- Theo dõi đơn hàng, doanh số vé và hoạt động sự kiện

\- Check-in người tham dự bằng mã vé



\### Kiểm duyệt viên



\- Tiếp nhận sự kiện chờ duyệt

\- Phê duyệt hoặc từ chối sự kiện

\- Quản lý ban tổ chức và nội dung sự kiện

\- Theo dõi hoạt động trên workspace kiểm duyệt



\### Quản trị viên



\- Quản lý người dùng, vai trò, danh mục sự kiện và địa điểm

\- Quản lý quyền truy cập theo vai trò

\- Theo dõi hoạt động chung của hệ thống



\### Nhân viên tài chính



\- Theo dõi các sự kiện đã kết thúc và hồ sơ quyết toán

\- Tính tổng doanh thu, phí hệ thống và số tiền chi trả cho ban tổ chức

\- Tạo quyết toán thủ công với tỷ lệ phí hệ thống tùy chỉnh

\- Hoàn tất quyết toán và theo dõi số liệu tài chính



\## Công nghệ sử dụng



| Thành phần | Công nghệ |

|---|---|

| Backend | Java 21, Spring Boot 3.5 |

| Web | Spring MVC, REST API |

| Bảo mật | Spring Security, Google OAuth2 |

| Truy cập dữ liệu | Spring Data JPA, Hibernate |

| Cơ sở dữ liệu | Microsoft SQL Server |

| Frontend | Thymeleaf, HTML, CSS, JavaScript, Bootstrap |

| Thanh toán | VNPay, VietQR |

| Lưu trữ hình ảnh | Cloudinary |

| Email | Spring Mail |

| Mã QR | ZXing |

| Trí tuệ nhân tạo | Google Gemini API |

| Công cụ build | Maven |



\## Kiến trúc hệ thống



Ứng dụng được tổ chức theo kiến trúc phân lớp:



```text

Trình duyệt / Thymeleaf / JavaScript

&#x20;                 ↓

&#x20;         MVC \& REST Controller

&#x20;                 ↓

&#x20;               Service

&#x20;                 ↓

&#x20;     Spring Data JPA Repository

&#x20;                 ↓

&#x20;        Microsoft SQL Server

```



Các package chính:



```text

configuration  Cấu hình ứng dụng, bảo mật, Cloudinary, VNPay và scheduled job

controller     MVC Controller và REST API theo từng vai trò

service        Interface và implementation xử lý nghiệp vụ

repository     JPA Repository, projection và các câu truy vấn tùy chỉnh

model          Các JPA Entity đại diện cho dữ liệu nghiệp vụ

model.constant Enum và các trạng thái cố định của hệ thống

modelview      Request DTO, Response DTO và View Model

security       Xác thực, OAuth2, OTP và đặt lại mật khẩu

common         Tiện ích dùng chung cho email, QR, bảo mật và sinh mã

exception      Các exception nghiệp vụ của ứng dụng

```



\## Luồng đặt vé và thanh toán



```text

Khám phá sự kiện

→ Xem chi tiết sự kiện

→ Tải sơ đồ ghế bằng REST API

→ Khóa ghế

→ Kiểm tra voucher

→ Tạo Order, OrderDetail và Payment

→ Chuyển hướng sang VNPay

→ Xác minh chữ ký VNPay trả về

→ Xác nhận thanh toán

→ Phát hành vé

→ Xóa khóa ghế

```



Sơ đồ ghế được tải bất đồng bộ thông qua:



```http

GET /api/events/{eventId}/seat-map

```



Việc chọn và bỏ chọn ghế được xử lý qua:



```http

POST /checkout/toggle-lock

```



Frontend nhận dữ liệu JSON, tạo các phần tử ghế bằng JavaScript và gắn CSS class tương ứng với trạng thái của từng ghế.



\## Yêu cầu hệ thống



Cài đặt các thành phần sau trước khi chạy dự án:



\- Java Development Kit 21

\- Microsoft SQL Server

\- Maven 3.9+ hoặc Maven Wrapper đi kèm dự án

\- Tài khoản Cloudinary

\- Thông tin xác thực Google OAuth2

\- Gmail App Password

\- Tài khoản VNPay Sandbox

\- Google Gemini API Key



\## Cấu hình



Tạo database có tên `EventHub` trong SQL Server.



Cấu hình kết nối tại `src/main/resources/application.properties`:



```properties

spring.datasource.url=jdbc:sqlserver://localhost:1433;database=EventHub;encrypt=true;trustServerCertificate=true

spring.datasource.username=YOUR\_DATABASE\_USERNAME

spring.datasource.password=YOUR\_DATABASE\_PASSWORD

```



Tạo file `.env` tại thư mục gốc của dự án:



```dotenv

CLOUDINARY\_NAME=your\_cloud\_name

CLOUDINARY\_KEY=your\_cloudinary\_key

CLOUDINARY\_SECRET=your\_cloudinary\_secret



GOOGLE\_CLIENT\_ID=your\_google\_client\_id

GOOGLE\_CLIENT\_SECRET=your\_google\_client\_secret



APP\_PASSWORD=your\_gmail\_app\_password

GEMINI\_API\_KEY=your\_gemini\_api\_key



VNPAY.TMN-CODE=your\_vnpay\_tmn\_code

VNPAY.HASH-SECRET=your\_vnpay\_hash\_secret

VNPAY.PAY-URL=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html

VNPAY.RETURN-URL=http://localhost:8081/payment/vnpay/return

```



> Không đưa file `.env`, mật khẩu cơ sở dữ liệu, API key, OAuth secret, mật khẩu email hoặc VNPay HashSecret lên GitHub.



\## Chạy dự án



Clone repository:



```bash

git clone <repository-url>

cd SE2034-SWP391-G3-code-vibe

```



Chạy bằng Maven Wrapper:



```bash

./mvnw spring-boot:run

```



Trên Windows:



```powershell

.\\mvnw.cmd spring-boot:run

```



Truy cập ứng dụng tại:



```text

http://localhost:8081

```



\## Kiểm thử và đóng gói



Biên dịch và chạy test:



```bash

./mvnw clean test

```



Đóng gói thành file JAR:



```bash

./mvnw clean package

```



Chạy file đã đóng gói:



```bash

java -jar target/SWP391-0.0.1-SNAPSHOT.jar

```



\## Vai trò trong hệ thống



```text

ROLE\_ATTENDEE

ROLE\_ORGANIZER

ROLE\_STAFF

ROLE\_MANAGER

ROLE\_MODERATOR

ROLE\_FINANCE

ROLE\_ADMIN

```



Mỗi workspace được bảo vệ bằng Spring Security và các quy tắc phân quyền tương ứng.



\## Cấu trúc dự án



```text

src

├── main

│   ├── java/vn/edu/fpt

│   │   ├── common

│   │   ├── configuration

│   │   ├── controller

│   │   ├── exception

│   │   ├── model

│   │   ├── modelview

│   │   ├── repository

│   │   ├── security

│   │   └── service

│   └── resources

│       ├── static

│       ├── templates

│       └── application.properties

└── test

```



\## Lưu ý bảo mật



\- Chỉ xác nhận kết quả thanh toán sau khi chữ ký VNPay được kiểm tra hợp lệ.

\- Giá vé, mức giảm voucher, phí hệ thống và số tiền quyết toán phải được backend tính lại.

\- Giới hạn số vé và quyền sở hữu ghế phải được kiểm tra tại server.

\- Việc ẩn hoặc xóa button trên giao diện không thay thế cho kiểm tra phân quyền ở backend.

\- Mọi khóa bí mật và thông tin xác thực phải được cung cấp qua biến môi trường.



\## Giấy phép



Dự án được phát triển phục vụ mục đích học tập trong khuôn khổ môn học SWP391.

