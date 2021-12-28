package www.fiberathome.com.parkingapp.data.model.response.search;

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

public class SearchRepository {
    private static SearchRepository searchRepository;
    private static SearchAPI searchAPI;

    public SearchRepository() {
        searchAPI = APIClient.createService(SearchAPI.class);
    }

    public static SearchRepository getInstance() {
        if (searchRepository == null) {
            searchRepository = new SearchRepository();
        }

        return searchRepository;
    }

    public MutableLiveData<BaseResponse> storeVisitedPlace(String mobileNo, String placeId, String endLatitude, String endLongitude,
                                                           String startLatitude, String startLongitude, String areaAddress) {
        MutableLiveData<BaseResponse> data = new MutableLiveData<>();

        searchAPI.storeSearchHistory(mobileNo, placeId, endLatitude, endLongitude,
                startLatitude, startLongitude, areaAddress).enqueue(new Callback<BaseResponse>() {
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

    public MutableLiveData<SearchVisitedPlaceResponse> getSearchHistory(String mobileNo) {
        MutableLiveData<SearchVisitedPlaceResponse> data = new MutableLiveData<>();

        searchAPI.getSearchHistory(mobileNo).enqueue(new Callback<SearchVisitedPlaceResponse>() {
            @Override
            public void onResponse(@NonNull Call<SearchVisitedPlaceResponse> call, @NonNull Response<SearchVisitedPlaceResponse> response) {
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        data.setValue(response.body());
                    } else {
                        ErrorResponse errorResponse = ErrorUtils.parseError(response);
                        data.setValue(convertSearchErrorResponse(errorResponse));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchVisitedPlaceResponse> call, @NonNull Throwable t) {
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

    private SearchVisitedPlaceResponse convertSearchErrorResponse(ErrorResponse errorResponse) {
        SearchVisitedPlaceResponse response = new SearchVisitedPlaceResponse();
        response.setError(errorResponse.getError());
        response.setMessage(errorResponse.getMessage());

        return response;
    }
}
