package www.fiberathome.com.parkingapp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import www.fiberathome.com.parkingapp.base.ParkingApp;

public class ConnectivityReceiver extends BroadcastReceiver{

    public static ConnectivityReceiverListener listener;

    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (listener != null){
            listener.onNetworkConnectionChanged(isConnected);
        }
    }

    public static boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) ParkingApp.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    public interface ConnectivityReceiverListener{
        void onNetworkConnectionChanged(boolean isConnected);
    }
}
