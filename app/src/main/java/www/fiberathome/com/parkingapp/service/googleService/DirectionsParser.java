package www.fiberathome.com.parkingapp.service.googleService;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * Created by NgocTri on 12/11/2017.
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
public class DirectionsParser {
    /**
     * Returns a list of lists containing latitude and longitude from a JSONObject
     */
    @SuppressWarnings("rawtypes")
    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        HashMap<String, Object> hashMap = new HashMap<>();
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        double totalDistance = 0;
        long totalDuration = 0;

        try {

            jRoutes = jObject.getJSONArray("routes");

            // Loop for all routes
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");

                List path = new ArrayList<HashMap<String, String>>();

                //Loop for all legs
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    JSONObject legs1 = jLegs.getJSONObject(j);
                    JSONObject distance = legs1.getJSONObject("distance");
                    JSONObject duration = legs1.getJSONObject("duration");
                    totalDistance = totalDistance + distance.getLong("value");
                    totalDuration = totalDuration + duration.getLong("value");

                    //Loop for all steps
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline;
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List list = decodePolyline(polyline);

                        //Loop for all points
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                            hm.put("lon", Double.toString(((LatLng) list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
                totalDistance /= 1000;
                totalDuration /= 60;
                totalDistance = Math.round(totalDistance * 10) / 10.0;
                hashMap.put("distance", totalDistance);
                hashMap.put("duration", totalDuration);
                Timber.e("Direction Parser distance -> %s", String.valueOf(totalDistance));
                Timber.e("Direction Parser duration -> %s", String.valueOf(totalDuration));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Timber.e("catch all called");
        }

        return routes;
    }

    // Dijkstra's shortest path algorithm

    @SuppressWarnings("rawtypes")
    private List decodePolyline(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}