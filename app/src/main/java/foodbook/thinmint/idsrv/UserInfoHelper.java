package foodbook.thinmint.idsrv;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class UserInfoHelper {

    public static UserInfo getUserInfoFromJson(String json) {
        UserInfo userInfo = new UserInfo();

        userInfo.setSubject(JsonManipulation.getAttrFromJson(json, "sub"));
        userInfo.setGivenName(JsonManipulation.getAttrFromJson(json, "given_name"));
        userInfo.setFamilyName(JsonManipulation.getAttrFromJson(json, "family_name"));

        return userInfo;
    }
}
