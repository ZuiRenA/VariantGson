package com.variant_gson.core

import com.variant_gson.core.Gson.*
import com.variant_gson.core.internal.Excluder
import com.variant_gson.core.internal.`$Gson$Preconditions`
import com.variant_gson.core.internal.bind.TreeTypeAdapter
import com.variant_gson.core.internal.bind.TypeAdapters
import com.variant_gson.core.internal.bind.TypeAdaptersJava
import com.variant_gson.core.reflect.TypeToken
import java.lang.reflect.Type
import java.sql.Date
import java.sql.Timestamp
import java.text.DateFormat
import java.util.*

/**
 *
 * Use this builder to construct a [Gson] instance when you need to set configuration
 * options other than the default. For [Gson] with default configuration, it is simpler to
 * use `new Gson()`. `GsonBuilder` is best used by creating it, and then invoking its
 * various configuration methods, and finally calling create.
 *
 *
 * The following is an example shows how to use the `GsonBuilder` to construct a Gson
 * instance:
 *
 * <pre>
 * Gson gson = new GsonBuilder()
 * .registerTypeAdapter(Id.class, new IdTypeAdapter())
 * .enableComplexMapKeySerialization()
 * .serializeNulls()
 * .setDateFormat(DateFormat.LONG)
 * .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
 * .setPrettyPrinting()
 * .setVersion(1.0)
 * .create();
</pre> *
 *
 *
 * NOTES:
 *
 *  *  the order of invocation of configuration methods does not matter.
 *  *  The default serialization of [Date] and its subclasses in Gson does
 * not contain time-zone information. So, if you are using date/time instances,
 * use `GsonBuilder` and its `setDateFormat` methods.
 *
 *
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 * @author Jesse Wilson
 */
class GsonBuilder {
    private var excluder: Excluder = Excluder.DEFAULT
    private var longSerializationPolicy = LongSerializationPolicy.DEFAULT
    private var fieldNamingPolicy: FieldNamingStrategy = FieldNamingPolicy.IDENTITY
    private val instanceCreators: MutableMap<Type, InstanceCreator<*>> = HashMap()
    private val factories: MutableList<TypeAdapterFactory?> = ArrayList()

    /** tree-style hierarchy factories. These come after factories for backwards compatibility.  */
    private val hierarchyFactories: MutableList<TypeAdapterFactory?> = ArrayList()
    private var serializeNulls: Boolean = DEFAULT_SERIALIZE_NULLS
    private var datePattern: String? = null
    private var dateStyle = DateFormat.DEFAULT
    private var timeStyle = DateFormat.DEFAULT
    private var complexMapKeySerialization: Boolean = DEFAULT_COMPLEX_MAP_KEYS
    private var serializeSpecialFloatingPointValues: Boolean = DEFAULT_SPECIALIZE_FLOAT_VALUES
    private var escapeHtmlChars: Boolean = DEFAULT_ESCAPE_HTML
    private var prettyPrinting: Boolean = DEFAULT_PRETTY_PRINT
    private var generateNonExecutableJson: Boolean = DEFAULT_JSON_NON_EXECUTABLE
    private var lenient: Boolean = DEFAULT_LENIENT
    private var serializedAdapter: SerializedAdapter = DEFAULT_SERIALIZED_ADAPTER

    /**
     * Creates a GsonBuilder instance that can be used to build Gson with various configuration
     * settings. GsonBuilder follows the builder pattern, and it is typically used by first
     * invoking various configuration methods to set desired options, and finally calling
     * [.create].
     */
    constructor() {}

    /**
     * Constructs a GsonBuilder instance from a Gson instance. The newly constructed GsonBuilder
     * has the same configuration as the previously built Gson instance.
     *
     * @param gson the gson instance whose configuration should by applied to a new GsonBuilder.
     */
    internal constructor(gson: Gson) {
        excluder = gson.excluder
        fieldNamingPolicy = gson.fieldNamingStrategy
        instanceCreators.putAll(gson.instanceCreators)
        serializeNulls = gson.serializeNulls
        complexMapKeySerialization = gson.complexMapKeySerialization
        generateNonExecutableJson = gson.generateNonExecutableJson
        escapeHtmlChars = gson.htmlSafe
        prettyPrinting = gson.prettyPrinting
        lenient = gson.lenient
        serializeSpecialFloatingPointValues = gson.serializeSpecialFloatingPointValues
        longSerializationPolicy = gson.longSerializationPolicy
        datePattern = gson.datePattern
        dateStyle = gson.dateStyle
        timeStyle = gson.timeStyle
        factories.addAll(gson.builderFactories)
        hierarchyFactories.addAll(gson.builderHierarchyFactories)
        serializedAdapter = gson.serializedAdapter
    }

    /**
     * Configures Gson to enable versioning support.
     *
     * @param ignoreVersionsAfter any field or type marked with a version higher than this value
     * are ignored during serialization or deserialization.
     * @return a reference to this `GsonBuilder` object to fulfill the "Builder" pattern
     */
    fun setVersion(ignoreVersionsAfter: Double): GsonBuilder {
        excluder = excluder.withVersion(ignoreVersionsAfter)
        return this
    }

    /**
     * Configures Gson to excludes all class fields that have the specified modifiers. By default,
     * Gson will exclude all fields marked transient or static. This method will override that
     * behavior.
     *
     * @param modifiers the field modifiers. You must use the modifiers specified in the
     * [java.lang.reflect.Modifier] class. For example,
     * [java.lang.reflect.Modifier.TRANSIENT],
     * [java.lang.reflect.Modifier.STATIC].
     * @return a reference to this `GsonBuilder` object to fulfill the "Builder" pattern
     */
    fun excludeFieldsWithModifiers(vararg modifiers: Int): GsonBuilder {
        excluder = excluder.withModifiers(*modifiers)
        return this
    }

    /**
     * Makes the output JSON non-executable in Javascript by prefixing the generated JSON with some
     * special text. This prevents attacks from third-party sites through script sourcing. See
     * [Gson Issue 42](http://code.google.com/p/google-gson/issues/detail?id=42)
     * for details.
     *
     * @return a reference to this `GsonBuilder` object to fulfill the "Builder" pattern
     * @since 1.3
     */
    fun generateNonExecutableJson(): GsonBuilder {
        generateNonExecutableJson = true
        return this
    }

    /**
     * Configures Gson to exclude all fields from consideration for serialization or deserialization
     * that do not have the [com.google.gson.annotations.Expose] annotation.
     *
     * @return a reference to this `GsonBuilder` object to fulfill the "Builder" pattern
     */
    fun excludeFieldsWithoutExposeAnnotation(): GsonBuilder {
        excluder = excluder.excludeFieldsWithoutExposeAnnotation()
        return this
    }

    /**
     * Configure Gson to serialize null fields. By default, Gson omits all fields that are null
     * during serialization.
     *
     * @return a reference to this `GsonBuilder` object to fulfill the "Builder" pattern
     * @since 1.2
     */
    fun serializeNulls(): GsonBuilder {
        serializeNulls = true
        return this
    }

    /**
     * Enabling this feature will only change the serialized form if the map key is
     * a complex type (i.e. non-primitive) in its **serialized** JSON
     * form. The default implementation of map serialization uses `toString()`
     * on the key; however, when this is called then one of the following cases
     * apply:
     *
     * <h3>Maps as JSON objects</h3>
     * For this case, assume that a type adapter is registered to serialize and
     * deserialize some `Point` class, which contains an x and y coordinate,
     * to/from the JSON Primitive string value `"(x,y)"`. The Java map would
     * then be serialized as a [JsonObject].
     *
     *
     * Below is an example:
     * <pre>  `Gson gson = new GsonBuilder()
     * .register(Point.class, new MyPointTypeAdapter())
     * .enableComplexMapKeySerialization()
     * .create();
     *
     * Map<Point, String> original = new LinkedHashMap<Point, String>();
     * original.put(new Point(5, 6), "a");
     * original.put(new Point(8, 8), "b");
     * System.out.println(gson.toJson(original, type));
    `</pre> *
     * The above code prints this JSON object:<pre>  `{
     * "(5,6)": "a",
     * "(8,8)": "b"
     * }
    `</pre> *
     *
     * <h3>Maps as JSON arrays</h3>
     * For this case, assume that a type adapter was NOT registered for some
     * `Point` class, but rather the default Gson serialization is applied.
     * In this case, some `new Point(2,3)` would serialize as `{"x":2,"y":5}`.
     *
     *
     * Given the assumption above, a `Map<Point, String>` will be
     * serialize as an array of arrays (can be viewed as an entry set of pairs).
     *
     *
     * Below is an example of serializing complex types as JSON arrays:
     * <pre> `Gson gson = new GsonBuilder()
     * .enableComplexMapKeySerialization()
     * .create();
     *
     * Map<Point, String> original = new LinkedHashMap<Point, String>();
     * original.put(new Point(5, 6), "a");
     * original.put(new Point(8, 8), "b");
     * System.out.println(gson.toJson(original, type));
    ` *
     *
     * The JSON output would look as follows:
     * <pre>   `[
     * [
     * {
     * "x": 5,
     * "y": 6
     * },
     * "a"
     * ],
     * [
     * {
     * "x": 8,
     * "y": 8
     * },
     * "b"
     * ]
     * ]
    `</pre> *
     *
     * @return a reference to this `GsonBuilder` object to fulfill the "Builder" pattern
     * @since 1.7
    </pre> */
    fun enableComplexMapKeySerialization(): GsonBuilder {
        complexMapKeySerialization = true
        return this
    }

    /**
     * Configures Gson to exclude inner classes during serialization.
     *
     * @return a reference to this `GsonBuilder` object to fulfill the "Builder" pattern
     * @since 1.3
     */
    fun disableInnerClassSerialization(): GsonBuilder {
        excluder = excluder.disableInnerClassSerialization()
        return this
    }

    /**
     * Configures Gson to apply a specific serialization policy for `Long` and `long`
     * objects.
     *
     * @param serializationPolicy the particular policy to use for serializing longs.
     * @return a reference to this `GsonBuilder` object to fulfill the "Builder" pattern
     * @since 1.3
     */
    fun setLongSerializationPolicy(serializationPolicy: LongSerializationPolicy): GsonBuilder {
        longSerializationPolicy = serializationPolicy
        return this
    }

    /**
     * Configures Gson to apply a specific naming policy to an object's field during serialization
     * and deserialization.
     *
     * @param namingConvention the JSON field naming convention to use for serialization and
     * deserialization.
     * @return a reference to this `GsonBuilder` object to fulfill the "Builder" pattern
     */
    fun setFieldNamingPolicy(namingConvention: FieldNamingPolicy): GsonBuilder {
        fieldNamingPolicy = namingConvention
        return this
    }

    /**
     * Configures Gson to apply a specific naming policy strategy to an object's field during
     * serialization and deserialization.
     *
     * @param fieldNamingStrategy the actual naming strategy to apply to the fields
     * @return a reference to this `GsonBuilder` object to fulfill the "Builder" pattern
     * @since 1.3
     */
    fun setFieldNamingStrategy(fieldNamingStrategy: FieldNamingStrategy): GsonBuilder {
        fieldNamingPolicy = fieldNamingStrategy
        return this
    }

    /**
     * @param serializedAdapter the actual adapter to serialized name
     */
    fun setSerializedAdapter(serializedAdapter: SerializedAdapter): GsonBuilder = apply {
        this.serializedAdapter = serializedAdapter
    }

    /**
     * Configures Gson to apply a set of exclusion strategies during both serialization and
     * deserialization. Each of the `strategies` will be applied as a disjunction rule.
     * This means that if one of the `strategies` suggests that a field (or class) should be
     * skipped then that field (or object) is skipped during serialization/deserialization.
     *
     * @param strategies the set of strategy object to apply during object (de)serialization.
     * @return a reference to this `GsonBuilder` object to fulfill the "Builder" pattern
     * @since 1.4
     */
    fun setExclusionStrategies(vararg strategies: ExclusionStrategy?): GsonBuilder {
        for (strategy in strategies) {
            excluder = excluder.withExclusionStrategy(strategy, true, true)
        }
        return this
    }

    /**
     * Configures Gson to apply the passed in exclusion strategy during serialization.
     * If this method is invoked numerous times with different exclusion strategy objects
     * then the exclusion strategies that were added will be applied as a disjunction rule.
     * This means that if one of the added exclusion strategies suggests that a field (or
     * class) should be skipped then that field (or object) is skipped during its
     * serialization.
     *
     * @param strategy an exclusion strategy to apply during serialization.
     * @return a reference to this `GsonBuilder` object to fulfill the "Builder" pattern
     * @since 1.7
     */
    fun addSerializationExclusionStrategy(strategy: ExclusionStrategy?): GsonBuilder {
        excluder = excluder.withExclusionStrategy(strategy, true, false)
        return this
    }

    /**
     * Configures Gson to apply the passed in exclusion strategy during deserialization.
     * If this method is invoked numerous times with different exclusion strategy objects
     * then the exclusion strategies that were added will be applied as a disjunction rule.
     * This means that if one of the added exclusion strategies suggests that a field (or
     * class) should be skipped then that field (or object) is skipped during its
     * deserialization.
     *
     * @param strategy an exclusion strategy to apply during deserialization.
     * @return a reference to this `GsonBuilder` object to fulfill the "Builder" pattern
     * @since 1.7
     */
    fun addDeserializationExclusionStrategy(strategy: ExclusionStrategy?): GsonBuilder {
        excluder = excluder.withExclusionStrategy(strategy, false, true)
        return this
    }

    /**
     * Configures Gson to output Json that fits in a page for pretty printing. This option only
     * affects Json serialization.
     *
     * @return a reference to this `GsonBuilder` object to fulfill the "Builder" pattern
     */
    fun setPrettyPrinting(): GsonBuilder {
        prettyPrinting = true
        return this
    }

    /**
     * By default, Gson is strict and only accepts JSON as specified by
     * [RFC 4627](http://www.ietf.org/rfc/rfc4627.txt). This option makes the parser
     * liberal in what it accepts.
     *
     * @return a reference to this `GsonBuilder` object to fulfill the "Builder" pattern
     * @see JsonReader.setLenient
     */
    fun setLenient(): GsonBuilder {
        lenient = true
        return this
    }

    /**
     * By default, Gson escapes HTML characters such as &lt; &gt; etc. Use this option to configure
     * Gson to pass-through HTML characters as is.
     *
     * @return a reference to this `GsonBuilder` object to fulfill the "Builder" pattern
     * @since 1.3
     */
    fun disableHtmlEscaping(): GsonBuilder {
        escapeHtmlChars = false
        return this
    }

    /**
     * Configures Gson to serialize `Date` objects according to the pattern provided. You can
     * call this method or [.setDateFormat] multiple times, but only the last invocation
     * will be used to decide the serialization format.
     *
     *
     * The date format will be used to serialize and deserialize [java.util.Date], [ ] and [java.sql.Date].
     *
     *
     * Note that this pattern must abide by the convention provided by `SimpleDateFormat`
     * class. See the documentation in [java.text.SimpleDateFormat] for more information on
     * valid date and time patterns.
     *
     * @param pattern the pattern that dates will be serialized/deserialized to/from
     * @return a reference to this `GsonBuilder` object to fulfill the "Builder" pattern
     * @since 1.2
     */
    fun setDateFormat(pattern: String?): GsonBuilder {
        // TODO(Joel): Make this fail fast if it is an invalid date format
        datePattern = pattern
        return this
    }

    /**
     * Configures Gson to to serialize `Date` objects according to the style value provided.
     * You can call this method or [.setDateFormat] multiple times, but only the last
     * invocation will be used to decide the serialization format.
     *
     *
     * Note that this style value should be one of the predefined constants in the
     * `DateFormat` class. See the documentation in [java.text.DateFormat] for more
     * information on the valid style constants.
     *
     * @param style the predefined date style that date objects will be serialized/deserialized
     * to/from
     * @return a reference to this `GsonBuilder` object to fulfill the "Builder" pattern
     * @since 1.2
     */
    fun setDateFormat(style: Int): GsonBuilder {
        dateStyle = style
        datePattern = null
        return this
    }

    /**
     * Configures Gson to to serialize `Date` objects according to the style value provided.
     * You can call this method or [.setDateFormat] multiple times, but only the last
     * invocation will be used to decide the serialization format.
     *
     *
     * Note that this style value should be one of the predefined constants in the
     * `DateFormat` class. See the documentation in [java.text.DateFormat] for more
     * information on the valid style constants.
     *
     * @param dateStyle the predefined date style that date objects will be serialized/deserialized
     * to/from
     * @param timeStyle the predefined style for the time portion of the date objects
     * @return a reference to this `GsonBuilder` object to fulfill the "Builder" pattern
     * @since 1.2
     */
    fun setDateFormat(dateStyle: Int, timeStyle: Int): GsonBuilder {
        this.dateStyle = dateStyle
        this.timeStyle = timeStyle
        datePattern = null
        return this
    }

    /**
     * Configures Gson for custom serialization or deserialization. This method combines the
     * registration of an [TypeAdapter], [InstanceCreator], [JsonSerializer], and a
     * [JsonDeserializer]. It is best used when a single object `typeAdapter` implements
     * all the required interfaces for custom serialization with Gson. If a type adapter was
     * previously registered for the specified `type`, it is overwritten.
     *
     *
     * This registers the type specified and no other types: you must manually register related
     * types! For example, applications registering `boolean.class` should also register `Boolean.class`.
     *
     * @param type the type definition for the type adapter being registered
     * @param typeAdapter This object must implement at least one of the [TypeAdapter],
     * [InstanceCreator], [JsonSerializer], and a [JsonDeserializer] interfaces.
     * @return a reference to this `GsonBuilder` object to fulfill the "Builder" pattern
     */
    fun registerTypeAdapter(type: Type, typeAdapter: Any?): GsonBuilder {
        `$Gson$Preconditions`.checkArgument(typeAdapter is JsonSerializer<*>
                || typeAdapter is JsonDeserializer<*>
                || typeAdapter is InstanceCreator<*>
                || typeAdapter is TypeAdapter<*>)
        if (typeAdapter is InstanceCreator<*>) {
            instanceCreators[type] = typeAdapter
        }
        if (typeAdapter is JsonSerializer<*> || typeAdapter is JsonDeserializer<*>) {
            val typeToken: TypeToken<*> = TypeToken.get(type)
            factories.add(TreeTypeAdapter.newFactoryWithMatchRawType(typeToken, typeAdapter))
        }
        if (typeAdapter is TypeAdapter<*>) {
            factories.add(TypeAdaptersJava.newFactory(TypeToken.get(type), typeAdapter))
        }
        return this
    }

    /**
     * Register a factory for type adapters. Registering a factory is useful when the type
     * adapter needs to be configured based on the type of the field being processed. Gson
     * is designed to handle a large number of factories, so you should consider registering
     * them to be at par with registering an individual type adapter.
     *
     * @since 2.1
     */
    fun registerTypeAdapterFactory(factory: TypeAdapterFactory?): GsonBuilder {
        factories.add(factory)
        return this
    }

    /**
     * Configures Gson for custom serialization or deserialization for an inheritance type hierarchy.
     * This method combines the registration of a [TypeAdapter], [JsonSerializer] and
     * a [JsonDeserializer]. If a type adapter was previously registered for the specified
     * type hierarchy, it is overridden. If a type adapter is registered for a specific type in
     * the type hierarchy, it will be invoked instead of the one registered for the type hierarchy.
     *
     * @param baseType the class definition for the type adapter being registered for the base class
     * or interface
     * @param typeAdapter This object must implement at least one of [TypeAdapter],
     * [JsonSerializer] or [JsonDeserializer] interfaces.
     * @return a reference to this `GsonBuilder` object to fulfill the "Builder" pattern
     * @since 1.7
     */
    fun registerTypeHierarchyAdapter(baseType: Class<*>?, typeAdapter: Any?): GsonBuilder {
        `$Gson$Preconditions`.checkArgument(typeAdapter is JsonSerializer<*>
                || typeAdapter is JsonDeserializer<*>
                || typeAdapter is TypeAdapter<*>)
        if (typeAdapter is JsonDeserializer<*> || typeAdapter is JsonSerializer<*>) {
            hierarchyFactories.add(TreeTypeAdapter.newTypeHierarchyFactory(baseType, typeAdapter))
        }
        if (typeAdapter is TypeAdapter<*>) {
            @Suppress("UNCHECKED_CAST")
            factories.add(TypeAdaptersJava.newTypeHierarchyFactory(baseType as Class<Any?>, typeAdapter as TypeAdapter<Any?>))
        }
        return this
    }

    /**
     * Section 2.4 of [JSON specification](http://www.ietf.org/rfc/rfc4627.txt) disallows
     * special double values (NaN, Infinity, -Infinity). However,
     * [Javascript
 * specification](http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-262.pdf) (see section 4.3.20, 4.3.22, 4.3.23) allows these values as valid Javascript
     * values. Moreover, most JavaScript engines will accept these special values in JSON without
     * problem. So, at a practical level, it makes sense to accept these values as valid JSON even
     * though JSON specification disallows them.
     *
     *
     * Gson always accepts these special values during deserialization. However, it outputs
     * strictly compliant JSON. Hence, if it encounters a float value [Float.NaN],
     * [Float.POSITIVE_INFINITY], [Float.NEGATIVE_INFINITY], or a double value
     * [Double.NaN], [Double.POSITIVE_INFINITY], [Double.NEGATIVE_INFINITY], it
     * will throw an [IllegalArgumentException]. This method provides a way to override the
     * default behavior when you know that the JSON receiver will be able to handle these special
     * values.
     *
     * @return a reference to this `GsonBuilder` object to fulfill the "Builder" pattern
     * @since 1.3
     */
    fun serializeSpecialFloatingPointValues(): GsonBuilder {
        serializeSpecialFloatingPointValues = true
        return this
    }

    /**
     * Creates a [Gson] instance based on the current configuration. This method is free of
     * side-effects to this `GsonBuilder` instance and hence can be called multiple times.
     *
     * @return an instance of Gson configured with the options currently set in this builder
     */
    fun create(): Gson {
        val factories: MutableList<TypeAdapterFactory?> = ArrayList(factories.size + hierarchyFactories.size + 3)
        factories.addAll(this.factories)
        Collections.reverse(factories)
        val hierarchyFactories: List<TypeAdapterFactory?> = ArrayList(hierarchyFactories)
        Collections.reverse(hierarchyFactories)
        factories.addAll(hierarchyFactories)
        addTypeAdaptersForDate(datePattern, dateStyle, timeStyle, factories)
        return Gson(excluder, fieldNamingPolicy, instanceCreators,
                serializeNulls, complexMapKeySerialization,
                generateNonExecutableJson, escapeHtmlChars, prettyPrinting, lenient,
                serializeSpecialFloatingPointValues, longSerializationPolicy,
                datePattern, dateStyle, timeStyle,
                this.factories, this.hierarchyFactories, factories, serializedAdapter)
    }

    @Suppress("UNCHECKED_CAST")
    private fun addTypeAdaptersForDate(datePattern: String?, dateStyle: Int, timeStyle: Int,
                                       factories: MutableList<TypeAdapterFactory?>) {
        val dateTypeAdapter: DefaultDateTypeAdapter
        val timestampTypeAdapter: TypeAdapter<Timestamp>
        val javaSqlDateTypeAdapter: TypeAdapter<Date>
        if (datePattern != null && "" != datePattern.trim { it <= ' ' }) {
            dateTypeAdapter = DefaultDateTypeAdapter(java.util.Date::class.java, datePattern)
            timestampTypeAdapter = DefaultDateTypeAdapter(Timestamp::class.java, datePattern) as TypeAdapter<Timestamp>
            javaSqlDateTypeAdapter = DefaultDateTypeAdapter(Date::class.java, datePattern) as TypeAdapter<Date>
        } else if (dateStyle != DateFormat.DEFAULT && timeStyle != DateFormat.DEFAULT) {
            dateTypeAdapter = DefaultDateTypeAdapter(java.util.Date::class.java, dateStyle, timeStyle)
            timestampTypeAdapter = DefaultDateTypeAdapter(Timestamp::class.java, dateStyle, timeStyle) as TypeAdapter<Timestamp>
            javaSqlDateTypeAdapter = DefaultDateTypeAdapter(Date::class.java, dateStyle, timeStyle) as TypeAdapter<Date>
        } else {
            return
        }
        factories.add(TypeAdapters.newFactory(java.util.Date::class.java, dateTypeAdapter))
        factories.add(TypeAdapters.newFactory(Timestamp::class.java, timestampTypeAdapter))
        factories.add(TypeAdapters.newFactory(Date::class.java, javaSqlDateTypeAdapter))
    }
}