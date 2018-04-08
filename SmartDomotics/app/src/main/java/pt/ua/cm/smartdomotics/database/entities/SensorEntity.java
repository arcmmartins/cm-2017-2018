package pt.ua.cm.smartdomotics.database.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by alvaro on 18-10-2017.
 */
@Entity(tableName = "sentity", foreignKeys = {
        @ForeignKey(
                entity = Division.class,
                parentColumns = "uid",
                childColumns = "did",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )

}, indices = {@Index(value = {"name", "did"}, unique = true)})
public class SensorEntity {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDid() {
        return did;
    }

    @Override
    public String toString() {
        return "SensorEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", value=" + value +
                ", did=" + did +
                '}';
    }

    public void setDid(int did) {
        this.did = did;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @PrimaryKey(autoGenerate=true)
    public int id;
    String name;
    int value;
    int did;
}