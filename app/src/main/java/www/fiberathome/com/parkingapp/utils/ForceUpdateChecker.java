package www.fiberathome.com.parkingapp.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import timber.log.Timber;

public class ForceUpdateChecker {

    public static final String KEY_UPDATE_REQUIRED = "force_update_required";
    public static final String KEY_CURRENT_VERSION = "force_update_current_version";
    public static final String KEY_UPDATE_URL = "force_update_store_url";

    private final OnUpdateNeededListener onUpdateNeededListener;
    private final Context context;

    public interface OnUpdateNeededListener {
        void onUpdateNeeded(String updateUrl);

        void noUpdateNeeded();
    }

    public static Builder with(@NonNull Context context) {
        return new Builder(context);
    }

    public ForceUpdateChecker(@NonNull Context context,
                              OnUpdateNeededListener onUpdateNeededListener) {
        this.context = context;
        this.onUpdateNeededListener = onUpdateNeededListener;
    }

    public void check() {
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

        if (remoteConfig.getBoolean(KEY_UPDATE_REQUIRED)) {
            String currentVersion = remoteConfig.getString(KEY_CURRENT_VERSION);
            String appVersion = getAppVersion(context);
            String updateUrl = remoteConfig.getString(KEY_UPDATE_URL);

            if (!TextUtils.equals(currentVersion, appVersion)
                    && onUpdateNeededListener != null) {

                if (!updateUrl.equalsIgnoreCase("") && !updateUrl.isEmpty()) {
                    onUpdateNeededListener.onUpdateNeeded(updateUrl);
                } else {
                    Toast.makeText(context, "No play store URL found", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (onUpdateNeededListener != null) {
                    onUpdateNeededListener.noUpdateNeeded();
                }
            }
        } else {
            if (onUpdateNeededListener != null) {
                onUpdateNeededListener.noUpdateNeeded();
            }
        }
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

    public static class Builder {

        private final Context context;
        private OnUpdateNeededListener onUpdateNeededListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder onUpdateNeeded(OnUpdateNeededListener onUpdateNeededListener) {
            this.onUpdateNeededListener = onUpdateNeededListener;
            return this;
        }

        public ForceUpdateChecker build() {
            return new ForceUpdateChecker(context, onUpdateNeededListener);
        }

        public ForceUpdateChecker check() {
            ForceUpdateChecker forceUpdateChecker = build();

            forceUpdateChecker.check();

            return forceUpdateChecker;
        }
    }
}
