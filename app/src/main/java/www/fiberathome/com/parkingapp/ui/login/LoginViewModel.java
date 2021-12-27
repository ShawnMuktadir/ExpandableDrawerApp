package www.fiberathome.com.parkingapp.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import www.fiberathome.com.parkingapp.data.model.response.login.LoginRepository;
import www.fiberathome.com.parkingapp.data.model.response.login.LoginResponse;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<LoginResponse> data;

    public void init(String mobileNo, String password) {
        data = LoginRepository.getInstance().login(mobileNo, password);
    }

    public LiveData<LoginResponse> getMutableData() {
        return data;
    }
}
