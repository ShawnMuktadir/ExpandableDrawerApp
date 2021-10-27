package www.fiberathome.com.parkingapp.utils;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import timber.log.Timber;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class DateTimeUtils {
    private static DateTimeUtils dateTimeUtils;

    public static DateTimeUtils getInstance() {
        if (dateTimeUtils == null) {
            dateTimeUtils = new DateTimeUtils();
        }

        return dateTimeUtils;
    }

    public String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        Timber.e("Current time => %s", c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return df.format(c);
    }

    public String getPSTTimeZoneCurrentDate() {
        //Output: ex: Wednesday, July 20, 2011
        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);
        df.setTimeZone(TimeZone.getTimeZone("PST"));
        return df.format(new Date());
    }

    public String getTime(String dateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
        SimpleDateFormat expectedFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        try {
            Date dT = dateFormat.parse(dateTime);
            assert dT != null;
            return expectedFormat.format(dT);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getDateNow() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
        return dateFormat.format(System.currentTimeMillis());
    }

    @SuppressLint("SimpleDateFormat")
    public String getCurrentTimeStamp() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            return dateFormat.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @SuppressLint("SimpleDateFormat")
    public String getCurrentDayTime() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
            return dateFormat.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
