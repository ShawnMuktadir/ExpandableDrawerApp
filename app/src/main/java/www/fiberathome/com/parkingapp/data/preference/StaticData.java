package www.fiberathome.com.parkingapp.data.preference;

/**
 * Created by George on 27-July-2016.
 */
public class StaticData {

    //    public static final String BASE_URL_IMAGE = "http://fangoo.montorlabs.com/";
    public static final String BASE_URL_IMAGE = "";
    public static final String BASE_URL_ADMIN_IMAGE = "http://beta.ponno.co/uploads/images/admin/";

    /**
     * These here is the must change static data for each application
     **/

    public static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    public static final String PHONE_REGEX = "^(?:\\+?88)?01[3-9]\\d{8}$";
    public static final String WEB_REGEX = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    //Session
    public static final String PREFERENCE_NAME = "app_preference";
    public static final String LOGIN_STATE = "login_state";
    public static final String FIRST_LOGIN = "first_login";
    public static final String UPDATE_AVAILABLE = "update_available";

    //User
    public static final String USER = "USER";
    public static final String SHOP = "SHOP";
    public static final String USER_LIST = "user_list";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_EMAIL = "email";
    public static final String USER_ROLE = "role";
    public static final String USER_PRIVILEGE = "privilege";
    public static final String USER_TOKEN = "token";
    public static final String USER_PASSWORD = "password";
    public static final String USER_DEFAULT_PRIVILEGE_LIST = "default_privilege_list";
    public static final String CATEGORIES = "categories";
    public static final String PAYMENT_URL = "payment_url";
    public static final String REDIRECT_URL = "redirect_url";
    public static final String START_DATE = "start_dt";
    public static final String END_DATE = "end_dt";
    public static final String LAST_LOGIN_TIME = "last_login_time";
    public static final String HOME_VIEW = "homeView";
    public static final String SHOP_IMAGE = "shop_image";
    public static final String ALL_PERMISSIONS = "all_permissions";
    public static final String ASSIGNED_PERMISSIONS = "assigned_permissions";
    public static final String OS = "os";
    public static final String VERSION = "version";
    public static final String BUTTON_DUMMY_DATA = "btn_dummy_data";
    public static final String CASH_DRAWER = "cash_drawer";
    public static final String BALANCE_SHEET = "balance_sheet";
    public static final String CASH_FLOW_REPORT = "cash_flow_report";
    public static final String PRODUCT_CATEGORY = "product_category";
    public static final String QR_RESULT = "qr_result";
    public static final String LAST_UPDATE_TIME = "last_update_time";
    public static final String SALE_PREORDER = "SALE_PREORDER";
    public static final String SALE_DRAFT = "SALE_DRAFT";
    public static final String SALE_ACTION = "SALE_ACTION";
    //    public static final String LANDING_FRAGMENT = "landing_fragment" ;
    public static String PRODUCTS = "products";

    public static final String IS_DELIVERY = "is_delivery";

    //Modules
    public static final String DUE_BOOK = "dueBook";
    public static final String DELIVERY_BOOK = "deliveryBook";
    public static final String PAYABLE_BOOK = "payableBook";
    public static final String ACCOUNT_BOOK = "accountBook";
    public static final String PRE_ORDER_BOOK = "preOrderBook";
    public static final String EMPLOYEE_BOOK = "employeeBook" ;
    public static final String CUSTOMER_BOOK = "customerBook";
    public static final String VENDOR_BOOK = "vendorBook";
    public static final String EXPENSE_BOOK = "expenseBook";

    //Inventory Management
    public static final String INVENTORY_OR_DISCOUNT_KEY = "inventory_or_discount_key";
    public static final String INVENTORY_VALUE = "inventory";
    public static final String DISCOUNT_VALUE = "discount";
    public static final String PURCHASE_VALUE = "purchase";

    //Firebase
    public static final String FCM_TOKEN = "fcm_token";
    public static final String NOTIFICATION_MESSAGE = "message";
    public static final String TITLE = "title";

    //Update Application
    public static final String APP_UPDATE = "app_update";
    public static final String UPDATE_URL = "url";
    public static final String FILE_NAME = "file_name";
    public static final int DOWNLOAD_ID = 112;

    //Settings item names
    public static final String TYPE_MAIN_SLIDER = "main_slider";
    public static final String TYPE_SECONDARY_SLIDER = "secondary_slider";
    public static final String TYPE_NEW_ARRIVAL = "new_arrivals";
    public static final String TYPE_BEST_SELLER = "best_sellers";
    public static final String TYPE_MOST_PURCHASED = "most_purchased";
    public static final String TYPE_MOST_SEARCHED = "most_searched";
    public static final String TYPE_LOGO = "logo";
    public static final String TYPE_CART_SETTINGS = "cart_settings";

    //Common activity utils
    public static final String IS_SEARCH = "is_search";

    //Orders
    public static final int PERMISSION_CALL_PHONE_REQUEST_CODE = 12;
    public static final int PERMISSION_LOCATION_REQUEST_CODE = 13;
    public static final int PERMISSION_MEDIA_REQUEST_CODE = 14;
    public static final int PERMISSION_CAMERA_REQUEST_CODE = 15;
    public static final int PERMISSION_GALLERY_REQUEST_CODE = 16;
    public static final int SELECT_PICTURE = 7;
    public static final String IMAGE_DIRECTORY = "/ponno";
    public static final String DUE_NUMBER = "due_number";

    //PRODUCT
    public static final String PRODUCT_KEY = "product_to_display";
    public static final String SALE_LIST = "sale_list";
    public static final String SALE = "sale_module";
    public static final String PURCHASE = "purchase_module";

    // User info/ billing
    public static final String PRINTER_NAME = "printer_name";
    public static final double VAT = 10.00;
    public static boolean isPrinterConnected = false;

    //Language and locale
    public static final String APP_LANGUAGE = "app_lang";

    // Account
    public static final long TWO_WEEK = 14 * 24 * 60 * 60 * 1000;

}
