package com.rescuefarm.ui.order;

import com.rescuefarm.domain.model.Order;
import com.rescuefarm.domain.model.Payment;
import com.rescuefarm.domain.model.Shipment;

public final class OrderLifecycleState {
    public enum Status { IDLE, LOADING, SUCCESS, ERROR }
    private final Status status; private final Order order; private final Payment payment;
    private final Shipment shipment; private final String message; private final boolean replay;
    private OrderLifecycleState(Status status,Order order,Payment payment,Shipment shipment,String message,boolean replay){this.status=status;this.order=order;this.payment=payment;this.shipment=shipment;this.message=message;this.replay=replay;}
    public static OrderLifecycleState idle(){return new OrderLifecycleState(Status.IDLE,null,null,null,"",false);}
    public static OrderLifecycleState loading(){return new OrderLifecycleState(Status.LOADING,null,null,null,"Đang xử lý…",false);}
    public static OrderLifecycleState success(Order o,Payment p,Shipment s,boolean replay){return new OrderLifecycleState(Status.SUCCESS,o,p,s,replay?"Yêu cầu đã được xử lý trước đó.":"Đã cập nhật trạng thái đơn.",replay);}
    public static OrderLifecycleState error(String m){return new OrderLifecycleState(Status.ERROR,null,null,null,m==null?"Có lỗi xảy ra.":m,false);}
    public Status getStatus(){return status;} public Order getOrder(){return order;} public Payment getPayment(){return payment;} public Shipment getShipment(){return shipment;} public String getMessage(){return message;} public boolean isReplay(){return replay;}
}
