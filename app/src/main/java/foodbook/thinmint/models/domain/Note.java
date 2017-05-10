package foodbook.thinmint.models.domain;

import java.util.List;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class Note {
    private long mUserId;
    private User mUser;
    private String mContent;
    private List<Comment> mComments;
}
