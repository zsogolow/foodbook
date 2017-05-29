package foodbook.thinmint.activities.adapters.common;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import foodbook.thinmint.R;
import foodbook.thinmint.activities.ActivityHelper;
import foodbook.thinmint.models.domain.Comment;
import foodbook.thinmint.models.domain.EntityBase;
import foodbook.thinmint.models.domain.Like;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.models.domain.User;
import foodbook.thinmint.models.views.ListItem;
import foodbook.thinmint.models.views.ListItemTypes;

/**
 * Created by ZachS on 5/27/2017.
 */

public abstract class AbstractItemRecyclerAdapter<T>
        extends AbstractListRecyclerAdapter<ListItem<EntityBase>, T>
        implements IRecyclerAdapter<ListItem<EntityBase>> {

    public AbstractItemRecyclerAdapter(List<ListItem<EntityBase>> items, T listener, Activity activity) {
        super(items, listener, activity);
    }

    @Override
    public abstract AbstractListViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(AbstractListViewHolder<T> holder, int position) {
        ListItem<EntityBase> listItem = mItems.get(position);
        ListItemTypes type = listItem.getType();

        if (listItem.getItem() != null) {
            long nowInMillis = System.currentTimeMillis();
            Date dateCreated = listItem.getDateCreated();

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

            if (type == ListItemTypes.Comment) {
                Comment comment = (Comment) listItem.getItem();
                ((TextView) holder.mLinearLayout.findViewById(R.id.user_name))
                        .setText(comment.getUser().getUsername());
                ((TextView) holder.mLinearLayout.findViewById(R.id.hidden_user_id))
                        .setText(comment.getUser().getSubject());
                ((TextView) holder.mLinearLayout.findViewById(R.id.comment_text))
                        .setText(comment.getText());
                ((TextView) holder.mLinearLayout.findViewById(R.id.comment_date))
                        .setText(dateString);
            } else if (type == ListItemTypes.Note) {
                Note note = (Note) listItem.getItem();

                long userId = ActivityHelper.getCurrentUserId(mActivity);

                boolean hasLiked = false;
                for (Like like : note.getLikes()) {
                    if (like.getUserId() == userId) {
                        hasLiked = true;
                        break;
                    }
                }

                if (hasLiked) {
                    holder.mLinearLayout.findViewById(R.id.like_button).setVisibility(View.GONE);
                    holder.mLinearLayout.findViewById(R.id.un_like_button).setVisibility(View.VISIBLE);
                } else {
                    holder.mLinearLayout.findViewById(R.id.like_button).setVisibility(View.VISIBLE);
                    holder.mLinearLayout.findViewById(R.id.un_like_button).setVisibility(View.GONE);
                }

                ((TextView) holder.mLinearLayout.findViewById(R.id.hidden_user_id))
                        .setText(note.getUser().getSubject());
                ((TextView) holder.mLinearLayout.findViewById(R.id.hidden_note_id))
                        .setText(note.getId() + "");
                ((TextView) holder.mLinearLayout.findViewById(R.id.user_name))
                        .setText(note.getUser().getUsername());
                ((TextView) holder.mLinearLayout.findViewById(R.id.note_date))
                        .setText(dateString);
                ((TextView) holder.mLinearLayout.findViewById(R.id.note_contents))
                        .setText(note.getContent());
                ((TextView) holder.mLinearLayout.findViewById(R.id.note_comments))
                        .setText(note.getComments().size() + " comments");
                ((TextView) holder.mLinearLayout.findViewById(R.id.note_likes))
                        .setText(note.getLikes().size() + " likes");
            } else if (type == ListItemTypes.User) {
                User user = (User) listItem.getItem();
                ((TextView) holder.mLinearLayout.findViewById(R.id.user_name))
                        .setText(user.getUsername());
                ((TextView) holder.mLinearLayout.findViewById(R.id.hidden_user_id))
                        .setText(user.getSubject());
            }
        } else {
            if (type == ListItemTypes.AddComment) {
                ((EditText) holder.mLinearLayout.findViewById(R.id.comment_edit_text))
                        .setText("");
            }
        }
    }

    public abstract ListItemTypes getType(int itemViewType);

    @Override
    public abstract int getItemViewType(int position);
}
