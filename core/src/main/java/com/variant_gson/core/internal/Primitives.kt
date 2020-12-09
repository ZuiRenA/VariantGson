package com.variant_gson.core.internal

import java.lang.reflect.Type

/**
 * Contains static utility methods pertaining to primitive types and their
 * corresponding wrapper types.
 */
object Primitives {
    /**
     * Returns true if this type is a primitive.
     */
    @JvmStatic
    fun isPrimitive(type: Type?): Boolean {
        return type is Class<*> && type.isPrimitive
    }

    /**
     * Returns `true` if `type` is one of the nine
     * primitive-wrapper types, such as [Integer].
     *
     * @see Class.isPrimitive
     */
    @JvmStatic
    fun isWrapperType(type: Type): Boolean {
        return type === Int::class.java || type === Float::class.java || type === Byte::class.java || type === Double::class.java || type === Long::class.java || type === Char::class.java || type === Boolean::class.java || type === Short::class.java || type === Void::class.java
    }

    /**
     * Returns the corresponding wrapper type of `type` if it is a primitive
     * type; otherwise returns `type` itself. Idempotent.
     * - wrap(int.class) == Integer.class
     * - wrap(Integer.class) == Integer.class
     * - wrap(String.class) == String.class
     */
    @JvmStatic
    fun <T> wrap(type: Class<T>): Class<T> {
        if (type == Int::class.javaPrimitiveType) return Int::class.java as Class<T>
        if (type == Float::class.javaPrimitiveType) return Float::class.java as Class<T>
        if (type == Byte::class.javaPrimitiveType) return Byte::class.java as Class<T>
        if (type == Double::class.javaPrimitiveType) return Double::class.java as Class<T>
        if (type == Long::class.javaPrimitiveType) return Long::class.java as Class<T>
        if (type == Char::class.javaPrimitiveType) return Char::class.java as Class<T>
        if (type == Boolean::class.javaPrimitiveType) return Boolean::class.java as Class<T>
        if (type == Short::class.javaPrimitiveType) return Short::class.java as Class<T>
        return if (type == Void.TYPE) Void::class.java as Class<T> else type
    }

    /**
     * Returns the corresponding primitive type of `type` if it is a
     * wrapper type; otherwise returns `type` itself. Idempotent.
     * - unwrap(Integer.class) == int.class
     * - unwrap(int.class) == int.class
     * - unwrap(String.class) == String.class
     */
    @JvmStatic
    fun <T> unwrap(type: Class<T>): Class<T>? {
        if (type == Int::class.java) return Int::class.javaPrimitiveType as Class<T>?
        if (type == Float::class.java) return Float::class.javaPrimitiveType as Class<T>?
        if (type == Byte::class.java) return Byte::class.javaPrimitiveType as Class<T>?
        if (type == Double::class.java) return Double::class.javaPrimitiveType as Class<T>?
        if (type == Long::class.java) return Long::class.javaPrimitiveType as Class<T>?
        if (type == Char::class.java) return Char::class.javaPrimitiveType as Class<T>?
        if (type == Boolean::class.java) return Boolean::class.javaPrimitiveType as Class<T>?
        if (type == Short::class.java) return Short::class.javaPrimitiveType as Class<T>?
        return if (type == Void::class.java) Void.TYPE as Class<T> else type
    }
}