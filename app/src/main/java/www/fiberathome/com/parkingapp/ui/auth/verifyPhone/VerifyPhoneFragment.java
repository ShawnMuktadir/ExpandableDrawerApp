package www.fiberathome.com.parkingapp.ui.auth.verifyPhone;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.data.model.response.login.LoginResponse;
import www.fiberathome.com.parkingapp.databinding.FragmentVerifyPhoneBinding;
import www.fiberathome.com.parkingapp.ui.auth.AuthViewModel;
import www.fiberathome.com.parkingapp.ui.auth.login.LoginActivity;
import www.fiberathome.com.parkingapp.utils.NoUnderlineSpan;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class VerifyPhoneFragment extends BaseFragment {

    private VerifyPhoneActivity context;
    private AuthViewModel authViewModel;
    FragmentVerifyPhoneBinding binding;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVerifyPhoneBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = (VerifyPhoneActivity) getActivity();
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
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
        super.onDestroyView();
    }

    private void checkLogin(final String mobileNo, final String password) {
        showLoading(context);
        authViewModel.initLogin(mobileNo, password);
        authViewModel.getLoginMutableLiveData().observe(requireActivity(), (@NonNull LoginResponse loginResponse) -> {
            hideLoading();
            if (!loginResponse.getError()) {
                ToastUtils.getInstance().showToastMessage(context, loginResponse.getMessage());
            } else if (loginResponse.getError() && !loginResponse.getAuthentication()) {
                Timber.e("error & authentication response -> %s", loginResponse.getMessage());
                ToastUtils.getInstance().showToastMessage(context, loginResponse.getMessage());
            } else {
                Timber.e("error -> %s", loginResponse.getMessage());
            }
        });
    }

    private void setListeners() {
        binding.txtPinEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.btnVerifyOtp.setOnClickListener(v -> {
            if (binding.txtPinEntry.getText() != null) {
                if (binding.txtPinEntry.getText().length() == 4) {
                    String otp = binding.txtPinEntry.getText().toString();
                    submitOTPVerification(otp);
                }
            } else {
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.enter_valid_otp));
            }
        });
    }

    private CountDownTimer countDownTimer;

    @SuppressLint("SetTextI18n")
    private void startCountDown() {
        countDownTimer = new CountDownTimer(150000, 1000) {
            @SuppressLint("DefaultLocale")
            public void onTick(long millisUntilFinished) {
                binding.tvCountDown.setText("" + String.format("%d min, %d sec remaining",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                binding.tvCountDown.setText(context.getResources().getString(R.string.please_wait));
                clickableSpanResendOTP();
            }
        }.start();
    }

    private void submitOTPVerification(String otp) {
        if (!otp.isEmpty()) {
            showLoading(context);
            authViewModel.initVerifyPhone(otp);
            authViewModel.getVerifyPhoneMutableData().observe(requireActivity(), (@NonNull LoginResponse response) -> {
                hideLoading();
                countDownTimer.cancel();
                if (response.getError()) {
                    ToastUtils.getInstance().showToastMessage(context, response.getMessage());
                } else if (!response.getError()) {
                    ToastUtils.getInstance().showToastMessage(context, response.getMessage());
                    context.startActivityWithFinishAffinity(LoginActivity.class);
                    ToastUtils.getInstance().showToastMessage(context, "Dear " + response.getUser().getFullName() + ", Your Registration Completed Successfully...");
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
                binding.textViewResentOtp.setMovementMethod(null);
                binding.textViewResentOtp.setClickable(false);

                NoUnderlineSpan mNoUnderlineSpan = new NoUnderlineSpan(context);
                if (binding.textViewResentOtp.getText() instanceof Spannable) {
                    Spannable s = (Spannable) binding.textViewResentOtp.getText();
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
        binding.textViewResentOtp.setText(spannableString);
        binding.textViewResentOtp.setMovementMethod(LinkMovementMethod.getInstance());
    }
}