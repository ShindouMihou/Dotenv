## :tada: Dotenv
A simple, dependency-free dotenv serializator and deserializator for Java that is able to use
reflection to fill in variables for you.

## :package: What makes this different from the other Dotenv library?
This dotenv library utilizes Reflection to automatically fill in variables to your specific class similar to
how GSON can deserialize a JSON into an Object, this does the same but only works for static variables as our thought process is that `.env` is most likely used for 
configuration and this usually means the variables are usually static (~~also code limitations~~).

## :cake: Installation
You can easily install this library from Central Maven:
### Maven
```xml
<dependency>
  <groupId>pw.mihou</groupId>
  <artifactId>Dotenv</artifactId>
  <version>1.0.1</version>
</dependency>
```

### Gradle
```gradle
implementation 'pw.mihou:Dotenv:1.0.1'
```

## 💬 How to use this?
To use this library, you have to understand the two types of Dotenv parsers that exists here (`Normal` and `Reflective`), both have pretty much the same methods
as `Reflective` extends over `Normal` allowing it to have the same methods as `Normal` but compared to `Normal`, it has methods like `reflectTo(...)` and `create(...)` which
utilizes Reflection to reflect variables onto their part and also to create a `.env` from an Object.

If you want to use the normal parser, you can simply create a new instance through:
```java
NormalDotEnv dotEnv = Dotenv.as();
```

The `.as()` loads the default settings which are:
- It loads the environments from the `.env` file located on the **ROOT-LEVEL** of the directory.
- It does not fallback to the System Environmental Variables if a variable is not there on `.env`.

You can customize it to support them through the methods:
```java
NormalDotEnv dotEnv = Dotenv.as(new File("/path/to/someEnvFile.env"), true);
```

To make a Reflective Dotenv, you can easily cast over NormalDotEnv as internally, they are both the same or you
could create a new Dotenv instance the proper way, for example:
```java
ReflectiveDotenv dotEnv = Dotenv.asReflective();
```

The same methods and rules applies to the above, after creating the instance, all the variables inside the `.env` should have
been loaded into the Dotenv instance which allows you to retrieve them through the method:
```java
String value = dotEnv.get("someKey");
```

## ✨ Deserializing with Reflective.
If you don't want to spend a pile load of time adding a method that parses and sets the variables onto a Class, then feel free to use `ReflectiveDotEnv` which is what we 
recommend to use over `NormalDotenv` and also is what makes this library different from others. You can easily set this up by simply doing:
```java
dotEnv.reflectTo(SomeConfiguration.class);
```

By default, the reflector can parse the following types: `character`, `long`, `integer`, `string`, `boolean`, `double` but you can make your own `ReflectiveAdapter` to parse 
your own entity, for example:

### SomeEntity.class
```java
class SomeEntity {
  
  public String response;
  
  public SomeEntity(String keyA, String keyB) {
    this.response = keyB + " " + keyA;
  }

}
```

If we want the reflective dotenv to be able to parse the value into that field, then all we have to do is add a `ReflectiveAdapter`:

### SomeEntityAdapter.class
```java
class SomeEntityAdapter implements ReflectiveAdapter<SomeEntity> {

  public SomeEntity parse(String value, Map<String, String> map) {
    return new SomeEntity(value, map.get("anotherKey"));
  }

}
```

### Main.class
```java
public void main(String[] args) {
  ReflectiveDotenv.addAdapter(new SomeEntityAdapter(), SomeEntity.class);
}
```

You could also simplify this into one line, for example:
```java
public void main(String[] args) {
  ReflectiveDotenv.addAdapter((value, map) -> new SomeEntity(value, map.get("anotherKey")), SomeEntity.class);
}
```

After which, `ReflectiveDotenv` should be able to parse the `SomeEntity` class from hereon as the adapters are added statically (which means it is universal for all instances
of ReflectiveDotenv), now let us try to parse this `.env` into the `Configuration` class:

### .env
```env
key=ARandomKey
anotherKey=ARandomValue
```

### Configuration.class
```java
class Configuration {

  @EnvironmentItem(key = "key")
  public static String someKey;
  public static SomeEntity someEntity;

}
```

### Main.class
```java
public void main(String[] args) {
  ReflectiveDotenv.addAdapter((value, map) -> new SomeEntity(value, map.get("anotherKey")), SomeEntity.class);
  Dotenv.asReflective().reflectTo(Configuration.class);
  System.out.println(Configuration.someKey);
  System.out.println(Configuration.someEntity.response);
}
```

The expected output of this should be:
```terminal
ARandomKey
ARandomValue ARandomKey
```

Now, you might have noticed the annotation `EnvironmentItem` which is a special annotation used by this library to parse or generate environment variables. For parsing, 
the only value supported on the annotation is `key` but you can also set the `comment` and `value` values of the annotation to specify what comment should be generated on 
generation of a new `.env` file when you call `ReflectiveDotenv#create(...)`, please read below for more information.

## 🔥 Generating an .env format
You can easily generate a new `.env` with the help of this library using reflection, similar to the deserialization. For example, we have a Configuration class like:
```java
class Configuration {

  @EnvironmentItem(key = "key", value = "noValue", comment = "This is just some key")
  public static String someKey;
  @EnvironmentItem(value = "0", comment = "This is just some integer")
  public static int someInt;

}
```

You can easily convert this into a `.env` through:
```java
reflectiveDotEnv.create(Configuration.class);
```

And it should output the following:
```env
# This is just some key
key=noValue
# This is just some integer
someInt=0
```
