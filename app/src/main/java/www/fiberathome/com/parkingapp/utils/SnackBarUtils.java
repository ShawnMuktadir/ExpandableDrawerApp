package www.fiberathome.com.parkingapp.utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import www.fiberathome.com.parkingapp.R;

public class SnackBarUtils {
    private static SnackBarUtils snackBarUtils;

    public static SnackBarUtils getInstance() {
        if (snackBarUtils == null) {
            snackBarUtils = new SnackBarUtils();
        }

        return snackBarUtils;
    }

    public void showSuccessSnackBar(Context context, View view, String s) {
        view.setEnabled(false);
        Snackbar snack = Snackbar.make(view, s, Snackbar.LENGTH_LONG);
        View sbview = snack.getView();
        sbview.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
        TextView textView = (TextView) sbview.findViewById(R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.white));
        snack.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        snack.show();
    }

    public void showWarningSnackBar(Context context, View view, String s) {
        view.setEnabled(false);
        Snackbar snack = Snackbar.make(view, s, Snackbar.LENGTH_LONG);
        View sbview = snack.getView();
        sbview.setBackgroundColor(ContextCompat.getColor(context, R.color.quantum_orange));
        TextView textView = (TextView) sbview.findViewById(R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.white));
        snack.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        snack.show();
    }

    public void showErrorSnackBar(Context context, View view, String s) {
        view.setEnabled(false);
        Snackbar snack = Snackbar.make(view, s, Snackbar.LENGTH_LONG);
        View sbview = snack.getView();
        sbview.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
        TextView textView = (TextView) sbview.findViewById(R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.white));
        snack.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        snack.show();
    }
}