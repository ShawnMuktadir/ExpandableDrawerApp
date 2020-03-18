package www.fiberathome.com.parkingapp.eventBus;

import com.google.android.gms.maps.model.LatLng;

public class GetDirectionForSearchEvent {

    public final LatLng location;

    public GetDirectionForSearchEvent(LatLng location) {
        this.location = location;
    }
}
