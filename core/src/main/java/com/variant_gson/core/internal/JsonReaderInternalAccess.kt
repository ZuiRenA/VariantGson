package com.variant_gson.core.internal

import com.variant_gson.core.stream.JsonReader
import java.io.IOException
import kotlin.jvm.Throws

/**
 * Internal-only APIs of JsonReader available only to other classes in Gson.
 */
abstract class JsonReaderInternalAccess {

    /**
     * Changes the type of the current property name token to a string value.
     */
    @Throws(IOException::class)
    abstract fun promoteNameToValue(reader: JsonReader)

    companion object {

        @JvmField
        var INSTANCE: JsonReaderInternalAccess? = null
    }
}