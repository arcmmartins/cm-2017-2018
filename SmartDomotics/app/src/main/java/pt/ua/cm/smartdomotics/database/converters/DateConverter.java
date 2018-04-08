package pt.ua.cm.smartdomotics.database.converters;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by alvaro on 18-10-2017.
 */

public class DateConverter {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}