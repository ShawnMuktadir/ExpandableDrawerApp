package www.fiberathome.com.parkingapp.module.geoFenceInterface;

import java.util.List;

@SuppressWarnings({"unused", "RedundantSuppression"})
public interface IOnLoadLocationListener {

    void onLoadLocationSuccess(List<MyLatLng> latLngs);

    void onLoadLocationFailed(String message);
}
