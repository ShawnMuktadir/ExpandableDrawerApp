package www.fiberathome.com.parkingapp.ui.changePassword.newPassword;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;
import www.fiberathome.com.parkingapp.data.model.response.newPassword.NewPasswordRepository;

public class NewPasswordViewModel extends ViewModel {
    private MutableLiveData<BaseResponse> data;

    public void init(String newPassword, String confirmPassword, String mobileNo) {
        data = NewPasswordRepository.getInstance().createNewPassword(newPassword, confirmPassword, mobileNo);
    }

    public LiveData<BaseResponse> getMutableData() {
        return data;
    }
}
