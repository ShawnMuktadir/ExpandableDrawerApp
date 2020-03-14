package www.fiberathome.com.parkingapp.ui.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Objects;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.User;
import www.fiberathome.com.parkingapp.ui.MainView;
import www.fiberathome.com.parkingapp.utils.SharedPreManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class QRCodeFragment extends Fragment {

    private ImageView QRCode;

    public QRCodeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qrcode, container, false);

        // Set the title of fragment
        if (getActivity() != null) {
            getActivity().setTitle("QR Code");
        }

        QRCode = view.findViewById(R.id.sample_qr_iv);

        User user = SharedPreManager.getInstance(getContext()).getUser();
        Objects.requireNonNull(getActivity()).setTitle(user.getFullName());

        //show QR on create
        String text = "User Name:" + user.getFullName();
        text = text + "User ID:" + user.getMobileNo();
        text = text + "Vehicle Reg No:" + user.getVehicleNo();


        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            QRCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set the title of fragment
        if (getActivity() != null) {
            getActivity().setTitle("QR Code");
        }
    }
}
