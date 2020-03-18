package www.fiberathome.com.parkingapp.eventBus;

import com.google.android.gms.maps.model.LatLng;

public class GetDirectionAfterButtonClickEvent {

    public final LatLng location;

    public GetDirectionAfterButtonClickEvent(LatLng location) {
        this.location = location;
    }
}
