package com.variant_gson.core.internal

import java.io.ObjectInputStream
import java.io.ObjectStreamClass
import java.lang.reflect.Modifier

/**
 * Do sneaky things to allocate objects without invoking their constructors.
 */
abstract class UnsafeAllocator {
    @Throws(Exception::class)
    abstract fun <T> newInstance(c: Class<T>): T

    companion object {
        @JvmStatic
        fun create(): UnsafeAllocator {
            // try JVM
            // public class Unsafe {
            //   public Object allocateInstance(Class<?> type);
            // }
            try {
                val unsafeClass = Class.forName("sun.misc.Unsafe")
                val f = unsafeClass.getDeclaredField("theUnsafe")
                f.isAccessible = true
                val unsafe = f[null]
                val allocateInstance = unsafeClass.getMethod("allocateInstance", Class::class.java)
                return object : UnsafeAllocator() {
                    @Throws(Exception::class)
                    override fun <T> newInstance(c: Class<T>): T {
                        assertInstantiable(c)
                        return allocateInstance.invoke(unsafe, c) as T
                    }
                }
            } catch (ignored: Exception) {
            }

            // try dalvikvm, post-gingerbread
            // public class ObjectStreamClass {
            //   private static native int getConstructorId(Class<?> c);
            //   private static native Object newInstance(Class<?> instantiationClass, int methodId);
            // }
            try {
                val getConstructorId = ObjectStreamClass::class.java
                        .getDeclaredMethod("getConstructorId", Class::class.java)
                getConstructorId.isAccessible = true
                val constructorId = getConstructorId.invoke(null, Any::class.java) as Int
                val newInstance = ObjectStreamClass::class.java
                        .getDeclaredMethod("newInstance", Class::class.java, Int::class.javaPrimitiveType)
                newInstance.isAccessible = true
                return object : UnsafeAllocator() {
                    @Throws(Exception::class)
                    override fun <T> newInstance(c: Class<T>): T {
                        assertInstantiable(c)
                        return newInstance.invoke(null, c, constructorId) as T
                    }
                }
            } catch (ignored: Exception) {
            }

            // try dalvikvm, pre-gingerbread
            // public class ObjectInputStream {
            //   private static native Object newInstance(
            //     Class<?> instantiationClass, Class<?> constructorClass);
            // }
            try {
                val newInstance = ObjectInputStream::class.java
                        .getDeclaredMethod("newInstance", Class::class.java, Class::class.java)
                newInstance.isAccessible = true
                return object : UnsafeAllocator() {
                    @Throws(Exception::class)
                    override fun <T> newInstance(c: Class<T>): T {
                        assertInstantiable(c)
                        return newInstance.invoke(null, c, Any::class.java) as T
                    }
                }
            } catch (ignored: Exception) {
            }

            // give up
            return object : UnsafeAllocator() {
                override fun <T> newInstance(c: Class<T>): T {
                    throw UnsupportedOperationException("Cannot allocate $c")
                }
            }
        }

        /**
         * Check if the class can be instantiated by unsafe allocator. If the instance has interface or abstract modifiers
         * throw an [java.lang.UnsupportedOperationException]
         * @param c instance of the class to be checked
         */
        fun assertInstantiable(c: Class<*>) {
            val modifiers = c.modifiers
            if (Modifier.isInterface(modifiers)) {
                throw UnsupportedOperationException("Interface can't be instantiated! Interface name: " + c.name)
            }
            if (Modifier.isAbstract(modifiers)) {
                throw UnsupportedOperationException("Abstract class can't be instantiated! Class name: " + c.name)
            }
        }
    }
}