package com.variant_gson.core.internal.bind

import com.variant_gson.core.*
import com.variant_gson.core.annotations.SerializedName
import com.variant_gson.core.internal.EnumFactoryCreator
import com.variant_gson.core.internal.LazilyParsedNumber
import com.variant_gson.core.internal.`$Gson$Preconditions`
import com.variant_gson.core.reflect.TypeToken
import com.variant_gson.core.stream.JsonReader
import com.variant_gson.core.stream.JsonToken
import com.variant_gson.core.stream.JsonWriter
import java.io.IOException
import java.math.BigDecimal
import java.math.BigInteger
import java.net.InetAddress
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicIntegerArray

/**
 * Created by ChaoShen on 2020/12/9
 */
object TypeAdapters {

    @JvmField
    val CLASS = object : TypeAdapter<Class<*>>() {
        override fun write(out: JsonWriter, value: Class<*>?) {
            throw UnsupportedOperationException("Attempted to serialize java.lang.Class: ${value?.name}"
                    + ". Forgot to register a type adapter?")
        }

        override fun read(`in`: JsonReader): Class<*>? {
            throw UnsupportedOperationException(
                    "Attempted to deserialize a java.lang.Class. Forgot to register a type adapter?")
        }
    }.nullSafe()

    @JvmField
    val CLASS_FACTORY: TypeAdapterFactory = newFactory(Class::class.java, CLASS)

    @JvmField
    val BIT_SET: TypeAdapter<BitSet> = object : TypeAdapter<BitSet>() {
        override fun write(out: JsonWriter, value: BitSet?) {
            if (value == null) {
                out.nullValue()
            } else {
                out.beginArray()
                for (i in 0..value.size()) {
                    val srcValue = if (value.get(i)) 1 else 0
                    out.value(srcValue)
                }
                out.endArray()
            }
        }

        override fun read(`in`: JsonReader): BitSet {
            val bitset = BitSet()
            `in`.beginArray()
            var i = 0
            var tokenType: JsonToken = `in`.peek()
            while (tokenType !== JsonToken.END_ARRAY) {
                var set: Boolean
                set = when (tokenType) {
                    JsonToken.NUMBER -> `in`.nextInt() != 0
                    JsonToken.BOOLEAN -> `in`.nextBoolean()
                    JsonToken.STRING -> {
                        val stringValue: String = `in`.nextString()
                        try {
                            stringValue.toInt() != 0
                        } catch (e: NumberFormatException) {
                            throw JsonSyntaxException(
                                    "Error: Expecting: bitset number value (1, 0), Found: $stringValue")
                        }
                    }
                    else -> throw JsonSyntaxException("Invalid bitset value type: $tokenType")
                }
                if (set) {
                    bitset.set(i)
                }
                ++i
                tokenType = `in`.peek()
            }
            `in`.endArray()
            return bitset
        }
    }.nullSafe()

    @JvmField
    val BIT_SET_FACTORY: TypeAdapterFactory = newFactory(BitSet::class.java, BIT_SET)

    @JvmField
    val BOOLEAN: TypeAdapter<Boolean> = object : TypeAdapter<Boolean>() {
        override fun write(out: JsonWriter, value: Boolean?) {
            out.value(value)
        }

        override fun read(`in`: JsonReader): Boolean? {
            val peek = `in`.peek()
            if (peek == JsonToken.NULL) {
                `in`.nextNull()
                return null
            } else if (peek == JsonToken.STRING) {
                return `in`.nextString().toBoolean()
            }

            return `in`.nextBoolean()
        }
    }

    @JvmField
    val BOOLEAN_AS_STRING: TypeAdapter<Boolean> = object : TypeAdapter<Boolean>() {
        override fun write(out: JsonWriter, value: Boolean?) {
            out.value(value?.toString() ?: "null")
        }

        override fun read(`in`: JsonReader): Boolean? {
            if (`in`.peek() == JsonToken.NULL) {
                `in`.nextNull()
                return null
            }

            return `in`.nextString().toBoolean()
        }
    }

    @JvmField
    val BOOLEAN_FACTORY: TypeAdapterFactory =
            newFactory(`$Gson$Preconditions`.checkNotNull(Boolean::class.javaPrimitiveType), Boolean::class.java, BOOLEAN)

    @JvmField
    val BYTE: TypeAdapter<Number> = object : TypeAdapter<Number>() {
        override fun read(`in`: JsonReader): Number? {
            if (`in`.peek() === JsonToken.NULL) {
                `in`.nextNull()
                return null
            }
            return try {
                val intValue = `in`.nextInt()
                intValue.toByte()
            } catch (e: java.lang.NumberFormatException) {
                throw JsonSyntaxException(e)
            }
        }

        override fun write(out: JsonWriter, value: Number?) {
            out.value(value)
        }
    }

    @JvmField
    val BYTE_FACTORY = newFactory(`$Gson$Preconditions`.checkNotNull(Byte::class.javaPrimitiveType), Byte::class.java, BYTE)

    @JvmField
    val SHORT: TypeAdapter<Number> = object : TypeAdapter<Number>() {
        override fun read(`in`: JsonReader): Number? {
            if (`in`.peek() === JsonToken.NULL) {
                `in`.nextNull()
                return null
            }
            return try {
                `in`.nextInt().toShort()
            } catch (e: java.lang.NumberFormatException) {
                throw JsonSyntaxException(e)
            }
        }

        override fun write(out: JsonWriter, value: Number?) {
            out.value(value)
        }
    }

    @JvmField
    val SHORT_FACTORY = newFactory(`$Gson$Preconditions`.checkNotNull(Short::class.javaPrimitiveType), Short::class.java, SHORT)

    @JvmField
    val INTEGER: TypeAdapter<Number> = object : TypeAdapter<Number>() {
        override fun read(`in`: JsonReader): Number? {
            if (`in`.peek() === JsonToken.NULL) {
                `in`.nextNull()
                return null
            }
            return try {
                `in`.nextInt()
            } catch (e: java.lang.NumberFormatException) {
                throw JsonSyntaxException(e)
            }
        }

        override fun write(out: JsonWriter, value: Number?) {
            out.value(value)
        }
    }

    @JvmField
    val INTEGER_FACTORY = newFactory(`$Gson$Preconditions`.checkNotNull(Int::class.javaPrimitiveType), Int::class.java, INTEGER)

    @JvmField
    val ATOMIC_INTEGER = object : TypeAdapter<AtomicInteger>() {
        override fun read(`in`: JsonReader): AtomicInteger? {
            return try {
                AtomicInteger(`in`.nextInt())
            } catch (e: java.lang.NumberFormatException) {
                throw JsonSyntaxException(e)
            }
        }

        override fun write(out: JsonWriter, value: AtomicInteger?) {
            out.value(value?.get())
        }
    }.nullSafe()

    @JvmField
    val ATOMIC_INTEGER_FACTORY = newFactory(AtomicInteger::class.java, ATOMIC_INTEGER)

    @JvmField
    val ATOMIC_BOOLEAN = object : TypeAdapter<AtomicBoolean>() {
        override fun read(`in`: JsonReader): AtomicBoolean {
            return AtomicBoolean(`in`.nextBoolean())
        }

        override fun write(out: JsonWriter, value: AtomicBoolean?) {
            out.value(value?.get())
        }
    }.nullSafe()

    @JvmField
    val ATOMIC_BOOLEAN_FACTORY = newFactory(AtomicBoolean::class.java, ATOMIC_BOOLEAN)

    @JvmField
    val ATOMIC_INTEGER_ARRAY = object : TypeAdapter<AtomicIntegerArray>() {
        override fun read(`in`: JsonReader): AtomicIntegerArray {
            val list: MutableList<Int> = ArrayList()
            `in`.beginArray()
            while (`in`.hasNext()) {
                try {
                    val integer = `in`.nextInt()
                    list.add(integer)
                } catch (e: java.lang.NumberFormatException) {
                    throw JsonSyntaxException(e)
                }
            }
            `in`.endArray()
            val length = list.size
            val array = AtomicIntegerArray(length)
            for (i in 0 until length) {
                array[i] = list[i]
            }
            return array
        }

        override fun write(out: JsonWriter, value: AtomicIntegerArray?) {
            if (value == null) {
                out.nullValue()
            } else {
                out.beginArray()
                var i = 0
                val length = value.length()
                while (i < length) {
                    out.value(value[i])
                    i++
                }
                out.endArray()
            }
        }
    }.nullSafe()

    @JvmField
    val ATOMIC_INTEGER_ARRAY_FACTORY = newFactory(AtomicIntegerArray::class.java, ATOMIC_INTEGER_ARRAY)

    @JvmField
    val LONG: TypeAdapter<Number> = object : TypeAdapter<Number>() {
        override fun read(`in`: JsonReader): Number? {
            if (`in`.peek() === JsonToken.NULL) {
                `in`.nextNull()
                return null
            }
            return try {
                `in`.nextLong()
            } catch (e: java.lang.NumberFormatException) {
                throw JsonSyntaxException(e)
            }
        }

        override fun write(out: JsonWriter, value: Number?) {
            out.value(value)
        }
    }

    @JvmField
    val FLOAT: TypeAdapter<Number> = object : TypeAdapter<Number>() {
        override fun read(`in`: JsonReader): Number? {
            if (`in`.peek() === JsonToken.NULL) {
                `in`.nextNull()
                return null
            }
            return `in`.nextDouble().toFloat()
        }

        override fun write(out: JsonWriter, value: Number?) {
            out.value(value)
        }
    }

    @JvmField
    val DOUBLE: TypeAdapter<Number> = object : TypeAdapter<Number>() {
        override fun read(`in`: JsonReader): Number? {
            if (`in`.peek() === JsonToken.NULL) {
                `in`.nextNull()
                return null
            }
            return `in`.nextDouble()
        }

        override fun write(out: JsonWriter, value: Number?) {
            out.value(value)
        }
    }

    @JvmField
    val NUMBER: TypeAdapter<Number> = object : TypeAdapter<Number>() {
        @Throws(IOException::class)
        override fun read(`in`: JsonReader): Number? {
            val jsonToken = `in`.peek()
            return when (jsonToken) {
                JsonToken.NULL -> {
                    `in`.nextNull()
                    null
                }
                JsonToken.NUMBER, JsonToken.STRING -> LazilyParsedNumber(`in`.nextString())
                else -> throw JsonSyntaxException("Expecting number, got: $jsonToken")
            }
        }

        @Throws(IOException::class)
        override fun write(out: JsonWriter, value: Number?) {
            out.value(value)
        }
    }

    @JvmField
    val NUMBER_FACTORY = newFactory(Number::class.java, NUMBER)

    @JvmField
    val CHARACTER: TypeAdapter<Char> = object : TypeAdapter<Char>() {
        override fun read(`in`: JsonReader): Char? {
            if (`in`.peek() === JsonToken.NULL) {
                `in`.nextNull()
                return null
            }
            val str = `in`.nextString()
            if (str.length != 1) {
                throw JsonSyntaxException("Expecting character, got: $str")
            }
            return str[0]
        }

        override fun write(out: JsonWriter, value: Char?) {
            out.value(value?.toString())
        }
    }

    @JvmField
    val CHARACTER_FACTORY = newFactory(`$Gson$Preconditions`.checkNotNull(Char::class.javaPrimitiveType), Char::class.java, CHARACTER)

    @JvmField
    val STRING: TypeAdapter<String> = object : TypeAdapter<String>() {
        override fun read(`in`: JsonReader): String? {
            val peek = `in`.peek()
            if (peek === JsonToken.NULL) {
                `in`.nextNull()
                return null
            }
            /* coerce booleans to strings for backwards compatibility */return if (peek === JsonToken.BOOLEAN) {
                java.lang.Boolean.toString(`in`.nextBoolean())
            } else `in`.nextString()
        }

        override fun write(out: JsonWriter, value: String?) {
            out.value(value)
        }
    }

    @JvmField
    val BIG_DECIMAL: TypeAdapter<BigDecimal> = object : TypeAdapter<BigDecimal>() {
        override fun read(`in`: JsonReader): BigDecimal? {
            if (`in`.peek() === JsonToken.NULL) {
                `in`.nextNull()
                return null
            }
            return try {
                BigDecimal(`in`.nextString())
            } catch (e: java.lang.NumberFormatException) {
                throw JsonSyntaxException(e)
            }
        }

        override fun write(out: JsonWriter, value: BigDecimal?) {
            out.value(value)
        }
    }

    @JvmField
    val BIG_INTEGER: TypeAdapter<BigInteger> = object : TypeAdapter<BigInteger>() {
        override fun read(`in`: JsonReader): BigInteger? {
            if (`in`.peek() === JsonToken.NULL) {
                `in`.nextNull()
                return null
            }
            return try {
                BigInteger(`in`.nextString())
            } catch (e: java.lang.NumberFormatException) {
                throw JsonSyntaxException(e)
            }
        }

        override fun write(out: JsonWriter, value: BigInteger?) {
            out.value(value)
        }
    }

    @JvmField
    val STRING_FACTORY = newFactory(String::class.java, STRING)

    @JvmField
    val STRING_BUILDER: TypeAdapter<StringBuilder> = object : TypeAdapter<StringBuilder>() {
        override fun read(`in`: JsonReader): StringBuilder? {
            if (`in`.peek() === JsonToken.NULL) {
                `in`.nextNull()
                return null
            }
            return StringBuilder(`in`.nextString())
        }

        @Throws(IOException::class)
        override fun write(out: JsonWriter, value: StringBuilder?) {
            out.value(value?.toString())
        }
    }

    @JvmField
    val STRING_BUILDER_FACTORY = newFactory(java.lang.StringBuilder::class.java, STRING_BUILDER)

    @JvmField
    val STRING_BUFFER: TypeAdapter<StringBuffer> = object : TypeAdapter<StringBuffer>() {
        override fun read(`in`: JsonReader): StringBuffer? {
            if (`in`.peek() === JsonToken.NULL) {
                `in`.nextNull()
                return null
            }
            return StringBuffer(`in`.nextString())
        }

        override fun write(out: JsonWriter, value: StringBuffer?) {
            out.value(value?.toString())
        }
    }

    @JvmField
    val STRING_BUFFER_FACTORY = newFactory(StringBuffer::class.java, STRING_BUFFER)

    @JvmField
    val URL: TypeAdapter<URL> = object : TypeAdapter<URL>() {
        override fun read(`in`: JsonReader): URL? {
            if (`in`.peek() === JsonToken.NULL) {
                `in`.nextNull()
                return null
            }
            val nextString = `in`.nextString()
            return if ("null" == nextString) null else URL(nextString)
        }
        override fun write(out: JsonWriter, value: URL?) {
            out.value(value?.toExternalForm())
        }
    }

    @JvmField
    val URL_FACTORY = newFactory(java.net.URL::class.java, URL)

    @JvmField
    val URI: TypeAdapter<URI> = object : TypeAdapter<URI>() {
        @Throws(IOException::class)
        override fun read(`in`: JsonReader): URI? {
            if (`in`.peek() === JsonToken.NULL) {
                `in`.nextNull()
                return null
            }
            return try {
                val nextString = `in`.nextString()
                if ("null" == nextString) null else URI(nextString)
            } catch (e: URISyntaxException) {
                throw JsonIOException(e)
            }
        }

        @Throws(IOException::class)
        override fun write(out: JsonWriter, value: URI?) {
            out.value(value?.toASCIIString())
        }
    }

    @JvmField
    val URI_FACTORY = newFactory(java.net.URI::class.java, URI)

    @JvmField
    val INET_ADDRESS: TypeAdapter<InetAddress> = object : TypeAdapter<InetAddress>() {
        override fun read(`in`: JsonReader): InetAddress? {
            if (`in`.peek() === JsonToken.NULL) {
                `in`.nextNull()
                return null
            }
            // regrettably, this should have included both the host name and the host address
            return InetAddress.getByName(`in`.nextString())
        }

        override fun write(out: JsonWriter, value: InetAddress?) {
            out.value(value?.hostAddress)
        }
    }

    @JvmField
    val INET_ADDRESS_FACTORY = newTypeHierarchyFactory(InetAddress::class.java, INET_ADDRESS)

    @JvmField
    val UUID: TypeAdapter<UUID> = object : TypeAdapter<UUID>() {
        override fun read(`in`: JsonReader): UUID? {
            if (`in`.peek() === JsonToken.NULL) {
                `in`.nextNull()
                return null
            }
            return java.util.UUID.fromString(`in`.nextString())
        }

        override fun write(out: JsonWriter, value: UUID?) {
            out.value(value?.toString())
        }
    }

    @JvmField
    val UUID_FACTORY = newFactory(java.util.UUID::class.java, UUID)

    @JvmField
    val CURRENCY = object : TypeAdapter<Currency>() {
        override fun read(`in`: JsonReader): Currency? {
            return Currency.getInstance(`in`.nextString())
        }

        override fun write(out: JsonWriter, value: Currency?) {
            out.value(value?.currencyCode)
        }
    }.nullSafe()

    @JvmField
    val CURRENCY_FACTORY = newFactory(Currency::class.java, CURRENCY)

    @JvmField
    val TIMESTAMP_FACTORY: TypeAdapterFactory = object : TypeAdapterFactory {
        override fun <T> create(gson: Gson, typeToken: TypeToken<T>): TypeAdapter<T>? {
            if (typeToken.getRawType() !== Timestamp::class.java) {
                return null
            }
            val dateTypeAdapter: TypeAdapter<Date> = gson.getAdapter(Date::class.java)
            return object : TypeAdapter<Timestamp>() {
                override fun read(`in`: JsonReader): Timestamp? {
                    val date = dateTypeAdapter.read(`in`)
                    return if (date != null) Timestamp(date.time) else null
                }

                override fun write(out: JsonWriter, value: Timestamp?) {
                    dateTypeAdapter.write(out, value)
                }
            } as TypeAdapter<T>
        }
    }

    @JvmField
    val CALENDAR: TypeAdapter<Calendar> = object : TypeAdapter<Calendar>() {
        private val YEAR = "year"
        private val MONTH = "month"
        private val DAY_OF_MONTH = "dayOfMonth"
        private val HOUR_OF_DAY = "hourOfDay"
        private val MINUTE = "minute"
        private val SECOND = "second"

        @Throws(IOException::class)
        override fun read(`in`: JsonReader): Calendar? {
            if (`in`.peek() === JsonToken.NULL) {
                `in`.nextNull()
                return null
            }
            `in`.beginObject()
            var year = 0
            var month = 0
            var dayOfMonth = 0
            var hourOfDay = 0
            var minute = 0
            var second = 0
            while (`in`.peek() !== JsonToken.END_OBJECT) {
                val name = `in`.nextName()
                val value = `in`.nextInt()
                if (YEAR == name) {
                    year = value
                } else if (MONTH == name) {
                    month = value
                } else if (DAY_OF_MONTH == name) {
                    dayOfMonth = value
                } else if (HOUR_OF_DAY == name) {
                    hourOfDay = value
                } else if (MINUTE == name) {
                    minute = value
                } else if (SECOND == name) {
                    second = value
                }
            }
            `in`.endObject()
            return GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second)
        }

        override fun write(out: JsonWriter, value: Calendar?) {
            if (value == null) {
                out.nullValue()
                return
            }
            out.beginObject()
            out.name(YEAR)
            out.value(value[Calendar.YEAR])
            out.name(MONTH)
            out.value(value[Calendar.MONTH])
            out.name(DAY_OF_MONTH)
            out.value(value[Calendar.DAY_OF_MONTH])
            out.name(HOUR_OF_DAY)
            out.value(value[Calendar.HOUR_OF_DAY])
            out.name(MINUTE)
            out.value(value[Calendar.MINUTE])
            out.name(SECOND)
            out.value(value[Calendar.SECOND])
            out.endObject()
        }
    }

    @JvmField
    val CALENDAR_FACTORY = newFactoryForMultipleTypes(Calendar::class.java, GregorianCalendar::class.java, CALENDAR)

    @JvmField
    val LOCALE: TypeAdapter<Locale> = object : TypeAdapter<Locale>() {
        @Throws(IOException::class)
        override fun read(`in`: JsonReader): Locale? {
            if (`in`.peek() === JsonToken.NULL) {
                `in`.nextNull()
                return null
            }
            val locale = `in`.nextString()
            val tokenizer = StringTokenizer(locale, "_")
            var language: String? = null
            var country: String? = null
            var variant: String? = null
            if (tokenizer.hasMoreElements()) {
                language = tokenizer.nextToken()
            }
            if (tokenizer.hasMoreElements()) {
                country = tokenizer.nextToken()
            }
            if (tokenizer.hasMoreElements()) {
                variant = tokenizer.nextToken()
            }
            return if (country == null && variant == null) {
                Locale(language)
            } else if (variant == null) {
                Locale(language, country)
            } else {
                Locale(language, country, variant)
            }
        }

        @Throws(IOException::class)
        override fun write(out: JsonWriter, value: Locale?) {
            out.value(value?.toString())
        }
    }

    @JvmField
    val LOCALE_FACTORY = newFactory(Locale::class.java, LOCALE)

    @JvmField
    val JSON_ELEMENT: TypeAdapter<JsonElement> = object : TypeAdapter<JsonElement>() {
        override fun read(`in`: JsonReader): JsonElement {
            return when (`in`.peek()) {
                JsonToken.STRING -> JsonPrimitive(`in`.nextString())
                JsonToken.NUMBER -> {
                    val number = `in`.nextString()
                    JsonPrimitive(LazilyParsedNumber(number))
                }
                JsonToken.BOOLEAN -> JsonPrimitive(`in`.nextBoolean())
                JsonToken.NULL -> {
                    `in`.nextNull()
                    JsonNull.INSTANCE
                }
                JsonToken.BEGIN_ARRAY -> {
                    val array = JsonArray()
                    `in`.beginArray()
                    while (`in`.hasNext()) {
                        array.add(read(`in`))
                    }
                    `in`.endArray()
                    array
                }
                JsonToken.BEGIN_OBJECT -> {
                    val `object` = JsonObject()
                    `in`.beginObject()
                    while (`in`.hasNext()) {
                        `object`.add(`in`.nextName(), read(`in`)!!)
                    }
                    `in`.endObject()
                    `object`
                }
                JsonToken.END_DOCUMENT, JsonToken.NAME, JsonToken.END_OBJECT, JsonToken.END_ARRAY ->
                    throw IllegalArgumentException()
                else -> throw IllegalArgumentException()
            }
        }

        override fun write(out: JsonWriter, value: JsonElement?) {
            if (value == null || value.isJsonNull()) {
                out.nullValue()
            } else if (value.isJsonPrimitive()) {
                val primitive = value.getAsJsonPrimitive()
                when {
                    primitive.isNumber() -> {
                        out.value(primitive.getAsNumber())
                    }
                    primitive.isBoolean() -> {
                        out.value(primitive.getAsBoolean())
                    }
                    else -> {
                        out.value(primitive.getAsString())
                    }
                }
            } else if (value.isJsonArray()) {
                out.beginArray()
                for (e in value.getAsJsonArray()) {
                    write(out, e)
                }
                out.endArray()
            } else if (value.isJsonObject()) {
                out.beginObject()
                for ((key, value1) in value.getAsJsonObject().entrySet()) {
                    out.name(key)
                    write(out, value1)
                }
                out.endObject()
            } else {
                throw IllegalArgumentException("Couldn't write " + value.javaClass)
            }
        }
    }

    @JvmField
    val JSON_ELEMENT_FACTORY = newTypeHierarchyFactory(JsonElement::class.java, JSON_ELEMENT)

    internal class EnumTypeAdapter<T : Enum<T>>(
            classOfT: Class<T>,
            serializedAdapter: SerializedAdapter,
    ) : TypeAdapter<T>() {
        private val nameToConstant: MutableMap<String, T> = HashMap()
        private val constantToName: MutableMap<T, String> = HashMap()

        override fun read(`in`: JsonReader): T? {
            if (`in`.peek() === JsonToken.NULL) {
                `in`.nextNull()
                return null
            }
            return nameToConstant[`in`.nextString()]
        }

        override fun write(out: JsonWriter, value: T?) {
            out.value(if (value == null) null else constantToName[value])
        }

        init {
            try {
                for (constant in classOfT.enumConstants) {
                    var name = constant.name
                    val annotation: SerializedName? = classOfT.getField(name).getAnnotation(SerializedName::class.java)
                    if (annotation != null) {
                        name = serializedAdapter.adapter(annotation, annotation.value)
                        val alternateList = serializedAdapter.adapterAlternate(annotation, annotation.alternate)
                        for (alternate in alternateList) {
                            nameToConstant[alternate] = constant
                        }
                    }
                    nameToConstant[name] = constant
                    constantToName[constant] = name
                }
            } catch (e: NoSuchFieldException) {
                throw AssertionError(e)
            }
        }
    }

    @JvmStatic
    @JvmOverloads
    fun ENUM_FACTORY(
            serializedAdapter: SerializedAdapter = DEFAULT_SERIALIZED_ADAPTER
    ): TypeAdapterFactory = EnumFactoryCreator.CREATOR(serializedAdapter)


    @JvmStatic
    fun <TT> newFactory(
            type: TypeToken<TT>,
            typeAdapter: TypeAdapter<TT>
    ): TypeAdapterFactory = object : TypeAdapterFactory {
        override fun <T> create(gson: Gson, typeToken: TypeToken<T>): TypeAdapter<T>? {
            return if (typeToken == type) typeAdapter as TypeAdapter<T> else null
        }
    }

    @JvmStatic
    fun <TT> newFactory(
            type: Class<TT>,
            typeAdapter: TypeAdapter<TT>
    ): TypeAdapterFactory = object : TypeAdapterFactory {
        override fun <T> create(gson: Gson, typeToken: TypeToken<T>): TypeAdapter<T>? {
            return if (typeToken.getRawType() == type) typeAdapter as TypeAdapter<T> else null
        }

        override fun toString(): String {
            return "Factory[type=" + type.name + ",adapter=" + typeAdapter + "]"
        }
    }

    @JvmStatic
    fun <TT> newFactory(
            unboxed: Class<TT>,
            boxed: Class<TT>,
            typeAdapter: TypeAdapter<in TT>
    ): TypeAdapterFactory = object : TypeAdapterFactory {
        override fun <T> create(gson: Gson, typeToken: TypeToken<T>): TypeAdapter<T>? {
            val rawType: Class<in T> = typeToken.getRawType()
            return if (rawType == unboxed || rawType == boxed) typeAdapter as TypeAdapter<T> else null
        }

        override fun toString(): String {
            return ("Factory[type=" + boxed.name + "+" + unboxed.name + ",adapter=" + typeAdapter + "]")
        }
    }

    @JvmStatic
    fun <TT> newFactoryForMultipleTypes(
            base: Class<TT>,
            sub: Class<out TT>,
            typeAdapter: TypeAdapter<in TT>
    ): TypeAdapterFactory = object : TypeAdapterFactory {
        override fun <T> create(gson: Gson, typeToken: TypeToken<T>): TypeAdapter<T>? {
            val rawType = typeToken.getRawType()
            return if (rawType == base || rawType == sub) typeAdapter as TypeAdapter<T> else null
        }

        override fun toString(): String {
            return ("Factory[type=" + base.name + "+" + sub.name + ",adapter=" + typeAdapter + "]")
        }
    }

    /**
     * Returns a factory for all subtypes of [typeAdapter]. We do a runtime check to confirm
     * that the deserialized type matches the type requested.
     */
    @JvmStatic
    fun <TT> newTypeHierarchyFactory(
            clazz: Class<TT>,
            typeAdapter: TypeAdapter<TT>
    ): TypeAdapterFactory = object : TypeAdapterFactory {
        override fun <T> create(gson: Gson, typeToken: TypeToken<T>): TypeAdapter<T>? {
            val requestedType = typeToken.getRawType()
            if (!clazz.isAssignableFrom(requestedType)) {
                return null
            }
            return object : TypeAdapter<TT>() {
                override fun write(out: JsonWriter, value: TT?) {
                    typeAdapter.write(out, value)
                }

                override fun read(`in`: JsonReader): TT? {
                    val result = typeAdapter.read(`in`)
                    if (result != null && requestedType.isInstance(result)) {
                        throw JsonSyntaxException("Expected a " + requestedType.name
                                + " but was " + result.javaClass.name)
                    }
                    return result
                }
            } as TypeAdapter<T>
        }

        override fun toString(): String {
            return "Factory[typeHierarchy=" + clazz.name + ",adapter=" + typeAdapter + "]"
        }
    }
}