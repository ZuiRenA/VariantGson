package com.variant_gson.core.internal.bind

import com.variant_gson.core.*
import com.variant_gson.core.annotations.JsonAdapter
import com.variant_gson.core.internal.ConstructorConstructor
import com.variant_gson.core.reflect.TypeToken

/**
 * Created by ChaoShen on 2020/12/9
 */
class JsonAdapterAnnotationTypeAdapterFactory(
        private val constructorConstructor: ConstructorConstructor
) : TypeAdapterFactory {

    override fun <T> create(gson: Gson, targetType: TypeToken<T>): TypeAdapter<T>? {
        val rawType = targetType.getRawType()
        val annotation: JsonAdapter = rawType.getAnnotation(JsonAdapter::class.java) ?: return null
        return getTypeAdapter(constructorConstructor, gson, targetType, annotation) as? TypeAdapter<T>
    }

    fun getTypeAdapter(
            constructorConstructor: ConstructorConstructor,
            gson: Gson,
            targetType: TypeToken<*>,
            annotation: JsonAdapter
    ): TypeAdapter<*>? {

        var typeAdapter: TypeAdapter<*>? = when(val instance = constructorConstructor.get(TypeToken
                .get(annotation.value.java)).construct()) {
            is TypeAdapter<*> -> instance
            is TypeAdapterFactory -> instance.create(gson, targetType)
            is JsonSerializer<*>, is JsonDeserializer<*> -> {
                val serializer: JsonSerializer<*>? = if (instance is JsonSerializer<*>) instance else null
                val deserializer: JsonDeserializer<*>? = if (instance is JsonDeserializer<*>) instance else null
                TreeTypeAdapter.create(serializer, deserializer, gson, targetType, null)
            }
            else -> throw IllegalArgumentException("Invalid attempt to bind an instance of "
                    + instance.javaClass.name + " as a @JsonAdapter for " + targetType.toString()
                    + ". @JsonAdapter value must be a TypeAdapter, TypeAdapterFactory,"
                    + " JsonSerializer or JsonDeserializer.")
        }
        if (typeAdapter != null && annotation.nullSafe) {
            typeAdapter = typeAdapter.nullSafe()
        }
        return typeAdapter
    }
}