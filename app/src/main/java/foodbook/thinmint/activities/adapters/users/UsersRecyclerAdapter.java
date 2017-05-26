package foodbook.thinmint.activities.adapters.users;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import foodbook.thinmint.R;
import foodbook.thinmint.activities.adapters.common.AbstractRecyclerAdapter;
import foodbook.thinmint.activities.adapters.common.AbstractViewHolder;
import foodbook.thinmint.models.domain.User;

/**
 * Created by Zachery.Sogolow on 5/18/2017.
 */

public class UsersRecyclerAdapter extends AbstractRecyclerAdapter<User, IOnUserClickListener> {

    // Provide a suitable constructor (depends on the kind of dataset)
    public UsersRecyclerAdapter(List<User> users, IOnUserClickListener listener) {
        super(users, listener);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        // set the view's size, margins, paddings and layout parameters
        UsersViewHolder vh = new UsersViewHolder(v, mListener);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(AbstractViewHolder<IOnUserClickListener> holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ((TextView) holder.mLinearLayout.findViewById(R.id.user_name))
                .setText(mItems.get(position).getUsername());
        ((TextView) holder.mLinearLayout.findViewById(R.id.hidden_user_id))
                .setText(mItems.get(position).getSubject());
    }
}

