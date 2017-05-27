package foodbook.thinmint.activities.adapters.notes.item;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.List;

import foodbook.thinmint.R;
import foodbook.thinmint.activities.adapters.common.AbstractItemRecyclerAdapter;
import foodbook.thinmint.activities.adapters.common.AbstractListViewHolder;
import foodbook.thinmint.models.domain.EntityBase;
import foodbook.thinmint.models.views.ListItem;
import foodbook.thinmint.models.views.ListItemTypes;

/**
 * Created by ZachS on 5/27/2017.
 */

public class NoteRecyclerAdapter extends AbstractItemRecyclerAdapter<IOnNoteClickListener> {

    public NoteRecyclerAdapter(List<ListItem<EntityBase>> items, IOnNoteClickListener listener) {
        super(items, listener);
    }

    @Override
    public AbstractListViewHolder<IOnNoteClickListener> onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout v = null;
        ListItemTypes type = getType(viewType);
        switch (type) {
            case Note:
                v = (LinearLayout) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_note, parent, false);
                break;

            case AddComment:
                v = (LinearLayout) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_add_comment, parent, false);

                final Button addCommentButton = (Button) v.findViewById(R.id.add_comment_button);
                final EditText commentText = (EditText) v.findViewById(R.id.comment_edit_text);

                commentText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.toString().length() > 0) {
                            addCommentButton.setEnabled(true);
                        } else {
                            addCommentButton.setEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                addCommentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onAddCommentClick(commentText);
                    }
                });

                break;
            case Comment:
            default:
                v = (LinearLayout) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_comment, parent, false);
                break;
        }

        return new NoteViewHolder(v, mListener, type);
    }

    @Override
    public ListItemTypes getType(int itemViewType) {
        switch (itemViewType) {
            case 1:
                return ListItemTypes.Note;
            case 2:
                return ListItemTypes.AddComment;
            case 3:
            default:
                return ListItemTypes.Comment;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 1;
        } else if (position == 1) {
            return 2;
        } else {
            return 3;
        }
    }
}
