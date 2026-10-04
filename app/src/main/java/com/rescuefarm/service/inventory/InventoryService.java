package com.rescuefarm.service.inventory;

import com.rescuefarm.domain.model.ProductBatch;
import com.rescuefarm.domain.model.RescueCampaign;
import java.util.Date;

public class InventoryService {

    public void validateAvailableStock(ProductBatch batch, double requestedQuantity) {
        validateAvailableStock(batch, requestedQuantity, new Date());
    }

    public void validateAvailableStock(ProductBatch batch, double requestedQuantity, Date now) {
        if (batch == null) { throw new IllegalArgumentException("Product batch is required"); }
        if (!batch.isSellable(now)) { throw new IllegalStateException("Expired or unavailable batch cannot be sold"); }
        if (!batch.hasAvailableStock(requestedQuantity)) { throw new IllegalStateException("Insufficient available stock"); }
    }

    public void reserveStock(ProductBatch batch, double quantity, Date now) {
        validateAvailableStock(batch, quantity, now);
        batch.reserveStock(quantity, now);
    }

    public void commitReservedStock(ProductBatch batch, double quantity) {
        batch.commitReservedStock(quantity);
    }

    public void releaseReservedStock(ProductBatch batch, double quantity) {
        batch.releaseReservedStock(quantity);
    }

    public void reserveStock(ProductBatch batch, RescueCampaign campaign, double quantity) {
        validateAvailableStock(batch, quantity);
        if (campaign != null && campaign.calculateRemainingQuantity() < quantity) {
            throw new IllegalStateException("Campaign target does not have enough remaining quantity");
        }
        batch.reserveStock(quantity);
        if (campaign != null) { campaign.reserveQuantity(quantity); }
    }

    public void commitReservedStock(ProductBatch batch, RescueCampaign campaign, double quantity) {
        if (batch.getReservedQuantity() < quantity) { throw new IllegalStateException("Batch does not have enough reserved quantity"); }
        if (campaign != null && campaign.getReservedQuantity() < quantity) { throw new IllegalStateException("Campaign does not have enough reserved quantity"); }
        batch.commitReservedStock(quantity);
        if (campaign != null) { campaign.commitRescue(quantity); }
    }

    public void releaseReservedStock(ProductBatch batch, RescueCampaign campaign, double quantity) {
        if (batch.getReservedQuantity() < quantity) { throw new IllegalStateException("Batch does not have enough reserved quantity"); }
        if (campaign != null && campaign.getReservedQuantity() < quantity) { throw new IllegalStateException("Campaign does not have enough reserved quantity"); }
        batch.releaseReservedStock(quantity);
        if (campaign != null) { campaign.releaseReservation(quantity); }
    }
}
