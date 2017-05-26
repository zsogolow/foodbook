package foodbook.thinmint.activities.adapters.notes;

import android.view.View;

/**
 * Created by Zachery.Sogolow on 5/26/2017.
 */

public interface IOnNoteClickListener {
    void onNoteClicked(View caller);

    void onLikeNoteClicked(View caller);

    void onCommentClicked(View caller);

    void onUserClicked(View caller);
}
