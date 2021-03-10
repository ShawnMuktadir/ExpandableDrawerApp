package www.fiberathome.com.parkingapp.ui.changePassword;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.response.BaseResponse;
import www.fiberathome.com.parkingapp.model.user.User;
import www.fiberathome.com.parkingapp.ui.signIn.LoginActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.Validator;

@SuppressLint("NonConstantResourceId")
public class ChangePasswordFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = ChangePasswordFragment.class.getSimpleName();

    @BindView(R.id.textInputLayoutOldPassword)
    TextInputLayout textInputLayoutOldPassword;

    @BindView(R.id.editTextOldPassword)
    EditText editTextOldPassword;

    @BindView(R.id.textInputLayoutNewPassword)
    TextInputLayout textInputLayoutNewPassword;

    @BindView(R.id.editTextNewPassword)
    EditText editTextNewPassword;

    @BindView(R.id.textInputLayoutConfirmPassword)
    TextInputLayout textInputLayoutConfirmPassword;

    @BindView(R.id.editTextConfirmPassword)
    EditText editTextConfirmPassword;

    @BindView(R.id.changePasswordBtn)
    Button changePasswordBtn;

    private Context context;

    private Unbinder unbinder;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    public static ChangePasswordFragment newInstance() {
        return new ChangePasswordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        context = getActivity();
        if (getActivity() != null)
            getActivity().setTitle(R.string.title_change_password);

        changePasswordBtn.setOnClickListener(this);

        setListeners();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.e("onDestroyView called ");
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.changePasswordBtn:
                if (ApplicationUtils.checkInternet(context)) {
                    changePassword();
                } else {
                    ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
                        Timber.e("Positive Button clicked");
                        if (ApplicationUtils.checkInternet(context)) {
                            changePassword();
                        } else {
                            TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                        }
                    }, (dialog, which) -> {
                        Timber.e("Negative Button Clicked");
                        dialog.dismiss();
                        if (getActivity() != null) {
                            getActivity().finish();
                            TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                        }
                    });
                }
                break;
        }
    }

    private void setListeners() {
        Objects.requireNonNull(textInputLayoutOldPassword.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    textInputLayoutOldPassword.setErrorEnabled(true);
                    textInputLayoutOldPassword.setError(context.getString(R.string.err_old_password));
                }

                if (s.length() > 0) {
                    textInputLayoutOldPassword.setError(null);
                    textInputLayoutOldPassword.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Objects.requireNonNull(textInputLayoutNewPassword.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    textInputLayoutNewPassword.setErrorEnabled(true);
                    textInputLayoutNewPassword.setError(context.getString(R.string.err_new_password));
                }

                if (s.length() > 0) {
                    textInputLayoutNewPassword.setError(null);
                    textInputLayoutNewPassword.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Objects.requireNonNull(textInputLayoutConfirmPassword.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    textInputLayoutConfirmPassword.setErrorEnabled(true);
                    textInputLayoutConfirmPassword.setError(context.getString(R.string.err_confirm_password));
                }

                if (s.length() > 0) {
                    textInputLayoutConfirmPassword.setError(null);
                    textInputLayoutConfirmPassword.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void changePassword() {

        if (checkFields()) {
            User user = Preferences.getInstance(context).getUser();
            String oldPassword = editTextOldPassword.getText().toString().trim();
            String newPassword = editTextNewPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPassword.getText().toString().trim();
            String mobileNo = user.getMobileNo().trim();

            if (validatePassword()) {
                updatePassword(oldPassword, newPassword, confirmPassword, mobileNo);
            }
        }
    }

    private boolean checkFields() {
        boolean isOldPasswordValid = Validator.checkValidity(textInputLayoutOldPassword, editTextOldPassword.getText().toString(), context.getString(R.string.err_old_password), "textPassword");
        boolean isNewPasswordValid = Validator.checkValidity(textInputLayoutNewPassword, editTextNewPassword.getText().toString(), context.getString(R.string.err_new_password), "textPassword");
        boolean isConfirmPasswordValid = Validator.checkValidity(textInputLayoutConfirmPassword, editTextConfirmPassword.getText().toString(), context.getString(R.string.err_confirm_password), "textPassword");
        return isOldPasswordValid && isNewPasswordValid && isConfirmPasswordValid;
    }

    private boolean validatePassword() {
        String userPassword;
        String newPassword;
        //User user = SharedPreManager.getInstance(getContext()).getUser();
        if (SharedData.getInstance().getPassword() != null) {
            userPassword = SharedData.getInstance().getPassword();
            Timber.e("userPassword -> %s", userPassword);
            String oldPassword = editTextOldPassword.getText().toString().trim();
            checkUserPasswordAndOldPasswordField(userPassword, oldPassword);
        }

        newPassword = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        if (SharedData.getInstance().getPassword() != null) {
            SharedData.getInstance().setPassword(newPassword);
        }

        checkPassWordAndConfirmPassword(newPassword, confirmPassword);
        return true;
    }

    private void updatePassword(String oldPassword, String newPassword, String confirmPassword, String mobileNo) {

        showLoading(context);

        // Changing Password through UI Service.
        ApiService service = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<BaseResponse> passwordUpgradeCall = service.updatePassword(oldPassword, newPassword, confirmPassword, mobileNo);

        // Gathering results.
        passwordUpgradeCall.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {

                Timber.e("change password response body-> %s", new Gson().toJson(response.body()));

                hideLoading();

                if (response.body() != null) {
                    if (!response.body().getError()) {
                        ApplicationUtils.showToastMessage(context, response.body().getMessage());

                        Preferences.getInstance(context).logout();
                        Intent intentLogout = new Intent(context, LoginActivity.class);
                        startActivity(intentLogout);
                        if (getActivity() != null) {
                            getActivity().finishAffinity();
                        }
                    } else {
                        ApplicationUtils.showToastMessage(context, response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable errors) {
                Timber.e("Throwable Errors: -> %s", errors.toString());
            }
        });
    }

    private boolean checkPassWordAndConfirmPassword(String password, String confirmPassword) {
        boolean passStatus = false;
        if (confirmPassword != null && password != null) {
            if (password.equals(confirmPassword)) {
                passStatus = true;
            } else {
                ApplicationUtils.showToastMessage(context, context.getString(R.string.err_confirm_password));
            }
        }
        return passStatus;
    }

    private boolean checkUserPasswordAndOldPasswordField(String password, String oldPassword) {

        boolean passStatus = false;
        if (password != null && oldPassword != null) {
            if (password.equals(oldPassword)) {
                passStatus = true;
            } else {
                ApplicationUtils.showToastMessage(context, context.getString(R.string.err_old_password));
            }
        }
        return passStatus;
    }
}