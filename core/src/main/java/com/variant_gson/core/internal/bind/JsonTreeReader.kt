package com.variant_gson.core.internal.bind

import com.variant_gson.core.*
import com.variant_gson.core.stream.JsonReader
import com.variant_gson.core.stream.JsonToken
import java.io.IOException
import java.io.Reader

/**
 * This reader walks the elements of a JsonElement as if it was coming from a
 * character stream.
 *
 */
class JsonTreeReader(element: JsonElement) : JsonReader(UNREADABLE_READER) {

    /**
    * The nesting stack. Using a manual array rather than an ArrayList saves 20%.
    */
    private var stack = arrayOfNulls<Any?>(32)
    private var stackSize = 0

    /**
    * The path members. It corresponds directly to stack: At indices where the
    * stack contains an object (EMPTY_OBJECT, DANGLING_NAME or NONEMPTY_OBJECT),
    * pathNames contains the name at this scope. Where it contains an array
    * (EMPTY_ARRAY, NONEMPTY_ARRAY) pathIndices contains the current index in
    * that array. Otherwise the value is undefined, and we take advantage of that
    * by incrementing pathIndices when doing so isn't useful.
    */
    private var pathNames = arrayOfNulls<String>(32)
    private var pathIndices = IntArray(32)


    init {
        push(element)
    }

    @Throws(IOException::class)
    override fun beginArray() {
        expect(JsonToken.BEGIN_ARRAY)
        val array = peekStack() as JsonArray?
        push(array!!.iterator())
        pathIndices[stackSize - 1] = 0
    }

    @Throws(IOException::class)
    override fun endArray() {
        expect(JsonToken.END_ARRAY)
        popStack() // empty iterator
        popStack() // array
        if (stackSize > 0) {
            pathIndices[stackSize - 1]++
        }
    }

    @Throws(IOException::class)
    override fun beginObject() {
        expect(JsonToken.BEGIN_OBJECT)
        val `object`: JsonObject? = peekStack() as? JsonObject
        push(`object`?.entrySet()?.iterator())
    }


    @Throws(IOException::class)
    override fun endObject() {
        expect(JsonToken.END_OBJECT)
        popStack() // empty iterator
        popStack() // object
        if (stackSize > 0) {
            pathIndices[stackSize - 1]++
        }
    }

    override fun peek(): JsonToken {
        if (stackSize == 0) return JsonToken.END_DOCUMENT

        val any = peekStack()
        when (any) {
            is Iterator<*> -> {
                val isAny = stack[stackSize - 2] is JsonObject
                return if (any.hasNext()) {
                    if (isAny) {
                        JsonToken.NAME
                    } else {
                        push(any.next())
                        peek()
                    }
                } else {
                    if (isAny) JsonToken.END_OBJECT else JsonToken.END_ARRAY
                }
            }
            is JsonObject -> return JsonToken.BEGIN_OBJECT
            is JsonArray -> return JsonToken.BEGIN_ARRAY
            is JsonPrimitive -> {
                return when {
                    any.isString() -> JsonToken.STRING
                    any.isBoolean() -> JsonToken.BOOLEAN
                    any.isNumber() -> JsonToken.NUMBER
                    else -> throw AssertionError()
                }
            }
            is JsonNull -> return JsonToken.NULL
            SENTINEL_CLOSED -> throw IllegalStateException("JsonReader is closed")
            else -> throw AssertionError()
        }
    }

    @Throws(IOException::class)
    override fun nextName(): String {
        expect(JsonToken.NAME)
        val i = peekStack() as Iterator<*>?
        val entry = i!!.next() as Map.Entry<*, *>
        val result = entry.key as String
        pathNames[stackSize - 1] = result
        push(entry.value)
        return result
    }

    @Throws(IOException::class)
    override fun nextString(): String {
        val token = peek()
        check(!(token !== JsonToken.STRING && token !== JsonToken.NUMBER)) { "Expected " + JsonToken.STRING.toString() + " but was " + token.toString() + locationString() }
        val result = (popStack() as JsonPrimitive).getAsString()
        if (stackSize > 0) {
            pathIndices[stackSize - 1]++
        }
        return result
    }

    @Throws(IOException::class)
    override fun nextBoolean(): Boolean {
        expect(JsonToken.BOOLEAN)
        val result = (popStack() as JsonPrimitive).getAsBoolean()
        if (stackSize > 0) {
            pathIndices[stackSize - 1]++
        }
        return result
    }

    @Throws(IOException::class)
    override fun nextNull() {
        expect(JsonToken.NULL)
        popStack()
        if (stackSize > 0) {
            pathIndices[stackSize - 1]++
        }
    }

    @Throws(IOException::class)
    override fun nextDouble(): Double {
        val token = peek()
        check(!(token !== JsonToken.NUMBER && token !== JsonToken.STRING)) { "Expected " + JsonToken.NUMBER.toString() + " but was " + token.toString() + locationString() }
        val result = (peekStack() as JsonPrimitive).getAsDouble()
        if (!isLenient && (java.lang.Double.isNaN(result) || java.lang.Double.isInfinite(result))) {
            throw NumberFormatException("JSON forbids NaN and infinities: $result")
        }
        popStack()
        if (stackSize > 0) {
            pathIndices[stackSize - 1]++
        }
        return result
    }

    @Throws(IOException::class)
    override fun nextLong(): Long {
        val token = peek()
        check(!(token !== JsonToken.NUMBER && token !== JsonToken.STRING)) { "Expected " + JsonToken.NUMBER.toString() + " but was " + token.toString() + locationString() }
        val result = (peekStack() as JsonPrimitive).getAsLong()
        popStack()
        if (stackSize > 0) {
            pathIndices[stackSize - 1]++
        }
        return result
    }

    @Throws(IOException::class)
    override fun nextInt(): Int {
        val token = peek()
        check(!(token !== JsonToken.NUMBER && token !== JsonToken.STRING)) { "Expected " + JsonToken.NUMBER.toString() + " but was " + token.toString() + locationString() }
        val result = (peekStack() as JsonPrimitive).getAsInt()
        popStack()
        if (stackSize > 0) {
            pathIndices[stackSize - 1]++
        }
        return result
    }

    @Throws(IOException::class)
    override fun close() {
        stack = arrayOf(SENTINEL_CLOSED)
        stackSize = 1
    }

    @Throws(IOException::class)
    override fun skipValue() {
        if (peek() === JsonToken.NAME) {
            nextName()
            pathNames[stackSize - 2] = "null"
        } else {
            popStack()
            if (stackSize > 0) {
                pathNames[stackSize - 1] = "null"
            }
        }
        if (stackSize > 0) {
            pathIndices[stackSize - 1]++
        }
    }

    override fun getPath(): String {
        val result = StringBuilder().append('$')
        var i = 0
        while (i < stackSize) {
            if (stack[i] is JsonArray) {
                if (stack[++i] is Iterator<*>) {
                    result.append('[').append(pathIndices[i]).append(']')
                }
            } else if (stack[i] is JsonObject) {
                if (stack[++i] is Iterator<*>) {
                    result.append('.')
                    if (pathNames[i] != null) {
                        result.append(pathNames[i])
                    }
                }
            }
            i++
        }
        return result.toString()
    }

    override fun toString(): String {
        return javaClass.simpleName
    }

    @Throws(IOException::class)
    fun promoteNameToValue() {
        expect(JsonToken.NAME)
        val i = peekStack() as Iterator<*>?
        val entry = i!!.next() as Map.Entry<*, *>
        push(entry.value)
        push(JsonPrimitive((entry.key as String?)!!))
    }

    private fun peekStack(): Any? {
        return stack[stackSize - 1]
    }

    private fun popStack(): Any? {
        val result = stack[--stackSize]
        stack[stackSize] = null
        return result
    }

    @Throws(IOException::class)
    private fun expect(expected: JsonToken) {
        check(!(peek() !== expected)) { "Expected " + expected + " but was " + peek() + locationString() }
    }

    private fun push(newTop: Any?) {
        if (stackSize == stack.size) {
            val newLength = stackSize * 2
            stack = stack.copyOf(newLength)
            pathIndices = pathIndices.copyOf(newLength)
            pathNames = pathNames.copyOf(newLength)
        }
        stack[stackSize++] = newTop
    }

    private fun locationString(): String {
        return " at path $path"
    }

    companion object {
        private val SENTINEL_CLOSED = Any()

        private val UNREADABLE_READER: Reader = object : Reader() {
            override fun read(buffer: CharArray, offset: Int, count: Int): Int {
                throw AssertionError()
            }
            override fun close() {
                throw AssertionError()
            }
        }
    }
}