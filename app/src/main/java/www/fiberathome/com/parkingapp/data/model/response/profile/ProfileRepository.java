package www.fiberathome.com.parkingapp.data.model.response.profile;

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

public class ProfileRepository {
    private static ProfileRepository profileRepository;
    private static ProfileAPI profileAPI;

    public ProfileRepository() {
        profileAPI = APIClient.createService(ProfileAPI.class);
    }

    public static ProfileRepository getInstance() {
        if (profileRepository == null) {
            profileRepository = new ProfileRepository();
        }

        return profileRepository;
    }

    public MutableLiveData<LoginResponse> editProfile(String fullName, String password, String mobileNo,
                                                      String vehicleNo, String profileImage, String profileImageName,
                                                      String vehicleImage, String vehicleImageName) {
        MutableLiveData<LoginResponse> data = new MutableLiveData<>();

        profileAPI.editProfile(fullName, password, mobileNo, vehicleNo, profileImage,
                profileImageName, vehicleImage, vehicleImageName).enqueue(new Callback<LoginResponse>() {
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

    public MutableLiveData<BaseResponse> addVehicle(String mobileNo, String licencePlateInfo) {
        MutableLiveData<BaseResponse> data = new MutableLiveData<>();

        profileAPI.setUserVehicle(mobileNo, licencePlateInfo).enqueue(new Callback<BaseResponse>() {
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
