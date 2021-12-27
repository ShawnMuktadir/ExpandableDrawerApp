package www.fiberathome.com.parkingapp.data.source.api;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class AppConfig {
    // Parking App URL's
    public static final String BASE_URL = "http://163.47.157.198/parkingapp/";
    public static final String BASE_URL_PYTHON = "http://163.47.157.195:5000/";
    public static final String URL_REGISTER = BASE_URL + "request_sms.php";
    public static final String URL_LOGIN = BASE_URL + "verify_user.php";
    public static final String URL_VERIFY_OTP = BASE_URL + "verify_otp.php";
    public static final String URL_OTP_VERIFY = "http://163.47.157.195:5000/otp_varification";
    public static final String IMAGES_URL = "http://163.47.157.198/parkingapp/uploads/";
    public static final String URL_FETCH_SENSORS = BASE_URL + "sensors.php";
    public static final String URL_FETCH_SENSOR_AREA = BASE_URL + "sensor_area.php";
    public static final String URL_FETCH_BOOKINGS = BASE_URL + "bookings.php";
    public static final String URL_STORE_RESERVATION = BASE_URL + "reservation_fnc.php";
    public static final String URL_PRIVACY_POLICY = BASE_URL + "terms_condition.php";
    public static final String URL_FORGET_PASSWORD = BASE_URL + "verify_user_otp.php";
    public static final String URL_CHANGE_PASSWORD = BASE_URL + "change_password.php/";
    public static final String URL_CHANGE_PASSWORD_OTP = BASE_URL + "change_password_otp.php/";
    public static final String URL_SAVE_SEARCH_HISTORY_POST = BASE_URL + "visitor_place_history.php";
    public static final String URL_SEARCH_HISTORY_GET = BASE_URL + "visitor_place_tracker_get.php";
}
