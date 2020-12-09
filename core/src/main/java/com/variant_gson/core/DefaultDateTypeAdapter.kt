package com.variant_gson.core

import com.variant_gson.core.internal.JavaVersion
import com.variant_gson.core.internal.PreJava9DateFormatProvider
import com.variant_gson.core.stream.JsonReader
import com.variant_gson.core.stream.JsonWriter
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * This type adapter supports three subclasses of date: Date, Timestamp, and
 * java.sql.Date.
 */
class DefaultDateTypeAdapter : TypeAdapter<Date> {

    private val dateType: Class<out Date>

    /**
     * List of 1 or more different date formats used for de-serialization attempts.
     * The first of them is used for serialization as well.
     */
    private val dateFormats: MutableList<DateFormat> = ArrayList()

    internal constructor(dateType: Class<out Date>) {
        this.dateType = verifyDateType(dateType)
        dateFormats.add(DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US))
        if (Locale.getDefault() != Locale.US) {
            dateFormats.add(DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT))
        }
        if (JavaVersion.isJava9OrLater()) {
            dateFormats.add(PreJava9DateFormatProvider.getUSDateTimeFormat(DateFormat.DEFAULT, DateFormat.DEFAULT))
        }
    }

    @Suppress("SimpleDateFormat")
    internal constructor(dateType: Class<out Date>, datePattern: String) {
        this.dateType = verifyDateType(dateType)
        dateFormats.add(SimpleDateFormat(datePattern, Locale.US))
        if (Locale.getDefault() != Locale.US) {
            dateFormats.add(SimpleDateFormat(datePattern))
        }
    }

    internal constructor(dateType: Class<out Date>, style: Int) {
        this.dateType = verifyDateType(dateType)
        dateFormats.add(DateFormat.getDateInstance(style, Locale.US))
        if (Locale.getDefault() != Locale.US) {
            dateFormats.add(DateFormat.getDateInstance(style))
        }
        if (JavaVersion.isJava9OrLater()) {
            dateFormats.add(PreJava9DateFormatProvider.getUSDateFormat(style))
        }
    }


    override fun write(out: JsonWriter, value: Date?) {

    }

    override fun read(`in`: JsonReader): Date? {

    }

    companion object {
        private const val SIMPLE_NAME = "DefaultDateTypeAdapter"


        private fun verifyDateType(dateType: Class<out Date>): Class<out Date> {
            require(!(dateType != Date::class.java && dateType != java.sql.Date::class.java && dateType != Timestamp::class.java)) { "Date type must be one of " + Date::class.java + ", " + Timestamp::class.java + ", or " + java.sql.Date::class.java + " but was " + dateType }
            return dateType
        }
    }
}