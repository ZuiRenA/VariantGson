package com.variant_gson.core.internal.reflect

/**
 * Come from Gson
 *
 *
 * Provides a replacement for {@link AccessibleObject#setAccessible(boolean)}, which may be used to
 * avoid reflective access issues appeared in Java 9, like {@link java.lang.reflect.InaccessibleObjectException}
 * thrown or warnings like
 * - WARNING: An illegal reflective access operation has occurred
 * - WARNING: Illegal reflective access by ...
 *
 *
 * Works both for Java 9 and earlier Java versions.
 */
abstract class ReflectionAccessor {


    companion object {
        private val instance: ReflectionAccessor = Java
    }
}