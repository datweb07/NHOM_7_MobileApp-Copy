package com.rescuefarm.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.rescuefarm.data.local.dao.GuestCartDao;
import com.rescuefarm.data.local.entity.GuestCartItemEntity;
import com.rescuefarm.data.local.mapper.GuestCartMapper;
import com.rescuefarm.domain.enums.CartOwnerType;
import com.rescuefarm.domain.model.Cart;
import com.rescuefarm.domain.model.CartItem;
import java.util.concurrent.ExecutorService;

public class GuestCartRepository implements CartRepository {
    private final GuestCartDao dao;
    private final String guestId;
    private final ExecutorService executor;

    public GuestCartRepository(GuestCartDao dao, String guestId, ExecutorService executor) {
        this.dao = dao; this.guestId = guestId; this.executor = executor;
    }
    @Override public LiveData<Cart> observeCart(String ignored) {
        return Transformations.map(dao.observeItems(guestId), values -> GuestCartMapper.toCart(guestId, values));
    }
    @Override public CartOwnerType getOwnerType() { return CartOwnerType.GUEST; }
    @Override public String getOwnerKey() { return guestId; }

    @Override public void addOrReplace(CartItem item, ActionCallback callback) {
        execute(callback, () -> {
            GuestCartItemEntity existing = dao.findItem(guestId, item.getId());
            CartItem saved = existing == null ? item : item.withQuantity(existing.quantity + item.getQuantity());
            dao.upsert(GuestCartMapper.toEntity(guestId, saved));
        });
    }
    @Override public void replace(CartItem item, ActionCallback callback) {
        execute(callback, () -> {
            if (dao.findItem(guestId, item.getId()) == null) throw new IllegalStateException("NOT_FOUND");
            dao.upsert(GuestCartMapper.toEntity(guestId, item));
        });
    }
    @Override public void updateQuantity(String itemId, double quantity, ActionCallback callback) {
        if (!validQuantity(quantity, callback)) return;
        execute(callback, () -> {
            GuestCartItemEntity value = requireItem(itemId);
            dao.upsert(GuestCartMapper.toEntity(guestId, new CartItem(value.id, value.productId,
                    value.batchId, value.sellerId, quantity, value.unitPrice, value.selected)));
        });
    }
    @Override public void setSelected(String itemId, boolean selected, ActionCallback callback) {
        execute(callback, () -> {
            GuestCartItemEntity value = requireItem(itemId);
            dao.upsert(GuestCartMapper.toEntity(guestId, new CartItem(value.id, value.productId,
                    value.batchId, value.sellerId, value.quantity, value.unitPrice, selected)));
        });
    }
    @Override public void updateQuote(String itemId, double unitPrice, ActionCallback callback) {
        execute(callback, () -> {
            GuestCartItemEntity value = requireItem(itemId);
            dao.upsert(GuestCartMapper.toEntity(guestId, new CartItem(value.id, value.productId,
                    value.batchId, value.sellerId, value.quantity, unitPrice, value.selected)));
        });
    }
    @Override public void remove(String itemId, ActionCallback callback) {
        execute(callback, () -> dao.deleteById(guestId, itemId));
    }
    @Override public void clear(ActionCallback callback) { execute(callback, () -> dao.clear(guestId)); }
    @Override public void refresh(ActionCallback callback) { callback.onSuccess(); }

    private GuestCartItemEntity requireItem(String itemId) {
        GuestCartItemEntity value = dao.findItem(guestId, itemId);
        if (value == null) throw new IllegalStateException("NOT_FOUND");
        return value;
    }
    private boolean validQuantity(double quantity, ActionCallback callback) {
        if (!Double.isFinite(quantity) || quantity <= 0D) {
            callback.onError(ErrorCode.VALIDATION, "Số lượng phải là số dương."); return false;
        }
        return true;
    }
    private void execute(ActionCallback callback, Runnable action) {
        executor.execute(() -> {
            try { action.run(); callback.onSuccess(); }
            catch (IllegalStateException error) {
                callback.onError(ErrorCode.NOT_FOUND, "Không tìm thấy sản phẩm trong giỏ.");
            } catch (RuntimeException error) {
                callback.onError(ErrorCode.UNKNOWN, "Không thể cập nhật giỏ local.");
            }
        });
    }
    @Override public void close() { executor.shutdownNow(); }
}
