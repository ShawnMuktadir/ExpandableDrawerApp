package www.fiberathome.com.parkingapp.ui.verifyPhone;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import www.fiberathome.com.parkingapp.data.model.response.login.LoginResponse;
import www.fiberathome.com.parkingapp.data.model.response.verifyPhone.VerifyPhoneRepository;

public class VerifyPhoneViewModel extends ViewModel {
    private MutableLiveData<LoginResponse> data;

    public void init(String otp) {
        data = VerifyPhoneRepository.getInstance().verifyPhone(otp);
    }

    public LiveData<LoginResponse> getMutableData() {
        return data;
    }
}
