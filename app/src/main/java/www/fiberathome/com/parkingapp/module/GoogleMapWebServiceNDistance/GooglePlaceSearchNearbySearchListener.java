package www.fiberathome.com.parkingapp.module.GoogleMapWebServiceNDistance;

import www.fiberathome.com.parkingapp.model.response.sensors.SensorList;

public interface GooglePlaceSearchNearbySearchListener {

    void onGooglePlaceSearchStart();
    void onGooglePlaceSearchSuccess(SensorList sensorList);
}
