package www.fiberathome.com.parkingapp.utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import www.fiberathome.com.parkingapp.R;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class SnackBarUtils {
    private static SnackBarUtils snackBarUtils;

    public static SnackBarUtils getInstance() {
        if (snackBarUtils == null) {
            snackBarUtils = new SnackBarUtils();
        }

        return snackBarUtils;
    }

    public void showSuccessSnackBar(Context context, View view, String message, String actionDismissText) {
        view.setEnabled(false);
        Snackbar snack = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View sbview = snack.getView();
        sbview.setBackgroundColor(ContextCompat.getColor(context, R.color.glossy_green));
        TextView textView = sbview.findViewById(R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.glossy_white));
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snack.setAction(actionDismissText, v -> {
        });

        snack.show();
    }

    public void showWarningSnackBar(Context context, View view, String s, String actionDismissText) {
        view.setEnabled(false);
        Snackbar snack = Snackbar.make(view, s, Snackbar.LENGTH_LONG);
        View sbview = snack.getView();
        sbview.setBackgroundColor(ContextCompat.getColor(context, R.color.quantum_orange));
        TextView textView = sbview.findViewById(R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.white));
        snack.setAction(actionDismissText, v -> {
        });
        snack.show();
    }

    public void showErrorSnackBar(Context context, View view, String s, String actionDismissText) {
        view.setEnabled(false);
        Snackbar snack = Snackbar.make(view, s, Snackbar.LENGTH_LONG);
        View sbview = snack.getView();
        sbview.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
        TextView textView = sbview.findViewById(R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.white));
        snack.setAction(actionDismissText, v -> {
        });
        snack.show();
    }
}
