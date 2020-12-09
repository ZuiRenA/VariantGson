package com.variant_gson.core

import com.variant_gson.core.internal.bind.JsonTreeReader
import com.variant_gson.core.reflect.TypeToken
import com.variant_gson.core.stream.JsonReader
import java.io.EOFException
import java.io.IOException
import java.lang.reflect.Type
import java.util.*

/**
 * Created by ChaoShen on 2020/12/9
 */
class Gson {

    /**
     * Returns the type adapter for `` type.
     *
     * @throws IllegalArgumentException if this GSON cannot serialize and
     * deserialize `type`.
     */
    fun <T> getAdapter(type: Class<T>?): TypeAdapter<T> {
        // TODO: 2020/12/9 getAdapter
    }

    /**
     * Returns the type adapter for `` type.
     *
     * @throws IllegalArgumentException if this GSON cannot serialize and
     * deserialize `type`.
     */
    fun <T> getAdapter(type: TypeToken<T>?): TypeAdapter<T>? {
        // TODO: 2020/12/9
    }


    /**
     * This method is used to get an alternate type adapter for the specified type. This is used
     * to access a type adapter that is overridden by a {@link TypeAdapterFactory} that you
     * may have registered. This features is typically used when you want to register a type
     * adapter that does a little bit of work but then delegates further processing to the Gson
     * default type adapter. Here is an example:
     *
     *
     * Let's say we want to write a type adapter that counts the number of objects being read
     * from or written to JSON. We can achieve this by writing a type adapter factory that uses
     * the `getDelegateAdapter` method:
     * ``` Java
     *  class StatsTypeAdapterFactory implements TypeAdapterFactory {
     *    public int numReads = 0;
     *    public int numWrites = 0;
     *    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
     *      final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
     *      return new TypeAdapter<T>() {
     *        public void write(JsonWriter out, T value) throws IOException {
     *          ++numWrites;
     *          delegate.write(out, value);
     *        }
     *        public T read(JsonReader in) throws IOException {
     *          ++numReads;
     *          return delegate.read(in);
     *        }
     *      };
     *    }
     *  }
     *  }
     *  ```
     *  This factory can now be used like this:
     *  ``` Java
     *  StatsTypeAdapterFactory stats = new StatsTypeAdapterFactory();
     *  Gson gson = new GsonBuilder().registerTypeAdapterFactory(stats).create();
     *  // Call gson.toJson() and fromJson methods on objects
     *  System.out.println("Num JSON reads" + stats.numReads);
     *  System.out.println("Num JSON writes" + stats.numWrites);
     *  }
     *  ```
     *  Note that this call will skip all factories registered before [skipPast]. In case of
     *  multiple TypeAdapterFactories registered it is up to the caller of this function to insure
     *  that the order of registration does not prevent this method from reaching a factory they
     *  would expect to reply from this call.
     *  Note that since you can not override type adapter factories for String and Java primitive
     *  types, our stats factory will not count the number of String or primitives that will be
     *  read or written.
     * @param skipPast The type adapter factory that needs to be skipped while searching for
     *   a matching type adapter. In most cases, you should just pass this (the type adapter
     *   factory from where [getDelegateAdapter] method is being invoked).
     * @param type Type for which the delegate adapter is being searched for.
     *
     * @since 2.2
     */
    fun <T> getDelegateAdapter(skipPast: TypeAdapterFactory, type: TypeToken<T>): TypeAdapter<T>? {
        // Hack. If the skipPast factory isn't registered, assume the factory is being requested via
        // our @JsonAdapter annotation.
        // TODO: 2020/12/9
    }


    /**
     * This method serializes the specified object into its equivalent representation as a tree of
     * [JsonElement]s. This method should be used when the specified object is not a generic
     * type. This method uses [Class.getClass] to get the type for the specified object, but
     * the `getClass()` loses the generic type information because of the Type Erasure feature
     * of Java. Note that this method works fine if the any of the object fields are of generic type,
     * just the object itself should not be of a generic type. If the object is of generic type, use
     * [.toJsonTree] instead.
     *
     * @param src the object for which Json representation is to be created setting for Gson
     * @return Json representation of `src`.
     * @since 1.4
     */
    fun toJsonTree(src: Any?): JsonElement? {
        return if (src == null) {
            JsonNull.INSTANCE
        } else toJsonTree(src, src.javaClass)
    }

    /**
     * This method serializes the specified object, including those of generic types, into its
     * equivalent representation as a tree of [JsonElement]s. This method must be used if the
     * specified object is a generic type. For non-generic objects, use [.toJsonTree]
     * instead.
     *
     * @param src the object for which JSON representation is to be created
     * @param typeOfSrc The specific genericized type of src. You can obtain
     * this type by using the [com.google.gson.reflect.TypeToken] class. For example,
     * to get the type for `Collection<Foo>`, you should use:
     * <pre>
     * Type typeOfSrc = new TypeToken&lt;Collection&lt;Foo&gt;&gt;(){}.getType();
    </pre> *
     * @return Json representation of `src`
     * @since 1.4
     */
    fun toJsonTree(src: Any?, typeOfSrc: Type?): JsonElement {
        // TODO: 2020/12/9
    }


    /**
     * This method deserializes the Json read from the specified parse tree into an object of the
     * specified type. This method is useful if the specified object is a generic type. For
     * non-generic objects, use [.fromJson] instead.
     *
     * @param <T> the type of the desired object
     * @param json the root of the parse tree of [JsonElement]s from which the object is to
     * be deserialized
     * @param typeOfT The specific genericized type of src. You can obtain this type by using the
     * [com.google.gson.reflect.TypeToken] class. For example, to get the type for
     * `Collection<Foo>`, you should use:
     * <pre>
     * Type typeOfT = new TypeToken&lt;Collection&lt;Foo&gt;&gt;(){}.getType();
    </pre> *
     * @return an object of type T from the json. Returns `null` if `json` is `null`
     * or if `json` is empty.
     * @throws JsonSyntaxException if json is not a valid representation for an object of type typeOfT
     * @since 1.3
    </T> */
    @Throws(JsonSyntaxException::class)
    fun <T> fromJson(json: JsonElement?, typeOfT: Type?): T? {
        return if (json == null) {
            null
        } else fromJson(JsonTreeReader(json), typeOfT) as? T
    }

    /**
     * Reads the next JSON value from `reader` and convert it to an object
     * of type `typeOfT`. Returns `null`, if the `reader` is at EOF.
     * Since Type is not parameterized by T, this method is type unsafe and should be used carefully
     *
     * @throws JsonIOException if there was a problem writing to the Reader
     * @throws JsonSyntaxException if json is not a valid representation for an object of type
     */
    @Throws(JsonIOException::class, JsonSyntaxException::class)
    fun <T> fromJson(reader: JsonReader, typeOfT: Type?): T? {
        // TODO: 2020/12/9
    }
}