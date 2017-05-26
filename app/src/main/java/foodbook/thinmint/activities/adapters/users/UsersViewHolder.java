package foodbook.thinmint.activities.adapters.users;

import android.view.View;
import android.widget.LinearLayout;

import foodbook.thinmint.activities.adapters.common.AbstractViewHolder;

/**
 * Created by Zachery.Sogolow on 5/26/2017.
 */

public class UsersViewHolder extends AbstractViewHolder<IOnUserClickListener> {

    protected UsersViewHolder(LinearLayout v, IOnUserClickListener listener) {
        super(v, listener);
        mLinearLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mOnClickListener.onUserClicked(v);
    }
}
