package www.fiberathome.com.parkingapp.ui.verifyPhone;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import www.fiberathome.com.parkingapp.data.model.response.auth.AuthRepository;
import www.fiberathome.com.parkingapp.data.model.response.login.LoginResponse;

public class VerifyPhoneViewModel extends ViewModel {
    private MutableLiveData<LoginResponse> data;

    public void init(String otp) {
        data = AuthRepository.getInstance().verifyPhone(otp);
    }

    public LiveData<LoginResponse> getMutableData() {
        return data;
    }
}
