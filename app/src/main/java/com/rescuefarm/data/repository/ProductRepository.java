package com.rescuefarm.data.repository;

import androidx.lifecycle.LiveData;
import com.rescuefarm.domain.model.Category;
import com.rescuefarm.domain.model.Product;
import com.rescuefarm.domain.model.ProductBatch;
import java.util.List;

public interface ProductRepository {
    enum ErrorCode { NOT_CONFIGURED, NOT_FOUND, FORBIDDEN, VALIDATION, NETWORK, CONFLICT, UNKNOWN }
    enum StockMutation { RESERVE, COMMIT, RELEASE }

    interface ActionCallback {
        void onSuccess();
        void onError(ErrorCode error, String message);
    }
    interface ProductCallback {
        void onSuccess(Product product);
        void onError(ErrorCode error, String message);
    }
    interface BatchCallback {
        void onSuccess(ProductBatch batch);
        void onError(ErrorCode error, String message);
    }

    LiveData<List<Category>> observeCategories();
    LiveData<List<Product>> observeProducts();
    LiveData<List<Product>> observeSellerProducts(String sellerId);
    LiveData<List<ProductBatch>> observeBatches(String productId);
    void refreshCatalog(ActionCallback callback);
    void refreshSellerProducts(String sellerId, ActionCallback callback);
    void refreshBatches(String productId, ActionCallback callback);
    void getProduct(String productId, ProductCallback callback);
    void saveProduct(Product product, ProductCallback callback);
    void hideProduct(String productId, ActionCallback callback);
    void saveBatch(ProductBatch batch, long expectedVersion, BatchCallback callback);
    void deleteBatch(String batchId, long expectedVersion, ActionCallback callback);
    void mutateStock(String batchId, long expectedVersion, StockMutation mutation,
            double quantity, BatchCallback callback);
    default void close() { }
}
