package www.fiberathome.com.parkingapp.data.model.response.reservation;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.data.model.response.global.ErrorResponse;
import www.fiberathome.com.parkingapp.data.model.response.vehicle_list.UserVehicleListResponse;
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

    public MutableLiveData<BookingParkStatusResponse> getBookingParkStatus(String mobileNo) {
        MutableLiveData<BookingParkStatusResponse> data = new MutableLiveData<>();
        reservationAPI.getBookingParkStatus(mobileNo).enqueue(new Callback<BookingParkStatusResponse>() {
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

    public MutableLiveData<ReservationCancelResponse> setBookingPark(String mobileNo, String uid) {
        MutableLiveData<ReservationCancelResponse> data = new MutableLiveData<>();
        reservationAPI.setBookingPark(mobileNo, uid).enqueue(new Callback<ReservationCancelResponse>() {
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

    public MutableLiveData<TimeSlotResponse> getTimeSlot() {
        MutableLiveData<TimeSlotResponse> data = new MutableLiveData<>();
        reservationAPI.getTimeSlot().enqueue(new Callback<TimeSlotResponse>() {
            @Override
            public void onResponse(@NonNull Call<TimeSlotResponse> call,
                                   @NonNull Response<TimeSlotResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    ErrorResponse errorResponse = ErrorUtils.parseError(response);
                    TimeSlotResponse timeSlotResponse = new TimeSlotResponse();
                    timeSlotResponse.setError(errorResponse.getError());
                    timeSlotResponse.setMessage(errorResponse.getMessage());
                    data.setValue(timeSlotResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TimeSlotResponse> call, @NonNull Throwable t) {
                Timber.e(t.getCause());
                data.setValue(null);
            }
        });
        return data;
    }

    public MutableLiveData<UserVehicleListResponse> getUserVehicleList(String mobileNo) {
        MutableLiveData<UserVehicleListResponse> data = new MutableLiveData<>();
        reservationAPI.getUserVehicleList(mobileNo).enqueue(new Callback<UserVehicleListResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserVehicleListResponse> call,
                                   @NonNull Response<UserVehicleListResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    ErrorResponse errorResponse = ErrorUtils.parseError(response);
                    UserVehicleListResponse vehicleListResponse = new UserVehicleListResponse();
                    vehicleListResponse.setError(errorResponse.getError());
                    vehicleListResponse.setMessage(errorResponse.getMessage());
                    data.setValue(vehicleListResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserVehicleListResponse> call, @NonNull Throwable t) {
                Timber.e(t.getCause());
                data.setValue(null);
            }
        });
        return data;
    }

    public MutableLiveData<CloseReservationResponse> endReservation(String mobileNo, String bookedUid,
                                                                    String tbl_id) {
        MutableLiveData<CloseReservationResponse> data = new MutableLiveData<>();
        reservationAPI.endReservation(mobileNo, bookedUid, tbl_id).enqueue(new Callback<CloseReservationResponse>() {
            @Override
            public void onResponse(@NonNull Call<CloseReservationResponse> call,
                                   @NonNull Response<CloseReservationResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    ErrorResponse errorResponse = ErrorUtils.parseError(response);
                    CloseReservationResponse closeReservationResponse = new CloseReservationResponse();
                    closeReservationResponse.setError(errorResponse.getError());
                    closeReservationResponse.setMessage(errorResponse.getMessage());
                    data.setValue(closeReservationResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CloseReservationResponse> call, @NonNull Throwable t) {
                Timber.e(t.getCause());
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
