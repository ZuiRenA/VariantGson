package com.variant_gson.core.internal.bind;

import com.variant_gson.core.Gson;
import com.variant_gson.core.JsonSyntaxException;
import com.variant_gson.core.TypeAdapter;
import com.variant_gson.core.TypeAdapterFactory;
import com.variant_gson.core.reflect.TypeToken;
import com.variant_gson.core.stream.JsonReader;
import com.variant_gson.core.stream.JsonWriter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Created by ChaoShen on 2020/12/9
 */
public class TypeAdaptersJava {
    private TypeAdaptersJava() {

    }

    public static TypeAdapterFactory newFactory(
            final TypeToken<?> type, final TypeAdapter<?> typeAdapter) {
        return new TypeAdapterFactory() {
            @SuppressWarnings("unchecked") // we use a runtime check to make sure the 'T's equal
            @Override public <T> TypeAdapter<T> create(
                    @NotNull Gson gson, @NotNull TypeToken<T> typeToken) {
                return typeToken.equals(type) ? (TypeAdapter<T>) typeAdapter : null;
            }
        };
    }

    /**
     * Returns a factory for all subtypes of {@code typeAdapter}. We do a runtime check to confirm
     * that the deserialized type matches the type requested.
     */
    public static <T1> TypeAdapterFactory newTypeHierarchyFactory(
            final Class<T1> clazz, final TypeAdapter<T1> typeAdapter) {
        return new TypeAdapterFactory() {
            @SuppressWarnings("unchecked")
            @Override public <T2> TypeAdapter<T2> create(Gson gson, TypeToken<T2> typeToken) {
                final Class<? super T2> requestedType = typeToken.getRawType();
                if (!clazz.isAssignableFrom(requestedType)) {
                    return null;
                }
                return (TypeAdapter<T2>) new TypeAdapter<T1>() {
                    @Override public void write(JsonWriter out, T1 value) throws IOException {
                        typeAdapter.write(out, value);
                    }

                    @Override public T1 read(JsonReader in) throws IOException {
                        T1 result = typeAdapter.read(in);
                        if (result != null && !requestedType.isInstance(result)) {
                            throw new JsonSyntaxException("Expected a " + requestedType.getName()
                                    + " but was " + result.getClass().getName());
                        }
                        return result;
                    }
                };
            }
            @Override public String toString() {
                return "Factory[typeHierarchy=" + clazz.getName() + ",adapter=" + typeAdapter + "]";
            }
        };
    }
}
