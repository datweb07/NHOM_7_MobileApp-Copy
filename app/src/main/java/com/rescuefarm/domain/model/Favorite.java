package com.rescuefarm.domain.model;

import java.util.Date;

public class Favorite {
    private String id;
    private String customerId;
    private String productId;
    private Date createdAt;

    public Favorite() { }

    public static Favorite create(String customerId,String productId,Date now){Favorite v=new Favorite();v.customerId=required(customerId);v.productId=required(productId);v.id=key(v.customerId,v.productId);v.createdAt=copy(now==null?new Date():now);return v;}
    public static Favorite restore(String id,String customerId,String productId,Date createdAt){Favorite v=create(customerId,productId,createdAt);if(id!=null&&!id.trim().isEmpty())v.id=id.trim();return v;}
    public static String key(String customerId,String productId){return required(customerId)+"_"+required(productId);}

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getProductId() { return productId; }
    public Date getCreatedAt() { return copy(createdAt); }
    private static String required(String v){if(v==null||v.trim().isEmpty())throw new IllegalArgumentException("Favorite id is required");return v.trim();}
    private static Date copy(Date v){return v==null?null:new Date(v.getTime());}
}
