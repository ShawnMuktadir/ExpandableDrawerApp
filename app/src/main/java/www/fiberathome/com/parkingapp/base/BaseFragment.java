package www.fiberathome.com.parkingapp.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.utils.DialogUtils;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class BaseFragment extends Fragment {

    private ProgressDialog progressDialog;

    protected void showLoading(Context context) {
        progressDialog = DialogUtils.getInstance().progressDialog(context, context.getResources().getString(R.string.please_wait));
    }

    protected void showLoading(Context context, String message) {
        progressDialog = DialogUtils.getInstance().progressDialog(context, message);
    }

    protected void hideLoading() {
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