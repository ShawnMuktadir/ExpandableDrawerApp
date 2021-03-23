package www.fiberathome.com.parkingapp.ui.signIn;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.Locale;
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
import www.fiberathome.com.parkingapp.model.response.login.LoginResponse;
import www.fiberathome.com.parkingapp.model.user.User;
import www.fiberathome.com.parkingapp.ui.forgetPassword.ForgetPasswordActivity;
import www.fiberathome.com.parkingapp.ui.helper.ProgressView;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.permission.PermissionActivity;
import www.fiberathome.com.parkingapp.ui.signUp.SignUpActivity;
import www.fiberathome.com.parkingapp.ui.verifyPhone.VerifyPhoneActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.Validator;

import static www.fiberathome.com.parkingapp.utils.Constants.LANGUAGE_BN;
import static www.fiberathome.com.parkingapp.utils.Constants.LANGUAGE_EN;

@SuppressLint("NonConstantResourceId")
public class LoginFragment extends BaseFragment implements View.OnClickListener, ProgressView {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.btnSignIn)
    Button btnSignIn;

    @BindView(R.id.btnOTP)
    Button btnOTP;

    @BindView(R.id.tvForgetPassword)
    TextView tvForgetPassword;

    @BindView(R.id.textViewSignUp)
    TextView textViewSignUp;

    @BindView(R.id.textInputLayoutMobile)
    TextInputLayout textInputLayoutMobile;

    @BindView(R.id.editTextMobileNumber)
    EditText editTextMobile;

    @BindView(R.id.textInputLayoutPassword)
    TextInputLayout textInputLayoutPassword;

    @BindView(R.id.editTextPassword)
    EditText editTextPassword;

    @BindView(R.id.relativeLayoutLogin)
    ConstraintLayout relativeLayoutLogin;

    @BindView(R.id.login_rl_invisible)
    RelativeLayout relativeLayoutInvisible;

    private Unbinder unbinder;

    private LoginActivity context;

    private String deviceOs = "";
    private int sdkVersion = 0;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder = ButterKnife.bind(this, view);

        context = (LoginActivity) getActivity();

        deviceOs = android.os.Build.VERSION.RELEASE; // e.g. myVersion := "10"
        sdkVersion = android.os.Build.VERSION.SDK_INT; // e.g. sdkVersion := 29;

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
                // do some thing
                startActivity(new Intent(context, SignUpActivity.class));
                context.finish();
            }
        };

        if (Locale.getDefault().getLanguage().equalsIgnoreCase(LANGUAGE_EN)) {
            //spannableString.setSpan(clickableSpan, 87, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(clickableSpan, 16, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            textViewSignUp.setText(spannableString);
            textViewSignUp.setMovementMethod(LinkMovementMethod.getInstance());
        } else if (Locale.getDefault().getLanguage().equalsIgnoreCase(LANGUAGE_BN)) {
            //spannableString.setSpan(clickableSpan, 50, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(clickableSpan, 16, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            textViewSignUp.setText(spannableString);
            textViewSignUp.setMovementMethod(LinkMovementMethod.getInstance());
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
        tvForgetPassword.setText(ss);
        tvForgetPassword.setMovementMethod(LinkMovementMethod.getInstance());

        btnSignIn.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);
        btnOTP.setOnClickListener(this);
        tvForgetPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignIn:
                if (ConnectivityUtils.getInstance().checkInternet(context)) {
                    submitLogin();
                } else {
                    DialogUtils.getInstance().alertDialog(context,
                            (Activity) context,
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
                break;

            case R.id.btnOTP:
                Intent otpIntent = new Intent(context, VerifyPhoneActivity.class);
                startActivity(otpIntent);
                break;

        }
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public void showProgress() {
        //progressBarLogin.setVisibility(View.VISIBLE);
        relativeLayoutInvisible.setVisibility(View.VISIBLE);
        editTextMobile.setEnabled(false);
        editTextPassword.setEnabled(false);
        btnSignIn.setEnabled(false);
        btnSignIn.setClickable(false);
    }

    @Override
    public void hideProgress() {
        //progressBarLogin.setVisibility(View.INVISIBLE);
        relativeLayoutInvisible.setVisibility(View.GONE);
        editTextMobile.setEnabled(true);
        editTextPassword.setEnabled(true);
        btnSignIn.setEnabled(true);
        btnSignIn.setClickable(true);
    }

    private void setListeners() {
        editTextPassword.setOnEditorActionListener(
                (v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || event.getAction() == KeyEvent.ACTION_DOWN
                            && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        if (ConnectivityUtils.getInstance().checkInternet(context)) {
                            submitLogin();
                        } else {
                            DialogUtils.getInstance().alertDialog(context,
                                    (Activity) context,
                                    context.getString(R.string.connect_to_internet),
                                    context.getResources().getString(R.string.retry),
                                    context.getResources().getString(R.string.close_app),
                                    new DialogUtils.DialogClickListener() {
                                        @Override
                                        public void onPositiveClick() {
                                            Timber.e("Positive Button clicked");
                                            if (ConnectivityUtils.getInstance().checkInternet(context)) {
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

        Objects.requireNonNull(textInputLayoutPassword.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    textInputLayoutPassword.setErrorEnabled(true);
                    textInputLayoutPassword.setError(context.getString(R.string.err_msg_password));
                }

                if (s.length() > 0) {
                    textInputLayoutPassword.setError(null);
                    textInputLayoutPassword.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void submitLogin() {
        if (checkFields()) {
            String mobileNo = editTextMobile.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            checkLogin(mobileNo, password);
        }
    }

    private boolean checkFields() {
        boolean isPhoneValid = Validator.checkValidity(textInputLayoutMobile, editTextMobile.getText().toString(), context.getString(R.string.err_msg_mobile), "phone");
        boolean isPasswordValid = Validator.checkValidity(textInputLayoutPassword, editTextPassword.getText().toString(), context.getString(R.string.err_msg_password), "textPassword");

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
                        user.setImage(response.body().getUser().getImage());

                        // storing the user in sharedPreference
                        Preferences.getInstance(context).userLogin(user);
                        Timber.e("user after login -> %s", new Gson().toJson(user));

                        if (response.body().getMessage().equalsIgnoreCase("Please verify Your Account by OTP")) {
                            //Intent verifyPhoneIntent = new Intent(context, VerifyPhoneActivity.class);
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
                                ApplicationUtils.showToastMessage(context, response.body().getMessage());
                                btnOTP.setVisibility(View.GONE);
                                //Intent verifyPhoneIntent = new Intent(context, VerifyPhoneActivity.class);
                                Intent verifyPhoneIntent = new Intent(context, VerifyPhoneActivity.class);
                                verifyPhoneIntent.putExtra("mobile_no", mobileNo);
                                verifyPhoneIntent.putExtra("password", password);
                                startActivity(verifyPhoneIntent);
                                context.finish();
                            }
                        }
                    } else {
                        ApplicationUtils.showToastMessage(context, response.body().getMessage());
                    }
                } else {
                    ApplicationUtils.showToastMessage(context, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable errors) {
                Timber.e("Throwable Errors: -> %s", errors.toString());
                hideLoading();
                hideProgress();
                ApplicationUtils.showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
            }
        });
    }
}
