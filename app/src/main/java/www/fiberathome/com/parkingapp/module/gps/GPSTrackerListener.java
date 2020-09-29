package www.fiberathome.com.parkingapp.module.gps;

import android.location.Location;
import android.os.Bundle;

public interface GPSTrackerListener {
    void onGPSTrackerLocationChanged(Location location);

    void onGPSTrackerStatusChanged(String provider, int status, Bundle extras);
}
