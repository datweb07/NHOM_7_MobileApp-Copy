package com.rescuefarm.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import com.rescuefarm.domain.enums.CommentStatus;
import com.rescuefarm.domain.model.Comment;
import java.util.Date;
import org.junit.Test;

public class CommentTest {
    @Test public void editAndDelete_followModerationStates() {
        Comment comment = Comment.create("comment", "post", "user", "Ủng hộ nhà vườn", new Date(1L));
        comment.edit("Tôi sẽ ghé điểm giải cứu", new Date(2L));
        assertEquals(CommentStatus.EDITED, comment.getStatus());
        comment.delete();
        assertEquals(CommentStatus.DELETED, comment.getStatus());
        assertEquals("", comment.getContent());
    }
    @Test public void emptyOrOversizedComment_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> Comment.validateContent("   "));
        assertThrows(IllegalArgumentException.class, () -> Comment.validateContent("x".repeat(501)));
    }
}
