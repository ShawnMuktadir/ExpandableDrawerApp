package www.fiberathome.com.parkingapp.utils;

import static www.fiberathome.com.parkingapp.ui.home.HomeFragment.PLAY_SERVICES_ERROR_CODE;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.data.Constants;
import www.fiberathome.com.parkingapp.model.data.StaticData;
import www.fiberathome.com.parkingapp.service.booking_service.BookingService;
import www.fiberathome.com.parkingapp.service.notification.BookingServiceStarter;
import www.fiberathome.com.parkingapp.utils.internet.ConnectivityInterceptor;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class ApplicationUtils {

    public static OkHttpClient getClient(final Context context) {

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient
                .Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    Request.Builder requestBuilder = request.newBuilder();
                    requestBuilder.addHeader(StaticData.OS, "Android");
                    //requestBuilder.addHeader(StaticData.VERSION, BuildConfig.VERSION_NAME);
                    if (ConnectivityUtils.getInstance().checkInternet(context)) {
                        requestBuilder.header("Cache-Control", "public, max-age=" + 60);
                    } else {
                        requestBuilder.header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7);
                    }
                    request = requestBuilder.build();
                    return chain.proceed(request);
                })
                .addInterceptor(new ConnectivityInterceptor(context))
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .build();
    }

    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment, int frameId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frameId, fragment);
        transaction.commit();
    }

    public static void addFragmentToActivityWithBackStack(@NonNull FragmentManager fragmentManager,
                                                          @NonNull Fragment fragment, int frameId, String title) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frameId, fragment);
        transaction.addToBackStack(title);
        transaction.commit();
    }

    public static void replaceFragmentWithAnimation(@NonNull FragmentManager fragmentManager,
                                                    @NonNull Fragment fragment) {
        //, String tag
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        transaction.replace(R.id.nav_host_fragment, fragment);
        //transaction.addToBackStack(tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static void refreshFragment(@NonNull FragmentManager fragmentManager,
                                       @NonNull Fragment fragment) {
        // Reload current fragment
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        //transaction.setCustomAnimations(0, 0);
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.detach(fragment);
        transaction.attach(fragment);
        transaction.commit();
    }

    public static void recreateFragment(@NonNull FragmentManager fragmentManager,
                                        @NonNull Fragment fragment) {
        try {
            Fragment.SavedState savedState = fragmentManager.saveFragmentInstanceState(fragment);
            Fragment newInstance = fragment.getClass().newInstance();
            newInstance.setInitialSavedState(savedState);
        } catch (Exception e) // InstantiationException, IllegalAccessException
        {
            throw new RuntimeException("Cannot reinstated fragment " + fragment.getClass().getName(), e);
        }
    }

    public static void reLoadFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        //Timber.e("reloading fragment");
        fragmentManager.beginTransaction().replace(fragment.getId(),
                fragment).commit();
    }

    public static void detachAttachFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        fragmentManager.beginTransaction()
                .detach(fragment)
                .attach(fragment)
                .commit();
    }

    /**
     * Redirect to play store
     */
    public static void redirectStore(Context context) {
        Uri updateUrl = Uri.parse("market://details?id=" + context.getPackageName());
        final Intent intent = new Intent(Intent.ACTION_VIEW, updateUrl);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static boolean isServicesOk(Context context) {

        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();

        int result = googleApi.isGooglePlayServicesAvailable(context);

        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApi.isUserResolvableError(result)) {
            Dialog dialog = googleApi.getErrorDialog((Activity) context, result, PLAY_SERVICES_ERROR_CODE, task ->
                    ToastUtils.getInstance().showToastMessage(context, "Dialog is cancelled by User"));
            if (dialog != null) {
                dialog.show();
            }
        }

        return false;
    }

    public static boolean isLocationTrackingServiceRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                String a = Service.class.getName();
                if (serviceInfo.foreground) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public static void startBookingTrackService(Context context) {
        if (!isLocationTrackingServiceRunning(context)) {
            Intent intent = new Intent(context, BookingService.class);
            intent.setAction(Constants.START_BOOKING_TRACKING);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        }
    }

    public static void startBookingExceedService(Context context, long departureDate) {
        if (!isLocationTrackingServiceRunning(context)) {
            Intent intent = new Intent(context, BookingService.class);
            intent.putExtra("departureDate", departureDate);
            intent.setAction(Constants.BOOKING_EXCEED_CHECK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        }
    }

    public static void stopBookingTrackService(Context context) {
        Intent intent = new Intent(context, BookingService.class);
        intent.setAction(Constants.STOP_BOOKING_TRACKING);
        context.startService(intent);
    }

    public static void startAlarm(Context context, Calendar arrival, Calendar departure) {
        Timber.e("startAlarm called");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BookingServiceStarter.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);
        intent.putExtra("EndBooking", true);
        intent.putExtra("departure", departure.getTimeInMillis());
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 2, intent, 0);

        if (new Date().getTime() >= (arrival.getTimeInMillis() - 900000)) {
            Objects.requireNonNull(alarmManager).setExact(AlarmManager.RTC_WAKEUP,
                    new Date().getTime() - 2000, pendingIntent);
        } else {
            Objects.requireNonNull(alarmManager).setExact(AlarmManager.RTC_WAKEUP,
                    arrival.getTimeInMillis() - 900000, pendingIntent);
        }
        Objects.requireNonNull(alarmManager).setExact(AlarmManager.RTC_WAKEUP,
                (departure.getTimeInMillis() + (6 * 60000)), pendingIntent2);
    }

    public static String getDate(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static Calendar convertLongToCalendar(Long source) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(source);
        return calendar;
    }
}
