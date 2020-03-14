package www.fiberathome.com.parkingapp.service;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import www.fiberathome.com.parkingapp.utils.AppConfig;

public class HttpService extends IntentService {

    public static String TAG = HttpService.class.getSimpleName();

    public HttpService() {
        super(HttpService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null){
            String otp = intent.getStringExtra("otp");
            verifyOtp(otp);
        }
    }

    private void verifyOtp(String otp) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    // Parsing json object response
                    // response will be a json object
                    boolean error = jsonObject.getBoolean("error");
                    String mesage = jsonObject.getString("message");

                    if (!error){
                        // parsing
                    }




                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

        };
    }
}
