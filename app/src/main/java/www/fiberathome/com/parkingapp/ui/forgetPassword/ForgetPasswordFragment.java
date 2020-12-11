package www.fiberathome.com.parkingapp.ui.forgetPassword;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.ui.changePassword.ChangePasswordActivityForOTP;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.HttpsTrustManager;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.Validator;

public class ForgetPasswordFragment extends BaseFragment {

    private static String TAG = ForgetPasswordActivity.class.getSimpleName();

    @BindView(R.id.textInputLayoutMobile)
    TextInputLayout textInputLayoutMobile;
    @BindView(R.id.editTextMobileNumber)
    EditText editTextMobileNumber;
    @BindView(R.id.btnForgetPassword)
    Button btnForgetPassword;

    private Unbinder unbinder;

    private ForgetPasswordActivity context;
    private ProgressDialog progressDialog;

    public ForgetPasswordFragment() {
        // Required empty public constructor
    }

    public static ForgetPasswordFragment newInstance() {
        return new ForgetPasswordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forget_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder = ButterKnife.bind(this, view);
        context = (ForgetPasswordActivity) getActivity();

        editTextMobileNumber.requestFocus();
        editTextMobileNumber.requestLayout();

        setListener();
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    private void setListener() {

        Objects.requireNonNull(textInputLayoutMobile.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    textInputLayoutMobile.setErrorEnabled(true);
                    textInputLayoutMobile.setError(context.getString(R.string.err_msg_mobile));
                }

                if (s.length() > 0) {
                    textInputLayoutMobile.setError(null);
                    textInputLayoutMobile.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextMobileNumber.setOnEditorActionListener(
                (v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || event.getAction() == KeyEvent.ACTION_DOWN
                            && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        if (ApplicationUtils.checkInternet(context)) {
                            submitLogin();
                        } else {
                            ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app),
                                    (dialog, which) -> {
                                Timber.e("Positive Button clicked");
                                if (ApplicationUtils.checkInternet(context)) {
                                    submitLogin();
                                } else {
                                    TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                                }
                            },
                                    (dialog, which) -> {
                                Timber.e("Negative Button Clicked");
                                dialog.dismiss();
                                if (context != null) {
                                    TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                                    context.finish();
                                }
                            });
                        }
                        return true;
                    }
                    return false;
                });

        btnForgetPassword.setOnClickListener(v -> {
            if (ApplicationUtils.checkInternet(context)) {
                submitLogin();
            } else {
                ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
                    Timber.e("Positive Button clicked");
                    if (ApplicationUtils.checkInternet(context)) {
                        submitLogin();
                    } else {
                        TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                    }
                }, (dialog, which) -> {
                    Timber.e("Negative Button Clicked");
                    dialog.dismiss();
                    if (context != null) {
                        TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                        context.finish();
                    }
                });
            }
        });
    }

    private boolean checkFields() {
        boolean isPhoneValid = Validator.checkValidity(textInputLayoutMobile, editTextMobileNumber.getText().toString(), context.getString(R.string.err_msg_mobile), "phone");
        return isPhoneValid;

    }

    private void submitLogin() {
        if (checkFields()) {
            String mobileNo = editTextMobileNumber.getText().toString().trim();
            checkForgetPassword(mobileNo);
        }
    }

    private void checkForgetPassword(final String mobileNo) {

        //progressDialog = ApplicationUtils.progressDialog(context, "Please wait...");
        showLoading(context);
        HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_FORGET_PASSWORD, response -> {
            // remove the progress bar
            Timber.e("URL -> %s", new Gson().toJson(AppConfig.URL_FORGET_PASSWORD));
            //progressDialog.dismiss();
            hideLoading();
            if (response.equals("[]")) {
                TastyToastUtils.showTastyErrorToast(context, context.getResources().getString(R.string.mobile_number_not_exist));
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    Timber.e("jsonObject -> %s", jsonObject.toString());

                    if (jsonObject.getBoolean("error")) {

                        //progressDialog.dismiss();
                        showMessage(jsonObject.getString("message"));

                        Intent intent = new Intent(context, ChangePasswordActivityForOTP.class);
                        intent.putExtra("mobile_no", mobileNo);
                        startActivity(intent);
                        context.finish();
                    } else {
                        showMessage(jsonObject.getString("message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, error -> {
            Timber.e("Error Message -> %s ", error.getMessage());
            //if (progressDialog != null) progressDialog.dismiss();
            showMessage(error.getMessage());
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mobile_no", mobileNo);
                SharedData.getInstance().setForgetPasswordMobile(mobileNo);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ParkingApp.getInstance().addToRequestQueue(stringRequest, TAG);
    }

    private void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /*@Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ForgetPasswordActivity.super.onBackPressed();
                        TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                    }
                }).create();
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
            }
        });
        dialog.show();
    }*/
}
