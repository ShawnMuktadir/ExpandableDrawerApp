package www.fiberathome.com.parkingapp.ui.changePassword.changePassword;

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

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;
import www.fiberathome.com.parkingapp.data.model.response.login.LoginResponse;
import www.fiberathome.com.parkingapp.databinding.FragmentVerifyPhoneBinding;
import www.fiberathome.com.parkingapp.ui.login.LoginActivity;
import www.fiberathome.com.parkingapp.ui.verifyPhone.VerifyPhoneViewModel;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.NoUnderlineSpan;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class ChangePasswordOTPFragment extends BaseFragment {

    private ChangePasswordOTPActivity context;
    private VerifyPhoneViewModel verifyPhoneViewModel;

    FragmentVerifyPhoneBinding binding;

    public ChangePasswordOTPFragment() {
        // Required empty public constructor
    }

    public static ChangePasswordOTPFragment newInstance() {
        return new ChangePasswordOTPFragment();
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
        context = (ChangePasswordOTPActivity) getActivity();
        verifyPhoneViewModel = new ViewModelProvider(this).get(VerifyPhoneViewModel.class);
        setListeners();
        startCountDown();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
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

    private void checkForgetPassword(final String mobileNo) {
        showLoading(context);
        verifyPhoneViewModel.init(mobileNo);
        verifyPhoneViewModel.getMutableData().observe(requireActivity(), (@NonNull BaseResponse response) -> {
            hideLoading();
            if (!response.getError()) {
                ToastUtils.getInstance().showToastMessage(context, response.getMessage());
            } else {
                if (response.getMessage().equalsIgnoreCase("Try Again! Invalid Mobile Number.")) {
                    ToastUtils.getInstance().showToastMessage(context,
                            context.getResources().getString(R.string.mobile_number_not_exist));
                } else {
                    ToastUtils.getInstance().showToastMessage(context, response.getMessage());
                }
            }
        });
    }

    CountDownTimer countDownTimer;

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
                binding.btnVerifyOtp.setVisibility(View.VISIBLE);
                clickableSpanResendOTP();
            }
        }.start();
    }

    private void submitOTPVerification(final String otp) {
        if (!otp.isEmpty()) {
            showLoading(context);

            verifyPhoneViewModel.init(otp);
            verifyPhoneViewModel.getMutableData().observe(requireActivity(), (@NonNull LoginResponse loginResponse) -> {
                hideLoading();
                countDownTimer.cancel();
                if (loginResponse.getError()) {
                    ToastUtils.getInstance().showToastMessage(context, loginResponse.getMessage());
                } else if (!loginResponse.getError()) {
                    ToastUtils.getInstance().showToastMessage(context, loginResponse.getMessage());
                    context.startActivityWithFinishAffinity(LoginActivity.class);
                    ToastUtils.getInstance().showToastMessage(context, "Dear " + loginResponse.getUser().getFullName() + ", Your Registration Completed Successfully...");
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

        ForegroundColorSpan foregroundSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.light_blue));

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {
                // do some thing
                String mobileNo = context.getIntent().getStringExtra("mobile_no");
                if (ConnectivityUtils.getInstance().checkInternet(context)) {
                    checkForgetPassword(mobileNo);
                }

                binding.btnVerifyOtp.setVisibility(View.VISIBLE);
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