package foodbook.thinmint.activities.common;

/**
 * Created by Zachery.Sogolow on 5/24/2017.
 */

public class RequestCodes {
    public static final int NOTE_REQUEST_CODE = 0;
    public static final int CREATE_NOTE_REQUEST_CODE = 1;
    public static final int DELETE_NOTE_REQUEST_CODE = 2;

    public static final String DELETE_NOTE_ACTION = "delete_note_action";
    public static final String CREATE_NOTE_ACTION = "create_note_action";
    public static final String COMMENT_NOTE_ACTION = "comment_note_action";
    public static final String LIKE_NOTE_ACTION = "like_note_action";

    public static final String NOTE_EXTRA_ID = "note_id";
    public static final String NOTE_COMMENT_EXTRA_ID = "note_comment_id";
    public static final String NOTE_EXTRA_ACTION = "note_action";
    public static final String CREATE_NOTE_EXTRA_ID = "created_note_id";
    public static final String DELETE_NOTE_EXTRA_ID = "deleted_note_id";
}
