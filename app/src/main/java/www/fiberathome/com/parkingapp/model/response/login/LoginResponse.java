package www.fiberathome.com.parkingapp.model.response.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import www.fiberathome.com.parkingapp.model.response.BaseResponse;
import www.fiberathome.com.parkingapp.model.user.User;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class LoginResponse extends BaseResponse {

    @SerializedName("authentication")
    @Expose
    private Boolean authentication;

    @SerializedName("user")
    @Expose
    private User user;

    public Boolean getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Boolean authentication) {
        this.authentication = authentication;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
