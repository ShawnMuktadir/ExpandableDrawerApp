package www.fiberathome.com.parkingapp.ui.forgetPassword;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.model.api.ApiClient;
import www.fiberathome.com.parkingapp.model.api.ApiService;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.response.BaseResponse;
import www.fiberathome.com.parkingapp.ui.changePassword.ChangePasswordActivityForOTPNew;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.Validator;

@SuppressLint("NonConstantResourceId")
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
                            DialogUtils.getInstance().alertDialog(context,
                                    (Activity) context, context.getString(R.string.connect_to_internet),
                                    context.getString(R.string.retry),
                                    context.getString(R.string.close_app),
                                    new DialogUtils.DialogClickListener() {
                                        @Override
                                        public void onPositiveClick() {
                                            Timber.e("Positive Button clicked");
                                            if (ApplicationUtils.checkInternet(context)) {
                                                submitLogin();
                                            } else {
                                                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                                            }
                                        }

                                        @Override
                                        public void onNegativeClick() {
                                            Timber.e("Negative Button Clicked");
                                            if (context != null) {
                                                TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                                                context.finish();
                                            }
                                        }
                                    }).show();
                        }
                        return true;
                    }
                    return false;
                });

        btnForgetPassword.setOnClickListener(v -> {
            if (ApplicationUtils.checkInternet(context)) {
                submitLogin();
            } else {
                DialogUtils.getInstance().alertDialog(context,
                        (Activity) context, context.getString(R.string.connect_to_internet),
                        context.getString(R.string.retry), context.getString(R.string.close_app),
                        new DialogUtils.DialogClickListener() {
                            @Override
                            public void onPositiveClick() {
                                Timber.e("Positive Button clicked");
                                if (ApplicationUtils.checkInternet(context)) {
                                    submitLogin();
                                } else {
                                    TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                                }
                            }

                            @Override
                            public void onNegativeClick() {
                                Timber.e("Negative Button Clicked");
                                if (context != null) {
                                    TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                                    context.finish();
                                }
                            }
                        }).show();
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

        showLoading(context);

        ApiService service = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<BaseResponse> call = service.checkForgetPassword(mobileNo);

        // Gathering results.
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {

                Timber.e("response body-> %s", new Gson().toJson(response.body()));

                hideLoading();

                if (response.body() != null) {
                    if (!response.body().getError()) {
                        ApplicationUtils.showToastMessage(context, response.body().getMessage());
                    } else {
                        if (response.body().getMessage().equalsIgnoreCase("Try Again! Invalid Mobile Number.")) {
                            TastyToastUtils.showTastyErrorToast(context,
                                    context.getResources().getString(R.string.mobile_number_not_exist));
                        } else {
                            ApplicationUtils.showToastMessage(context, response.body().getMessage());
                            if (response.body().getError()) {

                                ApplicationUtils.showToastMessage(context, response.body().getMessage());

                                Intent intent = new Intent(context, ChangePasswordActivityForOTPNew.class);
                                intent.putExtra("mobile_no", mobileNo);
                                startActivity(intent);
                                SharedData.getInstance().setForgetPasswordMobile(mobileNo);
                                context.finish();
                            } else {
                                ApplicationUtils.showToastMessage(context, response.body().getMessage());
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable errors) {
                Timber.e("Throwable Errors: -> %s", errors.toString());
                hideLoading();
                ApplicationUtils.showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
            }
        });
    }
}
