package www.fiberathome.com.parkingapp.module.GoogleMapWebServiceNDistance.directionModules;

import java.util.List;

public interface DirectionFinderListener {
    void onDirectionFinderStart();

    void onDirectionFinderSuccess(List<Route> route);
}
