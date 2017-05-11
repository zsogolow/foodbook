package foodbook.thinmint.models;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Zachery.Sogolow on 5/10/2017.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface JsonField {
    String name();
    String jsonGetMethod();
    boolean isAccessible();
    boolean isNav() default false;
    String objectType() default "";
}
