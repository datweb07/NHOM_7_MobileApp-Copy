package com.rescuefarm.service;
import static org.junit.Assert.*;import com.rescuefarm.domain.enums.BatchStatus;import com.rescuefarm.domain.model.ProductBatch;import com.rescuefarm.service.inventory.InventoryService;import java.util.Date;import org.junit.Test;
public class CheckoutReservationContractTest{
 @Test public void insufficientSecondBatch_meansNoExternalMutationBeforeCommit(){ProductBatch persistedA=batch("a",5);ProductBatch persistedB=batch("b",1);ProductBatch txA=batch("a",5);ProductBatch txB=batch("b",1);InventoryService service=new InventoryService();try{service.validateAvailableStock(txA,2,new Date(1000));service.validateAvailableStock(txB,2,new Date(1000));fail();}catch(IllegalStateException expected){}assertEquals(5,persistedA.getAvailableQuantity(),0);assertEquals(0,persistedA.getReservedQuantity(),0);assertEquals(1,persistedB.getAvailableQuantity(),0);}
 @Test public void pendingReservation_movesAvailableToReserved_only(){ProductBatch b=batch("a",5);new InventoryService().reserveStock(b,2,new Date(1000));assertEquals(3,b.getAvailableQuantity(),0);assertEquals(2,b.getReservedQuantity(),0);assertEquals(0,b.getSoldQuantity(),0);}
 private ProductBatch batch(String id,double q){return ProductBatch.restore(id,"p",null,new Date(0),new Date(2000),q,q,0,0,BatchStatus.AVAILABLE,1);}
}
