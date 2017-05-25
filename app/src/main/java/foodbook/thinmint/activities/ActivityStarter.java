package foodbook.thinmint.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;

import foodbook.thinmint.activities.common.RequestCodes;
import foodbook.thinmint.activities.notes.CommentsActivity;
import foodbook.thinmint.activities.notes.CreateNoteActivity;
import foodbook.thinmint.activities.notes.NoteActivity;
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

    public static void startCreateNoteActivityForResult(Activity activity) {
        Intent createNoteIntent = new Intent(activity, CreateNoteActivity.class);
        activity.startActivityForResult(createNoteIntent, RequestCodes.CREATE_NOTE_REQUEST_CODE);
    }

    public static void startNoteActivity(Activity activity, long noteId) {
        Intent noteIntent = new Intent(activity, NoteActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("note_id", noteId);
        noteIntent.putExtras(bundle);
        activity.startActivity(noteIntent);
    }

    public static void startNoteActivityForResult(Activity activity, long noteId, int requestCode) {
        Intent noteIntent = new Intent(activity, NoteActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("note_id", noteId);
        noteIntent.putExtras(bundle);
        activity.startActivityForResult(noteIntent, requestCode);
    }

    public static void startNoteActivityForResult(Fragment fragment, long noteId, int requestCode) {
        Intent noteIntent = new Intent(fragment.getActivity(), NoteActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("note_id", noteId);
        noteIntent.putExtras(bundle);
        fragment.startActivityForResult(noteIntent, requestCode);
    }

    public static void startCommentsActivity(Activity activity, long noteId) {
        Intent noteIntent = new Intent(activity, CommentsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("note_id", noteId);
        noteIntent.putExtras(bundle);
        activity.startActivity(noteIntent);
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

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
