package www.fiberathome.com.parkingapp.ui.widget;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import www.fiberathome.com.parkingapp.R;

public class BaseBottomSheetDialog extends BottomSheetDialog {
    public BaseBottomSheetDialog(@NonNull Context context) {
        super(context, R.style.BottomSheetDialogStyle);
    }
}
