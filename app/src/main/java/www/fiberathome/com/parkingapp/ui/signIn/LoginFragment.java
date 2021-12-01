package www.fiberathome.com.parkingapp.ui.signIn;

import static www.fiberathome.com.parkingapp.model.data.Constants.LANGUAGE_BN;
import static www.fiberathome.com.parkingapp.model.data.Constants.LANGUAGE_EN;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;

import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.databinding.FragmentLoginBinding;
import www.fiberathome.com.parkingapp.model.api.ApiClient;
import www.fiberathome.com.parkingapp.model.api.ApiService;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.response.login.LoginResponse;
import www.fiberathome.com.parkingapp.model.user.User;
import www.fiberathome.com.parkingapp.ui.forgetPassword.ForgetPasswordActivity;
import www.fiberathome.com.parkingapp.ui.helper.ProgressView;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.permission.PermissionActivity;
import www.fiberathome.com.parkingapp.ui.signUp.SignUpActivity;
import www.fiberathome.com.parkingapp.ui.verifyPhone.VerifyPhoneActivity;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;
import www.fiberathome.com.parkingapp.utils.Validator;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class LoginFragment extends BaseFragment implements View.OnClickListener, ProgressView {

    private LoginActivity context;

    FragmentLoginBinding binding;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = (LoginActivity) getActivity();

        String deviceOs = android.os.Build.VERSION.RELEASE; // e.g. myVersion := "10"
        int sdkVersion = android.os.Build.VERSION.SDK_INT; // e.g. sdkVersion := 29;

        Timber.e("device OS -> %s", deviceOs);
        Timber.e("device sdkVersion -> %s", sdkVersion);

        setListeners();

        if (Preferences.getInstance(context).isLoggedIn() && Preferences.getInstance(context) != null && Preferences.getInstance(context).isWaitingForLocationPermission()) {
            Timber.e("isWaitingForLocationPermission -> %s", Preferences.getInstance(context).isWaitingForLocationPermission());
            Intent intent = new Intent(context, HomeActivity.class);
            startActivity(intent);
            context.finish();
            return;
        } else if (Preferences.getInstance(context).isLoggedIn() && !Preferences.getInstance(context).isWaitingForLocationPermission()) {
            //Timber.e("location check else method called");
            Timber.e("isWaitingForLocationPermission else -> %s", Preferences.getInstance(context).isWaitingForLocationPermission());
            Intent intent = new Intent(context, PermissionActivity.class);
            startActivity(intent);
            context.finish();
        }

        //makes an underline on for Registration Click Here
        SpannableString spannableString = new SpannableString(context.getResources().getString(R.string.no_account_yet_click_here));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {
                // start SignUpActivity
                startActivity(new Intent(context, SignUpActivity.class));
            }
        };

        if (Locale.getDefault().getLanguage().equalsIgnoreCase(LANGUAGE_EN)) {
            spannableString.setSpan(clickableSpan, 16, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.textViewSignUp.setText(spannableString);
            binding.textViewSignUp.setMovementMethod(LinkMovementMethod.getInstance());
        } else if (Locale.getDefault().getLanguage().equalsIgnoreCase(LANGUAGE_BN)) {
            spannableString.setSpan(clickableSpan, 16, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.textViewSignUp.setText(spannableString);
            binding.textViewSignUp.setMovementMethod(LinkMovementMethod.getInstance());
        }

        //makes an underline on Forgot Password Click Here
        SpannableString ss = new SpannableString(context.getResources().getString(R.string.forget_password_click_here));
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {
                // do some thing
                startActivity(new Intent(context, ForgetPasswordActivity.class));
            }
        };

        int s1 = ss.toString().codePointAt(0);
        if (s1 >= 0x0980 && s1 <= 0x09E0) {
            ss.setSpan(span, 21, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            ss.setSpan(span, 17, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        binding.tvForgetPassword.setText(ss);
        binding.tvForgetPassword.setMovementMethod(LinkMovementMethod.getInstance());

        binding.btnSignIn.setOnClickListener(this);
        binding.textViewSignUp.setOnClickListener(this);
        binding.btnOTP.setOnClickListener(this);
        binding.tvForgetPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignIn:
                if (ConnectivityUtils.getInstance().checkInternet(context)) {
                    submitLogin();
                } else {
                    DialogUtils.getInstance().alertDialog(context,
                            context,
                            context.getResources().getString(R.string.connect_to_internet),
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
                break;

            case R.id.btnOTP:
                Intent otpIntent = new Intent(context, VerifyPhoneActivity.class);
                startActivity(otpIntent);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void showProgress() {
        binding.relativeLayoutInvisible.setVisibility(View.VISIBLE);
        binding.editTextMobile.setEnabled(false);
        binding.editTextPassword.setEnabled(false);
        binding.btnSignIn.setEnabled(false);
        binding.btnSignIn.setClickable(false);
    }

    @Override
    public void hideProgress() {
        binding.relativeLayoutInvisible.setVisibility(View.GONE);
        binding.editTextMobile.setEnabled(true);
        binding.editTextPassword.setEnabled(true);
        binding.btnSignIn.setEnabled(true);
        binding.btnSignIn.setClickable(true);
    }

    private void setListeners() {
        binding.editTextPassword.setOnEditorActionListener(
                (v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || event.getAction() == KeyEvent.ACTION_DOWN
                            && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        if (ConnectivityUtils.getInstance().checkInternet(context)) {
                            submitLogin();
                        } else {
                            DialogUtils.getInstance().alertDialog(context,
                                    context,
                                    context.getResources().getString(R.string.connect_to_internet),
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

        Objects.requireNonNull(binding.textInputLayoutPassword.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    binding.textInputLayoutPassword.setErrorEnabled(true);
                    binding.textInputLayoutPassword.setError(context.getResources().getString(R.string.err_msg_password));
                }

                if (s.length() > 0) {
                    binding.textInputLayoutPassword.setError(null);
                    binding.textInputLayoutPassword.setErrorEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void submitLogin() {
        if (checkFields()) {
            String mobileNo = binding.editTextMobile.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString().trim();

            checkLogin(mobileNo, password);
        }
    }

    private boolean checkFields() {
        boolean isPhoneValid = Validator.checkValidity(binding.textInputLayoutMobile, binding.editTextMobile.getText().toString(), context.getResources().getString(R.string.err_msg_mobile), "phone");
        boolean isPasswordValid = Validator.checkValidity(binding.textInputLayoutPassword, binding.editTextPassword.getText().toString(), context.getResources().getString(R.string.err_msg_password), "textPassword");
        return isPhoneValid && isPasswordValid;
    }

    private void checkLogin(final String mobileNo, final String password) {

        showLoading(context);

        showProgress();

        ApiService service = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<LoginResponse> call = service.loginUser(mobileNo, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {

                Timber.e("login response body-> %s", new Gson().toJson(response.body()));

                hideLoading();

                hideProgress();

                if (response.body() != null) {
                    if (!response.body().getError()) {
                        //showMessage(response.body().getMessage());

                        // creating a new user object
                        User user = new User();
                        user.setId(response.body().getUser().getId());
                        user.setFullName(response.body().getUser().getFullName());
                        user.setMobileNo(response.body().getUser().getMobileNo());
                        user.setVehicleNo(response.body().getUser().getVehicleNo());
                        user.setVehicleImage(response.body().getUser().getVehicleImage());
                        user.setImage(response.body().getUser().getImage());

                        // storing the user in sharedPreference
                        Preferences.getInstance(context).setUser(user);

                        try {
                            String currentString = user.getVehicleNo().trim();
                            String[] separated = currentString.split(" ");

                            String vehicleClass = separated[0];
                            String vehicleDiv = separated[1];
                            String carNumber = separated[2];
                            Preferences.getInstance(context).saveVehicleClassData(vehicleClass);
                            Preferences.getInstance(context).saveVehicleDivData(vehicleDiv);
                        } catch (Exception e) {
                            Timber.e(e.getCause());
                        }
                        Timber.e("user after login -> %s", new Gson().toJson(user));

                        if (response.body().getMessage().equalsIgnoreCase("Please verify Your Account by OTP")) {
                            Intent verifyPhoneIntent = new Intent(context, VerifyPhoneActivity.class);
                            if (checkFields()) {
                                verifyPhoneIntent.putExtra("mobile_no", mobileNo);
                            }
                            startActivity(verifyPhoneIntent);
                            context.finish();
                        } else {
                            if ((ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                                Timber.e("activity login if -> %s", Preferences.getInstance(context).isWaitingForLocationPermission());
                                Intent intent = new Intent(context, PermissionActivity.class);
                                startActivity(intent);
                                context.finish();
                            } else if (!Preferences.getInstance(context).isWaitingForLocationPermission()) {
                                Timber.e("activity login else if -> %s", Preferences.getInstance(context).isWaitingForLocationPermission());
                                Intent intent = new Intent(context, PermissionActivity.class);
                                startActivity(intent);
                                context.finish();
                            } else {
                                Timber.e("activity login else -> %s", Preferences.getInstance(context).isWaitingForLocationPermission());
                                Intent intent = new Intent(context, HomeActivity.class);
                                startActivity(intent);
                                context.finish();
                            }
                        }
                    } else if (response.body().getAuthentication() != null) {
                        if (response.body().getError() && !response.body().getAuthentication()) {
                            // IF ERROR OCCURS AND AUTHENTICATION IS INVALID
                            if (response.body().getMessage().equalsIgnoreCase("Please verify Your Account by OTP")) {
                                ToastUtils.getInstance().showToastMessage(context, response.body().getMessage());
                                binding.btnOTP.setVisibility(View.GONE);
                                Intent verifyPhoneIntent = new Intent(context, VerifyPhoneActivity.class);
                                verifyPhoneIntent.putExtra("mobile_no", mobileNo);
                                verifyPhoneIntent.putExtra("password", password);
                                startActivity(verifyPhoneIntent);
                                context.finish();
                            }
                        }
                    } else {
                        ToastUtils.getInstance().showToastMessage(context, response.body().getMessage());
                    }
                } else {
                    ToastUtils.getInstance().showToastMessage(context, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable errors) {
                Timber.e("Throwable Errors: -> %s", errors.toString());
                hideLoading();
                hideProgress();
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
            }
        });
    }
}
