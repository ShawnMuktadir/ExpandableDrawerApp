package www.fiberathome.com.parkingapp.Architecture.Importer;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import www.fiberathome.com.parkingapp.model.MyLocation;

public class PlaceInfo {

    public String googlePlaceId;
    public String id;
    public String name;
    public String address;
    public double lat;
    public double lng;
    public String website;
    public String phoneNumber;
    public String internationaPhoneNumber;
    public String rating;
    public String keyword;

    private Uri websiteUri;
    private LatLng latlng;
    private String attributions;
    private float rating1;

    public PlaceInfo() {

    }

    public PlaceInfo(String name, String address, String phoneNumber, String id, Uri websiteUri,
                     LatLng latlng, float rating1, String attributions) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.websiteUri = websiteUri;
        this.latlng = latlng;
        this.rating1 = rating1;
        this.attributions = attributions;
    }

    public PlaceInfo(String googlePlaceId, String id, String name, String address, double lat, double lng, String website, String phoneNumber, String internationaPhoneNumber, String rating, String keyword) {
        this.googlePlaceId = googlePlaceId;
        this.id = id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.website = website;
        this.phoneNumber = phoneNumber;
        this.internationaPhoneNumber = internationaPhoneNumber;
        this.rating = rating;
        this.keyword = keyword;
    }


    public String getGooglePlaceId() {
        return googlePlaceId;
    }

    public void setGooglePlaceId(String googlePlaceId) {
        this.googlePlaceId = googlePlaceId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Uri getWebsiteUri() {
        return websiteUri;
    }

    public void setWebsiteUri(Uri websiteUri) {
        this.websiteUri = websiteUri;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

    public String getAttributions() {
        return attributions;
    }

    public void setAttributions(String attributions) {
        this.attributions = attributions;
    }

    public float getRating1() {
        return rating1;
    }

    public void setRating1(float rating1) {
        this.rating1 = rating1;
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

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getStandardPlaceId() {
        if (Integer.parseInt(id) <= 0) {
            return googlePlaceId;
        }

        return String.valueOf(id);
    }

    public LatLng toLatLng() {
        return new LatLng(lat, lng);
    }

    public MyLocation toMyLocation() {
        return new MyLocation(lat, lng);
    }
}
