package www.fiberathome.com.parkingapp.data.preference;

import android.location.Location;

import com.google.gson.Gson;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.model.BookingSensors;
import www.fiberathome.com.parkingapp.model.SensorArea;

/*Created by MiQ0717 on 23-Mar-2020.*/
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

    public String getRegistrationFullName() {
        return registrationFullName;
    }

    public void setRegistrationFullName(String registrationFullName) {
        this.registrationFullName = registrationFullName;
    }

    public String getLoginMobileNo() {
        return loginMobileNo;
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

    private BookingSensors bookingSensors;

    public BookingSensors getBookingSensors() {
        return bookingSensors;
    }

    public void setBookingSensors(BookingSensors bookingSensors) {
        Timber.e("BookingSensor Area in SharedData -> %s", new Gson().toJson(bookingSensors));
        this.bookingSensors = bookingSensors;
    }
}
