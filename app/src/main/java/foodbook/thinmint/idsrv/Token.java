package foodbook.thinmint.idsrv;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import foodbook.thinmint.constants.Constants;

/**
 * Created by Zachery.Sogolow on 5/8/2017.
 */

public class Token {
    private String mAccessToken;
    private String mRefreshToken;
    private String mExpiresIn;
    private long mLastRetrieved;

    public Token() {
        mAccessToken = "";
        mRefreshToken = "";
        mExpiresIn = "";
        mLastRetrieved = -1;
    }

    public void setLastRetrieved(long lastRetrieved) {
        this.mLastRetrieved = lastRetrieved;
    }

    public void setExpiresIn(String expiresIn) {
        this.mExpiresIn = expiresIn;
    }

    public void setAccessToken(String accessToken) {
        this.mAccessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.mRefreshToken = refreshToken;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public String getExpiresIn() {
        return mExpiresIn;
    }

    public long getLastRetrieved() {
        return mLastRetrieved;
    }


    public TokenResult getAccessToken(String _clientId, String _clientSecret, String _userName, String _userPassword, String _scope) {
        String result = "";
        TokenResult tokenResult = new TokenResult();

        try {
            String url = Constants.IdSrvToken;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //add parameters
            String charset = "UTF-8";
            String urlParameters = "grant_type=" + URLEncoder.encode("password", charset);
            urlParameters += "&client_id=" + URLEncoder.encode(_clientId, charset);
            urlParameters += "&client_secret=" + URLEncoder.encode(_clientSecret, charset);
            urlParameters += "&username=" + URLEncoder.encode(_userName, charset);
            urlParameters += "&password=" + URLEncoder.encode(_userPassword, charset);
            urlParameters += "&scope=" + URLEncoder.encode(_scope, charset);

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            // Get response code
            int responseCode = con.getResponseCode();

            if (responseCode < 400) {
                // Response to String
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();


                // setAccessToken((new JSONObject(response.toString())).getString("access_token"));

                result = response.toString();
                tokenResult.setSuccess(true);
            } else {
                result = con.getResponseMessage();
                tokenResult.setSuccess(false);
            }

            // Return response (token)
            tokenResult.setTokenResult(result);

            return tokenResult;

        } catch (Exception e) {
            return new TokenResult(false, "Error: " + result + "\n\nException: " + e.getMessage());
        }
    }

    public TokenResult getRefreshToken(String _clientId, String _clientSecret) {
        String result = "";
        TokenResult tokenResult = new TokenResult();

        try {
            String url = Constants.IdSrvToken;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");


            //add parameters
            String charset = "UTF-8";
            String urlParameters = "refresh_token=" + URLEncoder.encode(mRefreshToken, charset);
            urlParameters += "&client_id=" + URLEncoder.encode(_clientId, charset);
            urlParameters += "&client_secret=" + URLEncoder.encode(_clientSecret, charset);
            urlParameters += "&grant_type=" + URLEncoder.encode("refresh_token", charset);
            urlParameters += "&scope=" + URLEncoder.encode("openid profile thinmintapi offline_access", charset);

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            // Get response code
            int responseCode = con.getResponseCode();

            if (responseCode < 400) {
                // Response to String
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                result = response.toString();
                tokenResult.setSuccess(true);
            } else {
                result = con.getResponseMessage();
                tokenResult.setSuccess(false);
            }

            tokenResult.setTokenResult(result);
            // Return response (token)
            return tokenResult;

        } catch (Exception e) {
            return new TokenResult(false, "Error: " + result + "\n\nException: " + e.getMessage());
        }
    }

    public UserInfoResult getUserInfo() {
        String result = "";
        UserInfoResult userInfo = new UserInfoResult();

        try {
            String url = Constants.IdSrvUserInfo;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + mAccessToken);

            // Get response code
            int responseCode = con.getResponseCode();

            if (responseCode < 400) {
                // Response to String
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                result = response.toString();
                userInfo.setSuccess(true);
            } else {
                result = con.getResponseMessage();
                userInfo.setSuccess(false);
            }

            userInfo.setUserInfoResult(result);

            // Return response (token)
            return userInfo;

        } catch (Exception e) {
            return new UserInfoResult(false, "Error: " + result + "\n\nException: " + e.getMessage());
        }
    }
}
