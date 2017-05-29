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

import foodbook.thinmint.models.domain.Comment;
import foodbook.thinmint.models.domain.EntityBase;
import foodbook.thinmint.models.domain.Like;
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
            users = gson.fromJson(json, new TypeToken<List<User>>() {
            }.getType());
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
            for (Comment comment : note.getComments()) {
                updateDate(comment);
            }
        } catch (JsonSyntaxException jse) {
            note = null;
        }
        return note;
    }

    public static List<Note> getNotes(String json) {
        List<Note> notes;
        try {
            notes = gson.fromJson(json, new TypeToken<List<Note>>() {
            }.getType());
            for (Note base : notes) {
                updateDate(base);
                for (Comment comment : base.getComments()) {
                    updateDate(comment);
                }
            }
        } catch (JsonSyntaxException jse) {
            notes = null;
        }

        return notes;
    }

    public static Comment getComment(String json) {
        Comment comment;
        try {
            comment = gson.fromJson(json, Comment.class);
            updateDate(comment);
        } catch (JsonSyntaxException jse) {
            comment = null;
        }
        return comment;
    }

    public static Like getLike(String json) {
        Like like;
        try {
            like = gson.fromJson(json, Like.class);
            updateDate(like);
        } catch (JsonSyntaxException jse) {
            like = null;
        }
        return like;
    }

    public static List<Comment> getComments(String json) {
        List<Comment> comments;
        try {
            comments = gson.fromJson(json, new TypeToken<List<Comment>>() {
            }.getType());
            for (EntityBase base : comments) {
                updateDate(base);
            }
        } catch (JsonSyntaxException jse) {
            comments = null;
        }
        return comments;
    }

    private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        if (type == null) {
            return fields;
        }

        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        return getAllFields(fields, type.getSuperclass());
    }
}
