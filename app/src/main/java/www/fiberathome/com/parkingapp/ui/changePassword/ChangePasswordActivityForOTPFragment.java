package www.fiberathome.com.parkingapp.ui.changePassword;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
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
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.response.BaseResponse;
import www.fiberathome.com.parkingapp.model.response.login.LoginResponse;
import www.fiberathome.com.parkingapp.ui.changePassword.newPassword.ChangeNewPasswordActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;

@SuppressLint("NonConstantResourceId")
public class ChangePasswordActivityForOTPFragment extends BaseFragment {

    @BindView(R.id.btn_verify_otp)
    Button btnVerifyOtp;

    @BindView(R.id.btnResendOTP)
    Button btnResendOTP;

    @BindView(R.id.countdown)
    TextView tvCountdown;

    /*@BindView(R.id.txt_pin_entry)
    PinEntryEditText txtPinEntry;*/

    @BindView(R.id.txt_pin_entry)
    SquarePinField txtPinEntry;

    private Unbinder unbinder;

    private ChangePasswordActivityForOTPNew context;

    public ChangePasswordActivityForOTPFragment() {
        // Required empty public constructor
    }

    public static ChangePasswordActivityForOTPFragment newInstance() {
        return new ChangePasswordActivityForOTPFragment();
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

        context = (ChangePasswordActivityForOTPNew) getActivity();

        setListeners();

        startCountDown();
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
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

        btnResendOTP.setOnClickListener(v -> {
            String mobileNo = context.getIntent().getStringExtra("mobile_no");
            if (ApplicationUtils.checkInternet(context)) {
                checkForgetPassword(mobileNo);
            }

            btnVerifyOtp.setVisibility(View.VISIBLE);
            btnResendOTP.setVisibility(View.INVISIBLE);
            startCountDown();
        });
    }

    private void checkForgetPassword(final String mobileNo) {

        showLoading(context);

        ApiService service = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<BaseResponse> call = service.checkForgetPassword(mobileNo);

        // Gathering results.
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {

                Timber.e("response body-> %s", new Gson().toJson(response.body()));

                hideLoading();

                if (response.body() != null) {
                    if (!response.body().getError()) {
                        ApplicationUtils.showToastMessage(context, response.body().getMessage());
                    } else {
                        if (response.body().getMessage().equalsIgnoreCase("Try Again! Invalid Mobile Number.")) {
                            TastyToastUtils.showTastyErrorToast(context,
                                    context.getResources().getString(R.string.mobile_number_not_exist));
                        } else {
                            ApplicationUtils.showToastMessage(context, response.body().getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable errors) {
                Timber.e("Throwable Errors: -> %s", errors.toString());
                hideLoading();
                TastyToastUtils.showTastyErrorToast(context,
                        context.getResources().getString(R.string.mobile_number_not_exist));
            }
        });
    }

    CountDownTimer countDownTimer;

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
                btnResendOTP.setVisibility(View.VISIBLE);
                btnVerifyOtp.setVisibility(View.INVISIBLE);
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
                            ApplicationUtils.showToastMessage(context, "Sorry! Failed to Verify Your Account by OTP.");

                        } else if (!response.body().getError()) {

                            ApplicationUtils.showToastMessage(context, response.body().getMessage());

                            SharedData.getInstance().setOtp(otp);

                            Intent intent = new Intent(context, ChangeNewPasswordActivity.class);
                            startActivity(intent);
                            context.finish();
                            countDownTimer.cancel();
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
            TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.enter_valid_otp));
        }
    }
}
