package foodbook.thinmint.activities.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import foodbook.thinmint.R;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.models.domain.User;

/**
 * Created by Zachery.Sogolow on 5/18/2017.
 */

public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.ViewHolder>
        implements IRecyclerAdapter<User> {
    private List<User> mUsers;
    private ViewHolder.IOnUserClickListener mListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public LinearLayout mLinearLayout;
        private IOnUserClickListener mListener;

        public ViewHolder(LinearLayout v, IOnUserClickListener listener) {
            super(v);
            mLinearLayout = v;
            mListener = listener;
            mLinearLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onUserClicked(v);
        }

        public interface IOnUserClickListener {
            void onUserClicked(View caller);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public UsersRecyclerAdapter(List<User> users, ViewHolder.IOnUserClickListener clickListener) {
        mUsers = users;
        mListener = clickListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public UsersRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v, mListener);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ((TextView) holder.mLinearLayout.findViewById(R.id.user_name))
                .setText(mUsers.get(position).getUsername());
        ((TextView) holder.mLinearLayout.findViewById(R.id.hidden_user_id))
                .setText(mUsers.get(position).getSubject());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    @Override
    public void swap(List<User> users) {
        mUsers = users;
        notifyDataSetChanged();
    }

    @Override
    public void add(int index, User user) {
        mUsers.add(index, user);
        notifyDataSetChanged();
    }

    @Override
    public void addAll(List<User> users) {
        mUsers.addAll(users);
        notifyDataSetChanged();
    }

    @Override
    public void remove(long userId) {
        int indexToRemove = -1;
        for (int i = 0; i < mUsers.size(); i++) {
            if (mUsers.get(i).getId() == userId) {
                indexToRemove = i;
                break;
            }
        }

        if (indexToRemove >= 0) {
            mUsers.remove(indexToRemove);
            notifyDataSetChanged();
        }
    }

    @Override
    public void replace(User item) {

    }
}

