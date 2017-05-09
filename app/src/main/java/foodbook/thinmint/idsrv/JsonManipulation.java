package foodbook.thinmint.idsrv;

import org.json.JSONObject;

/**
 * Created by Zachery.Sogolow on 5/8/2017.
 */

public class JsonManipulation {
    public static String getAttrFromJson(String json, String attribute){
        try{
            return (new JSONObject(json)).getString(attribute);
        }
        catch (Exception e){
            return "{Exception: " + e.getMessage() +"}";
        }
    }
}
