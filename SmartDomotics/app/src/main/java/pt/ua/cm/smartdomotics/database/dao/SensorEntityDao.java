package pt.ua.cm.smartdomotics.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;
import pt.ua.cm.smartdomotics.database.entities.SensorEntity;

/**
 * Created by alvaro on 18-10-2017.
 */
@Dao
public interface SensorEntityDao {
    @Query("SELECT * FROM sentity")
    List<SensorEntity> getAll();

    @Query("SELECT * FROM sentity WHERE name LIKE :name AND did = :division_id LIMIT 1")
    SensorEntity findByName(String name, int division_id);

    @Insert
    void insertAll(SensorEntity... sensorEntities);

    @Delete
    void delete(SensorEntity sensorEntity);

    @Update
    public void updateSensorEntities(SensorEntity... sensorEntities);

    @Update
    public void updateSensorEntity(SensorEntity sensorEntity);
}