package www.fiberathome.com.parkingapp.data.model.response.newPassword;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;
import www.fiberathome.com.parkingapp.data.model.response.global.ErrorResponse;
import www.fiberathome.com.parkingapp.data.model.response.login.LoginResponse;
import www.fiberathome.com.parkingapp.data.source.APIClient;
import www.fiberathome.com.parkingapp.utils.ErrorUtils;

public class NewPasswordRepository {
    private static NewPasswordRepository passwordRepository;
    private static NewPasswordAPI newPasswordAPI;

    public NewPasswordRepository() {
        newPasswordAPI = APIClient.createService(NewPasswordAPI.class);
    }

    public static NewPasswordRepository getInstance() {
        if (passwordRepository == null) {
            passwordRepository = new NewPasswordRepository();
        }

        return passwordRepository;
    }

    public MutableLiveData<BaseResponse> createNewPassword(String newPassword, String confirmPassword, String mobileNo) {
        MutableLiveData<BaseResponse> data = new MutableLiveData<>();

        newPasswordAPI.createNewPassword(newPassword, confirmPassword, mobileNo).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
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
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
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
