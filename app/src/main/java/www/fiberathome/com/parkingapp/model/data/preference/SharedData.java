package www.fiberathome.com.parkingapp.model.data.preference;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.List;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.model.response.sensors.SensorArea;

public final class SharedData {

    private static SharedData instance = new SharedData();

    private SharedData() {
    }

    public static SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }

    public static void setInstance(SharedData instance) {
        SharedData.instance = instance;
    }

    public void setAllClean() {
        instance = null;
    }

    private String loginMobileNo;
    private String loginPassword;
    private String registrationFullName;
    private String otp;
    private String forgetPasswordMobile;
    private String selectedLanguage;

    public String getSelectedLanguage() {
        return selectedLanguage;
    }

    public void setSelectedLanguage(String selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }

    public String getForgetPasswordMobile() {
        return forgetPasswordMobile;
    }

    public void setForgetPasswordMobile(String forgetPasswordMobile) {
        this.forgetPasswordMobile = forgetPasswordMobile;
    }

    public String getRegistrationFullName() {
        return registrationFullName;
    }

    public void setRegistrationFullName(String registrationFullName) {
        this.registrationFullName = registrationFullName;
    }

    public String getLoginMobileNo() {
        return loginMobileNo;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public void setLoginMobileNo(String loginMobileNo) {
        this.loginMobileNo = loginMobileNo;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private SensorArea sensorArea;

    public SensorArea getSensorArea() {
        return sensorArea;
    }

    public void setSensorArea(SensorArea sensorArea) {
        Timber.e("Sensor Area in SharedData -> %s", new Gson().toJson(sensorArea));
        this.sensorArea = sensorArea;
    }

    public Location onConnectedLocation;

    public Location getOnConnectedLocation() {
        return onConnectedLocation;
    }

    public void setOnConnectedLocation(Location onConnectedLocation) {
        this.onConnectedLocation = onConnectedLocation;
    }

    public LatLng parkingLocation;

    public LatLng getParkingLocation() {
        return parkingLocation;
    }

    public void setParkingLocation(LatLng parkingLocation) {
        this.parkingLocation = parkingLocation;
    }

    private boolean locationPermission = false;

    public boolean getLocationPermission() {
        return locationPermission;
    }

    public void setLocationPermission(boolean locationPermission) {
        this.locationPermission = locationPermission;
    }

    private List<LatLng> dangerousArea;

    public List<LatLng> getDangerousArea() {
        return dangerousArea;
    }

    public void setDangerousArea(List<LatLng> dangerousArea) {
        this.dangerousArea = dangerousArea;
    }

    private Location lastLocation;

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }
}
