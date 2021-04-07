package www.fiberathome.com.parkingapp.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.data.StaticData;
import www.fiberathome.com.parkingapp.utils.internet.ConnectivityInterceptor;

@SuppressWarnings("unused")
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
}
