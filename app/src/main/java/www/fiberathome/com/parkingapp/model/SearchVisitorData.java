package www.fiberathome.com.parkingapp.model;

import java.io.Serializable;

public class SearchVisitorData implements Serializable {

    private String visitedArea;
    private String placeId;
    private double endLat;
    private double endLng;
    private double startLat;
    private double startLng;

    public SearchVisitorData() {
    }

    public SearchVisitorData(String visitedArea, String placeId, double endLat, double endLng, double startLat, double startLng) {
        this.visitedArea = visitedArea;
        this.placeId = placeId;
        this.endLat = endLat;
        this.endLng = endLng;
        this.startLat = startLat;
        this.startLng = startLng;
    }

    public SearchVisitorData(String visitedArea, String placeId, double endLat, double endLng) {
        this.visitedArea = visitedArea;
        this.placeId = placeId;
        this.endLat = endLat;
        this.endLng = endLng;
    }

    public String getVisitedArea() {
        return visitedArea;
    }

    public void setVisitedArea(String visitedArea) {
        this.visitedArea = visitedArea;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public double getEndLng() {
        return endLng;
    }

    public void setEndLng(double endLng) {
        this.endLng = endLng;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public double getStartLng() {
        return startLng;
    }

    public void setStartLng(double startLng) {
        this.startLng = startLng;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}