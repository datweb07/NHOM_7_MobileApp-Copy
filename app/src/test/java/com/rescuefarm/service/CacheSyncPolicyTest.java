package com.rescuefarm.service;

import com.rescuefarm.service.sync.CacheSyncPolicy;
import com.rescuefarm.service.sync.CriticalActionPolicy;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import static org.junit.Assert.*;

public class CacheSyncPolicyTest {
    private final CacheSyncPolicy cache = new CacheSyncPolicy();

    @Test public void ttlControlsRefreshWithoutDiscardingStaleRead() {
        long now = 1_000_000L, expires = now + TimeUnit.MINUTES.toMillis(10);
        assertFalse(cache.shouldRefresh(now - 1L, expires, now, false));
        assertTrue(cache.shouldRefresh(now - 1L, expires, expires, false));
        assertTrue(cache.shouldRefresh(now, expires, now, true));
    }

    @Test public void hardExpiryIsSevenDays() {
        long now = TimeUnit.DAYS.toMillis(10);
        assertFalse(cache.isHardExpired(now - TimeUnit.DAYS.toMillis(7), now));
        assertTrue(cache.isHardExpired(now - TimeUnit.DAYS.toMillis(8), now));
    }

    @Test public void scopedOrderCacheUsesShortTtl() {
        assertEquals(TimeUnit.MINUTES.toMillis(10), cache.ttlMillis("orders:user-1"));
    }

    @Test public void onlyReadSyncMayBeQueuedOffline() {
        CriticalActionPolicy policy = new CriticalActionPolicy();
        assertTrue(policy.mayQueue(CriticalActionPolicy.Action.READ_SYNC));
        assertFalse(policy.mayQueue(CriticalActionPolicy.Action.CHECKOUT));
        assertFalse(policy.mayQueue(CriticalActionPolicy.Action.PAYMENT));
        assertFalse(policy.mayQueue(CriticalActionPolicy.Action.APPROVAL));
        assertFalse(policy.mayQueue(CriticalActionPolicy.Action.INVENTORY));
        try {
            policy.requireOnline(CriticalActionPolicy.Action.CHECKOUT, false);
            fail("Offline checkout must be blocked");
        } catch (IllegalStateException expected) {
            assertEquals("OFFLINE_CRITICAL_ACTION_BLOCKED", expected.getMessage());
        }
    }
}
