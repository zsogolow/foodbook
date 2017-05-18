package foodbook.thinmint.activities;

import java.util.List;

import foodbook.thinmint.models.domain.Note;

/**
 * Created by ZachS on 5/17/2017.
 */

public interface OnNotesInteractionListener {
    void onNoteAdded(Note note);

    void onNotesRetrieved(List<Note> notes);
}
