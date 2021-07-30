package pw.mihou.dotenv.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is used to tell {@link pw.mihou.dotenv.types.ReflectiveDotenv} that
 * the variable has a different setting than what it is shown.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EnvironmentItem {

    /**
     * This is used to indicate that this variable
     * has a different key on the .env file.
     *
     * @return The key.
     */
    String key() default "";

    /**
     * This is used to append a comment to the
     * key-value pair during generation of an .env file.
     *
     * @return The comment to append.
     */
    String comment() default "";

    /**
     * This is used to append a default value to the
     * key-value pair during generation of an .env file.
     *
     * @return The default value to append.
     */
    String value() default "";

}
