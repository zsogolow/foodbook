package foodbook.thinmint.activities.adapters.common;

import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Zachery.Sogolow on 5/26/2017.
 */

public abstract class AbstractItemViewHolder<T> extends AbstractListViewHolder<T> {

    protected AbstractItemViewHolder(LinearLayout v, T listener) {
        super(v, listener);
    }

    @Override
    public abstract void onClick(View v);
}
