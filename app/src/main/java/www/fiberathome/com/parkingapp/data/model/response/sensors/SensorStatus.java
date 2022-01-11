package www.fiberathome.com.parkingapp.data.model.response.sensors;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class SensorStatus {

    private String areaId;
    private String totalCount;
    private String occupiedCount;

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getOccupiedCount() {
        return occupiedCount;
    }

    public void setOccupiedCount(String occupiedCount) {
        this.occupiedCount = occupiedCount;
    }
}
