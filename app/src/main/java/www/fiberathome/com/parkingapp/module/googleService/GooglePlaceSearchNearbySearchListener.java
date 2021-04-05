package www.fiberathome.com.parkingapp.module.googleService;

import www.fiberathome.com.parkingapp.model.response.sensors.SensorList;

public interface GooglePlaceSearchNearbySearchListener {

    void onGooglePlaceSearchStart();
    void onGooglePlaceSearchSuccess(SensorList sensorList);
}
