package www.fiberathome.com.parkingapp.ui.authPassword.newPassword;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import www.fiberathome.com.parkingapp.data.model.response.auth.AuthRepository;
import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;

public class NewPasswordViewModel extends ViewModel {
    private MutableLiveData<BaseResponse> data;

    public void init(String newPassword, String confirmPassword, String mobileNo) {
        data = AuthRepository.getInstance().createNewPassword(newPassword, confirmPassword, mobileNo);
    }

    public LiveData<BaseResponse> getMutableData() {
        return data;
    }
}
