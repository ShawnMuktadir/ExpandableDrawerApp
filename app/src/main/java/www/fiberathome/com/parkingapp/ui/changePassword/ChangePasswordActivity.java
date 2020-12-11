package www.fiberathome.com.parkingapp.ui.changePassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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

import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.model.api.ApiClient;
import www.fiberathome.com.parkingapp.model.api.ApiService;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.data.preference.SharedPreManager;
import www.fiberathome.com.parkingapp.model.loginUser.User;
import www.fiberathome.com.parkingapp.model.response.common.CommonResponse;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.HttpsTrustManager;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.Validator;
import www.fiberathome.com.parkingapp.ui.signIn.LoginActivity;

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ChangePasswordActivity.class.getSimpleName();
    private TextInputLayout textInputLayoutOldPassword;
    private TextInputLayout textInputLayoutNewPassword;
    private TextInputLayout textInputLayoutConfirmPassword;

    private EditText editTextOldPassword;
    private EditText editTextNewPassword;
    private EditText editTextConfirmPassword;

    private Button changePasswordBtn;
    private Context context;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        context = this;
        initUI();
    }

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

    private void initUI() {
        editTextOldPassword = findViewById(R.id.editTextOldPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);

        textInputLayoutOldPassword = findViewById(R.id.textInputLayoutOldPassword);
        textInputLayoutNewPassword = findViewById(R.id.textInputLayoutNewPassword);
        textInputLayoutConfirmPassword = findViewById(R.id.textInputLayoutConfirmPassword);

        changePasswordBtn.setOnClickListener(this);
        editTextOldPassword.addTextChangedListener(new MyTextWatcher(editTextOldPassword));

    }

    private void changePassword() {

        /**
         * TODO : CHECK WHETHER PASSWORD MATCH WITH SERVER PASSWORD
         * 1. if matched, then check the new password and confirmation password
         * 2. if not matched, then show error the status
         * 3. check number of times user tried to change password.
         * ================================================================================
         */
        if (checkFields()) {
            // IF PASSWORD IS VALID, MOVE TO SERVER WITH user request.

            // COLLECT OLD PASSWORD | NEW PASSWORD | CONFIRMATION PASSWORD

            User user = SharedPreManager.getInstance(context).getUser();
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
        /*editTextOldPassword.requestFocus();
        boolean isOldPasswordValid = Validator.checkValidity(textInputLayoutOldPassword, editTextOldPassword.getText().toString(), context.getString(R.string.err_old_password), "textPassword");*/
        boolean isNewPasswordValid = Validator.checkValidity(textInputLayoutNewPassword, editTextNewPassword.getText().toString(), context.getString(R.string.err_new_password), "textPassword");
        boolean isConfirmPasswordValid = Validator.checkValidity(textInputLayoutConfirmPassword, editTextConfirmPassword.getText().toString(), context.getString(R.string.err_confirm_password), "textPassword");
        return isNewPasswordValid && isConfirmPasswordValid;
    }

    private boolean validatePassword() {
        String userPassword;
        String newPassword;
        /*User user = SharedPreManager.getInstance(getContext()).getUser();
        if (SharedData.getInstance().getPassword() != null) {
            userPassword = SharedData.getInstance().getPassword();
            Timber.e("userPassword -> %s", userPassword);
            String oldPassword = editTextOldPassword.getText().toString().trim();
            checkUserPasswordAndOldPasswordField(userPassword, oldPassword);
        }*/

        newPassword = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        if (SharedData.getInstance().getPassword()!=null){
            SharedData.getInstance().setPassword(newPassword);
        }

        checkPassWordAndConfirmPassword(newPassword, confirmPassword);
        return true;
    }

    private void updatePassword(String newPassword, String confirmPassword, String mobileNo) {

        progressDialog = ApplicationUtils.progressDialog(context, context.getResources().getString(R.string.please_wait));

        HttpsTrustManager.allowAllSSL();

        // Changing Password through UI Service.
        ApiService service = ApiClient.getRetrofitInstance(AppConfig.URL_CHANGE_PASSWORD_OTP).create(ApiService.class);
        Call<CommonResponse> passwordUpgradeCall = service.forgetPassword(newPassword, confirmPassword, mobileNo);

        // Gathering results.
        passwordUpgradeCall.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(@NonNull Call<CommonResponse> call, @NonNull Response<CommonResponse> response) {
                Timber.e("response -> %s", response.message());

                progressDialog.dismiss();
                if (response.body() != null) {
                    if (!response.body().getError()) {
                        showMessage(response.body().getMessage());
                        Timber.e("response -> %s",response.body().getMessage());
                        //editTextOldPassword.setText("");
                        editTextNewPassword.setText("");
                        editTextConfirmPassword.setText("");
                        SharedPreManager.getInstance(context).logout();
                        Intent intentLogout = new Intent(context, LoginActivity.class);
                        startActivity(intentLogout);
                        finish();
                    } else {
                        showMessage(response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommonResponse> call, @NonNull Throwable errors) {
                Timber.e("Throwable Errors: -> %s", errors.toString());
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private static class MyTextWatcher implements TextWatcher {

        private View view;

        public MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.editTextOldPassword:
                    //validateEditText(editTextOldPassword, textInputLayoutOldPassword, R.string.err_old_password);
                    break;

                case R.id.editTextNewPassword:
                    //validateEditText(editTextNewPassword, textInputLayoutNewPassword, R.string.err_new_password);
                    break;

                case R.id.editTextConfirmPassword:
                    //validateEditText(editTextConfirmPassword, textInputLayoutConfirmPassword, R.string.err_confirm_password);
                    break;

            }
        }
    }

    private boolean validateEditText(EditText editText, TextInputLayout textInputLayout, int errorResource) {
        String value = editText.getText().toString().trim();
        if (value.isEmpty()) {
            textInputLayout.setError(getResources().getString(errorResource));
            requestFocus(editText);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(EditText view) {
        if (view.requestFocus()) {
            if (context != null)
                ChangePasswordActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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
