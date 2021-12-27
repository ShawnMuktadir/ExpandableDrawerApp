package www.fiberathome.com.parkingapp.data.model.response.law;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("laws")
    @Expose
    private List<Law> laws = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Law> getLaws() {
        return laws;
    }

    public void setLaws(List<Law> laws) {
        this.laws = laws;
    }
}
