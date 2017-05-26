package foodbook.thinmint.activities.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import foodbook.thinmint.R;
import foodbook.thinmint.activities.MainActivity;
import foodbook.thinmint.models.domain.Comment;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.models.domain.User;

/**
 * Created by Zachery.Sogolow on 5/18/2017.
 */

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder>
        implements IRecyclerAdapter<Comment> {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMM d", Locale.US);
    private static final DateFormat DATE_FORMAT_YEAR = new SimpleDateFormat("MMM d yyyy", Locale.US);
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a", Locale.US);

    private List<Comment> mComments;
    private ViewHolder.IOnCommentClickListener mListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public LinearLayout mLinearLayout;
        private TextView mUserName;
        private IOnCommentClickListener mListener;

        public ViewHolder(LinearLayout v, IOnCommentClickListener listener) {
            super(v);
            mLinearLayout = v;
            mUserName = (TextView) mLinearLayout.findViewById(R.id.user_name);
            mListener = listener;
            mUserName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.equals(mUserName)) {
                mListener.onUserClicked(mLinearLayout);
            }
        }

        public interface IOnCommentClickListener {
            void onUserClicked(View caller);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CommentsRecyclerAdapter(List<Comment> comments, ViewHolder.IOnCommentClickListener clickListener) {
        mComments = comments;
        mListener = clickListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CommentsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v, mListener);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        long nowInMillis = System.currentTimeMillis();
        Date dateCreated = mComments.get(position).getDateCreated();

        Calendar now = Calendar.getInstance();
        Calendar created = Calendar.getInstance();
        now.setTime(new Date(nowInMillis));
        created.setTime(dateCreated);

        boolean sameDay = now.get(Calendar.YEAR) == created.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == created.get(Calendar.DAY_OF_YEAR);
        boolean sameYear = now.get(Calendar.YEAR) == created.get(Calendar.YEAR);

        DateFormat dateFormat = sameYear ? DATE_FORMAT : DATE_FORMAT_YEAR;
        String dateString = sameDay ? TIME_FORMAT.format(dateCreated)
                : dateFormat.format(dateCreated) + " at " + TIME_FORMAT.format(dateCreated);

        Comment comment = mComments.get(position);
        User user = comment.getUser();

        ((TextView) holder.mLinearLayout.findViewById(R.id.user_name))
                .setText(mComments.get(position).getUser().getUsername());
        ((TextView) holder.mLinearLayout.findViewById(R.id.hidden_user_id))
                .setText(mComments.get(position).getUser().getSubject());
        ((TextView) holder.mLinearLayout.findViewById(R.id.comment_text))
                .setText(mComments.get(position).getText());
        ((TextView) holder.mLinearLayout.findViewById(R.id.comment_date))
                .setText(dateString);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mComments.size();
    }

    @Override
    public void swap(List<Comment> comments) {
        mComments = comments;
        notifyDataSetChanged();
    }

    @Override
    public void add(int index, Comment comment) {
        mComments.add(index, comment);
        notifyDataSetChanged();
    }

    @Override
    public void addAll(List<Comment> comments) {
        mComments.addAll(comments);
        notifyDataSetChanged();
    }

    @Override
    public void remove(long commentId) {
        int indexToRemove = -1;
        for (int i = 0; i < mComments.size(); i++) {
            if (mComments.get(i).getId() == commentId) {
                indexToRemove = i;
                break;
            }
        }

        if (indexToRemove >= 0) {
            mComments.remove(indexToRemove);
            notifyDataSetChanged();
        }
    }

    @Override
    public void replace(Comment item) {
    }
}

