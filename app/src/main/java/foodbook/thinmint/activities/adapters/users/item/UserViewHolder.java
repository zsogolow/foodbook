package foodbook.thinmint.activities.adapters.users.item;

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

public class UserViewHolder extends AbstractItemViewHolder<IOnUserClickListener> {

    private TextView mUserNameView;
    private Button mAddCommentButton;

    public UserViewHolder(LinearLayout v, IOnUserClickListener listener, ListItemTypes type) {
        super(v, listener);
        if (type == ListItemTypes.Note) {
            mAddCommentButton = (Button) mLinearLayout.findViewById(R.id.comment_button);
            mAddCommentButton.setOnClickListener(this);
            mLinearLayout.setOnClickListener(this);
        } else if (type == ListItemTypes.Comment) {
            mUserNameView = (TextView) mLinearLayout.findViewById(R.id.user_name);
            mUserNameView.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
       if (v.equals(mAddCommentButton)) {
            mOnClickListener.onCommentButtonClick(mLinearLayout);
        } else {
            mOnClickListener.onClick(mLinearLayout);
        }
    }
}
