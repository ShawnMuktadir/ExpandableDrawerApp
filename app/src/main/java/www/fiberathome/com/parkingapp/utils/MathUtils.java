package www.fiberathome.com.parkingapp.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;

import androidx.appcompat.app.AlertDialog;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.ui.booking.newBooking.BookingActivity;

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
        } catch (NumberFormatException ex) {
            intValue = 0.0f;
        } catch (NullPointerException ex) {
            intValue = 0.0f;
        }
        return intValue;
    }

    public int convertToInt(String value) {
        int intValue = 0;
        try {
            intValue = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            intValue = 0;
        } catch (NullPointerException ex) {
            intValue = 0;
        }
        return intValue;
    }

    public long convertToLong(String value) {
        long longValue = 0;
        try {
            longValue = Long.parseLong(value);
        } catch (NumberFormatException ex) {
            longValue = 0;
        } catch (NullPointerException ex) {
            longValue = 0;
        }
        return longValue;
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
}
