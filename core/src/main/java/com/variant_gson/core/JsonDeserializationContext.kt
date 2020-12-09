package com.variant_gson.core

import java.lang.reflect.Type

/**
 * Context for deserialization that is passed to a custom deserializer during invocation of its
 * [JsonDeserializer#deserialize(JsonElement, Type, JsonDeserializationContext)]
 * method.
 */
interface JsonDeserializationContext {
    /**
     * Invokes default deserialization on the specified object. It should never be invoked on
     * the element received as a parameter of the
     * [JsonDeserializer.deserialize] method. Doing
     * so will result in an infinite loop since Gson will in-turn call the custom deserializer again.
     *
     * @param json the parse tree.
     * @param typeOfT type of the expected return value.
     * @param <T> The type of the deserialized object.
     * @return An object of type typeOfT.
     * @throws JsonParseException if the parse tree does not contain expected data.
    </T> */
    @Throws(JsonParseException::class)
    fun <T> deserialize(json: JsonElement?, typeOfT: Type?): T
}