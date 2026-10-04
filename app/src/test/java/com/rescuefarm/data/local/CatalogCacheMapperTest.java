package com.rescuefarm.data.local;

import com.rescuefarm.data.local.entity.ProductBatchCacheEntity;
import com.rescuefarm.data.local.entity.ProductCacheEntity;
import com.rescuefarm.data.local.mapper.CatalogCacheMapper;
import com.rescuefarm.domain.enums.ProductStatus;
import com.rescuefarm.domain.model.Product;
import com.rescuefarm.domain.model.ProductBatch;
import java.util.Arrays;
import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;

public class CatalogCacheMapperTest {
    @Test public void productRoundTrip_preservesCatalogFields() {
        Product source = Product.restore("p", "s", "c", "Xoài", "Ngọt", 30_000, 20_000,
                "kg", "Cao Lãnh", "Đồng Tháp", Arrays.asList("https://a", "https://b"),
                4.5, 8, ProductStatus.ACTIVE);
        ProductCacheEntity entity = CatalogCacheMapper.toEntity(source, 123);
        Product restored = CatalogCacheMapper.toDomain(entity);
        assertEquals(source.getDescription(), restored.getDescription());
        assertEquals(source.getImageUrls(), restored.getImageUrls());
        assertEquals(4.5, restored.getAverageRating(), 0.000001);
    }

    @Test public void batchRoundTrip_preservesVersionAndInvariant() {
        ProductBatch source = new ProductBatch("b", "p", 20);
        source.updateDetails(new Date(1_000), new Date(5_000), 20, new Date(2_000));
        ProductBatchCacheEntity entity = CatalogCacheMapper.toEntity(source, 123);
        entity.inventoryVersion = 7;
        ProductBatch restored = CatalogCacheMapper.toDomain(entity);
        assertEquals(7, restored.getInventoryVersion());
        restored.verifyInventoryInvariant();
    }
}
