package www.fiberathome.com.parkingapp.view.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputLayout;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.api.ApiClient;
import www.fiberathome.com.parkingapp.api.ApiService;
import www.fiberathome.com.parkingapp.base.AppConfig;
import www.fiberathome.com.parkingapp.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.User;
import www.fiberathome.com.parkingapp.model.common.RetrofitCommon;
import www.fiberathome.com.parkingapp.data.preference.SharedPreManager;
import www.fiberathome.com.parkingapp.utils.Validator;
import www.fiberathome.com.parkingapp.view.activity.login.LoginActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ChangePasswordFragment.class.getSimpleName();
    private TextInputLayout textInputLayoutOldPassword;
    private TextInputLayout textInputLayoutNewPassword;
    private TextInputLayout textInputLayoutConfirmPassword;

    private EditText editTextOldPassword;
    private EditText editTextNewPassword;
    private EditText editTextConfirmPassword;

    private Button changePasswordBtn;
    private Context context;
    private ProgressDialog progressDialog;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        context = getActivity();
        if (getActivity() != null)
            getActivity().setTitle(R.string.title_change_password);

        initUI(view);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.change_password_btn:
                changePassword();
                break;
        }
    }

    private void initUI(View view) {
        editTextOldPassword = view.findViewById(R.id.input_old_password);
        editTextNewPassword = view.findViewById(R.id.input_new_password);
        editTextConfirmPassword = view.findViewById(R.id.input_confirm_password);
        changePasswordBtn = view.findViewById(R.id.change_password_btn);

        textInputLayoutOldPassword = view.findViewById(R.id.input_layout_old_password);
        textInputLayoutNewPassword = view.findViewById(R.id.input_layout_new_password);
        textInputLayoutConfirmPassword = view.findViewById(R.id.input_layout_confirm_password);

        changePasswordBtn.setOnClickListener(this);
        editTextOldPassword.addTextChangedListener(new MyTextWatcher(editTextOldPassword));

    }

    private void changePassword() {
        // CHECK OLD PASSWORD
//        if (!validateEditText(editTextOldPassword, textInputLayoutOldPassword, R.string.err_old_password)) {
//            return;
//        }
//
//        // CHECK NEW PASSWORD
//        if (!validateEditText(editTextNewPassword, textInputLayoutNewPassword, R.string.err_new_password)) {
//            return;
//        }
//
//        // CHECK CONFIRM PASSWORD
//        if (!validateEditText(editTextConfirmPassword, textInputLayoutOldPassword, R.string.err_confirm_password)) {
//            return;
//        }


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

            User user = SharedPreManager.getInstance(getContext()).getUser();
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
//        editTextOldPassword.requestFocus();
        boolean isOldPasswordValid = Validator.checkValidity(textInputLayoutOldPassword, editTextOldPassword.getText().toString(), context.getString(R.string.err_old_password), "textPassword");
        boolean isNewPasswordValid = Validator.checkValidity(textInputLayoutNewPassword, editTextNewPassword.getText().toString(), context.getString(R.string.err_new_password), "textPassword");
        boolean isConfirmPasswordValid = Validator.checkValidity(textInputLayoutConfirmPassword, editTextConfirmPassword.getText().toString(), context.getString(R.string.err_confirm_password), "textPassword");
        return isOldPasswordValid && isNewPasswordValid && isConfirmPasswordValid;
    }

    private boolean validatePassword() {
        String userPassword;
        String newPassword;
//        User user = SharedPreManager.getInstance(getContext()).getUser();
        if (SharedData.getInstance().getPassword() != null) {
            userPassword = SharedData.getInstance().getPassword();
            Timber.e("userPassword -> %s", userPassword);
            String oldPassword = editTextOldPassword.getText().toString().trim();
            checkUserPasswordAndOldPasswordField(userPassword, oldPassword);
        }

        newPassword = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        if (SharedData.getInstance().getPassword()!=null){
            SharedData.getInstance().setPassword(newPassword);
        }

        checkPassWordAndConfirmPassword(newPassword, confirmPassword);
        return true;
    }

    /**
     * @param oldPassword
     * @param newPassword
     * @param confirmPassword
     * @param mobileNo
     */
    private void updatePassword(String oldPassword, String newPassword, String confirmPassword, String mobileNo) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Please Wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Changing Password through UI Service.
        ApiService service = ApiClient.getRetrofitInstance(AppConfig.URL_CHANGE_PASSWORD).create(ApiService.class);
        Call<RetrofitCommon> passwordUpgradeCall = service.updatePassword(oldPassword, newPassword, confirmPassword, mobileNo);

        // Gathering results.
        passwordUpgradeCall.enqueue(new Callback<RetrofitCommon>() {
            @Override
            public void onResponse(Call<RetrofitCommon> call, Response<RetrofitCommon> response) {
                Timber.e("response -> %s", response.message());

                progressDialog.dismiss();
                if (response.body() != null) {
                    if (!response.body().getError()) {
                        showMessage(response.body().getMessage());
                        editTextOldPassword.setText("");
                        editTextNewPassword.setText("");
                        editTextConfirmPassword.setText("");
                        SharedPreManager.getInstance(getActivity()).logout();
                        Intent intentLogout = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intentLogout);
                        if (getActivity()!=null){
                            getActivity().finish();
                        }
                    } else {
                        showMessage(response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<RetrofitCommon> call, Throwable errors) {
                Timber.e("Throwable Errors: -> %s", errors.toString());
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private class MyTextWatcher implements TextWatcher {

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
                case R.id.input_old_password:
//                    validateEditText(editTextOldPassword, textInputLayoutOldPassword, R.string.err_old_password);
                    break;

                case R.id.input_new_password:
//                    validateEditText(editTextNewPassword, textInputLayoutNewPassword, R.string.err_new_password);
                    break;

                case R.id.input_confirm_password:
//                    validateEditText(editTextConfirmPassword, textInputLayoutConfirmPassword, R.string.err_confirm_password);
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
            if (getActivity() != null)
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean checkPassWordAndConfirmPassword(String password, String confirmPassword) {
        boolean passStatus = false;
        if (confirmPassword != null && password != null) {
            if (password.equals(confirmPassword)) {
                passStatus = true;
            } else {
                Toast.makeText(getActivity(), context.getString(R.string.err_confirm_password), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), context.getString(R.string.err_old_password), Toast.LENGTH_SHORT).show();
            }
        }
        return passStatus;
    }
}
