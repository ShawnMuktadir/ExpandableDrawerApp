package www.fiberathome.com.parkingapp.preference.utils;

import java.io.IOException;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.ParkingApp;

public class NoConnectivityException extends IOException {
    @Override
    public String getMessage() {
        return ParkingApp.getInstance().getString(R.string.connect_to_internet);
    }
}

