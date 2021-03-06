package www.fiberathome.com.parkingapp.ui.auth.forgotPassword;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.data.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;
import www.fiberathome.com.parkingapp.databinding.FragmentForgotPasswordBinding;
import www.fiberathome.com.parkingapp.ui.auth.AuthViewModel;
import www.fiberathome.com.parkingapp.ui.auth.changePassword.ChangePasswordOTPActivity;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;
import www.fiberathome.com.parkingapp.utils.Validator;

@SuppressLint("NonConstantResourceId")
public class ForgotPasswordFragment extends BaseFragment {

    private ForgotPasswordActivity context;
    private AuthViewModel viewModel;
    FragmentForgotPasswordBinding binding;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    public static ForgotPasswordFragment newInstance() {
        return new ForgotPasswordFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentForgotPasswordBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = (ForgotPasswordActivity) getActivity();
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        binding.editTextMobileNumber.requestFocus();
        binding.editTextMobileNumber.requestLayout();
        setListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setListener() {

        Objects.requireNonNull(binding.textInputLayoutMobile.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    binding.textInputLayoutMobile.setErrorEnabled(true);
                    binding.textInputLayoutMobile.setError(context.getResources().getString(R.string.err_msg_mobile));
                }

                if (s.length() > 0) {
                    binding.textInputLayoutMobile.setError(null);
                    binding.textInputLayoutMobile.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.editTextMobileNumber.setOnEditorActionListener(
                (v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || event.getAction() == KeyEvent.ACTION_DOWN
                            && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        if (ConnectivityUtils.getInstance().checkInternet(context)) {
                            submitLogin();
                        } else {
                            DialogUtils.getInstance().alertDialog(context,
                                    context, context.getResources().getString(R.string.connect_to_internet),
                                    context.getResources().getString(R.string.retry),
                                    context.getResources().getString(R.string.close_app),
                                    new DialogUtils.DialogClickListener() {
                                        @Override
                                        public void onPositiveClick() {
                                            Timber.e("Positive Button clicked");
                                            if (ConnectivityUtils.getInstance().checkInternet(context)) {
                                                submitLogin();
                                            } else {
                                                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet));
                                            }
                                        }

                                        @Override
                                        public void onNegativeClick() {
                                            Timber.e("Negative Button Clicked");
                                            if (context != null) {
                                                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.thanks_message));
                                                context.finish();
                                            }
                                        }
                                    }).show();
                        }
                        return true;
                    }
                    return false;
                });

        binding.btnForgetPassword.setOnClickListener(v -> {
            if (ConnectivityUtils.getInstance().checkInternet(context)) {
                submitLogin();
            } else {
                DialogUtils.getInstance().alertDialog(context,
                        context, context.getResources().getString(R.string.connect_to_internet),
                        context.getResources().getString(R.string.retry), context.getResources().getString(R.string.close_app),
                        new DialogUtils.DialogClickListener() {
                            @Override
                            public void onPositiveClick() {
                                Timber.e("Positive Button clicked");
                                if (ConnectivityUtils.getInstance().checkInternet(context)) {
                                    submitLogin();
                                } else {
                                    ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet));
                                }
                            }

                            @Override
                            public void onNegativeClick() {
                                Timber.e("Negative Button Clicked");
                                if (context != null) {
                                    ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.thanks_message));
                                    context.finish();
                                }
                            }
                        }).show();
            }
        });
    }

    private boolean checkFields() {
        return Validator.checkValidity(binding.textInputLayoutMobile, binding.editTextMobileNumber.getText().toString(), context.getResources().getString(R.string.err_msg_mobile), "phone");

    }

    private void submitLogin() {
        if (checkFields()) {
            String mobileNo = binding.editTextMobileNumber.getText().toString().trim();
            checkForgetPassword(mobileNo);
        }
    }

    private void checkForgetPassword(final String mobileNo) {
        showLoading(context);
        viewModel.initForgotPassword(mobileNo);
        viewModel.getForgotPasswordMutableLiveData().observe(requireActivity(), (@NonNull BaseResponse response) -> {
            if (response.getMessage().equalsIgnoreCase("Try Again! Invalid Mobile Number.")) {
                ToastUtils.getInstance().showToastMessage(context,
                        context.getResources().getString(R.string.mobile_number_not_exist));
            } else {
                if (!response.getError()) {
                    Intent intent = new Intent(context, ChangePasswordOTPActivity.class);
                    intent.putExtra("mobile_no", mobileNo);
                    startActivity(intent);
                    SharedData.getInstance().setForgetPasswordMobile(mobileNo);
                    ToastUtils.getInstance().showToastMessage(context, response.getMessage());
                    context.finish();
                } else {
                    ToastUtils.getInstance().showToastMessage(context, response.getMessage());
                }
            }
        });
    }
}
