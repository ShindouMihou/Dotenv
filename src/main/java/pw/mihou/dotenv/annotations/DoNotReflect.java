package pw.mihou.dotenv.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is used to tell {@link pw.mihou.dotenv.types.ReflectiveDotenv} that
 * this variable shouldn't be filled and should be left to its own discretion,
 * we recommend using {@link pw.mihou.dotenv.adapters.ReflectiveAdapter} if you want
 * it to be parsed your way though.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DoNotReflect {
}
