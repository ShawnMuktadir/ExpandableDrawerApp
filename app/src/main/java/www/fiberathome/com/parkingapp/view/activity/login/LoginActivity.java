package www.fiberathome.com.parkingapp.view.activity.login;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.android.volley.DefaultRetryPolicy;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.User;
import www.fiberathome.com.parkingapp.base.AppConfig;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.view.activity.forgetPassword.ForgetPasswordActivity;
import www.fiberathome.com.parkingapp.view.activity.main.MainActivity;
import www.fiberathome.com.parkingapp.view.activity.permission.PermissionActivity;
import www.fiberathome.com.parkingapp.view.activity.registration.SignUpActivity;
import www.fiberathome.com.parkingapp.utils.HttpsTrustManager;
import www.fiberathome.com.parkingapp.data.preference.SharedPreManager;
import www.fiberathome.com.parkingapp.utils.Validator;
import www.fiberathome.com.parkingapp.view.activity.registration.VerifyPhoneActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private Context context;
    private EditText editTextMobile;
    private EditText editTextPassword;

    private TextInputLayout textInputLayoutMobile;
    private TextInputLayout textInputLayoutPassword;
    private TextView textViewSignUp;

    private Button btnOTP;
    private Button btnSignIn;
    private Button btnForgetPassword;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        initUI();
        setListeners();

        // Check user is logged in
//        if (SharedData.getInstance().getLocationPermission()) {
//            Timber.e("location check if method e dhukche");
        if (SharedPreManager.getInstance(getApplicationContext()).isLoggedIn() && SharedPreManager.getInstance(context) != null && SharedPreManager.getInstance(context).isWaitingForLocationPermission()) {
            Timber.e("isWaitingForLocationPermission -> %s", SharedPreManager.getInstance(context).isWaitingForLocationPermission());
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        } else if (SharedPreManager.getInstance(getApplicationContext()).isLoggedIn() && !SharedPreManager.getInstance(context).isWaitingForLocationPermission()) {
//            Timber.e("location check else method e dhukche");
            Timber.e("isWaitingForLocationPermission else -> %s", SharedPreManager.getInstance(context).isWaitingForLocationPermission());
            Intent intent = new Intent(LoginActivity.this, PermissionActivity.class);
            startActivity(intent);
            finish();
        }

        //makes an underline on for Registration Click Here
        SpannableString spannableString = new SpannableString(context.getResources().getString(R.string.no_account_yet_click_here));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                // do some thing
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        };
        spannableString.setSpan(clickableSpan, 16, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewSignUp.setText(spannableString);
        textViewSignUp.setMovementMethod(LinkMovementMethod.getInstance());

        //makes an underline on Forgot Password Click Here
        SpannableString ss = new SpannableString(context.getResources().getString(R.string.forget_password_click_here));
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                // do some thing
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            }
        };
        ss.setSpan(span, 17, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        btnForgetPassword.setText(ss);
        btnForgetPassword.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onStart() {
        super.onStart();

        btnSignIn.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);
        btnOTP.setOnClickListener(this);
        btnForgetPassword.setOnClickListener(this);

        //mobileNumberET.addTextChangedListener(new MyTextWatcher(inputLayoutMobile));
        //passwordET.addTextChangedListener(new MyTextWatcher(inputLayoutPassword));
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
            case R.id.signin_btn:
                submitLogin();
                break;

//            case R.id.link_signup:
//                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
//                startActivity(intent);
//                break;

            case R.id.btn_retrive_otp:
                Intent otpIntent = new Intent(LoginActivity.this, VerifyPhoneActivity.class);
                startActivity(otpIntent);
                break;

//            case R.id.link_forget_password:
//                Intent forgetPasswordIntent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
//                startActivity(forgetPasswordIntent);
//                break;

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
                        LoginActivity.super.onBackPressed();
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

    private void initUI() {
        // initialize component
        btnSignIn = findViewById(R.id.signin_btn);
        editTextMobile = findViewById(R.id.editTextMobileNumber);
        editTextPassword = findViewById(R.id.editTextPassword);
        textViewSignUp = findViewById(R.id.link_signup);
        btnOTP = findViewById(R.id.btn_retrive_otp);
        btnForgetPassword = findViewById(R.id.link_forget_password);
        textInputLayoutMobile = findViewById(R.id.input_layout_mobile);
        textInputLayoutPassword = findViewById(R.id.input_layout_password);
    }

    private void setListeners() {
        Objects.requireNonNull(textInputLayoutMobile.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    textInputLayoutMobile.setErrorEnabled(true);
                    textInputLayoutMobile.setError(context.getString(R.string.err_msg_fullname));
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

    /**
     * @desc submit Form
     */
    private void submitLogin() {
        // Loading Progress
//        if (!validateMobileNumber() && !validatePassword()) {
//            return;
//        }

        if (checkFields()) {
            String mobileNo = editTextMobile.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            checkLogin(mobileNo, password);
        }

//        if (!validatePassword()){
//            return;
//        }
    }

    private boolean checkFields() {
        boolean isPhoneValid = Validator.checkValidity(textInputLayoutMobile, editTextMobile.getText().toString(), context.getString(R.string.err_msg_mobile), "phone");
        boolean isPasswordValid = Validator.checkValidity(textInputLayoutPassword, editTextPassword.getText().toString(), context.getString(R.string.err_msg_password), "textPassword");

        return isPhoneValid && isPasswordValid;

    }

    private void checkLogin(final String mobileNo, final String password) {

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);

        // Hide the OTP Button
        btnOTP.setVisibility(View.GONE);

        // inactive button
        progressDialog.show();


        HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                // remove the progress bar
                Log.e("URL", AppConfig.URL_LOGIN);
                progressDialog.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    Log.e("Object", jsonObject.toString());

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
                        Timber.e("user after login -> %s",new Gson().toJson(user));


//                        progressDialog.dismiss();
                        if (response.equals("Please verify Your Account by OTP")) {
                            Intent verifyPhoneIntent = new Intent(LoginActivity.this, VerifyPhoneActivity.class);
                            if (checkFields()) {
                                verifyPhoneIntent.putExtra("mobile_no", mobileNo);
                            }
                            startActivity(verifyPhoneIntent);
                        } else {
                            // Move to another Activity

                            //Toast.makeText(context, "ami", Toast.LENGTH_SHORT).show();

                            if ((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                                Timber.e("activity login if -> %s",SharedPreManager.getInstance(context).isWaitingForLocationPermission());
                                Intent intent = new Intent(LoginActivity.this, PermissionActivity.class);
                                startActivity(intent);
                                finish();
                                //Toast.makeText(context, "nai ami", Toast.LENGTH_SHORT).show();
                            } else if (!SharedPreManager.getInstance(context).isWaitingForLocationPermission()){
                                Timber.e("activity login else if -> %s",SharedPreManager.getInstance(context).isWaitingForLocationPermission());
                                Intent intent = new Intent(LoginActivity.this, PermissionActivity.class);
                                startActivity(intent);
                                finish();
                                //Toast.makeText(context, "asi ami", Toast.LENGTH_SHORT).show();
                            }else {
                                Timber.e("activity login else -> %s",SharedPreManager.getInstance(context).isWaitingForLocationPermission());
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
//                            verifyPhoneIntent.putExtra("fromLoginPage", "fromLoginPage");
                            startActivity(verifyPhoneIntent);
                        }
                    } else {
                        showMessage(jsonObject.getString("message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Timber.e("Error Message -> %s ", error.getMessage());
                if (progressDialog != null) progressDialog.dismiss();
                showMessage(error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mobile_no", mobileNo);
                params.put("password", password);

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

    /**
     * @return boolean
     * @desc validate username
     */

    private boolean validateMobileNumber() {
        String mobileNumber = editTextMobile.getText().toString().trim();

        if (mobileNumber.isEmpty()) {
            textInputLayoutMobile.setError(getString(R.string.err_msg_mobile));
            requestFocus(editTextMobile);
            return false;
        } else {
            textInputLayoutMobile.setErrorEnabled(false);
        }

        return true;
    }

    /**
     * @return
     * @desc validate Password
     */
    private boolean validatePassword() {
        String password = editTextPassword.getText().toString().trim();
        if (password.isEmpty()) {
            textInputLayoutPassword.setError(getResources().getString(R.string.err_msg_password));
            requestFocus(editTextPassword);
            return false;

        } else {
            textInputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    /**
     * request focus
     * =============================================
     *
     * @param view
     */
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void showMessage(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private class MyTextWatcher implements TextWatcher {


        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.editTextMobileNumber:
                    validateMobileNumber();
                    break;

                case R.id.editTextPassword:
                    validatePassword();
                    break;
            }
        }

    }
}
