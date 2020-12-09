package com.variant_gson.core.reflect

import com.variant_gson.core.internal.`$Gson$Preconditions`
import com.variant_gson.core.internal.`$Gson$Types`
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.util.*

/**
 * Represents a generic type [T]. Java doesn't yet provide a way to
 * represent generic types, so this class does. Forces clients to create a
 * subclass of this class which enables retrieval the type information even at
 * runtime.
 *
 *
 * For example, to create a type literal for [List<String>], you can
 * create an empty anonymous inner class:
 * ``` Java
 * TypeToken<List<String>> list = new TypeToken<List<String>>() {};
 * ```
 * This syntax cannot be used to create type literals that have wildcard
 * parameters, such as [Class<*>] or [List<in CharSequence>].
 */
open class TypeToken<T> {
    private val type: Type
    private val rawType: Class<in T>
    private val hashCode: Int

    /**
     * Constructs a new type literal. Derives represented class from type
     * parameter.
     *
     *
     * Clients create an empty anonymous subclass. Doing so embeds the type
     * parameter in the anonymous class's type hierarchy so we can reconstitute it
     * at runtime despite erasure.
     */
    protected constructor() {
        type = getSuperclassTypeParameter(javaClass)
        rawType = `$Gson$Types`.getRawType(type) as Class<in T>
        hashCode = type.hashCode()
    }

    /**
     * Unsafe. Constructs a type literal manually.
     */
    constructor(type: Type) {
        this.type = `$Gson$Types`.canonicalize(`$Gson$Preconditions`.checkNotNull(type))
        rawType = `$Gson$Types`.getRawType(type) as Class<in T>
        hashCode = type.hashCode()
    }

    /**
     * Returns the raw (non-generic) type for this type.
     */
    fun getRawType(): Class<in T> = rawType

    /**
     * Gets underlying [Type] instance.
     */
    fun getType(): Type = type

    /**
     * Check if this type is assignable from the given class object.
     */
    @Deprecated("this implementation may be inconsistent with javac for types with wildcards.",
            ReplaceWith("isAssignableFrom(cls as Type)", "java.lang.reflect.Type"))
    fun isAssignableFrom(cls: Class<*>): Boolean = isAssignableFrom(cls as Type)

    /**
     * Check if this type is assignable from the given Type.
     */
    @Deprecated("this implementation may be inconsistent with javac for types with wildcards.")
    fun isAssignableFrom(from: Type?): Boolean {
        if (from == null) return false
        if (type == from) return true

        return when (type) {
            is Class<*> -> rawType.isAssignableFrom(`$Gson$Types`.getRawType(from))
            is ParameterizedType -> isAssignableFrom(from, type, hashMapOf())
            is GenericArrayType -> rawType.isAssignableFrom(`$Gson$Types`.getRawType(from))
                    && isAssignableFrom(from, type)
            else -> throw buildUnexpectedTypeError(type, Class::class.java,
                    ParameterizedType::class.java, GenericArrayType::class.java)
        }
    }

    override fun hashCode(): Int = hashCode

    override fun equals(other: Any?): Boolean {
        return other is TypeToken<*> && `$Gson$Types`.equals(type, other.type)
    }

    override fun toString(): String = `$Gson$Types`.typeToString(type)

    companion object {

        /**
         * Gets type literal for the given [Type] instance.
         */
        @JvmStatic
        fun get(type: Type): TypeToken<*> = TypeToken<Any?>(type)

        /**
         * Gets type literal for the given [Class] instance.
         */
        @JvmStatic
        fun <T> get(type: Class<T>): TypeToken<T> = TypeToken(type)

        /**
         * Gets type literal for the parameterized type represented by applying [typeArguments] to
         * [rawType].
         */
        @JvmStatic
        fun getParameterized(rawType: Type, vararg typeArguments: Type): TypeToken<*> {
            return TypeToken<Any?>(`$Gson$Types`.newParameterizedTypeWithOwner(null, rawType, *typeArguments))
        }

        /**
         * Gets type literal for the array type whose elements are all instances of [componentType].
         */
        @JvmStatic
        fun getArray(componentType: Type): TypeToken<*> = TypeToken<Any?>(`$Gson$Types`.arrayOf(componentType))

        internal fun getSuperclassTypeParameter(subclass: Class<*>): Type {
            val superclass = subclass.genericSuperclass
            if (superclass is Class<*>) {
                throw RuntimeException("Missing type parameter.")
            }
            val parameterized = superclass as ParameterizedType
            return `$Gson$Types`.canonicalize(parameterized.actualTypeArguments[0])
        }


        private fun isAssignableFrom(from: Type, to: GenericArrayType): Boolean {
            val toGenericComponentType = to.genericComponentType
            if (toGenericComponentType is ParameterizedType) {
                var t: Type = from
                if (from is GenericArrayType) {
                   t = from.genericComponentType
                } else if (from is Class<*>) {
                    var classType: Class<*> = from
                    while (classType.isArray) {
                        classType = classType.componentType
                    }
                    t = classType
                }

                return isAssignableFrom(t, toGenericComponentType, hashMapOf())
            }

            // No generic defined on "to"; therefore, return true and let other
            // checks determine assignability
            return true
        }

        /**
         * Private recursive helper function to actually do the type-safe checking
         * of assignability.
         */
        private fun isAssignableFrom(from: Type?, to: ParameterizedType,
                                     typeVarMap: MutableMap<String, Type>): Boolean {
            if (from == null) {
                return false
            }
            if (to == from) {
                return true
            }

            // First figure out the class and any type information.
            val clazz = `$Gson$Types`.getRawType(from)
            var ptype: ParameterizedType? = null
            if (from is ParameterizedType) {
                ptype = from
            }

            // Load up parameterized variable info if it was parameterized.
            if (ptype != null) {
                val tArgs = ptype.actualTypeArguments
                val tParams: Array<out TypeVariable<out Class<out Any>>> = clazz.typeParameters
                for (i in tArgs.indices) {
                    var arg = tArgs[i]
                    val `var` = tParams[i]
                    while (arg is TypeVariable<*>) {
                        arg = typeVarMap[arg.name]!!
                    }
                    typeVarMap[`var`.name] = arg
                }

                // check if they are equivalent under our current mapping.
                if (typeEquals(ptype, to, typeVarMap)) {
                    return true
                }
            }
            for (iType in clazz.genericInterfaces) {
                if (isAssignableFrom(iType, to, HashMap(typeVarMap))) {
                    return true
                }
            }

            // Interfaces didn't work, try the superclass.
            val sType = clazz.genericSuperclass
            return isAssignableFrom(sType, to, HashMap(typeVarMap))
        }

        /**
         * Checks if two parameterized types are exactly equal, under the variable
         * replacement described in the typeVarMap.
         */
        private fun typeEquals(
                from: ParameterizedType,
                to: ParameterizedType,
                typeVarMap: Map<String, Type>
        ): Boolean {
            if (from.rawType == to.rawType) {
                val fromArgs = from.actualTypeArguments
                val toArgs = to.actualTypeArguments
                for (i in fromArgs.indices) {
                    if (!matches(fromArgs[i], toArgs[i], typeVarMap)) {
                        return false
                    }
                }
                return true
            }
            return false
        }

        /**
         * Checks if two types are the same or are equivalent under a variable mapping
         * given in the type map that was provided.
         */
        private fun matches(from: Type, to: Type, typeMap: Map<String, Type>): Boolean {
            return to == from || (from is TypeVariable<*> && to == typeMap[from.name])
        }


        private fun buildUnexpectedTypeError(
                token: Type,
                vararg expected: Class<*>
        ): AssertionError {
            // Build exception message
            val exceptionMessage = StringBuilder("Unexpected type. Expected one of: ")
            for (clazz in expected) {
                exceptionMessage.append(clazz.name).append(", ")
            }
            exceptionMessage.append("but got: ").append(token.javaClass.name)
                    .append(", for type token: ").append(token.toString()).append('.')

            return AssertionError(exceptionMessage.toString())
        }
    }


}