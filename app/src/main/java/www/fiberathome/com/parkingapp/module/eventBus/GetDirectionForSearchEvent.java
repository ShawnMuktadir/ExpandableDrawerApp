package www.fiberathome.com.parkingapp.module.eventBus;

import com.google.android.gms.maps.model.LatLng;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class GetDirectionForSearchEvent {

    public final LatLng location;

    public GetDirectionForSearchEvent(LatLng location) {
        this.location = location;
    }
}
