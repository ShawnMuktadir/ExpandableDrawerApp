package www.fiberathome.com.parkingapp.base;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.utils.LocaleManager;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.Utility;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    private AlertDialog alertDialog;
    private static final int WIFI_ENABLE_REQUEST = 0x1006;

    private Context context;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(ParkingApp.localeManager.setLocale(base));
        Timber.e("attachBaseContext");
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkInternetConnection();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        registerReceiver(receiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        Utility.resetActivityTitle(this);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
            // Toast.makeText(BaseActivity.this, "Connected", Toast.LENGTH_LONG).show();
        } else {
            showNoInternetDialog();
        }
    }

    private void showNoInternetDialog() {

        if (alertDialog != null && alertDialog.isShowing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Internet Disabled!");
        builder.setMessage("No active Internet connection found.");
        builder.setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent enableWifi = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                startActivityForResult(enableWifi, WIFI_ENABLE_REQUEST);
            }
        }).setNegativeButton("No, Just Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                TastyToastUtils.showTastySuccessToast(context, "Thanks for being with us");
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == WIFI_ENABLE_REQUEST) {
            // Toast.makeText(BaseActivity.this, "Connected", Toast.LENGTH_LONG).show();
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if (!wifiManager.isWifiEnabled()) {

            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
