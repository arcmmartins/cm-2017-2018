package pt.ua.cm.smartdomotics.database;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import pt.ua.cm.smartdomotics.database.converters.DateConverter;
import pt.ua.cm.smartdomotics.database.dao.BooleanEntityDao;
import pt.ua.cm.smartdomotics.database.dao.DivisionDao;
import pt.ua.cm.smartdomotics.database.dao.LogDao;
import pt.ua.cm.smartdomotics.database.dao.SensorEntityDao;
import pt.ua.cm.smartdomotics.database.entities.BooleanEntity;
import pt.ua.cm.smartdomotics.database.entities.Division;
import pt.ua.cm.smartdomotics.database.entities.Log;
import pt.ua.cm.smartdomotics.database.entities.SensorEntity;

/**
 * Created by alvaro on 18-10-2017.
 */
@Database(entities = {Division.class,BooleanEntity.class, SensorEntity.class, Log.class}, version = 3)
@TypeConverters({DateConverter.class})
public abstract class SmartDomoticsDatabase extends RoomDatabase {
    public abstract DivisionDao dModel();
    public abstract LogDao lModel();
    public abstract BooleanEntityDao beModel();
    public abstract SensorEntityDao seModel();
    private static SmartDomoticsDatabase INSTANCE;
    public static SmartDomoticsDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), SmartDomoticsDatabase.class, "SmartDomotics.db").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        }
        return INSTANCE;
    }
}