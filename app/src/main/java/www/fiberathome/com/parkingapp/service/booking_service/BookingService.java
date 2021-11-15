package www.fiberathome.com.parkingapp.service.booking_service;

import static www.fiberathome.com.parkingapp.model.data.Constants.BOOKING_SERVICE_ID;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.api.ApiClient;
import www.fiberathome.com.parkingapp.model.api.ApiService;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.Constants;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.response.booking.CloseReservationResponse;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;

public class BookingService extends Service {

    public static final String TAG = "LocationTrackService";
    public static final int BOOKING_CHECK_DELAY = 1000; // 1 sec

    private Context context;
    public Location previousBestLocation = null;
    FusedLocationProviderClient fusedLocationProviderClient;


    private CountDownTimer countDownTimer;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder mBuilder;
    private long departureDate;
    protected final long exceedTime = 300000;

    public static Boolean isRunning = false;
    private boolean isExceedRunned = false;
    private boolean warringShowed = false;
    private boolean endBookingCalled = false;
    private boolean isServiceStarted = false;
    private boolean isFifteenMinsRemaining = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("not Implemented yet");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        context = getApplicationContext();
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case Constants.START_BOOKING_TRACKING:
                    mHandlerTask.run();
                    break;
                case Constants.STOP_BOOKING_TRACKING:
                    stopTrackingLocation();
                    break;
                case Constants.BOOKING_EXCEED_CHECK:
                    departureDate = 0;
                    departureDate = intent.getLongExtra("departureDate", 0);
                    exceedHandlerTask.run();
                    break;
            }
        }
        return START_REDELIVER_INTENT;
    }

    private void startBookingTracking() {

        Timber.e("abdur service running");
        if (!isServiceStarted) {
            notificationCaller(Constants.NOTIFICATION_CHANNEL_BOOKING, "Booked for : \n" + Preferences.getInstance(context).getBooked().getAreaName(), 2);
            startForeground(BOOKING_SERVICE_ID, mBuilder.build());
            isServiceStarted = true;
        }
//        else if (new Date().getTime() >= (Preferences.getInstance(context).getBooked().getArriveDate() - 900000) && !isFifteenMinsRemaining) {
//            isFifteenMinsRemaining = true;
//            sendNotification("Booked park", "Booked time is about to begin shortly", false);
//        }
        else if (new Date().getTime() >= Preferences.getInstance(context).getBooked().getArriveDate()
                && new Date().getTime() < Preferences.getInstance(context).getBooked().getDepartedDate() && !isRunning) {
            isRunning = true;
            findDifference(getDate(new Date().getTime()), getDate(Preferences.getInstance(context).getBooked().getDepartedDate()));
        } else if (new Date().getTime() > (Preferences.getInstance(context).getBooked().getDepartedDate() + 60000L) && !endBookingCalled) {
            if (ConnectivityUtils.getInstance().checkInternet(context)) {
                endBooking();
            }
        }
    }

    private void startExceedTimeTracking() {
        //service starts when car is on parking spot and user clicks on park button
        if (!isExceedRunned) {
            Timber.e("car parked");
            //Toast.makeText(context, "car parked", Toast.LENGTH_LONG).show();
            notificationCaller(Constants.NOTIFICATION_CHANNEL_EXCEED_BOOKING, "Car Parked", 3);
            startForeground(Constants.BOOKING_Exceed_SERVICE_ID, mBuilder.build());
            isExceedRunned = true;
        }

        //executes when booking time ends
        if (!warringShowed && new Date().getTime() >= departureDate) {
            Timber.e("car Parking Duration End -> %s %s", new Date().getTime(), departureDate);
            //Toast.makeText(context, "car Parking Duration End:" + new Date().getTime() + "," + departureDate, Toast.LENGTH_LONG).show();
            if (ConnectivityUtils.getInstance().checkInternet(context)) {
                warringShowed = true;
                sendNotification("Booked Time", "Parking Duration About To End", false);
                startCountDown((exceedTime - (new Date().getTime() - departureDate)) >= 0 ? (exceedTime - (new Date().getTime() - departureDate)) : 0, true);
            }
        }

    }

    private void notificationCaller(String NOTIFICATION_CHANNEL_ID, String msg, int requestCode) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent targetIntent = new Intent(context, HomeActivity.class);
        targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, requestCode, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Booked Parking")
                .setContentText(msg)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setSilent(true)
                .setContentIntent(contentIntent);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, msg, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Booked Time");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            //notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.setVibrationPattern(null);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(100)
                .setFastestInterval(500)
                .setMaxWaitTime(100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setSmallestDisplacement(1f);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {
            if (fusedLocationProviderClient != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                }
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Objects.requireNonNull(Looper.myLooper()));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopTrackingLocation() {
        if (mHandler != null && mHandlerTask != null) {
            mHandler.removeCallbacks(mHandlerTask);
        }
        if (exceedHandler != null) {
            exceedHandler.removeCallbacks(exceedHandlerTask);
        }
        isRunning = false;
        warringShowed = false;
        departureDate = 0;
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        try {
            stopForeground(true);
            stopSelf();
        } catch (Exception e) {
            Timber.e(e.getCause());
        }
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            if (isBetterLocation(locationResult.getLastLocation(), previousBestLocation)) {
                super.onLocationResult(locationResult);
                previousBestLocation = locationResult.getLastLocation();
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();
                Timber.d("LocationResult- lat" + latitude + " -lon" + longitude);
                sendLocationUpdate(locationResult.getLastLocation());
            }
        }
    };

    private void sendLocationUpdate(Location l) {
        Intent intent = new Intent("GPSLocationUpdates");
        // You can also include some extra data.
        Bundle b = new Bundle();
        b.putParcelable("Location", l);
        intent.putExtra("Location", b);
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > BOOKING_CHECK_DELAY;
        boolean isSignificantlyOlder = timeDelta < -BOOKING_CHECK_DELAY;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else return isNewer && !isSignificantlyLessAccurate && isFromSameProvider;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    Handler mHandler = new Handler();
    Handler exceedHandler = new Handler();
    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            startBookingTracking();
            mHandler.postDelayed(mHandlerTask, BOOKING_CHECK_DELAY);
        }
    };

    protected final boolean isExceedRunning = false;
    private final Runnable exceedHandlerTask = new Runnable() {
        @Override
        public void run() {
            if (!isExceedRunning) {
                startExceedTimeTracking();
            }
            exceedHandler.postDelayed(exceedHandlerTask, BOOKING_CHECK_DELAY);
        }
    };

    @SuppressLint("SetTextI18n")
    private void startCountDown(long timerMilliDifference, boolean exceedCounter) {
        countDownTimer = new CountDownTimer(timerMilliDifference, 1000) {
            @SuppressLint("DefaultLocale")
            public void onTick(long millisUntilFinished) {
                int numMessages = 0;
                mBuilder.setContentText("" + String.format("%d min, %d sec remaining",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))))
                        .setSound(null)
                        .setVibrate(null);
                // Because the ID remains unchanged, the existing notification is
                // updated.
                if (exceedCounter) {
                    notificationManager.notify(
                            Constants.BOOKING_Exceed_SERVICE_ID,  // <-- Place your notification id here
                            mBuilder.build());
                } else {
                    notificationManager.notify(
                            BOOKING_SERVICE_ID,  // <-- Place your notification id here
                            mBuilder.build());
                }
            }

            public void onFinish() {
                if (ConnectivityUtils.getInstance().checkInternet(context)) {
                    endBooking();
                }
            }
        }.start();
    }

    private void endBooking() {
        endBookingCalled = true;
        ApiService request = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        String user = Preferences.getInstance(context).getUser().getMobileNo();
        String bookedUid = Preferences.getInstance(context).getBooked().getBookedUid();
        String reservationId = Preferences.getInstance(context).getBooked().getReservation();
        Call<CloseReservationResponse> call = request.endReservation(user, bookedUid, reservationId);
        call.enqueue(new Callback<CloseReservationResponse>() {
            @Override
            public void onResponse(@NonNull Call<CloseReservationResponse> call, @NonNull Response<CloseReservationResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Preferences.getInstance(context).isBookingCancelled = true;
                        Preferences.getInstance(context).clearBooking();
                        sendNotification("Booked Time", "Your Booked Parking Duration Has Ended", true);
                        Timber.e("Booking closed");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CloseReservationResponse> call, @NonNull Throwable t) {
                Timber.e("onFailure -> %s", t.getMessage());
            }
        });
    }

    public void findDifference(String start_date,
                               String end_date) {

        // SimpleDateFormat converts the
        // string format to date object
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        // try Block
        try {

            // parse method is used to parse
            // the text from a string to
            // produce the date
            Date d1 = sdf.parse(start_date);
            Date d2 = sdf.parse(end_date);

            // Calculate time difference
            // in milliseconds
            if (d1 != null && d2 != null) {
                long difference_In_Time
                        = d2.getTime() - d1.getTime();

                // Calculate time difference in
                // seconds, minutes, hours, years,
                // and days
                long difference_In_Seconds
                        = (difference_In_Time
                        / 1000)
                        % 60;

                long difference_In_Minutes
                        = (difference_In_Time
                        / (1000 * 60))
                        % 60;

                long difference_In_Hours
                        = (difference_In_Time
                        / (1000 * 60 * 60))
                        % 24;

                long difference_In_Years
                        = (difference_In_Time
                        / (1000L * 60 * 60 * 24 * 365));

                long difference_In_Days
                        = (difference_In_Time
                        / (1000 * 60 * 60 * 24))
                        % 365;

                // Print the date difference in
                // years, in days, in hours, in
                // minutes, and in seconds
                //tvTimeDifference.setText(difference_In_Hours + " hr " + difference_In_Minutes + " min " + difference_In_Seconds + " sec");
                startCountDown(difference_In_Time, false);

                System.out.print(
                        "Difference "
                                + "between two dates is: ");

                System.out.println(
                        difference_In_Years
                                + " years, "
                                + difference_In_Days
                                + " days, "
                                + difference_In_Hours
                                + " hours, "
                                + difference_In_Minutes
                                + " minutes, "
                                + difference_In_Seconds
                                + " seconds");
            }
        }

        // catch the Exception
        catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String getDate(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @SuppressWarnings("SameParameterValue")
    private void sendNotification(String title, String content, boolean close) {
        String NOTIFICATION_CHANNEL_ID = "Shawn_Muktadir";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            //config
            notificationChannel.setDescription("Parking App Booking Notification");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));

        Notification notification = builder.build();
        if (notificationManager != null) {
            notificationManager.notify(new Random().nextInt(), notification);
        }
        if (close) {
            endBookingCalled = false;
            closeBooking();
            if (countDownTimer != null)
                countDownTimer.cancel();
            stopTrackingLocation();
        }
    }

    private void closeBooking() {
        Intent intent = new Intent("booking_ended");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        if (Preferences.getInstance(context).getBooked().getIsBooked()) {
//           Toast.makeText(context, "service restarted", Toast.LENGTH_SHORT).show();
            Timber.e("abdur service restarted");
            Intent intent = new Intent(context, BookingService.class);
            intent.setAction(Constants.START_BOOKING_TRACKING);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        } else {
//           Toast.makeText(context, "service destroyed", Toast.LENGTH_SHORT).show();
            Timber.e("abdur service destroyed");
            super.onDestroy();
        }
    }
}
