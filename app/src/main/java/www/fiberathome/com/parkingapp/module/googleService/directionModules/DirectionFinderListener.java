package www.fiberathome.com.parkingapp.module.googleService.directionModules;

import java.util.List;

public interface DirectionFinderListener {
    void onDirectionFinderStart();

    void onDirectionFinderSuccess(List<Route> route);
}
