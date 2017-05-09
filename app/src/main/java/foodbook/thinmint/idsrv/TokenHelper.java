package foodbook.thinmint.idsrv;

import foodbook.thinmint.constants.Constants;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class TokenHelper {
    public static Token getTokenFromJson(String json) {
        Token token = new Token();

        String refreshToken = JsonManipulation.getAttrFromJson(json, Constants.REFRESH_TOKEN_PREFERENCE_KEY);
        String accessToken = JsonManipulation.getAttrFromJson(json, Constants.ACCESS_TOKEN_PREFERENCE_KEY);
        String expiresIn = JsonManipulation.getAttrFromJson(json, Constants.EXPIRES_IN_PREFERENCE_KEY);

        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setExpiresIn(expiresIn);

        return token;
    }

    public static boolean isTokenExpired(Token token) {
        long now = System.currentTimeMillis();
        long expiresInMs = Long.parseLong(token.getExpiresIn()) * 1000;
        long lastRefresh = token.getLastRetrieved();
        return (lastRefresh + expiresInMs) < now;
    }
}
