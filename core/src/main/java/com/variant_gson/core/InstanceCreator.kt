package com.variant_gson.core

import java.lang.reflect.Type

/**
 * This interface is implemented to create instances of a class that does not define a no-args
 * constructor. If you can modify the class, you should instead add a private, or public
 * no-args constructor. However, that is not possible for library classes, such as JDK classes, or
 * a third-party library that you do not have source-code of. In such cases, you should define an
 * instance creator for the class. Implementations of this interface should be registered with
 * [GsonBuilder.registerTypeAdapter(Type, Object)] method before Gson will be able to use
 * them.
 *
 *
 * Let us look at an example where defining an InstanceCreator might be useful. The
 * [id] class defined below does not have a default no-args constructor.
 *
 * ``` Java
 * public class Id&lt;T&gt; {
 *   private final Class&lt;T&gt; clazz;
 *   private final long value;
 *   public Id(Class&lt;T&gt; clazz, long value) {
 *     this.clazz = clazz;
 *     this.value = value;
 *   }
 * }
 * ```
 *
 *
 * If Gson encounters an object of type {@code Id} during deserialization, it will throw an
 * exception. The easiest way to solve this problem will be to add a (public or private) no-args
 * constructor as follows:
 *
 * ``` Java
 * private Id() {
 *   this(Object.class, 0L);
 * }
 * ```
 *
 *
 * However, let us assume that the developer does not have access to the source-code of the
 * [id] class, or does not want to define a no-args constructor for it. The developer
 * can solve this problem by defining an [InstanceCreator] for [id]:
 *
 * ``` Java
 * class IdInstanceCreator implements InstanceCreator&lt;Id&gt; {
 *   public Id createInstance(Type type) {
 *     return new Id(Object.class, 0L);
 *   }
 * }
 * ```
 *
 *
 * Note that it does not matter what the fields of the created instance contain since Gson will
 * overwrite them with the deserialized values specified in Json. You should also ensure that a
 * ***new*** object is returned, not a common object since its fields will be overwritten.
 * The developer will need to register {@code IdInstanceCreator} with Gson as follows:</p>
 *
 * ``` Java
 * Gson gson = new GsonBuilder().registerTypeAdapter(Id.class, new IdInstanceCreator()).create();
 * ```
 * @param T the type of object that will be created by this implementation.
 *
 */
interface InstanceCreator<T> {
    /**
     * Gson invokes this call-back method during deserialization to create an instance of the
     * specified type. The fields of the returned instance are overwritten with the data present
     * in the Json. Since the prior contents of the object are destroyed and overwritten, do not
     * return an instance that is useful elsewhere. In particular, do not return a common instance,
     * always use `new` to create a new instance.
     *
     * @param type the parameterized T represented as a [Type].
     * @return a default object instance of type T.
     */
    fun createInstance(type: Type?): T
}
