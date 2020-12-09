package com.variant_gson.core.internal.reflect

import java.lang.reflect.AccessibleObject

/**
 * A basic implementation of {@link ReflectionAccessor} which is suitable for Java 8 and below.
 *
 *
 * This implementation just calls {@link AccessibleObject#setAccessible(boolean) setAccessible(true)}, which worked
 * fine before Java 9.
 */
class PreJava9ReflectionAccessor : ReflectionAccessor() {

    /** {@inheritDoc} */
    override fun makeAccessible(ao: AccessibleObject) {
        ao.isAccessible = true
    }
}