package pt.ua.cm.smartdomotics.database.dao;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import pt.ua.cm.smartdomotics.database.entities.BooleanEntity;

/**
 * Created by alvaro on 18-10-2017.
 */

@Dao
public interface BooleanEntityDao {
    @Query("SELECT * FROM bentity")
    List<BooleanEntity> getAll();

    @Query("SELECT * FROM bentity WHERE type = 0")
    List<BooleanEntity> getAllLights();

    @Query("SELECT * FROM bentity WHERE type = 1")
    List<BooleanEntity> getAllAlarms();

    @Query("SELECT * FROM bentity WHERE type = 2")
    List<BooleanEntity> getAllGates();

    @Query("SELECT * FROM bentity WHERE id IN (:beIds)")
    List<BooleanEntity> loadAllByIds(int[] beIds);

    @Query("SELECT * FROM bentity WHERE name LIKE :name AND did = :division_id LIMIT 1")
    BooleanEntity findByName(String name, int division_id);

    @Query("SELECT * FROM bentity WHERE did = :division_id")
    List<BooleanEntity> findByDivisionId(int division_id);

    @Insert
    void insertAll(BooleanEntity... booleanEntities);

    @Delete
    void delete(BooleanEntity booleanEntity);

    @Update
    public void updateBooleanEntities(BooleanEntity... booleanEntities);

    @Update
    public void updateBooleanEntity(BooleanEntity booleanEntity);
}
