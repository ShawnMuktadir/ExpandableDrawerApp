package www.fiberathome.com.parkingapp.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

public class DialogUtil {
    private static DialogUtil dialogUtil;

    public static DialogUtil getInstance() {
        if (dialogUtil == null) {
            dialogUtil = new DialogUtil();
        }

        return dialogUtil;
    }

    public interface DialogClickListener {
        void onPositiveClick();

        void onNegativeClick();
    }

    public AlertDialog alertDialog(final Activity activity, String title, String msg,
                                   String positiveButtonText, String negativeButtonText,
                                   DialogClickListener dialogClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        if (negativeButtonText != null) {
            builder.setNegativeButton(negativeButtonText, (dialog, which) -> {
                dialog.dismiss();
                dialogClickListener.onNegativeClick();
            });
        }
        builder.setPositiveButton(positiveButtonText, (dialog, which) -> {
            dialog.dismiss();
            dialogClickListener.onPositiveClick();
        });
        return builder.create();
    }

    public ProgressDialog progressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();

        return progressDialog;
    }
}

