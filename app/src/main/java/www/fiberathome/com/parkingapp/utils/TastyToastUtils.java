package www.fiberathome.com.parkingapp.utils;

import android.content.Context;

import com.sdsmdg.tastytoast.TastyToast;

public class TastyToastUtils {

    public static void showTastySuccessToast(Context context, String message) {
        //TastyToast.makeText(context, message, TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
        //AnimatedToast.Success(context, "Success", message, Gravity.BOTTOM, Toast.LENGTH_LONG, AnimatedToast.STYLE_DARK, AnimatedToast.ANIMATION_ROTATE);
        ToastUtils.getInstance().showToast(context, message);
    }

    public static void showTastyErrorToast(Context context, String message) {
        //TastyToast.makeText(context, message, TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
        //AnimatedToast.Error(context, "Error", message, Gravity.BOTTOM, Toast.LENGTH_LONG, AnimatedToast.STYLE_DARK, AnimatedToast.ANIMATION_BLINK);
        ToastUtils.getInstance().showToast(context, message);
    }

    public static void showTastyInfoToast(Context context, String message) {
        //TastyToast.makeText(context, message, TastyToast.LENGTH_LONG, TastyToast.INFO).show();
        //AnimatedToast.Info(context, "Info", message, Gravity.BOTTOM, Toast.LENGTH_LONG, AnimatedToast.STYLE_DARK, AnimatedToast.ANIMATION_FLIP);
        ToastUtils.getInstance().showToast(context, message);
    }

    public static void showTastyWarningToast(Context context, String message) {
        //TastyToast.makeText(context, message, TastyToast.LENGTH_LONG, TastyToast.WARNING).show();
        //AnimatedToast.Warning(context, "Warning", message, Gravity.BOTTOM, Toast.LENGTH_LONG, AnimatedToast.STYLE_DARK, AnimatedToast.ANIMATION_PULSE);
        ToastUtils.getInstance().showToast(context, message);
    }

    public static void showTastyDefaultToast(Context context, String message) {
        TastyToast.makeText(context, message, TastyToast.LENGTH_LONG, TastyToast.DEFAULT).show();
    }

    public static void showTastyConfusionToast(Context context, String message) {
        TastyToast.makeText(context, message, TastyToast.LENGTH_LONG, TastyToast.CONFUSING).show();
    }


}

