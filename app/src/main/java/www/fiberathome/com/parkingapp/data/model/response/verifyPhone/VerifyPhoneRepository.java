package www.fiberathome.com.parkingapp.data.model.response.verifyPhone;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.data.model.response.global.ErrorResponse;
import www.fiberathome.com.parkingapp.data.model.response.login.LoginResponse;
import www.fiberathome.com.parkingapp.data.source.APIClient;
import www.fiberathome.com.parkingapp.utils.ErrorUtils;

public class VerifyPhoneRepository {
    private static VerifyPhoneRepository verifyPhoneRepository;
    private static VerifyPhoneAPI verifyPhoneAPI;

    public VerifyPhoneRepository() {
        verifyPhoneAPI = APIClient.createService(VerifyPhoneAPI.class);
    }

    public static VerifyPhoneRepository getInstance() {
        if (verifyPhoneRepository == null) {
            verifyPhoneRepository = new VerifyPhoneRepository();
        }

        return verifyPhoneRepository;
    }

    public MutableLiveData<LoginResponse> verifyPhone(String otp) {
        MutableLiveData<LoginResponse> data = new MutableLiveData<>();

        verifyPhoneAPI.verifyOtp(otp).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        data.setValue(response.body());
                    } else {
                        ErrorResponse errorResponse = ErrorUtils.parseError(response);
                        data.setValue(convertErrorResponse(errorResponse));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Timber.d(t.getCause());
                data.setValue(null);
            }
        });

        return data;
    }

    private LoginResponse convertErrorResponse(ErrorResponse errorResponse) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setError(errorResponse.getError());
        loginResponse.setMessage(errorResponse.getMessage());

        return loginResponse;
    }
}
