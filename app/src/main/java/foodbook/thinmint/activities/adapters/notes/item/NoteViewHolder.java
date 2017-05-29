package foodbook.thinmint.activities.adapters.notes.item;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import foodbook.thinmint.R;
import foodbook.thinmint.activities.adapters.common.AbstractItemViewHolder;
import foodbook.thinmint.models.views.ListItemTypes;

/**
 * Created by ZachS on 5/27/2017.
 */

public class NoteViewHolder extends AbstractItemViewHolder<IOnNoteClickListener> {

    private TextView mUserNameView;
    private Button mAddCommentButton;
    private Button mLikeButton;
    private Button mUnlikeButton;

    public NoteViewHolder(LinearLayout v, IOnNoteClickListener listener, ListItemTypes type) {
        super(v, listener);
        if (type == ListItemTypes.Note) {
            mUserNameView = (TextView) mLinearLayout.findViewById(R.id.user_name);
            mAddCommentButton = (Button) mLinearLayout.findViewById(R.id.comment_button);
            mLikeButton = (Button) mLinearLayout.findViewById(R.id.like_button);
            mUnlikeButton = (Button) mLinearLayout.findViewById(R.id.un_like_button);
            mUserNameView.setOnClickListener(this);
            mAddCommentButton.setOnClickListener(this);
            mLikeButton.setOnClickListener(this);
            mUnlikeButton.setOnClickListener(this);
        } else if (type == ListItemTypes.Comment) {
            mUserNameView = (TextView) mLinearLayout.findViewById(R.id.user_name);
            mUserNameView.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mUserNameView)) {
            mOnClickListener.onUserClick(mLinearLayout);
        } else if (v.equals(mAddCommentButton)) {
            mOnClickListener.onCommentButtonClick(mLinearLayout);
        } else if (v.equals(mLikeButton)) {
            mOnClickListener.onLikeButtonClick(mLinearLayout);
        } else if (v.equals(mUnlikeButton)) {
            mOnClickListener.onUnlikeButtonClick(mLinearLayout);
        }
    }
}
