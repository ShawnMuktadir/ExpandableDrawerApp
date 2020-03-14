package www.fiberathome.com.parkingapp.GoogleMapWebService;

import www.fiberathome.com.parkingapp.model.SensorList;

public interface GooglePlaceSearchNearbySearchListener {

    void onGooglePlaceSearchStart();
    void onGooglePlaceSearchSuccess(SensorList sensorList);
}
