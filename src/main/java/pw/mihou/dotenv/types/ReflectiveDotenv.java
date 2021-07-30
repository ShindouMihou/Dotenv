package pw.mihou.dotenv.types;

import pw.mihou.dotenv.adapters.ReflectiveAdapter;
import pw.mihou.dotenv.annotations.DoNotReflect;
import pw.mihou.dotenv.annotations.EnvironmentItem;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * A reflective dotenv refers to a class in this library
 * that utilizes reflection to load the set of variables onto
 * a specific class, adding a layer of convenience to the user.
 */
public class ReflectiveDotenv extends NormalDotenv {

    private static final Map<Class, ReflectiveAdapter> adapters = new HashMap<>();

    /**
     * Creates a new simple Reflective dotenv that utilizes
     * reflection to load the set of variables onto a specific class.
     * @param file The file to load.
     * @param loadSystemEnv Whether to load the system environmental variables
     *                      as a fallback value.
     */
    public ReflectiveDotenv(File file, boolean loadSystemEnv) {
        super(file, loadSystemEnv);
    }

    /**
     * Creates a new simple Reflective dotenv that utilizes
     * reflection to load the set of variables onto a specific class,
     * this doesn't utilize the system environmental variables by default, but
     * you can change that way through {@link ReflectiveDotenv#ReflectiveDotenv(File, boolean)}.
     */
    public ReflectiveDotenv() {
        super(new File(".env"), false);
    }

    /**
     * Adds an adapter to the static list of adapters that {@link ReflectiveDotenv} will
     * use when the field type is unknown.
     *
     * @param adapter The adapter to use.
     * @param type The type of class this adapter should be used.
     */
    public static void addAdapter(ReflectiveAdapter<?> adapter, Class<?> type) {
        adapters.put(type, adapter);
    }

    /**
     * Loads the variables as long as it is within the following
     * types: character, long, integer, string, boolean, double. Anything beyond
     * will require {@link DoNotReflect} annotation to be parsed by the user
     * themselves.
     *
     * <br>
     * This requires the {@link Class} to have static variables as this
     * method will only fill in the static variables.
     * @param tClass The class to reflect to, this must contain static
     *               variables as this method will only fill in static variables.
     */
    public void reflectTo(Class<?> tClass) {
        Arrays.stream(tClass.getDeclaredFields())
                .filter(field -> Modifier.isStatic(field.getModifiers()))
                .forEach(field -> {
            field.setAccessible(true);
            if(!field.isAnnotationPresent(DoNotReflect.class)) {
                String name = field.isAnnotationPresent(EnvironmentItem.class) ?
                        field.getAnnotation(EnvironmentItem.class).key() : field.getName();
                try {
                    if (!isNull(name)) {
                        Class<?> t = field.getType();
                        if (t.equals(Boolean.class) || t.equals(boolean.class))
                            field.setBoolean(field, Boolean.parseBoolean(get(name)));
                        else if (t.equals(Integer.class) || t.equals(int.class))
                            field.setInt(field, Integer.parseInt(get(name)));
                        else if(t.equals(Long.class) || t.equals(long.class))
                            field.setLong(field, Long.parseLong(get(name)));
                        else if(t.equals(Character.class) || t.equals(char.class))
                            field.setChar(field, get(name).charAt(0));
                        else if(t.equals(String.class))
                            field.set(field, get(name));
                        else if(t.equals(Double.class) || t.equals(double.class))
                            field.set(field, Double.parseDouble(get(name)));
                        else if(adapters.keySet().stream().anyMatch(t::equals))
                            field.set(field, adapters.entrySet().stream().filter(e -> e.getKey().equals(t))
                                            .map(Map.Entry::getValue).findFirst().get().parse(get(name), asMap()));
                        else
                            throw new IllegalArgumentException("We couldn't identify the variable " + field.getName() + "'s type, please use " +
                                    "DoNotReflect annotation and parse it yourself otherwise please create a ReflectiveAdapter for it.");
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Generates an .env from an object with the help of reflection,
     * the values can be customized through {@link EnvironmentItem} and can be
     * done without the annotation as it would default to the name of the variable.
     *
     * For example, if you have a variable like:
     * <code>String key;</code>
     *
     * This method would then generate the following line:
     * <code>key=</code>
     *
     * You can also customize this behavior by adding the {@link EnvironmentItem} annotation
     * to the variable.
     *
     * @param object The object to parse.
     * @return A new .env config.
     */
    public String create(Object object) {
        return create(object.getClass());
    }

    /**
     * Generates an .env from an object with the help of reflection,
     * the values can be customized through {@link EnvironmentItem} and can be
     * done without the annotation as it would default to the name of the variable.
     *
     * For example, if you have a variable like:
     * <code>String key;</code>
     *
     * This method would then generate the following line:
     * <code>key=</code>
     *
     * You can also customize this behavior by adding the {@link EnvironmentItem} annotation
     * to the variable.
     *
     * @param object The object to parse.
     * @return A new .env config.
     */
    public String create(Class<?> object) {
        StringBuilder builder = new StringBuilder();
        Arrays.stream(object.getDeclaredFields())
                .forEach(field -> {
                    field.setAccessible(true);
                    String name = field.getName();
                    String comment = "";
                    String value = "";
                    if(field.isAnnotationPresent(EnvironmentItem.class)) {
                        EnvironmentItem i = field.getAnnotation(EnvironmentItem.class);
                        if(!i.comment().isEmpty())
                            comment = i.comment();

                        if(!i.key().isEmpty())
                            name = i.key();

                        if(!i.value().isEmpty())
                            value = i.value();
                    }

                    if(!comment.equals(""))
                        builder.append("# ").append(comment).append("\n");

                    builder.append(name).append("=");

                    if(!value.equals(""))
                        builder.append(value).append("\n");
                    else
                        builder.append("\n");
                });

        return builder.toString();
    }

}
