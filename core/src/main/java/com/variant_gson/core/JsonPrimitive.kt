package com.variant_gson.core

import com.variant_gson.core.internal.LazilyParsedNumber
import java.math.BigDecimal
import java.math.BigInteger

/**
 * A class representing a Json primitive value. A primitive value
 * is either a String, a Java primitive, or a Java primitive
 * wrapper type.
 */
class JsonPrimitive : JsonElement {
    private val value: Any?

    /**
     * Create a primitive containing a boolean value.
     *
     * @param bool the value to create the primitive with.
     */
    constructor(bool: Boolean) {
        value = bool
    }

    /**
     * Create a primitive containing a {@link Number}.
     *
     * @param number the value to create the primitive with.
     */
    constructor(number: Number) {
        value = number
    }

    /**
     * Create a primitive containing a String value.
     *
     * @param string the value to create the primitive with.
     */
    constructor(string: String) {
        value = string
    }

    /**
     * Create a primitive containing a character. The character is turned into a one character String
     * since Json only supports String.
     *
     * @param c the value to create the primitive with.
     */
    constructor(c: Char) {
        value = c.toString()
    }


    /**
     * Returns the same value as primitives are immutable.
     * @since 2.8.2
     */
    override fun deepCopy(): JsonElement = this

    /**
     * Check whether this primitive contains a boolean value.
     *
     * @return true if this primitive contains a boolean value, false otherwise.
     */
    fun isBoolean() = value is Boolean

    /**
     * convenience method to get this element as a boolean value.
     *
     * @return get this element as a primitive boolean value.
     */
    override fun getAsBoolean(): Boolean {
        return if (isBoolean()) {
            value as Boolean
        } else {
            // Check to see if the value as a String is "true" in any case.
            getAsString().toBoolean()
        }
    }

    /**
     * Check whether this primitive contains a Number.
     *
     * @return true if this primitive contains a Number, false otherwise.
     */
    fun isNumber(): Boolean {
        return value is Number
    }

    /**
     * convenience method to get this element as a Number.
     *
     * @return get this element as a Number.
     * @throws NumberFormatException if the value contained is not a valid Number.
     */
    override fun getAsNumber(): Number {
        return if (value is String) LazilyParsedNumber(value) else value as Number
    }

    /**
     * Check whether this primitive contains a String value.
     *
     * @return true if this primitive contains a String value, false otherwise.
     */
    fun isString(): Boolean {
        return value is String
    }

    /**
     * convenience method to get this element as a String.
     *
     * @return get this element as a String.
     */
    override fun getAsString(): String {
        return when {
            isNumber() -> getAsNumber().toString()
            isBoolean() -> (value as Boolean).toString()
            else -> value.toString()
        }
    }

    /**
     * convenience method to get this element as a primitive double.
     *
     * @return get this element as a primitive double.
     * @throws NumberFormatException if the value contained is not a valid double.
     */
    override fun getAsDouble(): Double {
        return if (isNumber()) getAsNumber().toDouble() else getAsString().toDouble()
    }


    /**
     * convenience method to get this element as a [BigDecimal].
     *
     * @return get this element as a [BigDecimal].
     * @throws NumberFormatException if the value contained is not a valid [BigDecimal].
     */
    override fun getAsBigDecimal(): BigDecimal {
        return if (value is BigDecimal) value else BigDecimal(value.toString())
    }

    /**
     * convenience method to get this element as a [BigInteger].
     *
     * @return get this element as a [BigInteger].
     * @throws NumberFormatException if the value contained is not a valid [BigInteger].
     */
    override fun getAsBigInteger(): BigInteger {
        return if (value is BigInteger) value else BigInteger(value.toString())
    }

    /**
     * convenience method to get this element as a float.
     *
     * @return get this element as a float.
     * @throws NumberFormatException if the value contained is not a valid float.
     */
    override fun getAsFloat(): Float {
        return if (isNumber()) getAsNumber().toFloat() else getAsString().toFloat()
    }

    /**
     * convenience method to get this element as a primitive long.
     *
     * @return get this element as a primitive long.
     * @throws NumberFormatException if the value contained is not a valid long.
     */
    override fun getAsLong(): Long {
        return if (isNumber()) getAsNumber().toLong() else getAsString().toLong()
    }


    /**
     * convenience method to get this element as a primitive short.
     *
     * @return get this element as a primitive short.
     * @throws NumberFormatException if the value contained is not a valid short value.
     */
    override fun getAsShort(): Short {
        return if (isNumber()) getAsNumber().toShort() else getAsString().toShort()
    }

    /**
     * convenience method to get this element as a primitive integer.
     *
     * @return get this element as a primitive integer.
     * @throws NumberFormatException if the value contained is not a valid integer.
     */
    override fun getAsInt(): Int {
        return if (isNumber()) getAsNumber().toInt() else getAsString().toInt()
    }

    override fun getAsByte(): Byte {
        return if (isNumber()) getAsNumber().toByte() else getAsString().toByte()
    }

    override fun getAsCharacter(): Char {
        return getAsString()[0]
    }

    override fun hashCode(): Int {
        if (value == null) {
            return 31
        }
        // Using recommended hashing algorithm from Effective Java for longs and doubles
        if (isIntegral(this)) {
            val value = getAsNumber().toLong()
            return (value xor (value ushr 32)).toInt()
        }
        if (value is Number) {
            val value = java.lang.Double.doubleToLongBits(getAsNumber().toDouble())
            return (value xor (value ushr 32)).toInt()
        }
        return value.hashCode()
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) {
            return true
        }
        if (obj == null || javaClass != obj.javaClass) {
            return false
        }
        val other = obj as JsonPrimitive
        if (value == null) {
            return other.value == null
        }
        if (isIntegral(this) && isIntegral(other)) {
            return getAsNumber().toLong() == other.getAsNumber().toLong()
        }
        if (value is Number && other.value is Number) {
            val a = getAsNumber().toDouble()
            // Java standard types other than double return true for two NaN. So, need
            // special handling for double.
            val b = other.getAsNumber().toDouble()
            return a == b || java.lang.Double.isNaN(a) && java.lang.Double.isNaN(b)
        }
        return value == other.value
    }

    companion object {
        /**
         * Returns true if the specified number is an integral type
         * (Long, Integer, Short, Byte, BigInteger)
         */
        private fun isIntegral(primitive: JsonPrimitive): Boolean {
            if (primitive.value is Number) {
                val number = primitive.value as Number?
                return (number is BigInteger || number is Long || number is Int
                        || number is Short || number is Byte)
            }
            return false
        }
    }
}