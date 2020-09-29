package www.fiberathome.com.parkingapp.model.sensors;

import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.base.ParkingApp;


public class SensorList {

    public List<Sensors> sensors;
    public String next_page_token;
    public String id;

    public SensorList() {
        sensors = new ArrayList<Sensors>();
    }

    private void fachText(View view) {

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_FETCH_SENSORS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.e("response",""+response);

                try {
                    JSONArray JsonArray = new JSONArray(response);

                    for (int i = 0; i < 1; i++) {

                        JSONObject obj = JsonArray.getJSONObject(0);
                        String uid= obj.getString("uid");
                        Log.e("Sensor Id: ", uid);

                        //  String id= obj.getString ( "id" );



//                        String fetcheddata = text+ "\n" ;
//                        textView.setText(fetcheddata );

                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    System.out.println(e.getMessage());

                }
            }
        }, new Response.ErrorListener() {
            @Override


            public void onErrorResponse(VolleyError error) {


            }
        }) {

        };

        ParkingApp.getInstance().addToRequestQueue(strReq);
    }



    public boolean canLoadMore(){
        return next_page_token != null;
    }
}
