package com.variant_gson.core

/**
 * A class representing a Json [null] value.
 */
class JsonNull : JsonElement() {

    companion object {

        /**
         * singleton for JsonNull
         */
        @JvmField
        val INSTANCE: JsonNull = JsonNull()
    }

    /**
     * Returns the same instance since it is an immutable value
     * @since 2.8.2
     */
    override fun deepCopy(): JsonElement = INSTANCE

    /**
     * All instances of JsonNull have the same hash code since they are indistinguishable
     */
    override fun hashCode(): Int = JsonNull::class.java.hashCode()


    /**
     * All instances of JsonNull are the same
     */
    override fun equals(other: Any?): Boolean {
        return this === other || other is JsonNull
    }
}