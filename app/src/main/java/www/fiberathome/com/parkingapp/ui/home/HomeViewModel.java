package www.fiberathome.com.parkingapp.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import www.fiberathome.com.parkingapp.data.model.response.home.HomeRepository;
import www.fiberathome.com.parkingapp.data.model.response.parkingSlot.ParkingSlotResponse;
import www.fiberathome.com.parkingapp.data.model.response.reservation.SensorAreaStatusResponse;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<SensorAreaStatusResponse> sensorAreaStatusResponseMutableLiveData;
    private MutableLiveData<ParkingSlotResponse> parkingSlotResponseMutableLiveData;
    private final HomeRepository repository;

    public HomeViewModel() {
        repository = HomeRepository.getInstance();
    }

    public void initSensorAreaStatus() {
        sensorAreaStatusResponseMutableLiveData = repository.getSensorAreaStatus();
    }

    public LiveData<SensorAreaStatusResponse> getSensorAreaStatusMutableLiveData() {
        return sensorAreaStatusResponseMutableLiveData;
    }

    public void initFetchParkingSlotSensors() {
        parkingSlotResponseMutableLiveData = repository.fetchParkingSlotSensors();
    }

    public LiveData<ParkingSlotResponse> getParkingSlotResponseMutableLiveData() {
        return parkingSlotResponseMutableLiveData;
    }
}