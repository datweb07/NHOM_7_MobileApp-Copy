package com.rescuefarm.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.rescuefarm.domain.enums.OrderOwnerType;
import com.rescuefarm.domain.enums.UserRole;
import com.rescuefarm.domain.model.Admin;
import com.rescuefarm.domain.model.Customer;
import com.rescuefarm.domain.model.Seller;
import com.rescuefarm.domain.model.User;
import org.junit.Test;

public class ActorContractTest {
    @Test public void systemHasThreeAuthenticatedRolesAndGuestIsNotAUser() {
        assertEquals(3, UserRole.values().length);
        assertEquals(2, OrderOwnerType.values().length);
        assertFalse(hasRole("GUEST"));
        assertTrue(hasOwnerType(OrderOwnerType.GUEST));
        assertTrue(hasOwnerType(OrderOwnerType.CUSTOMER));

        assertTrue(User.class.isAssignableFrom(Customer.class));
        assertTrue(User.class.isAssignableFrom(Seller.class));
        assertTrue(User.class.isAssignableFrom(Admin.class));
    }

    private boolean hasRole(String role) {
        for (UserRole value : UserRole.values()) if (value.name().equals(role)) return true;
        return false;
    }

    private boolean hasOwnerType(OrderOwnerType type) {
        for (OrderOwnerType value : OrderOwnerType.values()) if (value == type) return true;
        return false;
    }
}
