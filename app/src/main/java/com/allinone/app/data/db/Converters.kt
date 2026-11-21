package com.allinone.app.data.db

import androidx.room.TypeConverter

/**
 * Room type converters. Entities in this app use primitives and String so most
 * conversions are identity mappings; kept for future-proofing (e.g. dates as
 * ISO strings, lists as JSON).
 */
class Converters {

    @TypeConverter
    fun fromLong(value: Long?): Long? = value

    @TypeConverter
    fun toLong(value: Long?): Long? = value
}
