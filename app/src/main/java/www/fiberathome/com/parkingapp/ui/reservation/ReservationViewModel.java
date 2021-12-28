package www.fiberathome.com.parkingapp.ui.reservation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import www.fiberathome.com.parkingapp.data.model.response.booking.BookedResponse;
import www.fiberathome.com.parkingapp.data.model.response.booking.ReservationCancelResponse;
import www.fiberathome.com.parkingapp.data.model.response.booking.ReservationRepository;
import www.fiberathome.com.parkingapp.data.model.response.booking.ReservationResponse;

public class ReservationViewModel extends ViewModel {
    private MutableLiveData<ReservationResponse> storeReservationData;
    private MutableLiveData<BookedResponse> bookedResponseMutableLiveData;
    private MutableLiveData<ReservationCancelResponse> cancelResponseMutableLiveData;

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
}
