package www.fiberathome.com.parkingapp.ui.activity.reserveSpot;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import www.fiberathome.com.parkingapp.R;

public class ReserveSpotActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView selectedspot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_spot);

        //selectedspot = findViewById(R.id.selectedSpotTV);

        String selectedSpot= getIntent().getStringExtra("selectedSpot");
        Toast.makeText(this, selectedSpot, Toast.LENGTH_LONG).show();

        //selectedspot.setText("Your selected: "+selectedSpot);
    }

    @Override
    public void onClick(View view) {

    }
}
