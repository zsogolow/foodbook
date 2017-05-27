package foodbook.thinmint.activities.adapters.users.list;

import android.view.View;
import android.widget.LinearLayout;

import foodbook.thinmint.activities.adapters.common.AbstractListViewHolder;

/**
 * Created by Zachery.Sogolow on 5/26/2017.
 */

public class UsersListViewHolder extends AbstractListViewHolder<IOnUsersListClickListener> {

    protected UsersListViewHolder(LinearLayout v, IOnUsersListClickListener listener) {
        super(v, listener);
        mLinearLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mOnClickListener.onUserClick(v);
    }
}
