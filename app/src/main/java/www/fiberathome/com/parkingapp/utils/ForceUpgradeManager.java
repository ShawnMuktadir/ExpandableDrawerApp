package www.fiberathome.com.parkingapp.utils;

import static www.fiberathome.com.parkingapp.R.layout.bottom_sheet_dialog_app_update_options;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.BuildConfig;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.ParkingApp;

public class ForceUpgradeManager implements LifecycleObserver {

    private static final String KEY_UPDATE_REQUIRED = "force_update_required";
    private static final String KEY_CURRENT_VERSION = "force_update_current_version";
    private static final String KEY_UPDATE_URL = "force_update_store_url";

    private final Context context;

    @Nullable
    private WeakReference<Activity> activityWeakReference;
    public final Application.ActivityLifecycleCallbacks callbacks =
            new Application.ActivityLifecycleCallbacks() {

                @Override
                public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
                }

                @Override
                public void onActivityStarted(@NonNull Activity activity) {
                    ForceUpgradeManager.this.activityWeakReference = new WeakReference<>(activity);
                }

                @Override
                public void onActivityResumed(@NonNull Activity activity) {
                    //ForceUpgradeManager.this.activityWeakReference = new WeakReference<>(activity);
                }

                @Override
                public void onActivityPaused(@NonNull Activity activity) {
                    if (bottomSheetDialogAppUpdate != null) {
                        bottomSheetDialogAppUpdate.dismiss();
                    }
                }

                @Override
                public void onActivityStopped(@NonNull Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(@NonNull Activity activity,
                                                        @NonNull Bundle bundle) {

                }

                @Override
                public void onActivityDestroyed(@NonNull Activity activity) {

                }
            };
    private BottomSheetDialog bottomSheetDialogAppUpdate;

    public ForceUpgradeManager(ParkingApp application) {
        this.context = application.getApplicationContext();
        application.registerActivityLifecycleCallbacks(callbacks);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void appStarted() {
        checkForceUpdateNeeded();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void appStopped() {
        if (activityWeakReference != null) {
            activityWeakReference.clear();
        }
    }

    @Nullable
    private Activity getCurrentActivity() {
        return activityWeakReference != null && activityWeakReference.get() != null
                ? activityWeakReference.get() : null;
    }

    /**
     * Gets update alert.
     */
    private void onUpdateNeeded(String updateUrl) {
        Activity temp = getCurrentActivity();
        if (temp != null) {
            showAppUpdateDialog(updateUrl);
        }
    }

    @SuppressLint("InflateParams")
    public void showAppUpdateDialog(String updateUrl) {
        getLayoutInflater();
        View dialogView = View.inflate(context, bottom_sheet_dialog_app_update_options, null);

        AppCompatButton buttonUpdate = dialogView.findViewById(R.id.button_update);
        buttonUpdate.setOnClickListener(view -> {
            redirectStore(updateUrl);
            //redirectStore();
        });

        bottomSheetDialogAppUpdate = DialogUtils.getInstance().bottomSheetDialog(getCurrentActivity(), dialogView,
                true, false);
    }

    private void getLayoutInflater() {
        View.inflate(context, bottom_sheet_dialog_app_update_options, null);
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void redirectStore(String updateUrl) {
        try {
            Uri webpage = Uri.parse(updateUrl);

            if (!updateUrl.startsWith("http://") && !updateUrl.startsWith("https://")) {
                webpage = Uri.parse("http://" + updateUrl);
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        } catch (Exception e) {
            Timber.e(e.getCause());
            TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.something_went_wrong));
        }
    }

    private void checkForceUpdateNeeded() {
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        // long cacheExpiration = 12 * 60 * 60; // fetch every 12 hours
        // set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap<>();
        remoteConfigDefaults.put(KEY_UPDATE_REQUIRED, false);
        remoteConfigDefaults.put(KEY_CURRENT_VERSION, BuildConfig.VERSION_NAME);
        remoteConfigDefaults.put(KEY_UPDATE_URL,
                "https://play.google.com/store/apps/details?id=www.fiberathome.com.parkingapp"); //play store url

        remoteConfig.setDefaultsAsync(remoteConfigDefaults);
        remoteConfig.fetch(0)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Timber.d("task successful -> %s", "remote config is fetched.");
                        remoteConfig.activate();
                    }
                    if (remoteConfig.getBoolean(KEY_UPDATE_REQUIRED)) {
                        String currentVersion = remoteConfig.getString(KEY_CURRENT_VERSION);
                        String appVersion = getAppVersion(context);
                        String updateUrl = remoteConfig.getString(KEY_UPDATE_URL);
                        if (!TextUtils.equals(currentVersion, appVersion)) {
                            onUpdateNeeded(updateUrl);
                        }
                    }
                });
    }

    private String getAppVersion(Context context) {
        String result = "";
        try {
            result = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionName;
            result = result.replaceAll("[a-zA-Z]|-", "");
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e("Exception -> %s", e.getMessage());
        }

        return result;
    }
}
