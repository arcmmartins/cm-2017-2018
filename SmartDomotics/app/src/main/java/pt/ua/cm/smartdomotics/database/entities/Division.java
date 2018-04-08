package pt.ua.cm.smartdomotics.database.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by alvaro on 18-10-2017.
 */

@Entity
public class Division {



    @PrimaryKey(autoGenerate = true)
    private int uid;

    private String name;

    @Override
    public String toString() {
        return "Division{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                '}';
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}