package www.fiberathome.com.parkingapp.ui.navigation.law;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import www.fiberathome.com.parkingapp.data.model.response.law.LawRepository;
import www.fiberathome.com.parkingapp.data.model.response.law.LawResponse;

public class LawViewModel extends ViewModel {
    private MutableLiveData<LawResponse> lawResponseMutableLiveData;
    private LawRepository repository;

    public void initFetchParkingLaws() {
        lawResponseMutableLiveData = LawRepository.getInstance().fetchParkingLaws();
    }

    public LiveData<LawResponse> getParkingLawsResponseMutableLiveData() {
        return lawResponseMutableLiveData;
    }
}