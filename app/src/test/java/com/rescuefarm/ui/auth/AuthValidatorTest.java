package com.rescuefarm.ui.auth;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.rescuefarm.domain.enums.UserRole;

import org.junit.Test;

public class AuthValidatorTest {
    @Test
    public void validCustomerRegistration_hasNoValidationError() {
        assertNull(AuthValidator.validateRegistration(
                "customer@example.com",
                "strongPass123",
                "Nguyen Van A",
                "0912345678",
                UserRole.CUSTOMER
        ));
    }

    @Test
    public void invalidEmail_isRejected() {
        assertNotNull(AuthValidator.validateLogin("not-an-email", "password"));
    }

    @Test
    public void adminSelfRegistration_isRejected() {
        assertNotNull(AuthValidator.validateRegistration(
                "admin@example.com",
                "strongPass123",
                "Admin User",
                "0912345678",
                UserRole.ADMIN
        ));
    }
}
