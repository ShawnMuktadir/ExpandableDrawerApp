package www.fiberathome.com.parkingapp.model;

public class SearchVisitorData {

    private String visitedArea;
    private double endLat;
    private double endLng;
    private double startLat;
    private double startLng;


    public SearchVisitorData(String visitedArea, double endLat, double endLng, double startLat, double startLng) {
        this.visitedArea = visitedArea;
        this.endLat = endLat;
        this.endLng = endLng;
        this.startLat = startLat;
        this.startLng = startLng;
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
}