package www.fiberathome.com.parkingapp.listener;

import com.karumi.dexter.PermissionToken;

public interface PermissionInterface {
    void showPermissionGranted(String permissionName);

    void handlePermanentDeniedPermission(String permissionName);

    void showPermissionDenied(String permissionName);

    void showPermissionRational(final PermissionToken token);
}
