package www.fiberathome.com.parkingapp.utils;

import android.content.Context;
import android.location.Location;

import java.text.NumberFormat;
import java.util.Locale;

import www.fiberathome.com.parkingapp.data.model.data.preference.LanguagePreferences;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class MathUtils {
    private static MathUtils mathUtils;

    public static MathUtils getInstance() {
        if (mathUtils == null) {
            mathUtils = new MathUtils();
        }

        return mathUtils;
    }

    public double convertToDouble(String value) {
        double intValue;
        try {
            intValue = Double.parseDouble(value);
        } catch (NumberFormatException | NullPointerException ex) {
            intValue = 0.00;
        }
        return intValue;
    }

    public float convertToFloat(String value) {
        float intValue;
        try {
            intValue = Float.parseFloat(value);
        } catch (NumberFormatException | NullPointerException ex) {
            intValue = 0.0f;
        }
        return intValue;
    }

    public int convertToInt(String value) {
        int intValue;
        try {
            intValue = Integer.parseInt(value);
        } catch (NumberFormatException | NullPointerException ex) {
            ex.getCause();
            intValue = 0;
        }
        return intValue;
    }

    public long convertToLong(String value) {
        long longValue;
        try {
            longValue = Long.parseLong(value);
        } catch (NumberFormatException | NullPointerException ex) {
            longValue = 0;
        }
        return longValue;
    }

    public double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        Location startPoint = new Location("locationA");
        startPoint.setLatitude(lat1);
        startPoint.setLongitude(lon1);

        Location endPoint = new Location("locationB");
        endPoint.setLatitude(lat2);
        endPoint.setLongitude(lon2);

        double distance = startPoint.distanceTo(endPoint);

        return (distance / 1000);
    }

    public String localeIntConverter(Context context, String number) {
        try {
            return NumberFormat.getInstance(new Locale(LanguagePreferences.getInstance(context).getAppLanguage(), "BAN")).format(MathUtils.getInstance().convertToInt(number));
        } catch (Exception e) {
            e.getCause();
            return "0";
        }
    }

    public String localeDoubleConverter(Context context, String number) {
        try {
            return NumberFormat.getInstance(new Locale(LanguagePreferences.getInstance(context).getAppLanguage(), "BAN")).format(MathUtils.getInstance().convertToDouble(number));
        } catch (Exception e) {
            e.getCause();
            return "0.0";
        }
    }
}
