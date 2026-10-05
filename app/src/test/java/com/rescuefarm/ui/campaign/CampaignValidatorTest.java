package com.rescuefarm.ui.campaign;

import static org.junit.Assert.*;
import java.util.Map;
import org.junit.Test;

public class CampaignValidatorTest {
    @Test public void targets_areParsedAndSummable() {
        Map<String, Double> values = CampaignValidator.parseTargets("batch-a=20\nbatch-b=30.5");
        assertEquals(2, values.size());
        assertEquals(50.5, values.get("batch-a") + values.get("batch-b"), 0.000001);
    }
    @Test public void duplicatedBatch_isRejected() {
        assertTrue(CampaignValidator.parseTargets("batch-a=20;batch-a=10").isEmpty());
    }
    @Test public void invalidCoordinates_areRejected() {
        assertNotNull(CampaignValidator.validate("Campaign", "batch=10", "2027-01-01",
                "2027-01-02", "91", "106"));
    }
}
