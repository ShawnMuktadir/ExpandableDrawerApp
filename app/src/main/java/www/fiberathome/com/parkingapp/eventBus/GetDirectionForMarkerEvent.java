package www.fiberathome.com.parkingapp.eventBus;

import com.google.android.gms.maps.model.LatLng;

public class GetDirectionForMarkerEvent {

    public final LatLng location;

    public GetDirectionForMarkerEvent(LatLng location) {
        this.location = location;
    }
}
