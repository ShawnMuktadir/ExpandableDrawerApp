package www.fiberathome.com.parkingapp.base;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.fragment.app.Fragment;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.utils.DialogUtils;

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
            if ((progressDialog != null) && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog.cancel();
            }
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
            e.getCause();
        } catch (final Exception e) {
            // Handle or log or ignore
            e.getCause();
        } finally {
            progressDialog = null;
            Timber.e("progressDialog finally block called");
        }

        /*progressDialog.dismiss();
        progressDialog.cancel();*/
    }
}