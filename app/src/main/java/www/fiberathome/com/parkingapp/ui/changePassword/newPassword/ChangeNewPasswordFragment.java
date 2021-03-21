package www.fiberathome.com.parkingapp.ui.changePassword.newPassword;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputLayout;

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
import www.fiberathome.com.parkingapp.utils.Validator;

@SuppressLint("NonConstantResourceId")
public class ChangeNewPasswordFragment extends BaseFragment {

    private static final String TAG = ChangeNewPasswordFragment.class.getSimpleName();

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
    AppCompatButton changePasswordBtn;

    private ChangeNewPasswordActivity context;

    private Unbinder unbinder;

    public ChangeNewPasswordFragment() {
        // Required empty public constructor
    }

    public static ChangeNewPasswordFragment newInstance() {
        return new ChangeNewPasswordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_new_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder = ButterKnife.bind(this, view);

        context =  (ChangeNewPasswordActivity) getActivity();

        setListeners();
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    private void setListeners() {
        changePasswordBtn.setOnClickListener(v -> setNewPassword());
    }

    private void setNewPassword() {

        if (checkFields()) {

            User user = Preferences.getInstance(context).getUser();

            String oldPassword = SharedData.getInstance().getOtp().trim();

            String newPassword = editTextNewPassword.getText().toString().trim();

            String confirmPassword = editTextConfirmPassword.getText().toString().trim();

            String mobileNo = SharedData.getInstance().getForgetPasswordMobile();

            if (validatePassword()) {
                updatePassword(newPassword, confirmPassword, mobileNo);
            }
        }
    }

    private boolean checkFields() {
        boolean isNewPasswordValid = Validator.checkValidity(textInputLayoutNewPassword, editTextNewPassword.getText().toString(), context.getString(R.string.err_new_password), "textPassword");
        boolean isConfirmPasswordValid = Validator.checkValidity(textInputLayoutConfirmPassword, editTextConfirmPassword.getText().toString(), context.getString(R.string.err_confirm_password), "textPassword");
        return isNewPasswordValid && isConfirmPasswordValid;
    }

    private boolean validatePassword() {
        String userPassword;
        String newPassword;

        newPassword = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        if (SharedData.getInstance().getPassword() != null) {
            SharedData.getInstance().setPassword(newPassword);
        }

        checkPassWordAndConfirmPassword(newPassword, confirmPassword);
        return true;
    }

    private void updatePassword(String newPassword, String confirmPassword, String mobileNo) {

        showLoading(context);

        ApiService service = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<BaseResponse> call = service.setPasswordForForgetPassword(newPassword, confirmPassword, mobileNo);

        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                Timber.e("response -> %s", response.message());

                hideLoading();
                if (response.body() != null) {
                    if (!response.body().getError()) {
                        ApplicationUtils.showToastMessage(context, response.body().getMessage());
                        Timber.e("response -> %s", response.body().getMessage());
                        editTextNewPassword.setText("");
                        editTextConfirmPassword.setText("");
                        Preferences.getInstance(context).logout();
                        Intent intentLogout = new Intent(context, LoginActivity.class);
                        startActivity(intentLogout);
                        context.finish();
                    } else {
                        ApplicationUtils.showToastMessage(context, response.body().getMessage());
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