# Phase 10 — Checkout, Order, Inventory Reservation

Phase 10 không thêm Gradle dependency và chưa tạo Payment/Shipment đầy đủ. Checkout dùng Firestore transaction để tạo `orders/{orderId}`, các item snapshot và chuyển tồn kho `available → reserved` cùng một lần commit.

## Deploy Firebase

```powershell
firebase deploy --only "firestore:rules,firestore:indexes"
```

Chờ index `orders(orderCode, receiverPhone)` chuyển sang `Enabled` trước khi thử guest tracking.

## Idempotency và transaction

- `orderId` đồng thời là UUID `requestId` được giữ trong `CheckoutViewModel`.
- Retry cùng request/owner trả lại Order cũ và không reserve lần hai.
- Dùng lại key cho owner khác bị từ chối.
- Mỗi Order chỉ chứa item của một seller và tối đa một campaign đang ACTIVE.
- Transaction đọc lại Product, ProductBatch, Promotion và Campaign trước khi ghi.
- Nếu một item thiếu tồn kho, campaign thiếu target hoặc rules từ chối, Order/item/batch/campaign đều không commit.
- Order `PENDING` chỉ tăng `reservedQuantity`; không tăng `soldQuantity` hoặc `rescuedQuantity`.

## Document shape

`orders/{orderId}` dùng schema trong `final.md`, bổ sung hai field kỹ thuật:

```text
requestId: string (bằng orderId)
campaignReservedQuantity: number
batchReservations: map<batchId, quantity>, tối đa 20 batch
```

`orders/{orderId}/items/{batchId}` chứa snapshot product name/image/unit, original/rescue/final price, quantity và subtotal. Snapshot không được thay đổi sau khi tạo.

Batch/campaign có thêm metadata transaction:

```text
lastReservationOrderId
lastReservationQuantity
```

Metadata này phục vụ Firestore Rules kiểm tra reservation có đi kèm OrderItem trong cùng atomic write.

## Guest checkout — lưu ý bảo mật

Guest không phải `User` và không cần Firebase Auth. Rules demo cho phép guest tạo Order chỉ khi batch reservation, Order và OrderItem khớp nhau bằng `getAfter()`. Guest tracking dùng `orderCode + receiverPhone`.

Để hỗ trợ truy vấn guest trực tiếp từ APK, rule đọc guest order hiện phù hợp cho phạm vi đồ án nhưng không nên tuyên bố là bảo mật production. Khi triển khai thật, hãy chuyển guest checkout/tracking sang trusted backend hoặc callable function có rate-limit và trả về dữ liệu tối thiểu.

## Checklist

1. Checkout một seller: Order PENDING và OrderItem snapshot được tạo.
2. Kiểm tra batch: `available -= quantity`, `reserved += quantity`, `sold` giữ nguyên.
3. Nếu batch thuộc campaign ACTIVE: campaign `reserved` tăng, `rescued` giữ nguyên.
4. Checkout hai seller cùng lúc phải bị chặn trước transaction.
5. Giảm available dưới quantity rồi checkout: không có document hoặc mutation nào được commit.
6. Bấm đặt hàng/retry cùng màn hình: nhận lại cùng mã đơn, tồn kho không giảm lần hai.
7. Guest lưu mã `RF-...` và phone rồi thử màn tra cứu.
8. BANK_TRANSFER chỉ tạo `PaymentStatus.PENDING`; Phase này không xác nhận đã thanh toán.
