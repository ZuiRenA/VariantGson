package com.variant_gson.core

import java.lang.reflect.Type

/**
 * Interface representing a custom deserializer for Json. You should write a custom
 * deserializer, if you are not happy with the default deserialization done by Gson. You will
 * also need to register this deserializer through
 * [GsonBuilder.registerTypeAdapter(Type, Object)].
 *
 *
 * Let us look at example where defining a deserializer will be useful. The `Id` class
 * defined below has two fields: `clazz` and `value`.
 *
 * ``` Java
 * public class Id&lt;T&gt; {
 *   private final Class&lt;T&gt; clazz;
 *   private final long value;
 *   public Id(Class&lt;T&gt; clazz, long value) {
 *     this.clazz = clazz;
 *     this.value = value;
 *   }
 *   public long getValue() {
 *     return value;
 *   }
 * }
 * ```
 *
 * <p>The default deserialization of `Id(com.foo.MyObject.class, 20L)` will require the
 * Json string to be `{"clazz":com.foo.MyObject,"value":20}`. Suppose, you already know
 * the type of the field that the `Id` will be deserialized into, and hence just want to
 * deserialize it from a Json string `20`. You can achieve that by writing a custom
 * deserializer:
 * ``` Java
 * class IdDeserializer implements JsonDeserializer&lt;Id&gt;() {
 *   public Id deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
 *       throws JsonParseException {
 *     return new Id((Class)typeOfT, id.getValue());
 *   }
 * ```
 *
 *
 * You will also need to register `IdDeserializer` with Gson as follows:
 *
 * ``` Java
 * Gson gson = new GsonBuilder().registerTypeAdapter(Id.class, new IdDeserializer()).create();
 * ```
 *
 *
 * New applications should prefer {@link TypeAdapter}, whose streaming API
 * is more efficient than this interface's tree API.
 *
 * @param T type for which the deserializer is being registered. It is possible that a
 * deserializer may be asked to deserialize a specific generic type of the T.
 */
interface JsonDeserializer<T> {
    /**
     * Gson invokes this call-back method during deserialization when it encounters a field of the
     * specified type.
     *
     * In the implementation of this call-back method, you should consider invoking
     * [JsonDeserializationContext.deserialize] method to create objects
     * for any non-trivial field of the returned object. However, you should never invoke it on the
     * the same type passing `json` since that will cause an infinite loop (Gson will call your
     * call-back method again).
     *
     * @param json The Json data being deserialized
     * @param typeOfT The type of the Object to deserialize to
     * @return a deserialized object of the specified type typeOfT which is a subclass of `T`
     * @throws JsonParseException if json is not in the expected format of `typeofT`
     */
    @Throws(JsonParseException::class)
    fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): T
}
