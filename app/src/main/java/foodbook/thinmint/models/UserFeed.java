package foodbook.thinmint.models;

import java.util.List;

import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.models.domain.User;

/**
 * Created by ZachS on 5/10/2017.
 */

public class UserFeed {
    private User mUser;
    private List<Note> mUsersNotes;

    public UserFeed() {
        this.mUser = null;
        this.mUsersNotes = null;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        this.mUser = user;
    }

    public List<Note> getUsersNotes() {
        return mUsersNotes;
    }

    public void setUsersNotes(List<Note> userNotes) {
        this.mUsersNotes = userNotes;
    }
}
