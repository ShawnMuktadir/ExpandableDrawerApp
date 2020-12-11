package www.fiberathome.com.parkingapp.ui.signIn;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.SharedPreManager;
import www.fiberathome.com.parkingapp.model.loginUser.User;
import www.fiberathome.com.parkingapp.ui.forgetPassword.ForgetPasswordActivity;
import www.fiberathome.com.parkingapp.ui.helper.ProgressView;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.permission.PermissionActivity;
import www.fiberathome.com.parkingapp.ui.signUp.SignUpActivity;
import www.fiberathome.com.parkingapp.ui.verifyPhone.VerifyPhoneActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.HttpsTrustManager;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.Validator;

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
    RelativeLayout relativeLayoutLogin;
    @BindView(R.id.login_rl_invisible)
    RelativeLayout relativeLayoutInvisible;

    private Unbinder unbinder;
    private LoginActivity context;

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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder = ButterKnife.bind(this, view);

        context = (LoginActivity) getActivity();

        setListeners();

        if (SharedPreManager.getInstance(context).isLoggedIn() && SharedPreManager.getInstance(context) != null && SharedPreManager.getInstance(context).isWaitingForLocationPermission()) {
            Timber.e("isWaitingForLocationPermission -> %s", SharedPreManager.getInstance(context).isWaitingForLocationPermission());
            //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            Intent intent = new Intent(context, HomeActivity.class);
            startActivity(intent);
            context.finish();
            return;
        } else if (SharedPreManager.getInstance(context).isLoggedIn() && !SharedPreManager.getInstance(context).isWaitingForLocationPermission()) {
            //Timber.e("location check else method called");
            Timber.e("isWaitingForLocationPermission else -> %s", SharedPreManager.getInstance(context).isWaitingForLocationPermission());
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
                //startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        };

        if (Locale.getDefault().getLanguage().equals("en")) {
            //spannableString.setSpan(clickableSpan, 87, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(clickableSpan, 16, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            textViewSignUp.setText(spannableString);
            textViewSignUp.setMovementMethod(LinkMovementMethod.getInstance());
        } else if (Locale.getDefault().getLanguage().equals("bn")) {
            //spannableString.setSpan(clickableSpan, 50, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(clickableSpan, 16, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
                //finish();
            }
        };

        if (Locale.getDefault().getLanguage().equals("en")) {
            //spannableString.setSpan(clickableSpan, 87, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(span, 17, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvForgetPassword.setText(ss);
            tvForgetPassword.setMovementMethod(LinkMovementMethod.getInstance());
        } else if (Locale.getDefault().getLanguage().equals("bn")) {
            //spannableString.setSpan(clickableSpan, 50, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(span, 18, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvForgetPassword.setText(ss);
            tvForgetPassword.setMovementMethod(LinkMovementMethod.getInstance());
        }

        btnSignIn.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);
        btnOTP.setOnClickListener(this);
        tvForgetPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignIn:
                if (ApplicationUtils.checkInternet(context)) {
                    submitLogin();
                } else {
                    ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
                        Timber.e("Positive Button clicked");
                        if (ApplicationUtils.checkInternet(context)) {
                            submitLogin();
                        } else {
                            TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                        }
                    }, (dialog, which) -> {
                        Timber.e("Negative Button Clicked");
                        dialog.dismiss();
                        if (context != null) {
                            TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                            context.finish();
                        }
                    });
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
                        if (ApplicationUtils.checkInternet(context)) {
                            submitLogin();
                        } else {
                            ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
                                Timber.e("Positive Button clicked");
                                if (ApplicationUtils.checkInternet(context)) {
                                    submitLogin();
                                } else {
                                    TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                                }
                            }, (dialog, which) -> {
                                Timber.e("Negative Button Clicked");
                                dialog.dismiss();
                                if (context != null) {
                                    TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                                    context.finish();
                                }
                            });
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

        btnOTP.setVisibility(View.GONE);

        HttpsTrustManager.allowAllSSL();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN, response -> {
            // remove the progress bar
            Timber.e("URL -> %s", AppConfig.URL_LOGIN);

            hideLoading();

            hideProgress();

            try {
                JSONObject jsonObject = new JSONObject(response);

                Timber.e("jsonObject login-> %s", jsonObject.toString());

                if (!jsonObject.getBoolean("error")) {

                    // getting the user from the response
                    JSONObject userJson = jsonObject.getJSONObject("user");

                    // creating a new user object
                    User user = new User();
                    user.setId(userJson.getInt("id"));
                    user.setFullName(userJson.getString("fullname"));
                    user.setMobileNo(userJson.getString("mobile_no"));
                    user.setVehicleNo(userJson.getString("vehicle_no"));
                    user.setProfilePic(userJson.getString("image"));

                    // storing the user in sharedPreference
                    SharedPreManager.getInstance(context).userLogin(user);
                    Timber.e("user after login -> %s", new Gson().toJson(user));

                    if (response.equals("Please verify Your Account by OTP")) {
                        Intent verifyPhoneIntent = new Intent(context, VerifyPhoneActivity.class);
                        if (checkFields()) {
                            verifyPhoneIntent.putExtra("mobile_no", mobileNo);
                        }
                        startActivity(verifyPhoneIntent);
                        context.finish();
                    } else {
                        // Move to another Activity

                        if ((ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                            Timber.e("activity login if -> %s", SharedPreManager.getInstance(context).isWaitingForLocationPermission());
                            Intent intent = new Intent(context, PermissionActivity.class);
                            startActivity(intent);
                            context.finish();
                            //Toast.makeText(context, "nai ami", Toast.LENGTH_SHORT).show();
                        } else if (!SharedPreManager.getInstance(context).isWaitingForLocationPermission()) {
                            Timber.e("activity login else if -> %s", SharedPreManager.getInstance(context).isWaitingForLocationPermission());
                            Intent intent = new Intent(context, PermissionActivity.class);
                            startActivity(intent);
                            context.finish();
                        } else {
                            Timber.e("activity login else -> %s", SharedPreManager.getInstance(context).isWaitingForLocationPermission());
                            //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            Intent intent = new Intent(context, HomeActivity.class);
                            startActivity(intent);
                            context.finish();
                        }
                    }

                } else if (jsonObject.getBoolean("error") && jsonObject.has("authentication")) {
                    // IF ERROR OCCURS AND AUTHENTICATION IS INVALID
                    if (jsonObject.getString("message").equals("Please verify Your Account by OTP")) {
                        showMessage("Please verify Your Account by OTP");
                        btnOTP.setVisibility(View.GONE);
                        Intent verifyPhoneIntent = new Intent(context, VerifyPhoneActivity.class);
                        verifyPhoneIntent.putExtra("mobile_no", mobileNo);
                        verifyPhoneIntent.putExtra("password", password);
                        startActivity(verifyPhoneIntent);
                        context.finish();
                    }
                } else {
                    Timber.e("LoginActivity error message -> %s", jsonObject.getString("message"));
                    showMessage(jsonObject.getString("message"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Timber.e("Error Message -> %s ", error.getMessage());
            hideProgress();
            if (Objects.requireNonNull(error.getMessage()).equals("java.net.ConnectException: failed to connect to /163.47.157.195 (port 80) after 50000ms: connect failed: ENETUNREACH (Network is unreachable)") || Objects.requireNonNull(error.getMessage()).equals("")) {
                //showMessage(error.getMessage());
                showMessage(context.getResources().getString(R.string.connect_to_internet));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mobile_no", mobileNo);
                params.put("password", password);
                Timber.e("LoginActivity params -> %s", params);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ParkingApp.getInstance().addToRequestQueue(stringRequest, TAG);
    }

    private void showMessage(String message) {
        //Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
        final Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 2000);
    }
}
