package www.fiberathome.com.parkingapp.data.model.response.termsCondition;

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

public class TermConditionRepository {
    private static TermConditionRepository termConditionRepository;
    private static TermConditionAPI termConditionAPI;

    public TermConditionRepository() {
        termConditionAPI = APIClient.createService(TermConditionAPI.class);
    }

    public static TermConditionRepository getInstance() {
        if (termConditionRepository == null) {
            termConditionRepository = new TermConditionRepository();
        }

        return termConditionRepository;
    }

    public MutableLiveData<TermsConditionResponse> getTermCondition() {
        MutableLiveData<TermsConditionResponse> data = new MutableLiveData<>();
        termConditionAPI.getTermCondition().enqueue(new Callback<TermsConditionResponse>() {
            @Override
            public void onResponse(@NonNull Call<TermsConditionResponse> call,
                                   @NonNull Response<TermsConditionResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    ErrorResponse errorResponse = ErrorUtils.parseError(response);
                    TermsConditionResponse termsConditionResponse = new TermsConditionResponse();
                    termsConditionResponse.setError(errorResponse.getError());
                    termsConditionResponse.setMessage(errorResponse.getMessage());
                    data.setValue(termsConditionResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TermsConditionResponse> call, @NonNull Throwable t) {
                Timber.e(t.getCause());
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
