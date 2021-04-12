package www.fiberathome.com.parkingapp.module.googleService.directionModules;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Route {

    Distance distance;
    Duration duration;
    public LatLng endLocation;
    LatLng startLocation;

    public List<LatLng> points;
}
