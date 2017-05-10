package foodbook.thinmint.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
}
