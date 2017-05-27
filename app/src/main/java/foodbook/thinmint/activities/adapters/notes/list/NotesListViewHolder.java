package foodbook.thinmint.activities.adapters.notes.list;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import foodbook.thinmint.R;
import foodbook.thinmint.activities.adapters.common.AbstractListViewHolder;

/**
 * Created by Zachery.Sogolow on 5/26/2017.
 */

public class NotesListViewHolder extends AbstractListViewHolder<IOnNotesListClickListener> {

    private Button mLikeButton;
    private Button mCommentButton;
    private TextView mUserName;
    private TextView mNoteComments;

    public NotesListViewHolder(LinearLayout v, IOnNotesListClickListener listener) {
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
            mOnClickListener.onLikeNoteClick(mLinearLayout);
        } else if (v.equals(mCommentButton)) {
            mOnClickListener.onCommentClick(mLinearLayout);
        } else if (v.equals(mUserName)) {
            mOnClickListener.onUserClick(mLinearLayout);
        } else {
            mOnClickListener.onNoteClick(mLinearLayout);
        }
    }
}
