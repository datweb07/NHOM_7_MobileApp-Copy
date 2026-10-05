# Phase 7 — Post, Reaction, Comment

Phase 7 không thêm Gradle dependency. Module dùng Firestore, Room, Glide và `MediaStorage` abstraction đã có.

## Deploy Firestore

Từ thư mục project, đăng nhập đúng Firebase project rồi chạy:

```powershell
firebase deploy --only "firestore:rules,firestore:indexes"
```

Index mới phục vụ pagination của feed:

- collection `posts`;
- `status` ascending;
- `createdAt` descending.

Chờ Firebase Console báo index `Enabled` trước khi kiểm tra feed. Nếu chưa deploy index, query feed sẽ trả lỗi cần index nhưng cache Room cũ vẫn được giữ.

## Document shape

`posts/{postId}`:

```text
id: string
sellerId: string
campaignId: string (có thể rỗng)
title: string, 3-120 ký tự
content: string, 10-3000 ký tự
imageUrls: array<string>, tối đa 6
linkedProductIds: array<string>, tối đa 12
urgencyLevel: NORMAL | HIGH | CRITICAL
status: DRAFT | PENDING_APPROVAL | PUBLISHED | REJECTED | HIDDEN
viewCount: integer >= 0
createdAt: timestamp
updatedAt: timestamp
```

`posts/{postId}/reactions/{userId}` dùng chính UID làm document ID. Vì vậy mỗi user chỉ có một reaction trên một post; chọn lại cùng loại sẽ xóa reaction, chọn loại khác sẽ thay thế.

`posts/{postId}/comments/{commentId}` chứa `id`, `postId`, `userId`, `content`, `status`, `createdAt`, `updatedAt`.

## Moderation

- Chỉ seller có `sellerStatus == APPROVED` được tạo/sửa post.
- Seller chỉ tạo `DRAFT` hoặc `PENDING_APPROVAL`.
- Post chỉ xuất hiện trên feed khi Admin đổi `PENDING_APPROVAL -> PUBLISHED`.
- UI Admin Moderation thuộc Phase 14. Trong lúc test Phase 7, có thể dùng Firebase Console/Admin SDK đáng tin cậy để đổi `status` và `updatedAt`; Firebase Console chạy với quyền quản trị.
- Client không thể tự xuất bản post qua Firestore rules.

## Media

Phase 7 render URL ảnh bằng Glide. Màn editor nhận URL HTTPS và không chứa secret. `MediaStorage` đã giữ abstraction cho Cloudinary; upload thực tế chỉ cấu hình khi team cung cấp unsigned upload preset an toàn. Không dùng Firebase Storage.

## Offline và Room

Room tăng từ version 4 lên version 5, migration tự thêm `imageUrlsSerialized`, `linkedProductIdsSerialized`, `viewCount` vào `post_cache`. Không cần xóa app khi nâng cấp.

- Feed và Post Detail có thể đọc post đã cache khi offline.
- Reaction, comment và lưu/gửi duyệt post sẽ báo lỗi ngay khi thiết bị offline; không giả lập thao tác ghi.
- Reaction/comment không được cache trong Phase 7.

## Checklist kiểm tra

1. Seller đã approve tạo draft, sửa draft và gửi duyệt; trạng thái phải là `PENDING_APPROVAL`.
2. Post pending không xuất hiện trong public feed.
3. Sau khi Admin publish, refresh feed và mở detail.
4. Mỗi tài khoản chọn LIKE rồi CARE: chỉ còn một reaction document.
5. Chọn CARE lần nữa: reaction document bị xóa.
6. Gửi comment rỗng hoặc hơn 500 ký tự phải bị chặn.
7. Mở feed online để cache, tắt mạng rồi mở lại: post vẫn đọc được; ghi reaction/comment phải báo cần mạng.
8. Bấm **Tải thêm** để kiểm tra pagination không trùng dữ liệu.
