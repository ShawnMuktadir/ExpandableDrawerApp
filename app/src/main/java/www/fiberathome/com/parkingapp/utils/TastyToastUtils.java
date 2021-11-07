package www.fiberathome.com.parkingapp.utils;

import android.content.Context;

public class TastyToastUtils {

    public static void showTastySuccessToast(Context context, String message) {
        ToastUtils.getInstance().showToast(context, message);
    }

    public static void showTastyErrorToast(Context context, String message) {
        ToastUtils.getInstance().showToast(context, message);
    }

    public static void showTastyInfoToast(Context context, String message) {
        ToastUtils.getInstance().showToast(context, message);
    }

    public static void showTastyWarningToast(Context context, String message) {
        ToastUtils.getInstance().showToast(context, message);
    }
}

