package foodbook.thinmint.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import foodbook.thinmint.activities.notes.CreateNoteActivity;
import foodbook.thinmint.activities.users.UserActivity;
import foodbook.thinmint.constants.Constants;

/**
 * Created by Zachery.Sogolow on 5/10/2017.
 */

public class ActivityStarter {

    public static void startLogin(Activity activity) {
        Intent loginActivity = new Intent(activity, LoginActivity.class);
        activity.startActivity(loginActivity);
        activity.finish();
    }

    public static void finishLogin(Activity activity) {
        Intent feedActivity = new Intent(activity, MainActivity.class);
        activity.startActivity(feedActivity);
        activity.finish();
    }

    public static void startCreateNoteActivity(Activity activity) {
        Intent createNoteIntent = new Intent(activity, CreateNoteActivity.class);
        activity.startActivity(createNoteIntent);
    }

    public static void startUserActivity(Activity activity, String userSubject, String username) {
        Intent userIntent = new Intent(activity, UserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("user_subject", userSubject);
        bundle.putString("user_name", username);
        userIntent.putExtras(bundle);
        activity.startActivity(userIntent);
    }

    public static void logout(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        prefs.edit().putString(Constants.ACCESS_TOKEN_PREFERENCE_KEY, "").apply();
        Intent loginActivity = new Intent(activity, LoginActivity.class);
        activity.startActivity(loginActivity);
        activity.finish();
    }
}
