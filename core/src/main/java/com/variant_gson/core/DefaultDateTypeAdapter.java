package com.variant_gson.core;

import com.variant_gson.core.internal.JavaVersion;
import com.variant_gson.core.internal.PreJava9DateFormatProvider;
import com.variant_gson.core.internal.bind.util.ISO8601Utils;
import com.variant_gson.core.stream.JsonReader;
import com.variant_gson.core.stream.JsonToken;
import com.variant_gson.core.stream.JsonWriter;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This type adapter supports three subclasses of date: Date, Timestamp, and
 * java.sql.Date.
 */
final class DefaultDateTypeAdapter extends TypeAdapter<Date> {

    private static final String SIMPLE_NAME = "DefaultDateTypeAdapter";

    private final Class<? extends Date> dateType;

    /**
     * List of 1 or more different date formats used for de-serialization attempts.
     * The first of them is used for serialization as well.
     */
    private final List<DateFormat> dateFormats = new ArrayList<DateFormat>();

    DefaultDateTypeAdapter(Class<? extends Date> dateType) {
        this.dateType = verifyDateType(dateType);
        dateFormats.add(DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US));
        if (!Locale.getDefault().equals(Locale.US)) {
            dateFormats.add(DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT));
        }
        if (JavaVersion.isJava9OrLater()) {
            dateFormats.add(PreJava9DateFormatProvider.getUSDateTimeFormat(DateFormat.DEFAULT, DateFormat.DEFAULT));
        }
    }

    DefaultDateTypeAdapter(Class<? extends Date> dateType, String datePattern) {
        this.dateType = verifyDateType(dateType);
        dateFormats.add(new SimpleDateFormat(datePattern, Locale.US));
        if (!Locale.getDefault().equals(Locale.US)) {
            dateFormats.add(new SimpleDateFormat(datePattern));
        }
    }

    DefaultDateTypeAdapter(Class<? extends Date> dateType, int style) {
        this.dateType = verifyDateType(dateType);
        dateFormats.add(DateFormat.getDateInstance(style, Locale.US));
        if (!Locale.getDefault().equals(Locale.US)) {
            dateFormats.add(DateFormat.getDateInstance(style));
        }
        if (JavaVersion.isJava9OrLater()) {
            dateFormats.add(PreJava9DateFormatProvider.getUSDateFormat(style));
        }
    }

    public DefaultDateTypeAdapter(int dateStyle, int timeStyle) {
        this(Date.class, dateStyle, timeStyle);
    }

    public DefaultDateTypeAdapter(Class<? extends Date> dateType, int dateStyle, int timeStyle) {
        this.dateType = verifyDateType(dateType);
        dateFormats.add(DateFormat.getDateTimeInstance(dateStyle, timeStyle, Locale.US));
        if (!Locale.getDefault().equals(Locale.US)) {
            dateFormats.add(DateFormat.getDateTimeInstance(dateStyle, timeStyle));
        }
        if (JavaVersion.isJava9OrLater()) {
            dateFormats.add(PreJava9DateFormatProvider.getUSDateTimeFormat(dateStyle, timeStyle));
        }
    }

    private static Class<? extends Date> verifyDateType(Class<? extends Date> dateType) {
        if ( dateType != Date.class && dateType != java.sql.Date.class && dateType != Timestamp.class ) {
            throw new IllegalArgumentException("Date type must be one of " + Date.class + ", " + Timestamp.class + ", or " + java.sql.Date.class + " but was " + dateType);
        }
        return dateType;
    }

    // These methods need to be synchronized since JDK DateFormat classes are not thread-safe
    // See issue 162
    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        synchronized(dateFormats) {
            String dateFormatAsString = dateFormats.get(0).format(value);
            out.value(dateFormatAsString);
        }
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        Date date = deserializeToDate(in.nextString());
        if (dateType == Date.class) {
            return date;
        } else if (dateType == Timestamp.class) {
            return new Timestamp(date.getTime());
        } else if (dateType == java.sql.Date.class) {
            return new java.sql.Date(date.getTime());
        } else {
            // This must never happen: dateType is guarded in the primary constructor
            throw new AssertionError();
        }
    }

    private Date deserializeToDate(String s) {
        synchronized (dateFormats) {
            for (DateFormat dateFormat : dateFormats) {
                try {
                    return dateFormat.parse(s);
                } catch (ParseException ignored) {}
            }
            try {
                return ISO8601Utils.parse(s, new ParsePosition(0));
            } catch (ParseException e) {
                throw new JsonSyntaxException(s, e);
            }
        }
    }

    @Override
    public String toString() {
        DateFormat defaultFormat = dateFormats.get(0);
        if (defaultFormat instanceof SimpleDateFormat) {
            return SIMPLE_NAME + '(' + ((SimpleDateFormat) defaultFormat).toPattern() + ')';
        } else {
            return SIMPLE_NAME + '(' + defaultFormat.getClass().getSimpleName() + ')';
        }
    }
}
