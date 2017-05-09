package foodbook.thinmint;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import foodbook.thinmint.constants.Constants;

/**
 * Created by Zachery.Sogolow on 5/8/2017.
 */

public class WebAPIConnect {
    String url = Constants.ThinMintAPI;


    public String callService(String accessToken, String path) {
        String result = "Exception Occured";
        try {

            URL obj = new URL(url + path);

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + accessToken);

            int responseCode = con.getResponseCode();

            if (responseCode < 400) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                result = response.toString();

            } else {
                result = con.getResponseMessage();
            }
        } catch (Exception e) {
            return "Error: " + result + "\n\nException: " + result;
        }
        return result;
    }
}
