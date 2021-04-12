package www.fiberathome.com.parkingapp.module.eventBus;

import com.google.android.gms.maps.model.LatLng;

@SuppressWarnings("unused")
public class GetDirectionAfterButtonClickEvent {

    public final LatLng location;

    public GetDirectionAfterButtonClickEvent(LatLng location) {
        this.location = location;
    }
}
