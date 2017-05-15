package foodbook.thinmint.models.factories;

import java.util.Date;

import foodbook.thinmint.models.domain.Note;

/**
 * Created by ZachS on 5/14/2017.
 */

public class NoteFactory {
    public static Note createNote(long id, Date dateCreated, long userId, String contents) {
        Note note = new Note();

        note.setId(id);
        note.setDateCreated(dateCreated);
        note.setUserId(userId);
        note.setContent(contents);

        return note;
    }
}
