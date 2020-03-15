package www.fiberathome.com.parkingapp.base;

public class AppConfig {

    // ROOT URL
    public static final String BASE_URL = "http://163.47.157.195/parkingapp/";
    public static final String URL_REGISTER     = BASE_URL + "request_sms.php";
    public static final String URL_LOGIN        = BASE_URL + "verify_user.php";
    public static final String URL_VERIFY_OTP   = BASE_URL + "verify_otp.php";
    public static final String IMAGES_URL       = "http://163.47.157.195/parkingapp/uploads/";

    public static final String URL_FETCH_SENSORS = BASE_URL + "sensors.php";

    public static final String URL_FETCH_SENSOR_AREA = BASE_URL + "sensor_area.php";

    public static final String URL_FETCH_BOOKINGS = BASE_URL + "bookings.php";

    public static final String URL_STORE_RESERVATION = BASE_URL + "reservation_fnc.php";

    // Retrofit URLs
    public static final String URL_CHANGE_PASSWORD = BASE_URL + "change_password.php/";

    // SMS provider identification
    // It should match with your SMS gateway origin
    // You can use MSGIND, TESTER and ALERTS as sender ID
    // If you want custom sender Id, approve Msg91 to get one
    public static final String SMS_ORIGIN       = "PARKINGAPP_";
    public static final String SHARED_PREFERENCES = "";

}
