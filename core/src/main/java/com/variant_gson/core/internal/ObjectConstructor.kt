package com.variant_gson.core.internal

/**
 * Defines a generic object construction factory.  The purpose of this class
 * is to construct a default instance of a class that can be used for object
 * navigation while deserialization from its JSON representation.
 */
interface ObjectConstructor<T> {
    /**
     * Returns a new instance.
     */
    fun construct(): T
}