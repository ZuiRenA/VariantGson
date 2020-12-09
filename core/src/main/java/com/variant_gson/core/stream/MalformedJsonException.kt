package com.variant_gson.core.stream

import java.io.IOException

/**
 * Thrown when a reader encounters malformed JSON. Some syntax errors can be
 * ignored by calling [JsonReader.setLenient].
 */
class MalformedJsonException : IOException {
    constructor(msg: String?) : super(msg)
    constructor(msg: String?, throwable: Throwable?) : super(msg) {
        // Using initCause() instead of calling super() because Java 1.5 didn't retrofit IOException
        // with a constructor with Throwable. This was done in Java 1.6
        initCause(throwable)
    }

    constructor(throwable: Throwable?) {
        // Using initCause() instead of calling super() because Java 1.5 didn't retrofit IOException
        // with a constructor with Throwable. This was done in Java 1.6
        initCause(throwable)
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}