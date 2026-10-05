package com.rescuefarm.domain.model;

import com.rescuefarm.domain.enums.ReviewStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Review {
    private String id;
    private String customerId;
    private String productId;
    private String orderId;
    private String orderItemId;
    private int rating;
    private String content;
    private List<String> imageUrls;
    private ReviewStatus status;
    private Date createdAt;

    public Review() {
        imageUrls = new ArrayList<>();
        status = ReviewStatus.PENDING;
    }

    public static Review create(String customerId,String productId,String orderId,String orderItemId,
            int rating,String content,List<String> imageUrls,Date now){
        Review v=new Review();v.customerId=required(customerId,"Customer");v.productId=required(productId,"Product");
        v.orderId=required(orderId,"Order");v.orderItemId=required(orderItemId,"Order item");
        v.id=key(v.customerId,v.orderId,v.orderItemId);v.rating=rating;v.content=clean(content);
        v.imageUrls=safeImages(imageUrls);v.status=ReviewStatus.PUBLISHED;v.createdAt=copy(now==null?new Date():now);
        if(!v.isValid())throw new IllegalArgumentException("Review rating/content/images are invalid");return v;
    }
    public static Review restore(String id,String customerId,String productId,String orderId,String orderItemId,
            int rating,String content,List<String> imageUrls,ReviewStatus status,Date createdAt){Review v=create(customerId,productId,orderId,orderItemId,rating,content,imageUrls,createdAt);v.id=required(id,"Review");v.status=status==null?ReviewStatus.PUBLISHED:status;return v;}
    public static String key(String customerId,String orderId,String orderItemId){return required(customerId,"Customer")+"_"+required(orderId,"Order")+"_"+required(orderItemId,"Order item");}

    public boolean isValid() { return rating >= 1 && rating <= 5 && !orderItemId.isEmpty()&&content.length()<=1000&&imageUrls.size()<=5; }
    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getProductId() { return productId; }
    public String getOrderId() { return orderId; }
    public String getOrderItemId() { return orderItemId; }
    public int getRating() { return rating; }
    public String getContent() { return content; }
    public List<String> getImageUrls() { return new ArrayList<>(imageUrls); }
    public ReviewStatus getStatus() { return status; }
    public Date getCreatedAt() { return copy(createdAt); }
    private static List<String> safeImages(List<String> values){List<String> out=new ArrayList<>();if(values!=null)for(String x:values)if(x!=null&&!x.trim().isEmpty())out.add(x.trim());return out;}
    private static String required(String v,String f){if(v==null||v.trim().isEmpty())throw new IllegalArgumentException(f+" id is required");return v.trim();}
    private static String clean(String v){return v==null?"":v.trim();}private static Date copy(Date v){return v==null?null:new Date(v.getTime());}
}
