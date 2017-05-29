package foodbook.thinmint.activities.adapters.notes.list;

import android.view.View;

/**
 * Created by Zachery.Sogolow on 5/26/2017.
 */

public interface IOnNotesListClickListener {
    void onNoteClick(View caller);

    void onCommentClick(View caller);

    void onUserClick(View caller);

    void onLikeNoteClick(View caller);

    void onUnlikeButtonClick(View view);

}
