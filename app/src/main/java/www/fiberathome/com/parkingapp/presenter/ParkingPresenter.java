package www.fiberathome.com.parkingapp.presenter;

import java.util.ArrayList;

import www.fiberathome.com.parkingapp.data.APIInteractor;
import www.fiberathome.com.parkingapp.model.SensorArea;
import www.fiberathome.com.parkingapp.ui.parking.ParkingFragment;

public interface ParkingPresenter {

    ArrayList<SensorArea> fetchAreas();
}
