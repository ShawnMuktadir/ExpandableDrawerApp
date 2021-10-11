package www.fiberathome.com.parkingapp.ui.booking.helper;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import www.fiberathome.com.parkingapp.R;

public class DialogHelper extends DialogFragment {
    private final String TAG = getClass().getSimpleName();
    private Dialog dialog;
    private ImageButton dialogClos;
    private TextView arrivedTV, departerTV, timeDiferenceTV, subtotalTV, totalTV, editSlotText, termsConText, promoText;
    private Button payBtn;
    private Context context;
    private String arrived, departure, difference;
    private long diferenceUnit;
    private PayBtnClickListener listener;

    public DialogHelper(Dialog dialog, Context context, String arrived, String departure, String difference,
                        long diferenceUnit, PayBtnClickListener listener) {
        this.dialog = dialog;
        this.context = context;
        this.arrived = arrived;
        this.departure = departure;
        this.difference = difference;
        this.diferenceUnit = diferenceUnit;
        this.listener = listener;
    }

    public void initDialog() {
        arrivedTV = dialog.findViewById(R.id.tvArrivedTime);
        departerTV = dialog.findViewById(R.id.tvDepartureTime);
        timeDiferenceTV = dialog.findViewById(R.id.tvDifferenceTime);
        subtotalTV = dialog.findViewById(R.id.tvSubTotal);
        totalTV = dialog.findViewById(R.id.tvTotal);
        payBtn = dialog.findViewById(R.id.btnPay);
        editSlotText = dialog.findViewById(R.id.editSlotText);
        termsConText = dialog.findViewById(R.id.tvTermCondition);
        promoText = dialog.findViewById(R.id.tvPromo);

        arrivedTV.setText(arrived);
        departerTV.setText(departure);
        timeDiferenceTV.setText(difference);
        setBill();
        setListeners();
        dialogClos.setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().getAttributes().windowAnimations = android.R.anim.fade_in;
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // safety check
        if (getDialog() == null)
            return;

        /*int dialogWidth = 1000; // specify a value here
        int dialogHeight = 500; // specify a value here

        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);*/

        // ... other stuff you want to do in your onStart() method
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
        getDialog().getWindow().setLayout(width, height);
    }

    private void setListeners() {
        editSlotText.setOnClickListener(v -> Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show());

        promoText.setOnClickListener(v -> Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show());

        termsConText.setOnClickListener(v -> Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show());

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(context, "Coming Soon...paybtn", Toast.LENGTH_SHORT).show();
                listener.payBtnClick();
            }
        });


    }

    private void setBill() {
        final double perMintBill = 1.67;
        DecimalFormat df = new DecimalFormat("##.##");
        Log.d(TAG, "setBill: perMint:" + perMintBill);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diferenceUnit);
        Log.d(TAG, "setBill: total:" + perMintBill * minutes);
        subtotalTV.setText(df.format(perMintBill * minutes));
        totalTV.setText(df.format(perMintBill * minutes));
        payBtn.setText("Pay BDT " + df.format(perMintBill * minutes));
    }

    public interface PayBtnClickListener {
        void payBtnClick();
    }
}
