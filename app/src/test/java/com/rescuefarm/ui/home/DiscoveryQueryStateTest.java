package com.rescuefarm.ui.home;

import static org.junit.Assert.assertEquals;
import androidx.lifecycle.SavedStateHandle;
import com.rescuefarm.domain.enums.RescueMode;
import com.rescuefarm.domain.enums.RescueReason;
import com.rescuefarm.domain.enums.UrgencyLevel;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class DiscoveryQueryStateTest {
    @Test public void restoresAllDiscoveryFiltersFromSavedState() {
        SavedStateHandle first = new SavedStateHandle();
        DiscoveryQuery expected = new DiscoveryQuery("rau", "vegetables", "Da Nang",
                RescueReason.OVER_SUPPLY, UrgencyLevel.CRITICAL, RescueMode.FIXED_POINT,
                DiscoveryQuery.Sort.DISTANCE);
        DiscoveryQueryState.save(first, expected);

        Map<String, Object> restoredValues = new HashMap<>();
        restoredValues.put("discovery.text", first.get("discovery.text"));
        restoredValues.put("discovery.category", first.get("discovery.category"));
        restoredValues.put("discovery.province", first.get("discovery.province"));
        restoredValues.put("discovery.reason", first.get("discovery.reason"));
        restoredValues.put("discovery.urgency", first.get("discovery.urgency"));
        restoredValues.put("discovery.mode", first.get("discovery.mode"));
        restoredValues.put("discovery.sort", first.get("discovery.sort"));
        SavedStateHandle recreated = new SavedStateHandle(restoredValues);
        DiscoveryQuery actual = DiscoveryQueryState.restore(recreated);

        assertEquals(expected.getText(), actual.getText());
        assertEquals(expected.getCategoryId(), actual.getCategoryId());
        assertEquals(expected.getProvince(), actual.getProvince());
        assertEquals(expected.getReason(), actual.getReason());
        assertEquals(expected.getUrgency(), actual.getUrgency());
        assertEquals(expected.getMode(), actual.getMode());
        assertEquals(expected.getSort(), actual.getSort());
    }
}
