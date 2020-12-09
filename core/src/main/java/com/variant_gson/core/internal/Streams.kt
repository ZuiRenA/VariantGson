package com.variant_gson.core.internal

import com.variant_gson.core.*
import com.variant_gson.core.internal.bind.TypeAdapters
import com.variant_gson.core.stream.JsonReader
import com.variant_gson.core.stream.JsonWriter
import com.variant_gson.core.stream.MalformedJsonException
import java.io.EOFException
import java.io.IOException
import java.io.Writer

/**
 * Reads and writes GSON parse trees over streams.
 */
object Streams {

    /**
     * Takes a reader in any state and returns the next value as a JsonElement.
     */
    @JvmStatic
    @Throws(JsonParseException::class)
    fun parse(reader: JsonReader): JsonElement? {
        var isEmpty = true
        return try {
            reader.peek()
            isEmpty = false
            TypeAdapters.JSON_ELEMENT.read(reader)
        } catch (e: EOFException) {
            /*
            * For compatibility with JSON 1.5 and earlier, we return a JsonNull for
            * empty documents instead of throwing.
            */
            if (isEmpty) {
                return JsonNull.INSTANCE
            }
            throw JsonSyntaxException(e)
        } catch (e: MalformedJsonException) {
            throw JsonSyntaxException(e)
        } catch (e: IOException) {
            throw JsonIOException(e)
        } catch (e: NumberFormatException) {
            throw JsonSyntaxException(e)
        }
    }

    /**
     * Writes the JSON element to the writer, recursively.
     */
    @JvmStatic
    @Throws(IOException::class)
    fun write(element: JsonElement?, writer: JsonWriter) {
        TypeAdapters.JSON_ELEMENT.write(writer, element)
    }

    @JvmStatic
    fun writerForAppendable(appendable: java.lang.Appendable?): Writer {
        return if (appendable is Writer) appendable else AppendableWriter(appendable!!)
    }
}

/**
 * Adapts an [Appendable] so it can be passed anywhere a [Writer]
 * is used.
 */
private class AppendableWriter(private val appendable: Appendable) : Writer() {
    private val currentWrite = CurrentWrite()

    @Throws(IOException::class)
    override fun write(chars: CharArray, offset: Int, length: Int) {
        currentWrite.chars = chars
        appendable.append(currentWrite, offset, offset + length)
    }

    @Throws(IOException::class)
    override fun write(i: Int) {
        appendable.append(i.toChar())
    }

    override fun flush() {}
    override fun close() {}


    companion object {
        /**
         * A mutable char sequence pointing at a single char[].
         */
        internal class CurrentWrite : CharSequence {

            var chars: CharArray? = null

            override val length: Int
                get() = chars?.size ?: 0

            override fun get(index: Int): Char = chars?.get(index) ?: ' '

            override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
                return String(chars ?: charArrayOf(), startIndex, endIndex - startIndex)
            }
        }
    }
}

