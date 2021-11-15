package www.fiberathome.com.parkingapp.service.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

public class BookingServiceStarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ApplicationUtils.startBookingTrackService(context);
    }
}
