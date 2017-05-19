package foodbook.thinmint;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public abstract class AbstractAsyncCallback<T> implements IAsyncCallback<T> {
    protected IApiCallback mActivityCallback;

    public AbstractAsyncCallback(IApiCallback callback) {
        this.mActivityCallback = callback;
    }
}
