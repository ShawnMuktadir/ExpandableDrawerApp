package www.fiberathome.com.parkingapp.utils;

import android.content.Context;

import com.sdsmdg.tastytoast.TastyToast;

public class TastyToastUtils {
    public static void showTastySuccessToast(Context context, String message){
        TastyToast.makeText(context,message, com.sdsmdg.tastytoast.TastyToast.LENGTH_LONG, com.sdsmdg.tastytoast.TastyToast.SUCCESS).show();
    }

    public static void showTastyErrorToast(Context context, String message){
        TastyToast.makeText(context,message, com.sdsmdg.tastytoast.TastyToast.LENGTH_LONG, com.sdsmdg.tastytoast.TastyToast.ERROR).show();
    }
    public static void showTastyInfoToast(Context context, String message){
        TastyToast.makeText(context,message, com.sdsmdg.tastytoast.TastyToast.LENGTH_LONG, com.sdsmdg.tastytoast.TastyToast.INFO).show();
    }

    public static void showTastyWarningToast(Context context, String message){
        //TastyToastUtils.makeText(context,message,TastyToastUtils.LENGTH_LONG,TastyToastUtils.WARNING).show();
        TastyToast.makeText(context,message, com.sdsmdg.tastytoast.TastyToast.LENGTH_LONG, com.sdsmdg.tastytoast.TastyToast.WARNING).show();
    }


}

