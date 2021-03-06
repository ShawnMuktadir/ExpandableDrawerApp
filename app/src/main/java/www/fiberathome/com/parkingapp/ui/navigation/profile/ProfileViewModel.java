package www.fiberathome.com.parkingapp.ui.navigation.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;
import www.fiberathome.com.parkingapp.data.model.response.login.LoginResponse;
import www.fiberathome.com.parkingapp.data.model.response.profile.ProfileRepository;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<LoginResponse> data;

    public void initEditProfile(String fullName, String password, String mobileNo,
                                String vehicleNo, String profileImage, String profileImageName,
                                String vehicleImage, String vehicleImageName) {
        data = ProfileRepository.getInstance().editProfile(fullName, password, mobileNo, vehicleNo,
                profileImage, profileImageName, vehicleImage, vehicleImageName);
    }

    public LiveData<LoginResponse> getEditProfileMutableData() {
        return data;
    }

    private MutableLiveData<BaseResponse> mutableAddVehicleLiveData;

    public void initAddVehicle(String mobileNo, String licencePlateInfo) {
        mutableAddVehicleLiveData = ProfileRepository.getInstance().addVehicle(mobileNo, licencePlateInfo);
    }

    public LiveData<BaseResponse> getAddVehicleMutableData() {
        return mutableAddVehicleLiveData;
    }
}
