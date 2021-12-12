package www.fiberathome.com.parkingapp.utils;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by Manu on 11/24/2017.
 */

public class GeofenceConstants {

    //Location
    public static final String GEOFENCE_ID = "parking";
    public static final float GEOFENCE_RADIUS_IN_METERS = 1000;

    /**
     * Map for storing information about tacme in the dubai.
     */
    public static HashMap<String, LatLng> AREA_LANDMARKS = new HashMap<String, LatLng>();
    public static long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 12 * 60 * 60 * 1000;

    static {
        AREA_LANDMARKS.put(GEOFENCE_ID, new LatLng(25.116354, 55.390398));
    }
}