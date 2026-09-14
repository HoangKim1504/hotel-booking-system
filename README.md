# Hotel Room Booking Website

Website đặt phòng khách sạn trực tuyến được xây dựng nhằm hỗ trợ người dùng tìm kiếm phòng, xem thông tin chi tiết, đặt nhiều loại phòng trong cùng một lần đặt và quản lý lịch sử đặt phòng.

Hệ thống đồng thời cung cấp trang quản trị dành cho Admin để quản lý người dùng, loại phòng, phòng, đơn đặt phòng và theo dõi các số liệu thống kê của hệ thống.

## 1. Công nghệ sử dụng

### Backend

- Java
- Spring Boot 4
- Spring REST / Spring MVC
- Spring Data MongoDB
- Spring Security
- Maven

### Frontend

- ReactJS
- JavaScript
- HTML / JSX
- CSS
- Bootstrap

### Database

- MongoDB

### Deployment

- Docker
- Docker Compose

## 2. Kiến trúc hệ thống

Hệ thống được xây dựng theo kiến trúc **Monolithic**, bao gồm:

```text
Frontend (ReactJS)
        |
        | REST API
        v
Backend (Spring Boot)
        |
        v
MongoDB
```

Frontend giao tiếp với Backend thông qua REST API.

Backend chịu trách nhiệm xử lý nghiệp vụ, xác thực, phân quyền và truy xuất dữ liệu từ MongoDB.

## 3. Chức năng chính

### User

- Đăng ký tài khoản
- Đăng nhập / đăng xuất
- Tìm kiếm loại phòng theo ngày Check-in / Check-out
- Tìm kiếm theo số lượng khách
- Lọc, sắp xếp và phân trang kết quả
- Xem thông tin chi tiết loại phòng
- Thêm phòng vào giỏ đặt phòng
- Đặt nhiều loại phòng trong cùng một booking
- Xem lịch sử đặt phòng
- Xem chi tiết booking
- Cập nhật thông tin cá nhân
- Hủy booking theo điều kiện của hệ thống

### Admin

- Quản lý loại phòng
- Quản lý phòng
- Quản lý người dùng
- Quản lý booking
- Tìm kiếm, sắp xếp và phân trang dữ liệu
- Thay đổi trạng thái booking
- Theo dõi trạng thái thanh toán
- Xem thống kê hệ thống
- Xem biểu đồ doanh thu và dữ liệu booking

## 4. Phân quyền

Hệ thống sử dụng Spring Security để kiểm soát quyền truy cập.

### USER

Được phép sử dụng các chức năng đặt phòng và quản lý booking cá nhân.

### ADMIN

Được phép truy cập trang quản trị và thực hiện các chức năng quản lý hệ thống.

Người dùng thông thường không được phép truy cập các URL thuộc khu vực `/admin`.

Tài khoản bị khóa sẽ không thể đăng nhập vào hệ thống.

## 5. Cấu trúc project

```text
project/
│
├── backend/
│   └── hotel-booking-system/
│       ├── src/
│       ├── pom.xml
│       ├── Dockerfile
│       └── docker-compose.yml
│
├── frontend/
│   ├── src/
│   ├── public/
│   ├── package.json
│   └── ...
│
└── README.md
```

## 6. Cách chạy project

### Yêu cầu

Cài đặt:

- Docker Desktop
- Node.js
- npm
- Git

### Chạy Backend và Database

Di chuyển vào thư mục backend:

```bash
cd backend/hotel-booking-system
```

Build và chạy Docker:

```bash
docker compose up --build
```

Nếu muốn chạy ở chế độ nền:

```bash
docker compose up --build -d
```

### Chạy Frontend

Di chuyển vào thư mục frontend:

```bash
cd frontend
```

Cài dependency:

```bash
npm install
```

```bash
npm install recharts
```

Chạy React:

```bash
npm run dev
```

Sau khi khởi động thành công, truy cập website bằng URL được hiển thị trên terminal của frontend.

## 7. Dừng hệ thống

Dừng Backend và Database:

```bash
docker compose down
```

Nếu muốn xóa container, network và volume:

```bash
docker compose down -v
```

## 8. Build lại hệ thống

Khi thay đổi code Backend và muốn build lại Docker:

```bash
docker compose down
docker compose build --no-cache
docker compose up
```

Hoặc:

```bash
docker compose up --build
```

## 9. API

Backend cung cấp REST API cho Frontend với các nhóm API chính:

- Authentication API
- User API
- Room Type API
- Room API
- Booking API
- Cart / Booking Item API
- Admin API
- Statistics API

Dữ liệu giữa Frontend và Backend được trao đổi chủ yếu dưới định dạng JSON.

## 10. Database

Hệ thống sử dụng MongoDB để lưu trữ dữ liệu.

Một số collection chính:

```text
users
roomTypes
rooms
bookings
bookingItems
```

MongoDB được chạy thông qua Docker Compose cùng với Backend.

## 11. Mục tiêu đồ án

- Xây dựng website đặt phòng khách sạn hoàn chỉnh.
- Áp dụng Java Spring Boot để phát triển REST API.
- Áp dụng ReactJS để xây dựng giao diện người dùng.
- Áp dụng Spring Security cho Authentication và Authorization.
- Sử dụng MongoDB để quản lý dữ liệu.
- Áp dụng kiến trúc Monolithic.
- Triển khai môi trường ứng dụng bằng Docker và Docker Compose.
- Thực hành kết nối Frontend – Backend – Database trong một hệ thống hoàn chỉnh.

## 12. Tác giả

[Kim Tran Hoang](https://github.com/HoangKim1504)
