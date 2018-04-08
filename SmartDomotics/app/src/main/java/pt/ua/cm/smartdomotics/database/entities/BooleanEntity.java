package pt.ua.cm.smartdomotics.database.entities;

/**
 * Created by alvaro on 21-10-2017.
 */

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by alvaro on 18-10-2017.
 */
@Entity(tableName = "bentity", foreignKeys = {
        @ForeignKey(
                entity = Division.class,
                parentColumns = "uid",
                childColumns = "did",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )

}, indices = {@Index(value = {"name", "did"},
        unique = true)})
public class BooleanEntity {
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getDid() {
        return did;
    }

    public void setDid(int did) {
        this.did = did;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    @PrimaryKey(autoGenerate=true)
    public int id;
    String name;

    @Override
    public String toString() {
        return "BooleanEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", did=" + did +
                ", type=" + type +
                '}';
    }

    boolean status;
    int did;
    int type;

}