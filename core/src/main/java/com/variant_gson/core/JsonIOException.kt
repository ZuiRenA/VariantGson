package com.variant_gson.core

/**
 * This exception is raised when Gson was unable to read an input stream
 * or write to one.
 */
class JsonIOException : JsonParseException {

    constructor(msg: String?) : super(msg)

    constructor(msg: String?, cause: Throwable?) : super(msg, cause)

    /**
     * Creates exception with the specified cause. Consider using
     * [JsonIOException(String, Throwable)] instead if you can describe what happened.
     *
     * @param cause root exception that caused this exception to be thrown.
     */
    constructor(cause: Throwable?) : super(cause)


    companion object {
        private const val serialVersionUID = 1L
    }
}