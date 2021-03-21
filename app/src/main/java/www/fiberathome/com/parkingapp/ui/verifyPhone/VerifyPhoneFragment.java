package www.fiberathome.com.parkingapp.ui.verifyPhone;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.poovam.pinedittextfield.SquarePinField;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
import www.fiberathome.com.parkingapp.ui.signIn.LoginActivity;
import www.fiberathome.com.parkingapp.ui.signUp.SignUpActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;

import static www.fiberathome.com.parkingapp.utils.Constants.LANGUAGE_BN;
import static www.fiberathome.com.parkingapp.utils.Constants.LANGUAGE_EN;

@SuppressLint("NonConstantResourceId")
public class VerifyPhoneFragment extends BaseFragment {

    @BindView(R.id.btn_verify_otp)
    Button btnVerifyOtp;

    /*@BindView(R.id.btnResendOTP)
    Button btnResendOTP;*/

    @BindView(R.id.tv_count_down)
    TextView tvCountdown;

    @BindView(R.id.textViewResentOtp)
    TextView textViewResentOtp;

    /*@BindView(R.id.toolbar)
    Toolbar mToolbar;*/

    /*@BindView(R.id.txt_pin_entry)
    PinEntryEditTextNew txtPinEntry;*/

    @BindView(R.id.txt_pin_entry)
    SquarePinField txtPinEntry;

    private Unbinder unbinder;

    private VerifyPhoneActivity context;

    public VerifyPhoneFragment() {
        // Required empty public constructor
    }

    public static VerifyPhoneFragment newInstance() {
        return new VerifyPhoneFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_verify_phone_otp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder = ButterKnife.bind(this, view);

        context = (VerifyPhoneActivity) getActivity();

        setListeners();

        startCountDown();

        btnVerifyOtp.setVisibility(View.VISIBLE);
        //makes an underline on for Resend OTP Here
        SpannableString spannableString = new SpannableString(context.getResources().getString(R.string.if_you_have_not_received_any_otp_code_within_3_minute_then_resend));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {
                // do some thing
                String mobileNo = context.getIntent().getStringExtra("mobile_no");
                String password = context.getIntent().getStringExtra("password");
                checkLogin(mobileNo, password);
                startCountDown();
            }
        };

        if (Preferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_EN)) {
            spannableString.setSpan(clickableSpan, 61, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            textViewResentOtp.setText(spannableString);
            textViewResentOtp.setMovementMethod(LinkMovementMethod.getInstance());
        } else if (Preferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_BN)) {
            spannableString.setSpan(clickableSpan, 16, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            textViewResentOtp.setText(spannableString);
            textViewResentOtp.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    private void checkLogin(final String mobileNo, final String password) {

        showLoading(context);

        ApiService service = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<LoginResponse> call = service.loginUser(mobileNo, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {

                Timber.e("login response body-> %s", new Gson().toJson(response.body()));

                hideLoading();

                if (response.body() != null) {
                    if (!response.body().getError()) {
                        ApplicationUtils.showToastMessage(context, response.body().getMessage());
                    } else if (response.body().getError() && !response.body().getAuthentication()) {
                        // IF ERROR OCCURS AND AUTHENTICATION IS INVALID
                        Timber.e("error & authentication response -> %s", response.body().getMessage());
                        ApplicationUtils.showToastMessage(context, response.body().getMessage());
                    } else {
                        Timber.e("error -> %s", response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable errors) {
                Timber.e("Throwable Errors: -> %s", errors.toString());
                hideLoading();
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.something_went_wrong));
            }
        });
    }

    private void setListeners() {
        txtPinEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                /*if (s.toString().equals("1234")) {
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                } else if (s.length() == "1234".length()) {
                    Toast.makeText(context, "Incorrect", Toast.LENGTH_SHORT).show();
                    txtPinEntry.setText(null);
                }*/
            }
        });

        btnVerifyOtp.setOnClickListener(v -> {
            if (txtPinEntry.getText().length() == 4) {
                String otp = txtPinEntry.getText().toString();
                submitOTPVerification(otp);
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.enter_valid_otp));
            }
        });

        /*btnResendOTP.setOnClickListener(v -> {
            String mobileNo = context.getIntent().getStringExtra("mobile_no");
            String password = context.getIntent().getStringExtra("password");
            checkLogin(mobileNo, password);
            btnVerifyOtp.setVisibility(View.VISIBLE);
            btnResendOTP.setVisibility(View.GONE);
            startCountDown();
        });*/
    }

    private CountDownTimer countDownTimer;

    @SuppressLint("SetTextI18n")
    private void startCountDown() {
        countDownTimer = new CountDownTimer(150000, 1000) {
            @SuppressLint("DefaultLocale")
            public void onTick(long millisUntilFinished) {
                tvCountdown.setText("" + String.format("%d min, %d sec remaining",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                tvCountdown.setText(context.getResources().getString(R.string.please_wait));
            }
        }.start();
    }

    private void submitOTPVerification(String otp) {
        if (!otp.isEmpty()) {
            showLoading(context);

            ApiService service = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
            Call<LoginResponse> call = service.verifyOtp(otp);

            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {

                    Timber.e("response body-> %s", new Gson().toJson(response.body()));

                    hideLoading();

                    countDownTimer.cancel();

                    if (response.body() != null) {
                        Timber.e("response body not null -> %s", new Gson().toJson(response.body()));
                        if (response.body().getError() && response.body().getMessage().equalsIgnoreCase("Sorry! Failed to Verify Your Account by OYP.")) {
                            ApplicationUtils.showToastMessage(context, "Sorry! Failed to Verify Your Account by OTP.");
                        } else if (!response.body().getError()) {
                            ApplicationUtils.showToastMessage(context, response.body().getMessage());
                            context.startActivityWithFinishAffinity(LoginActivity.class);
                            ApplicationUtils.showToastMessage(context, "Dear " + response.body().getUser().getFullName() + ", Your Registration Completed Successfully...");
                        }
                    } else {
                        ApplicationUtils.showToastMessage(context, response.body().getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable errors) {
                    Timber.e("Throwable Errors: -> %s", errors.toString());
                    hideLoading();
                    ApplicationUtils.showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
                }
            });
        } else {
            hideLoading();
            ApplicationUtils.showToastMessage(context, context.getResources().getString(R.string.enter_valid_otp));
        }
    }

    private TextPaint textpaint;

    public boolean shouldHighlightWord = false;

    private void clickableSpanResendOTP() {
        Timber.e("clickableSpanResendOTP called");
        String completeString = context.getResources().getString(R.string.if_you_have_not_received_any_otp_code_within_3_minute_then_resend);
        String partToClick = "resend";
        ApplicationUtils.setSubTextColor(textViewResentOtp, completeString,
                partToClick, context.getResources().getColor(R.color.light_blue));
        ApplicationUtils.createLink(textViewResentOtp, completeString, partToClick,
                new ClickableSpan() {
                    @Override
                    public void onClick(@NotNull View widget) {
                        // your action
                        Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
                        String mobileNo = context.getIntent().getStringExtra("mobile_no");
                        String password = context.getIntent().getStringExtra("password");
                        checkLogin(mobileNo, password);
                        startCountDown();
                    }

                    @Override
                    public void updateDrawState(@NotNull TextPaint ds) {
                        super.updateDrawState(ds);
                        // this is where you set link color, underline, typeface etc.
                        int linkColor = ContextCompat.getColor(context, R.color.light_blue);
                        ds.setColor(linkColor);
                        ds.clearShadowLayer();
                        ds.setUnderlineText(false);

                        textpaint = ds;
                        if (shouldHighlightWord) {
                            textpaint.bgColor = Color.TRANSPARENT;
                            //textpaint.setARGB(255, 255, 255, 255);
                            textpaint.setColor(context.getResources().getColor(R.color.transparent));
                        }
                    }
                });
    }
}