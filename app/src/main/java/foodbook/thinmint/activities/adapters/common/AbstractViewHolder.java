package foodbook.thinmint.activities.adapters.common;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Zachery.Sogolow on 5/26/2017.
 */

public abstract class AbstractViewHolder<C> extends RecyclerView.ViewHolder implements View.OnClickListener {

    public LinearLayout mLinearLayout;

    protected C mOnClickListener;

    protected AbstractViewHolder(LinearLayout v, C listener) {
        super(v);
        mLinearLayout = v;
        mOnClickListener = listener;
    }
}
