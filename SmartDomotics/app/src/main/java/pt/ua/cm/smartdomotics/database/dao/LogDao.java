package pt.ua.cm.smartdomotics.database.dao;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverter;

import java.util.Date;
import java.util.List;

import pt.ua.cm.smartdomotics.database.entities.Division;
import pt.ua.cm.smartdomotics.database.entities.Log;

/**
 * Created by alvaro on 18-10-2017.
 */

@Dao
public interface LogDao {

    @Query("SELECT * FROM log")
    List<Log> getAll();

    @Insert
    void inserAll(Log... logs);
}
