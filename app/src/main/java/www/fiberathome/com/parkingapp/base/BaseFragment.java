package www.fiberathome.com.parkingapp.base;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.fragment.app.Fragment;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.utils.DialogUtils;


public class BaseFragment extends Fragment {

    private ProgressDialog progressDialog;

    protected void showLoading(Context context) {
        progressDialog = DialogUtils.getInstance().progressDialog(context, getString(R.string.please_wait));
    }

    protected void hideLoading() {
        if (progressDialog == null) return;

        progressDialog.dismiss();
        progressDialog.cancel();
    }
}