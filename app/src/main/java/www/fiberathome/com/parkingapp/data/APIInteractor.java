package www.fiberathome.com.parkingapp.data;

import www.fiberathome.com.parkingapp.model.response.ParkingResponse;

public interface APIInteractor {

    /*----------------------------------------------------------Parking-------------------------------------------------------------*/

    void getParkingData(ParkingLoadedListener parkingLoadedListener);

    interface ParkingLoadedListener extends FailedListener {

        void onParkingLoaded(ParkingResponse parkingResponse);

    }

    /*----------------------------------------------------------Common-------------------------------------------------------------*/

    interface FailedListener {

        void onFailed(String message, String failedAPI);
    }


}
