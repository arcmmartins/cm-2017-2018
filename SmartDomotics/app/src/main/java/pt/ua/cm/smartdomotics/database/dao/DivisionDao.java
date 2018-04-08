package pt.ua.cm.smartdomotics.database.dao;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import pt.ua.cm.smartdomotics.database.entities.Division;

/**
 * Created by alvaro on 18-10-2017.
 */
@Dao
public interface DivisionDao {
    @Query("SELECT * FROM division")
    List<Division> getAll();

    @Query("SELECT * FROM division WHERE uid IN (:divisionIds)")
    List<Division> loadAllByIds(int[] divisionIds);

    @Query("SELECT * FROM division WHERE name LIKE :name LIMIT 1")
    Division findByName(String name);

    @Query("SELECT * FROM division WHERE uid LIKE :uid")
    Division findById(int uid);

    @Insert
    void insertAll(Division... divisions);

    @Delete
    void delete(Division division);

    @Update
    public void updateDivisions(Division... divisions);

}
