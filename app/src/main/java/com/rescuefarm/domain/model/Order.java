package com.rescuefarm.domain.model;

import com.rescuefarm.domain.enums.FulfillmentType;
import com.rescuefarm.domain.enums.OrderOwnerType;
import com.rescuefarm.domain.enums.OrderStatus;
import com.rescuefarm.domain.enums.PaymentMethod;
import com.rescuefarm.domain.enums.PaymentStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    private String id;
    private OrderOwnerType ownerType;
    private String ownerId;
    private String orderCode;
    private String sellerId;
    private String campaignId;
    private FulfillmentType fulfillmentType;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private double receiverLatitude;
    private double receiverLongitude;
    private double subtotal;
    private double quantityDiscount;
    private double shippingFee;
    private double totalAmount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private OrderStatus status;
    private String note;
    private Date createdAt;
    private Date updatedAt;
    private List<OrderItem> items;

    public Order() {
        items = new ArrayList<>();
        status = OrderStatus.PENDING;
        paymentStatus = PaymentStatus.UNPAID;
    }

    public double calculateTotal() { return Math.max(0.0, subtotal - quantityDiscount + shippingFee); }
    public boolean canCancel() { return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED || status == OrderStatus.PREPARING; }
    public boolean isDeliveryRequired() { return fulfillmentType == FulfillmentType.DELIVERY; }
    public String getId() { return id; }
    public OrderOwnerType getOwnerType() { return ownerType; }
    public String getOwnerId() { return ownerId; }
    public String getOrderCode() { return orderCode; }
    public String getSellerId() { return sellerId; }
    public String getCampaignId() { return campaignId; }
    public FulfillmentType getFulfillmentType() { return fulfillmentType; }
    public String getReceiverName() { return receiverName; }
    public String getReceiverPhone() { return receiverPhone; }
    public String getReceiverAddress() { return receiverAddress; }
    public double getReceiverLatitude() { return receiverLatitude; }
    public double getReceiverLongitude() { return receiverLongitude; }
    public double getSubtotal() { return subtotal; }
    public double getQuantityDiscount() { return quantityDiscount; }
    public double getShippingFee() { return shippingFee; }
    public double getTotalAmount() { return totalAmount; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public OrderStatus getStatus() { return status; }
    public String getNote() { return note; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public List<OrderItem> getItems() { return new ArrayList<>(items); }
}
