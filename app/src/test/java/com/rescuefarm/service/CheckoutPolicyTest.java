package com.rescuefarm.service;
import static org.junit.Assert.*;import com.rescuefarm.domain.enums.*;import com.rescuefarm.domain.model.CartItem;import com.rescuefarm.service.order.*;import java.util.*;import org.junit.Test;
public class CheckoutPolicyTest{
 @Test(expected=IllegalArgumentException.class)public void multipleSellers_areRejected(){CheckoutPolicy.requireSingleSeller(Arrays.asList(new CartItem("1","p1","b1","s1",1,10),new CartItem("2","p2","b2","s2",1,10)));}
 @Test public void retryWithSameRequest_isIntentionalReplay(){CheckoutRequest request=request("request-12345678","owner");assertTrue(CheckoutIdempotencyPolicy.isReplay("request-12345678","owner",request));assertEquals(CheckoutPolicy.orderCode(request.requestId),CheckoutPolicy.orderCode(request.requestId));}
 @Test(expected=IllegalStateException.class)public void reusedKeyForAnotherOwner_isConflict(){CheckoutIdempotencyPolicy.isReplay("request-12345678","other",request("request-12345678","owner"));}
 private CheckoutRequest request(String id,String owner){return new CheckoutRequest(id,OrderOwnerType.GUEST,owner,"Guest","0900000000","",0,0,FulfillmentType.PICKUP,PaymentMethod.CASH_ON_SITE,"",Collections.singletonList(new CartItem("1","p","b","s",1,10)));}
}
