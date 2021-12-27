package www.fiberathome.com.parkingapp.ui.forgetPassword;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import www.fiberathome.com.parkingapp.data.model.response.forgetPassword.ForgetPasswordRepository;
import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;

public class ForgetPasswordViewModel extends ViewModel {
    private MutableLiveData<BaseResponse> data;

    public void init(String mobileNo) {
        data = ForgetPasswordRepository.getInstance().forgetPassword(mobileNo);
    }

    public LiveData<BaseResponse> getMutableData() {
        return data;
    }
}
