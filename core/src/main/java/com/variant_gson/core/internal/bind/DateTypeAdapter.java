package com.variant_gson.core.internal.bind;

import com.variant_gson.core.Gson;
import com.variant_gson.core.JsonSyntaxException;
import com.variant_gson.core.TypeAdapter;
import com.variant_gson.core.TypeAdapterFactory;
import com.variant_gson.core.internal.JavaVersion;
import com.variant_gson.core.internal.PreJava9DateFormatProvider;
import com.variant_gson.core.internal.bind.util.ISO8601Utils;
import com.variant_gson.core.reflect.TypeToken;
import com.variant_gson.core.stream.JsonReader;
import com.variant_gson.core.stream.JsonToken;
import com.variant_gson.core.stream.JsonWriter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for Date. Although this class appears stateless, it is not.
 * DateFormat captures its time zone and locale when it is created, which gives
 * this class state. DateFormat isn't thread safe either, so this class has
 * to synchronize its read and write methods.
 */
public final class DateTypeAdapter extends TypeAdapter<Date> {
    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @SuppressWarnings("unchecked") // we use a runtime check to make sure the 'T's equal
        @Override public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            return typeToken.getRawType() == Date.class ? (TypeAdapter<T>) new DateTypeAdapter() : null;
        }
    };

    /**
     * List of 1 or more different date formats used for de-serialization attempts.
     * The first of them (default US format) is used for serialization as well.
     */
    private final List<DateFormat> dateFormats = new ArrayList<DateFormat>();

    public DateTypeAdapter() {
        dateFormats.add(DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US));
        if (!Locale.getDefault().equals(Locale.US)) {
            dateFormats.add(DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT));
        }
        if (JavaVersion.isJava9OrLater()) {
            dateFormats.add(PreJava9DateFormatProvider.getUSDateTimeFormat(DateFormat.DEFAULT, DateFormat.DEFAULT));
        }
    }

    @Override public Date read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        return deserializeToDate(in.nextString());
    }

    private synchronized Date deserializeToDate(String json) {
        for (DateFormat dateFormat : dateFormats) {
            try {
                return dateFormat.parse(json);
            } catch (ParseException ignored) {}
        }
        try {
            return ISO8601Utils.parse(json, new ParsePosition(0));
        } catch (ParseException e) {
            throw new JsonSyntaxException(json, e);
        }
    }

    @Override public synchronized void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        String dateFormatAsString = dateFormats.get(0).format(value);
        out.value(dateFormatAsString);
    }
}

