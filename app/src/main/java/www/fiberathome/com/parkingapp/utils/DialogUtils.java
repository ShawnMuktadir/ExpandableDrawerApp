package www.fiberathome.com.parkingapp.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.afollestad.materialdialogs.MaterialDialog;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.ui.booking.newBooking.BookingActivity;
import www.fiberathome.com.parkingapp.ui.widget.BaseBottomSheetDialog;

@SuppressWarnings("unused")
public class DialogUtils {
    private static DialogUtils dialogUtils;

    public static DialogUtils getInstance() {
        if (dialogUtils == null) {
            dialogUtils = new DialogUtils();
        }

        return dialogUtils;
    }

    public void alertDialog(BookingActivity context, Activity context1, String string, BookingActivity context2, String string1, String string2, Object positive_button_clicked, Object negative_button_clicked) {
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

    public void showMessageDialog(String message, Context context) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message);
            builder.setCancelable(true);
            builder.setPositiveButton(context.getResources().getString(R.string.get_support),
                    (dialog, which) -> DialogUtils.getInstance().showAlertDialog(context.getString(R.string.number),
                            context, context.getString(R.string.call),
                            context.getString(R.string.cancel),
                            (dialog1, which1) -> {
                                Timber.e("Positive Button clicked");
                                String number = context.getString(R.string.number);
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_DIAL); // Action for what intent called for
                                intent.setData(Uri.parse("tel: " + number)); // Datum with intent respective action on intent
                                context.startActivity(intent);
                                dialog1.dismiss();
                            },

                            (dialog1, which1) -> {
                                Timber.e("Negative Button Clicked");
                                dialog1.dismiss();
                            }));
            builder.setNegativeButton(context.getResources().getString(R.string.ok), (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            // Let's start with animation work. We just need to create a style and use it here as follows.
            /*if (alertDialog.getWindow() != null)
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.slidingDialogAnimation;*/

        }
    }

    public void showOnlyMessageDialog(String message, Context context) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message);
            builder.setCancelable(true);
            builder.setPositiveButton(context.getResources().getString(R.string.ok), (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            // Let's start with animation work. We just need to create a style and use it here as follows.
            /*if (alertDialog.getWindow() != null)
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.slidingDialogAnimation;*/

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void showExitDialog(final Activity activity) {
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(activity, R.style.Theme_AppCompat_NoActionBar);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.exit_alert, null);
        dialogBuilder.setView(dialogView);
        TextView tv_exit = dialogView.findViewById(R.id.tv_exit);
        View outside_view = dialogView.findViewById(R.id.outside_view);
        final android.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCancelable(false);
        alertDialog.show();

       /* WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
        lp.dimAmount = 0.4f;
        alertDialog.getWindow().setAttributes(lp);
        alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);*/
        outside_view.setOnTouchListener((View view, MotionEvent motionEvent) -> {
            alertDialog.dismiss();

            return false;
        });

        tv_exit.setOnClickListener(v -> {
            alertDialog.dismiss();
            activity.finish();
            TastyToastUtils.showTastySuccessToast(activity, activity.getResources().getString(R.string.thanks_message));
        });

    }

    public BaseBottomSheetDialog bottomSheetDialog(
            Context context,
            View view,
            boolean cancellable,
            boolean cancellableOnOutsideTouch
    ) {
        BaseBottomSheetDialog baseBottomSheetDialog = new BaseBottomSheetDialog(context);

        baseBottomSheetDialog.setContentView(view);
        baseBottomSheetDialog.setCancelable(cancellable);
        baseBottomSheetDialog.setCanceledOnTouchOutside(cancellableOnOutsideTouch);
        baseBottomSheetDialog.show();

        return baseBottomSheetDialog;
    }

    public MaterialDialog showMaterialCircularProgressDialog(Context callingClassContext, String content) {
        return new MaterialDialog.Builder(callingClassContext)
                .content(content)
                .progress(true, 100)
                .cancelable(false)
                .show();
    }

    public void showAlertDialog(String message, Context context, String positiveText, String negativeText,
                                DialogInterface.OnClickListener positiveCallback, DialogInterface.OnClickListener negativeCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setPositiveButton(positiveText, positiveCallback);
        if (!TextUtils.isEmpty(negativeText)) {
            builder.setNegativeButton(negativeText, negativeCallback);
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(arg0 -> alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.red)));
        alertDialog.setCancelable(false);
        if (!((Activity) context).isFinishing()) {
            alertDialog.show();
        }
    }


    public void showAlertDialog(String title, String message, Context context, String positiveText, String negativeText,
                                DialogInterface.OnClickListener positiveCallback, DialogInterface.OnClickListener negativeCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setPositiveButton(positiveText, positiveCallback);
        if (!TextUtils.isEmpty(negativeText)) {
            builder.setNegativeButton(negativeText, negativeCallback);
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(arg0 -> alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.red)));
        alertDialog.setCancelable(false);

        if (!((Activity) context).isFinishing()) {
            alertDialog.show();
        }
    }
}
