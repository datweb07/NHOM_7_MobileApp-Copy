package com.rescuefarm.data.local;

import static org.junit.Assert.assertEquals;
import com.rescuefarm.data.local.mapper.PostCacheMapper;
import com.rescuefarm.domain.enums.PostStatus;
import com.rescuefarm.domain.enums.UrgencyLevel;
import com.rescuefarm.domain.model.Post;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import org.junit.Test;

public class PostCacheMapperTest {
    @Test public void post_roundTripsThroughRoomCache() {
        Post source = Post.restore("p", "s", "c", "Giải cứu dưa hấu", "Bài viết đủ nội dung.",
                Arrays.asList("https://a", "https://b"), Collections.singletonList("product"),
                UrgencyLevel.HIGH, PostStatus.PUBLISHED, 12L, new Date(1000L));
        Post restored = PostCacheMapper.toDomain(PostCacheMapper.toEntity(source, 2000L));
        assertEquals(source.getImageUrls(), restored.getImageUrls());
        assertEquals(source.getLinkedProductIds(), restored.getLinkedProductIds());
        assertEquals(12L, restored.getViewCount());
        assertEquals(PostStatus.PUBLISHED, restored.getStatus());
    }
}
