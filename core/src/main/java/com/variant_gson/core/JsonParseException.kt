package com.variant_gson.core

import java.lang.RuntimeException

/**
 * This exception is raised if there is a serious issue that occurs during parsing of a Json
 * string. One of the main usages for this class is for the Gson infrastructure. If the incoming
 * Json is bad/malicious, an instance of this exception is raised.
 *
 *
 * This exception is a [RuntimeException] because it is exposed to the client. Using a
 * [RuntimeException] avoids bad coding practices on the client side where they catch the
 * exception and do nothing. It is often the case that you want to blow up if there is a parsing
 * error (i.e. often clients do not know how to recover from a [JsonParseException].
 *
 */
open class JsonParseException : RuntimeException {

    /**
     * Creates exception with the specified message. If you are wrapping another exception, consider
     * using [JsonParseException(String, Throwable)] instead.
     *
     * @param msg error message describing a possible cause of this exception.
     */
    constructor(msg: String?) : super(msg)

    /**
     * Creates exception with the specified message and cause.
     *
     * @param msg error message describing what happened.
     * @param cause root exception that caused this exception to be thrown.
     */
    constructor(msg: String?, cause: Throwable?) : super(msg, cause)

    /**
     * Creates exception with the specified cause. Consider using
     * [JsonParseException(String, Throwable)] instead if you can describe what happened.
     *
     * @param cause root exception that caused this exception to be thrown.
     */
    constructor(cause: Throwable?) : super(cause)

    companion object {
        internal const val serialVersionUID = -4086729973971783390L
    }
}