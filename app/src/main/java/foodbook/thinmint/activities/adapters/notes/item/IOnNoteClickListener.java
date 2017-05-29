package foodbook.thinmint.activities.adapters.notes.item;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Zachery.Sogolow on 5/26/2017.
 */

public interface IOnNoteClickListener {
    void onClick(View view);

    void onAddCommentClick(EditText editText);

    void onUserClick(View view);

    void onCommentButtonClick(View view);

    void onLikeButtonClick(View view);

    void onUnlikeButtonClick(View view);
}
