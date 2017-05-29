package foodbook.thinmint.activities.adapters.users.item;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
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

public class UserRecyclerAdapter extends AbstractItemRecyclerAdapter<IOnUserClickListener> {

    public UserRecyclerAdapter(List<ListItem<EntityBase>> items, IOnUserClickListener listener, Activity activity) {
        super(items, listener, activity);
    }

    @Override
    public AbstractListViewHolder<IOnUserClickListener> onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout v = null;
        ListItemTypes type = getType(viewType);
        switch (type) {
            case Note:
                v = (LinearLayout) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_note, parent, false);
                break;

            case User:
            default:
                v = (LinearLayout) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_user_info, parent, false);
                break;
        }

        return new UserViewHolder(v, mListener, type);
    }

    @Override
    public ListItemTypes getType(int itemViewType) {
        switch (itemViewType) {
            case 1:
                return ListItemTypes.Note;
            case 2:
            default:
                return ListItemTypes.User;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 2;
        } else {
            return 1;
        }
    }
}
