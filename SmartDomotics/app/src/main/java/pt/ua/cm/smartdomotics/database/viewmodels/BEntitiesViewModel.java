package pt.ua.cm.smartdomotics.database.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import java.util.List;

import pt.ua.cm.smartdomotics.database.entities.BooleanEntity;
import pt.ua.cm.smartdomotics.database.SmartDomoticsDatabase;
import pt.ua.cm.smartdomotics.database.repositories.DataRepo;

/**
 * Created by alvaro on 19-10-2017.
 */

public class BEntitiesViewModel extends AndroidViewModel {
    private MutableLiveData<List<BooleanEntity>> entities;
    private DataRepo repo = DataRepo.getInstance();
    public BEntitiesViewModel(Application application) {
        super(application);
    }
    public LiveData<List<BooleanEntity>> getEntities(int division_id) {
        if (entities == null) {
            entities = new MutableLiveData<List<BooleanEntity>>();
            // Asynchronous call for inserting data using service or asynctask.
            loadEntities();
        }
        return entities;
    }


    private void loadEntities(){
        entities = repo.getBE();
    }
}