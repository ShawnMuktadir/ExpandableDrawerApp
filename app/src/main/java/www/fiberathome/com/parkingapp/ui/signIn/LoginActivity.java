package www.fiberathome.com.parkingapp.ui.signIn;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.android.volley.DefaultRetryPolicy;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.model.loginUser.User;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.ui.forgetPassword.ForgetPasswordActivity;
import www.fiberathome.com.parkingapp.ui.helper.ProgressView;
import www.fiberathome.com.parkingapp.ui.permission.PermissionActivity;
import www.fiberathome.com.parkingapp.utils.HttpsTrustManager;
import www.fiberathome.com.parkingapp.model.data.preference.SharedPreManager;
import www.fiberathome.com.parkingapp.utils.Validator;
import www.fiberathome.com.parkingapp.ui.signUp.SignUpActivity;
import www.fiberathome.com.parkingapp.ui.signUp.verifyPhone.VerifyPhoneActivity;

public class LoginActivity extends BaseActivity implements View.OnClickListener, ProgressView {

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
    private Context context;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        unbinder = ButterKnife.bind(this);
        context = this;

        setListeners();

        if (SharedPreManager.getInstance(getApplicationContext()).isLoggedIn() && SharedPreManager.getInstance(context) != null && SharedPreManager.getInstance(context).isWaitingForLocationPermission()) {
            Timber.e("isWaitingForLocationPermission -> %s", SharedPreManager.getInstance(context).isWaitingForLocationPermission());
            //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            return;
        } else if (SharedPreManager.getInstance(getApplicationContext()).isLoggedIn() && !SharedPreManager.getInstance(context).isWaitingForLocationPermission()) {
            //Timber.e("location check else method called");
            Timber.e("isWaitingForLocationPermission else -> %s", SharedPreManager.getInstance(context).isWaitingForLocationPermission());
            Intent intent = new Intent(LoginActivity.this, PermissionActivity.class);
            startActivity(intent);
            finish();
        }

        //makes an underline on for Registration Click Here
        SpannableString spannableString = new SpannableString(context.getResources().getString(R.string.no_account_yet_click_here));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {
                // do some thing
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                finish();
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
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
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
    }

    @Override
    protected void onStart() {
        super.onStart();

        btnSignIn.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);
        btnOTP.setOnClickListener(this);
        tvForgetPassword.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissProgressDialog();

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
                            finish();
                        }
                    });
                }
                break;

            case R.id.btnOTP:
                Intent otpIntent = new Intent(LoginActivity.this, VerifyPhoneActivity.class);
                startActivity(otpIntent);
                break;

        }
    }

    /**
     * Check user input
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //LoginActivity.super.onBackPressed();
                        finishAffinity();
                        TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                    }
                }).create();
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
                //dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.black));
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
                                    finish();
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

        progressDialog = ApplicationUtils.progressDialog(context, "Please wait...");

        showProgress();

        btnOTP.setVisibility(View.GONE);

        HttpsTrustManager.allowAllSSL();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN, response -> {
            // remove the progress bar
            Timber.e("URL -> %s", AppConfig.URL_LOGIN);
            progressDialog.dismiss();
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
                    SharedPreManager.getInstance(getApplicationContext()).userLogin(user);
                    Timber.e("user after login -> %s", new Gson().toJson(user));

                    if (response.equals("Please verify Your Account by OTP")) {
                        Intent verifyPhoneIntent = new Intent(LoginActivity.this, VerifyPhoneActivity.class);
                        if (checkFields()) {
                            verifyPhoneIntent.putExtra("mobile_no", mobileNo);
                        }
                        startActivity(verifyPhoneIntent);
                        finish();
                    } else {
                        // Move to another Activity

                        if ((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                            Timber.e("activity login if -> %s", SharedPreManager.getInstance(context).isWaitingForLocationPermission());
                            Intent intent = new Intent(LoginActivity.this, PermissionActivity.class);
                            startActivity(intent);
                            finish();
                            //Toast.makeText(context, "nai ami", Toast.LENGTH_SHORT).show();
                        } else if (!SharedPreManager.getInstance(context).isWaitingForLocationPermission()) {
                            Timber.e("activity login else if -> %s", SharedPreManager.getInstance(context).isWaitingForLocationPermission());
                            Intent intent = new Intent(LoginActivity.this, PermissionActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Timber.e("activity login else -> %s", SharedPreManager.getInstance(context).isWaitingForLocationPermission());
                            //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                } else if (jsonObject.getBoolean("error") && jsonObject.has("authentication")) {
                    // IF ERROR OCCURS AND AUTHENTICATION IS INVALID
                    if (jsonObject.getString("message").equals("Please verify Your Account by OTP")) {
                        showMessage("Please verify Your Account by OTP");
                        btnOTP.setVisibility(View.GONE);
                        Intent verifyPhoneIntent = new Intent(LoginActivity.this, VerifyPhoneActivity.class);
                        verifyPhoneIntent.putExtra("mobile_no", mobileNo);
                        verifyPhoneIntent.putExtra("password", password);
                        startActivity(verifyPhoneIntent);
                        finish();
                    }
                } else {
                    Timber.e("LoginActivity error message -> %s", jsonObject.getString("message"));
                    showMessage(jsonObject.getString("message"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Timber.e("Error Message -> %s ", error.getMessage());
                if (progressDialog != null) progressDialog.dismiss();
                if (Objects.requireNonNull(error.getMessage()).equals("java.net.ConnectException: failed to connect to /163.47.157.195 (port 80) after 50000ms: connect failed: ENETUNREACH (Network is unreachable)") || Objects.requireNonNull(error.getMessage()).equals("")) {
                    //showMessage(error.getMessage());
                    showMessage(context.getResources().getString(R.string.connect_to_internet));
                }
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

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void showMessage(String message) {
        //Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
        final Toast toast = Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT);
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
