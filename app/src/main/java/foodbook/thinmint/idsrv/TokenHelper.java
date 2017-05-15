package foodbook.thinmint.idsrv;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import foodbook.thinmint.constants.Constants;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class TokenHelper {
    public static Token getTokenFromJson(TokenResult tokenResult) {
        Token token = new Token();
        String json = tokenResult.getTokenResult();

        String refreshToken = JsonManipulation.getAttrFromJson(json, Constants.REFRESH_TOKEN_PREFERENCE_KEY);
        String accessToken = JsonManipulation.getAttrFromJson(json, Constants.ACCESS_TOKEN_PREFERENCE_KEY);
        String expiresIn = JsonManipulation.getAttrFromJson(json, Constants.EXPIRES_IN_PREFERENCE_KEY);
        long lastRetrieved = tokenResult.getRetrieved();

        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setExpiresIn(expiresIn);
        token.setLastRetrieved(lastRetrieved);

        return token;
    }

    public static boolean isTokenExpired(Token token) {
        long now = System.currentTimeMillis();
        long expiresInMs = Long.parseLong(token.getExpiresIn()) * 1000;
        long lastRefresh = token.getLastRetrieved();
        return (lastRefresh + expiresInMs) < (now + 5000);
    }

    public static Token getToken(Context context) {
        Token token = new Token();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        token.setAccessToken(prefs.getString(Constants.ACCESS_TOKEN_PREFERENCE_KEY, ""));
        token.setRefreshToken(prefs.getString(Constants.REFRESH_TOKEN_PREFERENCE_KEY, ""));
        token.setExpiresIn(prefs.getString(Constants.EXPIRES_IN_PREFERENCE_KEY, ""));
        token.setLastRetrieved(prefs.getLong(Constants.LAST_RETRIEVED_PREFERENCE_KEY, 0));
        return token;
    }

    public static void copyToken(Token source, Token destination) {
        destination.setExpiresIn(source.getExpiresIn());
        destination.setLastRetrieved(source.getLastRetrieved());
        destination.setAccessToken(source.getAccessToken());
        destination.setRefreshToken(source.getRefreshToken());
    }

    public static Token saveToken(Context context, Token token) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        prefs.edit().putString(Constants.REFRESH_TOKEN_PREFERENCE_KEY, token.getRefreshToken()).apply();
        prefs.edit().putString(Constants.ACCESS_TOKEN_PREFERENCE_KEY, token.getAccessToken()).apply();
        prefs.edit().putString(Constants.EXPIRES_IN_PREFERENCE_KEY, token.getExpiresIn()).apply();
        prefs.edit().putLong(Constants.LAST_RETRIEVED_PREFERENCE_KEY, token.getLastRetrieved()).apply();
        return token;
    }
}
