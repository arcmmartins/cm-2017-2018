package pt.ua.cm.smartdomotics.database.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import java.util.List;
import pt.ua.cm.smartdomotics.database.entities.SensorEntity;
import pt.ua.cm.smartdomotics.database.repositories.DataRepo;

/**
 * Created by alvaro on 19-10-2017.
 */

public class SEntitiesViewModel extends AndroidViewModel {
    private MutableLiveData<List<SensorEntity>> entities;
    private DataRepo repo = DataRepo.getInstance();

    public SEntitiesViewModel(Application application) {
        super(application);
    }
    public MutableLiveData<List<SensorEntity>> getEntities() {
        if (entities == null) {
            entities = new MutableLiveData<>();
            loadEntities();
        }
        return entities;
    }


    private void loadEntities(){
        entities = repo.getSE();
    }
}