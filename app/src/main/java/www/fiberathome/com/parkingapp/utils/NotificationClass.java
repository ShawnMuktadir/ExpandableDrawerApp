package www.fiberathome.com.parkingapp.utils;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationClass extends Application {

    public static final String CHANNEL_HIGH_PRIORITY_ID = "heighpriority";
    public static final String CHANNEL_DEFAULT_PRIORITY_ID = "defaultpriority";


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {

        // check android version heigher than Orio or heigher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel heighpriority = new NotificationChannel(
                    CHANNEL_HIGH_PRIORITY_ID,
                    "Heigh Priority",
                    NotificationManager.IMPORTANCE_HIGH
            );

            heighpriority.enableVibration(true);
            heighpriority.setDescription("Heigh Priority Notification!");

            NotificationChannel defaultpriority = new NotificationChannel(
                    CHANNEL_DEFAULT_PRIORITY_ID,
                    "Default Priority",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            defaultpriority.enableVibration(false);
            defaultpriority.setDescription("Default  Priority Notification!");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(heighpriority);
            manager.createNotificationChannel(defaultpriority);
        }
    }
}
