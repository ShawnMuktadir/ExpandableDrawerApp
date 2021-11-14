package www.fiberathome.com.parkingapp.service.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

class NotificationHelper extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";
    private final Context context;
    private String ended;
    private NotificationManager notificationManager;

    public NotificationHelper(Context base, String ended) {
        super(base);
        context = base;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            this.ended = ended;
//        }
    }

    private void createChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel;
            notificationChannel = new NotificationChannel(channelID, channelName,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
            getManager().createNotificationChannel(notificationChannel);
        }
    }

    public NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public NotificationCompat.Builder getChannelNotification() {
        if (ended != null) {
            if (ended.equalsIgnoreCase("Book Time Up")) {
//               closeBooking();
            }

            ApplicationUtils.startBookingTrackService(context);

            return new NotificationCompat.Builder(getApplicationContext(), channelID)
                    .setContentTitle("Booking Scheduled Alert")
//                    .setContentText(ended)
                    .setContentText("Booked time will start shortly")
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                    .setSmallIcon(R.mipmap.ic_launcher);
        } else {
            return new NotificationCompat.Builder(getApplicationContext(), channelID)
                    .setContentTitle("Booking Scheduled Alert")
                    .setContentText("Booking time is about to end")
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                    .setSmallIcon(R.mipmap.ic_launcher);
        }
    }

    private void closeBooking() {
        Intent intent = new Intent("booking_ended");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
