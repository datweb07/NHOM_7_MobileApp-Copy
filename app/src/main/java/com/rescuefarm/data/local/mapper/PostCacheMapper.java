package com.rescuefarm.data.local.mapper;

import com.rescuefarm.data.local.entity.PostCacheEntity;
import com.rescuefarm.domain.enums.PostStatus;
import com.rescuefarm.domain.enums.UrgencyLevel;
import com.rescuefarm.domain.model.Post;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public final class PostCacheMapper {
    private static final String SEPARATOR = "\u001F";
    private PostCacheMapper() { }
    public static PostCacheEntity toEntity(Post value, long cachedAt) {
        PostCacheEntity entity = new PostCacheEntity(value.getId());
        entity.sellerId = value.getSellerId(); entity.campaignId = value.getCampaignId();
        entity.title = value.getTitle(); entity.content = value.getContent();
        entity.imageUrlsSerialized = String.join(SEPARATOR, value.getImageUrls());
        entity.linkedProductIdsSerialized = String.join(SEPARATOR, value.getLinkedProductIds());
        entity.urgencyLevel = value.getUrgencyLevel().name(); entity.status = value.getStatus().name();
        entity.createdAtEpochMillis = value.getCreatedAt() == null ? 0L : value.getCreatedAt().getTime();
        entity.viewCount = value.getViewCount(); entity.cachedAtEpochMillis = cachedAt; return entity;
    }
    public static Post toDomain(PostCacheEntity value) {
        return Post.restore(value.id, value.sellerId, value.campaignId, value.title, value.content,
                decode(value.imageUrlsSerialized), decode(value.linkedProductIdsSerialized),
                enumValue(UrgencyLevel.class, value.urgencyLevel, UrgencyLevel.NORMAL),
                enumValue(PostStatus.class, value.status, PostStatus.DRAFT), value.viewCount,
                value.createdAtEpochMillis <= 0 ? null : new Date(value.createdAtEpochMillis));
    }
    public static List<Post> posts(List<PostCacheEntity> values) {
        List<Post> result = new ArrayList<>();
        if (values != null) for (PostCacheEntity value : values) {
            try { result.add(toDomain(value)); } catch (IllegalArgumentException ignored) { }
        }
        return result;
    }
    private static List<String> decode(String value) {
        if (value == null || value.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(value.split(Pattern.quote(SEPARATOR))));
    }
    private static <T extends Enum<T>> T enumValue(Class<T> type, String value, T fallback) {
        try { return Enum.valueOf(type, value == null ? "" : value); }
        catch (IllegalArgumentException error) { return fallback; }
    }
}
