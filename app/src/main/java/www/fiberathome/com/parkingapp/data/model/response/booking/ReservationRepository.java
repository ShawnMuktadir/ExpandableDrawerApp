package www.fiberathome.com.parkingapp.data.model.response.booking;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.data.model.response.global.ErrorResponse;
import www.fiberathome.com.parkingapp.data.source.APIClient;
import www.fiberathome.com.parkingapp.utils.ErrorUtils;

public class ReservationRepository {
    private static ReservationRepository reservationRepository;
    private static ReservationAPI reservationAPI;

    public ReservationRepository() {
        reservationAPI = APIClient.createService(ReservationAPI.class);
    }

    public static ReservationRepository getInstance() {
        if (reservationRepository == null) {
            reservationRepository = new ReservationRepository();
        }

        return reservationRepository;
    }

    public MutableLiveData<ReservationResponse> storeReservation(String mobileNo, String startTime, String endTime,
                                                                 String spotId, String stage, String vehicleNo) {
        MutableLiveData<ReservationResponse> data = new MutableLiveData<>();

        reservationAPI.storeReservation(mobileNo, startTime, endTime, spotId, stage, vehicleNo).enqueue(new Callback<ReservationResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReservationResponse> call, @NonNull Response<ReservationResponse> response) {
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
            public void onFailure(@NonNull Call<ReservationResponse> call, @NonNull Throwable t) {
                Timber.d(t.getCause());
                data.setValue(null);
            }
        });

        return data;
    }

    public MutableLiveData<ReservationCancelResponse> cancelReservation(String mobileNo, String uid, String id) {
        MutableLiveData<ReservationCancelResponse> data = new MutableLiveData<>();

        reservationAPI.cancelReservation(mobileNo, uid, id).enqueue(new Callback<ReservationCancelResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReservationCancelResponse> call, @NonNull Response<ReservationCancelResponse> response) {
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        data.setValue(response.body());
                    } else {
                        ErrorResponse errorResponse = ErrorUtils.parseError(response);
                        data.setValue(convertCancelReservationErrorResponse(errorResponse));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReservationCancelResponse> call, @NonNull Throwable t) {
                Timber.d(t.getCause());
                data.setValue(null);
            }
        });

        return data;
    }

    public MutableLiveData<BookedResponse> getBookedPlace(String mobileNo) {
        MutableLiveData<BookedResponse> data = new MutableLiveData<>();

        reservationAPI.getBookedPlace(mobileNo).enqueue(new Callback<BookedResponse>() {
            @Override
            public void onResponse(@NonNull Call<BookedResponse> call, @NonNull Response<BookedResponse> response) {
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        data.setValue(response.body());
                    } else {
                        ErrorResponse errorResponse = ErrorUtils.parseError(response);
                        data.setValue(convertBookedPlaceErrorResponse(errorResponse));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookedResponse> call, @NonNull Throwable t) {
                Timber.d(t.getCause());
                data.setValue(null);
            }
        });

        return data;
    }

    private ReservationResponse convertErrorResponse(ErrorResponse errorResponse) {
        ReservationResponse response = new ReservationResponse();
        response.setError(errorResponse.getError());
        response.setMessage(errorResponse.getMessage());

        return response;
    }

    private BookedResponse convertBookedPlaceErrorResponse(ErrorResponse errorResponse) {
        BookedResponse response = new BookedResponse();
        response.setError(errorResponse.getError());
        response.setMessage(errorResponse.getMessage());

        return response;
    }

    private ReservationCancelResponse convertCancelReservationErrorResponse(ErrorResponse errorResponse) {
        ReservationCancelResponse response = new ReservationCancelResponse();
        response.setError(errorResponse.getError());
        response.setMessage(errorResponse.getMessage());

        return response;
    }
}
