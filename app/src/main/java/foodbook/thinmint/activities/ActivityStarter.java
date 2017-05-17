package foodbook.thinmint.activities;

import android.app.Activity;
import android.content.Intent;

import foodbook.thinmint.activities.day.DayActivity;

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
        Intent feedActivity = new Intent(activity, DayActivity.class);
        activity.startActivity(feedActivity);
        activity.finish();
    }
}
