package www.fiberathome.com.parkingapp.data.model.response.law;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class LawData {

    private String title;
    private String description;

    public LawData() {
    }

    public LawData(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}