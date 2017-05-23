package foodbook.thinmint.api;

import java.net.URLEncoder;

/**
 * Created by Zachery.Sogolow on 5/22/2017.
 */

public class Query {

//    private String mAccessToken;
    private String mPath;
    private String mFilter;
    private String mSort;
    private int mPage;
    private int mPageSize;

    private Query() {
//        mAccessToken = "";
        mPath = "";
        mFilter = "";
        mSort = "";
        mPage = 0;
        mPageSize = 0;
    }

    @Override
    public String toString() {
        StringBuilder query = new StringBuilder(mPath);
        try {
            boolean firstAdded = false;
            if (!mFilter.equals("")) {
                query.append("?filter=");
                query.append(URLEncoder.encode(mFilter, "UTF-8"));
                firstAdded = true;
            }

            if (!mSort.equals("")) {
                query.append(firstAdded ? "&" : "?");
                query.append("sort=");
                query.append(URLEncoder.encode(mSort, "UTF-8"));
                firstAdded = true;
            }

            if (mPage > 0) {
                query.append(firstAdded ? "&" : "?");
                query.append("page=");
                query.append(mPage);
                firstAdded = true;
            }

            if (mPageSize > 0) {
                query.append(firstAdded ? "&" : "?");
                query.append("pageSize=");
                query.append(mPageSize);
                firstAdded = true;
            }
        } catch (Exception e) {
        }

        return query.toString();
    }

    public static QueryBuilder builder() {
        return new QueryBuilder();
    }

//    public String getAccessToken() {
//        return mAccessToken;
//    }

    public String getPath() {
        return mPath;
    }

    public String getFilter() {
        return mFilter;
    }

    public String getSort() {
        return mSort;
    }

    public int getPage() {
        return mPage;
    }

    public int getPageSize() {
        return mPageSize;
    }

    public static class QueryBuilder {

        private Query mQuery = new Query();

//        public QueryBuilder setAccessToken(String token) {
//            this.mQuery.mAccessToken = token;
//            return this;
//        }

        public QueryBuilder setPath(String path) {
            this.mQuery.mPath = path;
            return this;
        }

        public QueryBuilder setFilter(String filter) {
            this.mQuery.mFilter = filter;
            return this;
        }

        public QueryBuilder setSort(String sort) {
            this.mQuery.mSort = sort;
            return this;
        }

        public QueryBuilder setPage(int page) {
            this.mQuery.mPage = page;
            return this;
        }

        public QueryBuilder setPageSize(int pageSize) {
            this.mQuery.mPageSize = pageSize;
            return this;
        }

        public Query build() {
            return mQuery;
        }
    }
}
