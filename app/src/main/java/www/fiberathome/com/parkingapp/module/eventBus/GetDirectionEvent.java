package www.fiberathome.com.parkingapp.module.eventBus;

import com.google.android.gms.maps.model.LatLng;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class GetDirectionEvent {

    public final LatLng location;

    public GetDirectionEvent(LatLng location) {
        this.location = location;
    }
}
