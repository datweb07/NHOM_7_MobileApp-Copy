package com.rescuefarm.domain.model;

import com.rescuefarm.domain.enums.UserRole;

public class Admin extends User {
    public Admin() { super(); }

    public Admin(String id, String email, String fullName) {
        super(id, email, fullName, UserRole.ADMIN);
    }
}
