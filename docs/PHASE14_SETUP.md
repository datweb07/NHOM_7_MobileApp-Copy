# Phase 14 — Admin setup

Phase 14 không thêm Android dependency và không yêu cầu Cloud Functions. Cần deploy
Firestore Rules mới vì các truy vấn và moderation transition chỉ được phép với `ADMIN`.

## 1. Tạo tài khoản admin đầu tiên

Không đăng ký admin từ màn hình Register. Hãy tạo user trong Firebase Authentication,
lấy `uid`, rồi tạo document `users/{uid}` bằng Firebase Console hoặc Admin SDK đáng tin cậy:

```text
id: <uid>
email: <email đăng nhập>
fullName: <tên admin>
phone: ""
avatarUrl: ""
role: "ADMIN"
status: "ACTIVE"
latitude: 0
longitude: 0
createdAt: <server timestamp>
updatedAt: <server timestamp>
```

Không cho client tự ghi `role = ADMIN`. Rules hiện tại cố ý chặn luồng này.

## 2. Deploy Firestore Rules

Từ thư mục gốc dự án:

```powershell
firebase login --reauth
firebase use rescuefarm-24b5f
firebase deploy --only firestore:rules
```

Phase này không thêm composite index. Collection `adminAuditLogs` được tạo tự động khi
admin thực hiện transition; client không được update hoặc delete audit log.

## 3. Dữ liệu category và banner

Màn hình admin cho phép bật/tắt category và banner hiện có. Có thể seed các document ban
đầu bằng Firebase Console. Banner vẫn dùng schema Phase 6 (`id`, `title`, `imageUrl`,
`campaignId`, `displayOrder`, `startDate`, `endDate`, `active`).

## 4. Luồng kiểm tra nhanh

1. Đăng nhập tài khoản có `role = ADMIN`.
2. Mở `Hồ sơ -> Dashboard quản trị`.
3. Duyệt seller đang `PENDING`; kiểm tra cả `sellerApplications/{uid}.status` và
   `users/{uid}.sellerStatus` chuyển cùng lúc.
4. Duyệt/từ chối post hoặc campaign, nhập lý do cho thao tác hạn chế.
5. Ẩn product/review, dừng campaign hoặc khóa user.
6. Kiểm tra document mới trong `adminAuditLogs`.
7. Xác nhận Orders/Analytics chỉ có chức năng theo dõi, không hard-delete.

Nếu nhận `PERMISSION_DENIED`, kiểm tra đúng project Firebase, rules đã deploy và document
`users/{uid}` của tài khoản hiện tại có chính xác `role: "ADMIN"`.
