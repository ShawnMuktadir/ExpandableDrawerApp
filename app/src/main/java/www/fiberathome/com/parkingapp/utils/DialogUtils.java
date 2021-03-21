package www.fiberathome.com.parkingapp.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import www.fiberathome.com.parkingapp.R;

public class DialogUtils {
    private static DialogUtils dialogUtils;

    public static DialogUtils getInstance() {
        if (dialogUtils == null) {
            dialogUtils = new DialogUtils();
        }

        return dialogUtils;
    }

    public interface DialogClickListener {
        void onPositiveClick();

        void onNegativeClick();
    }

    public AlertDialog alertDialog(Context context, final Activity activity, String title, String msg,
                                   String positiveButtonText, String negativeButtonText,
                                   DialogClickListener dialogClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);

        if (negativeButtonText != null) {
            builder.setNegativeButton(negativeButtonText, (dialog12, which) -> {
                dialog12.dismiss();
                dialogClickListener.onNegativeClick();
            });
        }
        builder.setPositiveButton(positiveButtonText, (dialog21, which) -> {
            dialog21.dismiss();
            dialogClickListener.onPositiveClick();
        });

        AlertDialog dialog = builder.create();
        //dialog.show();
        dialog.setOnShowListener(arg0 -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.black));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.red));
            //dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(context.getResources().getColor(R.color.black));
        });

        return dialog;
    }

    public AlertDialog alertDialog(Context context, final Activity activity, String msg,
                                   String positiveButtonText, String negativeButtonText,
                                   DialogClickListener dialogClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(msg);
        builder.setCancelable(false);

        if (negativeButtonText != null) {
            builder.setNegativeButton(negativeButtonText, (dialog12, which) -> {
                dialog12.dismiss();
                dialogClickListener.onNegativeClick();
            });
        }
        builder.setPositiveButton(positiveButtonText, (dialog21, which) -> {
            dialog21.dismiss();
            dialogClickListener.onPositiveClick();
        });

        AlertDialog dialog = builder.create();
        //dialog.show();
        dialog.setOnShowListener(arg0 -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.black));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.red));
            //dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(context.getResources().getColor(R.color.black));
        });

        return dialog;
    }

    public ProgressDialog progressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();

        return progressDialog;
    }
}
