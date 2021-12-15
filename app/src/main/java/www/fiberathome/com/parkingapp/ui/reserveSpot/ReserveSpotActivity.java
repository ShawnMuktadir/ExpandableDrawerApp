package www.fiberathome.com.parkingapp.ui.reserveSpot;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import www.fiberathome.com.parkingapp.databinding.ActivityReserveSpotBinding;

public class ReserveSpotActivity extends AppCompatActivity {

    ActivityReserveSpotBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReserveSpotBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        String selectedSpot = getIntent().getStringExtra("selectedSpot");
        Toast.makeText(this, selectedSpot, Toast.LENGTH_LONG).show();

        //binding.selectedspot.setText("Your selected: "+selectedSpot);
    }
}
