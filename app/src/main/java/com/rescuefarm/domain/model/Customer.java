package com.rescuefarm.domain.model;

import com.rescuefarm.domain.enums.CustomerType;
import com.rescuefarm.domain.enums.UserRole;

public class Customer extends User {
    private CustomerType customerType;
    private String companyName;
    private String taxCode;

    public Customer() { super(); }

    public Customer(String id, String email, String fullName, CustomerType customerType) {
        super(id, email, fullName, UserRole.CUSTOMER);
        this.customerType = customerType;
    }

    public void updateCustomerDetails(CustomerType customerType, String companyName, String taxCode) {
        this.customerType = customerType == null ? CustomerType.INDIVIDUAL : customerType;
        this.companyName = companyName == null ? "" : companyName.trim();
        this.taxCode = taxCode == null ? "" : taxCode.trim();
    }

    public CustomerType getCustomerType() { return customerType; }
    public String getCompanyName() { return companyName; }
    public String getTaxCode() { return taxCode; }
}
