package www.fiberathome.com.parkingapp.ui.changePassword.changePassword;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import www.fiberathome.com.parkingapp.data.model.response.changePassword.ChangePasswordRepository;
import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;

public class ChangePasswordViewModel extends ViewModel {
    private MutableLiveData<BaseResponse> data;

    public void init(String oldPassword, String newPassword, String confirmPassword, String mobileNo) {
        data = ChangePasswordRepository.getInstance().changePassword(oldPassword, newPassword,
                confirmPassword, mobileNo);
    }

    public LiveData<BaseResponse> getMutableData() {
        return data;
    }
}
