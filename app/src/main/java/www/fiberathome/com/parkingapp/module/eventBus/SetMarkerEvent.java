package www.fiberathome.com.parkingapp.module.eventBus;

import com.google.android.gms.maps.model.LatLng;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class SetMarkerEvent {

    public final LatLng location;

    public SetMarkerEvent(LatLng location) {
        this.location = location;
    }
}
