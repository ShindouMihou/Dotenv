package pw.mihou.dotenv;

import pw.mihou.dotenv.types.NormalDotenv;
import pw.mihou.dotenv.types.ReflectiveDotenv;

import java.io.File;

public interface Dotenv {

    /**
     * Creates a new Dotenv instance that utilizes Reflection
     * at its core to fill variables into a specific class. This setting
     * by default does not use System Environmental Variables as a fallback.
     *
     * @return A new reflective instance.
     */
    static ReflectiveDotenv asReflective() {
        return new ReflectiveDotenv();
    }

    /**
     * Creates a new Dotenv instance that utilizes Reflection
     * at its core to fill variables into a specific class. This setting
     * by default does not use System Environmental Variables as a fallback.
     *
     * @param file The file to load.
     * @return A new reflective instance.
     */
    static ReflectiveDotenv asReflective(File file) {
        return new ReflectiveDotenv(file, false);
    }

    /**
     * Creates a new Dotenv instance that utilizes Reflection
     * at its core to fill variables into a specific class. This setting
     * by default does not use System Environmental Variables as a fallback.
     *
     * @param file The file to load.
     * @param loadSystemEnv Whether to load the System Environmental Variables
     *                      as a fallback if the .env file does not exist.
     * @return A new reflective instance.
     */
    static ReflectiveDotenv asReflective(File file, boolean loadSystemEnv) {
        return new ReflectiveDotenv(file, loadSystemEnv);
    }

    /**
     * Creates a new Dotenv instance that does not utilizes Reflection
     * at its core to fill variables into a specific class. This setting
     * by default does not use System Environmental Variables as a fallback.
     *
     * @return A new reflective instance.
     */
    static NormalDotenv as() {
        return new ReflectiveDotenv();
    }

    /**
     * Creates a new Dotenv instance that does not utilizes Reflection
     * at its core to fill variables into a specific class. This setting
     * by default does not use System Environmental Variables as a fallback.
     *
     * @param file The file to load.
     * @return A new reflective instance.
     */
    static NormalDotenv as(File file) {
        return new ReflectiveDotenv(file, false);
    }

    /**
     * Creates a new Dotenv instance that does not utilizes Reflection
     * at its core to fill variables into a specific class. This setting
     * by default does not use System Environmental Variables as a fallback.
     *
     * @param file The file to load.
     * @param loadSystemEnv Whether to load the System Environmental Variables
     *                      as a fallback if the .env file does not exist.
     * @return A new reflective instance.
     */
    static NormalDotenv as(File file, boolean loadSystemEnv) {
        return new ReflectiveDotenv(file, loadSystemEnv);
    }

}
