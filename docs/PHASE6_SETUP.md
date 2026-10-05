# Phase 6 — Home, Discovery, Search, Filter, Highlight

Phase 6 không thêm dependency mới. Firebase Auth, Firestore, Room, Fused Location và Glide đã được cấu hình từ các phase trước.

## Việc cần làm trên Firebase

1. Deploy file `firestore.rules` mới. Rule cho phép client chỉ đọc document `banners` có `active == true`; client không được ghi banner.
2. Tạo collection `banners`. Mỗi document dùng các field:
   - `title` (string)
   - `imageUrl` (string, URL HTTPS)
   - `campaignId` (string, có thể rỗng; nên trỏ tới campaign `ACTIVE`)
   - `displayOrder` (number)
   - `startDate` và `endDate` (Firestore Timestamp, `endDate > startDate`)
   - `active` (boolean)
3. Đảm bảo dữ liệu Home đã có trong `categories`, `products` và `campaigns`. Home chỉ highlight campaign `ACTIVE`; sản phẩm chỉ hiển thị khi `status == ACTIVE`.

Ví dụ banner:

```text
title: "Giải cứu xoài Đồng Tháp"
imageUrl: "https://.../xoai.jpg"
campaignId: "campaign-id"
displayOrder: 1
startDate: Timestamp hiện tại
endDate: Timestamp sau 7 ngày
active: true
```

Query Phase 6 chỉ dùng một điều kiện equality (`active` hoặc `status`), nên không cần composite index mới. Nếu Firebase Console hiển thị link yêu cầu index do bạn tự mở rộng query, tạo index theo đúng link đó.

## Vị trí foreground

`ACCESS_COARSE_LOCATION` và `ACCESS_FINE_LOCATION` đã có trong Manifest. Người dùng phải bấm **Dùng vị trí** và cấp quyền; ứng dụng không theo dõi nền. Mobile point chỉ nằm trong mục gần bạn khi:

- campaign là `ACTIVE`;
- mode là `MOBILE_POINT`;
- đang bật chia sẻ;
- `locationUpdatedAt` không quá 10 phút.

## Cache và nâng cấp Room

Database tăng lên version 4 và có migration `3 -> 4` để tạo `banner_cache`. Không cần xóa app khi nâng cấp từ Phase 5. Sau khi Android Studio build thành công, giữ file schema Room `app/schemas/.../4.json` trong source control.

Khi offline, Home/Discovery vẫn đọc dữ liệu đã cache của banner, category, product và campaign. Lần cài mới hoàn toàn chưa từng sync sẽ hiển thị empty state đúng thiết kế.

## Kiểm tra thủ công

1. Mở Home online, bấm **Làm mới**, rồi tắt mạng và mở lại app: 8 section dữ liệu phải đọc được từ cache; Feed hiển thị thông báo chờ Phase 7.
2. Xác nhận đúng thứ tự 9 section và campaign `CRITICAL` đứng trước.
3. Cấp/từ chối quyền vị trí để kiểm tra các trạng thái permission/unavailable/empty.
4. Tạo một mobile campaign có vị trí cũ hơn 10 phút: campaign vẫn có thể xuất hiện ở danh sách ACTIVE nhưng không xuất hiện trong mục nearby hoặc sort Distance.
5. Thử tìm không dấu, lọc reason/urgency/mode/category/province và các kiểu sắp xếp.
