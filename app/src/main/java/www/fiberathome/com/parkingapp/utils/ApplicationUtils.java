package www.fiberathome.com.parkingapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;
//import www.fiberathome.com.parkingapp.BuildConfig;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.preference.StaticData;
import www.fiberathome.com.parkingapp.preference.utils.ConnectivityInterceptor;
import www.fiberathome.com.parkingapp.view.fragments.HomeFragment;

public class ApplicationUtils {

    public static void setTextColor(TextView tvText, Context context, int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvText.setTextColor(context.getColor(resId));
        } else {
            tvText.setTextColor(context.getResources().getColor(resId));
        }
    }

    public static void setTextColor(TextInputEditText textInputEditText, Context context, int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textInputEditText.setTextColor(context.getColor(resId));
        } else {
            textInputEditText.setTextColor(context.getResources().getColor(resId));
        }
    }

    public static void setSeparateTextColor(TextView tvText, String color, String text, String coloredText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvText.setText(Html.fromHtml(text + "<font color='" + color + "'>" + coloredText + "</font>", 0));
        } else {
            tvText.setText(Html.fromHtml(text + "<font color='" + color + "'>" + coloredText + "</font>"));
        }
    }

    public static void setHintTextColor(TextView tvText, Context context, int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvText.setHintTextColor(context.getColor(resId));
        } else {
            tvText.setHintTextColor(context.getResources().getColor(resId));
        }
    }

    public static void setHintTextColor(TextInputEditText tvText, Context context, int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvText.setHintTextColor(context.getColor(resId));
        } else {
            tvText.setHintTextColor(context.getResources().getColor(resId));
        }
    }

    public static void setBackground(Context context, View source, int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            source.setBackground(context.getDrawable(resId));
        } else {
            source.setBackground(context.getResources().getDrawable(resId));
        }
    }

    //set margin programmatically
    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public static Typeface getTypeface(int style, Context context) {
        switch (style) {
            case Typeface.BOLD:
                return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
            case Typeface.ITALIC:
                return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Italic.ttf");
            case Typeface.NORMAL:
                return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
            default:
                return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        }
    }

    public static Drawable resizeImage(int resId, int w, int h, Context context) {
        // load the origial Bitmap
        Bitmap BitmapOrg = BitmapFactory.decodeResource(context.getResources(), resId);
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;
        // calculate the scale
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);
        return new BitmapDrawable(resizedBitmap);
    }

    public static OkHttpClient getClient(final Context context) {

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient
                .Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Request.Builder requestBuilder = request.newBuilder();
                        requestBuilder.addHeader(StaticData.OS, "Android");
//                        requestBuilder.addHeader(StaticData.VERSION, BuildConfig.VERSION_NAME);
                        if (ApplicationUtils.checkInternet(context)) {
                            requestBuilder.header("Cache-Control", "public, max-age=" + 60);
                        } else {
                            requestBuilder.header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7);
                        }
                        request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(new ConnectivityInterceptor(context))
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .build();
        return client;
    }

    public static boolean checkInternet(Context context) {
        ConnectivityManager check = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = check.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static void showMessageDialog(String message, Context context) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message);
            builder.setCancelable(true);
            builder.setPositiveButton(context.getString(R.string.ok), (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            // Let's start with animation work. We just need to create a style and use it here as follows.
            if (alertDialog.getWindow() != null)
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.slidingDialogAnimation;

        }
    }

    public static double convertToDouble(String value) {
        double intValue;
        try {
            intValue = Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            intValue = 0.00;
        } catch (NullPointerException ex) {
            intValue = 0.00;
        }
        return intValue;
    }

    public static float convertToFloat(String value) {
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

    public static int convertToInt(String value) {
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

    public static long convertToLong(String value) {
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

    public static void createToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void hideKeyboard(Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(((Activity) mContext).getWindow().getCurrentFocus().getWindowToken(), 0);
    }

    public static void hideKeyboard(final Activity activity) {
        Timber.e("hideKeyboard -> ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (activity != null) {
                                final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                                final View view = activity.getCurrentFocus();
                                if (view != null) {
                                    final IBinder binder = view.getWindowToken();
                                    imm.hideSoftInputFromWindow(binder, 0);
                                    imm.showSoftInputFromInputMethod(binder, 0);
                                }
                            }
                        } catch (final Exception e) {
                            Timber.d(e, "-> %s Exception to hide keyboard", ApplicationUtils.class.getSimpleName());
                        }
                    }
                });
            }
        }).start();
    }

    public static String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        Timber.e("Current time => %s", c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public static String getTime(String dateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
        SimpleDateFormat expectedFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        try {
            Date dT = dateFormat.parse(dateTime);
            return expectedFormat.format(dT);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String addCountryPrefix(String number) {
        if (number != null && android.text.TextUtils.isDigitsOnly(number)) {
            if (number.length() > 2) {
                if (number.substring(0, 2).equals("88")) {
                    return number;
                } else
                    return "+88" + number;
            } else
                return "88";
        } else
            return "88";

    }

    public static void showExitDialog(final Activity activity) {
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(activity, R.style.Theme_AppCompat_NoActionBar);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.exit_alert, null);
        dialogBuilder.setView(dialogView);
        TextView tv_exit = (TextView) dialogView.findViewById(R.id.tv_exit);
        View outside_view = dialogView.findViewById(R.id.outside_view);
        final android.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCancelable(false);
        alertDialog.show();

//        WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
//        lp.dimAmount = 0.4f;
//        alertDialog.getWindow().setAttributes(lp);
//        alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        outside_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                alertDialog.dismiss();

                return false;
            }
        });

        tv_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                activity.finish();
                TastyToastUtils.showTastySuccessToast(activity, "Thanks for being with us");
            }
        });

    }

    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment, int frameId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frameId, fragment);
        transaction.commit();
    }

    public static void addFragmentToActivityWithBackStack(@NonNull FragmentManager fragmentManager,
                                                          @NonNull Fragment fragment, int frameId, String title) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frameId, fragment);
        transaction.addToBackStack(title);
        transaction.commit();
    }

    public static void refreshFragment(@NonNull FragmentManager fragmentManager,
                                       @NonNull Fragment fragment, int frameId) {
        // Reload current fragment
        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.setCustomAnimations(android.R.anim.fade_in,
//                android.R.anim.fade_out);
        transaction.replace(frameId, fragment);
        transaction.detach(fragment);
        transaction.attach(fragment);
        transaction.commit();
    }

    public static Fragment recreateFragment(@NonNull FragmentManager fragmentManager,
                                            @NonNull Fragment fragment) {
        try {
            Fragment.SavedState savedState = fragmentManager.saveFragmentInstanceState(fragment);

            Fragment newInstance = fragment.getClass().newInstance();
            newInstance.setInitialSavedState(savedState);

            return newInstance;
        } catch (Exception e) // InstantiationException, IllegalAccessException
        {
            throw new RuntimeException("Cannot reinstantiate fragment " + fragment.getClass().getName(), e);
        }
    }

    public static void reLoadFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        Timber.e("reloading fragment");
        fragmentManager.beginTransaction().replace(fragment.getId(),
                new HomeFragment()).commit();
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double mile = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        mile = Math.acos(mile);
        mile = rad2deg(mile);
        mile = mile * 60 * 1.1515;
        double km = mile / 0.62137;
        Timber.e("distance -> %s", km);
        return (km);
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {

        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Timber.e("My Current loction address -> %s", strReturnedAddress.toString());
            } else {
                Timber.e("My Current loction address -> %s", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
//                        Timber.e("My Current loction address -> ", e.getMessage() + "Canont get Address!");
        }
        return strAdd;
    }

    public static SpannableString getUnderlinedString(String text) {
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        return content;
    }

    public static CharSequence highlight(String search, String originalText) {
        // ignore case and accents
        // the same thing should have been done for the search text
        String normalizedText = Normalizer.normalize(originalText, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();

        int start = normalizedText.indexOf(search);
        if (start < 0) {
            // not found, nothing to to
            return originalText;
        } else {
            // highlight each appearance in the original text
            // while searching in normalized text
            Spannable highlighted = new SpannableString(originalText);
            while (start >= 0) {
                int spanStart = Math.min(start, originalText.length());
                int spanEnd = Math.min(start + search.length(), originalText.length());

                highlighted.setSpan(new BackgroundColorSpan(0xFFFCFF48), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                start = normalizedText.indexOf(search, spanEnd);
            }

            return highlighted;
        }
    }

    public static SpannableStringBuilder highlightSearchText(SpannableStringBuilder fullText, String searchText) {

        if (searchText.length() == 0) return fullText;

        SpannableStringBuilder wordSpan = new SpannableStringBuilder(fullText);
        Pattern p = Pattern.compile(searchText, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(fullText);
        while (m.find()) {

            int wordStart = m.start();
            int wordEnd = m.end();

            setWordSpan(wordSpan, wordStart, wordEnd);

        }

        return wordSpan;
    }

    private static void setWordSpan(SpannableStringBuilder wordSpan, int wordStart, int wordEnd) {
        // Now highlight based on the word boundaries
        ColorStateList redColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{0xffa10901});
        TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, redColor, null);

        wordSpan.setSpan(highlightSpan, wordStart, wordEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordSpan.setSpan(new BackgroundColorSpan(0xFFFCFF48), wordStart, wordEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordSpan.setSpan(new RelativeSizeSpan(1.25f), wordStart, wordEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public static Spannable highlightSearchKey(String title) {
        Spannable highlight;
        Pattern pattern;
        Matcher matcher;
        int word_index;
        String title_str;
        String[] words = new String[20];

        word_index = words.length;
        title_str = Html.fromHtml(title).toString();
        highlight = (Spannable) Html.fromHtml(title);
        for (int index = 0; index < word_index; index++) {
            pattern = Pattern.compile("(?i)" + words[index]);
            matcher = pattern.matcher(title_str);
            while (matcher.find()) {
                highlight.setSpan(
                        new BackgroundColorSpan(0x44444444),
                        matcher.start(),
                        matcher.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return highlight;
    }

    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            assert tm != null;
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                Timber.e("simcountry -> %s", simCountry.toLowerCase(Locale.US));
                return simCountry.toLowerCase(Locale.US);
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    Timber.e("networkCountry -> %s", networkCountry.toLowerCase(Locale.US));
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getCountryZipCode(Context context) {

        String CountryID = "";
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        assert manager != null;
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = context.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                Timber.e("CountryZipCode -> %s", CountryZipCode);
                break;
            }
        }
        return CountryZipCode;
    }
}
