package www.fiberathome.com.parkingapp.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import www.fiberathome.com.parkingapp.data.Repository.Repository;
import www.fiberathome.com.parkingapp.data.model.response.booking.BookingParkStatusResponse;
import www.fiberathome.com.parkingapp.data.model.response.booking.ReservationCancelResponse;
import www.fiberathome.com.parkingapp.data.model.response.booking.SensorAreaStatusResponse;
import www.fiberathome.com.parkingapp.data.model.response.parkingSlot.ParkingSlotResponse;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<SensorAreaStatusResponse> sensorAreaStatusResponseMutableLiveData;
    private MutableLiveData<ParkingSlotResponse> parkingSlotResponseMutableLiveData;
    private MutableLiveData<ReservationCancelResponse> reservationCancelResponseMutableLiveData;
    private MutableLiveData<BookingParkStatusResponse> bookingParkStatusResponseMutableLiveData;
    private final Repository repository;

    public HomeViewModel() {
        repository = Repository.getInstance();
    }

    public void initSensorAreaStatus() {
        sensorAreaStatusResponseMutableLiveData = repository.getSensorAreaStatus();
    }

    public void initFetchParkingSlotSensors() {
        parkingSlotResponseMutableLiveData = repository.fetchParkingSlotSensors();
    }

    public void initReservation(String mobile, String uid) {
        reservationCancelResponseMutableLiveData = repository.setBookingPark(mobile, uid);
    }

    public void initBookingParkStatus(String mobile) {
        bookingParkStatusResponseMutableLiveData = repository.getBookingParkStatus(mobile);
    }

    public LiveData<SensorAreaStatusResponse> getSensorAreaStatusLiveData() {
        return sensorAreaStatusResponseMutableLiveData;
    }

    public LiveData<ParkingSlotResponse> getParkingSlotResponseLiveData() {
        return parkingSlotResponseMutableLiveData;
    }

    public LiveData<ReservationCancelResponse> setParkedCar() {
        return reservationCancelResponseMutableLiveData;
    }

    public LiveData<BookingParkStatusResponse> getBookingParkStatus() {
        return bookingParkStatusResponseMutableLiveData;
    }

}