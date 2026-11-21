package com.allinone.app.data.db

import androidx.room.TypeConverter

/**
 * Room type converters. Entities in this app use primitives and String, which
 * Room handles natively — no explicit converters are required. This class is
 * kept as a placeholder for future types (e.g. dates as ISO strings, lists as
 * JSON) and is wired via @TypeConverters in AllInOneDatabase.
 */
class Converters {

    // Identity conversions for primitives are handled natively by Room and
    // must NOT be declared here (declaring them conflicts with Room's own
    // built-in converters and fails the KSP build).

    @TypeConverter
    fun fromStringList(value: String?): List<String>? =
        value?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }

    @TypeConverter
    fun toStringList(value: List<String>?): String? =
        value?.joinToString(",")
}
