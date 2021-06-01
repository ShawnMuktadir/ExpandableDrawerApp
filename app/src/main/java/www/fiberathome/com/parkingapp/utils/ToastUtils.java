package www.fiberathome.com.parkingapp.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import www.fiberathome.com.parkingapp.R;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class ToastUtils {

    private static ToastUtils toastUtils;

    public static ToastUtils getInstance() {
        if (toastUtils == null) {
            toastUtils = new ToastUtils();
        }

        return toastUtils;
    }

    public void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void showToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public void showToastWithDelay(Context context, String message, long countDown) {
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

    public void showSuccessToast(Context context, CharSequence message, int duration) {
        Toast toast = Toast.makeText(context, message, duration);
        View toastView = toast.getView();
        assert toastView != null;
        toastView.setBackgroundResource(R.drawable.rect_green_selected_bg_rounded);
        toastView.setPadding(3, 3, 3, 3);
        TextView text = toastView.findViewById(android.R.id.message);
        text.setTextColor(context.getResources().getColor(R.color.white));
        toast.show();
    }

    public void showErrorToast(Context context, CharSequence message, int duration) {
        Toast toast = Toast.makeText(context, message, duration);
        View toastView = toast.getView();
        assert toastView != null;
        toastView.setBackgroundResource(R.drawable.rect_red_bg_rounded);
        toastView.setPadding(3, 3, 3, 3);
        TextView text = toastView.findViewById(android.R.id.message);
        text.setTextColor(context.getResources().getColor(R.color.white));
        toast.show();
    }
}

