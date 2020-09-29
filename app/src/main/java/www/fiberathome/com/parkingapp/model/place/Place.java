package www.fiberathome.com.parkingapp.model.place;

import www.fiberathome.com.parkingapp.module.globalVars.MyLocation;

public class Place {

    public MyLocation location;
    public String icon;
    public String name;
    public String placeId;
    public String vicinity;
    public String photoReference;
    public String rating;
    public PlaceDetails placeDetails;

    public Place(MyLocation location, String icon, String name, String placeId, String vicinity, String photoReference, String rating, PlaceDetails placeDetails) {
        this.location = location;
        this.icon = icon;
        this.name = name;
        this.placeId = placeId;
        this.vicinity = vicinity;
        this.photoReference = photoReference;
        this.rating = rating;
        this.placeDetails = placeDetails;
    }

    // There will be a to String method


    public Place Clone(){
        return new Place(location, icon, name, placeId, vicinity, photoReference, rating, placeDetails);
    }
}
