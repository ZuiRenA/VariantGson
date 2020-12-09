package com.variant_gson.core.internal.reflect

import com.variant_gson.core.JsonIOException
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * An implementation of {@link ReflectionAccessor} based on {@link Unsafe}.
 *
 *
 * NOTE: This implementation is designed for Java 9. Although it should work with earlier Java releases, it is better to
 * use [PreJava9ReflectionAccessor] for them.
 */
class UnsafeReflectionAccessor : ReflectionAccessor() {

    private val theUnsafe: Any? = getUnsafeInstance()
    private val overrideField: Field? = getOverrideField();

    /** {@inheritDoc} */
    override fun makeAccessible(ao: AccessibleObject) {
        val success = makeAccessibleWithUnsafe(ao)
        if (!success) {
            try {
                // unsafe couldn't be found, so try using accessible anyway
                ao.isAccessible = true
            } catch (e: SecurityException) {
                throw JsonIOException("Gson couldn't modify fields for $ao\nand sun.misc.Unsafe not found."
                        + "\nEither write a custom type adapter, or make fields accessible, or include sun.misc.Unsafe.$e")
            }
        }
    }

    // Visible for testing only
    internal fun makeAccessibleWithUnsafe(ao: AccessibleObject): Boolean {
        if (theUnsafe != null && overrideField != null) {
            try {
                val method: Method? = unsafeClass?.getMethod("objectFieldOffset", Field::class.java)
                val overrideOffset: Long? = method?.invoke(theUnsafe, overrideField) as? Long
                val putBooleanMethod: Method? = unsafeClass?.getMethod("putBoolean", Any::class.java, Long::class.java, Boolean::class.java)
                putBooleanMethod?.invoke(theUnsafe, ao, overrideOffset, true)
                return true
            } catch (ignored: Exception) {
                // ignore
            }
        }

        return false
    }

    companion object {
        private var unsafeClass: Class<*>? = null

        private fun getUnsafeInstance(): Any? = try {
            unsafeClass = Class.forName("sun.misc.Unsafe")
            val unsafeField = unsafeClass?.getDeclaredField("theUnsafe")
            unsafeField?.isAccessible = true
            unsafeField?.get(null)
        } catch (e: Exception) {
            null
        }

        private fun getOverrideField(): Field? = try {
            AccessibleObject::class.java.getDeclaredField("override")
        } catch (e: NoSuchFieldException) {
            null
        }
    }
}