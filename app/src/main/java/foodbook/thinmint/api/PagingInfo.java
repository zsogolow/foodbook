package foodbook.thinmint.api;

/**
 * Created by ZachS on 5/9/2017.
 */

public class PagingInfo {
    private long mCurrentPage;
    private long mPageSize;
    private long mTotalCount;
    private long mTotalPages;
    private String mPrevPageLink;
    private String mNextPageLink;

    public PagingInfo(long currentPage, long pageSize, long totalCount,
                      long totalPages, String prevPageLink, String nextPageLink) {

        this.mCurrentPage = currentPage;
        this.mPageSize = pageSize;
        this.mTotalCount = totalCount;
        this.mTotalPages = totalPages;
        this.mPrevPageLink = prevPageLink;
        this.mNextPageLink = nextPageLink;
    }

    public long getCurrentPage() {
        return mCurrentPage;
    }

    public void setCurrentPage(long currentPage) {
        this.mCurrentPage = currentPage;
    }

    public long getPageSize() {
        return mPageSize;
    }

    public void setPageSize(long pageSize) {
        this.mPageSize = pageSize;
    }

    public long getTotalCount() {
        return mTotalCount;
    }

    public void setTotalCount(long totalCount) {
        this.mTotalCount = totalCount;
    }

    public long getTotalPages() {
        return mTotalPages;
    }

    public void setTotalPages(long totalPages) {
        this.mTotalPages = totalPages;
    }

    public String getPrevPageLink() {
        return mPrevPageLink;
    }

    public void setPrevPageLink(String prevPageLink) {
        this.mPrevPageLink = prevPageLink;
    }

    public String getNextPageLink() {
        return mNextPageLink;
    }

    public void setNextPageLink(String nextPageLink) {
        this.mNextPageLink = nextPageLink;
    }
}
