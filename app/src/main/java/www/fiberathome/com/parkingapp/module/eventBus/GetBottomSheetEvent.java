package www.fiberathome.com.parkingapp.module.eventBus;

import com.google.android.gms.maps.model.LatLng;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class GetBottomSheetEvent {

    public final LatLng location;

    public GetBottomSheetEvent(LatLng location) {
        this.location = location;
    }
}
