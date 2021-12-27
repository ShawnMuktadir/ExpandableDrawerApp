package www.fiberathome.com.parkingapp.data.model.response.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.data.model.response.global.ErrorResponse;
import www.fiberathome.com.parkingapp.data.source.APIClient;
import www.fiberathome.com.parkingapp.utils.ErrorUtils;

public class LoginRepository {
    private static LoginRepository loginRepository;
    private static LoginAPI loginAPI;

    public LoginRepository() {
        loginAPI = APIClient.createService(LoginAPI.class);
    }

    public static LoginRepository getInstance() {
        if (loginRepository == null) {
            loginRepository = new LoginRepository();
        }

        return loginRepository;
    }

    public MutableLiveData<LoginResponse> login(String mobileNo, String password) {
        MutableLiveData<LoginResponse> data = new MutableLiveData<>();

        loginAPI.login(mobileNo, password).enqueue(new Callback<LoginResponse>() {
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
