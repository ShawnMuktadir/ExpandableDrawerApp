package www.fiberathome.com.parkingapp.service.googleService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class DistanceTimeParser {

    public List<HashMap<String, String>> parse(JSONObject jObject) {

        List<HashMap<String, String>> routes = new ArrayList<>();
        JSONArray jRoutes;
        JSONArray jLegs;

        JSONObject jDistance;
        JSONObject jDuration;

        try {

            jRoutes = jObject.getJSONArray("routes");

            jLegs = ((JSONObject) jRoutes.get(0)).getJSONArray("legs");

            List<HashMap<String, String>> path = new ArrayList<>();


            /* Getting distance from the json data */
            jDistance = ((JSONObject) jLegs.get(0)).getJSONObject("distance");
            HashMap<String, String> hmDistance = new HashMap<>();
            hmDistance.put("distance", jDistance.getString("text"));

            /* Getting duration from the json data */
            jDuration = ((JSONObject) jLegs.get(0)).getJSONObject("duration");
            HashMap<String, String> hmDuration = new HashMap<>();
            hmDuration.put("duration", jDuration.getString("text"));

            routes.add(hmDistance);

            routes.add(hmDuration);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Timber.e("catch all called");
        }
        return routes;
    }

}
