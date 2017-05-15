package foodbook.thinmint.models.factories;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import foodbook.thinmint.models.domain.User;
import foodbook.thinmint.tasks.UserInfoCallback;

/**
 * Created by ZachS on 5/14/2017.
 */

public class UserFactory {
    public static User createUser(long id, Date dateCreated, String userSubject, String userName) {
        User user = new User();

        user.setId(id);
        user.setDateCreated(dateCreated);
        user.setSubject(userSubject);
        user.setUsername(userName);

        return user;
    }
}
