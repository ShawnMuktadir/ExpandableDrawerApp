package www.fiberathome.com.parkingapp.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.data.preference.StaticData;

/**
 * Created by Zahidul_Islam_George on 08-November-2016.
 */
public class Validator {
    public static String EMAIL_ERROR = "ইমেইল সঠিক নয়";
    public static int PASSWORD_COUNT_MAX = 20;
    public static int PASSWORD_COUNT_MIN = 6;
    public static String PASSWORD_MATCH_ERROR = "পাসওয়ার্ড সঠিক হয়নি";
    public static String PASSWORD_COUNT_ERROR = "৬-২০ অক্ষরের মধ্যে পাসওয়ার্ড লিখুন";
    public static String NUMBER_ERROR = "এখানে শুধু নাম্বার লিখা যাবে";
    public static String PHONE_ERROR = "১১ ডিজিটের ফোন নাম্বার লিখুন। উদা: ০১*১১******";

    public static boolean checkValidity(EditText editText, String errorText, String textType) {
        String text = editText.getText().toString();
        if (TextUtils.isEmpty(text)) {
            editText.setError(errorText);
            return false;
        } else {
            if (textType.equalsIgnoreCase("email")) {
                if (text.matches(StaticData.EMAIL_REGEX)) {
                    return true;
                } else {
                    editText.setError(errorText);
                    return false;
                }
            } else if (textType.equalsIgnoreCase("password")) {
                if (text.length() < 6) {
                    editText.setError(errorText);
                    return false;
                } else {
                    return true;
                }
            } else if (textType.equalsIgnoreCase("number")) {
                if (isInputTypeNumber(text)) {
                    editText.setError(null);
                    return true;
                } else {
                    editText.setError(errorText);
                    return false;
                }
            } else if (textType.equalsIgnoreCase("text")) {
                if (!TextUtils.isEmpty(text)) {
                    editText.setError(null);
                    return true;
                } else {
                    editText.setError(errorText);
                    return false;
                }
            } else if (textType.equalsIgnoreCase("phone")) {
                if (text.matches(StaticData.PHONE_REGEX)) {
                    editText.setError(null);
                    return true;
                } else {
                    editText.setError(errorText);
                    return false;
                }
            } else {
                return true;
            }
        }
    }

    public static boolean checkValidity(TextInputLayout textInputLayout, String input, String errorText, String textType) {
        if (TextUtils.isEmpty(input)) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(errorText);
            return false;
        } else {
            if (textType.equalsIgnoreCase("number")) {
                if (isInputTypeNumber(input)) {
                    textInputLayout.setError(null);
                    textInputLayout.setErrorEnabled(false);
                    return true;
                } else {
                    textInputLayout.setError(errorText);
                    return false;
                }
            } else if (textType.equalsIgnoreCase("numberDecimal")) {
                if (isInputTypeDecimal(input)) {
                    textInputLayout.setError(null);
                    textInputLayout.setErrorEnabled(false);
                    return true;
                } else {
                    textInputLayout.setError(errorText);
                    return false;
                }
            } else if (textType.equalsIgnoreCase("email")) {
                if (input.matches(StaticData.EMAIL_REGEX)) {
                    textInputLayout.setError(null);
                    textInputLayout.setErrorEnabled(false);
                    return true;
                } else {
                    textInputLayout.setErrorEnabled(true);
                    textInputLayout.setError(EMAIL_ERROR);
                    return false;
                }
            } else if (textType.equalsIgnoreCase("phone")) {
//                if (input.matches(StaticData.PHONE_REGEX)) {
                if (input.length() >= 11) {
                    textInputLayout.setError(null);
                    textInputLayout.setErrorEnabled(false);
                    return true;
                } else {
                    textInputLayout.setErrorEnabled(true);
//                    textInputLayout.setError(PHONE_ERROR);
                    textInputLayout.setError("Enter valid mobile number!");
                    return false;
                }
            } else if (textType.equalsIgnoreCase("textPassword")) {
                if (input.length() > PASSWORD_COUNT_MAX || input.length() < 6) {
                    textInputLayout.setErrorEnabled(true);
                    textInputLayout.setError(PASSWORD_COUNT_ERROR);
                    return false;
                } else {
                    textInputLayout.setError(null);
                    textInputLayout.setErrorEnabled(false);
                    return true;
                }
            } else if (textType.equalsIgnoreCase("text")) {
                if (TextUtils.isEmpty(input)) {
                    textInputLayout.setErrorEnabled(true);
                    textInputLayout.setError(errorText);
                    return false;
                } else {
                    textInputLayout.setError(null);
                    textInputLayout.setErrorEnabled(false);
                    return true;
                }
            } else {
                textInputLayout.setError(null);
                textInputLayout.setErrorEnabled(false);
                return true;
            }
        }
    }

    public static boolean checkValidity(TextInputLayout textInputLayout, String input, String textType) {
        if (TextUtils.isEmpty(input)) {
            return true;
        } else {
            if (textType.equalsIgnoreCase("number")) {
                if (isInputTypeNumber(input)) {
                    textInputLayout.setError(null);
                    return true;
                } else {
                    textInputLayout.setError(NUMBER_ERROR);
                    return false;
                }
            } else if (textType.equalsIgnoreCase("phone")) {
                if (input.matches(StaticData.PHONE_REGEX)) {
                    textInputLayout.setError(null);
                    return true;
                } else {
                    textInputLayout.setError(PHONE_ERROR);
                    return false;
                }
            } else if (textType.equalsIgnoreCase("website")) {
                if (input.matches(StaticData.WEB_REGEX)) {
                    textInputLayout.setError(null);
                    return true;
                } else {
//                    textInputLayout.setError(WEB_ERROR);
                    return true;
                }
            } else {
                return true;
            }
        }
    }

    public static boolean isValidLicensePlate(TextInputLayout textInputLayout, TextInputEditText editText, String input, String errorText) {
        final Pattern licensePlatePattern
                = Pattern.compile("(([a-zA-Z]{3}[0-9]{3})|(\\w{2}-\\w{2}-\\w{2})|([0-9]{2}-[a-zA-Z]{3}-[0-9]{1})|([0-9]{1}-[a-zA-Z]{3}-[0-9]{2})|([a-zA-Z]{1}-[0-9]{3}-[a-zA-Z]{2}))\n");

        if (TextUtils.isEmpty(input)) {
            editText.setError(errorText);
            textInputLayout.setError(errorText);
            return false;
        } else {
            if (licensePlatePattern.matcher(input).matches()) {
                return true;
            } else {
                editText.setError(errorText);
                textInputLayout.setError(errorText);
                return false;
            }
        }
    }

    public static boolean isValidTextFormat(TextInputLayout textInputLayout, TextInputEditText editText, String input, String errorText, String regex) {
//        final Pattern licensePlatePattern
//                = Pattern.compile(regex);

        if (TextUtils.isEmpty(input)) {
//            editText.setError(errorText);
            textInputLayout.requestFocus();
            textInputLayout.setError(errorText);
            return false;
        } else {
            if (input.matches(regex)) {   //licensePlatePattern.matcher(input).matches()||
                return true;
            } else {
//                editText.setError(errorText);
                textInputLayout.requestFocus();
                textInputLayout.setError(errorText);
                return false;
            }
        }
    }

    public static boolean isInputTypeNumber(String text) {
        try {
            int number = Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isInputTypeDecimal(String text) {
        try {
            double number = Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean checkConfirmPassword(TextInputLayout til_confirm_password, String confirmPassword, String password) {
        if (confirmPassword.equals(password)) {
            til_confirm_password.setError(null);
            til_confirm_password.setErrorEnabled(false);
            return true;
        } else {
            til_confirm_password.setErrorEnabled(true);
            til_confirm_password.setError(PASSWORD_MATCH_ERROR);
            return false;
        }
    }
}
