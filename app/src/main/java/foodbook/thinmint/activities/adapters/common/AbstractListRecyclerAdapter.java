package foodbook.thinmint.activities.adapters.common;

import android.support.v7.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import foodbook.thinmint.models.domain.EntityBase;

/**
 * Created by Zachery.Sogolow on 5/26/2017.
 */

public abstract class AbstractListRecyclerAdapter<T extends EntityBase, C>
        extends RecyclerView.Adapter<AbstractListViewHolder<C>>
        implements IRecyclerAdapter<T> {

    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMM d", Locale.US);
    protected static final DateFormat DATE_FORMAT_YEAR = new SimpleDateFormat("MMM d yyyy", Locale.US);
    protected static final DateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a", Locale.US);

    protected List<T> mItems;
    protected C mListener;

    protected AbstractListRecyclerAdapter(List<T> items, C listener) {
        mItems = items;
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void swap(List<T> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public void add(int index, T item) {
        mItems.add(index, item);
        notifyDataSetChanged();
    }

    @Override
    public void addAll(List<T> items) {
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public void remove(long commentId) {
        int indexToRemove = indexOf(commentId);

        if (indexToRemove >= 0) {
            mItems.remove(indexToRemove);
            notifyDataSetChanged();
        }
    }

    @Override
    public void replace(T item) {
        int index = indexOf(item);
        if (index >= 0) {
            mItems.set(index, item);
        } else {
            add(0, item);
        }
        notifyDataSetChanged();
    }

    protected int indexOf(long id) {
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).getId() == id) {
                return i;
            }
        }

        return -1;
    }

    protected int indexOf(T item) {
        return indexOf(item.getId());
    }
}
