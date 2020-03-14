package www.fiberathome.com.parkingapp.data;

import android.content.Context;
import android.util.Log;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.response.CommonResponse;
import www.fiberathome.com.parkingapp.model.response.ParkingResponse;
import www.fiberathome.com.parkingapp.utils.AppConfig;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

public class APIInteractorImpl implements APIInteractor {

    private static final String BASE_URL = AppConfig.BASE_URL;
    private Retrofit retrofit;
    private APIServiceInterface apiServiceInterface;
    private Context context;

    public APIInteractorImpl(Context context) {
        this.context = context;
        initAPIService(BASE_URL);
    }

//    private void initAPIService(String url) {
//        OkHttpClient client;
//        if (userManager.isLoggedIn()) {
//            //  token = "Bearer " + userManager.getUser().getToken();
//            client = ApplicationUtils.getClient(context, token);
//        } else {
//            client = ApplicationUtils.getClient(context);
//        }
//        retrofit = new Retrofit.Builder()
//                .baseUrl(url)
//                .client(client)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        apiServiceInterface = retrofit.create(APIServiceInterface.class);
//    }

    private boolean isResponseValid(Response<?> response) {
        if (response == null) {
            Timber.e("response == null");
            return false;
        } else {
            if (response.body() == null) {
                Timber.e("response.body == null");
                return false;
            } else {
                Timber.e("true");
                return true;
            }
        }
    }


    private String prepareFailedMessage(Response<?> response) {
        if (isResponseValid(response)) {
            CommonResponse commonResponse = (CommonResponse) response.body();
            return commonResponse.getMessage();
        } else return context.getString(R.string.conn_failed);
    }

    private void initAPIService(String url) {
        OkHttpClient client;
        client = ApplicationUtils.getClient(context);
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        apiServiceInterface = retrofit.create(APIServiceInterface.class);
    }

    /*----------------------------------------------------------Parking Sensors-------------------------------------------------------------*/
    @Override
    public void getParkingData(ParkingLoadedListener parkingLoadedListener) {
        Call<ParkingResponse> call = apiServiceInterface.getParkingSensors();
        call.enqueue(new Callback<ParkingResponse>() {
            @Override
            public void onResponse(Call<ParkingResponse> call, Response<ParkingResponse> response) {
                Log.e("RetrofitBody", String.valueOf(response.body()));
//                if (isResponseValid(response) && response.body().isError()) {
//                    parkingLoadedListener.onParkingLoaded(response.body());
//                } else {
//                    parkingLoadedListener.onFailed(prepareFailedMessage(response), APIConstants.GET_PARKING);
//                }
            }

            @Override
            public void onFailure(Call<ParkingResponse> call, Throwable t) {
                Timber.e("API INterceptor -> %s", t.getMessage());
                parkingLoadedListener.onFailed(context.getString(R.string.conn_failed), APIConstants.GET_PARKING);
            }
        });
    }
}
