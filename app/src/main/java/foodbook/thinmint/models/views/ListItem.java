package foodbook.thinmint.models.views;

import java.util.Date;

import foodbook.thinmint.models.domain.EntityBase;

/**
 * Created by ZachS on 5/27/2017.
 */

public class ListItem<T extends EntityBase> extends EntityBase {

    private ListItemTypes mType;
    private T mItem;

    public ListItem(ListItemTypes type, T item) {
        mType = type;
        mItem = item;
    }

    @Override
    public long getId() {
        return mItem.getId();
    }

    @Override
    public Date getDateCreated() {
        return mItem.getDateCreated();
    }

    public T getItem() {
        return mItem;
    }

    public ListItemTypes getType() {
        return mType;
    }
}
