package com.rescuefarm.data.local.mapper;

import com.rescuefarm.data.local.entity.CategoryCacheEntity;
import com.rescuefarm.data.local.entity.ProductBatchCacheEntity;
import com.rescuefarm.data.local.entity.ProductCacheEntity;
import com.rescuefarm.domain.enums.BatchStatus;
import com.rescuefarm.domain.enums.ProductStatus;
import com.rescuefarm.domain.model.Category;
import com.rescuefarm.domain.model.Product;
import com.rescuefarm.domain.model.ProductBatch;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public final class CatalogCacheMapper {
    private static final String URL_SEPARATOR = "\u001F";
    private CatalogCacheMapper() { }

    public static CategoryCacheEntity toEntity(Category value, long cachedAt) {
        CategoryCacheEntity entity = new CategoryCacheEntity(value.getId());
        entity.name = value.getName(); entity.imageUrl = value.getImageUrl();
        entity.active = value.isActive(); entity.displayOrder = value.getDisplayOrder();
        entity.cachedAtEpochMillis = cachedAt; return entity;
    }

    public static Category toDomain(CategoryCacheEntity value) {
        return new Category(value.id, value.name, value.imageUrl, value.active, value.displayOrder);
    }

    public static ProductCacheEntity toEntity(Product value, long cachedAt) {
        ProductCacheEntity entity = new ProductCacheEntity(value.getId());
        entity.sellerId = value.getSellerId(); entity.categoryId = value.getCategoryId();
        entity.name = value.getName(); entity.description = value.getDescription();
        entity.originalPrice = value.getOriginalPrice(); entity.rescuePrice = value.getRescuePrice();
        entity.unit = value.getUnit(); entity.origin = value.getOrigin(); entity.province = value.getProvince();
        entity.imageUrlsSerialized = String.join(URL_SEPARATOR, value.getImageUrls());
        entity.averageRating = value.getAverageRating(); entity.reviewCount = value.getReviewCount();
        entity.status = value.getStatus().name(); entity.cachedAtEpochMillis = cachedAt; return entity;
    }

    public static Product toDomain(ProductCacheEntity value) {
        return Product.restore(value.id, value.sellerId, value.categoryId, value.name, value.description,
                value.originalPrice, value.rescuePrice, value.unit, value.origin, value.province,
                decodeUrls(value.imageUrlsSerialized), value.averageRating, value.reviewCount,
                enumValue(ProductStatus.class, value.status, ProductStatus.INACTIVE));
    }

    public static ProductBatchCacheEntity toEntity(ProductBatch value, long cachedAt) {
        ProductBatchCacheEntity entity = new ProductBatchCacheEntity(value.getId());
        entity.productId = value.getProductId(); entity.activeCampaignId = value.getActiveCampaignId();
        entity.harvestAtEpochMillis = epoch(value.getHarvestDate());
        entity.expiresAtEpochMillis = epoch(value.getExpiryDate());
        entity.initialQuantity = value.getInitialQuantity(); entity.availableQuantity = value.getAvailableQuantity();
        entity.reservedQuantity = value.getReservedQuantity(); entity.soldQuantity = value.getSoldQuantity();
        entity.status = value.getStatus().name(); entity.inventoryVersion = value.getInventoryVersion();
        entity.cachedAtEpochMillis = cachedAt; return entity;
    }

    public static ProductBatch toDomain(ProductBatchCacheEntity value) {
        return ProductBatch.restore(value.id, value.productId, value.activeCampaignId,
                date(value.harvestAtEpochMillis), date(value.expiresAtEpochMillis), value.initialQuantity,
                value.availableQuantity, value.reservedQuantity, value.soldQuantity,
                enumValue(BatchStatus.class, value.status, BatchStatus.AVAILABLE), value.inventoryVersion);
    }

    public static List<Product> products(List<ProductCacheEntity> values) {
        List<Product> result = new ArrayList<>();
        if (values != null) for (ProductCacheEntity value : values) result.add(toDomain(value));
        return result;
    }

    public static List<Category> categories(List<CategoryCacheEntity> values) {
        List<Category> result = new ArrayList<>();
        if (values != null) for (CategoryCacheEntity value : values) result.add(toDomain(value));
        return result;
    }

    public static List<ProductBatch> batches(List<ProductBatchCacheEntity> values) {
        List<ProductBatch> result = new ArrayList<>();
        if (values != null) for (ProductBatchCacheEntity value : values) result.add(toDomain(value));
        return result;
    }

    private static List<String> decodeUrls(String value) {
        if (value == null || value.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(value.split(Pattern.quote(URL_SEPARATOR))));
    }
    private static long epoch(Date value) { return value == null ? 0L : value.getTime(); }
    private static Date date(long value) { return value <= 0 ? null : new Date(value); }
    private static <T extends Enum<T>> T enumValue(Class<T> type, String value, T fallback) {
        try { return Enum.valueOf(type, value == null ? "" : value); }
        catch (IllegalArgumentException error) { return fallback; }
    }
}
