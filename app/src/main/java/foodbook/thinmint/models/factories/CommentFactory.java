package foodbook.thinmint.models.factories;

import java.util.Date;

import foodbook.thinmint.models.domain.Comment;

/**
 * Created by ZachS on 5/14/2017.
 */

public class CommentFactory {
    public static Comment createComment(long id, Date dateCreated, long noteId, long userId, String text) {
        Comment comment = new Comment();

        comment.setId(id);
        comment.setDateCreated(dateCreated);
        comment.setNoteId(noteId);
        comment.setUserId(userId);
        comment.setText(text);

        return comment;
    }
}
