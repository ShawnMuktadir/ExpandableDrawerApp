package www.fiberathome.com.parkingapp.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.data.StaticData;
import www.fiberathome.com.parkingapp.utils.internet.ConnectivityInterceptor;

import static android.content.Context.LOCATION_SERVICE;

public class ApplicationUtils {

    public static void showToast(Context context, String message, long countDown) {
        // Set the toast and duration
        final Toast mToastToShow = Toast.makeText(context, message, Toast.LENGTH_LONG);

        // Set the countdown to display the toast
        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(countDown, countDown /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                mToastToShow.show();
            }

            public void onFinish() {
                mToastToShow.cancel();
            }
        };

        // Show the toast and starts the countdown
        mToastToShow.show();
        toastCountDown.start();
    }

    public static void showToastWithDelay(Context context, String message, long countDown) {
        // Set the toast and duration
        final Toast mToastToShow = Toast.makeText(context, message, Toast.LENGTH_LONG);

        // Set the countdown to display the toast
        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(countDown, countDown /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                mToastToShow.show();
            }

            public void onFinish() {
                mToastToShow.cancel();
            }
        };

        // Show the toast and starts the countdown
        mToastToShow.show();
        toastCountDown.start();
    }

    public static int getToolBarHeight(Context context) {
        int[] attrs = new int[]{R.attr.actionBarSize};
        TypedArray ta = context.obtainStyledAttributes(attrs);
        int toolBarHeight = ta.getDimensionPixelSize(0, -1);
        ta.recycle();
        return toolBarHeight;
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

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected());
    }

    public static void showMessageDialog(String message, Context context) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message);
            builder.setCancelable(true);
            builder.setPositiveButton(context.getResources().getString(R.string.get_support), (dialog, which) -> {
                showAlertDialog(context.getString(R.string.number),
                        context, context.getString(R.string.call),
                        context.getString(R.string.cancel),
                        (dialog1, which1) -> {
                            Timber.e("Positive Button clicked");
                            String number = context.getString(R.string.number);
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_DIAL); // Action for what intent called for
                            intent.setData(Uri.parse("tel: " + number)); // Datum with intent respective action on intent
                            context.startActivity(intent);
                            dialog1.dismiss();
                        },

                        (dialog1, which1) -> {
                            Timber.e("Negative Button Clicked");
                            dialog1.dismiss();
                        });
            });
            builder.setNegativeButton(context.getResources().getString(R.string.ok), (dialog, which) -> {
                if (context != null) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            // Let's start with animation work. We just need to create a style and use it here as follows.
            /*if (alertDialog.getWindow() != null)
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.slidingDialogAnimation;*/

        }
    }

    public static void showOnlyMessageDialog(String message, Context context) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message);
            builder.setCancelable(true);
            builder.setPositiveButton(context.getResources().getString(R.string.ok), (dialog, which) -> {
                if (context != null) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            // Let's start with animation work. We just need to create a style and use it here as follows.
            /*if (alertDialog.getWindow() != null)
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.slidingDialogAnimation;*/

        }
    }

    public static int getNavBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    public static void showAlertDialog(String message, Context context, String positiveText, String negativeText,
                                       DialogInterface.OnClickListener positiveCallback, DialogInterface.OnClickListener negativeCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setPositiveButton(positiveText, positiveCallback);
        if (!TextUtils.isEmpty(negativeText)) {
            builder.setNegativeButton(negativeText, negativeCallback);
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(arg0 -> alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.red)));
        alertDialog.setCancelable(false);
        if (!((Activity) context).isFinishing()) {
            alertDialog.show();
        }
    }


    public static void showAlertDialog(String title, String message, Context context, String positiveText, String negativeText,
                                       DialogInterface.OnClickListener positiveCallback, DialogInterface.OnClickListener negativeCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setPositiveButton(positiveText, positiveCallback);
        if (!TextUtils.isEmpty(negativeText)) {
            builder.setNegativeButton(negativeText, negativeCallback);
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(arg0 -> alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.red)));
        alertDialog.setCancelable(false);

        if (!((Activity) context).isFinishing()) {
            alertDialog.show();
        }
    }

    public static void showToastMessage(Context context, String message) {
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

    public static void hideKeyboard(Context context, EditText editText) {
        editText.clearFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    public static boolean isGPSEnabled(Context context) {

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnabled) {
            return true;
        } else {
            //showToastMessage(context, context.getResources().getString(R.string.please_enable_gps));
        }
        return false;
    }

    public static String getGreetingsMessage() {

        String message = "";

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            return "Good Morning";
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            message = "Good Afternoon";
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            message = "Good Evening";
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            message = "Good Night";
        }
        return message;
    }

    public static String addCountryPrefix(String number) {
        if (number != null && android.text.TextUtils.isDigitsOnly(number)) {
            if (number.length() > 2) {
                if (number.startsWith("88")) {
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
        TextView tv_exit = dialogView.findViewById(R.id.tv_exit);
        View outside_view = dialogView.findViewById(R.id.outside_view);
        final android.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCancelable(false);
        alertDialog.show();

       /* WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
        lp.dimAmount = 0.4f;
        alertDialog.getWindow().setAttributes(lp);
        alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);*/
        outside_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                alertDialog.dismiss();

                return false;
            }
        });

        tv_exit.setOnClickListener(v -> {
            alertDialog.dismiss();
            activity.finish();
            TastyToastUtils.showTastySuccessToast(activity, activity.getResources().getString(R.string.thanks_message));
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

    public static void replaceFragmentWithAnimation(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        //, String tag
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        transaction.replace(R.id.nav_host_fragment, fragment);
        //transaction.addToBackStack(tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static void refreshFragment(@NonNull FragmentManager fragmentManager,
                                       @NonNull Fragment fragment) {
        // Reload current fragment
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        //transaction.setCustomAnimations(0, 0);
        transaction.replace(R.id.nav_host_fragment, fragment);
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
            throw new RuntimeException("Cannot reinstated fragment " + fragment.getClass().getName(), e);
        }
    }

    public static void reLoadFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        //Timber.e("reloading fragment");
        fragmentManager.beginTransaction().replace(fragment.getId(),
                fragment).commit();
    }

    public static void detachAttachFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        fragmentManager.beginTransaction()
                .detach(fragment)
                .attach(fragment)
                .commit();
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
//          Timber.e("My Current loction address -> ", e.getMessage() + "Canont get Address!");
        }
        return strAdd;
    }

    public static boolean checkLocationPermission(Context context) {
        Timber.e("checkLocationPermission call hoiche");
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static boolean isLocationEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            if (lm != null) {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            if (lm != null) {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(context)
                    .setMessage(R.string.gps_network_not_enabled)
                    .setPositiveButton(R.string.open_location_settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    }).setNegativeButton(R.string.cancel, null)
                    .show();
        }
        return gps_enabled && network_enabled;
    }

    public static Drawable changeDrawableColor(Drawable drawable, int color) {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, color);
        return drawable;
    }

    public static BitmapDescriptor getBitmapFromVector(@NonNull Context context,
                                                       @DrawableRes int vectorResourceId,
                                                       @ColorInt int tintColor) {

        Drawable vectorDrawable = ResourcesCompat.getDrawable(
                context.getResources(), vectorResourceId, null);
        if (vectorDrawable == null) {
//            Log.e(TAG, "Requested vector resource was not found");
            return BitmapDescriptorFactory.defaultMarker();
        }
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, tintColor);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private static BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
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
