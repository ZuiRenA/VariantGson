package com.variant_gson.core

import com.variant_gson.core.internal.Streams
import com.variant_gson.core.stream.JsonWriter
import java.io.IOException
import java.io.StringWriter
import java.math.BigDecimal
import java.math.BigInteger

/**
 * A class representing an element of Json. It could either be a {@link JsonObject}, a
 * [JsonArray], a [JsonPrimitive] or a [JsonNull].
 */
abstract class JsonElement {

    /**
     * Returns a deep copy of this element. Immutable elements like primitives
     * and nulls are not copied.
     * @since 2.8.2
     */
    abstract fun deepCopy(): JsonElement



    /**
     * provides check for verifying if this element is an array or not.
     *
     * @return true if this element is of type [JsonArray], false otherwise.
     */
    fun isJsonArray() = this is JsonArray

    /**
     * provides check for verifying if this element is a Json object or not.
     *
     * @return true if this element is of type [JsonObject], false otherwise.
     */
    fun isJsonObject(): Boolean {
        return this is JsonObject
    }

    /**
     * provides check for verifying if this element is a primitive or not.
     *
     * @return true if this element is of type [JsonPrimitive], false otherwise.
     */
    fun isJsonPrimitive() = this is JsonPrimitive

    /**
     * provides check for verifying if this element represents a null value or not.
     *
     * @return true if this element is of type [JsonNull], false otherwise.
     * @since 1.2
     */
    fun isJsonNull() = this is JsonNull

    /**
     * convenience method to get this element as a [JsonObject]. If the element is of some
     * other type, a [IllegalStateException] will result. Hence it is best to use this method
     * after ensuring that this element is of the desired type by calling [.isJsonObject]
     * first.
     *
     * @return get this element as a [JsonObject].
     * @throws IllegalStateException if the element is of another type.
     */
    fun getAsJsonObject(): JsonObject {
        if (isJsonObject()) {
            return this as JsonObject
        }
        throw IllegalStateException("Not a JSON Object: $this")
    }

    /**
     * convenience method to get this element as a [JsonArray]. If the element is of some
     * other type, a [IllegalStateException] will result. Hence it is best to use this method
     * after ensuring that this element is of the desired type by calling [.isJsonArray]
     * first.
     *
     * @return get this element as a [JsonArray].
     * @throws IllegalStateException if the element is of another type.
     */
    fun getAsJsonArray(): JsonArray {
        if (isJsonArray()) {
            return this as JsonArray
        }
        throw IllegalStateException("Not a JSON Array: $this")
    }

    /**
     * convenience method to get this element as a [JsonPrimitive]. If the element is of some
     * other type, a [IllegalStateException] will result. Hence it is best to use this method
     * after ensuring that this element is of the desired type by calling [.isJsonPrimitive]
     * first.
     *
     * @return get this element as a [JsonPrimitive].
     * @throws IllegalStateException if the element is of another type.
     */
    fun getAsJsonPrimitive(): JsonPrimitive {
        if (isJsonPrimitive()) {
            return this as JsonPrimitive
        }
        throw IllegalStateException("Not a JSON Primitive: $this")
    }

    /**
     * convenience method to get this element as a [JsonNull]. If the element is of some
     * other type, a [IllegalStateException] will result. Hence it is best to use this method
     * after ensuring that this element is of the desired type by calling [.isJsonNull]
     * first.
     *
     * @return get this element as a [JsonNull].
     * @throws IllegalStateException if the element is of another type.
     * @since 1.2
     */
    fun getAsJsonNull(): JsonNull {
        if (isJsonNull()) {
            return this as JsonNull
        }
        throw IllegalStateException("Not a JSON Null: $this")
    }

    /**
     * convenience method to get this element as a boolean value.
     *
     * @return get this element as a primitive boolean value.
     * @throws ClassCastException if the element is of not a [JsonPrimitive] and is not a valid
     * boolean value.
     * @throws IllegalStateException if the element is of the type [JsonArray] but contains
     * more than a single element.
     */
    open fun getAsBoolean(): Boolean {
        throw UnsupportedOperationException(javaClass.simpleName)
    }

    /**
     * convenience method to get this element as a [Number].
     *
     * @return get this element as a [Number].
     * @throws ClassCastException if the element is of not a [JsonPrimitive] and is not a valid
     * number.
     * @throws IllegalStateException if the element is of the type [JsonArray] but contains
     * more than a single element.
     */
    open fun getAsNumber(): Number {
        throw UnsupportedOperationException(javaClass.simpleName)
    }

    /**
     * convenience method to get this element as a string value.
     *
     * @return get this element as a string value.
     * @throws ClassCastException if the element is of not a [JsonPrimitive] and is not a valid
     * string value.
     * @throws IllegalStateException if the element is of the type [JsonArray] but contains
     * more than a single element.
     */
    open fun getAsString(): String {
        throw UnsupportedOperationException(javaClass.simpleName)
    }

    /**
     * convenience method to get this element as a primitive double value.
     *
     * @return get this element as a primitive double value.
     * @throws ClassCastException if the element is of not a [JsonPrimitive] and is not a valid
     * double value.
     * @throws IllegalStateException if the element is of the type [JsonArray] but contains
     * more than a single element.
     */
    open fun getAsDouble(): Double {
        throw UnsupportedOperationException(javaClass.simpleName)
    }

    /**
     * convenience method to get this element as a primitive float value.
     *
     * @return get this element as a primitive float value.
     * @throws ClassCastException if the element is of not a [JsonPrimitive] and is not a valid
     * float value.
     * @throws IllegalStateException if the element is of the type [JsonArray] but contains
     * more than a single element.
     */
    open fun getAsFloat(): Float {
        throw UnsupportedOperationException(javaClass.simpleName)
    }

    /**
     * convenience method to get this element as a primitive long value.
     *
     * @return get this element as a primitive long value.
     * @throws ClassCastException if the element is of not a [JsonPrimitive] and is not a valid
     * long value.
     * @throws IllegalStateException if the element is of the type [JsonArray] but contains
     * more than a single element.
     */
    open fun getAsLong(): Long {
        throw UnsupportedOperationException(javaClass.simpleName)
    }

    /**
     * convenience method to get this element as a primitive integer value.
     *
     * @return get this element as a primitive integer value.
     * @throws ClassCastException if the element is of not a [JsonPrimitive] and is not a valid
     * integer value.
     * @throws IllegalStateException if the element is of the type [JsonArray] but contains
     * more than a single element.
     */
    open fun getAsInt(): Int {
        throw UnsupportedOperationException(javaClass.simpleName)
    }

    /**
     * convenience method to get this element as a primitive byte value.
     *
     * @return get this element as a primitive byte value.
     * @throws ClassCastException if the element is of not a [JsonPrimitive] and is not a valid
     * byte value.
     * @throws IllegalStateException if the element is of the type [JsonArray] but contains
     * more than a single element.
     * @since 1.3
     */
    open fun getAsByte(): Byte {
        throw UnsupportedOperationException(javaClass.simpleName)
    }

    /**
     * convenience method to get the first character of this element as a string or the first
     * character of this array's first element as a string.
     *
     * @return the first character of the string.
     * @throws ClassCastException if the element is of not a [JsonPrimitive] and is not a valid
     * string value.
     * @throws IllegalStateException if the element is of the type [JsonArray] but contains
     * more than a single element.
     * @since 1.3
     */
    @Deprecated("""This method is misleading, as it does not get this element as a char but rather as
    a string's first character.""")
    open fun getAsCharacter(): Char {
        throw UnsupportedOperationException(javaClass.simpleName)
    }

    /**
     * convenience method to get this element as a [BigDecimal].
     *
     * @return get this element as a [BigDecimal].
     * @throws ClassCastException if the element is of not a [JsonPrimitive].
     * * @throws NumberFormatException if the element is not a valid [BigDecimal].
     * @throws IllegalStateException if the element is of the type [JsonArray] but contains
     * more than a single element.
     * @since 1.2
     */
    open fun getAsBigDecimal(): BigDecimal {
        throw UnsupportedOperationException(javaClass.simpleName)
    }

    /**
     * convenience method to get this element as a [BigInteger].
     *
     * @return get this element as a [BigInteger].
     * @throws ClassCastException if the element is of not a [JsonPrimitive].
     * @throws NumberFormatException if the element is not a valid [BigInteger].
     * @throws IllegalStateException if the element is of the type [JsonArray] but contains
     * more than a single element.
     * @since 1.2
     */
    open fun getAsBigInteger(): BigInteger {
        throw UnsupportedOperationException(javaClass.simpleName)
    }

    /**
     * convenience method to get this element as a primitive short value.
     *
     * @return get this element as a primitive short value.
     * @throws ClassCastException if the element is of not a [JsonPrimitive] and is not a valid
     * short value.
     * @throws IllegalStateException if the element is of the type [JsonArray] but contains
     * more than a single element.
     */
    open fun getAsShort(): Short {
        throw UnsupportedOperationException(javaClass.simpleName)
    }

    override fun toString(): String {
        try {
            val sw = StringWriter()
            val jsonWriter = JsonWriter(sw)
            jsonWriter.isLenient = true
            Streams.write(this, jsonWriter)
            return sw.toString()
        } catch (e: IOException) {
            throw AssertionError(e)
        }
    }
}