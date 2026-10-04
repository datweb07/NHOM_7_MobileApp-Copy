package com.rescuefarm.domain.model;

import com.rescuefarm.domain.enums.PaymentMethod;
import com.rescuefarm.domain.enums.PaymentStatus;

import java.util.Date;

public class Payment {
    private String id;
    private String orderId;
    private PaymentMethod method;
    private PaymentStatus status;
    private double amount;
    private String referenceCode;
    private Date paidAt;

    public Payment() { status = PaymentStatus.UNPAID; }

    public void markPending() { status = PaymentStatus.PENDING; }

    public void markPaid(Date paymentTime) {
        if (status != PaymentStatus.PENDING && status != PaymentStatus.UNPAID) {
            throw new IllegalStateException("Only an unpaid or pending payment can be marked paid");
        }
        status = PaymentStatus.PAID;
        paidAt = paymentTime;
    }

    public void markFailed() {
        if (status == PaymentStatus.PAID) { throw new IllegalStateException("A paid payment cannot be marked failed"); }
        status = PaymentStatus.FAILED;
    }

    public String getId() { return id; }
    public String getOrderId() { return orderId; }
    public PaymentMethod getMethod() { return method; }
    public PaymentStatus getStatus() { return status; }
    public double getAmount() { return amount; }
    public String getReferenceCode() { return referenceCode; }
    public Date getPaidAt() { return paidAt; }
}
