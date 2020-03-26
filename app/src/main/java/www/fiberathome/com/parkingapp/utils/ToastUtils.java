package www.fiberathome.com.parkingapp.utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import www.fiberathome.com.parkingapp.R;


public class ToastUtils {

    public static void showSuccessToast(Context context, CharSequence message, int duration) {
        Toast toast = Toast.makeText(context, message, duration);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.rect_green_selected_bg_rounded);
        toastView.setPadding(3,3,3,3);
        TextView text = toastView.findViewById(android.R.id.message);
        text.setTextColor(context.getResources().getColor(R.color.white));
        toast.show();
    }

    public static void showErrorToast(Context context, CharSequence message, int duration) {
        Toast toast = Toast.makeText(context, message, duration);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.rect_red_bg_rounded);
        toastView.setPadding(3,3,3,3);
        TextView text = toastView.findViewById(android.R.id.message);
        text.setTextColor(context.getResources().getColor(R.color.white));
        toast.show();
    }
}

