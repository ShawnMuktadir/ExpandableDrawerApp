package www.fiberathome.com.parkingapp.module.notification;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

public class NotificationPublisher extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        intent.getStringExtra("ended");
        intent.getStringExtra("Started");
        NotificationHelper notificationHelper;
       if(intent.getStringExtra("Started")!=null) {
            notificationHelper = new NotificationHelper(context, intent.getStringExtra("Started"));
       }
       else {

            notificationHelper = new NotificationHelper(context, intent.getStringExtra("ended"));
       }

        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(1, nb.build());
        Notification notification = nb.build();
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_SOUND;
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(2000);
        }
        // notification.sound = Uri.parse("android.resource://com.packagename.org/raw/alert");
    }
}
