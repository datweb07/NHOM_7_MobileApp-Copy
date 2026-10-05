package com.rescuefarm.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import com.rescuefarm.domain.enums.PostStatus;
import com.rescuefarm.domain.enums.UrgencyLevel;
import com.rescuefarm.domain.model.Post;
import java.util.Collections;
import org.junit.Test;

public class PostModerationTest {
    @Test public void sellerPost_startsDraft_andMustBecomePendingBeforePublication() {
        Post post = new Post("post", "seller");
        post.defineContent("campaign", "Giải cứu xoài", "Nông sản cần cộng đồng hỗ trợ.",
                Collections.emptyList(), Collections.singletonList("product"), UrgencyLevel.CRITICAL);
        assertEquals(PostStatus.DRAFT, post.getStatus());
        post.submitForApproval();
        assertEquals(PostStatus.PENDING_APPROVAL, post.getStatus());
        assertThrows(IllegalStateException.class, post::submitForApproval);
    }

    @Test public void invalidContent_isRejectedBeforeRepositoryWrite() {
        Post post = new Post("post", "seller");
        assertThrows(IllegalArgumentException.class, () -> post.defineContent("", "No", "short",
                Collections.emptyList(), Collections.emptyList(), UrgencyLevel.NORMAL));
    }
}
