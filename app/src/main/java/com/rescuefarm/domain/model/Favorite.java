package com.rescuefarm.domain.model;

import java.util.Date;

public class Favorite {
    private String id;
    private String customerId;
    private String productId;
    private Date createdAt;

    public Favorite() { }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getProductId() { return productId; }
    public Date getCreatedAt() { return createdAt; }
}
