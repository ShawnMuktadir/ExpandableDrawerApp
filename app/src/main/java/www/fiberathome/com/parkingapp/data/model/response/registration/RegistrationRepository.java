package www.fiberathome.com.parkingapp.data.model.response.registration;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;
import www.fiberathome.com.parkingapp.data.model.response.global.ErrorResponse;
import www.fiberathome.com.parkingapp.data.source.APIClient;
import www.fiberathome.com.parkingapp.utils.ErrorUtils;

public class RegistrationRepository {
    private static RegistrationRepository registrationRepository;
    private static RegistrationAPI registrationAPI;

    public RegistrationRepository() {
        registrationAPI = APIClient.createService(RegistrationAPI.class);
    }

    public static RegistrationRepository getInstance() {
        if (registrationRepository == null) {
            registrationRepository = new RegistrationRepository();
        }

        return registrationRepository;
    }

    public MutableLiveData<BaseResponse> createUser(String fullName, String password, String mobileNo,
                                               String vehicleNo, String profileImage,
                                               String profileImageName, String vehicleImage, String vehicleImageName) {
        MutableLiveData<BaseResponse> data = new MutableLiveData<>();

        registrationAPI.createUser(fullName, password, mobileNo, vehicleNo, profileImage,
                profileImageName, vehicleImage, vehicleImageName).enqueue(new Callback<BaseResponse>() {
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

    private BaseResponse convertErrorResponse(ErrorResponse errorResponse) {
        BaseResponse response = new BaseResponse();
        response.setError(errorResponse.getError());
        response.setMessage(errorResponse.getMessage());

        return response;
    }
}
