package www.fiberathome.com.parkingapp.data.model.response.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.data.model.response.global.ErrorResponse;
import www.fiberathome.com.parkingapp.data.model.response.parkingSlot.ParkingSlotResponse;
import www.fiberathome.com.parkingapp.data.model.response.reservation.SensorAreaStatusResponse;
import www.fiberathome.com.parkingapp.data.source.APIClient;
import www.fiberathome.com.parkingapp.utils.ErrorUtils;

public class HomeRepository {
    private static HomeRepository repository;
    private static HomeAPI homeAPI;

    public HomeRepository() {
        homeAPI = APIClient.createService(HomeAPI.class);
    }

    public static HomeRepository getInstance() {
        if (repository == null) {
            repository = new HomeRepository();
        }

        return repository;
    }

    public MutableLiveData<SensorAreaStatusResponse> getSensorAreaStatus() {
        MutableLiveData<SensorAreaStatusResponse> data = new MutableLiveData<>();
        homeAPI.getSensorAreaStatus().enqueue(new Callback<SensorAreaStatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<SensorAreaStatusResponse> call,
                                   @NonNull Response<SensorAreaStatusResponse> response) {

                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    ErrorResponse errorResponse = ErrorUtils.parseError(response);
                    SensorAreaStatusResponse sensorAreaStatusResponse = new SensorAreaStatusResponse();
                    sensorAreaStatusResponse.setError(errorResponse.getError());
                    sensorAreaStatusResponse.setMessage(errorResponse.getMessage());
                    data.setValue(sensorAreaStatusResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<SensorAreaStatusResponse> call, @NonNull Throwable t) {
                Timber.e(t.getCause());
                data.setValue(null);
            }
        });
        return data;
    }

    public MutableLiveData<ParkingSlotResponse> fetchParkingSlotSensors() {
        MutableLiveData<ParkingSlotResponse> data = new MutableLiveData<>();
        homeAPI.getParkingSlots().enqueue(new Callback<ParkingSlotResponse>() {
            @Override
            public void onResponse(@NonNull Call<ParkingSlotResponse> call,
                                   @NonNull Response<ParkingSlotResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    ErrorResponse errorResponse = ErrorUtils.parseError(response);
                    ParkingSlotResponse parkingSlotResponse = new ParkingSlotResponse();
                    parkingSlotResponse.setError(errorResponse.getError());
                    parkingSlotResponse.setMessage(errorResponse.getMessage());
                    data.setValue(parkingSlotResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ParkingSlotResponse> call, @NonNull Throwable t) {
                Timber.e(t.getCause());
                data.setValue(null);
            }
        });
        return data;
    }
}
