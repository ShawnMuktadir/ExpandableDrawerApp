package www.fiberathome.com.parkingapp.data.model.response.law;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.data.model.response.global.ErrorResponse;
import www.fiberathome.com.parkingapp.data.source.APIClient;
import www.fiberathome.com.parkingapp.utils.ErrorUtils;

public class LawRepository {
    private static LawRepository repository;
    private static LawAPI lawAPI;

    public LawRepository() {
        lawAPI = APIClient.createService(LawAPI.class);
    }

    public static LawRepository getInstance() {
        if (repository == null) {
            repository = new LawRepository();
        }

        return repository;
    }

    public MutableLiveData<LawResponse> fetchParkingLaws() {
        MutableLiveData<LawResponse> data = new MutableLiveData<>();
        lawAPI.getParkingLaws().enqueue(new Callback<LawResponse>() {
            @Override
            public void onResponse(@NonNull Call<LawResponse> call,
                                   @NonNull Response<LawResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    ErrorResponse errorResponse = ErrorUtils.parseError(response);
                    LawResponse lawResponse = new LawResponse();
                    lawResponse.setError(errorResponse.getError());
                    lawResponse.setMessage(errorResponse.getMessage());
                    data.setValue(lawResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LawResponse> call, @NonNull Throwable t) {
                Timber.e(t.getCause());
                data.setValue(null);
            }
        });
        return data;
    }
}
