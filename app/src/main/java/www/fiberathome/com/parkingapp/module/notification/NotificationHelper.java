package www.fiberathome.com.parkingapp.module.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import www.fiberathome.com.parkingapp.R;

class NotificationHelper extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";
    private String ended;
    private NotificationManager notificationManager;

    public NotificationHelper(Context base, String ended) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            this.ended = ended;
        }
    }

    //@TargetApi(Build.VERSION_CODES.O)
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
            return new NotificationCompat.Builder(getApplicationContext(), channelID)
                    .setContentTitle("Booking Scheduled Alert")
                    .setContentText(ended)
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
}
