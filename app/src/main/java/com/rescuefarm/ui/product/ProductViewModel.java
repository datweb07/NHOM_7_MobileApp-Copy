package com.rescuefarm.ui.product;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.rescuefarm.data.repository.AuthRepository;
import com.rescuefarm.data.repository.ProductRepository;
import com.rescuefarm.domain.enums.ProductStatus;
import com.rescuefarm.domain.model.Category;
import com.rescuefarm.domain.model.Product;
import com.rescuefarm.domain.model.ProductBatch;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ProductViewModel extends ViewModel {
    private final ProductRepository repository; private final AuthRepository authRepository;
    private final MutableLiveData<ProductScreenState> state =
            new MutableLiveData<>(ProductScreenState.idle());
    public ProductViewModel(ProductRepository repository, AuthRepository authRepository) {
        this.repository = repository; this.authRepository = authRepository;
    }

    public LiveData<ProductScreenState> getState() { return state; }
    public LiveData<List<Category>> getCategories() { return repository.observeCategories(); }
    public LiveData<List<Product>> getProducts() { return repository.observeProducts(); }
    public LiveData<List<Product>> getSellerProducts() {
        return repository.observeSellerProducts(currentUserId());
    }
    public LiveData<List<ProductBatch>> getBatches(String productId) {
        return repository.observeBatches(productId);
    }
    public String currentUserId() {
        String value = authRepository.getCurrentUserId(); return value == null ? "" : value;
    }

    public void refreshCatalog() { repository.refreshCatalog(actionCallback(null)); }
    public void refreshSellerProducts() {
        String sellerId = requireUserId(); if (sellerId != null) repository.refreshSellerProducts(sellerId, actionCallback(null));
    }
    public void refreshBatches(String productId) { repository.refreshBatches(productId, actionCallback(null)); }
    public void loadProduct(String productId) {
        state.setValue(ProductScreenState.loading());
        repository.getProduct(productId, new ProductRepository.ProductCallback() {
            @Override public void onSuccess(Product product) { state.postValue(ProductScreenState.product(product)); }
            @Override public void onError(ProductRepository.ErrorCode error, String message) {
                state.postValue(ProductScreenState.error(message));
            }
        });
    }

    public void saveProduct(String id, String categoryId, String name, String description,
            String originalPrice, String rescuePrice, String unit, String origin, String province,
            String imageUrls, ProductStatus status) {
        String validation = ProductValidator.validateProduct(categoryId, name, originalPrice, rescuePrice, unit);
        if (validation != null) { state.setValue(ProductScreenState.error(validation)); return; }
        String sellerId = requireUserId(); if (sellerId == null) return;
        Product product = new Product(id, sellerId, categoryId, name, description,
                ProductValidator.parseNumber(originalPrice), ProductValidator.parseNumber(rescuePrice), unit,
                origin, province, parseUrls(imageUrls), status);
        state.setValue(ProductScreenState.loading());
        repository.saveProduct(product, new ProductRepository.ProductCallback() {
            @Override public void onSuccess(Product value) {
                state.postValue(ProductScreenState.saved("Đã lưu sản phẩm."));
            }
            @Override public void onError(ProductRepository.ErrorCode error, String message) {
                state.postValue(ProductScreenState.error(message));
            }
        });
    }

    public void hideProduct(String productId) {
        state.setValue(ProductScreenState.loading());
        repository.hideProduct(productId, actionCallback("Đã ẩn sản phẩm."));
    }

    public void saveBatch(String id, String productId, String harvestDate, String expiryDate,
            String initialQuantity, long expectedVersion) {
        Date now = new Date();
        String validation = ProductValidator.validateBatch(harvestDate, expiryDate, initialQuantity, now);
        if (validation != null) { state.setValue(ProductScreenState.error(validation)); return; }
        ProductBatch batch = new ProductBatch(id, productId, ProductValidator.parseNumber(initialQuantity));
        batch.updateDetails(ProductValidator.parseDate(harvestDate), ProductValidator.parseDate(expiryDate),
                batch.getInitialQuantity(), now);
        state.setValue(ProductScreenState.loading());
        repository.saveBatch(batch, expectedVersion, new ProductRepository.BatchCallback() {
            @Override public void onSuccess(ProductBatch value) {
                state.postValue(ProductScreenState.saved("Đã lưu batch."));
            }
            @Override public void onError(ProductRepository.ErrorCode error, String message) {
                state.postValue(ProductScreenState.error(message));
            }
        });
    }

    public void deleteBatch(String batchId, long expectedVersion) {
        state.setValue(ProductScreenState.loading());
        repository.deleteBatch(batchId, expectedVersion, actionCallback("Đã xóa batch chưa phát sinh tồn kho."));
    }

    private ProductRepository.ActionCallback actionCallback(String successMessage) {
        return new ProductRepository.ActionCallback() {
            @Override public void onSuccess() {
                if (successMessage != null) state.postValue(ProductScreenState.saved(successMessage));
            }
            @Override public void onError(ProductRepository.ErrorCode error, String message) {
                state.postValue(ProductScreenState.error(message));
            }
        };
    }
    private String requireUserId() {
        String value = currentUserId();
        if (!authRepository.isAuthenticated() || value.isEmpty()) {
            state.setValue(ProductScreenState.error("Vui lòng đăng nhập seller.")); return null;
        }
        return value;
    }
    private List<String> parseUrls(String value) {
        List<String> result = new ArrayList<>();
        if (value == null) return result;
        for (String item : Arrays.asList(value.split("[,\\n]"))) {
            String clean = item.trim(); if (!clean.isEmpty()) result.add(clean);
        }
        return result;
    }
    @Override protected void onCleared() { repository.close(); super.onCleared(); }
}
