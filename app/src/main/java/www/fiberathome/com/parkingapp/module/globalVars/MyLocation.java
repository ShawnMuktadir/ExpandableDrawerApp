package www.fiberathome.com.parkingapp.module.globalVars;


import com.google.android.gms.maps.model.LatLng;

import org.simpleframework.xml.Attribute;

public class MyLocation {

    @Attribute
    public double latitude;
    @Attribute
    public double longitude;


    public MyLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String distanceTo(MyLocation location) {
        double Radius = 6371000.0; // meter
        double dLat = Math.toRadians(this.latitude - location.latitude);
        double dLong = Math.toRadians(this.longitude - location.latitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(this.latitude))
                * Math.cos(Math.toRadians(location.latitude))
                * Math.sin(dLong / 2) * Math.sin(dLong / 2);


        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return String.format("%.2f km", Radius * c / 1000);
    }

    public LatLng toLatLng() {
        return new LatLng(this.latitude, this.longitude);
    }
}
