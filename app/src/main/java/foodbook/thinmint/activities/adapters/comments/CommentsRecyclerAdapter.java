package foodbook.thinmint.activities.adapters.comments;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import foodbook.thinmint.R;
import foodbook.thinmint.activities.ActivityHelper;
import foodbook.thinmint.activities.adapters.common.AbstractListRecyclerAdapter;
import foodbook.thinmint.activities.adapters.common.AbstractListViewHolder;
import foodbook.thinmint.models.domain.Comment;
import foodbook.thinmint.models.domain.User;

/**
 * Created by Zachery.Sogolow on 5/18/2017.
 */

public class CommentsRecyclerAdapter extends AbstractListRecyclerAdapter<Comment, IOnCommentClickListener> {

    // Provide a suitable constructor (depends on the kind of dataset)
    public CommentsRecyclerAdapter(List<Comment> comments, IOnCommentClickListener listener, Activity activity) {
        super(comments, listener, activity);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        // set the view's size, margins, paddings and layout parameters
        CommentsViewHolder vh = new CommentsViewHolder(v, mListener);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(AbstractListViewHolder<IOnCommentClickListener> holder, int position) {
        long nowInMillis = System.currentTimeMillis();
        Date dateCreated = mItems.get(position).getDateCreated();

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

        Comment comment = mItems.get(position);
        User user = comment.getUser();

        ((TextView) holder.mLinearLayout.findViewById(R.id.user_name))
                .setText(mItems.get(position).getUser().getUsername());
        ((TextView) holder.mLinearLayout.findViewById(R.id.hidden_user_id))
                .setText(mItems.get(position).getUser().getSubject());
        ((TextView) holder.mLinearLayout.findViewById(R.id.comment_text))
                .setText(mItems.get(position).getText());
        ((TextView) holder.mLinearLayout.findViewById(R.id.comment_date))
                .setText(dateString);
    }

}

