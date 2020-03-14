package www.fiberathome.com.parkingapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AlertDialog;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import www.fiberathome.com.parkingapp.BuildConfig;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.preference.StaticData;
import www.fiberathome.com.parkingapp.preference.utils.ConnectivityInterceptor;

public class ApplicationUtils {

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
                        requestBuilder.addHeader(StaticData.VERSION, BuildConfig.VERSION_NAME);
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
        }

    }
    public static void hideKeyboard(Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(((Activity) mContext).getWindow().getCurrentFocus().getWindowToken(), 0);
    }


}
