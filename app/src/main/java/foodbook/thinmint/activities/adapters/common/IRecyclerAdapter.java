package foodbook.thinmint.activities.adapters.common;

import java.util.List;

import foodbook.thinmint.models.domain.Note;

/**
 * Created by Zachery.Sogolow on 5/25/2017.
 */

public interface IRecyclerAdapter<T> {
    void swap(List<T> items);

    void addAll(List<T> items);

    void add(int index, T item);

    void remove(long itemId);

    void replace(T item);
}
