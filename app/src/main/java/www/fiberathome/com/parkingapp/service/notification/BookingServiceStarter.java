package www.fiberathome.com.parkingapp.service.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

public class BookingServiceStarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isBookingEnd = intent.getBooleanExtra("EndBooking", false);
        if (!isBookingEnd) {
            ApplicationUtils.startBookingTrackService(context);
        }
        else {
            if (Preferences.getInstance(context).getBooked().getIsBooked()) {
                ApplicationUtils.startBookingExceedService(context, intent.getLongExtra("departure", 0));
            }
        }
    }
}
