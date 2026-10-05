package com.rescuefarm.service.feed;

import com.rescuefarm.domain.enums.ReactionType;

public final class ReactionPolicy {
    private ReactionPolicy() { }
    public static String documentIdForUser(String userId) {
        String value = userId == null ? "" : userId.trim();
        if (value.isEmpty()) throw new IllegalArgumentException("User is required for reaction");
        return value;
    }
    public static boolean shouldRemove(ReactionType existingType, ReactionType requestedType) {
        if (requestedType == null) throw new IllegalArgumentException("Reaction type is required");
        return existingType == requestedType;
    }
}
