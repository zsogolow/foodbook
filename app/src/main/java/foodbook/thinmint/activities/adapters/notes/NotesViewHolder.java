package foodbook.thinmint.activities.adapters.notes;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import foodbook.thinmint.R;
import foodbook.thinmint.activities.adapters.common.AbstractViewHolder;

/**
 * Created by Zachery.Sogolow on 5/26/2017.
 */

public class NotesViewHolder extends AbstractViewHolder<IOnNoteClickListener> {

    private Button mLikeButton;
    private Button mCommentButton;
    private TextView mUserName;
    private TextView mNoteComments;

    public NotesViewHolder(LinearLayout v, IOnNoteClickListener listener) {
        super(v, listener);

        mLikeButton = (Button) mLinearLayout.findViewById(R.id.like_button);
        mCommentButton = (Button) mLinearLayout.findViewById(R.id.comment_button);
        mUserName = (TextView) mLinearLayout.findViewById(R.id.user_name);
        mNoteComments = (TextView) mLinearLayout.findViewById(R.id.note_comments);

        mLikeButton.setOnClickListener(this);
        mCommentButton.setOnClickListener(this);
        mUserName.setOnClickListener(this);
        mNoteComments.setOnClickListener(this);
        mLinearLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mLikeButton)) {
            mOnClickListener.onLikeNoteClicked(mLinearLayout);
        } else if (v.equals(mCommentButton)) {
            mOnClickListener.onCommentClicked(mLinearLayout);
        } else if (v.equals(mUserName)) {
            mOnClickListener.onUserClicked(mLinearLayout);
        } else {
            mOnClickListener.onNoteClicked(mLinearLayout);
        }
    }
}
