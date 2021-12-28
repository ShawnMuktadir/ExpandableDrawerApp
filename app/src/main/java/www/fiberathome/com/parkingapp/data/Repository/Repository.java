package www.fiberathome.com.parkingapp.data.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.data.model.response.booking.BookingParkStatusResponse;
import www.fiberathome.com.parkingapp.data.model.response.booking.ReservationCancelResponse;
import www.fiberathome.com.parkingapp.data.model.response.booking.SensorAreaStatusResponse;
import www.fiberathome.com.parkingapp.data.model.response.global.ErrorResponse;
import www.fiberathome.com.parkingapp.data.model.response.parkingSlot.ParkingSlotResponse;
import www.fiberathome.com.parkingapp.data.source.APIClient;
import www.fiberathome.com.parkingapp.data.source.api.ApiService;
import www.fiberathome.com.parkingapp.utils.ErrorUtils;

public class Repository {
    private static Repository repository;
    private final ApiService request;

    public static Repository getInstance() {
        if (repository == null) {
            repository = new Repository();
        }

        return repository;
    }

    public Repository() {
        request = APIClient.createService(ApiService.class);
    }

    public MutableLiveData<SensorAreaStatusResponse> getSensorAreaStatus() {
        MutableLiveData<SensorAreaStatusResponse> data = new MutableLiveData<>();
        request.getSensorAreaStatus().enqueue(new Callback<SensorAreaStatusResponse>() {
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
        request.getParkingSlots().enqueue(new Callback<ParkingSlotResponse>() {
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

    public MutableLiveData<ReservationCancelResponse> setBookingPark(String mobileNo, String uid) {
        MutableLiveData<ReservationCancelResponse> data = new MutableLiveData<>();
        request.setBookingPark(mobileNo, uid).enqueue(new Callback<ReservationCancelResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReservationCancelResponse> call,
                                   @NonNull Response<ReservationCancelResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    ErrorResponse errorResponse = ErrorUtils.parseError(response);
                    ReservationCancelResponse reservationCancelResponse = new ReservationCancelResponse();
                    reservationCancelResponse.setError(errorResponse.getError());
                    reservationCancelResponse.setMessage(errorResponse.getMessage());
                    data.setValue(reservationCancelResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReservationCancelResponse> call, @NonNull Throwable t) {
                Timber.e(t.getCause());
                data.setValue(null);
            }
        });
        return data;
    }

    public MutableLiveData<BookingParkStatusResponse> getBookingParkStatus(String mobileNo) {
        MutableLiveData<BookingParkStatusResponse> data = new MutableLiveData<>();
        request.getBookingParkStatus(mobileNo).enqueue(new Callback<BookingParkStatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<BookingParkStatusResponse> call,
                                   @NonNull Response<BookingParkStatusResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    ErrorResponse errorResponse = ErrorUtils.parseError(response);
                    BookingParkStatusResponse bookingParkStatusResponse = new BookingParkStatusResponse();
                    bookingParkStatusResponse.setError(errorResponse.getError());
                    bookingParkStatusResponse.setMessage(errorResponse.getMessage());
                    data.setValue(bookingParkStatusResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookingParkStatusResponse> call, @NonNull Throwable t) {
                Timber.e(t.getCause());
                data.setValue(null);
            }
        });
        return data;
    }
}
