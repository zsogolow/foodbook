package foodbook.thinmint;

import foodbook.thinmint.IActivityCallback;
import foodbook.thinmint.IAsyncCallback;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public abstract class AbstractAsyncCallback<T> implements IAsyncCallback<T> {
    protected IActivityCallback mActivityCallback;

    public AbstractAsyncCallback(IActivityCallback callback) {
        this.mActivityCallback = callback;
    }
}
