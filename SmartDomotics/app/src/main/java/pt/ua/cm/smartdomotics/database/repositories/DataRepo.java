package pt.ua.cm.smartdomotics.database.repositories;

import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import pt.ua.cm.smartdomotics.database.SmartDomoticsDatabase;
import pt.ua.cm.smartdomotics.database.entities.BooleanEntity;
import pt.ua.cm.smartdomotics.database.entities.Division;
import pt.ua.cm.smartdomotics.database.entities.Log;
import pt.ua.cm.smartdomotics.database.entities.SensorEntity;
/**
 * Created by alvaro on 22-10-2017.
 */

public class DataRepo {
    private static final DataRepo ourInstance = new DataRepo();
    private SmartDomoticsDatabase db;
    MutableLiveData<List<SensorEntity>> dataSE = new MutableLiveData<>();
    MutableLiveData<List<BooleanEntity>> dataBE = new MutableLiveData<>();
    MutableLiveData<List<Division>> dataD = new MutableLiveData<>();
    MutableLiveData<List<Log>> dataL = new MutableLiveData<>();
    public static DataRepo getInstance() {
        return ourInstance;
    }

    private DataRepo() {
    }

    public void init(SmartDomoticsDatabase db) {
        this.db = db;
        dataSE.setValue(db.seModel().getAll());
        dataBE.setValue(db.beModel().getAll());
        dataD.setValue(db.dModel().getAll());
        dataL.setValue(db.lModel().getAll());
    }

    public MutableLiveData<List<BooleanEntity>> getBE(){
        dataBE.postValue(db.beModel().getAll());
        return dataBE;
    }

    public BooleanEntity findBE(String name, int did){
        return db.beModel().findByName(name, did);
    }

    public void updateBE(BooleanEntity be){
        db.beModel().updateBooleanEntities(be);
        dataBE.postValue(db.beModel().getAll());
    }

    public void insertBE(BooleanEntity be){
        db.beModel().insertAll(be);
        dataBE.postValue(db.beModel().getAll());
    }


    public MutableLiveData<List<SensorEntity>> getSE(){
        dataSE.postValue(db.seModel().getAll());
        return dataSE;
    }

    public SensorEntity findSE(String name, int did){
        return db.seModel().findByName(name, did);
    }

    public void updateSE(SensorEntity se){
        db.seModel().updateSensorEntities(se);
        dataSE.postValue(db.seModel().getAll());
    }

    public void insertSE(SensorEntity se){
        db.seModel().insertAll(se);
        dataSE.postValue(db.seModel().getAll());
    }

    public MutableLiveData<List<Log>> getL(){
        dataL.postValue(db.lModel().getAll());
        return dataL;
    }

    public void insertL(Log l){
        db.lModel().inserAll(l);
        dataL.postValue(db.lModel().getAll());
    }

    public MutableLiveData<List<Division>> getD(){
        dataD.postValue(db.dModel().getAll());
        return dataD;
    }

    public Division findDivision(String name){
        return db.dModel().findByName(name);
    }

    public void insertD(Division d){
        db.dModel().insertAll(d);
        dataD.postValue(db.dModel().getAll());
    }
}