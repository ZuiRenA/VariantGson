package com.variant_gson.core

import java.math.BigDecimal
import java.math.BigInteger

/**
 * A class representing an array type in Json. An array is a list of [JsonElement]s each of
 * which can be of a different type. This is an ordered list, meaning that the order in which
 * elements are added is preserved.
 */
class JsonArray : JsonElement, Iterable<JsonElement> {

    private val elements: MutableList<JsonElement>

    /**
     * Creates an empty JsonArray.
     */
    constructor() {
        elements = arrayListOf()
    }

    constructor(capacity: Int) {
        elements = ArrayList(capacity)
    }

    /**
     * Creates a deep copy of this element and all its children
     * @since 2.8.2
     */
    override fun deepCopy(): JsonElement {
        if (elements.isNotEmpty()) {
            val result = JsonArray(elements.size)
            for (element in elements) {
                result.add(element.deepCopy())
            }
            return result
        }

        return JsonArray()
    }

    override fun iterator(): Iterator<JsonElement> = elements.iterator()

    /**
     * Adds the specified boolean to self.
     *
     * @param bool the boolean that needs to be added to the array.
     */
    fun add(bool: Boolean?) {
        elements.add(bool?.let { JsonPrimitive(it) } ?: JsonNull.INSTANCE)
    }

    /**
     * Adds the specified character to self.
     *
     * @param character the character that needs to be added to the array.
     */
    fun add(character: Char?) {
        elements.add(character?.let { JsonPrimitive(it) } ?: JsonNull.INSTANCE)
    }

    /**
     * Adds the specified number to self.
     *
     * @param number the number that needs to be added to the array.
     */
    fun add(number: Number?) {
        elements.add(number?.let { JsonPrimitive(it) } ?: JsonNull.INSTANCE)
    }

    /**
     * Adds the specified string to self.
     *
     * @param string the string that needs to be added to the array.
     */
    fun add(string: String?) {
        elements.add(string?.let { JsonPrimitive(it) } ?: JsonNull.INSTANCE)
    }

    /**
     * Adds the specified element to self.
     *
     * @param element the element that needs to be added to the array.
     */
    fun add(element: JsonElement?) {
        elements.add(element ?: JsonNull.INSTANCE)
    }

    /**
     * Adds all the elements of the specified array to self.
     *
     * @param array the array whose elements need to be added to the array.
     */
    fun addAll(array: JsonArray) {
        elements.addAll(array.elements)
    }

    /**
     * Returns the ith element of the array.
     *
     * @param i the index of the element that is being sought.
     * @return the element present at the ith index.
     * @throws IndexOutOfBoundsException if i is negative or greater than or equal to the
     * [.size] of the array.
     */
    operator fun get(i: Int): JsonElement {
        return elements[i]
    }

    /**
     * Replaces the element at the specified position in this array with the specified element.
     * Element can be null.
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the specified index is outside the array bounds
     */
    operator fun set(index: Int, element: JsonElement): JsonElement {
        return elements.set(index, element)
    }

    /**
     * Removes the first occurrence of the specified element from this array, if it is present.
     * If the array does not contain the element, it is unchanged.
     * @param element element to be removed from this array, if present
     * @return true if this array contained the specified element, false otherwise
     * @since 2.3
     */
    fun remove(element: JsonElement): Boolean {
        return elements.remove(element)
    }

    /**
     * Removes the element at the specified position in this array. Shifts any subsequent elements
     * to the left (subtracts one from their indices). Returns the element that was removed from
     * the array.
     * @param index index the index of the element to be removed
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the specified index is outside the array bounds
     * @since 2.3
     */
    fun remove(index: Int): JsonElement {
        return elements.removeAt(index)
    }

    /**
     * Returns true if this array contains the specified element.
     * @return true if this array contains the specified element.
     * @param element whose presence in this array is to be tested
     * @since 2.3
     */
    operator fun contains(element: JsonElement?): Boolean {
        return elements.contains(element)
    }

    /**
     * Returns the number of elements in the array.
     *
     * @return the number of elements in the array.
     */
    fun size(): Int {
        return elements.size
    }

    /**
     * convenience method to get this array as a [Number] if it contains a single element.
     *
     * @return get this element as a number if it is single element array.
     * @throws ClassCastException if the element in the array is of not a [JsonPrimitive] and
     * is not a valid Number.
     * @throws IllegalStateException if the array has more than one element.
     */
    override fun getAsNumber(): Number {
        if (elements.size == 1) {
            return elements[0].getAsNumber()
        }
        throw IllegalStateException()
    }

    /**
     * convenience method to get this array as a [String] if it contains a single element.
     *
     * @return get this element as a String if it is single element array.
     * @throws ClassCastException if the element in the array is of not a [JsonPrimitive] and
     * is not a valid String.
     * @throws IllegalStateException if the array has more than one element.
     */
    override fun getAsString(): String {
        if (elements.size == 1) {
            return elements[0].getAsString()
        }
        throw java.lang.IllegalStateException()
    }

    /**
     * convenience method to get this array as a double if it contains a single element.
     *
     * @return get this element as a double if it is single element array.
     * @throws ClassCastException if the element in the array is of not a [JsonPrimitive] and
     * is not a valid double.
     * @throws IllegalStateException if the array has more than one element.
     */
    override fun getAsDouble(): Double {
        if (elements.size == 1) {
            return elements[0].getAsDouble()
        }
        throw java.lang.IllegalStateException()
    }

    /**
     * convenience method to get this array as a [BigDecimal] if it contains a single element.
     *
     * @return get this element as a [BigDecimal] if it is single element array.
     * @throws ClassCastException if the element in the array is of not a [JsonPrimitive].
     * @throws NumberFormatException if the element at index 0 is not a valid [BigDecimal].
     * @throws IllegalStateException if the array has more than one element.
     * @since 1.2
     */
    override fun getAsBigDecimal(): BigDecimal {
        if (elements.size == 1) {
            return elements[0].getAsBigDecimal()
        }
        throw java.lang.IllegalStateException()
    }

    /**
     * convenience method to get this array as a [BigInteger] if it contains a single element.
     *
     * @return get this element as a [BigInteger] if it is single element array.
     * @throws ClassCastException if the element in the array is of not a [JsonPrimitive].
     * @throws NumberFormatException if the element at index 0 is not a valid [BigInteger].
     * @throws IllegalStateException if the array has more than one element.
     * @since 1.2
     */
    override fun getAsBigInteger(): BigInteger {
        if (elements.size == 1) {
            return elements[0].getAsBigInteger()
        }
        throw java.lang.IllegalStateException()
    }

    /**
     * convenience method to get this array as a float if it contains a single element.
     *
     * @return get this element as a float if it is single element array.
     * @throws ClassCastException if the element in the array is of not a [JsonPrimitive] and
     * is not a valid float.
     * @throws IllegalStateException if the array has more than one element.
     */
    override fun getAsFloat(): Float {
        if (elements.size == 1) {
            return elements[0].getAsFloat()
        }
        throw java.lang.IllegalStateException()
    }

    /**
     * convenience method to get this array as a long if it contains a single element.
     *
     * @return get this element as a long if it is single element array.
     * @throws ClassCastException if the element in the array is of not a [JsonPrimitive] and
     * is not a valid long.
     * @throws IllegalStateException if the array has more than one element.
     */
    override fun getAsLong(): Long {
        if (elements.size == 1) {
            return elements[0].getAsLong()
        }
        throw java.lang.IllegalStateException()
    }

    /**
     * convenience method to get this array as an integer if it contains a single element.
     *
     * @return get this element as an integer if it is single element array.
     * @throws ClassCastException if the element in the array is of not a [JsonPrimitive] and
     * is not a valid integer.
     * @throws IllegalStateException if the array has more than one element.
     */
    override fun getAsInt(): Int {
        if (elements.size == 1) {
            return elements[0].getAsInt()
        }
        throw java.lang.IllegalStateException()
    }

    override fun getAsByte(): Byte {
        if (elements.size == 1) {
            return elements[0].getAsByte()
        }
        throw java.lang.IllegalStateException()
    }

    override fun getAsCharacter(): Char {
        if (elements.size == 1) {
            return elements[0].getAsCharacter()
        }
        throw java.lang.IllegalStateException()
    }

    /**
     * convenience method to get this array as a primitive short if it contains a single element.
     *
     * @return get this element as a primitive short if it is single element array.
     * @throws ClassCastException if the element in the array is of not a [JsonPrimitive] and
     * is not a valid short.
     * @throws IllegalStateException if the array has more than one element.
     */
    override fun getAsShort(): Short {
        if (elements.size == 1) {
            return elements[0].getAsShort()
        }
        throw java.lang.IllegalStateException()
    }

    /**
     * convenience method to get this array as a boolean if it contains a single element.
     *
     * @return get this element as a boolean if it is single element array.
     * @throws ClassCastException if the element in the array is of not a [JsonPrimitive] and
     * is not a valid boolean.
     * @throws IllegalStateException if the array has more than one element.
     */
    override fun getAsBoolean(): Boolean {
        if (elements.size == 1) {
            return elements[0].getAsBoolean()
        }
        throw java.lang.IllegalStateException()
    }

    override fun equals(other: Any?): Boolean {
        return other === this || other is JsonArray && other.elements == elements
    }

    override fun hashCode(): Int {
        return elements.hashCode()
    }
}