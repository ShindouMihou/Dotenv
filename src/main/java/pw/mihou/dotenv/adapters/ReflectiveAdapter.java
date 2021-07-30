package pw.mihou.dotenv.adapters;

import java.util.Map;

public interface ReflectiveAdapter<T> {

    /**
     * This method allows you to parse the environment
     * value yourself that will be filled in by {@link pw.mihou.dotenv.types.ReflectiveDotenv}
     * if the field matches the type set here.
     *
     * @param value The value from the environment.
     * @param map The key-value pair map containing all the environment key-values.
     * @return The value to fill.
     */
    T parse(String value, Map<String, String> map);

}
