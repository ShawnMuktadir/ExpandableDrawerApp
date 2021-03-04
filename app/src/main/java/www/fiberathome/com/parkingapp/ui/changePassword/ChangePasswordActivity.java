package www.fiberathome.com.parkingapp.ui.changePassword;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.model.api.ApiClient;
import www.fiberathome.com.parkingapp.model.api.ApiService;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.response.BaseResponse;
import www.fiberathome.com.parkingapp.model.user.User;
import www.fiberathome.com.parkingapp.ui.signIn.LoginActivity;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.Validator;

@SuppressLint("NonConstantResourceId")
public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        context = this;

        unbinder = ButterKnife.bind(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.changePasswordBtn:
                changePassword();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ChangePasswordActivity.super.onBackPressed();
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
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    private void changePassword() {

        if (checkFields()) {

            User user = Preferences.getInstance(context).getUser();

            String oldPassword = SharedData.getInstance().getOtp().trim();

            String newPassword = editTextNewPassword.getText().toString().trim();

            String confirmPassword = editTextConfirmPassword.getText().toString().trim();
            //String mobileNo = user.getMobileNo().trim();

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
                        showMessage(response.body().getMessage());
                        Timber.e("response -> %s", response.body().getMessage());
                        editTextNewPassword.setText("");
                        editTextConfirmPassword.setText("");
                        Preferences.getInstance(context).logout();
                        Intent intentLogout = new Intent(context, LoginActivity.class);
                        startActivity(intentLogout);
                        finish();
                    } else {
                        showMessage(response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable errors) {
                Timber.e("Throwable Errors: -> %s", errors.toString());
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private boolean checkPassWordAndConfirmPassword(String password, String confirmPassword) {
        boolean passStatus = false;
        if (confirmPassword != null && password != null) {
            if (password.equals(confirmPassword)) {
                passStatus = true;
            } else {
                Toast.makeText(context, context.getString(R.string.err_confirm_password), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, context.getString(R.string.err_old_password), Toast.LENGTH_SHORT).show();
            }
        }
        return passStatus;
    }
}
