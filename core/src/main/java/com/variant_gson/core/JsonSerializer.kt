package com.variant_gson.core

import java.lang.reflect.Type


/**
 * Interface representing a custom serializer for Json. You should write a custom serializer, if
 * you are not happy with the default serialization done by Gson. You will also need to register
 * this serializer through [GsonBuilder.registerTypeAdapter(Type, Object)].
 *
 *
 * Let us look at example where defining a serializer will be useful. The `Id` class
 * defined below has two fields: `clazz` and `value`.
 *
 * ```
 * public class Id&lt;T&gt; {
 *   private final Class&lt;T&gt; clazz;
 *   private final long value;
 *
 *   public Id(Class&lt;T&gt; clazz, long value) {
 *     this.clazz = clazz;
 *     this.value = value;
 *   }
 *
 *   public long getValue() {
 *     return value;
 *   }
 * }
 * ```
 *
 *
 * The default serialization of `Id(com.foo.MyObject.class, 20L)` will be
 * `{"clazz":com.foo.MyObject,"value":20}`. Suppose, you just want the output to be
 * the value instead, which is `20` in this case. You can achieve that by writing a custom
 * serializer:
 *
 * ``` Java
 * class IdSerializer implements JsonSerializer&lt;Id&gt;() {
 *   public JsonElement serialize(Id id, Type typeOfId, JsonSerializationContext context) {
 *     return new JsonPrimitive(id.getValue());
 *   }
 * }
 * ```
 *
 *
 * You will also need to register [IdSerializer] with Gson as follows:
 * ``` Java
 * Gson gson = new GsonBuilder().registerTypeAdapter(Id.class, new IdSerializer()).create();
 * ```
 *
 *
 * New applications should prefer [TypeAdapter], whose streaming API
 * is more efficient than this interface's tree API.
 * @param <T> type for which the serializer is being registered. It is possible that a serializer
 *        may be asked to serialize a specific generic type of the T.
 */
interface JsonSerializer<T> {
    /**
     * Gson invokes this call-back method during serialization when it encounters a field of the
     * specified type.
     *
     *
     * In the implementation of this call-back method, you should consider invoking
     * [JsonSerializationContext.serialize] method to create JsonElements for any
     * non-trivial field of the `src` object. However, you should never invoke it on the
     * `src` object itself since that will cause an infinite loop (Gson will call your
     * call-back method again).
     *
     * @param src the object that needs to be converted to Json.
     * @param typeOfSrc the actual type (fully genericized version) of the source object.
     * @return a JsonElement corresponding to the specified object.
     */
    fun serialize(src: T, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement?
}