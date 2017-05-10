package foodbook.thinmint.api;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.URL;

import foodbook.thinmint.constants.Constants;

/**
 * Created by Zachery.Sogolow on 5/8/2017.
 */

public class WebAPIConnect<T> {
    private static final String PAGING_HEADER = "X-Pagination";
    private static final String SORTING_HEADER = "X-Sort";

    private String url = Constants.ThinMintAPI;

    public WebAPIResult callService(String accessToken, String path) {
        WebAPIResult result = new WebAPIResult();

        try {

            URL obj = new URL(url + path);

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + accessToken);

            int responseCode = con.getResponseCode();

            result.setStatusCode(responseCode);

            if (responseCode < 400) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String pagingHeader = con.getHeaderField(PAGING_HEADER);
                String sortingHeader = con.getHeaderField(SORTING_HEADER);
                result.setSortingInfo(WebAPIHelper.getSortingInfo(sortingHeader));
                result.setPagingInfo(WebAPIHelper.getPagingInfo(pagingHeader));

                result.setSuccess(true);
                result.setResult(response.toString());
            } else {
                String responseMessage = con.getResponseMessage();
                result.setSuccess(false);
                result.setResult(responseMessage);
                result.setErrorMessage(responseMessage);
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage("Exception: " + e.getMessage());
        }

        return result;
    }

    public WebAPIResult postService(String accessToken, String path, JSONObject data) {
        WebAPIResult result = new WebAPIResult();

        try {

            URL obj = new URL(url + path);

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + accessToken);

            con.addRequestProperty("Content-Type", "application/json");

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(data.toString());
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();

            result.setStatusCode(responseCode);

            if (responseCode < 400) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String pagingHeader = con.getHeaderField(PAGING_HEADER);
                String sortingHeader = con.getHeaderField(SORTING_HEADER);
                result.setSortingInfo(WebAPIHelper.getSortingInfo(sortingHeader));
                result.setPagingInfo(WebAPIHelper.getPagingInfo(pagingHeader));

                result.setSuccess(true);
                result.setResult(response.toString());
            } else {
                String responseMessage = con.getResponseMessage();
                result.setSuccess(false);
                result.setResult(responseMessage);
                result.setErrorMessage(responseMessage);
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage("Exception: " + e.getMessage());
        }

        return result;
    }

}
