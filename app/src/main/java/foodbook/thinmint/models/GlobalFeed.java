package foodbook.thinmint.models;

import java.util.List;

import foodbook.thinmint.models.domain.Note;

/**
 * Created by ZachS on 5/10/2017.
 */

public class GlobalFeed {
    private List<Note> mNotes;

    public GlobalFeed() {
        this.mNotes = null;
    }

    public List<Note> getNotes() {
        return mNotes;
    }

    public void setNotes(List<Note> notes) {
        this.mNotes = notes;
    }
}
