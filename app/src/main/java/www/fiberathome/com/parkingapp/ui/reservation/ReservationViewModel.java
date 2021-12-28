package www.fiberathome.com.parkingapp.ui.reservation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import www.fiberathome.com.parkingapp.data.model.response.booking.BookedResponse;
import www.fiberathome.com.parkingapp.data.model.response.booking.BookingParkStatusResponse;
import www.fiberathome.com.parkingapp.data.model.response.booking.ReservationCancelResponse;
import www.fiberathome.com.parkingapp.data.model.response.booking.ReservationRepository;
import www.fiberathome.com.parkingapp.data.model.response.booking.ReservationResponse;

public class ReservationViewModel extends ViewModel {
    private MutableLiveData<ReservationResponse> storeReservationData;
    private MutableLiveData<BookedResponse> bookedResponseMutableLiveData;
    private MutableLiveData<ReservationCancelResponse> cancelResponseMutableLiveData;
    private MutableLiveData<BookingParkStatusResponse> bookingParkStatusResponseMutableLiveData;

    public void storeReservationInit(String mobileNo, String startTime, String endTime,
                                     String spotId, String stage, String vehicleNo) {
        storeReservationData = ReservationRepository.getInstance().storeReservation(mobileNo, startTime,
                endTime, spotId, stage, vehicleNo);
    }

    public LiveData<ReservationResponse> getStoreReservationMutableData() {
        return storeReservationData;
    }

    public void getBookedPlaceInit(String mobileNo) {
        bookedResponseMutableLiveData = ReservationRepository.getInstance().getBookedPlace(mobileNo);
    }

    public LiveData<BookedResponse> getBookedResponseMutableData() {
        return bookedResponseMutableLiveData;
    }

    public void cancelReservationInit(String mobileNo, String uid, String id) {
        cancelResponseMutableLiveData = ReservationRepository.getInstance().
                cancelReservation(mobileNo, uid, id);
    }

    public LiveData<ReservationCancelResponse> getCancelReservationMutableData() {
        return cancelResponseMutableLiveData;
    }

    public void initBookingParkStatus(String mobile) {
        bookingParkStatusResponseMutableLiveData = ReservationRepository.getInstance().getBookingParkStatus(mobile);
    }

    public LiveData<BookingParkStatusResponse> getBookingParkStatus() {
        return bookingParkStatusResponseMutableLiveData;
    }

    public void initReservation(String mobile, String uid) {
        cancelResponseMutableLiveData = ReservationRepository.getInstance().setBookingPark(mobile, uid);
    }

    public LiveData<ReservationCancelResponse> setParkedCar() {
        return cancelResponseMutableLiveData;
    }
}
