package www.fiberathome.com.parkingapp.view.permission.listener;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;

public class DexterPermissionListener implements com.karumi.dexter.listener.single.PermissionListener {
    private final PermissionInterface permissionInterface;

    public DexterPermissionListener(PermissionInterface permissionInterface) {
        this.permissionInterface = permissionInterface;
    }

    @Override
    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
        permissionInterface.showPermissionGranted(permissionGrantedResponse.getPermissionName());
    }

    @Override
    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

        if (permissionDeniedResponse.isPermanentlyDenied()) {
            permissionInterface.handlePermanentDeniedPermission(permissionDeniedResponse.getPermissionName());

            return;
        }
        permissionInterface.showPermissionDenied(permissionDeniedResponse.getPermissionName());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
        permissionInterface.showPermissionRational(permissionToken);
    }
}
