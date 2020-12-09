package com.variant_gson.core

import com.variant_gson.core.internal.bind.JsonTreeReader
import com.variant_gson.core.internal.bind.JsonTreeWriter
import com.variant_gson.core.stream.JsonReader
import com.variant_gson.core.stream.JsonToken
import com.variant_gson.core.stream.JsonWriter
import java.io.*


/**
 * Converts Java objects to and from JSON.
 *
 *
 * Defining a type's JSON form
 * By default Gson converts application classes to JSON using its built-in type
 * adapters. If Gson's default JSON conversion isn't appropriate for a type,
 * extend this class to customize the conversion. Here's an example of a type
 * adapter for an (X,Y) coordinate point:
 * ```Java
 *   public class PointAdapter extends TypeAdapter<Point> {
 *     public Point read(JsonReader reader) throws IOException {
 *       if (reader.peek() == JsonToken.NULL) {
 *         reader.nextNull();
 *         return null;
 *       }
 *       String xy = reader.nextString();
 *       String[] parts = xy.split(",");
 *       int x = Integer.parseInt(parts[0]);
 *       int y = Integer.parseInt(parts[1]);
 *       return new Point(x, y);
 *     }
 *     public void write(JsonWriter writer, Point value) throws IOException {
 *       if (value == null) {
 *         writer.nullValue();
 *         return;
 *       }
 *       String xy = value.getX() + "," + value.getY();
 *       writer.value(xy);
 *     }
 *   }}
 * ```
 * With this type adapter installed, Gson will convert {@code Points} to JSON as
 * strings like `"5,8"` rather than objects like `{"x":5,"y":8}`. In
 * this case the type adapter binds a rich Java class to a compact JSON value.
 *
 * The [read(JsonReader) read()] method must read exactly one value
 * and [write(JsonWriter,Object) write()] must write exactly one value.
 * For primitive types this is means readers should make exactly one call to
 * [nextBoolean()], [nextDouble()], [nextInt()], [nextLong()],
 * [nextString()] or [nextNull()]. Writers should make
 * exactly one call to one of `value()` or `nullValue()`.
 * For arrays, type adapters should start with a call to [beginArray()],
 * convert all elements, and finish with a call to [code endArray()]. For
 * objects, they should start with [beginObject()], convert the object,
 * and finish with [endObject()]. Failing to convert a value or converting
 * too many values may cause the application to crash.
 *
 *
 * Type adapters should be prepared to read null from the stream and write it
 * to the stream. Alternatively, they should use [nullSafe()] method while
 * registering the type adapter with Gson. If your {@code Gson} instance
 * has been configured to [GsonBuilder#serializeNulls()], these nulls will be
 * written to the final document. Otherwise the value (and the corresponding name
 * when writing to a JSON object) will be omitted automatically. In either case
 * your type adapter must handle null.
 *
 *
 * To use a custom type adapter with Gson, you must ***register*** it with a
 * [GsonBuilder]:
 * ``` Java
 *   GsonBuilder builder = new GsonBuilder();
 *   builder.registerTypeAdapter(Point.class, new PointAdapter());
 *   // if PointAdapter didn't check for nulls in its read/write methods, you should instead use
 *   // builder.registerTypeAdapter(Point.class, new PointAdapter().nullSafe());
 *   ...
 *   Gson gson = builder.create();
 * ```
 * @since 2.1
 */
abstract class TypeAdapter<T> {


    /**
     * Writes one JSON value (an array, object, string, number, boolean or null)
     * for [value].
     *
     * @param value the Java object to write. May be null.
     */
    @Throws(IOException::class)
    abstract fun write(out: JsonWriter, value: T?)

    /**
     * Reads one JSON value (an array, object, string, number, boolean or null)
     * and converts it to a Java object. Returns the converted object.
     *
     * @return the converted Java object. May be null.
     */
    @Throws(IOException::class)
    abstract fun read(`in`: JsonReader): T?

    /**
     * This wrapper method is used to make a type adapter null tolerant. In general, a
     * type adapter is required to handle nulls in write and read methods. Here is how this
     * is typically done:
     * ```Java
     * Gson gson = new GsonBuilder().registerTypeAdapter(Foo.class,
     *   new TypeAdapter<Foo>() {
     *     public Foo read(JsonReader in) throws IOException {
     *       if (in.peek() == JsonToken.NULL) {
     *         in.nextNull();
     *         return null;
     *       }
     *       // read a Foo from in and return it
     *     }
     *     public void write(JsonWriter out, Foo src) throws IOException {
     *       if (src == null) {
     *         out.nullValue();
     *         return;
     *       }
     *       // write src as JSON to out
     *     }
     *   }).create();
     * }
     * ```
     *
     *
     * You can avoid this boilerplate handling of nulls by wrapping your type adapter with
     * this method. Here is how we will rewrite the above example:
     * ```Java
     * Gson gson = new GsonBuilder().registerTypeAdapter(Foo.class,
     *   new TypeAdapter<Foo>() {
     *     public Foo read(JsonReader in) throws IOException {
     *       // read a Foo from in and return it
     *     }
     *     public void write(JsonWriter out, Foo src) throws IOException {
     *       // write src as JSON to out
     *     }
     *   }.nullSafe()).create();
     * }
     * ```
     * Note that we didn't need to check for nulls in our type adapter after we used nullSafe.
     */
    fun nullSafe(): TypeAdapter<T> = object : TypeAdapter<T>() {
        override fun write(out: JsonWriter, value: T?) {
            if (value == null) {
                out.nullValue()
            } else {
                write(out, value)
            }
        }

        override fun read(`in`: JsonReader): T? {
            if (`in`.peek() == JsonToken.NULL) {
                `in`.nextNull()
                return null
            }
            return read(`in`)
        }
    }

    /**
     * Converts [value] to a JSON document and writes it to [out].
     * Unlike Gson's similar [Gson.toJson(JsonElement, Appendable) toJson}]
     * method, this write is strict. Create a [JsonWriter.setLenient(boolean) lenient]
     * [JsonWriter] and call [write(com.google.gson.stream.JsonWriter, Object)] for lenient writing.
     *
     * @param value the Java object to convert. May be null.
     * @since 2.2
     */
    @Throws(IOException::class)
    fun toJson(out: Writer, value: T) {
        val writer = JsonWriter(out)
        write(writer, value)
    }


    /**
     * Converts [value] to a JSON document. Unlike Gson's similar [Gson.toJson(object)] method,
     * this write is strict. Create a [JsonWriter#setLenient(boolean) lenient] [JsonWriter] and call
     * [write] writing.
     *
     * @param value the Java object to convert. May be null.
     * @since 2.2
     */
    fun toJson(value: T): String {
        val stringWriter = StringWriter()
        try {
            toJson(stringWriter, value)
        } catch (e: IOException) {
            throw AssertionError(e) // No I/O writing to a StringWriter.
        }

        return stringWriter.toString()
    }


    /**
     * Converts `value` to a JSON tree.
     *
     * @param value the Java object to convert. May be null.
     * @return the converted JSON tree. May be [JsonNull].
     * @since 2.2
     */
    fun toJsonTree(value: T): JsonElement {
        return try {
            val jsonWriter = JsonTreeWriter()
            write(jsonWriter, value)
            jsonWriter.get()
        } catch (e: IOException) {
            throw JsonIOException(e)
        }
    }

    /**
     * Converts the JSON document in `in` to a Java object. Unlike Gson's
     * similar [fromJson][Gson.fromJson] method, this
     * read is strict. Create a [lenient][JsonReader.setLenient]
     * `JsonReader` and call [.read] for lenient reading.
     *
     * @return the converted Java object. May be null.
     * @since 2.2
     */
    @Throws(IOException::class)
    fun fromJson(`in`: Reader?): T? {
        val reader = JsonReader(`in`)
        return read(reader)
    }

    /**
     * Converts the JSON document in `json` to a Java object. Unlike Gson's
     * similar [fromJson][Gson.fromJson] method, this read is
     * strict. Create a [lenient][JsonReader.setLenient] `JsonReader` and call [.read] for lenient reading.
     *
     * @return the converted Java object. May be null.
     * @since 2.2
     */
    @Throws(IOException::class)
    fun fromJson(json: String): T? {
        return fromJson(StringReader(json))
    }

    /**
     * Converts `jsonTree` to a Java object.
     *
     * @param jsonTree the Java object to convert. May be [JsonNull].
     * @since 2.2
     */
    fun fromJsonTree(jsonTree: JsonElement): T? {
        return try {
            val jsonReader: JsonReader = JsonTreeReader(jsonTree)
            read(jsonReader)
        } catch (e: IOException) {
            throw JsonIOException(e)
        }
    }
}