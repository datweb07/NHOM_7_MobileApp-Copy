package com.rescuefarm.data.remote.firebase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.rescuefarm.data.repository.OrderRepository;
import com.rescuefarm.domain.enums.BatchStatus;
import com.rescuefarm.domain.enums.CampaignStatus;
import com.rescuefarm.domain.enums.FulfillmentType;
import com.rescuefarm.domain.enums.OrderOwnerType;
import com.rescuefarm.domain.enums.OrderStatus;
import com.rescuefarm.domain.enums.PaymentMethod;
import com.rescuefarm.domain.enums.PaymentStatus;
import com.rescuefarm.domain.enums.ProductStatus;
import com.rescuefarm.domain.enums.PromotionType;
import com.rescuefarm.domain.model.CartItem;
import com.rescuefarm.domain.model.Order;
import com.rescuefarm.domain.model.OrderItem;
import com.rescuefarm.domain.model.Product;
import com.rescuefarm.domain.model.ProductBatch;
import com.rescuefarm.domain.model.Promotion;
import com.rescuefarm.service.inventory.InventoryService;
import com.rescuefarm.service.network.NetworkStatusProvider;
import com.rescuefarm.service.order.CheckoutPolicy;
import com.rescuefarm.service.order.CheckoutIdempotencyPolicy;
import com.rescuefarm.service.order.CheckoutRequest;
import com.rescuefarm.service.pricing.PricingService;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FirebaseOrderRepository implements OrderRepository {
    private static final String ORDERS="orders", ITEMS="items", PRODUCTS="products",
            BATCHES="productBatches", CAMPAIGNS="campaigns", PROMOTIONS="promotions";
    private final FirebaseFirestore db=FirebaseFirestore.getInstance();
    private final FirebaseAuth auth=FirebaseAuth.getInstance(); private final NetworkStatusProvider network;
    private final MutableLiveData<List<Order>> orders=new MutableLiveData<>(new ArrayList<>());
    private final PricingService pricing=new PricingService(); private final InventoryService inventory=new InventoryService();
    public FirebaseOrderRepository(NetworkStatusProvider network){this.network=network;}
    @Override public LiveData<List<Order>> observeOrders(String ownerId){return orders;}

    @Override public void checkout(CheckoutRequest request, OrderCallback callback) {
        if(!network.isOnline()){callback.onError(ErrorCode.NETWORK,"Checkout cần kết nối mạng.");return;}
        final String sellerId;
        try { sellerId=CheckoutPolicy.requireSingleSeller(request.items); validateRequest(request); }
        catch(IllegalArgumentException e){callback.onError("MULTI_SELLER".equals(e.getMessage())?ErrorCode.MULTI_SELLER:ErrorCode.VALIDATION,
                "MULTI_SELLER".equals(e.getMessage())?"Mỗi Order chỉ được chứa sản phẩm của một seller.":e.getMessage());return;}
        DocumentReference orderRef=db.collection(ORDERS).document(request.requestId);
        db.runTransaction(tx->{
            DocumentSnapshot existing=tx.get(orderRef);
            if(existing.exists()){
                CheckoutIdempotencyPolicy.isReplay(existing.getString("requestId"),existing.getString("ownerId"),request);
                return new Result(mapOrder(existing,new ArrayList<>()),true);
            }
            Map<String,DocumentSnapshot> productDocs=new HashMap<>(),batchDocs=new HashMap<>(),promotionDocs=new HashMap<>();
            for(CartItem item:request.items){
                DocumentSnapshot product=tx.get(db.collection(PRODUCTS).document(item.getProductId()));
                DocumentSnapshot batch=tx.get(db.collection(BATCHES).document(item.getBatchId()));
                DocumentSnapshot promotion=tx.get(db.collection(PROMOTIONS).document(item.getProductId()));
                productDocs.put(item.getId(),product);batchDocs.put(item.getId(),batch);promotionDocs.put(item.getId(),promotion);
            }
            Set<String> campaignIds=new HashSet<>();
            for(DocumentSnapshot batch:batchDocs.values()){
                String campaign=clean(batch.getString("activeCampaignId")); if(!campaign.isEmpty())campaignIds.add(campaign);
            }
            if(campaignIds.size()>1)throw new IllegalStateException("CAMPAIGN_CONFLICT");
            String campaignId=campaignIds.isEmpty()?"":campaignIds.iterator().next();
            DocumentSnapshot campaignDoc=campaignId.isEmpty()?null:tx.get(db.collection(CAMPAIGNS).document(campaignId));
            double campaignReserve=0D,subtotal=0D; List<OrderItem> snapshots=new ArrayList<>(); Date now=new Date();
            Map<String,ProductBatch> batches=new HashMap<>();
            for(CartItem cartItem:request.items){
                DocumentSnapshot pDoc=productDocs.get(cartItem.getId()),bDoc=batchDocs.get(cartItem.getId());
                Product product=requireProduct(pDoc,sellerId); ProductBatch batch=requireBatch(bDoc,product.getId());
                Promotion promotion=mapPromotion(promotionDocs.get(cartItem.getId()));
                inventory.validateAvailableStock(batch,cartItem.getQuantity(),now);
                double finalPrice=pricing.calculatePrice(product.getOriginalPrice(),product.getRescuePrice(),
                        cartItem.getQuantity(),promotion,now).getFinalUnitPrice();
                String image=product.getImageUrls().isEmpty()?"":product.getImageUrls().get(0);
                OrderItem snapshot=OrderItem.snapshot(cartItem.getBatchId(),request.requestId,product.getId(),
                        batch.getId(),product.getName(),image,product.getUnit(),product.getOriginalPrice(),
                        product.getRescuePrice(),finalPrice,cartItem.getQuantity());
                snapshots.add(snapshot);subtotal+=snapshot.getSubtotal();inventory.reserveStock(batch,cartItem.getQuantity(),now);
                batches.put(batch.getId(),batch);if(!clean(batch.getActiveCampaignId()).isEmpty())campaignReserve+=cartItem.getQuantity();
            }
            if(campaignDoc!=null){
                if(!campaignDoc.exists()||!sellerId.equals(campaignDoc.getString("sellerId"))
                        || !CampaignStatus.ACTIVE.name().equals(campaignDoc.getString("status")))throw new IllegalStateException("CAMPAIGN_CONFLICT");
                Object rawTargets=campaignDoc.get("batchTargets");
                if(!(rawTargets instanceof Map<?,?>))throw new IllegalStateException("CAMPAIGN_CONFLICT");
                for(ProductBatch batch:batches.values())if(!clean(batch.getActiveCampaignId()).isEmpty()){
                    Object target=((Map<?,?>)rawTargets).get(batch.getId());
                    double requested=snapshots.stream().filter(v->v.getBatchId().equals(batch.getId())).findFirst().get().getQuantity();
                    if(!(target instanceof Number)||((Number)target).doubleValue()+0.000001<requested)
                        throw new IllegalStateException("CAMPAIGN_CONFLICT");
                }
                double remaining=number(campaignDoc,"targetQuantity")-number(campaignDoc,"reservedQuantity")-number(campaignDoc,"rescuedQuantity");
                if(remaining+0.000001<campaignReserve)throw new IllegalStateException("CAMPAIGN_CONFLICT");
            }
            PaymentStatus paymentStatus=request.paymentMethod==PaymentMethod.BANK_TRANSFER?PaymentStatus.PENDING:PaymentStatus.UNPAID;
            Order order=Order.create(request.requestId,request.ownerType,request.ownerId,
                    CheckoutPolicy.orderCode(request.requestId),sellerId,campaignId,request.fulfillmentType,
                    request.receiverName,request.receiverPhone,request.receiverAddress,request.latitude,request.longitude,
                    subtotal,0D,0D,request.paymentMethod,paymentStatus,request.note,snapshots,now);
            Map<String,Object> orderData=orderMap(order);orderData.put("requestId",request.requestId);
            orderData.put("campaignReservedQuantity",campaignReserve);
            Map<String,Double> reservations=new HashMap<>();for(OrderItem item:snapshots)reservations.put(item.getBatchId(),item.getQuantity());
            orderData.put("batchReservations",reservations);
            orderData.put("createdAt",FieldValue.serverTimestamp());orderData.put("updatedAt",FieldValue.serverTimestamp());tx.set(orderRef,orderData);
            for(OrderItem item:snapshots)tx.set(orderRef.collection(ITEMS).document(item.getId()),itemMap(item));
            for(Map.Entry<String,ProductBatch> entry:batches.entrySet()){
                DocumentSnapshot before=batchDocs.values().stream().filter(v->v.getId().equals(entry.getKey())).findFirst().orElseThrow();
                ProductBatch batch=entry.getValue(); Map<String,Object> updates=new HashMap<>();
                updates.put("availableQuantity",batch.getAvailableQuantity());updates.put("reservedQuantity",batch.getReservedQuantity());
                updates.put("status",batch.getStatus().name());updates.put("inventoryVersion",longValue(before,"inventoryVersion")+1L);
                updates.put("lastReservationOrderId",request.requestId);updates.put("lastReservationQuantity",
                        snapshots.stream().filter(v->v.getBatchId().equals(batch.getId())).findFirst().get().getQuantity());
                updates.put("updatedAt",FieldValue.serverTimestamp());tx.update(before.getReference(),updates);
            }
            if(campaignDoc!=null)tx.update(campaignDoc.getReference(),"reservedQuantity",
                    number(campaignDoc,"reservedQuantity")+campaignReserve,"lastReservationOrderId",request.requestId,
                    "lastReservationQuantity",campaignReserve,"updatedAt",FieldValue.serverTimestamp());
            return new Result(order,false);
        }).addOnSuccessListener(r->callback.onSuccess(r.order,r.replay))
                .addOnFailureListener(e->failure(e,callback));
    }

    @Override public void refreshOrders(String ownerId, ActionCallback callback){
        if(!network.isOnline()){callback.onError(ErrorCode.NETWORK,"Không thể tải đơn khi offline.");return;}
        db.collection(ORDERS).whereEqualTo("ownerId",ownerId).get().addOnSuccessListener(s->{
            List<Order> values=new ArrayList<>();for(DocumentSnapshot d:s.getDocuments())values.add(mapOrder(d,new ArrayList<>()));
            orders.setValue(values);callback.onSuccess();}).addOnFailureListener(e->failure(e,callback));
    }
    @Override public void findGuestOrder(String code,String phone,OrderCallback callback){
        if(!network.isOnline()){callback.onError(ErrorCode.NETWORK,"Tra cứu đơn cần kết nối mạng.");return;}
        db.collection(ORDERS).whereEqualTo("orderCode",clean(code).toUpperCase()).whereEqualTo("receiverPhone",clean(phone)).limit(1).get()
                .addOnSuccessListener(s->{if(s.isEmpty())callback.onError(ErrorCode.NOT_FOUND,"Không tìm thấy đơn.");
                    else callback.onSuccess(mapOrder(s.getDocuments().get(0),new ArrayList<>()),false);})
                .addOnFailureListener(e->failure(e,callback));
    }

    private void validateRequest(CheckoutRequest r){if(r==null||clean(r.requestId).isEmpty()||r.ownerType==null||clean(r.ownerId).isEmpty()
            ||clean(r.receiverName).length()<2||clean(r.receiverPhone).length()<9||r.fulfillmentType==null||r.paymentMethod==null)
        throw new IllegalArgumentException("Thông tin checkout chưa hợp lệ.");
        String uid=auth.getCurrentUser()==null?"":auth.getCurrentUser().getUid();
        if(r.ownerType==OrderOwnerType.CUSTOMER&&!r.ownerId.equals(uid))throw new IllegalArgumentException("Customer session không hợp lệ.");}
    private Product requireProduct(DocumentSnapshot d,String seller){if(!d.exists()||!seller.equals(d.getString("sellerId"))
            ||!ProductStatus.ACTIVE.name().equals(d.getString("status")))throw new IllegalStateException("PRODUCT_UNAVAILABLE");
        List<String> images=new ArrayList<>();Object raw=d.get("imageUrls");if(raw instanceof List<?>)for(Object x:(List<?>)raw)if(x instanceof String)images.add((String)x);
        return Product.restore(d.getId(),d.getString("sellerId"),d.getString("categoryId"),d.getString("name"),d.getString("description"),
                number(d,"originalPrice"),number(d,"rescuePrice"),d.getString("unit"),d.getString("origin"),d.getString("province"),images,
                number(d,"averageRating"),(int)longValue(d,"reviewCount"),ProductStatus.ACTIVE);}
    private ProductBatch requireBatch(DocumentSnapshot d,String productId){if(!d.exists()||!productId.equals(d.getString("productId")))throw new IllegalStateException("BATCH_UNAVAILABLE");
        return ProductBatch.restore(d.getId(),productId,d.getString("activeCampaignId"),date(d,"harvestDate"),date(d,"expiryDate"),number(d,"initialQuantity"),
                number(d,"availableQuantity"),number(d,"reservedQuantity"),number(d,"soldQuantity"),enumValue(BatchStatus.class,d.getString("status"),BatchStatus.AVAILABLE),longValue(d,"inventoryVersion"));}
    private Promotion mapPromotion(DocumentSnapshot d){if(d==null||!d.exists())return null;try{Map<String,Double> tiers=new HashMap<>();Object raw=d.get("quantityDiscountTiers");
        if(raw instanceof Map<?,?>)for(Map.Entry<?,?> e:((Map<?,?>)raw).entrySet())if(e.getKey() instanceof String&&e.getValue() instanceof Number)tiers.put((String)e.getKey(),((Number)e.getValue()).doubleValue());
        return Promotion.restore(d.getId(),d.getString("sellerId"),d.getString("productId"),enumValue(PromotionType.class,d.getString("type"),null),number(d,"value"),tiers,date(d,"startDate"),date(d,"endDate"),Boolean.TRUE.equals(d.getBoolean("active")));}catch(RuntimeException e){return null;}}
    private Map<String,Object> orderMap(Order o){Map<String,Object> m=new HashMap<>();m.put("id",o.getId());m.put("orderCode",o.getOrderCode());m.put("ownerType",o.getOwnerType().name());m.put("ownerId",o.getOwnerId());m.put("sellerId",o.getSellerId());m.put("campaignId",o.getCampaignId());m.put("fulfillmentType",o.getFulfillmentType().name());m.put("receiverName",o.getReceiverName());m.put("receiverPhone",o.getReceiverPhone());m.put("receiverAddress",o.getReceiverAddress());m.put("receiverLatitude",o.getReceiverLatitude());m.put("receiverLongitude",o.getReceiverLongitude());m.put("subtotal",o.getSubtotal());m.put("quantityDiscount",o.getQuantityDiscount());m.put("shippingFee",o.getShippingFee());m.put("totalAmount",o.getTotalAmount());m.put("paymentMethod",o.getPaymentMethod().name());m.put("paymentStatus",o.getPaymentStatus().name());m.put("status",o.getStatus().name());m.put("note",o.getNote());return m;}
    private Map<String,Object> itemMap(OrderItem i){Map<String,Object> m=new HashMap<>();m.put("id",i.getId());m.put("orderId",i.getOrderId());m.put("productId",i.getProductId());m.put("batchId",i.getBatchId());m.put("productNameSnapshot",i.getProductNameSnapshot());m.put("productImageSnapshot",i.getProductImageSnapshot());m.put("unitSnapshot",i.getUnitSnapshot());m.put("originalPriceSnapshot",i.getOriginalPriceSnapshot());m.put("rescuePriceSnapshot",i.getRescuePriceSnapshot());m.put("finalPriceSnapshot",i.getFinalPriceSnapshot());m.put("quantity",i.getQuantity());m.put("subtotal",i.getSubtotal());return m;}
    private Order mapOrder(DocumentSnapshot d,List<OrderItem> items){return Order.restore(d.getId(),enumValue(OrderOwnerType.class,d.getString("ownerType"),OrderOwnerType.GUEST),d.getString("ownerId"),d.getString("orderCode"),d.getString("sellerId"),d.getString("campaignId"),enumValue(FulfillmentType.class,d.getString("fulfillmentType"),FulfillmentType.PICKUP),d.getString("receiverName"),d.getString("receiverPhone"),d.getString("receiverAddress"),number(d,"receiverLatitude"),number(d,"receiverLongitude"),number(d,"subtotal"),number(d,"quantityDiscount"),number(d,"shippingFee"),number(d,"totalAmount"),enumValue(PaymentMethod.class,d.getString("paymentMethod"),PaymentMethod.COD),enumValue(PaymentStatus.class,d.getString("paymentStatus"),PaymentStatus.UNPAID),enumValue(OrderStatus.class,d.getString("status"),OrderStatus.PENDING),d.getString("note"),date(d,"createdAt"),date(d,"updatedAt"),items);}
    private void failure(Exception e,OrderCallback c){String m=e.getMessage();if(m!=null&&(m.contains("Insufficient")||m.contains("unavailable")||m.contains("BATCH")||m.contains("PRODUCT"))){c.onError(ErrorCode.INSUFFICIENT_STOCK,"Tồn kho đã thay đổi hoặc batch không còn bán.");return;}if("CAMPAIGN_CONFLICT".equals(m)){c.onError(ErrorCode.CAMPAIGN_CONFLICT,"Campaign không còn đủ số lượng hoặc không hợp lệ.");return;}if("IDEMPOTENCY_CONFLICT".equals(m)){c.onError(ErrorCode.CONFLICT,"Idempotency key đã được dùng cho request khác.");return;}if(e instanceof FirebaseFirestoreException&&((FirebaseFirestoreException)e).getCode()==FirebaseFirestoreException.Code.PERMISSION_DENIED){c.onError(ErrorCode.FORBIDDEN,"Firestore Rules từ chối checkout.");return;}c.onError(ErrorCode.UNKNOWN,"Không thể tạo đơn; transaction đã rollback.");}
    private void failure(Exception e,ActionCallback c){if(e instanceof FirebaseFirestoreException&&((FirebaseFirestoreException)e).getCode()==FirebaseFirestoreException.Code.PERMISSION_DENIED)c.onError(ErrorCode.FORBIDDEN,"Không có quyền đọc đơn.");else c.onError(ErrorCode.UNKNOWN,"Không thể tải đơn.");}
    private static double number(DocumentSnapshot d,String f){Double v=d.getDouble(f);return v==null?0D:v;}private static long longValue(DocumentSnapshot d,String f){Long v=d.getLong(f);return v==null?0L:v;}private static Date date(DocumentSnapshot d,String f){Timestamp v=d.getTimestamp(f);return v==null?null:v.toDate();}private static String clean(String v){return v==null?"":v.trim();}private static <T extends Enum<T>>T enumValue(Class<T> t,String v,T fallback){try{return Enum.valueOf(t,v==null?"":v);}catch(Exception e){return fallback;}}
    private static final class Result{final Order order;final boolean replay;Result(Order o,boolean r){order=o;replay=r;}}
}
