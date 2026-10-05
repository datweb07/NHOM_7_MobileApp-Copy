# Phase 15 — Offline Room Cache + WorkManager

## Setup

Phase 15 không cần thay đổi Firebase Rules, Firestore index hoặc Cloud Functions.

Dependency mới:

```kotlin
implementation("androidx.work:work-runtime:2.12.0")
```

Chỉ cần **Sync Project with Gradle Files** trong Android Studio. WorkManager tự khởi tạo
bằng AndroidX Startup; không cần thêm service/receiver thủ công vào Manifest.

## Room migration

Database tăng từ version 5 lên 6 và dùng `MIGRATION_5_6`, không dùng destructive
migration. Hai bảng mới:

- `cache_metadata`: TTL, lần attempt/success và lỗi sync gần nhất.
- `order_cache`: snapshot chỉ đọc cho danh sách đơn customer/seller.

Schema được export tại `app/schemas/com.rescuefarm.data.local.database.RescueFarmDatabase/6.json`.

## Chính sách đồng bộ

`ReadCacheSyncWorker` chạy khi có mạng:

- một lần khi app khởi động;
- định kỳ mỗi 6 giờ;
- catalog TTL 30 phút;
- campaign/order TTL 10 phút;
- feed TTL 15 phút;
- banner TTL 1 giờ.

Worker chỉ gọi read refresh cho catalog, campaign, banner, feed và order của user hiện
tại. Cache order cũ hơn 7 ngày được dọn. Worker không gọi checkout, payment, approval,
inventory reservation hoặc mutation moderation.

## Test offline

1. Mở app khi online và đợi Home/feed/order load thành công.
2. Bật Airplane mode hoặc tắt Wi-Fi/mobile data.
3. Mở lại app: Home, product, campaign, feed và danh sách order vẫn đọc từ Room.
4. Home phải hiển thị nhãn `OFFLINE` hoặc `CACHE CŨ`.
5. Thử checkout, cập nhật tồn kho, campaign reservation, payment hoặc admin approval.
6. App phải báo cần kết nối mạng và không tạo mutation chờ đồng bộ.
7. Bật mạng lại; WorkManager hoặc thao tác refresh sẽ cập nhật Room cache.

Có thể kiểm tra lịch worker trong Android Studio App Inspection → Background Task
Inspector với unique work `rescuefarm-read-cache-periodic` và
`rescuefarm-read-cache-startup`.
