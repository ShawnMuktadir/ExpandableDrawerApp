package www.fiberathome.com.parkingapp.data.model;

import android.graphics.drawable.Drawable;

public class MenuModel {

    public String menuName;
    public int icon;
    public boolean hasChildren, isGroup;

    public MenuModel(String menuName, int icon, boolean isGroup, boolean hasChildren) {
        this.menuName = menuName;
        this.icon = icon;
        this.isGroup = isGroup;
        this.hasChildren = hasChildren;
    }
}
