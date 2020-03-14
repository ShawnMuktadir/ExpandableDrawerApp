package www.fiberathome.com.parkingapp.model;

import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.List;

import www.fiberathome.com.parkingapp.Architecture.Importer.PlaceInfo;
import www.fiberathome.com.parkingapp.module.PlayerPrefs;

public class GlobalVars {

    public enum DrawerType {KEYWORDS, SEARCH_PLACES, FAVORITE_PLACES};

    public static MyLocation location = new MyLocation(10.7629886, 106.6821975);
    public static PlaceInfo currentPlace;
    public static List<PlaceInfo> currentPlaceList;
    public static List<PlaceInfo> currentGooglePlaceList;
    public static List<PlaceInfo> currentMyServerPlaceList;

    public static HashMap<Marker, PlaceInfo> markerData = new HashMap<>();
    public static KeywordItem keywordItem;
    public static DrawerType drawer;
    public static String currentPhotoPath;

    public static boolean IsFakeGPS = false;
    public static MyLocation fakeLocation = new MyLocation(10.7629886, 106.6821975); //University of Science, District 5

    public static MyLocation getUserLocation() {
        if (IsFakeGPS) {
            return fakeLocation;
        } else {
            return location;
        }
    }

    //Ha hoi phan giai sau -> Sau nay se dung session de luu thong tin dang nhap cua nguoi dung
    public static int getUserId() {
        return PlayerPrefs.getInstance().GetInt("user_id");
    }

    public static void setUserId(int userId) {
        PlayerPrefs.getInstance().SetInt("user_id", userId);
    }

    public static String getAvatar() {
        return PlayerPrefs.getInstance().GetString("avatar");
    }

    public static void setAvatar(String avatar) {
        PlayerPrefs.getInstance().SetString("avatar", avatar);
    }




}
