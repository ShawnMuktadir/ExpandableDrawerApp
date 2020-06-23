package www.fiberathome.com.parkingapp.view.activity.registration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.AppConfig;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.data.preference.SharedPreManager;
import www.fiberathome.com.parkingapp.model.User;
import www.fiberathome.com.parkingapp.utils.HttpsTrustManager;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.Validator;
import www.fiberathome.com.parkingapp.view.activity.login.LoginActivity;
import www.fiberathome.com.parkingapp.view.activity.main.MainActivity;

/**
 * This activity holds view with a custom 4-digit PIN EditText.
 */
public class VerifyPhoneActivity extends AppCompatActivity implements View.OnFocusChangeListener, View.OnKeyListener, TextWatcher {

    private static final String TAG = VerifyPhoneActivity.class.getSimpleName();
    private EditText mPinFirstDigitEditText;
    private EditText mPinSecondDigitEditText;
    private EditText mPinThirdDigitEditText;
    private EditText mPinForthDigitEditText;
    //    private EditText mPinFifthDigitEditText;
    private EditText mPinHiddenEditText;
    private Button btnVerifyOtp, btnChangePhoneNumber, btnResendOTP;
    private TextView countdown;
    private ProgressDialog progressDialog;
    private Context context;
    private String mobileNumber = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MainLayout(this, null));
        context = this;

        initUI();
        setPINListeners();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Verify OTP");

        btnVerifyOtp.setOnClickListener(v -> {
            if (checkFields()) {
                String otp = mPinHiddenEditText.getText().toString().trim();
                submitOTPVerification(otp);
            }
        });

        btnResendOTP.setOnClickListener(v -> {
            String mobileNo = getIntent().getStringExtra("mobile_no");
            String password = getIntent().getStringExtra("password");
            checkLogin(mobileNo, password);
            btnVerifyOtp.setVisibility(View.VISIBLE);
            btnResendOTP.setVisibility(View.INVISIBLE);
            startCountDown();
        });

//        btnChangePhoneNumber.setOnClickListener(v -> {
//            startActivity(new Intent(VerifyPhoneActivity.this, SignUpActivity.class));
//            finish();
//        });

        startCountDown();
    }

    private void checkLogin(final String mobileNo, final String password) {

        progressDialog = new ProgressDialog(VerifyPhoneActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);

        // Hide the OTP Button
//        btnOTP.setVisibility(View.GONE);

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
                        Timber.e("error not -> %s", jsonObject.getString("message"));
                        showMessage(jsonObject.getString("message"));

                        // getting the user from the response
//                        JSONObject userJson = jsonObject.getJSONObject("user");
//
//                        // creating a new user object
//                        User user = new User();
//                        user.setId(userJson.getInt("id"));
//                        user.setFullName(userJson.getString("fullname"));
//                        user.setMobileNo(userJson.getString("mobile_no"));
//                        user.setVehicleNo(userJson.getString("vehicle_no"));
//                        user.setProfilePic(userJson.getString("image"));
//
//                        // storing the user in sharedPreference
//                        SharedPreManager.getInstance(getApplicationContext()).userLogin(user);

                    } else if (jsonObject.getBoolean("error") && jsonObject.has("authentication")) {
                        // IF ERROR OCCURS AND AUTHENTICATION IS INVALID
//                        showMessage(jsonObject.getString("message"));
                        Timber.e("error & authentication response -> %s", jsonObject.getString("message"));
                    } else {
//                        showMessage(jsonObject.getString("message"));
                        Timber.e("error -> %s", jsonObject.getString("message"));
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
//                showMessage(error.getMessage());
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

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(VerifyPhoneActivity.this, SignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Hides soft keyboard.
     *
     * @param editText EditText which has focus
     */
    public void hideSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * Initialize EditText fields.
     */
    private void initUI() {
        mPinFirstDigitEditText = (EditText) findViewById(R.id.pin_first_edittext);
        mPinSecondDigitEditText = (EditText) findViewById(R.id.pin_second_edittext);
        mPinThirdDigitEditText = (EditText) findViewById(R.id.pin_third_edittext);
        mPinForthDigitEditText = (EditText) findViewById(R.id.pin_forth_edittext);
//        mPinFifthDigitEditText = (EditText) findViewById(R.id.pin_fifth_edittext);
        mPinHiddenEditText = (EditText) findViewById(R.id.inputOtp);

        btnVerifyOtp = findViewById(R.id.btn_verify_otp);
//        btnChangePhoneNumber = findViewById(R.id.btn_change_phone_number);
        btnResendOTP = findViewById(R.id.btnResendOTP);
        countdown = findViewById(R.id.countdown);
    }

    private boolean checkFields() {
        boolean isOTPValid = Validator.checkValidity(mPinHiddenEditText, context.getString(R.string.err_msg_pin), "text");
        return isOTPValid;

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        final int id = v.getId();
        switch (id) {
            case R.id.pin_first_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.pin_second_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.pin_third_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.pin_forth_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

//            case R.id.pin_fifth_edittext:
//                if (hasFocus) {
//                    setFocus(mPinHiddenEditText);
//                    showSoftKeyboard(mPinHiddenEditText);
//                }
//                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            final int id = v.getId();
            switch (id) {
                case R.id.inputOtp:
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
//                        if (mPinHiddenEditText.getText().length() == 5)
//                            mPinFifthDigitEditText.setText("");
//                        else
                        if (mPinHiddenEditText.getText().length() == 4)
                            mPinForthDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 3)
                            mPinThirdDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 2)
                            mPinSecondDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 1)
                            mPinFirstDigitEditText.setText("");

                        if (mPinHiddenEditText.length() > 0)
                            mPinHiddenEditText.setText(mPinHiddenEditText.getText().subSequence(0, mPinHiddenEditText.length() - 1));

                        return true;
                    }

                    break;

                default:
                    return false;
            }
        }

        return false;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        setDefaultPinBackground(mPinFirstDigitEditText);
        setDefaultPinBackground(mPinSecondDigitEditText);
        setDefaultPinBackground(mPinThirdDigitEditText);
        setDefaultPinBackground(mPinForthDigitEditText);
//        setDefaultPinBackground(mPinFifthDigitEditText);

        if (s.length() == 0) {
            setFocusedPinBackground(mPinFirstDigitEditText);
            mPinFirstDigitEditText.setText("");
        } else if (s.length() == 1) {
            setFocusedPinBackground(mPinSecondDigitEditText);
            mPinFirstDigitEditText.setText(s.charAt(0) + "");
            mPinSecondDigitEditText.setText("");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
//            mPinFifthDigitEditText.setText("");
        } else if (s.length() == 2) {
            setFocusedPinBackground(mPinThirdDigitEditText);
            mPinSecondDigitEditText.setText(s.charAt(1) + "");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
//            mPinFifthDigitEditText.setText("");
        } else if (s.length() == 3) {
            setFocusedPinBackground(mPinForthDigitEditText);
            mPinThirdDigitEditText.setText(s.charAt(2) + "");
            mPinForthDigitEditText.setText("");
//            mPinFifthDigitEditText.setText("");
        } else if (s.length() == 4) {
//            setFocusedPinBackground(mPinFifthDigitEditText);
            mPinForthDigitEditText.setText(s.charAt(3) + "");
//            mPinFifthDigitEditText.setText("");
            hideSoftKeyboard(mPinForthDigitEditText);
        }
//        else if (s.length() == 5) {
//            setDefaultPinBackground(mPinFifthDigitEditText);
//            mPinFifthDigitEditText.setText(s.charAt(4) + "");
//
//            hideSoftKeyboard(mPinFifthDigitEditText);
//        }
    }

    /**
     * Sets default PIN background.
     *
     * @param editText edit text to change
     */
    private void setDefaultPinBackground(EditText editText) {
        setViewBackground(editText, getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));
    }

    /**
     * Sets focus on a specific EditText field.
     *
     * @param editText EditText to set focus on
     */
    public static void setFocus(EditText editText) {
        if (editText == null)
            return;

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    /**
     * Sets focused PIN field background.
     *
     * @param editText edit text to change
     */
    private void setFocusedPinBackground(EditText editText) {
        setViewBackground(editText, getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));
    }

    /**
     * Sets listeners for EditText fields.
     */
    private void setPINListeners() {
        mPinHiddenEditText.addTextChangedListener(this);

        mPinFirstDigitEditText.setOnFocusChangeListener(this);
        mPinSecondDigitEditText.setOnFocusChangeListener(this);
        mPinThirdDigitEditText.setOnFocusChangeListener(this);
        mPinForthDigitEditText.setOnFocusChangeListener(this);
//        mPinFifthDigitEditText.setOnFocusChangeListener(this);

        mPinFirstDigitEditText.setOnKeyListener(this);
        mPinSecondDigitEditText.setOnKeyListener(this);
        mPinThirdDigitEditText.setOnKeyListener(this);
        mPinForthDigitEditText.setOnKeyListener(this);
//        mPinFifthDigitEditText.setOnKeyListener(this);
        mPinHiddenEditText.setOnKeyListener(this);
    }

    /**
     * Sets background of the view.
     * This method varies in implementation depending on Android SDK version.
     *
     * @param view       View to which set background
     * @param background Background to set to view
     */
    @SuppressWarnings("deprecation")
    public void setViewBackground(View view, Drawable background) {
        if (view == null || background == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }

    /**
     * Shows soft keyboard.
     *
     * @param editText EditText which has focus
     */
    public void showSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    private void startCountDown() {
        new CountDownTimer(150000, 1000) {
            //CountDownTimer(edittext1.getText()+edittext2.getText()) also parse it to long

            public void onTick(long millisUntilFinished) {
//                countdown.setText("seconds remaining: " + millisUntilFinished / 1000);
                countdown.setText("" + String.format("%d min, %d sec remaining",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                countdown.setText("Please wait...");
                btnResendOTP.setVisibility(View.VISIBLE);
                btnVerifyOtp.setVisibility(View.INVISIBLE);
                // enable the edit alert dialog
            }
        }.start();

    }

    private JSONObject jsonObject;
    private JSONObject userJson;

    private void submitOTPVerification(final String otp) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Verifying OTP...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();

        HttpsTrustManager.allowAllSSL();
        if (!otp.isEmpty()) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_VERIFY_OTP, response -> {
                Timber.e("URL -> %s", AppConfig.URL_VERIFY_OTP);
                progressDialog.dismiss();

                try {
                    jsonObject = new JSONObject(response);
                    Timber.e("object -> %s", jsonObject.toString());
                    if (jsonObject.getString("message").equals("Sorry! Failed to Verify Your Account by OYP.")){
                        showMessage("Sorry! Failed to Verify Your Account by OTP.");
                    }
                    if (!jsonObject.getBoolean("error")) {
                        showMessage(jsonObject.getString("message"));

                        // FETCHING USER INFORMATION FROM DATABASE
                        userJson = jsonObject.getJSONObject("user");

//                        if (SharedPreManager.getInstance(getApplicationContext()).isWaitingForSMS()) {
//                            SharedPreManager.getInstance(getApplicationContext()).setIsWaitingForSMS(false);

                        try {
                            mobileNumber = getIntent().getStringExtra("fromLoginPage");
                            Timber.e("mobileNumber -> %s", mobileNumber);
                            if (mobileNumber.equals("fromLoginPage")) {
                                Timber.e("if e dhukche");
                                Toast.makeText(context, "if e dhukche", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(context, LoginActivity.class));
                                finish();
                                showMessage("Dear " + userJson.getString("fullname") + ", Your Mobile Number is Verified...");
                            } else {
                                Timber.e("if else e dhukche");
                                Toast.makeText(context, "if else e dhukche", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(VerifyPhoneActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                                showMessage("Dear " + userJson.getString("fullname") + ", Your Registration Completed Successfully...");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showMessage(error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("otp", otp);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            ParkingApp.getInstance().addToRequestQueue(stringRequest, TAG);
        } else {
            // OTP IS EMPTY.
            progressDialog.dismiss();
//            TastyToastUtils.showTastyWarningToast(context, "Please Enter Valid OTP...");
            showMessage("Please Enter Valid OTP...");
        }

    }

    private void showMessage(String message) {
        Toast.makeText(VerifyPhoneActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Custom LinearLayout with overridden onMeasure() method
     * for handling software keyboard show and hide events.
     */
    public class MainLayout extends LinearLayout {

        public MainLayout(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.activity_verify_phone_otp, this);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            final int proposedHeight = MeasureSpec.getSize(heightMeasureSpec);
            final int actualHeight = getHeight();

            Log.d("TAG", "proposed: " + proposedHeight + ", actual: " + actualHeight);

            if (actualHeight >= proposedHeight) {
                // Keyboard is shown
                if (mPinHiddenEditText.length() == 0)
                    setFocusedPinBackground(mPinFirstDigitEditText);
                else
                    setDefaultPinBackground(mPinFirstDigitEditText);
            }

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissProgressDialog();

    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
// super.onBackPressed();
// Not calling **super**, disables back button in current screen.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        VerifyPhoneActivity.super.onBackPressed();
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
}

