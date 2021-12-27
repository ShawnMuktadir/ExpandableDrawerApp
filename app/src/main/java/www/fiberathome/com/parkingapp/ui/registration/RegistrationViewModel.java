package www.fiberathome.com.parkingapp.ui.registration;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;
import www.fiberathome.com.parkingapp.data.model.response.registration.RegistrationRepository;

public class RegistrationViewModel extends ViewModel {
    private MutableLiveData<BaseResponse> data;

    public void init(String fullName, String password, String mobileNo,
                     String vehicleNo, String profileImage,
                     String profileImageName, String vehicleImage, String vehicleImageName) {
        data = RegistrationRepository.getInstance().createUser(fullName, password, mobileNo, vehicleNo, profileImage,
                profileImageName, vehicleImage, vehicleImageName);
    }

    public LiveData<BaseResponse> getMutableData() {
        return data;
    }
}
