package foodbook.thinmint.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import foodbook.thinmint.models.domain.EntityBase;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.models.domain.User;

/**
 * Created by Zachery.Sogolow on 5/10/2017.
 */

public class JsonHelper {

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

    private static void updateDate(EntityBase entity) {
        Date localTime = new Date(entity.getDateCreated().getTime() +
                TimeZone.getDefault().getOffset(System.currentTimeMillis()));
        entity.setDateCreated(localTime);
    }

    public static User getUser(String json) {
        User usr = gson.fromJson(json, User.class);
        updateDate(usr);
        return usr;
    }


    public static List<User> getUsers(String json) {
        List<User> usrs = gson.fromJson(json, new TypeToken<List<User>>() {
        }.getType());
        for (EntityBase base : usrs) {
            updateDate(base);
        }
        return usrs;
    }

    public static Note getNote(String json) {
        Note note = gson.fromJson(json, Note.class);
        updateDate(note);
        return note;
    }

    public static List<Note> getNotes(String json) {
        List<Note> notes = gson.fromJson(json, new TypeToken<List<Note>>() {
        }.getType());
        for (EntityBase base : notes) {
            updateDate(base);
        }
        return notes;
    }

    private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        if (type == null) {
            return fields;
        }

        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        return getAllFields(fields, type.getSuperclass());
    }
}
