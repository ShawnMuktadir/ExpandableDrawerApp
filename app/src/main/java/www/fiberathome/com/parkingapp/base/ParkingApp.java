package www.fiberathome.com.parkingapp.base;

import static www.fiberathome.com.parkingapp.data.model.data.Constants.LANGUAGE_BN;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDex;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.BuildConfig;
import www.fiberathome.com.parkingapp.data.model.data.preference.LanguagePreferences;
import www.fiberathome.com.parkingapp.data.source.APIClient;
import www.fiberathome.com.parkingapp.ui.splash.SplashActivity;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.ForceUpdateChecker;
import www.fiberathome.com.parkingapp.utils.ForceUpgradeManager;
import www.fiberathome.com.parkingapp.utils.internet.ConnectivityReceiver;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class ParkingApp extends Application implements LifecycleObserver {

    public static final String TAG = ParkingApp.class.getSimpleName();

    private static ParkingApp mInstance;

    private RequestQueue mRequestQueue;

    protected FirebaseRemoteConfig firebaseRemoteConfig;

    private ForceUpgradeManager forceUpgradeManager;

    public static synchronized ParkingApp getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        initTimber();

        APIClient.init();

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        //setAppDefaults();

        if (ConnectivityUtils.getInstance().isGPSEnabled(getApplicationContext()) && !getClass().getSimpleName().equalsIgnoreCase(SplashActivity.class.getSimpleName())) {
            initForceUpgradeManager();
            ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        }
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
        if (LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_BN)) {
            setAppLocale(LANGUAGE_BN);
        } else {
            setAppLocale(LanguagePreferences.getInstance(context).getAppLanguage());
        }
    }

    private void setAppDefaults() {
        // set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap<>();
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, false);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_CURRENT_VERSION, BuildConfig.VERSION_NAME);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_URL,
                "https://play.google.com/store/apps/details?id=www.fiberathome.com.parkingapp"); //play store url

        firebaseRemoteConfig.setDefaultsAsync(remoteConfigDefaults);

        firebaseRemoteConfig.fetch(60) // fetch every minutes
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Timber.e("remote config is fetched.");
                        firebaseRemoteConfig.activate();
                    }
                });
    }

    public void initForceUpgradeManager() {
        if (forceUpgradeManager == null) {
            forceUpgradeManager = new ForceUpgradeManager(mInstance);
        }
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    private void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected String createStackElementTag(@NonNull StackTraceElement element) {
                    return super.createStackElementTag(element) + ": " + element.getLineNumber();
                }
            });
        }
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.listener = listener;
    }

    @SuppressLint("ObsoleteSdkInt")
    public void setAppLocale(String localeCode) {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(new Locale(localeCode.toLowerCase()));
        } else {
            config.locale = new Locale(localeCode.toLowerCase());
        }
        resources.updateConfiguration(config, dm);
    }
}