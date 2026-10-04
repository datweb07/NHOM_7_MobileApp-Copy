package com.rescuefarm.service.address;

public final class AddressDefaultPolicy {
    private AddressDefaultPolicy() { }

    public static boolean shouldMakeDefault(
            boolean requestedDefault,
            String existingDefaultId,
            String addressId
    ) {
        return requestedDefault
                || existingDefaultId == null
                || existingDefaultId.trim().isEmpty()
                || addressId.equals(existingDefaultId);
    }
}
