package www.fiberathome.com.parkingapp.eventBus;

import com.google.android.gms.maps.model.LatLng;

public class GetSensorInfoEvent {

    public LatLng location;
    public String areaName;
    public String parkingCount;
    public double distance;

    public GetSensorInfoEvent(String areaName, String parkingCount, double distance) {
        this.areaName = areaName;
        this.parkingCount = parkingCount;
        this.distance = distance;
    }

    public GetSensorInfoEvent(LatLng location, String areaName, String parkingCount, double distance) {
        this.location = location;
        this.areaName = areaName;
        this.parkingCount = parkingCount;
        this.distance = distance;
    }
}
