package com.rescuefarm.ui.profile;

import org.junit.Test;
import static org.junit.Assert.*;

public class ProfileValidatorTest {
    @Test public void validAddress_passes() {
        assertNull(ProfileValidator.validateAddress("Nguyễn An", "0901234567", "TP.HCM",
                "Quận 1", "Bến Nghé", "12 Lê Lợi"));
    }

    @Test public void invalidPhone_fails() {
        assertNotNull(ProfileValidator.validateProfile("Nguyễn An", "abc"));
    }

    @Test public void proofImage_requiresHttps() {
        assertNotNull(ProfileValidator.validateSellerApplication("Nguyễn An", "Nông trại An",
                "12 Lê Lợi, TP.HCM", "http://example.com/proof.jpg"));
    }
}
