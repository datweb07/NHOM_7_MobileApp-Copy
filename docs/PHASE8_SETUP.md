# Phase 8 — Promotion + Volume Discount

Phase 8 không thêm Gradle dependency, không tăng Room version và không cần xóa dữ liệu ứng dụng. Giá được tính tập trung bởi `PricingService` theo chuỗi:

```text
originalPrice → rescuePrice → promotion → volume tier → finalUnitPrice
```

Tiền VND được làm tròn `HALF_UP` về số nguyên. `startDate` có hiệu lực, `endDate` không còn hiệu lực (`[startDate, endDate)`).

## Deploy Firestore Rules

Sau khi chọn đúng Firebase project, chạy tại thư mục gốc:

```powershell
firebase deploy --only firestore:rules
```

Phase này không cần Firestore composite index mới.

## Document shape

Mỗi product có tối đa một document `promotions/{productId}`; document ID bắt buộc trùng `productId`. Cách này loại bỏ trường hợp nhiều promotion cùng hoạt động và tạo ra hai kết quả giá khác nhau.

```text
id: string (bằng productId)
sellerId: string
productId: string (bằng document ID)
type: PERCENT | FIXED | VOLUME
value: number (PERCENT/FIXED > 0; PERCENT <= 100; VOLUME = 0)
quantityDiscountTiers: map<string, number>, tối đa 10 tier
startDate: timestamp
endDate: timestamp
active: boolean
createdAt: timestamp
updatedAt: timestamp
```

Ví dụ tier:

```text
quantityDiscountTiers: { "5": 3, "10": 5, "20": 8 }
```

Các key là ngưỡng số lượng dương; value là phần trăm trong `(0, 100]`. Tier được áp dụng khi `quantity >= threshold`. Có thể gắn tier vào `PERCENT`/`FIXED` để áp dụng sau promotion, hoặc chọn `VOLUME` nếu chỉ giảm theo số lượng.

## Quyền và cách kiểm tra

- Chỉ seller `APPROVED` và sở hữu product được tạo/sửa promotion.
- Client không xóa document; tắt bằng `active = false`.
- Khách chỉ đọc promotion đang hoạt động và còn trong thời hạn; seller sở hữu đọc được bản nháp/hết hạn.
- Mở **Seller → Sản phẩm → Khuyến mãi & giá sỉ**, lưu promotion rồi mở Product Detail và đổi số lượng để kiểm tra breakdown.
- Kiểm tra đúng tại các mốc ngay dưới tier, bằng tier và trên tier; đổi `endDate` về thời điểm đã qua để chắc chắn giá quay về `rescuePrice`.

Không tạo collection voucher/coupon trong phase này.
