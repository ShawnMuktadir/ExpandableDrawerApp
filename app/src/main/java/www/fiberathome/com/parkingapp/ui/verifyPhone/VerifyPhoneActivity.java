package www.fiberathome.com.parkingapp.ui.verifyPhone;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.model.api.ApiClient;
import www.fiberathome.com.parkingapp.model.api.ApiService;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.response.login.LoginResponse;
import www.fiberathome.com.parkingapp.ui.signIn.LoginActivity;
import www.fiberathome.com.parkingapp.ui.signUp.SignUpActivity;
import www.fiberathome.com.parkingapp.utils.Validator;

/**
 * This activity holds view with a custom 4-digit PIN EditText.
 */
public class VerifyPhoneActivity extends BaseActivity implements View.OnFocusChangeListener, View.OnKeyListener, TextWatcher {

    private static final String TAG = VerifyPhoneActivity.class.getSimpleName();
    private EditText mPinFirstDigitEditText;
    private EditText mPinSecondDigitEditText;
    private EditText mPinThirdDigitEditText;
    private EditText mPinForthDigitEditText;
    //private EditText mPinFifthDigitEditText;
    private EditText mPinHiddenEditText;
    private Button btnVerifyOtp, btnResendOTP;
    private TextView countdown;
    private Context context;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MainLayout(this, null));
        context = this;

        initUI();
        setPINListeners();

        setViewBackground(mPinFirstDigitEditText, context.getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));
        setViewBackground(mPinSecondDigitEditText, context.getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));
        setViewBackground(mPinThirdDigitEditText, context.getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));
        setViewBackground(mPinForthDigitEditText, context.getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(context.getResources().getString(R.string.verify_otp));
        mToolbar.setTitleTextColor(context.getResources().getColor(R.color.black));
        if (mToolbar.getNavigationIcon() != null) {
            mToolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        }

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

        startCountDown();
    }

    /*private void checkLogin(final String mobileNo, final String password) {

        progressDialog = ApplicationUtils.progressDialog(context, "Please wait...");

        HttpsTrustManager.allowAllSSL();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN, response -> {
            Timber.e("URL -> %s", AppConfig.URL_LOGIN);
            progressDialog.dismiss();

            try {
                JSONObject jsonObject = new JSONObject(response);

                Timber.e("jsonObject otp -> %s", jsonObject.toString());

                if (!jsonObject.getBoolean("error")) {
                    Timber.e("error not -> %s", jsonObject.getString("message"));
                    showMessage(jsonObject.getString("message"));
                } else if (jsonObject.getBoolean("error") && jsonObject.has("authentication")) {
                    // IF ERROR OCCURS AND AUTHENTICATION IS INVALID
                    showMessage(jsonObject.getString("message"));
                    mPinFirstDigitEditText.setText("");
                    mPinSecondDigitEditText.setText("");
                    mPinThirdDigitEditText.setText("");
                    mPinForthDigitEditText.setText("");
                    Timber.e("error & authentication response -> %s", jsonObject.getString("message"));
                } else {
                    //showMessage(jsonObject.getString("message"));
                    Timber.e("error -> %s", jsonObject.getString("message"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Timber.e("Error Message -> %s ", error.getMessage());
            if (progressDialog != null) progressDialog.dismiss();
            //showMessage(error.getMessage());
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
    }*/

    private void checkLogin(final String mobileNo, final String password) {

        showLoading(context);

        ApiService service = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<LoginResponse> call = service.loginUser(mobileNo, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {

                Timber.e("login response body-> %s", new Gson().toJson(response.body()));

                hideLoading();

                if (!response.body().getError()) {
                    showMessage(response.body().getMessage());
                } else if (response.body().getError() && !response.body().getAuthentication()) {
                    // IF ERROR OCCURS AND AUTHENTICATION IS INVALID
                    showMessage(response.body().getMessage());
                    mPinFirstDigitEditText.setText("");
                    mPinSecondDigitEditText.setText("");
                    mPinThirdDigitEditText.setText("");
                    mPinForthDigitEditText.setText("");
                    Timber.e("error & authentication response -> %s", response.body().getMessage());
                } else {
                    Timber.e("error -> %s", response.body().getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable errors) {
                Timber.e("Throwable Errors: -> %s", errors.toString());
            }
        });
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
        //mPinFifthDigitEditText = (EditText) findViewById(R.id.pin_fifth_edittext);
        mPinHiddenEditText = (EditText) findViewById(R.id.inputOtp);

        btnVerifyOtp = findViewById(R.id.btn_verify_otp);
        btnResendOTP = findViewById(R.id.btnResendOTP);
        countdown = findViewById(R.id.countdown);
    }

    private boolean checkFields() {
        return Validator.checkValidity(mPinHiddenEditText, context.getString(R.string.err_msg_pin), "text");

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        final int id = v.getId();
        switch (id) {
            case R.id.pin_first_edittext:

            case R.id.pin_second_edittext:

            case R.id.pin_third_edittext:

            case R.id.pin_forth_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

               /*case R.id.pin_fifth_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;*/
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
                        /*if (mPinHiddenEditText.getText().length() == 5)
                            mPinFifthDigitEditText.setText("");
                        else*/
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
        //setDefaultPinBackground(mPinFifthDigitEditText);

        if (s.length() == 0) {
            setFocusedPinBackground(mPinFirstDigitEditText);
            mPinFirstDigitEditText.setText("");
        } else if (s.length() == 1) {
            setFocusedPinBackground(mPinSecondDigitEditText);
            mPinFirstDigitEditText.setText(s.charAt(0) + "");
            mPinSecondDigitEditText.setText("");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
            //mPinFifthDigitEditText.setText("");
        } else if (s.length() == 2) {
            setFocusedPinBackground(mPinThirdDigitEditText);
            mPinSecondDigitEditText.setText(s.charAt(1) + "");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
            //mPinFifthDigitEditText.setText("");
        } else if (s.length() == 3) {
            setFocusedPinBackground(mPinForthDigitEditText);
            mPinThirdDigitEditText.setText(s.charAt(2) + "");
            mPinForthDigitEditText.setText("");
            //mPinFifthDigitEditText.setText("");
        } else if (s.length() == 4) {
            //setFocusedPinBackground(mPinFifthDigitEditText);
            mPinForthDigitEditText.setText(s.charAt(3) + "");
            //mPinFifthDigitEditText.setText("");
            hideSoftKeyboard(mPinForthDigitEditText);
        }
       /* else if (s.length() == 5) {
            setDefaultPinBackground(mPinFifthDigitEditText);
            mPinFifthDigitEditText.setText(s.charAt(4) + "");

            hideSoftKeyboard(mPinFifthDigitEditText);
        }*/
    }

    /**
     * Sets default PIN background.
     *
     * @param editText edit text to change
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void setDefaultPinBackground(EditText editText) {
        setViewBackground(editText, context.getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));
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
    @SuppressLint("UseCompatLoadingForDrawables")
    private void setFocusedPinBackground(EditText editText) {
        setViewBackground(editText, context.getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));
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
        //mPinFifthDigitEditText.setOnFocusChangeListener(this);

        mPinFirstDigitEditText.setOnKeyListener(this);
        mPinSecondDigitEditText.setOnKeyListener(this);
        mPinThirdDigitEditText.setOnKeyListener(this);
        mPinForthDigitEditText.setOnKeyListener(this);
        //mPinFifthDigitEditText.setOnKeyListener(this);
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
                //countdown.setText("seconds remaining: " + millisUntilFinished / 1000);
                countdown.setText("" + String.format("%d min, %d sec remaining",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                countdown.setText(context.getResources().getString(R.string.please_wait));
                btnResendOTP.setVisibility(View.VISIBLE);
                btnVerifyOtp.setVisibility(View.INVISIBLE);
                // enable the edit alert dialog
            }
        }.start();

    }

    private void submitOTPVerification(final String otp) {
        if (!otp.isEmpty()) {
            showLoading(context);

            ApiService service = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
            Call<LoginResponse> call = service.verifyOtp(otp);

            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {

                    Timber.e("response body-> %s", new Gson().toJson(response.body()));

                    hideLoading();

                    if (response.body() != null) {
                        Timber.e("response body not null -> %s", new Gson().toJson(response.body()));

                        if (response.body().getError() && response.body().getMessage().equalsIgnoreCase("Sorry! Failed to Verify Your Account by OYP.")) {
                            showMessage("Sorry! Failed to Verify Your Account by OTP.");
                        } else if (!response.body().getError()) {

                            showMessage(response.body().getMessage());

                            Intent intent = new Intent(VerifyPhoneActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                            showMessage("Dear " + response.body().getUser().getFullName() + ", Your Registration Completed Successfully...");
                        }
                    } else {
                        showMessage(response.body().getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable errors) {
                    Timber.e("Throwable Errors: -> %s", errors.toString());
                }
            });
        } else {
            hideLoading();
            showMessage(context.getResources().getString(R.string.enter_valid_otp));
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

            Timber.d("TAG proposed: " + proposedHeight + ", actual: " + actualHeight);

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
        hideLoading();
    }

    @Override
    public void onBackPressed() {

        //super.onBackPressed();

        Intent intent = new Intent(VerifyPhoneActivity.this, SignUpActivity.class);
        // Intent intent = new Intent(VerifyPhoneActivity.this, RegistrationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

        // Not calling **super**, disables back button in current screen.
        /*AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //VerifyPhoneActivity.super.onBackPressed();
                        finish();
                        TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                    }
                }).create();
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(arg0 -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
        });
        dialog.show();*/
    }
}

