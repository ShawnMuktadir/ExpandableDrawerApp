package www.fiberathome.com.parkingapp.eventBus;

import com.google.android.gms.maps.model.LatLng;

public class GetDirectionBottomSheetEvent {

    public final LatLng location;

    public GetDirectionBottomSheetEvent(LatLng location) {
        this.location = location;
    }
}
