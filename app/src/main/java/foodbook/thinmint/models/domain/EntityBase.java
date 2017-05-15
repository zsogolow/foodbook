package foodbook.thinmint.models.domain;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by ZachS on 5/14/2017.
 */

public abstract class EntityBase {

    @SerializedName("id")
    protected long mId;

    @SerializedName("dateCreated")
    protected Date mDateCreated;

    protected EntityBase() {
    }

    protected EntityBase(long id, Date dateCreated) {
        this.mId = id;
        this.mDateCreated = dateCreated;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public Date getDateCreated() {
        return mDateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.mDateCreated = dateCreated;
    }
}
