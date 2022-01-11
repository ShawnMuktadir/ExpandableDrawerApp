package www.fiberathome.com.parkingapp.ui.auth.newPassword;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.data.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.data.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;
import www.fiberathome.com.parkingapp.data.model.user.User;
import www.fiberathome.com.parkingapp.databinding.FragmentChangeNewPasswordBinding;
import www.fiberathome.com.parkingapp.ui.auth.AuthViewModel;
import www.fiberathome.com.parkingapp.ui.auth.login.LoginActivity;
import www.fiberathome.com.parkingapp.utils.ToastUtils;
import www.fiberathome.com.parkingapp.utils.Validator;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class NewPasswordFragment extends BaseFragment {

    private NewPasswordActivity context;
    private AuthViewModel viewModel;
    FragmentChangeNewPasswordBinding binding;

    public NewPasswordFragment() {
        // Required empty public constructor
    }

    public static NewPasswordFragment newInstance() {
        return new NewPasswordFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChangeNewPasswordBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = (NewPasswordActivity) getActivity();
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        setListeners();
    }

    private void setListeners() {
        binding.changePasswordBtn.setOnClickListener(v -> setNewPassword());
    }

    private void setNewPassword() {
        if (checkFields()) {
            User user = Preferences.getInstance(context).getUser();
            String oldPassword = SharedData.getInstance().getOtp().trim();
            String newPassword = binding.editTextNewPassword.getText().toString().trim();
            String confirmPassword = binding.editTextConfirmPassword.getText().toString().trim();
            String mobileNo = SharedData.getInstance().getForgetPasswordMobile();
            if (validatePassword()) {
                updatePassword(newPassword, confirmPassword, mobileNo);
            }
        }
    }

    private boolean checkFields() {
        boolean isNewPasswordValid = Validator.checkValidity(binding.textInputLayoutNewPassword, binding.editTextNewPassword.getText().toString(), context.getResources().getString(R.string.err_new_password), "textPassword");
        boolean isConfirmPasswordValid = Validator.checkValidity(binding.textInputLayoutConfirmPassword, binding.editTextConfirmPassword.getText().toString(), context.getResources().getString(R.string.err_confirm_password), "textPassword");
        return isNewPasswordValid && isConfirmPasswordValid;
    }

    private boolean validatePassword() {
        String userPassword;
        String newPassword;

        newPassword = binding.editTextNewPassword.getText().toString().trim();
        String confirmPassword = binding.editTextConfirmPassword.getText().toString().trim();
        if (SharedData.getInstance().getPassword() != null) {
            SharedData.getInstance().setPassword(newPassword);
        }

        checkPassWordAndConfirmPassword(newPassword, confirmPassword);
        return true;
    }

    private void updatePassword(String newPassword, String confirmPassword, String mobileNo) {
        showLoading(context);
        viewModel.initNewPassword(newPassword, confirmPassword, mobileNo);
        viewModel.getNewPasswordMutableData().observe(requireActivity(), (@NonNull BaseResponse response) -> {
            hideLoading();
            if (!response.getError()) {
                ToastUtils.getInstance().showToastMessage(context, response.getMessage());
                Timber.e("response -> %s", response.getMessage());
                binding.editTextNewPassword.setText("");
                binding.editTextConfirmPassword.setText("");
                Preferences.getInstance(context).logout();
                Intent intentLogout = new Intent(context, LoginActivity.class);
                startActivity(intentLogout);
                context.finish();
            } else {
                ToastUtils.getInstance().showToastMessage(context, response.getMessage());
            }
        });
    }

    protected boolean checkPassWordAndConfirmPassword(String password, String confirmPassword) {
        boolean passStatus = false;
        if (confirmPassword != null && password != null) {
            if (password.equals(confirmPassword)) {
                passStatus = true;
            } else {
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.password_not_matched));
            }
        }
        return passStatus;
    }
}