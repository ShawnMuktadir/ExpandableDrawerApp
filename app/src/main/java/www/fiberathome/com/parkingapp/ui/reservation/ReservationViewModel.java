package www.fiberathome.com.parkingapp.ui.reservation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import www.fiberathome.com.parkingapp.data.model.response.reservation.BookedResponse;
import www.fiberathome.com.parkingapp.data.model.response.reservation.BookingParkStatusResponse;
import www.fiberathome.com.parkingapp.data.model.response.reservation.CloseReservationResponse;
import www.fiberathome.com.parkingapp.data.model.response.reservation.ReservationCancelResponse;
import www.fiberathome.com.parkingapp.data.model.response.reservation.ReservationRepository;
import www.fiberathome.com.parkingapp.data.model.response.reservation.ReservationResponse;
import www.fiberathome.com.parkingapp.data.model.response.reservation.TimeSlotResponse;
import www.fiberathome.com.parkingapp.data.model.response.vehicle_list.UserVehicleListResponse;

public class ReservationViewModel extends ViewModel {
    private MutableLiveData<ReservationResponse> storeReservationData;
    private MutableLiveData<BookedResponse> bookedResponseMutableLiveData;
    private MutableLiveData<ReservationCancelResponse> cancelResponseMutableLiveData;
    private MutableLiveData<BookingParkStatusResponse> bookingParkStatusResponseMutableLiveData;
    private MutableLiveData<UserVehicleListResponse> vehicleListResponseMutableLiveData;
    private MutableLiveData<TimeSlotResponse> timeSlotResponseMutableLiveData;
    private MutableLiveData<CloseReservationResponse> closeReservationResponseMutableLiveData;

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

    public void initUserVehicleList(String mobile) {
        vehicleListResponseMutableLiveData = ReservationRepository.getInstance().getUserVehicleList(mobile);
    }

    public LiveData<UserVehicleListResponse> getUserVehicleListMutableLiveDat() {
        return vehicleListResponseMutableLiveData;
    }

    public void initTimeSlotList() {
        timeSlotResponseMutableLiveData = ReservationRepository.getInstance().getTimeSlot();
    }

    public LiveData<TimeSlotResponse> getTimeSlotListMutableLiveDat() {
        return timeSlotResponseMutableLiveData;
    }

    public void initCloseReservation(String mobileNo, String bookedUid,
                                     String tbl_id) {
        closeReservationResponseMutableLiveData = ReservationRepository.getInstance().endReservation(mobileNo, bookedUid, tbl_id);
    }

    public LiveData<CloseReservationResponse> getCloseReservationMutableLiveData() {
        return closeReservationResponseMutableLiveData;
    }
}
