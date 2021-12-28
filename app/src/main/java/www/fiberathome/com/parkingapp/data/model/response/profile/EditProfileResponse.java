package www.fiberathome.com.parkingapp.data.model.response.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;
import www.fiberathome.com.parkingapp.data.model.user.User;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class EditProfileResponse extends BaseResponse {

    @SerializedName("user")
    @Expose
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
