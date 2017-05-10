package foodbook.thinmint.api;

/**
 * Created by ZachS on 5/9/2017.
 */

public class WebAPIResult {
    private PagingInfo mPagingInfo;
    private SortingInfo mSortingInfo;
    private String mResult;

    private boolean mSuccess;
    private int mStatusCode;
    private String mErrorMessage;

    public WebAPIResult() {
        this.mPagingInfo = null;
        this.mSortingInfo = null;
        this.mResult = "";
        this.mSuccess = false;
        this.mStatusCode = -1;
        this.mErrorMessage = "";
    }

    public PagingInfo getPagingInfo() {
        return mPagingInfo;
    }

    public void setPagingInfo(PagingInfo pagingInfo) {
        this.mPagingInfo = pagingInfo;
    }

    public SortingInfo getSortingInfo() {
        return mSortingInfo;
    }

    public void setSortingInfo(SortingInfo sortingInfo) {
        this.mSortingInfo = sortingInfo;
    }


    public String getResult() {
        return mResult;
    }

    public void setResult(String mResult) {
        this.mResult = mResult;
    }

    public boolean isSuccess() {
        return mSuccess;
    }

    public void setSuccess(boolean success) {
        this.mSuccess = success;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public void setStatusCode(int statusCode) {
        this.mStatusCode = statusCode;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.mErrorMessage = errorMessage;
    }
}
