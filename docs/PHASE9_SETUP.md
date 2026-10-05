# Phase 9 — Guest Cart + Customer Cart

Phase 9 không thêm Gradle dependency. Bảng Room `guest_cart_items` đã có từ schema ban đầu nên không cần tăng database version hoặc xóa dữ liệu ứng dụng.

## Firebase cần triển khai

Customer cart dùng collection `customerCarts`. Deploy rules mới bằng:

```powershell
firebase deploy --only firestore:rules
```

Không cần composite index mới.

`customerCarts/{customerUid}` có dạng:

```text
id: string (bằng customerUid)
ownerType: CUSTOMER
ownerKey: string (bằng customerUid)
items: array, tối đa 100 phần tử
  - id: productId::batchId
  - productId: string
  - batchId: string
  - sellerId: string
  - quantity: number > 0
  - unitPrice: number >= 0
  - selected: boolean
updatedAt: timestamp
```

Chỉ tài khoản có profile `users/{uid}.role == CUSTOMER` được đọc/ghi document của chính mình. Seller/Admin không dùng customer cart.

## Cách hoạt động

- Guest cart được nhận diện bằng `guestId` trong SharedPreferences và lưu trong Room. Đóng/mở lại app vẫn còn giỏ.
- Customer cart đọc/ghi Firestore và UI luôn hiển thị trạng thái `SYNCED`, `OFFLINE` hoặc lỗi quyền.
- Khi thêm, đổi số lượng hoặc bấm **Đồng bộ & kiểm tra giá/tồn**, app đọc lại Product, ProductBatch và Promotion rồi gọi cùng `PricingService` của Phase 8.
- Cart không gọi `reserveStock`, `commit` hoặc `release`; số lượng chỉ được giữ ở Phase checkout/order sau này.
- Các item đã chọn được nhóm theo `sellerId`, chuẩn bị cho bước tách order ở Phase sau.
- Guest cart và customer cart đang là hai giỏ riêng. Phase 9 không tự merge guest cart vào tài khoản khi đăng nhập.

## Checklist kiểm tra

1. Ở chế độ guest, thêm sản phẩm vào giỏ, đóng hẳn app rồi mở lại: item vẫn còn.
2. Thêm lại cùng product/batch: số lượng phải cộng dồn, không tạo dòng trùng.
3. Nhập số lượng `0`, số âm hoặc lớn hơn tồn kho: thao tác bị chặn.
4. Thay đổi promotion/tồn kho trên Firestore, mở giỏ và bấm revalidate: giá hoặc cảnh báo phải cập nhật.
5. Đăng nhập Customer, thêm item rồi mở app trên phiên khác: `customerCarts/{uid}` được đồng bộ.
6. Tắt mạng ở customer cart: thao tác ghi bị chặn và UI báo chưa đồng bộ.
7. Chọn sản phẩm từ hai seller: phần tổng kết phải báo hai nhóm seller.
8. Kiểm tra `reservedQuantity` của batch không thay đổi khi thêm/cập nhật/xóa cart.
