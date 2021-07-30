package pw.mihou.dotenv.types;

import pw.mihou.dotenv.Dotenv;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class NormalDotenv implements Dotenv {

    private final Map<String, String> map = new HashMap<>();
    private final boolean loadSystemEnv;

    /**
     * Creates a new dotenv instance with the
     * file to load specified, also whether to load any
     * System Environmental variables that matches the query
     * as a fallback.
     *
     * @param file The file to parse.
     * @param loadSystemEnv Whether to return back the variable
     *                      from System Environmental Variables as a
     *                      fallback if it doesn't exist on the .env file.
     */
    public NormalDotenv(File file, boolean loadSystemEnv) {
        this.loadSystemEnv = loadSystemEnv;
        try {
            if(file.exists()) {
                try (BufferedReader reader = Files.newBufferedReader(Paths.get(file.getPath()))) {
                    reader.lines()
                            .forEach(s -> {
                                if (!s.contains("=")) // Ignore because this is most likely a comment or a whitespace.
                                    return;

                                // Rule 1. Never too much equals.
                                String[] kv = s.split("=", 2);
                                if (kv.length < 2) {
                                    map.put(kv[0], "");
                                    return;
                                }

                                map.put(kv[0], kv[1]);
                            });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new Dotenv instance that utilizes the
     * default settings such as the default .env file location
     * which should always be on root-level of the directory and also
     * does not utilize the System Environmental Variables as a fallback.
     */
    public NormalDotenv() {
        this(new File(".env"), false);
    }

    /**
     * Gets the value of the key from the .env file, this returns back
     * a null value if the key-value pair does not exist on the .env file and
     * also if the option of loading from system environmental variables is disabled.
     *
     * @param key The key to fetch.
     * @return The value of the key.
     */
    public String get(String key) {
        return loadSystemEnv ? (map.containsKey(key) ? map.get(key) : System.getenv(key)) :
                map.get(key);
    }

    /**
     * An abstraction of {@link Integer#parseInt(String)} paired with {@link NormalDotenv#get(String)}
     * to turn an environmental variable into an integer. Like with {@link NormalDotenv#get(String)}, this may
     * return a null value if the key-value pair does not exist on the .env file and
     * also if the option of loading from system environmental variables is disabled.
     *
     * @param key The key to fetch.
     * @return The integer value if it exists.
     */
    public int getInteger(String key) {
        return Integer.parseInt(get(key));
    }

    /**
     * An abstraction of {@link Long#parseLong(String)} paired with {@link NormalDotenv#get(String)}
     * to turn an environmental variable into a long. Like with {@link NormalDotenv#get(String)}, this may
     * return a null value if the key-value pair does not exist on the .env file and
     * also if the option of loading from system environmental variables is disabled.
     *
     * @param key The key to fetch.
     * @return The long value if it exists.
     */
    public long getLong(String key) {
        return Long.parseLong(get(key));
    }

    /**
     * An abstraction of {@link Boolean#parseBoolean(String)} paired with {@link NormalDotenv#get(String)}
     * to turn an environmental variable into a boolean. Like with {@link NormalDotenv#get(String)}, this may
     * return a null value if the key-value pair does not exist on the .env file and
     * also if the option of loading from system environmental variables is disabled.
     *
     * @param key The key to fetch.
     * @return The boolean value if it exists.
     */
    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    /**
     * An abstraction of {@link Double#parseDouble(String)} paired with {@link NormalDotenv#get(String)}
     * to turn an environmental variable into a double. Like with {@link NormalDotenv#get(String)}, this may
     * return a null value if the key-value pair does not exist on the .env file and
     * also if the option of loading from system environmental variables is disabled.
     *
     * @param key The key to fetch.
     * @return The double value if it exists.
     */
    public double getDouble(String key) {
        return Double.parseDouble(get(key));
    }

    /**
     * Checks if the key-value pair exists on the .env file
     * or on the System Environmental variables (if the option is enabled).
     *
     * @param key The key to fetch.
     * @return Does the key-value pair exist.
     */
    public boolean isNull(String key) {
        return loadSystemEnv ? !map.containsKey(key) && Objects.isNull(System.getenv(key)) :
        !map.containsKey(key);
    }

    /**
     * Returns back a Map form of this dotenv.
     *
     * @return The map form containing all the key-value pairs.
     */
    public Map<String, String> asMap() {
        return map;
    }

    /**
     * Creates a simple .env config with only the key-value pairs,
     * if you want a more advanced usage, then feel free to use {@link pw.mihou.dotenv.types.ReflectiveDotenv#create(Object)}
     * which is a more advanced version of this that utilizes reflection.
     *
     * @param map The map to create.
     * @return A new .env config.
     */
    public String create(Map<String, String> map) {
        StringBuilder builder = new StringBuilder();
        map.forEach((s, s2) -> builder.append(s).append("=").append(s2).append("\n"));
        return builder.toString();
    }

}
