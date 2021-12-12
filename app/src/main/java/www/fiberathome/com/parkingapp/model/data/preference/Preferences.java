package www.fiberathome.com.parkingapp.model.data.preference;

import static android.content.Context.MODE_PRIVATE;
import static www.fiberathome.com.parkingapp.model.data.Constants.LANGUAGE_EN;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import www.fiberathome.com.parkingapp.model.BookedPlace;
import www.fiberathome.com.parkingapp.model.data.Constants;
import www.fiberathome.com.parkingapp.model.user.User;

@SuppressLint("StaticFieldLeak")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class Preferences {

    public static final String SHARED_PREF_NAME = "PARKINGAPP";
    private static final String KEY_FULLNAME = "fullname";
    private static final String KEY_MOBILE_NO = "mobile_no";
    private static final String KEY_VEHICLE_NO = "vehicle_no";
    private static final String KEY_VEHICLE_PIC = "vehicle_pic";
    private static final String KEY_PROFILE_PIC = "profile_pic";
    private static final String KEY_ID = "id";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_IS_LOCATION_PERMISSION = "KEY_IS_LOCATION_PERMISSION";
    private static final String KEY_VEHICLE_CLASS_DATA = "vehicle_class_data";

    // SMS Tags
    private static final String KEY_IS_WAITING_FOR_SMS = "isWaitingForSMS22";
    private static final String KEY_IS_UPDATE_REQUIRED = "isUpdateRequired";
    private static final String KEY_CHECKED_ITEM = "checked_item";
    private static final String SHARED_PREF_NAME_BOOKING = "SHARED_PREF_NAME_BOOKING";


    private static Preferences instance;

    private static Context mContext;
    public boolean isBookingCancelled = false;
    public boolean isGetDirectionClicked = false;

    public Preferences(Context context) {
        mContext = context;
    }

    public static synchronized Preferences getInstance(Context context) {
        if (instance == null) {
            instance = new Preferences(context);
        }

        return instance;
    }

    public void setUser(User user) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_FULLNAME, user.getFullName());
        editor.putString(KEY_MOBILE_NO, user.getMobileNo());
        editor.putString(KEY_VEHICLE_NO, user.getVehicleNo());
        editor.putString(KEY_PROFILE_PIC, user.getImage());
        editor.putString(KEY_VEHICLE_PIC, user.getVehicleImage());
        editor.apply();
    }

    public User getUser() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        User user = new User();
        user.setId(sharedPreferences.getInt(KEY_ID, -1));
        user.setFullName(sharedPreferences.getString(KEY_FULLNAME, null));
        user.setMobileNo(sharedPreferences.getString(KEY_MOBILE_NO, null));
        user.setVehicleNo(sharedPreferences.getString(KEY_VEHICLE_NO, null));
        user.setImage(sharedPreferences.getString(KEY_PROFILE_PIC, null));
        user.setVehicleImage(sharedPreferences.getString(KEY_VEHICLE_PIC, null));

        return user;
    }

    public void setBooked(BookedPlace booked) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME_BOOKING, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uid", booked.getBookedUid());
        editor.putString("areaName", booked.getAreaName());
        editor.putString("parkingSlotCount", booked.getParkingSlotCount());
        editor.putString("uid", booked.getBookedUid());
        editor.putString("lat", String.valueOf(booked.getLat()));
        editor.putString("lon", String.valueOf(booked.getLon()));
        editor.putBoolean("isBooked", booked.getIsBooked());
        editor.putBoolean("isPaid", booked.isPaid());
        editor.putBoolean("isCarParked", booked.isCarParked());
        editor.putBoolean("isExceedRunning", booked.isExceedRunning());
        editor.putString("placeId", booked.getPlaceId());
        editor.putString("reservation", booked.getReservation());
        editor.putLong("departedDate", booked.getDepartedDate());
        editor.putLong("arrivedDate", booked.getArriveDate());
        editor.putFloat("bill", booked.getBill());
        editor.putString("ps_Id", booked.getPsId());
        editor.apply();
    }

    public BookedPlace getBooked() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME_BOOKING, MODE_PRIVATE);
        BookedPlace bookedPlace = new BookedPlace();
        bookedPlace.setBookedUid(sharedPreferences.getString("uid", ""));
        bookedPlace.setAreaName(sharedPreferences.getString("areaName", ""));
        bookedPlace.setParkingSlotCount(sharedPreferences.getString("parkingSlotCount", ""));
        bookedPlace.setPlaceId(sharedPreferences.getString("placeId", ""));
        bookedPlace.setReservation(sharedPreferences.getString("reservation", ""));
        bookedPlace.setTicketSpotId(sharedPreferences.getString("ticketSpotId", ""));
        double lat = Double.parseDouble(sharedPreferences.getString("lat", "0"));
        double lon = Double.parseDouble(sharedPreferences.getString("lon", "0"));
        bookedPlace.setLat(lat);
        bookedPlace.setLon(lon);
        bookedPlace.setDepartedDate(sharedPreferences.getLong("departedDate", 0));
        bookedPlace.setArriveDate(sharedPreferences.getLong("arrivedDate", 0));
        bookedPlace.setIsBooked(sharedPreferences.getBoolean("isBooked", false));
        bookedPlace.setPaid(sharedPreferences.getBoolean("isPaid", false));
        bookedPlace.setCarParked(sharedPreferences.getBoolean("isCarParked", false));
        bookedPlace.setExceedRunning(sharedPreferences.getBoolean("isExceedRunning", false));
        bookedPlace.setBill(sharedPreferences.getFloat("bill", 0));
        bookedPlace.setPsId(sharedPreferences.getString("ps_Id", ""));
        return bookedPlace;
    }

    public void clearBooking() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME_BOOKING, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    // String IO
    @SuppressWarnings("SameParameterValue")
    private void saveValue(String key, String value) {
        if (key == null || key.isEmpty()) return;

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @SuppressWarnings("SameParameterValue")
    private String getValue(String key, String defaultValue) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    public String getVehicleClassData() {
        return getValue(Constants.KEY_VEHICLE_CLASS_DATA, LANGUAGE_EN);
    }

    public void saveVehicleClassData(String vehicleClassData) {
        saveValue(Constants.KEY_VEHICLE_CLASS_DATA, vehicleClassData);
    }

    public String getVehicleDivData() {
        return getValue(Constants.KEY_VEHICLE_DIV_DATA, LANGUAGE_EN);
    }

    public void saveVehicleDivData(String vehicleDivData) {
        saveValue(Constants.KEY_VEHICLE_DIV_DATA, vehicleDivData);
    }

    public String getBookedParkingData() {
        return getValue(Constants.KEY_Booked_Parking_DATA, null);
    }

    public void setBookedParkingData(String value) {
        saveValue(Constants.KEY_Booked_Parking_DATA, value);
    }

    public void setRadioButtonVehicleFormat(String key, boolean radioButtonValue) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, radioButtonValue);
        editor.apply();
    }

    public boolean getRadioButtonVehicleFormat(String key) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    public String getAppLanguage() {
        return getValue(Constants.LANGUAGE, LANGUAGE_EN);
    }

    public void setAppLanguage(String language) {
        saveValue(Constants.LANGUAGE, language);
    }

    public void setIsLocationPermissionGiven(boolean isLocationPermissionGiven) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOCATION_PERMISSION, isLocationPermissionGiven);
        editor.apply();
    }

    public boolean isWaitingForLocationPermission() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_IS_LOCATION_PERMISSION, false);
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getString(KEY_MOBILE_NO, null) != null;
    }

    public void logout() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void setIsWaitingForSMS(boolean isWaiting) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_WAITING_FOR_SMS, isWaiting);
        editor.apply();
    }

    public boolean isWaitingForSMS() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_IS_WAITING_FOR_SMS, false);
    }

    public void setIsUpdateRequired(boolean isUpdateRequired) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_UPDATE_REQUIRED, isUpdateRequired);
        editor.apply();
    }

    public boolean isUpdateRequired() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_IS_UPDATE_REQUIRED, false);
    }

    private int checkedItem;

    public int getCheckedItem() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_CHECKED_ITEM, 0);
    }

    public void setCheckedItem(int checkedItem) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_CHECKED_ITEM, checkedItem);
        editor.apply();
    }
}
