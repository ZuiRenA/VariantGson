package com.variant_gson.core

import com.variant_gson.core.reflect.TypeToken


interface TypeAdapterFactory {

    /**
     * Returns a type adapter for `type`, or null if this factory doesn't
     * support `type`.
     */
    fun <T> create(gson: Gson, typeToken: TypeToken<T>): TypeAdapter<T>?
}