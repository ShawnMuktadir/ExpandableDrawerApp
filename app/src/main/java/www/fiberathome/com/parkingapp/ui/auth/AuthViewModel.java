package www.fiberathome.com.parkingapp.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import www.fiberathome.com.parkingapp.data.model.response.auth.AuthRepository;
import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;
import www.fiberathome.com.parkingapp.data.model.response.login.LoginResponse;

public class AuthViewModel extends ViewModel {
    private MutableLiveData<BaseResponse> changePasswordMutableLiveData;
    private MutableLiveData<BaseResponse> forgotPasswordMutableLiveData;
    private MutableLiveData<BaseResponse> newPasswordMutableLiveData;

    public void initChangePassword(String oldPassword, String newPassword, String confirmPassword, String mobileNo) {
        changePasswordMutableLiveData = AuthRepository.getInstance().changePassword(oldPassword, newPassword,
                confirmPassword, mobileNo);
    }

    public LiveData<BaseResponse> getChangePasswordMutableData() {
        return changePasswordMutableLiveData;
    }

    public void initForgotPassword(String mobileNo) {
        forgotPasswordMutableLiveData = AuthRepository.getInstance().forgetPassword(mobileNo);
    }

    public LiveData<BaseResponse> getForgotPasswordMutableLiveData() {
        return forgotPasswordMutableLiveData;
    }

    public void initNewPassword(String newPassword, String confirmPassword, String mobileNo) {
        newPasswordMutableLiveData = AuthRepository.getInstance().createNewPassword(newPassword, confirmPassword, mobileNo);
    }

    public LiveData<BaseResponse> getNewPasswordMutableData() {
        return newPasswordMutableLiveData;
    }

    private MutableLiveData<LoginResponse> loginMutableLiveData;

    public void initLogin(String mobileNo, String password) {
        loginMutableLiveData = AuthRepository.getInstance().login(mobileNo, password);
    }

    public LiveData<LoginResponse> getLoginMutableLiveData() {
        return loginMutableLiveData;
    }

    private MutableLiveData<BaseResponse> registrationMutableLiveData;

    public void initRegistration(String fullName, String password, String mobileNo,
                                 String vehicleNo, String profileImage,
                                 String profileImageName, String vehicleImage, String vehicleImageName) {
        registrationMutableLiveData = AuthRepository.getInstance().createUser(fullName, password, mobileNo, vehicleNo, profileImage,
                profileImageName, vehicleImage, vehicleImageName);
    }

    public LiveData<BaseResponse> getRegistrationMutableLiveData() {
        return registrationMutableLiveData;
    }

    private MutableLiveData<LoginResponse> verifyPhoneMutableLiveData;

    public void initVerifyPhone(String otp) {
        verifyPhoneMutableLiveData = AuthRepository.getInstance().verifyPhone(otp);
    }

    public LiveData<LoginResponse> getVerifyPhoneMutableData() {
        return verifyPhoneMutableLiveData;
    }
}
