package www.fiberathome.com.parkingapp.model.response.termsCondition;

@SuppressWarnings("unused")
public class TermsCondition {

    private String title;

    private String description;

    private String date;

    public TermsCondition() {
    }

    public TermsCondition(String title, String description, String date) {
        this.title = title;
        this.description = description;
        this.date = date;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
