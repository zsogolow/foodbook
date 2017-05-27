package foodbook.thinmint.activities.adapters.users.list;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import foodbook.thinmint.R;
import foodbook.thinmint.activities.adapters.common.AbstractListRecyclerAdapter;
import foodbook.thinmint.activities.adapters.common.AbstractListViewHolder;
import foodbook.thinmint.models.domain.User;

/**
 * Created by Zachery.Sogolow on 5/18/2017.
 */

public class UsersListRecyclerAdapter extends AbstractListRecyclerAdapter<User, IOnUsersListClickListener> {

    // Provide a suitable constructor (depends on the kind of dataset)
    public UsersListRecyclerAdapter(List<User> users, IOnUsersListClickListener listener) {
        super(users, listener);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public UsersListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        // set the view's size, margins, paddings and layout parameters
        UsersListViewHolder vh = new UsersListViewHolder(v, mListener);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(AbstractListViewHolder<IOnUsersListClickListener> holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ((TextView) holder.mLinearLayout.findViewById(R.id.user_name))
                .setText(mItems.get(position).getUsername());
        ((TextView) holder.mLinearLayout.findViewById(R.id.hidden_user_id))
                .setText(mItems.get(position).getSubject());
    }
}

