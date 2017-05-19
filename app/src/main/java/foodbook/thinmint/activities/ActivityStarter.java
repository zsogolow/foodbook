package foodbook.thinmint.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import foodbook.thinmint.activities.notes.CreateNoteActivity;
import foodbook.thinmint.activities.users.UserActivity;

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
}
