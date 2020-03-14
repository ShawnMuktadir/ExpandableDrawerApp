package www.fiberathome.com.parkingapp.eventBus;

import com.google.android.gms.maps.model.LatLng;

public class SetMarkerEvent {

    public final LatLng location;

    public SetMarkerEvent(LatLng location) {
        this.location = location;
    }
}
