package foodbook.thinmint.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Zachery.Sogolow on 5/10/2017.
 */

public class ObjectFactory<T> {

    public T Deserialize(T instance, String json) throws ParseException {

        T object = null;

        try {
            JSONObject jsonObject = new JSONObject(json);
            Class<?> instanceClass = instance.getClass();
            object = (T) instanceClass.newInstance();


            Field[] fields = instanceClass.getDeclaredFields();

            for (Field field : fields) {
                JsonField jsonAnnotation = field.getAnnotation(JsonField.class);
                if (jsonAnnotation != null && jsonAnnotation.isAccessible()) {
                    field.setAccessible(true);
                    Method method = jsonObject.getClass().getMethod(jsonAnnotation.jsonGetMethod(), String.class);
                    Object value = method.invoke(jsonObject, jsonAnnotation.name());
                    field.set(object, value);
                }
            }

        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | NullPointerException | JSONException e) {
            throw new ParseException(e);
        }

        return object;
    }

    public List<T> DeserializeCollection(T instance, String json) throws ParseException {
        List<T> collection = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            Class<?> instanceClass = instance.getClass();

            Field[] fields = instanceClass.getDeclaredFields();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                T object = (T) instanceClass.newInstance();

                for (Field field : fields) {
                    JsonField jsonAnnotation = field.getAnnotation(JsonField.class);
                    if (jsonAnnotation != null && jsonAnnotation.isAccessible()) {
                        field.setAccessible(true);
                        if (jsonAnnotation.isNav()) {
                            Class<?> t = Class.forName(jsonAnnotation.objectType());
                            field.set(object, new ObjectFactory().Deserialize(t.newInstance(), jsonObject.getString(jsonAnnotation.name())));
                        } else {
                            Method method = jsonObject.getClass().getMethod(jsonAnnotation.jsonGetMethod(), String.class);
                            Object value = method.invoke(jsonObject, jsonAnnotation.name());
                            field.set(object, value);
                        }
                    }
                }

                collection.add(object);
            }
        } catch (InstantiationException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | NullPointerException | JSONException e) {
            throw new ParseException(e);
        }

        return collection;
    }
}
