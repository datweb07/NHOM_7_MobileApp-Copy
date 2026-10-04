package com.rescuefarm.ui.product;

import com.rescuefarm.domain.model.Product;
import com.rescuefarm.domain.model.ProductBatch;

public final class ProductScreenState {
    public enum Status { IDLE, LOADING, PRODUCT, BATCH, SAVED, ERROR }
    private final Status status; private final Product product; private final ProductBatch batch;
    private final String message;
    private ProductScreenState(Status status, Product product, ProductBatch batch, String message) {
        this.status = status; this.product = product; this.batch = batch; this.message = message;
    }
    public static ProductScreenState idle() { return value(Status.IDLE, null); }
    public static ProductScreenState loading() { return value(Status.LOADING, null); }
    public static ProductScreenState product(Product product) {
        return new ProductScreenState(Status.PRODUCT, product, null, null);
    }
    public static ProductScreenState batch(ProductBatch batch) {
        return new ProductScreenState(Status.BATCH, null, batch, null);
    }
    public static ProductScreenState saved(String message) { return value(Status.SAVED, message); }
    public static ProductScreenState error(String message) { return value(Status.ERROR, message); }
    private static ProductScreenState value(Status status, String message) {
        return new ProductScreenState(status, null, null, message);
    }
    public Status getStatus() { return status; }
    public Product getProduct() { return product; }
    public ProductBatch getBatch() { return batch; }
    public String getMessage() { return message; }
}
