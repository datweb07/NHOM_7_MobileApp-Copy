package com.rescuefarm.service.inventory;

import com.rescuefarm.domain.model.ProductBatch;
import com.rescuefarm.domain.model.RescueCampaign;

public class InventoryService {

    public void validateAvailableStock(ProductBatch batch, double requestedQuantity) {
        if (batch == null) { throw new IllegalArgumentException("Product batch is required"); }
        if (!batch.hasAvailableStock(requestedQuantity)) { throw new IllegalStateException("Insufficient available stock"); }
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
