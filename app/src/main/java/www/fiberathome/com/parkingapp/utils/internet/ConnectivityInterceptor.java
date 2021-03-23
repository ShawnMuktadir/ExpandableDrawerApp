package www.fiberathome.com.parkingapp.utils.internet;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;

public class ConnectivityInterceptor implements Interceptor {
    private final Context context;

    public ConnectivityInterceptor(Context context){
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!ConnectivityUtils.getInstance().checkInternet(context)) {
            throw new NoConnectivityException();
        } else {
            Response response = chain.proceed(chain.request());
            return response;
        }
    }
}

