package foodbook.thinmint.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
        User user;
        try {
            user = gson.fromJson(json, User.class);
            updateDate(user);
        } catch (JsonSyntaxException jse) {
            user = null;
        }
        return user;
    }


    public static List<User> getUsers(String json) {
        List<User> users;
        try {
            users = gson.fromJson(json, new TypeToken<List<User>>() {}.getType());
            for (EntityBase base : users) {
                updateDate(base);
            }
        } catch (JsonSyntaxException jse) {
            users = null;
        }
        return users;
    }

    public static Note getNote(String json) {
        Note note;
        try {
            note = gson.fromJson(json, Note.class);
            updateDate(note);
        } catch (JsonSyntaxException jse) {
            note = null;
        }
        return note;
    }

    public static List<Note> getNotes(String json) {
        List<Note> notes;
        try {
            notes = gson.fromJson(json, new TypeToken<List<Note>>() {}.getType());
            for (EntityBase base : notes) {
                updateDate(base);
            }
        } catch (JsonSyntaxException jse) {
            notes = null;
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
