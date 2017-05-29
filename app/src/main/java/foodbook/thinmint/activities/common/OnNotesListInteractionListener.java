package foodbook.thinmint.activities.common;

import java.util.List;

import foodbook.thinmint.models.domain.Note;

/**
 * Created by ZachS on 5/17/2017.
 */

public interface OnNotesListInteractionListener {
    void onNoteAdded(long noteId);
    void onNoteDeleted(long noteId);
    void onCommentAdded(long noteId);
    void onLikeAdded(long noteId);
}
