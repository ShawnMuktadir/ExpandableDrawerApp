package www.fiberathome.com.parkingapp.model.response.editProfile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import www.fiberathome.com.parkingapp.model.response.BaseResponse;
import www.fiberathome.com.parkingapp.model.user.User;

@SuppressWarnings("unused")
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
