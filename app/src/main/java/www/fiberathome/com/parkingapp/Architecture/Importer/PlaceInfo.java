package www.fiberathome.com.parkingapp.Architecture.Importer;

import com.google.android.gms.maps.model.LatLng;

import www.fiberathome.com.parkingapp.model.MyLocation;

public class PlaceInfo {

    public String googlePlaceId;
    public int placeId;
    public String name;
    public String address;
    public double lat;
    public double lng;
    public String website;
    public String phoneNumber;
    public String internationaPhoneNumber;
    public String rating;
    public String keword;

    public PlaceInfo(){

    }

    public PlaceInfo(String googlePlaceId, int placeId, String name, String address, double lat, double lng, String website, String phoneNumber, String internationaPhoneNumber, String rating, String keword) {
        this.googlePlaceId = googlePlaceId;
        this.placeId = placeId;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.website = website;
        this.phoneNumber = phoneNumber;
        this.internationaPhoneNumber = internationaPhoneNumber;
        this.rating = rating;
        this.keword = keword;
    }


    public String getGooglePlaceId() {
        return googlePlaceId;
    }

    public void setGooglePlaceId(String googlePlaceId) {
        this.googlePlaceId = googlePlaceId;
    }

    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getInternationaPhoneNumber() {
        return internationaPhoneNumber;
    }

    public void setInternationaPhoneNumber(String internationaPhoneNumber) {
        this.internationaPhoneNumber = internationaPhoneNumber;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getKeword() {
        return keword;
    }

    public void setKeword(String keword) {
        this.keword = keword;
    }

    public String getStandardPlaceId(){
        if (placeId <= 0){
            return googlePlaceId;
        }

        return String.valueOf(placeId);
    }

    public LatLng toLatLng(){
        return new LatLng(lat, lng);
    }

    public MyLocation toMyLocation(){
        return new MyLocation(lat, lng);
    }
}
