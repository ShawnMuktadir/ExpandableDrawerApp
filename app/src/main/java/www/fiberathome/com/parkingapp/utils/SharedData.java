package www.fiberathome.com.parkingapp.utils;

import com.google.gson.Gson;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.model.SensorArea;

/*Created by MiQ0717 on 23-Mar-2020.*/
public final class SharedData {
    private static SharedData instance = new SharedData();

    private SharedData() {
    }

    public static SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }

    public static void setInstance(SharedData instance) {
        SharedData.instance = instance;
    }

    public void setAllClean() {
        instance = null;
    }


    private SensorArea sensorArea;

    public SensorArea getSensorArea() {
        return sensorArea;
    }

    public void setSensorArea(SensorArea sensorArea) {
        Timber.e("Sensor Area in SharedData -> %s", new Gson().toJson(sensorArea));
        this.sensorArea = sensorArea;
    }
}
