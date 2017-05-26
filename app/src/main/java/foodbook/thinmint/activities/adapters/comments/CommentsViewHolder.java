package foodbook.thinmint.activities.adapters.comments;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import foodbook.thinmint.R;
import foodbook.thinmint.activities.adapters.common.AbstractViewHolder;

/**
 * Created by Zachery.Sogolow on 5/26/2017.
 */

public class CommentsViewHolder extends AbstractViewHolder<IOnCommentClickListener> {

    private TextView mUserName;

    protected CommentsViewHolder(LinearLayout v, IOnCommentClickListener listener) {
        super(v, listener);
        mUserName = (TextView) mLinearLayout.findViewById(R.id.user_name);
        mUserName.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mUserName)) {
            mOnClickListener.onUserClicked(mLinearLayout);
        }
    }
}
