package www.fiberathome.com.parkingapp.data.model.response.auth;

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

public class AuthRepository {
    private static AuthRepository loginRepository;
    private static AuthAPI authAPI;

    public AuthRepository() {
        authAPI = APIClient.createService(AuthAPI.class);
    }

    public static AuthRepository getInstance() {
        if (loginRepository == null) {
            loginRepository = new AuthRepository();
        }

        return loginRepository;
    }

    public MutableLiveData<LoginResponse> login(String mobileNo, String password) {
        MutableLiveData<LoginResponse> data = new MutableLiveData<>();

        authAPI.login(mobileNo, password).enqueue(new Callback<LoginResponse>() {
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

    public MutableLiveData<BaseResponse> createUser(String fullName, String password, String mobileNo,
                                                    String vehicleNo, String profileImage,
                                                    String profileImageName, String vehicleImage, String vehicleImageName) {
        MutableLiveData<BaseResponse> data = new MutableLiveData<>();

        authAPI.createUser(fullName, password, mobileNo, vehicleNo, profileImage,
                profileImageName, vehicleImage, vehicleImageName).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        data.setValue(response.body());
                    } else {
                        ErrorResponse errorResponse = ErrorUtils.parseError(response);
                        BaseResponse baseResponse = new BaseResponse();
                        baseResponse.setError(errorResponse.getError());
                        baseResponse.setMessage(errorResponse.getMessage());
                        data.setValue(baseResponse);
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

    public MutableLiveData<BaseResponse> createNewPassword(String newPassword, String confirmPassword, String mobileNo) {
        MutableLiveData<BaseResponse> data = new MutableLiveData<>();

        authAPI.createNewPassword(newPassword, confirmPassword, mobileNo).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        data.setValue(response.body());
                    } else {
                        ErrorResponse errorResponse = ErrorUtils.parseError(response);
                        BaseResponse baseResponse = new BaseResponse();
                        baseResponse.setError(errorResponse.getError());
                        baseResponse.setMessage(errorResponse.getMessage());
                        data.setValue(baseResponse);
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

    public MutableLiveData<BaseResponse> changePassword(String oldPassword, String newPassword,
                                                        String confirmPassword, String mobileNo) {
        MutableLiveData<BaseResponse> data = new MutableLiveData<>();

        authAPI.changePassword(oldPassword, newPassword, confirmPassword, mobileNo).
                enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                        if (response.body() != null) {
                            if (response.isSuccessful()) {
                                data.setValue(response.body());
                            } else {
                                ErrorResponse errorResponse = ErrorUtils.parseError(response);
                                BaseResponse baseResponse = new BaseResponse();
                                baseResponse.setError(errorResponse.getError());
                                baseResponse.setMessage(errorResponse.getMessage());
                                data.setValue(baseResponse);
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

    public MutableLiveData<BaseResponse> forgetPassword(String mobileNo) {
        MutableLiveData<BaseResponse> data = new MutableLiveData<>();

        authAPI.checkForgetPassword(mobileNo).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        data.setValue(response.body());
                    } else {
                        ErrorResponse errorResponse = ErrorUtils.parseError(response);
                        BaseResponse baseResponse = new BaseResponse();
                        baseResponse.setError(errorResponse.getError());
                        baseResponse.setMessage(errorResponse.getMessage());
                        data.setValue(baseResponse);
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

    public MutableLiveData<LoginResponse> verifyPhone(String otp) {
        MutableLiveData<LoginResponse> data = new MutableLiveData<>();

        authAPI.verifyOtp(otp).enqueue(new Callback<LoginResponse>() {
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
