package www.fiberathome.com.parkingapp.ui.verifyPhone;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.poovam.pinedittextfield.SquarePinField;

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
import www.fiberathome.com.parkingapp.model.response.login.LoginResponse;
import www.fiberathome.com.parkingapp.ui.signIn.LoginActivity;
import www.fiberathome.com.parkingapp.utils.NoUnderlineSpan;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings("unused")
public class VerifyPhoneFragment extends BaseFragment {

    @BindView(R.id.btn_verify_otp)
    Button btnVerifyOtp;

    @BindView(R.id.tv_count_down)
    TextView tvCountdown;

    @BindView(R.id.textViewResentOtp)
    TextView textViewResentOtp;

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
                        ToastUtils.getInstance().showToastMessage(context, response.body().getMessage());
                    } else if (response.body().getError() && !response.body().getAuthentication()) {
                        // IF ERROR OCCURS AND AUTHENTICATION IS INVALID
                        Timber.e("error & authentication response -> %s", response.body().getMessage());
                        ToastUtils.getInstance().showToastMessage(context, response.body().getMessage());
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
            if (txtPinEntry.getText() != null) {
                if (txtPinEntry.getText().length() == 4) {
                    String otp = txtPinEntry.getText().toString();
                    submitOTPVerification(otp);
                }
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.enter_valid_otp));
            }
        });
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
                clickableSpanResendOTP();
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
                            ToastUtils.getInstance().showToastMessage(context, "Sorry! Failed to Verify Your Account by OTP.");
                        } else if (!response.body().getError()) {
                            ToastUtils.getInstance().showToastMessage(context, response.body().getMessage());
                            context.startActivityWithFinishAffinity(LoginActivity.class);
                            ToastUtils.getInstance().showToastMessage(context, "Dear " + response.body().getUser().getFullName() + ", Your Registration Completed Successfully...");
                        }
                    } else {
                        ToastUtils.getInstance().showToastMessage(context, response.body().getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable errors) {
                    Timber.e("Throwable Errors: -> %s", errors.toString());
                    hideLoading();
                    ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
                }
            });
        } else {
            hideLoading();
            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.enter_valid_otp));
        }
    }

    private void clickableSpanResendOTP() {

        //makes an underline on for Resend OTP Click Here
        SpannableString spannableString = new SpannableString(context.getResources().getString(R.string.if_you_have_not_received_any_otp_code_within_3_minute_then_resend));

        int s1 = spannableString.toString().codePointAt(0);

        ForegroundColorSpan foregroundSpan = new ForegroundColorSpan(context.
                getResources().getColor(R.color.light_blue));

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {
                // do some thing
                String mobileNo = context.getIntent().getStringExtra("mobile_no");
                String password = context.getIntent().getStringExtra("password");
                checkLogin(mobileNo, password);
                startCountDown();
                textViewResentOtp.setMovementMethod(null);
                textViewResentOtp.setClickable(false);

                NoUnderlineSpan mNoUnderlineSpan = new NoUnderlineSpan(context);
                if (textViewResentOtp.getText() instanceof Spannable) {
                    Spannable s = (Spannable) textViewResentOtp.getText();
                    if (s1 >= 0x0980 && s1 <= 0x09E0) {
                        s.setSpan(mNoUnderlineSpan, 70, s.length(), Spanned.SPAN_MARK_MARK);
                        s.setSpan(foregroundSpan, 70, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {
                        s.setSpan(mNoUnderlineSpan, 63, s.length(), Spanned.SPAN_MARK_MARK);
                        s.setSpan(foregroundSpan, 63, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }

                if (s1 >= 0x0980 && s1 <= 0x09E0) {
                    spannableString.setSpan(new NoUnderlineSpan(context.getResources().getString(R.string.if_you_have_not_received_any_otp_code_within_3_minute_then_resend)),
                            70, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(foregroundSpan, 70, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    spannableString.setSpan(new NoUnderlineSpan(context.getResources().getString(R.string.if_you_have_not_received_any_otp_code_within_3_minute_then_resend)),
                            63, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(foregroundSpan, 63, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        };

        if (s1 >= 0x0980 && s1 <= 0x09E0) {
            spannableString.setSpan(clickableSpan, 70, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(foregroundSpan, 70, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            spannableString.setSpan(clickableSpan, 63, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(foregroundSpan, 63, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textViewResentOtp.setText(spannableString);
        textViewResentOtp.setMovementMethod(LinkMovementMethod.getInstance());
    }
}