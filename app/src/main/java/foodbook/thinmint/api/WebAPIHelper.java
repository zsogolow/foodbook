package foodbook.thinmint.api;

import org.json.JSONException;
import org.json.JSONObject;

import foodbook.thinmint.idsrv.JsonManipulation;

/**
 * Created by ZachS on 5/9/2017.
 */

public class WebAPIHelper {
    public static PagingInfo getPagingInfo(String json) {
        try {
            JSONObject jObj = new JSONObject(json);
            long currentPage = jObj.getLong("currentPage");
            long pageSize = jObj.getLong("pageSize");
            long totalCount = jObj.getLong("totalCount");
            long totalPages = jObj.getLong("totalPages");
            String prevLink = jObj.getString("prevPageLink");
            String nextLink = jObj.getString("nextPageLink");
            return new PagingInfo(currentPage, pageSize, totalCount, totalPages, prevLink, nextLink);
        } catch (NullPointerException | JSONException je) {
            return null;
        }
    }

    public static SortingInfo getSortingInfo(String json) {
        try {
            JSONObject jObj = new JSONObject(json);
            String sort = jObj.getString("sort");
            return new SortingInfo(sort);
        } catch (NullPointerException | JSONException je) {
            return null;
        }
    }
}
