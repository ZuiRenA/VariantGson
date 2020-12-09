package com.variant_gson.core.internal.reflect

import com.variant_gson.core.internal.JavaVersion
import java.lang.reflect.AccessibleObject

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


    /**
     * Does the same as @code ao.setAccessible(true), but never throws
     * [java.lang.reflect.InaccessibleObjectException]
     */
    abstract fun makeAccessible(ao: AccessibleObject)


    companion object {
        // the singleton instance, use getInstance() to obtain
        private val INSTANCE: ReflectionAccessor by lazy {
            if (!JavaVersion.isJava9OrLater()) {
                PreJava9ReflectionAccessor()
            } else {
                UnsafeReflectionAccessor()
            }
        }


        /**
         * Obtains a [ReflectionAccessor] instance suitable for the current Java version.
         *
         *
         * You may need one a reflective operation in your code throws [java.lang.reflect.InaccessibleObjectException].
         * In such a case, use [ReflectionAccessor#makeAccessible(AccessibleObject)] on a field, method or constructor
         * (instead of basic [AccessibleObject#setAccessible(boolean)]).
         */
        @JvmStatic
        fun getInstance(): ReflectionAccessor = INSTANCE
    }
}