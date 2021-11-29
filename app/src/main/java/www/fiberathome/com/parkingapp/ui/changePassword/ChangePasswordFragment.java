package www.fiberathome.com.parkingapp.ui.changePassword;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.databinding.FragmentChangePasswordBinding;
import www.fiberathome.com.parkingapp.model.api.ApiClient;
import www.fiberathome.com.parkingapp.model.api.ApiService;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.response.BaseResponse;
import www.fiberathome.com.parkingapp.model.user.User;
import www.fiberathome.com.parkingapp.ui.signIn.LoginActivity;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;
import www.fiberathome.com.parkingapp.utils.Validator;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class ChangePasswordFragment extends BaseFragment implements View.OnClickListener {

    private Context context;

    FragmentChangePasswordBinding binding;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    public static ChangePasswordFragment newInstance() {
        return new ChangePasswordFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentChangePasswordBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        if (getActivity() != null)
            getActivity().setTitle(R.string.title_change_password);

        binding.changePasswordBtn.setOnClickListener(this);

        setListeners();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.changePasswordBtn) {
            if (ConnectivityUtils.getInstance().checkInternet(context)) {
                changePassword();
            } else {
                DialogUtils.getInstance().alertDialog(context,
                        (Activity) context, context.getResources().getString(R.string.connect_to_internet), context.getResources().getString(R.string.retry), context.getResources().getString(R.string.close_app),
                        new DialogUtils.DialogClickListener() {
                            @Override
                            public void onPositiveClick() {
                                if (ConnectivityUtils.getInstance().checkInternet(context)) {
                                    changePassword();
                                } else {
                                    ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet));
                                }
                            }

                            @Override
                            public void onNegativeClick() {
                                if (getActivity() != null) {
                                    getActivity().finish();
                                    ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.thanks_message));
                                }
                            }
                        }).show();
            }
        }
    }

    private void setListeners() {
        Objects.requireNonNull(binding.textInputLayoutOldPassword.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    binding.textInputLayoutOldPassword.setErrorEnabled(true);
                    binding.textInputLayoutOldPassword.setError(context.getResources().getString(R.string.err_old_password));
                }

                if (s.length() > 0) {
                    binding.textInputLayoutOldPassword.setError(null);
                    binding.textInputLayoutOldPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Objects.requireNonNull(binding.textInputLayoutNewPassword.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    binding.textInputLayoutNewPassword.setErrorEnabled(true);
                    binding.textInputLayoutNewPassword.setError(context.getResources().getString(R.string.err_new_password));
                }

                if (s.length() > 0) {
                    binding.textInputLayoutNewPassword.setError(null);
                    binding.textInputLayoutNewPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Objects.requireNonNull(binding.textInputLayoutConfirmPassword.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    binding.textInputLayoutConfirmPassword.setErrorEnabled(true);
                    binding.textInputLayoutConfirmPassword.setError(context.getResources().getString(R.string.err_confirm_password));
                }

                if (s.length() > 0) {
                    binding.textInputLayoutConfirmPassword.setError(null);
                    binding.textInputLayoutConfirmPassword.setErrorEnabled(false);
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
            String oldPassword = binding.editTextOldPassword.getText().toString().trim();
            String newPassword = binding.editTextNewPassword.getText().toString().trim();
            String confirmPassword = binding.editTextConfirmPassword.getText().toString().trim();
            String mobileNo = user.getMobileNo().trim();
            if (validatePassword()) {
                updatePassword(oldPassword, newPassword, confirmPassword, mobileNo);
            }
        }
    }

    private boolean checkFields() {
        boolean isOldPasswordValid = Validator.checkValidity(binding.textInputLayoutOldPassword, binding.editTextOldPassword.getText().toString(), context.getResources().getString(R.string.err_old_password), "textPassword");
        boolean isNewPasswordValid = Validator.checkValidity(binding.textInputLayoutNewPassword, binding.editTextNewPassword.getText().toString(), context.getResources().getString(R.string.err_new_password), "textPassword");
        boolean isConfirmPasswordValid = Validator.checkValidity(binding.textInputLayoutConfirmPassword, binding.editTextConfirmPassword.getText().toString(), context.getResources().getString(R.string.err_confirm_password), "textPassword");
        return isOldPasswordValid && isNewPasswordValid && isConfirmPasswordValid;
    }

    private boolean validatePassword() {
        String oldPassword = binding.editTextOldPassword.getText().toString().trim();
        String newPassword = binding.editTextNewPassword.getText().toString().trim();
        String confirmPassword = binding.editTextConfirmPassword.getText().toString().trim();
        checkNewPasswordAndConfirmPassword(newPassword, confirmPassword);
        return true;
    }

    private void updatePassword(String oldPassword, String newPassword, String confirmPassword, String mobileNo) {
        showLoading(context);
        ApiService service = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<BaseResponse> call = service.updatePassword(oldPassword, newPassword, confirmPassword, mobileNo);

        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                Timber.e("change password response body-> %s", new Gson().toJson(response.body()));
                hideLoading();
                if (response.body() != null) {
                    if (!response.body().getError()) {
                        ToastUtils.getInstance().showToastMessage(context, response.body().getMessage());
                        Preferences.getInstance(context).logout();
                        Intent intentLogout = new Intent(context, LoginActivity.class);
                        startActivity(intentLogout);
                        if (getActivity() != null) {
                            getActivity().finishAffinity();
                        }
                    } else {
                        ToastUtils.getInstance().showToastMessage(context, response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable errors) {
                Timber.e("Throwable Errors: -> %s", errors.toString());
            }
        });
    }

    protected boolean checkNewPasswordAndConfirmPassword(String password, String confirmPassword) {
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