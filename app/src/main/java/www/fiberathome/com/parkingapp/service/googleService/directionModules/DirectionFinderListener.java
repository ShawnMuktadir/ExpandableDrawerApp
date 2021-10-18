package www.fiberathome.com.parkingapp.service.googleService.directionModules;

import java.util.List;

public interface DirectionFinderListener {
    void onDirectionFinderStart();

    void onDirectionFinderSuccess(List<Route> route);
}
