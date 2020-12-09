package com.variant_gson.core

import com.variant_gson.core.internal.LinkedTreeMap

/**
 * A class representing an object type in Json. An object consists of name-value pairs where names
 * are strings, and values are any other type of {@link JsonElement}. This allows for a creating a
 * tree of JsonElements. The member elements of this object are maintained in order they were added.
 */
class JsonObject : JsonElement() {
    private val members: LinkedTreeMap<String, JsonElement> = LinkedTreeMap<String, JsonElement>()


    /**
     * Creates a deep copy of this element and all its children
     * @since 2.8.2
     */
    override fun deepCopy(): JsonElement {
        val result = JsonObject()
        members.forEach {
            result.add(it.key, it.value.deepCopy())
        }
        return result
    }

    /**
     * Adds a member, which is a name-value pair, to self. The name must be a String, but the value
     * can be an arbitrary JsonElement, thereby allowing you to build a full tree of JsonElements
     * rooted at this node.
     *
     * @param property name of the member.
     * @param value the member object.
     */
    fun add(property: String, value: JsonElement) {
        members[property] = value ?: JsonNull.INSTANCE
    }


    /**
     * Removes the `property` from this [JsonObject].
     *
     * @param property name of the member that should be removed.
     * @return the [JsonElement] object that is being removed.
     * @since 1.3
     */
    fun remove(property: String?): JsonElement? {
        return members.remove(property)
    }

    /**
     * Convenience method to add a primitive member. The specified value is converted to a
     * JsonPrimitive of String.
     *
     * @param property name of the member.
     * @param value the string value associated with the member.
     */
    fun addProperty(property: String, value: String?) {
        add(property, value?.let { JsonPrimitive(it) } ?: JsonNull.INSTANCE)
    }

    /**
     * Convenience method to add a primitive member. The specified value is converted to a
     * JsonPrimitive of Number.
     *
     * @param property name of the member.
     * @param value the number value associated with the member.
     */
    fun addProperty(property: String, value: Number?) {
        add(property, value?.let { JsonPrimitive(it) } ?: JsonNull.INSTANCE)
    }


    /**
     * Convenience method to add a boolean member. The specified value is converted to a
     * JsonPrimitive of Boolean.
     *
     * @param property name of the member.
     * @param value the number value associated with the member.
     */
    fun addProperty(property: String, value: Boolean?) {
        add(property, value?.let { JsonPrimitive(it) } ?: JsonNull.INSTANCE)
    }

    /**
     * Convenience method to add a char member. The specified value is converted to a
     * JsonPrimitive of Character.
     *
     * @param property name of the member.
     * @param value the number value associated with the member.
     */
    fun addProperty(property: String, value: Char?) {
        add(property, value?.let { JsonPrimitive(it) } ?: JsonNull.INSTANCE)
    }

    /**
     * Returns a set of members of this object. The set is ordered, and the order is in which the
     * elements were added.
     *
     * @return a set of members of this object.
     */
    fun entrySet(): Set<Map.Entry<String, JsonElement>> {
        return members.entries
    }

    /**
     * Returns a set of members key values.
     *
     * @return a set of member keys as Strings
     * @since 2.8.1
     */
    fun keySet(): Set<String> {
        return members.keys
    }

    /**
     * Returns the number of key/value pairs in the object.
     *
     * @return the number of key/value pairs in the object.
     */
    fun size(): Int {
        return members.size
    }

    /**
     * Convenience method to check if a member with the specified name is present in this object.
     *
     * @param memberName name of the member that is being checked for presence.
     * @return true if there is a member with the specified name, false otherwise.
     */
    fun has(memberName: String?): Boolean {
        return members.containsKey(memberName)
    }

    /**
     * Returns the member with the specified name.
     *
     * @param memberName name of the member that is being requested.
     * @return the member matching the name. Null if no such member exists.
     */
    operator fun get(memberName: String?): JsonElement? {
        return members[memberName]
    }

    /**
     * Convenience method to get the specified member as a JsonPrimitive element.
     *
     * @param memberName name of the member being requested.
     * @return the JsonPrimitive corresponding to the specified member.
     */
    fun getAsJsonPrimitive(memberName: String): JsonPrimitive? {
        return members[memberName] as? JsonPrimitive
    }


    /**
     * Convenience method to get the specified member as a JsonArray.
     *
     * @param memberName name of the member being requested.
     * @return the JsonArray corresponding to the specified member.
     */
    fun getAsJsonArray(memberName: String): JsonArray? {
        return members[memberName] as? JsonArray
    }

    /**
     * Convenience method to get the specified member as a JsonObject.
     *
     * @param memberName name of the member being requested.
     * @return the JsonObject corresponding to the specified member.
     */
    fun getAsJsonObject(memberName: String): JsonObject? {
        return members[memberName] as? JsonObject
    }

    override fun equals(o: Any?): Boolean {
        return o === this || (o is JsonObject && o.members == members)
    }

    override fun hashCode(): Int {
        return members.hashCode()
    }
}