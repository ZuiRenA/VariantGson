package com.variant_gson.core

/**
 * This exception is raised when Gson attempts to read (or write) a malformed
 * JSON element.
 */
class JsonSyntaxException : JsonParseException {

    constructor(msg: String?) : super(msg)

    constructor(msg: String?, cause: Throwable?) : super(msg, cause)

    /**
     * Creates exception with the specified cause. Consider using
     * [JsonSyntaxException] instead if you can
     * describe what actually happened.
     *
     * @param cause root exception that caused this exception to be thrown.
     */
    constructor(cause: Throwable?) : super(cause)

    companion object {
        private const val serialVersionUID = 1L
    }
}