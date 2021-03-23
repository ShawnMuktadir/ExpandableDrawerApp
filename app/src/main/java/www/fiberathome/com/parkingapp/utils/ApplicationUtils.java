package www.fiberathome.com.parkingapp.utils;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.data.StaticData;
import www.fiberathome.com.parkingapp.utils.internet.ConnectivityInterceptor;

public class ApplicationUtils {

    public static void showToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showToastWithDelay(Context context, String message, long countDown) {
        // Set the toast and duration
        final Toast mToastToShow = Toast.makeText(context, message, Toast.LENGTH_LONG);

        // Set the countdown to display the toast
        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(countDown, countDown /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                mToastToShow.show();
            }

            public void onFinish() {
                mToastToShow.cancel();
            }
        };

        // Show the toast and starts the countdown
        mToastToShow.show();
        toastCountDown.start();
    }

    public static OkHttpClient getClient(final Context context) {

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient
                .Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
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
                    }
                })
                .addInterceptor(new ConnectivityInterceptor(context))
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .build();
        return client;
    }

    public static void hideKeyboard(Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(((Activity) mContext).getWindow().getCurrentFocus().getWindowToken(), 0);
    }

    public static void hideKeyboard(final Activity activity) {
        Timber.e("hideKeyboard -> ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (activity != null) {
                                final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                                final View view = activity.getCurrentFocus();
                                if (view != null) {
                                    final IBinder binder = view.getWindowToken();
                                    imm.hideSoftInputFromWindow(binder, 0);
                                    imm.showSoftInputFromInputMethod(binder, 0);
                                }
                            }
                        } catch (final Exception e) {
                            Timber.d(e, "-> %s Exception to hide keyboard", ApplicationUtils.class.getSimpleName());
                        }
                    }
                });
            }
        }).start();
    }

    public static void hideKeyboard(Context context, EditText editText) {
        editText.clearFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
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

    public static Fragment recreateFragment(@NonNull FragmentManager fragmentManager,
                                            @NonNull Fragment fragment) {
        try {
            Fragment.SavedState savedState = fragmentManager.saveFragmentInstanceState(fragment);
            Fragment newInstance = fragment.getClass().newInstance();
            newInstance.setInitialSavedState(savedState);
            return newInstance;
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
}
