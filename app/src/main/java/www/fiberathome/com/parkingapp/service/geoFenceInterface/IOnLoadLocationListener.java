package www.fiberathome.com.parkingapp.service.geoFenceInterface;

import java.util.List;

@SuppressWarnings({"unused", "RedundantSuppression"})
public interface IOnLoadLocationListener {

    void onLoadLocationSuccess(List<MyLatLng> latLngs);

    void onLoadLocationFailed(String message);
}
