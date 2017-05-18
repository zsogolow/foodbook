package foodbook.thinmint.constants;

/**
 * Created by Zachery.Sogolow on 5/8/2017.
 */

public class Constants {
    public static final String ThinMintAPI = "https://thinmintapi.azurewebsites.net/";

    public static final String IdSrvIssuerUri = "https://thinmintidsrv3/embedded";

    public static final String IdSrv = "https://thinmintidsrv.azurewebsites.net/identity";
    public static final String IdSrvToken = IdSrv + "/connect/token";
    public static final String IdSrvAuthorize = IdSrv + "/connect/authorize";
    public static final String IdSrvUserInfo = IdSrv + "/connect/userinfo";
//    public static final String IdSrvLogout = IdSrv + "/connect/endsession";
//    public static final String IdSrvTokenValidationRequest = IdSrv + "/connect/identitytokenvalidation";
//    public static final String IdSrvTokenRevocation = IdSrv + "/connect/revocation";
//    public static final String IdSrvIntrospection = IdSrv + "/connect/introspect";

    public static final String ACCESS_TOKEN_PREFERENCE_KEY = "access_token";
    public static final String REFRESH_TOKEN_PREFERENCE_KEY = "refresh_token";
    public static final String EXPIRES_IN_PREFERENCE_KEY = "expires_in";
    public static final String LAST_RETRIEVED_PREFERENCE_KEY = "last_retrieved";
    public static final String USER_SUBJECT = "user_subject";
    public static final String USER_NAME= "user_name";
    public static final String USER_ID = "user_id";


    public static final String CLIENT_ID = "android";
    public static final String CLIENT_SECRET = "secret";
}

