package com.variant_gson.core.internal;

import com.variant_gson.core.Gson;
import com.variant_gson.core.SerializedAdapter;
import com.variant_gson.core.TypeAdapter;
import com.variant_gson.core.TypeAdapterFactory;
import com.variant_gson.core.internal.bind.TypeAdapters;
import com.variant_gson.core.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

/**
 * Created by ChaoShen on 2020/12/9
 */
public class EnumFactoryCreator {

    private EnumFactoryCreator() {

    }


    public static TypeAdapterFactory CREATOR(final SerializedAdapter adapter) {
        return new TypeAdapterFactory() {
            @SuppressWarnings({"rawtypes", "unchecked"})
            @Override
            public <T>TypeAdapter<T> create(@NotNull Gson gson, @NotNull TypeToken<T> typeToken) {
                Class<? super T> rawType = typeToken.getRawType();
                if (!Enum.class.isAssignableFrom(rawType) || rawType == Enum.class) {
                    return null;
                }
                if (!rawType.isEnum()) {
                    rawType = rawType.getSuperclass(); // handle anonymous subclasses
                }
                return (TypeAdapter<T>) new TypeAdapters.EnumTypeAdapter(rawType, adapter);
            }
        };
    }
}
