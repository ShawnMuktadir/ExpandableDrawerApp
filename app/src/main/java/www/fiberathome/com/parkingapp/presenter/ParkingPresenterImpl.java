package www.fiberathome.com.parkingapp.presenter;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.model.SensorArea;
import www.fiberathome.com.parkingapp.base.AppConfig;

public class ParkingPresenterImpl implements ParkingPresenter {

    @Override
    public ArrayList<SensorArea> fetchAreas() {
        ArrayList<SensorArea> sensorAreas = new ArrayList<>();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_FETCH_SENSOR_AREA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(ParkingPresenterImpl.class.getCanonicalName(), "" + response);
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("sensors");
                    for (int i =0 ; i<jsonArray.length(); i++ ) {
                        SensorArea sensorArea = new SensorArea();
                        JSONArray array = jsonArray.getJSONArray(i);
                        sensorArea.setParkingArea(array.get(1).toString());
                        sensorArea.setLat(Double.parseDouble(array.get(2).toString()));
                        sensorArea.setLng(Double.parseDouble(array.get(3).toString()));
                        sensorArea.setCount(array.get(4).toString()) ;
                        sensorAreas.add(sensorArea);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        }) {

        };

        ParkingApp.getInstance().addToRequestQueue(strReq);

        Timber.e("Shaua -> %s", sensorAreas.get(0).getParkingArea());
        return sensorAreas;
    }
}