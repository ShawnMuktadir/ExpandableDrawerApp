package www.fiberathome.com.parkingapp.data.model.response.changePassword;

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

public class ChangePasswordRepository {
    private static ChangePasswordRepository changePasswordRepository;
    private static ChangePasswordAPI changePasswordAPI;

    public ChangePasswordRepository() {
        changePasswordAPI = APIClient.createService(ChangePasswordAPI.class);
    }

    public static ChangePasswordRepository getInstance() {
        if (changePasswordRepository == null) {
            changePasswordRepository = new ChangePasswordRepository();
        }

        return changePasswordRepository;
    }

    public MutableLiveData<BaseResponse> changePassword(String oldPassword, String newPassword,
                                                        String confirmPassword, String mobileNo) {
        MutableLiveData<BaseResponse> data = new MutableLiveData<>();

        changePasswordAPI.changePassword(oldPassword, newPassword, confirmPassword, mobileNo).
                enqueue(new Callback<BaseResponse>() {
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
