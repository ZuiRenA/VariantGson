package com.variant_gson.core

/**
 * A strategy (or policy) definition that is used to decide whether or not a field or top-level
 * class should be serialized or deserialized as part of the JSON output/input. For serialization,
 * if the [shouldSkipClass] method returns true then that class or field type
 * will not be part of the JSON output. For deserialization, if {@link #shouldSkipClass(Class)}
 * returns true, then it will not be set as part of the Java object structure.
 *
 *
 * The following are a few examples that shows how you can use this exclusion mechanism.
 *
 *
 * *Exclude fields and objects based on a particular class type:*
 * ``` Java
 * private static class SpecificClassExclusionStrategy implements ExclusionStrategy {
 *   private final Class&lt;?&gt; excludedThisClass;
 *
 *   public SpecificClassExclusionStrategy(Class&lt;?&gt; excludedThisClass) {
 *     this.excludedThisClass = excludedThisClass;
 *   }
 *
 *   public boolean shouldSkipClass(Class&lt;?&gt; clazz) {
 *     return excludedThisClass.equals(clazz);
 *   }
 *
 *   public boolean shouldSkipField(FieldAttributes f) {
 *     return excludedThisClass.equals(f.getDeclaredClass());
 *   }
 * }
 * ```
 *
 *
 * *Excludes fields and objects based on a particular annotation:*
 * ``` Java
 * public &#64interface FooAnnotation {
 *   // some implementation here
 * }
 *
 * // Excludes any field (or class) that is tagged with an "&#64FooAnnotation"
 * private static class FooAnnotationExclusionStrategy implements ExclusionStrategy {
 *   public boolean shouldSkipClass(Class&lt;?&gt; clazz) {
 *     return clazz.getAnnotation(FooAnnotation.class) != null;
 *   }
 *
 *   public boolean shouldSkipField(FieldAttributes f) {
 *     return f.getAnnotation(FooAnnotation.class) != null;
 *   }
 * }
 * ```
 *
 * <p>Now if you want to configure [Gson] to use a user defined exclusion strategy, then
 * the [GsonBuilder] is required. The following is an example of how you can use the
 * [GsonBuilder] to configure Gson to use one of the above sample:
 *
 * ``` Java
 * ExclusionStrategy excludeStrings = new UserDefinedExclusionStrategy(String.class);
 * Gson gson = new GsonBuilder()
 *     .setExclusionStrategies(excludeStrings)
 *     .create();
 * ```
 *
 *
 * For certain model classes, you may only want to serialize a field, but exclude it for
 * deserialization. To do that, you can write an [ExclusionStrategy] as per normal;
 * however, you would register it with the
 * [GsonBuilder.addDeserializationExclusionStrategy(ExclusionStrategy)] method.
 * For example:
 * ``` java
 * ExclusionStrategy excludeStrings = new UserDefinedExclusionStrategy(String.class);
 * Gson gson = new GsonBuilder()
 *     .addDeserializationExclusionStrategy(excludeStrings)
 *     .create();
 * ```
 * @see GsonBuilder#setExclusionStrategies(ExclusionStrategy...)
 * @see GsonBuilder#addDeserializationExclusionStrategy(ExclusionStrategy)
 * @see GsonBuilder#addSerializationExclusionStrategy(ExclusionStrategy)
 *
 * @since 1.4
 */
interface ExclusionStrategy {
    /**
     * @param f the field object that is under test
     * @return true if the field should be ignored; otherwise false
     */
    fun shouldSkipField(f: FieldAttributes?): Boolean

    /**
     * @param clazz the class object that is under test
     * @return true if the class should be ignored; otherwise false
     */
    fun shouldSkipClass(clazz: Class<*>?): Boolean
}