package com.variant_gson.core

import com.variant_gson.core.annotations.SerializedName

/**
 * Serialized and deserializated Adapter
 *
 * @see [SerializedName]
 */
interface SerializedAdapter {

    fun adapter(annotation: SerializedName, defaultName: String): String

    fun adapterAlternate(annotation: SerializedName, defaultAlternate: Array<String>): Array<String>
}

internal val DEFAULT_SERIALIZED_ADAPTER = DefaultSerializedAdapter()

internal class DefaultSerializedAdapter : SerializedAdapter {
    override fun adapter(annotation: SerializedName, defaultName: String): String = defaultName

    override fun adapterAlternate(annotation: SerializedName, defaultAlternate: Array<String>): Array<String> = defaultAlternate
}