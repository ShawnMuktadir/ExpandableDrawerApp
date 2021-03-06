package www.fiberathome.com.parkingapp.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import androidx.fragment.app.Fragment;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.utils.DialogUtils;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class BaseFragment extends Fragment {

    private ProgressDialog progressDialog;
    private boolean isLoadingHidden;

    protected void showLoading(Context context) {
        try {
            isLoadingHidden = false;
            progressDialog = DialogUtils.getInstance().progressDialog(context, context.getResources().getString(R.string.please_wait));
        } catch (final IllegalArgumentException e) {
            e.getCause();
        }
    }

    private void forceDismissLoading(Context context) {
        new Handler().postDelayed(() -> {
            if (!isLoadingHidden) {
                hideLoading();
            }
        }, 60000);
    }

    protected void showLoading(Context context, String message) {
        try {
            isLoadingHidden = false;
            progressDialog = DialogUtils.getInstance().progressDialog(context, message);
        } catch (final IllegalArgumentException e) {
            e.getCause();
        }
    }

    protected void hideLoading() {
        isLoadingHidden = true;
        if (progressDialog == null) return;

        try {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog.cancel();
            }
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
            e.getCause();
        } finally {
            progressDialog = null;
            Timber.e("progressDialog finally block called");
        }
    }

    @SuppressWarnings("rawtypes")
    public void startActivity(BaseActivity context, Class activityClass) {
        startActivity(new Intent(context, activityClass));
    }

    @SuppressWarnings("rawtypes")
    public void startActivityWithFinish(Activity context, Class activityClass) {
        startActivity(new Intent(context, activityClass));
        context.finish();
    }

    public void startActivityWithFinish(BaseActivity context, Class activityClass) {
        startActivity(new Intent(context, activityClass));
        context.finish();
    }

    @SuppressWarnings("rawtypes")
    public void startActivityWithFinishAffinity(BaseActivity context, Class activityClass) {
        startActivity(new Intent(context, activityClass));
        context.finishAffinity();
    }
}