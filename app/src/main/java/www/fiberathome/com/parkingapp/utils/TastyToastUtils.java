package www.fiberathome.com.parkingapp.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;
import com.vdx.animatedtoast.AnimatedToast;

public class TastyToastUtils {
    public static void showTastySuccessToast(Context context, String message) {
//        TastyToast.makeText(context, message, TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
        AnimatedToast.Success(context, "Success", message, Gravity.BOTTOM, Toast.LENGTH_LONG, AnimatedToast.STYLE_DARK, AnimatedToast.ANIMATION_ROTATE);
    }

    public static void showTastyErrorToast(Context context, String message) {
//        TastyToast.makeText(context, message, TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
        AnimatedToast.Error(context, "Error", message, Gravity.BOTTOM, Toast.LENGTH_LONG, AnimatedToast.STYLE_DARK, AnimatedToast.ANIMATION_BLINK);
    }

    public static void showTastyInfoToast(Context context, String message) {
//        TastyToast.makeText(context, message, TastyToast.LENGTH_LONG, TastyToast.INFO).show();
        AnimatedToast.Info(context, "Info", message, Gravity.BOTTOM, Toast.LENGTH_LONG, AnimatedToast.STYLE_DARK, AnimatedToast.ANIMATION_FLIP);
    }

    public static void showTastyWarningToast(Context context, String message) {
//        TastyToast.makeText(context, message, TastyToast.LENGTH_LONG, TastyToast.WARNING).show();
        AnimatedToast.Warning(context, "Warning", message, Gravity.BOTTOM, Toast.LENGTH_LONG, AnimatedToast.STYLE_DARK, AnimatedToast.ANIMATION_PULSE);
    }

    public static void showTastyDefaultToast(Context context, String message) {
        TastyToast.makeText(context, message, TastyToast.LENGTH_LONG, TastyToast.DEFAULT).show();
    }

    public static void showTastyConfusionToast(Context context, String message) {
        TastyToast.makeText(context, message, TastyToast.LENGTH_LONG, TastyToast.CONFUSING).show();
    }


}

